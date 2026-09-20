package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LocalStreamPreferences
import com.example.data.VlcStreamHistoryEntry
import com.example.model.MediaItem
import com.example.model.MediaType
import com.example.services.VlcLaunchResult
import com.example.services.VlcStreamHelper
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VlcStreamDialog(
    mediaTitle: String,
    mediaPath: String,
    mediaType: MediaType? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { LocalStreamPreferences(context) }
    val serverConfig = remember { prefs.loadServerConfig() }
    val coroutineScope = rememberCoroutineScope()

    val serverAddress = serverConfig.baseUrl
    val isServerRunning = serverConfig.isRunning

    // Generate safe stream URL
    val streamUrlResult = remember(serverAddress, mediaPath) {
        VlcStreamHelper.generateVlcStreamUrl(serverAddress, mediaPath)
    }
    val streamUrl = streamUrlResult.getOrDefault("")
    val urlError = streamUrlResult.exceptionOrNull()?.localizedMessage

    var isCopied by remember { mutableStateOf(false) }
    var vlcLaunchFailed by remember { mutableStateOf(false) }
    var vlcLaunchErrorMessage by remember { mutableStateOf("") }
    var isInstructionsExpanded by remember { mutableStateOf(false) }
    var recentHistory by remember { mutableStateOf(prefs.getVlcStreamHistory()) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    fun copyUrlToClipboard() {
        if (streamUrl.isNotBlank()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("VLC Stream URL", streamUrl)
            clipboard.setPrimaryClip(clip)
            isCopied = true
            Toast.makeText(context, "Stream URL copied.", Toast.LENGTH_SHORT).show()

            // Record in history
            prefs.addVlcStreamHistoryEntry(
                VlcStreamHistoryEntry(
                    id = UUID.randomUUID().toString(),
                    mediaTitle = mediaTitle,
                    streamUrl = streamUrl,
                    mediaType = mediaType?.name ?: "MEDIA",
                    timestamp = System.currentTimeMillis()
                )
            )
            recentHistory = prefs.getVlcStreamHistory()

            coroutineScope.launch {
                delay(3000)
                isCopied = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .widthIn(max = 540.dp)
                .padding(vertical = 24.dp)
                .testTag("vlc_stream_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dialog Header: Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFFF8800), Color(0xFFFF5500))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "VLC Player",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "VLC Player",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = mediaTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(DarkSurfaceVariant, CircleShape)
                            .testTag("btn_close_vlc_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Architecture Tag: VLC Stream URL Generation Notice
                Surface(
                    color = DarkSurfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFB388FF),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "VLC stream URL generation • Live HTTP stream served from active LocalStream backend",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }

                // Section 7: LocalStream Server & Stream Status
                DarkCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "LocalStream",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isServerRunning) Color(0xFF10B981) else Color(0xFFEF4444))
                                )
                                Text(
                                    text = "Server:",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isServerRunning) "Running" else "Stopped",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isServerRunning) Color(0xFF10B981) else Color(0xFFEF4444)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Stream:",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isServerRunning && streamUrl.isNotBlank()) "Ready" else "Unavailable",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (isServerRunning && streamUrl.isNotBlank()) Color(0xFF10B981) else Color(0xFFF59E0B)
                                )
                            }
                        }

                        if (!isServerRunning) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Start the LocalStream server before opening this stream.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        } else {
                            Text(
                                text = "Stream ready (${serverConfig.ipAddress}:${serverConfig.port})",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = TextMuted
                            )
                        }
                    }
                }

                // Section 2: Stream URL Display Box
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Stream URL",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Surface(
                        color = DarkBackground,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SelectionContainer {
                            Text(
                                text = if (urlError != null) "Error: $urlError" else streamUrl,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                ),
                                color = if (urlError != null) Color(0xFFEF4444) else Color(0xFFE2E8F0),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .testTag("text_vlc_stream_url")
                            )
                        }
                    }
                }

                // Section 3: VLC Launch Failure Alert (if triggered)
                if (vlcLaunchFailed) {
                    DarkCard(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2D1818)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7F1D1D)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = vlcLaunchErrorMessage.ifEmpty { "VLC could not be opened automatically." },
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFCA5A5)
                                    )
                                )
                            }
                            Text(
                                text = "Copy this URL and open it in VLC using Network Stream.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE2E8F0))
                            )
                            Button(
                                onClick = { copyUrlToClipboard() },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Copy Stream URL", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Section 4: Collapsible "How to play in VLC" Instructions
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isInstructionsExpanded = !isInstructionsExpanded }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = null,
                                    tint = AccentViolet,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "How to play in VLC",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = TextPrimary
                                )
                            }
                            Icon(
                                imageVector = if (isInstructionsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isInstructionsExpanded) "Collapse" else "Expand",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = isInstructionsExpanded,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val instructions = listOf(
                                    "1. Open VLC.",
                                    "2. Select \"Open Network Stream\".",
                                    "3. Paste the LocalStream URL.",
                                    "4. Select Play."
                                )
                                instructions.forEach { step ->
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 2 & 3: Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Copy URL Button
                    OutlinedButton(
                        onClick = { copyUrlToClipboard() },
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCopied) Color(0xFF10B981) else DarkCardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isCopied) Color(0xFF10B981) else TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_vlc_copy_url")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isCopied) Color(0xFF10B981) else TextPrimary
                            )
                            Text(
                                text = if (isCopied) "Copied!" else "Copy URL",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Open VLC Button
                    Button(
                        onClick = {
                            if (isServerRunning && streamUrl.isNotBlank()) {
                                // Save to recent history
                                prefs.addVlcStreamHistoryEntry(
                                    VlcStreamHistoryEntry(
                                        id = UUID.randomUUID().toString(),
                                        mediaTitle = mediaTitle,
                                        streamUrl = streamUrl,
                                        mediaType = mediaType?.name ?: "MEDIA",
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                                recentHistory = prefs.getVlcStreamHistory()

                                val result = VlcStreamHelper.launchVlcPlayer(
                                    context = context,
                                    streamUrl = streamUrl,
                                    mediaType = mediaType
                                )
                                when (result) {
                                    is VlcLaunchResult.Success -> {
                                        Toast.makeText(context, "Opening VLC...", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    }
                                    is VlcLaunchResult.Failure -> {
                                        vlcLaunchFailed = true
                                        vlcLaunchErrorMessage = result.message
                                    }
                                }
                            }
                        },
                        enabled = isServerRunning && streamUrl.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF7700),
                            contentColor = Color.White,
                            disabledContainerColor = DarkSurfaceVariant,
                            disabledContentColor = TextMuted
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("btn_vlc_open_player")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Open VLC",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Section 11: Recent VLC Streams (Lightweight history)
                if (recentHistory.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Recent VLC Streams",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary
                            )
                            Text(
                                text = "Clear History",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = StopButtonPink,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { showClearHistoryConfirm = true }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .testTag("btn_clear_vlc_history")
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            recentHistory.take(5).forEach { entry ->
                                val dateStr = remember(entry.timestamp) {
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
                                }
                                Surface(
                                    color = DarkSurfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = entry.mediaTitle,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                                color = TextPrimary,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = entry.streamUrl,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp
                                                ),
                                                color = TextMuted,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        Text(
                                            text = dateStr,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                            color = TextMuted,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_vlc_cancel")
                ) {
                    Text(
                        text = "Cancel",
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showClearHistoryConfirm) {
        ConfirmationActionDialog(
            title = "Clear Stream History?",
            message = "This will remove all recent VLC stream links stored on this device.",
            confirmText = "Clear History",
            isDestructive = true,
            onConfirm = {
                prefs.clearVlcStreamHistory()
                recentHistory = emptyList()
                Toast.makeText(context, "VLC stream history cleared", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showClearHistoryConfirm = false }
        )
    }
}
