package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerConfig
import com.example.ui.components.ConfirmationActionDialog
import com.example.ui.components.CopyableUrlField
import com.example.ui.theme.*

enum class LogFilterCategory(val label: String) {
    ALL("All Logs"),
    SYSTEM("System"),
    NETWORK("Network"),
    DLNA("DLNA/UPnP")
}

@Composable
fun AdbScreen(
    serverConfig: ServerConfig = ServerConfig(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isWirelessAdbEnabled by remember { mutableStateOf(true) }
    var showDisconnectConfirmation by remember { mutableStateOf(false) }
    var customCommand by remember { mutableStateOf("") }
    var selectedLogFilter by remember { mutableStateOf(LogFilterCategory.ALL) }

    var logLines by remember {
        mutableStateOf(
            listOf(
                "[SYSTEM] ADB server listening on 0.0.0.0:5555",
                "[NET] LocalStream daemon v2.4 initialized on ${serverConfig.ipAddress}:${serverConfig.port}",
                "[DLNA] SSDP Multicast loop initialized on 239.255.255.250:1900",
                "[SYSTEM] MediaCodec H.264 hardware encoder verified (OMX.google.h264.encoder)",
                "[NET] Bound HTTP streaming socket on port ${serverConfig.port}",
                "[ADB] Device authorized by RSA key fingerprint: SHA256:d8a4f9e12c...",
                "[DLNA] Notified M-SEARCH listener: urn:schemas-upnp-org:device:MediaServer:1",
                "[OK] Streaming engine ready. Listening for local renderers"
            )
        )
    }

    val filteredLogs = remember(logLines, selectedLogFilter) {
        when (selectedLogFilter) {
            LogFilterCategory.ALL -> logLines
            LogFilterCategory.SYSTEM -> logLines.filter { it.contains("[SYSTEM]") || it.contains("[ADB]") }
            LogFilterCategory.NETWORK -> logLines.filter { it.contains("[NET]") || it.contains("HTTP") || it.contains("socket") }
            LogFilterCategory.DLNA -> logLines.filter { it.contains("[DLNA]") || it.contains("SSDP") || it.contains("M-SEARCH") }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "⚡ Wireless ADB & Tools",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Android System & Streaming Diagnostics",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isWirelessAdbEnabled) StartButtonGreen.copy(alpha = 0.18f) else DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isWirelessAdbEnabled) StartButtonGreen.copy(alpha = 0.5f) else DarkCardBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isWirelessAdbEnabled) StartButtonGreen else TextMuted)
                    )
                    Text(
                        text = if (isWirelessAdbEnabled) "PORT 5555" else "OFFLINE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = if (isWirelessAdbEnabled) StartButtonGreen else TextMuted
                    )
                }
            }
        }

        // Wireless ADB Status & Connection Card
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_adb_status")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AccentViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Adb,
                                contentDescription = null,
                                tint = Color(0xFFB388FF),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Wireless Debugging",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = if (isWirelessAdbEnabled) "Listening on ${serverConfig.ipAddress}:5555" else "Daemon suspended",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = if (isWirelessAdbEnabled) StartButtonGreen else TextMuted
                            )
                        }
                    }

                    Switch(
                        checked = isWirelessAdbEnabled,
                        onCheckedChange = {
                            if (!it) {
                                showDisconnectConfirmation = true
                            } else {
                                isWirelessAdbEnabled = true
                                logLines = logLines + "[SYSTEM] adb tcpip 5555 -> listening"
                                Toast.makeText(context, "Wireless ADB daemon started on port 5555", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentViolet,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSurfaceVariant
                        )
                    )
                }

                CopyableUrlField(
                    title = "Remote Connect Command",
                    url = "adb connect ${serverConfig.ipAddress}:5555",
                    description = "Run from PC terminal to inspect stream packets",
                    tag = "ADB TCP"
                )

                // Device Specs Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📱 Android 14 • API 34",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = "RAM: 4.8 / 12 GB",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                    Text(
                        text = "⚡ 89% Charging",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = StartButtonGreen
                    )
                }
            }
        }

        // Quick Debug Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    logLines = logLines + "[NET] PING ${serverConfig.ipAddress}:${serverConfig.port} -> 200 OK (latency: 1.4ms)"
                    Toast.makeText(context, "Ping successful (1.4ms)", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.NetworkPing, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Ping Server", fontSize = 12.sp, color = TextPrimary)
            }

            Button(
                onClick = {
                    logLines = logLines + "[SYSTEM] Restarting LocalStream socket pool..." + "[OK] Sockets rebound"
                    Toast.makeText(context, "Streaming daemon restarted", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null, tint = AccentViolet, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Restart Daemon", fontSize = 12.sp, color = TextPrimary)
            }

            Button(
                onClick = {
                    logLines = logLines + "[DLNA] M-SEARCH packet broadcasted to 239.255.255.250"
                    Toast.makeText(context, "DLNA M-SEARCH sent", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Cast, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Dump SSDP", fontSize = 12.sp, color = TextPrimary)
            }

            Button(
                onClick = {
                    logLines = logLines + "[SYSTEM] Transcode cache cleared (0 MB freed)"
                    Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Clear Cache", fontSize = 12.sp, color = TextPrimary)
            }
        }

        // Live Log Output Console
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF07080D),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Console Toolbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "CONSOLE LOGS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = StartButtonGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = StartButtonGreen,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("LocalStream Logs", logLines.joinToString("\n")))
                                Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy logs", tint = TextSecondary, modifier = Modifier.size(15.dp))
                        }

                        IconButton(
                            onClick = {
                                logLines = emptyList()
                                Toast.makeText(context, "Console logs cleared", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear logs", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // Log Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LogFilterCategory.values().forEach { category ->
                        val isSelected = category == selectedLogFilter
                        Surface(
                            onClick = { selectedLogFilter = category },
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) AccentViolet.copy(alpha = 0.3f) else DarkSurfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB388FF)) else null
                        ) {
                            Text(
                                text = category.label,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) Color(0xFFB388FF) else TextSecondary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Log Lines
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredLogs) { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            ),
                            color = when {
                                line.contains("[OK]") || line.contains("ready") -> StartButtonGreen
                                line.contains("[NET]") || line.contains("PING") -> AccentCyan
                                line.contains("[DLNA]") -> Color(0xFFFBBF24)
                                line.contains("[SYSTEM]") -> Color(0xFFB388FF)
                                else -> TextPrimary
                            }
                        )
                    }
                }

                // Command Executor Bar at bottom of console
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customCommand,
                        onValueChange = { customCommand = it },
                        placeholder = { Text("Enter command (e.g. adb logcat, netstat)...", color = TextMuted, fontSize = 11.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB388FF),
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            if (customCommand.isNotBlank()) {
                                logLines = logLines + "> $customCommand" + "[EXEC] Output code 0: OK"
                                customCommand = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("Run", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }

    if (showDisconnectConfirmation) {
        ConfirmationActionDialog(
            title = "Disconnect Wireless ADB?",
            message = "Disabling wireless debugging will terminate active TCP connections on port 5555. Remote terminals will lose access.",
            confirmText = "Disconnect",
            isDestructive = true,
            onConfirm = {
                isWirelessAdbEnabled = false
                logLines = logLines + "[SYSTEM] adb daemon stopped"
                Toast.makeText(context, "Wireless ADB stopped", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showDisconnectConfirmation = false }
        )
    }
}
