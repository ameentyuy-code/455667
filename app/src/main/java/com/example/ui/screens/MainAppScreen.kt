package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.rememberNetworkStatus
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

enum class LocalStreamTab(
    val labelRes: Int,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val tag: String
) {
    MY_SERVER(R.string.nav_my_server, Icons.Filled.Dns, Icons.Outlined.Dns, "tab_my_server"),
    FILE_MANAGER(R.string.nav_file_manager, Icons.Filled.Folder, Icons.Outlined.Folder, "tab_file_manager"),
    PLAYER(R.string.nav_player, Icons.Filled.PlayCircle, Icons.Outlined.PlayCircle, "tab_player"),
    RENDERERS(R.string.nav_renderers, Icons.Filled.Cast, Icons.Outlined.Cast, "tab_renderers"),
    ADB(R.string.nav_adb, Icons.Filled.Adb, Icons.Outlined.Adb, "tab_adb")
}

@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val preferences = remember { com.example.data.LocalStreamPreferences(context) }
    val networkStatus by rememberNetworkStatus()
    val isOffline = networkStatus == NetworkStatus.OFFLINE

    var currentTab by remember { mutableStateOf(LocalStreamTab.MY_SERVER) }
    var serverConfig by remember { mutableStateOf(preferences.loadServerConfig()) }
    var activeMediaItem by remember { mutableStateOf<MediaItem?>(null) }
    var showPwaBanner by remember { mutableStateOf(!preferences.isPwaInstallDismissed()) }
    var showStopServerConfirmation by remember { mutableStateOf(false) }
    var activeToast by remember { mutableStateOf<ToastMessage?>(null) }

    fun showToast(message: String, type: ToastType = ToastType.INFO) {
        activeToast = ToastMessage(message = message, type = type)
    }

    fun updateConfig(newConfig: ServerConfig) {
        serverConfig = newConfig
        preferences.saveServerConfig(newConfig)
    }

    // Dialog visibility states
    var showNetworkDialog by remember { mutableStateOf(false) }
    var showFavoriteDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var showPcAppDialog by remember { mutableStateOf(false) }
    var showTelegramDialog by remember { mutableStateOf(false) }

    LocalStreamErrorBoundary(onRetry = { currentTab = LocalStreamTab.MY_SERVER }) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            containerColor = DarkBg,
            topBar = {
                LocalStreamTopAppBar(
                    onNetworkClick = { showNetworkDialog = true },
                    onFavoriteClick = { showFavoriteDialog = true },
                    onSettingsClick = { showSettingsDialog = true },
                    isServerOnline = serverConfig.isRunning
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("persistent_bottom_navigation")
                ) {
                    LocalStreamTab.values().forEach { tab ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (selected) tab.filledIcon else tab.outlinedIcon,
                                    contentDescription = stringResource(id = tab.labelRes),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = tab.labelRes),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                unselectedIconColor = TextSecondary,
                                selectedTextColor = TextPrimary,
                                unselectedTextColor = TextMuted,
                                indicatorColor = ServerCardAccent
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Offline notification banner
                    OfflineNotificationBanner(isOffline = isOffline)

                    // PWA install banner if eligible and not dismissed
                    if (showPwaBanner && currentTab == LocalStreamTab.MY_SERVER) {
                        PwaInstallBanner(
                            onInstall = {
                                showPwaBanner = false
                                preferences.setPwaInstallDismissed(true)
                                showToast("LocalStream added to home screen", ToastType.SUCCESS)
                            },
                            onDismiss = {
                                showPwaBanner = false
                                preferences.setPwaInstallDismissed(true)
                                showToast("Install prompt dismissed", ToastType.INFO)
                            },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Crossfade(targetState = currentTab, label = "ScreenTransition") { tab ->
                            when (tab) {
                                LocalStreamTab.MY_SERVER -> MyServerScreen(
                                    serverConfig = serverConfig,
                                    onToggleServer = {
                                        if (serverConfig.isRunning) {
                                            showStopServerConfirmation = true
                                        } else {
                                            val updated = serverConfig.copy(isRunning = true)
                                            updateConfig(updated)
                                            showToast("LocalStream server started on :${updated.port}", ToastType.SUCCESS)
                                        }
                                    },
                                    onOpenGuide = { showGuideDialog = true },
                                    onOpenPcApp = { showPcAppDialog = true },
                                    onOpenTelegram = { showTelegramDialog = true }
                                )
                                LocalStreamTab.FILE_MANAGER -> FileManagerScreen(
                                    onPlayMedia = { item ->
                                        activeMediaItem = item
                                        currentTab = LocalStreamTab.PLAYER
                                        showToast("Playing ${item.title}", ToastType.INFO)
                                    }
                                )
                                LocalStreamTab.PLAYER -> PlayerScreen(
                                    currentMedia = activeMediaItem,
                                    onCastToRenderer = {
                                        currentTab = LocalStreamTab.RENDERERS
                                        showToast("Select a renderer to cast", ToastType.INFO)
                                    }
                                )
                                LocalStreamTab.RENDERERS -> RenderersScreen(
                                    onNavigateToPlayer = {
                                        currentTab = LocalStreamTab.PLAYER
                                    }
                                )
                                LocalStreamTab.ADB -> AdbScreen(serverConfig = serverConfig)
                            }
                        }
                    }
                }

                // Global Toast banner overlay
                GlobalToastBanner(
                    toast = activeToast,
                    onDismiss = { activeToast = null },
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                )
            }
        }
    }

    // Stop Server Confirmation Dialog
    if (showStopServerConfirmation) {
        ConfirmationActionDialog(
            title = "Stop Media Server?",
            message = "Stopping the server will terminate active DLNA streams, UPnP broadcasts, and web player connections. Are you sure you want to stop?",
            confirmText = "Stop Server",
            isDestructive = true,
            onConfirm = {
                val updated = serverConfig.copy(isRunning = false)
                updateConfig(updated)
                showToast("LocalStream server stopped", ToastType.WARNING)
            },
            onDismiss = { showStopServerConfirmation = false }
        )
    }

    // Interactive Dialogs
    if (showNetworkDialog) {
        NetworkInfoDialog(
            serverConfig = serverConfig,
            onDismiss = { showNetworkDialog = false }
        )
    }

    if (showFavoriteDialog) {
        FavoriteDialog(
            onDismiss = { showFavoriteDialog = false }
        )
    }

    if (showSettingsDialog) {
        SettingsDialog(
            serverConfig = serverConfig,
            onUpdateConfig = { updated ->
                updateConfig(updated)
                Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
            },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showGuideDialog) {
        VideoGuideDialog(
            onDismiss = { showGuideDialog = false }
        )
    }

    if (showPcAppDialog) {
        PcAppDialog(
            onDismiss = { showPcAppDialog = false }
        )
    }

    if (showTelegramDialog) {
        TelegramDialog(
            onDismiss = { showTelegramDialog = false }
        )
    }
}
