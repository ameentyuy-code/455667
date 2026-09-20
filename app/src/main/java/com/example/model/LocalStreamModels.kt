package com.example.model

data class ServerConfig(
    val ipAddress: String = "192.168.0.4",
    val port: Int = 8080,
    val isRunning: Boolean = true,
    val connectedClients: Int = 0,
    val uptimeSeconds: Long = 4320L,
    val dlnaEnabled: Boolean = true,
    val transcodingEnabled: Boolean = true,
    val authEnabled: Boolean = false,
    val mediaDirectory: String = "/storage/emulated/0/LocalStream",
    val serverName: String = "LocalStream",
    val httpWebServer: Boolean = true,
    val upnpContentStreaming: Boolean = true,
    val upnpMediaReceiver: Boolean = true,
    val upnpSortOrder: String = "Name (A-Z)",
    val smbServerSearch: Boolean = false,
    val transcodeFlacToWav: Boolean = true,
    val autoStartServer: Boolean = false
) {
    val baseUrl: String get() = "http://$ipAddress:$port"
    val serverAddress: String get() = baseUrl
    val streamUrl: String get() = "$baseUrl/stream"
    val dlnaUrl: String get() = "$baseUrl/dlna/desc.xml"
    val apiUrl: String get() = "$baseUrl/api/v1/media"
    val hlsUrl: String get() = "$baseUrl/hls/live.m3u8"
}

data class ServerStatus(
    val isRunning: Boolean,
    val serverAddress: String,
    val ipAddress: String,
    val port: Int,
    val connectedClients: Int,
    val uptimeSeconds: Long
)

data class ServerSettings(
    val serverName: String = "LocalStream",
    val ipAddress: String = "192.168.0.4",
    val port: Int = 8080,
    val autoStart: Boolean = false,
    val dlnaEnabled: Boolean = true,
    val transcodingEnabled: Boolean = true,
    val authEnabled: Boolean = false
)

enum class NetworkStatus {
    ONLINE, OFFLINE
}

data class PlaybackState(
    val currentMedia: MediaItem? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val durationSeconds: Long = 0L,
    val durationFormatted: String = "00:00",
    val volume: Float = 0.8f,
    val queue: List<MediaItem> = emptyList(),
    val currentIndex: Int = 0
)

data class ServerUrlInfo(
    val title: String,
    val url: String,
    val description: String,
    val tag: String
)

data class MediaItem(
    val id: String,
    val title: String,
    val type: MediaType,
    val size: String,
    val duration: String = "00:00",
    val resolutionOrBitrate: String = "",
    val format: String = "",
    val artist: String = "",
    val album: String = "",
    val thumbnail: String = "",
    val path: String = "",
    val folder: String = ""
)

data class FolderItem(
    val id: String,
    val name: String,
    val path: String,
    val itemCount: Int,
    val sizeFormatted: String = "0 B"
)

enum class MediaType {
    VIDEO, AUDIO, PHOTO, FOLDER
}

data class RendererDevice(
    val id: String,
    val name: String,
    val type: String,
    val ipAddress: String,
    val isOnline: Boolean,
    val isConnected: Boolean = false,
    val isLocal: Boolean = false
)

data class AdbDevice(
    val id: String,
    val ipAddress: String,
    val port: Int = 5555,
    val isConnected: Boolean,
    val model: String = "Android Device"
)

data class CastRenderer(
    val id: String,
    val name: String,
    val type: String, // LG webOS TV, Chromecast, DLNA DMR, etc.
    val ipAddress: String,
    val isOnline: Boolean,
    val isConnected: Boolean = false,
    val volume: Float = 0.65f,
    val currentTrack: String? = null
)

data class AdbSession(
    val port: Int = 5555,
    val ipAddress: String = "192.168.0.4",
    val isWirelessAdbActive: Boolean = true,
    val pairedDevicesCount: Int = 1,
    val logs: List<String> = listOf(
        "[INFO] LocalStream daemon listening on tcp:8080",
        "[INFO] DLNA SSDP notify packet broadcast sent to 239.255.255.250:1900",
        "[DEBUG] HTTP server thread pool ready with 8 worker threads",
        "[INFO] ADB debug bridge enabled on port 5555",
        "[OK] Media catalog indexed 48 local media streams"
    )
)

enum class ToastType {
    SUCCESS, ERROR, WARNING, INFO
}

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val message: String,
    val type: ToastType = ToastType.INFO
)
