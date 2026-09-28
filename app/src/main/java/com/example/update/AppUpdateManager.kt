package com.example.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
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
        val cleanCurrentVersion = currentVersionName.removePrefix("v").removePrefix("V")

        // 1. المحاولة الأولى: فحص ملف التحديث السحابي المباشر version.json (خالٍ من أي تعقيد أو قيود Rate Limit)
        for (repoSlug in REPO_CANDIDATES) {
            try {
                val rawUrl = "https://raw.githubusercontent.com/$repoSlug/main/version.json"
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
                        val cleanRemote = remoteVer.removePrefix("v").removePrefix("V")
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
                }
            } catch (e: Exception) {
                Log.d(TAG, "Raw check skipped for $repoSlug: ${e.message}")
            }
        }

        // 2. المحاولة الثانية: فحص قسم الـ Releases العام في GitHub
        for (repoSlug in REPO_CANDIDATES) {
            try {
                val apiUrl = "https://api.github.com/repos/$repoSlug/releases"
                val conn = (URL(apiUrl).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Accept", "application/vnd.github.v3+json")
                    setRequestProperty("User-Agent", "EchoOfBattle-Android-AutoUpdater")
                    connectTimeout = 6000
                    readTimeout = 6000
                }

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                    val array = JSONArray(jsonString)
                    if (array.length() > 0) {
                        val latestRelease = array.getJSONObject(0)
                        val tagName = latestRelease.optString("tag_name", "").trim()
                        val releaseTitle = latestRelease.optString("name", "تحديث جديد للعبة")
                        val releaseNotes = latestRelease.optString("body", "تحسينات وإضافات جديدة.")

                        val cleanRemote = tagName.removePrefix("v").removePrefix("V")
                        var apkUrl = ""

                        val assets = latestRelease.optJSONArray("assets")
                        if (assets != null) {
                            for (i in 0 until assets.length()) {
                                val asset = assets.getJSONObject(i)
                                val assetName = asset.optString("name", "")
                                val dl = asset.optString("browser_download_url", "")
                                if (assetName.endsWith(".apk", ignoreCase = true) || dl.endsWith(".apk", ignoreCase = true)) {
                                    apkUrl = dl
                                    break
                                }
                            }
                        }

                        if (apkUrl.isNotBlank()) {
                            val isNewer = isNewerVersion(cleanRemote, cleanCurrentVersion)
                            Log.i(TAG, "GitHub release found on $repoSlug: tag=$tagName, isNewer=$isNewer")
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
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "GitHub release check failed for $repoSlug: ${e.message}")
            }
        }

        // إذا لم يكن هناك تحديث منشور، أو تعذر الاتصال، نرجع نتيجة واضحة ومطمئنة بدون أخطاء معقدة
        Result.success(
            UpdateInfo(
                latestVersionName = currentVersionName,
                releaseTitle = "أنت على أحدث إصدار",
                releaseNotes = "لعبتك محدثة حالياً إلى آخر إصدار متوفر.",
                apkDownloadUrl = "",
                isUpdateAvailable = false
            )
        )
    }

    /**
     * مقارنة إصدارين رقميين (Semantic Version Comparison)
     */
    fun isNewerVersion(remote: String, current: String): Boolean {
        if (remote.isBlank() || current.isBlank()) return false
        val rParts = remote.split(".").mapNotNull { it.trim().toIntOrNull() }
        val cParts = current.split(".").mapNotNull { it.trim().toIntOrNull() }

        if (rParts.isEmpty() || cParts.isEmpty()) {
            return remote.compareTo(current, ignoreCase = true) > 0
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

    /**
     * تنزيل ملف الـ APK وتثبيته مباشرة للمستخدم بنقرة واحدة
     */
    suspend fun downloadAndInstall(apkUrl: String) = withContext(Dispatchers.IO) {
        if (apkUrl.isBlank()) {
            _downloadState.value = DownloadState.Error("رابط التحديث غير متوفر حالياً.")
            return@withContext
        }

        try {
            _downloadState.value = DownloadState.Downloading(0f, 0L, 0L)
            Log.d(TAG, "Starting APK download from: $apkUrl")

            val url = URL(apkUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "EchoOfBattle-Android-AutoUpdater")
                connectTimeout = 15000
                readTimeout = 30000
            }

            var redirectConnection: HttpURLConnection = connection
            var redirectCount = 0
            while (redirectConnection.responseCode in 300..399 && redirectCount < 5) {
                val newUrl = redirectConnection.getHeaderField("Location")
                redirectConnection = (URL(newUrl).openConnection() as HttpURLConnection).apply {
                    instanceFollowRedirects = true
                    setRequestProperty("User-Agent", "EchoOfBattle-Android-AutoUpdater")
                    connectTimeout = 15000
                    readTimeout = 30000
                }
                redirectCount++
            }

            val totalBytes = redirectConnection.contentLength.toLong()
            val updateDir = File(context.cacheDir, "updates").apply { if (!exists()) mkdirs() }
            val apkFile = File(updateDir, "update.apk")
            if (apkFile.exists()) apkFile.delete()

            redirectConnection.inputStream.use { input ->
                FileOutputStream(apkFile).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalDownloaded = 0L

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalDownloaded += bytesRead
                        val progress = if (totalBytes > 0) totalDownloaded.toFloat() / totalBytes else 0.5f
                        _downloadState.value = DownloadState.Downloading(progress, totalDownloaded, totalBytes)
                    }
                }
            }

            Log.i(TAG, "APK download complete: ${apkFile.absolutePath}, size=${apkFile.length()} bytes")
            _downloadState.value = DownloadState.Downloaded(apkFile)

            withContext(Dispatchers.Main) {
                installApk(apkFile)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading APK", e)
            _downloadState.value = DownloadState.Error("تعذر إكمال التنزيل: تأكد من اتصال الإنترنت.")
        }
    }

    /**
     * تشغيل معالج تثبيت الحزم التابع لنظام أندرويد
     */
    fun installApk(file: File) {
        try {
            if (!file.exists() || file.length() == 0L) {
                _downloadState.value = DownloadState.Error("ملف التحديث غير موجود")
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                    return
                }
            }

            val authority = "${context.packageName}.fileprovider"
            val apkUri: Uri = FileProvider.getUriForFile(context, authority, file)

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch package installer", e)
            _downloadState.value = DownloadState.Error("تعذر فتح معالج التثبيت: ${e.localizedMessage}")
        }
    }

    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }
}
