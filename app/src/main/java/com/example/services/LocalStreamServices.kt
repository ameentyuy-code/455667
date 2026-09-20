package com.example.services

import com.example.model.*

// --- Server Service ---
interface ServerService {
    fun getServerStatus(): ServerStatus
    fun startServer(port: Int = 8080): ServerStatus
    fun stopServer(): ServerStatus
    fun updateSettings(settings: ServerSettings): ServerStatus
}

class MockServerService(
    private var currentConfig: ServerConfig = ServerConfig()
) : ServerService {
    override fun getServerStatus(): ServerStatus {
        return ServerStatus(
            isRunning = currentConfig.isRunning,
            serverAddress = currentConfig.baseUrl,
            ipAddress = currentConfig.ipAddress,
            port = currentConfig.port,
            connectedClients = currentConfig.connectedClients,
            uptimeSeconds = currentConfig.uptimeSeconds
        )
    }

    override fun startServer(port: Int): ServerStatus {
        currentConfig = currentConfig.copy(isRunning = true, port = port)
        return getServerStatus()
    }

    override fun stopServer(): ServerStatus {
        currentConfig = currentConfig.copy(isRunning = false)
        return getServerStatus()
    }

    override fun updateSettings(settings: ServerSettings): ServerStatus {
        currentConfig = currentConfig.copy(
            serverName = settings.serverName,
            ipAddress = settings.ipAddress,
            port = settings.port,
            autoStartServer = settings.autoStart,
            dlnaEnabled = settings.dlnaEnabled,
            transcodingEnabled = settings.transcodingEnabled,
            authEnabled = settings.authEnabled
        )
        return getServerStatus()
    }
}

// --- Media Service ---
data class StreamMediaResponse(
    val streamUrl: String,
    val mimeType: String,
    val isDirectStreamingAvailable: Boolean,
    val note: String = "VLC stream URL generation mode. Live byte-range streaming requires the active LocalStream HTTP backend."
)

interface MediaService {
    fun getMedia(type: MediaType? = null, folderId: String? = null): List<MediaItem>
    fun getFolders(): List<FolderItem>
    fun searchMedia(query: String): List<MediaItem>
    fun streamMedia(mediaPath: String): StreamMediaResponse
}

class MockMediaService : MediaService {
    private val folders = listOf(
        FolderItem("f1", "Music", "/storage/emulated/0/LocalStream/Music", 18, "340 MB"),
        FolderItem("f2", "Movies & Shows", "/storage/emulated/0/LocalStream/Movies", 6, "4.2 GB"),
        FolderItem("f3", "Wallpapers & Photos", "/storage/emulated/0/LocalStream/Photos", 14, "88 MB"),
        FolderItem("f4", "Podcasts", "/storage/emulated/0/LocalStream/Podcasts", 5, "180 MB"),
        FolderItem("f5", "Recordings", "/storage/emulated/0/LocalStream/Recordings", 5, "42 MB")
    )

    private val mediaCatalog = listOf(
        MediaItem(
            id = "m1",
            title = "ŞULTANRŞ - Going Dark (Slow...)",
            type = MediaType.AUDIO,
            size = "8.4 MB",
            duration = "03:42",
            resolutionOrBitrate = "320 kbps",
            format = "MP3",
            artist = "ŞULTANRŞ",
            album = "Going Dark EP",
            path = "/storage/emulated/0/LocalStream/Music/Going_Dark.mp3",
            folder = "Music"
        ),
        MediaItem(
            id = "m2",
            title = "AETERNUM",
            type = MediaType.AUDIO,
            size = "6.1 MB",
            duration = "02:55",
            resolutionOrBitrate = "320 kbps",
            format = "MP3",
            artist = "Aeternum Project",
            album = "Origins",
            path = "/storage/emulated/0/LocalStream/Music/Aeternum.mp3",
            folder = "Music"
        ),
        MediaItem(
            id = "m3",
            title = "ARABIC",
            type = MediaType.AUDIO,
            size = "7.3 MB",
            duration = "03:10",
            resolutionOrBitrate = "256 kbps",
            format = "MP3",
            artist = "Desert Wave",
            album = "Dune Beats",
            path = "/storage/emulated/0/LocalStream/Music/Arabic.mp3",
            folder = "Music"
        ),
        MediaItem(
            id = "m4",
            title = "AVANGARD - Super Slowed + ...",
            type = MediaType.AUDIO,
            size = "9.2 MB",
            duration = "04:12",
            resolutionOrBitrate = "320 kbps",
            format = "MP3",
            artist = "AVANGARD",
            album = "Midnight Sessions",
            path = "/storage/emulated/0/LocalStream/Music/Avangard.mp3",
            folder = "Music"
        ),
        MediaItem(
            id = "m5",
            title = "After Hours",
            type = MediaType.AUDIO,
            size = "8.8 MB",
            duration = "03:50",
            resolutionOrBitrate = "320 kbps",
            format = "FLAC",
            artist = "The Weeknd",
            album = "After Hours",
            path = "/storage/emulated/0/LocalStream/Music/After_Hours.flac",
            folder = "Music"
        ),
        MediaItem(
            id = "m6",
            title = "All Eyez On Me (Groove Edit)",
            type = MediaType.AUDIO,
            size = "10.4 MB",
            duration = "04:35",
            resolutionOrBitrate = "320 kbps",
            format = "MP3",
            artist = "2Pac / Groove Edit",
            album = "All Eyez On Me",
            path = "/storage/emulated/0/LocalStream/Music/All_Eyez.mp3",
            folder = "Music"
        ),
        MediaItem(
            id = "v1",
            title = "Nature 4K Documentary Stream",
            type = MediaType.VIDEO,
            size = "1.8 GB",
            duration = "45:20",
            resolutionOrBitrate = "3840x2160 • 60fps",
            format = "MKV",
            artist = "BBC Earth Studio",
            path = "/storage/emulated/0/LocalStream/Movies/Nature_4K.mkv",
            folder = "Movies & Shows"
        ),
        MediaItem(
            id = "v2",
            title = "Cyberpunk Neo-Tokyo Showcase",
            type = MediaType.VIDEO,
            size = "620 MB",
            duration = "12:45",
            resolutionOrBitrate = "1920x1080 • HDR",
            format = "MP4",
            artist = "Unreal Render Lab",
            path = "/storage/emulated/0/LocalStream/Movies/Cyberpunk.mp4",
            folder = "Movies & Shows"
        ),
        MediaItem(
            id = "p1",
            title = "Purple Neon City Nightscape",
            type = MediaType.PHOTO,
            size = "4.2 MB",
            duration = "Image",
            resolutionOrBitrate = "4096x2304 • Ultra-HD",
            format = "PNG",
            artist = "Wallpaper Collection",
            path = "/storage/emulated/0/LocalStream/Photos/Neon_City.png",
            folder = "Wallpapers & Photos"
        ),
        MediaItem(
            id = "p2",
            title = "Abstract Violet Wave Canvas",
            type = MediaType.PHOTO,
            size = "3.8 MB",
            duration = "Image",
            resolutionOrBitrate = "3840x2160 • OLED",
            format = "JPG",
            artist = "Minimal Works",
            path = "/storage/emulated/0/LocalStream/Photos/Abstract_Violet.jpg",
            folder = "Wallpapers & Photos"
        )
    )

    override fun getMedia(type: MediaType?, folderId: String?): List<MediaItem> {
        var result = mediaCatalog
        if (type != null) {
            result = result.filter { it.type == type }
        }
        if (folderId != null) {
            val folder = folders.find { it.id == folderId }
            if (folder != null) {
                result = result.filter { it.folder == folder.name }
            }
        }
        return result
    }

    override fun getFolders(): List<FolderItem> = folders

    override fun searchMedia(query: String): List<MediaItem> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return mediaCatalog
        return mediaCatalog.filter {
            it.title.lowercase().contains(q) ||
            it.artist.lowercase().contains(q) ||
            it.album.lowercase().contains(q) ||
            it.format.lowercase().contains(q)
        }
    }

    override fun streamMedia(mediaPath: String): StreamMediaResponse {
        val extension = mediaPath.substringAfterLast('.', "").lowercase()
        val mimeType = when (extension) {
            "mp3" -> "audio/mpeg"
            "flac" -> "audio/flac"
            "wav" -> "audio/wav"
            "aac" -> "audio/aac"
            "ogg" -> "audio/ogg"
            "m4a" -> "audio/mp4"
            "mp4" -> "video/mp4"
            "mkv" -> "video/x-matroska"
            "webm" -> "video/webm"
            "avi" -> "video/x-msvideo"
            "mov" -> "video/quicktime"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "application/octet-stream"
        }
        val cleanPath = mediaPath.removePrefix("/storage/emulated/0/LocalStream/").trimStart('/')
        val encodedSegments = cleanPath.split("/").map { java.net.URLEncoder.encode(it, "UTF-8").replace("+", "%20") }
        val streamUrl = "http://192.168.0.4:8080/media/${encodedSegments.joinToString("/")}"
        return StreamMediaResponse(
            streamUrl = streamUrl,
            mimeType = mimeType,
            isDirectStreamingAvailable = false,
            note = "VLC stream URL generation mode. Live byte-range streaming requires the active LocalStream HTTP backend."
        )
    }
}

// --- Renderer Service ---
interface RendererService {
    fun getRenderers(): List<RendererDevice>
    fun connectRenderer(id: String): RendererDevice?
    fun disconnectRenderer(id: String): Boolean
}

class MockRendererService : RendererService {
    private val devices = mutableListOf(
        RendererDevice("local", "Play Locally (This Device)", "Internal Audio & Video Engine", "127.0.0.1", true, true, isLocal = true),
        RendererDevice("lg_tv", "Living Room LG webOS OLED", "DLNA / UPnP DMR • 4K HDR", "192.168.0.12", true, false),
        RendererDevice("chromecast", "Bedroom Chromecast Ultra", "Google Cast Receiver", "192.168.0.18", true, false),
        RendererDevice("samsung_tv", "Samsung Crystal UHD 4K", "Samsung Smart View / DLNA", "192.168.0.25", true, false),
        RendererDevice("sonos", "Office Soundbar (Sonos UPnP)", "Hi-Fi Audio Renderer", "192.168.0.33", true, false)
    )

    override fun getRenderers(): List<RendererDevice> = devices.toList()

    override fun connectRenderer(id: String): RendererDevice? {
        val target = devices.find { it.id == id } ?: return null
        for (i in devices.indices) {
            devices[i] = devices[i].copy(isConnected = devices[i].id == id)
        }
        return target.copy(isConnected = true)
    }

    override fun disconnectRenderer(id: String): Boolean {
        val idx = devices.indexOfFirst { it.id == id }
        if (idx >= 0) {
            devices[idx] = devices[idx].copy(isConnected = false)
            // Reconnect local
            val localIdx = devices.indexOfFirst { it.isLocal }
            if (localIdx >= 0) {
                devices[localIdx] = devices[localIdx].copy(isConnected = true)
            }
            return true
        }
        return false
    }
}

// --- ADB Service ---
interface AdbService {
    fun getAdbStatus(): AdbDevice
    fun connectAdb(address: String): Boolean
    fun disconnectAdb(): Boolean
    fun getLogs(): List<String>
}

class MockAdbService : AdbService {
    private var isConnected: Boolean = true

    override fun getAdbStatus(): AdbDevice {
        return AdbDevice(
            id = "local-adb",
            ipAddress = "192.168.0.4",
            port = 5555,
            isConnected = isConnected
        )
    }

    override fun connectAdb(address: String): Boolean {
        isConnected = true
        return true
    }

    override fun disconnectAdb(): Boolean {
        isConnected = false
        return true
    }

    override fun getLogs(): List<String> {
        return listOf(
            "[SYSTEM] ADB server listening on 0.0.0.0:5555",
            "[NET] LocalStream daemon v2.4 initialized on 192.168.0.4:8080",
            "[DLNA] SSDP Multicast loop initialized on 239.255.255.250:1900",
            "[SYSTEM] MediaCodec H.264 hardware encoder verified (OMX.google.h264.encoder)",
            "[NET] Bound HTTP streaming socket on port 8080",
            "[ADB] Device authorized by RSA key fingerprint: SHA256:d8a4f9e12c...",
            "[DLNA] Notified M-SEARCH listener: urn:schemas-upnp-org:device:MediaServer:1",
            "[OK] Streaming engine ready. Listening for local renderers"
        )
    }
}

// --- Services Container ---
object LocalStreamServices {
    val server: ServerService = MockServerService()
    val media: MediaService = MockMediaService()
    val renderer: RendererService = MockRendererService()
    val adb: AdbService = MockAdbService()
}
