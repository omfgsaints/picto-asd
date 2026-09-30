package com.example.data.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class OnlineImageResult(
    val localFilePath: String,
    val webImageUrl: String,
    val description: String? = null
)

class OnlineImageFetcher(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val cacheDir = File(context.filesDir, "cached_images").apply {
        if (!exists()) mkdirs()
    }

    /**
     * Checks if an offline cached image already exists locally for this word.
     */
    fun getLocalOfflineImage(word: String): File? {
        val clean = sanitizeFilename(word)
        val file = File(cacheDir, "$clean.jpg")
        return if (file.exists() && file.length() > 0) file else null
    }

    /**
     * Fetches a relevant photo / picture from the web and downloads it
     * directly to internal storage for 100% offline access.
     */
    suspend fun fetchAndSaveForOffline(word: String): OnlineImageResult? = withContext(Dispatchers.IO) {
        val clean = word.trim().lowercase()
        if (clean.isBlank()) return@withContext null

        // 1. Check if already downloaded locally
        val existingFile = getLocalOfflineImage(clean)
        if (existingFile != null) {
            return@withContext OnlineImageResult(
                localFilePath = existingFile.absolutePath,
                webImageUrl = ""
            )
        }

        try {
            val encoded = URLEncoder.encode(clean, "UTF-8")
            val apiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/$encoded"

            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "PictoWordApp/1.0 (Android; Educational for kids)")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val bodyString = response.body?.string() ?: return@withContext null
                val json = JSONObject(bodyString)

                var imageUrl: String? = null
                if (json.has("thumbnail")) {
                    val thumb = json.getJSONObject("thumbnail")
                    if (thumb.has("source")) {
                        imageUrl = thumb.getString("source")
                    }
                } else if (json.has("originalimage")) {
                    val orig = json.getJSONObject("originalimage")
                    if (orig.has("source")) {
                        imageUrl = orig.getString("source")
                    }
                }

                if (imageUrl.isNullOrBlank()) return@withContext null

                val description = json.optString("description", "")

                // 2. Download image bytes and store permanently on disk
                val downloadedFile = downloadAndSaveFile(clean, imageUrl)
                if (downloadedFile != null) {
                    return@withContext OnlineImageResult(
                        localFilePath = downloadedFile.absolutePath,
                        webImageUrl = imageUrl,
                        description = description.ifBlank { null }
                    )
                }
            }
        } catch (e: Exception) {
            Log.w("OnlineImageFetcher", "Failed to fetch online image for $word: ${e.message}")
        }
        null
    }

    private fun downloadAndSaveFile(wordKey: String, url: String): File? {
        try {
            val req = Request.Builder()
                .url(url)
                .header("User-Agent", "PictoWordApp/1.0 (Android; Educational)")
                .get()
                .build()

            client.newCall(req).execute().use { res ->
                if (!res.isSuccessful) return null
                val bytes = res.body?.bytes() ?: return null
                val targetFile = File(cacheDir, "${sanitizeFilename(wordKey)}.jpg")
                FileOutputStream(targetFile).use { out ->
                    out.write(bytes)
                }
                return targetFile
            }
        } catch (e: Exception) {
            Log.w("OnlineImageFetcher", "Error saving offline file for $wordKey: ${e.message}")
        }
        return null
    }

    private fun sanitizeFilename(word: String): String {
        return word.lowercase().replace("[^a-z0-9_-]".toRegex(), "_")
    }
}
