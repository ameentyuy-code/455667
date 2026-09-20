package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerConfig
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MyServerScreen(
    serverConfig: ServerConfig,
    onToggleServer: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenPcApp: () -> Unit,
    onOpenTelegram: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Large rounded purple/indigo server status card
        ServerStatusHeroCard(
            serverConfig = serverConfig,
            onToggleServer = onToggleServer,
            onOpenGuide = onOpenGuide,
            onOpenPcApp = onOpenPcApp,
            onOpenTelegram = onOpenTelegram
        )

        // 2. Server Access URLs Card
        ServerAccessUrlsCard(serverConfig = serverConfig)

        // 3. Media API Endpoint & Server Information Card
        ServerInfoAndEndpointsCard(serverConfig = serverConfig)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ServerStatusHeroCard(
    serverConfig: ServerConfig,
    onToggleServer: () -> Unit,
    onOpenGuide: () -> Unit,
    onOpenPcApp: () -> Unit,
    onOpenTelegram: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.verticalGradient(listOf(Color(0xFF8B5CF6).copy(alpha = 0.5f), Color(0xFF4C1D95).copy(alpha = 0.2f))),
                RoundedCornerShape(26.dp)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            ServerCardBgStart,
                            Color(0xFF3B1D82),
                            ServerCardBgEnd
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Rocket Icon + "LocalStream" Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RocketLaunch,
                            contentDescription = "Rocket Icon",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = serverConfig.serverName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }

                // Subtle Visual Status Indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (serverConfig.isRunning) StartButtonGreen.copy(alpha = 0.18f) else StopButtonPink.copy(alpha = 0.18f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (serverConfig.isRunning) StartButtonGreen.copy(alpha = 0.45f) else StopButtonPink.copy(alpha = 0.45f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (serverConfig.isRunning) StartButtonGreen else StopButtonPink)
                        )
                        Text(
                            text = if (serverConfig.isRunning) "Local Wi-Fi Streaming Active" else "Local Streaming Paused",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = if (serverConfig.isRunning) StartButtonGreen else Color(0xFFFCA5A5)
                        )
                    }
                }

                // Subtitle: "Media server running at" or "Media server stopped"
                Text(
                    text = if (serverConfig.isRunning) "Media server running at" else "Media server is currently stopped",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.85f)
                )

                // Display local server address such as "http://192.0.0.4:8080"
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.clickable {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("LocalStream URL", serverConfig.baseUrl)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Server URL copied!", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (serverConfig.isRunning) serverConfig.baseUrl else "Server Offline",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            ),
                            color = if (serverConfig.isRunning) AccentCyan else TextMuted
                        )
                        if (serverConfig.isRunning) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy URL",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Action Row: Pink/Red "Stop Server" button & Connection counter showing network/globe and "0"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Large pink/red rounded "Stop Server" button with square stop icon
                    Button(
                        onClick = onToggleServer,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (serverConfig.isRunning) StopButtonPink else StartButtonGreen
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("server_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (serverConfig.isRunning) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                            contentDescription = if (serverConfig.isRunning) "Stop Server" else "Start Server",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (serverConfig.isRunning) "Stop Server" else "Start Server",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            ),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Small rounded connection counter showing network/globe icon and "0"
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color.Black.copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("connection_counter_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Active Connections",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${serverConfig.connectedClients}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White
                            )
                        }
                    }
                }

                // Text: "{ Stop me if you are done Streaming }"
                Text(
                    text = "{ Stop me if you are done Streaming }",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Links / Buttons:
                // "💡 How to use this app? (Watch Video)"
                // "💻 Get PC App"
                // "🔗 Telegram"
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoLinkButton(
                        icon = "💡",
                        text = "How to use this app? (Watch Video)",
                        onClick = onOpenGuide
                    )
                    InfoLinkButton(
                        icon = "💻",
                        text = "Get PC App",
                        onClick = onOpenPcApp
                    )
                    InfoLinkButton(
                        icon = "🔗",
                        text = "Telegram",
                        onClick = onOpenTelegram
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerAccessUrlsCard(serverConfig: ServerConfig) {
    DarkCard(
        backgroundColor = DarkSurface,
        borderColor = DarkCardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "📡",
                fontSize = 20.sp
            )
            Text(
                text = "Server Access URLs",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Explanatory text: "Use this on any device in the same network"
        Text(
            text = "Use this on any device in the same network",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // URLs with copy buttons
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CopyableUrlField(
                title = "Web Browser UI",
                url = serverConfig.baseUrl,
                description = "Open in Chrome, Safari, Edge, or TV browser",
                tag = "PORT ${serverConfig.port}"
            )

            CopyableUrlField(
                title = "Direct Video Stream",
                url = serverConfig.streamUrl,
                description = "Paste into VLC, mpv, or Kodi network stream",
                tag = "RAW HTTP"
            )

            CopyableUrlField(
                title = "DLNA / UPnP Descriptor",
                url = serverConfig.dlnaUrl,
                description = "Automatic service discovery for smart devices",
                tag = "UPnP"
            )

            CopyableUrlField(
                title = "HLS Playlist (Live Transcode)",
                url = serverConfig.hlsUrl,
                description = "Adaptive bitrate streaming for low bandwidth",
                tag = "HLS .m3u8"
            )
        }
    }
}

@Composable
private fun ServerInfoAndEndpointsCard(serverConfig: ServerConfig) {
    DarkCard(
        backgroundColor = DarkSurface,
        borderColor = DarkCardBorder,
        contentPadding = PaddingValues(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Api,
                contentDescription = null,
                tint = AccentViolet,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Media API & Server Diagnostics",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Endpoints for custom integrations and third-party media players",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Copyable API Endpoint field
        CopyableUrlField(
            title = "Media Catalog REST API",
            url = serverConfig.apiUrl,
            description = "JSON index of all video, audio, and subtitle tracks",
            tag = "REST JSON"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Server Diagnostics & Status Grid
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkSurfaceVariant.copy(alpha = 0.5f),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DiagnosticRow(label = "Engine Status", value = if (serverConfig.isRunning) "ONLINE (HTTP/1.1 & HTTP/2)" else "OFFLINE", isPositive = serverConfig.isRunning)
                DiagnosticRow(label = "Server Broadcast Name", value = serverConfig.serverName)
                DiagnosticRow(label = "UPnP / DLNA Sort Order", value = serverConfig.upnpSortOrder)
                DiagnosticRow(label = "HTTP Web Sharing", value = if (serverConfig.httpWebServer) "Active (:8080)" else "Disabled", isPositive = serverConfig.httpWebServer)
                DiagnosticRow(label = "Hardware Transcoding", value = if (serverConfig.transcodingEnabled) "Enabled (H.264/AAC)" else "Passthrough Only", isPositive = true)
                DiagnosticRow(label = "FLAC Audio Transcoding", value = if (serverConfig.transcodeFlacToWav) "FLAC -> WAV" else "Direct Passthrough", isPositive = serverConfig.transcodeFlacToWav)
                DiagnosticRow(label = "Media Directory", value = serverConfig.mediaDirectory, isMonospace = true)
            }
        }
    }
}

@Composable
private fun DiagnosticRow(
    label: String,
    value: String,
    isPositive: Boolean? = null,
    isMonospace: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = if (isMonospace) FontFamily.Monospace else FontFamily.Default,
                fontWeight = FontWeight.SemiBold
            ),
            color = when (isPositive) {
                true -> StartButtonGreen
                false -> StopButtonPink
                null -> TextPrimary
            }
        )
    }
}
