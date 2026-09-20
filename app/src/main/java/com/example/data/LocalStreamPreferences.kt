package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ServerConfig
import org.json.JSONArray
import org.json.JSONObject

data class VlcStreamHistoryEntry(
    val id: String,
    val mediaTitle: String,
    val streamUrl: String,
    val mediaType: String,
    val timestamp: Long
)

class LocalStreamPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("localstream_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SERVER_NAME = "key_server_name"
        private const val KEY_IP_ADDRESS = "key_ip_address"
        private const val KEY_PORT = "key_port"
        private const val KEY_HTTP_SERVER = "key_http_server"
        private const val KEY_UPNP_STREAMING = "key_upnp_streaming"
        private const val KEY_UPNP_RECEIVER = "key_upnp_receiver"
        private const val KEY_UPNP_SORT_ORDER = "key_upnp_sort_order"
        private const val KEY_SMB_SEARCH = "key_smb_search"
        private const val KEY_TRANSCODE_FLAC = "key_transcode_flac"
        private const val KEY_AUTO_START = "key_auto_start"
        private const val KEY_SERVER_RUNNING = "key_server_running"
        private const val KEY_PWA_DISMISSED = "key_pwa_dismissed"
        private const val KEY_VLC_HISTORY = "key_vlc_history"
    }

    fun loadServerConfig(): ServerConfig {
        return ServerConfig(
            serverName = prefs.getString(KEY_SERVER_NAME, "LocalStream") ?: "LocalStream",
            ipAddress = prefs.getString(KEY_IP_ADDRESS, "192.168.0.4") ?: "192.168.0.4",
            port = prefs.getInt(KEY_PORT, 8080),
            httpWebServer = prefs.getBoolean(KEY_HTTP_SERVER, true),
            upnpContentStreaming = prefs.getBoolean(KEY_UPNP_STREAMING, true),
            upnpMediaReceiver = prefs.getBoolean(KEY_UPNP_RECEIVER, true),
            upnpSortOrder = prefs.getString(KEY_UPNP_SORT_ORDER, "Name (A-Z)") ?: "Name (A-Z)",
            smbServerSearch = prefs.getBoolean(KEY_SMB_SEARCH, false),
            transcodeFlacToWav = prefs.getBoolean(KEY_TRANSCODE_FLAC, true),
            autoStartServer = prefs.getBoolean(KEY_AUTO_START, false),
            isRunning = prefs.getBoolean(KEY_SERVER_RUNNING, true)
        )
    }

    fun saveServerConfig(config: ServerConfig) {
        prefs.edit()
            .putString(KEY_SERVER_NAME, config.serverName)
            .putString(KEY_IP_ADDRESS, config.ipAddress)
            .putInt(KEY_PORT, config.port)
            .putBoolean(KEY_HTTP_SERVER, config.httpWebServer)
            .putBoolean(KEY_UPNP_STREAMING, config.upnpContentStreaming)
            .putBoolean(KEY_UPNP_RECEIVER, config.upnpMediaReceiver)
            .putString(KEY_UPNP_SORT_ORDER, config.upnpSortOrder)
            .putBoolean(KEY_SMB_SEARCH, config.smbServerSearch)
            .putBoolean(KEY_TRANSCODE_FLAC, config.transcodeFlacToWav)
            .putBoolean(KEY_AUTO_START, config.autoStartServer)
            .putBoolean(KEY_SERVER_RUNNING, config.isRunning)
            .apply()
    }

    fun isPwaInstallDismissed(): Boolean {
        return prefs.getBoolean(KEY_PWA_DISMISSED, false)
    }

    fun setPwaInstallDismissed(dismissed: Boolean) {
        prefs.edit().putBoolean(KEY_PWA_DISMISSED, dismissed).apply()
    }

    fun getVlcStreamHistory(): List<VlcStreamHistoryEntry> {
        val jsonStr = prefs.getString(KEY_VLC_HISTORY, null) ?: return emptyList()
        val list = mutableListOf<VlcStreamHistoryEntry>()
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    VlcStreamHistoryEntry(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        mediaTitle = obj.optString("mediaTitle", "Unknown Media"),
                        streamUrl = obj.optString("streamUrl", ""),
                        mediaType = obj.optString("mediaType", "MEDIA"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun addVlcStreamHistoryEntry(entry: VlcStreamHistoryEntry) {
        val current = getVlcStreamHistory().toMutableList()
        // Remove if duplicate streamUrl
        current.removeAll { it.streamUrl == entry.streamUrl }
        // Insert at the front
        current.add(0, entry)
        // Keep max 10
        val trimmed = current.take(10)
        try {
            val jsonArray = JSONArray()
            for (item in trimmed) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("mediaTitle", item.mediaTitle)
                    put("streamUrl", item.streamUrl)
                    put("mediaType", item.mediaType)
                    put("timestamp", item.timestamp)
                }
                jsonArray.put(obj)
            }
            prefs.edit().putString(KEY_VLC_HISTORY, jsonArray.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearVlcStreamHistory() {
        prefs.edit().remove(KEY_VLC_HISTORY).apply()
    }
}
