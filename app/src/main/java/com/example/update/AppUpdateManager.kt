package com.example.update

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val latestVersionName: String,
    val releaseTitle: String,
    val releaseNotes: String,
    val apkDownloadUrl: String,
    val isUpdateAvailable: Boolean
)

sealed class DownloadState {
    object Idle : DownloadState()
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : DownloadState()
    data class Downloaded(val apkFile: File) : DownloadState()
    data class PermissionRequired(val apkFile: File) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

/**
 * AppUpdateManager (نظام التحديث التلقائي السلس المباشر لعام 2026)
 *
 * مصمم خصيصاً ليكون مباشراً وبسيطاً تماماً للمستخدم العادي واللاعبين:
 * - لا يطلب من المستخدم أي رموز (Tokens) أو إعدادات تقنية معقدة.
 * - يفحص خوادم التحديث تلقائياً في الخلفية.
 * - يدعم الفحص المزدوج من مستودع GitHub والموجز السحابي المباشر بدون قيود.
 * - عند توفر إصدار جديد، يعرض للمستخدم زراً واحداً: "تحديث وتثبيت الآن".
 */
class AppUpdateManager(private val context: Context) {

    companion object {
        private const val TAG = "AppUpdateManager"

        // أسماء المستودعات المعتمدة تلقائياً في الخلفية
        private val REPO_CANDIDATES = listOf(
            "haderrhadrr1234-dev/Echo-of-Battle",
            "haderrhadrr1234-dev/EchoOfBattle",
            "haderrhadrr1234/Echo-of-Battle",
            "haderrhadrr1234/EchoOfBattle"
        )
    }

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    val currentVersionName: String by lazy {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: BuildConfig.VERSION_NAME
        } catch (_: Exception) {
            BuildConfig.VERSION_NAME
        }
    }

    /**
     * فحص التحديثات بطريقة مباشرة وذكية بدون أي إدخالات من المستخدم
     */
    suspend fun checkForUpdates(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        val cleanCurrentVersion = currentVersionName.removePrefix("v").removePrefix("V").trim()

        var anySuccess = false
        var lastError: Exception? = null

        // 1. المحاولة الأولى: فحص قسم الـ Releases في GitHub (الأكثر دقة وحداثة)
        for (repoSlug in REPO_CANDIDATES) {
            try {
                val apiUrl = "https://api.github.com/repos/$repoSlug/releases"
                val conn = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "EchoOfBattle-Android-AutoUpdater")
                    connectTimeout = 7000
                    readTimeout = 7000
                }

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                    val array = JSONArray(jsonString)
                    if (array.length() > 0) {
                        val latestRelease = array.getJSONObject(0)
                        val tagName = latestRelease.optString("tag_name", "").trim()
                        val releaseTitle = latestRelease.optString("name", "تحديث جديد للعبة")
                        val releaseNotes = latestRelease.optString("body", "تحسينات وإضافات جديدة.")

                        val cleanRemote = tagName.removePrefix("v").removePrefix("V").trim()
                        var apkUrl = ""

                        val assets = latestRelease.optJSONArray("assets")
                        if (assets != null) {
                            var bestUrl = ""
                            for (i in 0 until assets.length()) {
                                val asset = assets.getJSONObject(i)
                                val assetName = asset.optString("name", "")
                                val dl = asset.optString("browser_download_url", "")
                                if (dl.endsWith(".apk", ignoreCase = true)) {
                                    if (assetName.contains("EchoOfBattle", ignoreCase = true)) {
                                        bestUrl = dl
                                        break
                                    } else if (bestUrl.isBlank()) {
                                        bestUrl = dl
                                    }
                                }
                            }
                            apkUrl = bestUrl
                        }

                        if (apkUrl.isBlank()) {
                            apkUrl = "https://github.com/$repoSlug/releases/download/$tagName/EchoOfBattle.apk"
                        }

                        val isNewer = isNewerVersion(cleanRemote, cleanCurrentVersion)
                        Log.i(TAG, "GitHub release found on $repoSlug: tag=$tagName, cleanRemote=$cleanRemote, current=$cleanCurrentVersion, isNewer=$isNewer")
                        return@withContext Result.success(
                            UpdateInfo(
                                latestVersionName = tagName,
                                releaseTitle = releaseTitle,
                                releaseNotes = releaseNotes,
                                apkDownloadUrl = apkUrl,
                                isUpdateAvailable = isNewer
                            )
                        )
                    }
                } else {
                    lastError = Exception("HTTP ${conn.responseCode} from GitHub API")
                }
            } catch (e: Exception) {
                lastError = e
                Log.d(TAG, "GitHub release check failed for $repoSlug: ${e.message}")
            }
        }

        // 2. المحاولة الثانية: فحص ملف التحديث السحابي المباشر version.json مع منع التخزين المؤقت
        for (repoSlug in REPO_CANDIDATES) {
            try {
                val rawUrl = "https://raw.githubusercontent.com/$repoSlug/main/version.json?t=${System.currentTimeMillis()}"
                val rawConn = (URL(rawUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "EchoOfBattle-Android-AutoUpdater")
                    connectTimeout = 6000
                    readTimeout = 6000
                }

                if (rawConn.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonText = rawConn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(jsonText)
                    val remoteVer = json.optString("version", "").trim()
                    val downloadUrl = json.optString("download_url", "").trim()
                    val title = json.optString("title", "تحديث جديد للعبة")
                    val notes = json.optString("notes", "تحسينات جديدة وإصلاحات لتجربة لعب مثالية.")

                    if (remoteVer.isNotBlank() && downloadUrl.isNotBlank()) {
                        val cleanRemote = remoteVer.removePrefix("v").removePrefix("V").trim()
                        val isNewer = isNewerVersion(cleanRemote, cleanCurrentVersion)
                        Log.i(TAG, "Direct cloud manifest found: version=$remoteVer, isNewer=$isNewer")

                        return@withContext Result.success(
                            UpdateInfo(
                                latestVersionName = remoteVer,
                                releaseTitle = title,
                                releaseNotes = notes,
                                apkDownloadUrl = downloadUrl,
                                isUpdateAvailable = isNewer
                            )
                        )
                    }
                } else {
                    lastError = Exception("HTTP ${rawConn.responseCode} from version.json")
                }
            } catch (e: Exception) {
                lastError = e
                Log.d(TAG, "Raw check skipped for $repoSlug: ${e.message}")
            }
        }

        // 3. في حال فشل الاتصال بالشبكة تماماً:
        if (lastError != null) {
            Result.failure(lastError)
        } else {
            // الرابط الافتراضي المباشر للإصدار المنشور الأخير v2.0.0
            val fallbackUrl = "https://github.com/haderrhadrr1234-dev/Echo-of-Battle/releases/download/v2.0.0/EchoOfBattle.apk"
            Result.success(
                UpdateInfo(
                    latestVersionName = "v2.0.0",
                    releaseTitle = "الإصدار العملاق 2.0 - صدى المعركة (Echo of Battle)",
                    releaseNotes = "طور غارات الزعماء الأسطوريين، 50 سلاحاً، 50 قوة خارقة، 10 دروع أسطورية، 8 مرافقين مقاتلين، وورشة الحدادة السحرية!",
                    apkDownloadUrl = fallbackUrl,
                    isUpdateAvailable = isNewerVersion("2.0.0", cleanCurrentVersion)
                )
            )
        }
    }

    /**
     * مقارنة إصدارين رقميين بدقة (Semantic Version Comparison)
     */
    fun isNewerVersion(remote: String, current: String): Boolean {
        if (remote.isBlank() || current.isBlank()) return false
        val rClean = remote.removePrefix("v").removePrefix("V").trim()
        val cClean = current.removePrefix("v").removePrefix("V").trim()

        val rParts = rClean.split(".").mapNotNull { part ->
            part.filter { it.isDigit() }.toIntOrNull()
        }
        val cParts = cClean.split(".").mapNotNull { part ->
            part.filter { it.isDigit() }.toIntOrNull()
        }

        if (rParts.isEmpty() || cParts.isEmpty()) {
            return rClean.compareTo(cClean, ignoreCase = true) > 0
        }

        val maxLen = maxOf(rParts.size, cParts.size)
        for (i in 0 until maxLen) {
            val r = rParts.getOrElse(i) { 0 }
            val c = cParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    // حفظ آخر ملف تم تنزيله لاستئناف التثبيت فور منح الإذن من الإعدادات
    var lastDownloadedApk: File? = null
        private set

    /**
     * تنزيل ملف الـ APK وتثبيته مباشرة للمستخدم بنقرة واحدة
     */
    suspend fun downloadAndInstall(apkUrl: String) = withContext(Dispatchers.IO) {
        if (apkUrl.isBlank()) {
            _downloadState.value = DownloadState.Error("رابط التحديث غير متوفر حالياً.")
            return@withContext
        }

        try {
            _downloadState.value = DownloadState.Downloading(0.01f, 0L, 0L)
            Log.d(TAG, "Starting APK download from: $apkUrl")

            var currentUrl = apkUrl
            var redirectCount = 0
            var finalConnection: HttpURLConnection? = null

            // معالجة التوجيهات المتعددة (GitHub Releases -> AWS S3 / Release-Assets CDN)
            while (redirectCount < 7) {
                val conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", "EchoOfBattle-Android-AutoUpdater")
                    connectTimeout = 15000
                    readTimeout = 30000
                }
                val code = conn.responseCode
                if (code in 300..399) {
                    val location = conn.getHeaderField("Location")
                    conn.disconnect()
                    if (location.isNullOrBlank()) {
                        throw IOException("Redirect received without Location header")
                    }
                    currentUrl = if (location.startsWith("http")) location else URL(URL(currentUrl), location).toString()
                    redirectCount++
                } else if (code == HttpURLConnection.HTTP_OK) {
                    finalConnection = conn
                    break
                } else {
                    conn.disconnect()
                    throw IOException("HTTP $code from $currentUrl")
                }
            }

            val connection = finalConnection ?: throw IOException("تعذر إكمال التوجيه لتحميل الملف")
            val totalBytes = connection.contentLength.toLong()

            // تخزين الملف في مجلد التحميلات الخارجية أو الكاش المتاح
            val updateDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir, "updates").apply {
                if (!exists()) mkdirs()
            }
            val apkFile = File(updateDir, "EchoOfBattle.apk")
            if (apkFile.exists()) apkFile.delete()

            var lastReportTime = 0L
            var lastReportProgress = 0f

            connection.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(32768) // 32KB لتنزيل سريع وسلس
                    var bytesRead: Int
                    var totalDownloaded = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalDownloaded += bytesRead
                        val progress = if (totalBytes > 0) totalDownloaded.toFloat() / totalBytes else 0.5f
                        val now = System.currentTimeMillis()
                        // تحديث الحالة كل 200 مللي ثانية لمنع تهنيج واجهة Compose
                        if (now - lastReportTime > 200 || (progress - lastReportProgress) > 0.05f) {
                            lastReportTime = now
                            lastReportProgress = progress
                            _downloadState.value = DownloadState.Downloading(progress, totalDownloaded, totalBytes)
                        }
                    }
                    output.flush()
                }
            }
            connection.disconnect()

            // ضبط أذونات الملف للقراءة العامة بواسطة نظام التثبيت
            apkFile.setReadable(true, false)
            lastDownloadedApk = apkFile

            Log.i(TAG, "APK download complete: ${apkFile.absolutePath}, size=${apkFile.length()} bytes")
            _downloadState.value = DownloadState.Downloaded(apkFile)

            withContext(Dispatchers.Main) {
                installApk(apkFile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading APK", e)
            _downloadState.value = DownloadState.Error("تعذر إكمال التنزيل: ${e.localizedMessage ?: "تحقق من اتصال الإنترنت."}")
        }
    }

    /**
     * طلب إذن تثبيت التطبيقات غير المعروفة
     */
    fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
            } catch (e: Exception) {
                try {
                    val fallbackIntent = Intent(Settings.ACTION_SECURITY_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallbackIntent)
                } catch (_: Exception) {}
            }
        }
    }

    /**
     * فحص واستئناف التثبيت تلقائياً عند عودة المستخدم من شاشة الإعدادات
     */
    fun checkAndResumePendingInstall() {
        val apk = lastDownloadedApk ?: return
        if (apk.exists() && apk.length() > 0) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || context.packageManager.canRequestPackageInstalls()) {
                installApk(apk)
            }
        }
    }

    /**
     * تشغيل معالج تثبيت الحزم التابع لنظام أندرويد بأسلوب مرن ومتوافق مع جميع الهواتف
     */
    fun installApk(file: File) {
        try {
            if (!file.exists() || file.length() == 0L) {
                _downloadState.value = DownloadState.Error("ملف التحديث غير موجود، يرجى إعادة المحاولة.")
                return
            }

            file.setReadable(true, false)
            lastDownloadedApk = file

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    _downloadState.value = DownloadState.PermissionRequired(file)
                    requestInstallPermission()
                    return
                }
            }

            val authority = "${context.packageName}.fileprovider"
            val apkUri: Uri = FileProvider.getUriForFile(context, authority, file)

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            }

            // منح الأذونات صراحة لكافة التطبيقات التي يمكنها تثبيت الحزم
            val resolveInfos = context.packageManager.queryIntentActivities(installIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resolveInfos) {
                context.grantUriPermission(
                    resolveInfo.activityInfo.packageName,
                    apkUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            context.startActivity(installIntent)
            _downloadState.value = DownloadState.Downloaded(file)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch package installer", e)
            try {
                val altAuthority = "${context.packageName}.fileprovider"
                val altUri = FileProvider.getUriForFile(context, altAuthority, file)
                val altIntent = Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
                    data = altUri
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                }
                context.startActivity(altIntent)
            } catch (e2: Exception) {
                _downloadState.value = DownloadState.Error("تعذر فتح أداة تثبيت الحزم: ${e.localizedMessage}. يمكنك تثبيته عبر المتصفح.")
            }
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }
}
