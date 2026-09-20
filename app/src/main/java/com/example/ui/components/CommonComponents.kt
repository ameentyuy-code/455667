package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ServerConfig
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalStreamTopAppBar(
    onNetworkClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    isServerOnline: Boolean
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "LocalStream",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = TextPrimary
                )
                // Pulsing or colored online dot
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(if (isServerOnline) StartButtonGreen else StopButtonPink)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onNetworkClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_bar_network_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Network Information",
                    tint = TextPrimary
                )
            }
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_bar_favorite_button")
            ) {
                Icon(
                    imageVector = Icons.Default.StarBorder,
                    contentDescription = "Star and Favorites",
                    tint = TextPrimary
                )
            }
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("top_bar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Server Settings",
                    tint = TextPrimary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBg,
            scrolledContainerColor = DarkBg
        )
    )
}

@Composable
fun DarkCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkSurface,
    borderColor: Color = DarkCardBorder,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Composable
fun CopyableUrlField(
    title: String,
    url: String,
    description: String? = null,
    tag: String? = null,
    onQrCodeClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceVariant.copy(alpha = 0.8f))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextSecondary
            )
            if (tag != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = AccentViolet.copy(alpha = 0.2f),
                    border = null
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = AccentViolet,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = url,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Copy button
            IconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText(title, url)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkCardBorder)
                    .testTag("copy_button_${title.lowercase().replace(" ", "_")}")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy $title",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (onQrCodeClick != null) {
                IconButton(
                    onClick = onQrCodeClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkCardBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "View QR",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (!description.isNullOrEmpty()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}

@Composable
fun InfoLinkButton(
    icon: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = DarkSurfaceVariant.copy(alpha = 0.65f),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .testTag("link_${text.take(10).replace(" ", "_").lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = icon,
                fontSize = 18.sp
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// Dialogs
@Composable
fun NetworkInfoDialog(
    serverConfig: ServerConfig,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ServerCardAccent)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Wifi, contentDescription = null, tint = AccentCyan)
                Text("Network Status", color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Local Wi-Fi Network Information", color = TextSecondary, fontSize = 13.sp)
                HorizontalDivider(color = DarkCardBorder)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SSID / Network", color = TextSecondary)
                    Text("Home-WiFi-5G", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Device IP", color = TextSecondary)
                    Text(serverConfig.ipAddress, color = AccentCyan, fontFamily = FontFamily.Monospace)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Server Port", color = TextSecondary)
                    Text("${serverConfig.port}", color = TextPrimary, fontFamily = FontFamily.Monospace)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subnet Mask", color = TextSecondary)
                    Text("255.255.255.0", color = TextPrimary, fontFamily = FontFamily.Monospace)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("DLNA SSDP", color = TextSecondary)
                    Text("Active (Port 1900)", color = StartButtonGreen)
                }
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    badgeText: String? = null
) {
    val context = LocalContext.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                if (enabled) {
                    onCheckedChange(!checked)
                } else if (badgeText != null) {
                    Toast.makeText(context, "$title $badgeText", Toast.LENGTH_SHORT).show()
                }
            }
            .padding(vertical = 8.dp, horizontal = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    ),
                    color = if (enabled) TextPrimary else TextSecondary.copy(alpha = 0.6f)
                )
                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF3B2455),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AccentViolet.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = AccentViolet,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp
                ),
                color = if (enabled) TextSecondary else TextMuted.copy(alpha = 0.6f)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = if (enabled) onCheckedChange else null,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AccentViolet,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = DarkSurfaceVariant,
                uncheckedBorderColor = DarkCardBorder,
                disabledCheckedThumbColor = TextMuted,
                disabledCheckedTrackColor = DarkSurfaceVariant,
                disabledUncheckedThumbColor = TextMuted.copy(alpha = 0.4f),
                disabledUncheckedTrackColor = DarkSurfaceVariant.copy(alpha = 0.4f),
                disabledUncheckedBorderColor = DarkCardBorder.copy(alpha = 0.4f)
            ),
            modifier = Modifier.testTag("switch_${title.take(12).replace(" ", "_").lowercase()}")
        )
    }
}

@Composable
fun SettingsDialog(
    serverConfig: ServerConfig,
    onUpdateConfig: (ServerConfig) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var serverName by remember(serverConfig.serverName) { mutableStateOf(serverConfig.serverName) }
    var httpWebServer by remember(serverConfig.httpWebServer) { mutableStateOf(serverConfig.httpWebServer) }
    var upnpContentStreaming by remember(serverConfig.upnpContentStreaming) { mutableStateOf(serverConfig.upnpContentStreaming) }
    var upnpMediaReceiver by remember(serverConfig.upnpMediaReceiver) { mutableStateOf(serverConfig.upnpMediaReceiver) }
    var upnpSortOrder by remember(serverConfig.upnpSortOrder) { mutableStateOf(serverConfig.upnpSortOrder) }
    var smbServerSearch by remember(serverConfig.smbServerSearch) { mutableStateOf(serverConfig.smbServerSearch) }
    var transcodeFlacToWav by remember(serverConfig.transcodeFlacToWav) { mutableStateOf(serverConfig.transcodeFlacToWav) }

    var isSortDropdownExpanded by remember { mutableStateOf(false) }

    fun notifyUpdate(
        name: String = serverName,
        http: Boolean = httpWebServer,
        upnp: Boolean = upnpContentStreaming,
        receiver: Boolean = upnpMediaReceiver,
        sort: String = upnpSortOrder,
        smb: Boolean = smbServerSearch,
        flac: Boolean = transcodeFlacToWav
    ) {
        onUpdateConfig(
            serverConfig.copy(
                serverName = name,
                httpWebServer = http,
                upnpContentStreaming = upnp,
                upnpMediaReceiver = receiver,
                upnpSortOrder = sort,
                smbServerSearch = smb,
                transcodeFlacToWav = flac
            )
        )
    }

    Dialog(
        onDismissRequest = {
            notifyUpdate()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.86f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(26.dp))
                .testTag("dialog_server_settings"),
            shape = RoundedCornerShape(26.dp),
            color = Color(0xFF161826),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Modal Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentViolet.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = AccentViolet,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Server Settings",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = "UPnP, DLNA & Local Network",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp
                                ),
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            notifyUpdate()
                            onDismiss()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_dismiss_settings_x")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.6f), thickness = 1.dp)

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Section Heading: "General"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(AccentViolet)
                        )
                        Text(
                            text = "GENERAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontSize = 12.sp
                            ),
                            color = AccentViolet
                        )
                    }

                    // Text input labeled "Server Name (UPnP/DLNA)" with default value "LocalStream"
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Server Name (UPnP/DLNA)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = TextPrimary
                        )

                        OutlinedTextField(
                            value = serverName,
                            onValueChange = {
                                serverName = it
                                notifyUpdate(name = it)
                            },
                            placeholder = { Text("LocalStream", color = TextMuted) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentViolet,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                                unfocusedContainerColor = DarkSurfaceVariant.copy(alpha = 0.5f),
                                cursorColor = AccentViolet
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_server_name")
                        )

                        Text(
                            text = "Restart server to broadcast new name",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = TextMuted
                            ),
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.4f), thickness = 1.dp)

                    // Toggle: "HTTP Web Server" with subtitle "Share files via Web Browser"
                    SettingsToggleItem(
                        title = "HTTP Web Server",
                        subtitle = "Share files via Web Browser",
                        checked = httpWebServer,
                        onCheckedChange = {
                            httpWebServer = it
                            notifyUpdate(http = it)
                        }
                    )

                    // Toggle: "UPnP Content Streaming" with subtitle "Share local files over UPnP"
                    SettingsToggleItem(
                        title = "UPnP Content Streaming",
                        subtitle = "Share local files over UPnP",
                        checked = upnpContentStreaming,
                        onCheckedChange = {
                            upnpContentStreaming = it
                            notifyUpdate(upnp = it)
                        }
                    )

                    // Another UPnP-related streaming toggle matching the reference:
                    SettingsToggleItem(
                        title = "UPnP Media Receiver",
                        subtitle = "Allow external devices to push playback to this player",
                        checked = upnpMediaReceiver,
                        onCheckedChange = {
                            upnpMediaReceiver = it
                            notifyUpdate(receiver = it)
                        }
                    )

                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.4f), thickness = 1.dp)

                    // Dropdown: "UPnP/DLNA Sort Order", default "Name (A-Z)"
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "UPnP/DLNA Sort Order",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            ),
                            color = TextPrimary
                        )

                        val sortOptions = listOf(
                            "Name (A-Z)",
                            "Name (Z-A)",
                            "Date Added (Newest)",
                            "Date Added (Oldest)",
                            "File Size (Largest)"
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                onClick = { isSortDropdownExpanded = !isSortDropdownExpanded },
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSortDropdownExpanded) AccentViolet else DarkCardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("dropdown_sort_order")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = upnpSortOrder,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontSize = 14.sp
                                        ),
                                        color = TextPrimary
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Dropdown Arrow",
                                        tint = AccentViolet,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = isSortDropdownExpanded,
                                onDismissRequest = { isSortDropdownExpanded = false },
                                modifier = Modifier
                                    .background(DarkSurfaceVariant)
                                    .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                            ) {
                                sortOptions.forEach { option ->
                                    val isSelected = option == upnpSortOrder
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = option,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = if (isSelected) AccentViolet else TextPrimary,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = AccentViolet,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            upnpSortOrder = option
                                            isSortDropdownExpanded = false
                                            notifyUpdate(sort = option)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.4f), thickness = 1.dp)

                    // Toggle: "SMB Server Search" with subtitle "Discover Windows/SMB shares"
                    SettingsToggleItem(
                        title = "SMB Server Search",
                        subtitle = "Discover Windows/SMB shares",
                        checked = smbServerSearch,
                        onCheckedChange = {
                            smbServerSearch = it
                            notifyUpdate(smb = it)
                        }
                    )

                    // Toggle: "Transcode FLAC to WAV" with explanatory subtitle
                    SettingsToggleItem(
                        title = "Transcode FLAC to WAV",
                        subtitle = "Converts FLAC audio to WAV for older DLNA renderers",
                        checked = transcodeFlacToWav,
                        onCheckedChange = {
                            transcodeFlacToWav = it
                            notifyUpdate(flac = it)
                        }
                    )

                    // Disabled-looking "Auto-Start Server" toggle with "Requires Premium"
                    SettingsToggleItem(
                        title = "Auto-Start Server",
                        subtitle = "Launch server automatically on device boot",
                        checked = false,
                        onCheckedChange = {
                            Toast.makeText(context, "Auto-Start Server requires Premium", Toast.LENGTH_SHORT).show()
                        },
                        enabled = false,
                        badgeText = "Requires Premium"
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.6f), thickness = 1.dp)

                // Bottom-right "Close" button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            notifyUpdate()
                            onDismiss()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = AccentViolet),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .testTag("btn_close_settings")
                    ) {
                        Text(
                            text = "Close",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ServerCardAccent)
            ) {
                Text("Got It")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💡", fontSize = 20.sp)
                Text("How to use LocalStream", color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Quick 3-step setup guide:",
                    color = AccentCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "1. Keep your phone connected to the same Wi-Fi as your TV, PC, or tablet.\n\n" +
                    "2. Make sure the server is started (status card shows 'Media server running').\n\n" +
                    "3. Open any web browser on your PC or Smart TV and navigate to the Server Access URL (e.g. http://192.0.0.4:8080).\n\n" +
                    "4. Browse videos, music, and photos instantly with zero internet usage!",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun PcAppDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Download link copied: https://localstream.app/desktop", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ServerCardAccent)
            ) {
                Text("Copy PC Download URL")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Dismiss", color = TextSecondary)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("💻", fontSize = 20.sp)
                Text("Get LocalStream PC", color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "LocalStream is available for Windows, macOS, and Linux.",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
                Text(
                    "Features:\n• Hardware-accelerated 4K HEVC / AV1 streaming\n• Drag & drop media sharing directly to phone\n• Auto-discovery of mobile server over LAN",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun TelegramDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Telegram channel link: @LocalStreamApp", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
            ) {
                Text("Copy Link (@LocalStreamApp)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔗", fontSize = 20.sp)
                Text("Join LocalStream Community", color = TextPrimary)
            }
        },
        text = {
            Text(
                "Join our Telegram channel for product updates, feature requests, beta APKs, and troubleshooting guides.",
                color = TextSecondary,
                fontSize = 14.sp
            )
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun FavoriteDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var rating by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    Toast.makeText(context, "Thank you for supporting LocalStream!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
            ) {
                Text("Submit Review", color = Color.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Star, contentDescription = null, tint = AccentAmber)
                Text("Rate LocalStream", color = TextPrimary)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enjoying local media streaming? Give us a star rating!", color = TextSecondary, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { rating = i }) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "$i stars",
                                tint = AccentAmber,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun LocalStreamErrorBoundary(
    onRetry: () -> Unit,
    content: @Composable () -> Unit
) {
    var hasError by remember { mutableStateOf(false) }
    if (hasError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, StopButtonPink.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(StopButtonPink.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = StopButtonPink,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        "Something went wrong",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = TextPrimary
                    )
                    Text(
                        "LocalStream could not display this section.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = {
                            hasError = false
                            onRetry()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet)
                    ) {
                        Text("Try Again", color = Color.White)
                    }
                }
            }
        }
    } else {
        content()
    }
}

@Composable
fun PwaInstallBanner(
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1B162E),
        border = androidx.compose.foundation.BorderStroke(1.dp, AccentViolet.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("banner_pwa_install")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentViolet.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AddHomeWork,
                        contentDescription = null,
                        tint = Color(0xFFB388FF),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Install LocalStream",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    Text(
                        "Add LocalStream to your home screen for a better experience.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = TextSecondary
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TextButton(
                    onClick = onDismiss,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Not now", color = TextMuted, fontSize = 12.sp)
                }
                Button(
                    onClick = onInstall,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Install", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun OfflineNotificationBanner(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isOffline,
        enter = androidx.compose.animation.expandVertically() + androidx.compose.animation.fadeIn(),
        exit = androidx.compose.animation.shrinkVertically() + androidx.compose.animation.fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = Color(0xFFE11D48),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "You are offline • Local streaming features still active",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ConfirmationActionDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
    isDestructive: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDestructive) StopButtonPink else AccentViolet
                )
            ) {
                Text(confirmText, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText, color = TextSecondary)
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isDestructive) Icons.Default.Warning else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isDestructive) StopButtonPink else AccentViolet
                )
                Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Text(message, color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("dialog_confirmation")
    )
}

@Composable
fun GlobalToastBanner(
    toast: com.example.model.ToastMessage?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toast != null,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { it / 2 },
        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { it / 2 },
        modifier = modifier
    ) {
        if (toast != null) {
            val (bgColor, icon, iconColor) = when (toast.type) {
                com.example.model.ToastType.SUCCESS -> Triple(Color(0xFF064E3B), Icons.Default.CheckCircle, StartButtonGreen)
                com.example.model.ToastType.ERROR -> Triple(Color(0xFF4C0519), Icons.Default.Error, StopButtonPink)
                com.example.model.ToastType.WARNING -> Triple(Color(0xFF451A03), Icons.Default.Warning, Color(0xFFFBBF24))
                com.example.model.ToastType.INFO -> Triple(Color(0xFF1E1B4B), Icons.Default.Info, Color(0xFFB388FF))
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = bgColor,
                border = androidx.compose.foundation.BorderStroke(1.dp, iconColor.copy(alpha = 0.5f)),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .clickable { onDismiss() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                    Text(toast.message, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun LoadingSkeletonView(
    modifier: Modifier = Modifier,
    itemsCount: Int = 4
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        repeat(itemsCount) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DarkSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkCardBorder)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(12.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkCardBorder)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkCardBorder.copy(alpha = 0.6f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConsistentEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(DarkSurfaceVariant)
                .border(1.dp, DarkCardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextPrimary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = TextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (actionText != null && onAction != null) {
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(actionText, color = Color.White, fontSize = 13.sp)
            }
        }
    }
}
