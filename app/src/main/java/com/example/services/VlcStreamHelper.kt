package com.example.services

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.model.MediaItem
import com.example.model.MediaType
import java.net.URI
import java.net.URLEncoder

sealed class VlcLaunchResult {
    object Success : VlcLaunchResult()
    data class Failure(val message: String) : VlcLaunchResult()
}

object VlcStreamHelper {
    // Supported Media Types for VLC streaming
    val SUPPORTED_AUDIO_EXTENSIONS = setOf("mp3", "flac", "wav", "aac", "ogg", "m4a")
    val SUPPORTED_VIDEO_EXTENSIONS = setOf("mp4", "mkv", "webm", "avi", "mov")
    val SUPPORTED_IMAGE_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")

    val ALL_SUPPORTED_EXTENSIONS = SUPPORTED_AUDIO_EXTENSIONS +
            SUPPORTED_VIDEO_EXTENSIONS +
            SUPPORTED_IMAGE_EXTENSIONS

    /**
     * Checks if a given file extension or file name is supported by VLC.
     */
    fun isVlcSupported(fileNameOrExtension: String): Boolean {
        val ext = fileNameOrExtension.substringAfterLast('.', fileNameOrExtension).lowercase().trim()
        return ALL_SUPPORTED_EXTENSIONS.contains(ext)
    }

    /**
     * Checks if a MediaItem is supported by VLC.
     */
    fun isVlcSupportedMedia(mediaItem: MediaItem): Boolean {
        if (mediaItem.type == MediaType.FOLDER) return false
        val extFromFormat = mediaItem.format.lowercase().trim()
        if (ALL_SUPPORTED_EXTENSIONS.contains(extFromFormat)) return true
        val extFromPath = mediaItem.path.substringAfterLast('.', "").lowercase().trim()
        return ALL_SUPPORTED_EXTENSIONS.contains(extFromPath)
    }

    /**
     * Validates server address to strictly allow only http / https schemes
     * and prevent unsafe schemes like javascript:, data:, file:.
     */
    fun validateAndNormalizeServerAddress(serverAddress: String): Result<String> {
        val trimmed = serverAddress.trim()
        if (trimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Server address cannot be empty"))
        }

        val lower = trimmed.lowercase()
        if (lower.startsWith("javascript:") || lower.startsWith("data:") || lower.startsWith("file:") || lower.startsWith("content:")) {
            return Result.failure(SecurityException("Dangerous or unsupported URL scheme rejected"))
        }

        val fullUrlStr = if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            "http://$trimmed"
        } else {
            trimmed
        }

        return try {
            val parsedUri = URI(fullUrlStr)
            val scheme = parsedUri.scheme?.lowercase()
            if (scheme != "http" && scheme != "https") {
                return Result.failure(IllegalArgumentException("Only HTTP and HTTPS stream protocols are supported"))
            }
            val host = parsedUri.host
            if (host.isNullOrBlank()) {
                return Result.failure(IllegalArgumentException("Invalid server host name or IP address"))
            }
            val port = if (parsedUri.port != -1) parsedUri.port else if (scheme == "https") 443 else 80
            val normalizedBase = "$scheme://$host:$port"
            Result.success(normalizedBase)
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Malformed server URL: ${e.localizedMessage}"))
        }
    }

    /**
     * Generates a safe, URL-encoded HTTP stream URL for VLC based on centralized server address and media path.
     * Example: http://192.168.0.4:8080/media/Music/song.mp3
     */
    fun generateVlcStreamUrl(serverAddress: String, mediaPath: String): Result<String> {
        val baseResult = validateAndNormalizeServerAddress(serverAddress)
        if (baseResult.isFailure) {
            return Result.failure(baseResult.exceptionOrNull() ?: IllegalArgumentException("Invalid server address"))
        }
        val baseUrl = baseResult.getOrThrow()

        if (mediaPath.isBlank()) {
            return Result.failure(IllegalArgumentException("Media path cannot be blank"))
        }

        // Clean common absolute prefixes
        val relativePath = mediaPath
            .removePrefix("/storage/emulated/0/LocalStream/")
            .removePrefix("/storage/emulated/0/")
            .removePrefix("/LocalStream/")
            .trimStart('/')

        if (relativePath.isBlank()) {
            return Result.failure(IllegalArgumentException("Invalid relative media path"))
        }

        // Safely encode each segment to handle spaces, foreign characters, etc.
        val segments = relativePath.split("/")
            .filter { it.isNotBlank() }
            .map { segment ->
                URLEncoder.encode(segment, "UTF-8").replace("+", "%20")
            }

        val safePath = segments.joinToString("/")
        val fullStreamUrl = "$baseUrl/media/$safePath"
        return Result.success(fullStreamUrl)
    }

    /**
     * Attempts to launch VLC player on Android.
     * If VLC is not installed or fails to launch, returns Failure so UI can show
     * "VLC could not be opened automatically" with copy URL options.
     */
    fun launchVlcPlayer(
        context: Context,
        streamUrl: String,
        mediaType: MediaType? = null
    ): VlcLaunchResult {
        val uri = try {
            Uri.parse(streamUrl)
        } catch (e: Exception) {
            return VlcLaunchResult.Failure("Malformed stream URL")
        }

        val mimeType = when (mediaType) {
            MediaType.AUDIO -> "audio/*"
            MediaType.VIDEO -> "video/*"
            MediaType.PHOTO -> "image/*"
            else -> "*/*"
        }

        // 1. First attempt: Direct launch targeting VLC package
        try {
            val vlcIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                setPackage("org.videolan.vlc")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(vlcIntent)
            return VlcLaunchResult.Success
        } catch (e: ActivityNotFoundException) {
            // VLC package is not installed or not exported for this intent
        } catch (e: Exception) {
            // Permission or security exception
        }

        // 2. Second attempt: Generic media view chooser
        try {
            val genericIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (genericIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(genericIntent)
                return VlcLaunchResult.Success
            }
        } catch (e: Exception) {
            // Fallback failed
        }

        // Neither succeeded; report failure so UI displays manual instructions
        return VlcLaunchResult.Failure("VLC could not be opened automatically.")
    }
}
