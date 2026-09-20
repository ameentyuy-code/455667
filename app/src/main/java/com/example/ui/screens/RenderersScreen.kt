package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ConfirmationActionDialog
import com.example.ui.components.DarkCard
import com.example.ui.theme.*

data class CastingTargetDevice(
    val id: String,
    val name: String,
    val type: String,
    val icon: ImageVector,
    val isLocal: Boolean = false
)

@Composable
fun RenderersScreen(
    onNavigateToPlayer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var phoneAsReceiverEnabled by remember { mutableStateOf(false) }
    var isDeviceDropdownExpanded by remember { mutableStateOf(false) }
    var showDisconnectConfirmation by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val availableDevices = remember {
        listOf(
            CastingTargetDevice(
                id = "local",
                name = "Play Locally (This Device)",
                type = "Internal Audio & Video Engine",
                icon = Icons.Default.PhoneAndroid,
                isLocal = true
            ),
            CastingTargetDevice(
                id = "lg_tv",
                name = "Living Room LG webOS OLED",
                type = "DLNA / UPnP DMR • 4K HDR",
                icon = Icons.Default.Tv
            ),
            CastingTargetDevice(
                id = "chromecast",
                name = "Bedroom Chromecast Ultra",
                type = "Google Cast Receiver",
                icon = Icons.Default.Cast
            ),
            CastingTargetDevice(
                id = "samsung_tv",
                name = "Samsung Crystal UHD 4K",
                type = "Samsung Smart View / DLNA",
                icon = Icons.Default.Tv
            ),
            CastingTargetDevice(
                id = "sonos",
                name = "Office Soundbar (Sonos UPnP)",
                type = "Hi-Fi Audio Renderer",
                icon = Icons.Default.Speaker
            )
        )
    }

    var selectedDevice by remember { mutableStateOf(availableDevices[0]) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Screen Title: "📺 Streaming Setup" with Rescan Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📺 Streaming Setup",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    letterSpacing = 0.3.sp
                ),
                color = TextPrimary
            )

            IconButton(
                onClick = {
                    if (!isScanning) {
                        isScanning = true
                        Toast.makeText(context, "Scanning local Wi-Fi subnet for renderers...", Toast.LENGTH_SHORT).show()
                        kotlinx.coroutines.GlobalScope.let {
                            // Using a non-blocking timeout simulation or state change
                        }
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .background(DarkSurfaceVariant, CircleShape)
                    .testTag("btn_rescan_renderers")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Rescan Wi-Fi Subnet",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Top Rounded Card/Section: "Phone as Receiver"
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_phone_as_receiver")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        phoneAsReceiverEnabled = !phoneAsReceiverEnabled
                        val msg = if (phoneAsReceiverEnabled) "Phone receiver mode enabled" else "Phone receiver mode disabled"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (phoneAsReceiverEnabled) AccentViolet.copy(alpha = 0.2f)
                                else DarkSurfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = if (phoneAsReceiverEnabled) AccentViolet else TextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "Phone as Receiver",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "Allow other to cast to this phone",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = phoneAsReceiverEnabled,
                    onCheckedChange = {
                        phoneAsReceiverEnabled = it
                        val msg = if (it) "Phone receiver mode enabled" else "Phone receiver mode disabled"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentViolet,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurfaceVariant,
                        uncheckedBorderColor = DarkCardBorder
                    ),
                    modifier = Modifier.testTag("switch_phone_as_receiver")
                )
            }
        }

        // Large Rounded Casting Panel
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("panel_casting_setup")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Dark Rounded Device Selector at the Top
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        onClick = { isDeviceDropdownExpanded = !isDeviceDropdownExpanded },
                        shape = RoundedCornerShape(14.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDeviceDropdownExpanded) AccentViolet else DarkCardBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("selector_device_dropdown")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = selectedDevice.icon,
                                    contentDescription = null,
                                    tint = AccentViolet,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = selectedDevice.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    ),
                                    color = TextPrimary,
                                    maxLines = 1
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Device",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Dropdown Menu with Available Devices
                    DropdownMenu(
                        expanded = isDeviceDropdownExpanded,
                        onDismissRequest = { isDeviceDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                    ) {
                        availableDevices.forEach { device ->
                            val isCurrent = device.id == selectedDevice.id
                            DropdownMenuItem(
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = device.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isCurrent) AccentViolet else TextPrimary
                                            )
                                        )
                                        Text(
                                            text = device.type,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = TextMuted
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = device.icon,
                                        contentDescription = null,
                                        tint = if (isCurrent) AccentViolet else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (isCurrent) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AccentViolet,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                onClick = {
                                    selectedDevice = device
                                    isDeviceDropdownExpanded = false
                                    Toast.makeText(context, "Target set to ${device.name}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                // Center Content: Large Cast/Display Icon + Prompt Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 14.dp)
                ) {
                    // Large Cast / Display Icon in Center with Ambient Glow
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        AccentViolet.copy(alpha = 0.22f),
                                        Color(0xFF2E1065).copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(AccentViolet.copy(alpha = 0.6f), Color(0xFF3B2455))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedDevice.isLocal) Icons.Default.Cast else Icons.Default.CastConnected,
                            contentDescription = "Casting Display",
                            tint = Color(0xFFB388FF),
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    // Large Text: "Select a device to start casting"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (selectedDevice.isLocal) "Select a device to start casting" else "Ready to cast to ${selectedDevice.name}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (selectedDevice.isLocal)
                                "Tap the device selector above to choose a TV, speaker or Chromecast"
                            else
                                "Browse your library below to start streaming media to this target",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            ),
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        }

        // Local Subnet Discovery Notice Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF151424),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = Color(0xFFB388FF),
                    modifier = Modifier.size(22.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Local Wi-Fi Subnet Discovery",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                        color = TextPrimary
                    )
                    Text(
                        text = "Scans for DLNA, UPnP DMR & Google Cast receivers on your Wi-Fi router (192.168.1.x). Both devices must share the same network.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                        color = TextSecondary
                    )
                }
            }
        }

        // Large Lavender/Purple Rounded Button: "Browse Media to Cast"
        Button(
            onClick = onNavigateToPlayer,
            colors = ButtonDefaults.buttonColors(
                containerColor = ServerCardAccent,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("btn_browse_media_to_cast")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Browse Media to Cast",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }
        }

        if (!selectedDevice.isLocal) {
            OutlinedButton(
                onClick = { showDisconnectConfirmation = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = StopButtonPink
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, StopButtonPink.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_disconnect_renderer")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CastConnected,
                        contentDescription = null,
                        tint = StopButtonPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Disconnect from ${selectedDevice.name}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }

    if (showDisconnectConfirmation) {
        ConfirmationActionDialog(
            title = "Disconnect Renderer?",
            message = "Disconnecting from ${selectedDevice.name} will redirect active stream playback back to this local device.",
            confirmText = "Disconnect",
            isDestructive = true,
            onConfirm = {
                val oldName = selectedDevice.name
                selectedDevice = availableDevices[0]
                Toast.makeText(context, "Disconnected from $oldName", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showDisconnectConfirmation = false }
        )
    }
}
