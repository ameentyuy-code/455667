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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LocalStreamPreferences
import com.example.model.MediaItem
import com.example.model.MediaType
import com.example.services.VlcStreamHelper
import com.example.ui.components.VlcStreamDialog
import com.example.ui.theme.*

enum class FileSortMode(val label: String) {
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),
    SIZE_DESC("Size (Largest)"),
    SIZE_ASC("Size (Smallest)"),
    TYPE("Type")
}

data class LocalFileEntry(
    val id: String,
    val name: String,
    val isDirectory: Boolean,
    val parentFolderId: String?,
    val sizeBytes: Long,
    val sizeFormatted: String,
    val mediaType: MediaType? = null,
    val duration: String = "",
    val resolutionOrBitrate: String = "",
    val extension: String = "",
    val itemCount: Int = 0,
    val dateModified: String = "Today"
)

@Composable
fun FileManagerScreen(
    onPlayMedia: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { LocalStreamPreferences(context) }
    val serverConfig = remember { prefs.loadServerConfig() }
    var selectedVlcFile by remember { mutableStateOf<LocalFileEntry?>(null) }

    // Current Directory Path Navigation
    var currentFolderId by remember { mutableStateOf<String?>(null) } // null = root
    var currentFolderPath by remember { mutableStateOf(listOf("LocalStream")) }

    // Search and Sort State
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSortMode by remember { mutableStateOf(FileSortMode.NAME_ASC) }
    var isSortDropdownExpanded by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(false) }

    // File Hierarchy Definition
    val allFiles = remember {
        listOf(
            // --- ROOT FOLDERS ---
            LocalFileEntry(
                id = "folder_music",
                name = "Music",
                isDirectory = true,
                parentFolderId = null,
                sizeBytes = 880_000_000L,
                sizeFormatted = "880 MB",
                itemCount = 8
            ),
            LocalFileEntry(
                id = "folder_movies",
                name = "Movies & Shows",
                isDirectory = true,
                parentFolderId = null,
                sizeBytes = 32_000_000_000L,
                sizeFormatted = "32.0 GB",
                itemCount = 5
            ),
            LocalFileEntry(
                id = "folder_recordings",
                name = "Recordings",
                isDirectory = true,
                parentFolderId = null,
                sizeBytes = 142_000_000L,
                sizeFormatted = "142 MB",
                itemCount = 3
            ),
            LocalFileEntry(
                id = "folder_pictures",
                name = "Pictures & Artwork",
                isDirectory = true,
                parentFolderId = null,
                sizeBytes = 36_800_000L,
                sizeFormatted = "36.8 MB",
                itemCount = 4
            ),
            LocalFileEntry(
                id = "folder_downloads",
                name = "Downloads",
                isDirectory = true,
                parentFolderId = null,
                sizeBytes = 1_850_000_000L,
                sizeFormatted = "1.85 GB",
                itemCount = 3
            ),

            // --- ROOT FILES ---
            LocalFileEntry(
                id = "rf_1",
                name = "Dune_Part_Two_4K_HDR.mkv",
                isDirectory = false,
                parentFolderId = null,
                sizeBytes = 8_400_000_000L,
                sizeFormatted = "8.4 GB",
                mediaType = MediaType.VIDEO,
                duration = "02:46:12",
                resolutionOrBitrate = "3840x2160 • HEVC 10-bit",
                extension = "MKV"
            ),
            LocalFileEntry(
                id = "rf_2",
                name = "After_Hours_Deluxe.flac",
                isDirectory = false,
                parentFolderId = null,
                sizeBytes = 62_000_000L,
                sizeFormatted = "62 MB",
                mediaType = MediaType.AUDIO,
                duration = "06:01",
                resolutionOrBitrate = "Lossless • 24-bit/96kHz",
                extension = "FLAC"
            ),
            LocalFileEntry(
                id = "rf_3",
                name = "LocalStream_Quick_Guide.pdf",
                isDirectory = false,
                parentFolderId = null,
                sizeBytes = 1_200_000L,
                sizeFormatted = "1.2 MB",
                mediaType = null,
                extension = "PDF"
            ),

            // --- MUSIC FOLDER ITEMS ---
            LocalFileEntry(
                id = "m_1",
                name = "ŞULTANRŞ - Going Dark (Slowed).mp3",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 6_800_000L,
                sizeFormatted = "6.8 MB",
                mediaType = MediaType.AUDIO,
                duration = "02:48",
                resolutionOrBitrate = "MP3 • 320 kbps",
                extension = "MP3"
            ),
            LocalFileEntry(
                id = "m_2",
                name = "AETERNUM - LXST CXNTURY.flac",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 28_400_000L,
                sizeFormatted = "28.4 MB",
                mediaType = MediaType.AUDIO,
                duration = "03:15",
                resolutionOrBitrate = "FLAC • Lossless",
                extension = "FLAC"
            ),
            LocalFileEntry(
                id = "m_3",
                name = "After Hours - The Weeknd.flac",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 62_100_000L,
                sizeFormatted = "62.1 MB",
                mediaType = MediaType.AUDIO,
                duration = "06:01",
                resolutionOrBitrate = "FLAC • 24-bit",
                extension = "FLAC"
            ),
            LocalFileEntry(
                id = "m_4",
                name = "All Eyez On Me - 2Pac.mp3",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 11_800_000L,
                sizeFormatted = "11.8 MB",
                mediaType = MediaType.AUDIO,
                duration = "05:08",
                resolutionOrBitrate = "MP3 • 320 kbps",
                extension = "MP3"
            ),
            LocalFileEntry(
                id = "m_5",
                name = "Alors on Phonk.flac",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 24_500_000L,
                sizeFormatted = "24.5 MB",
                mediaType = MediaType.AUDIO,
                duration = "02:30",
                resolutionOrBitrate = "FLAC • Lossless",
                extension = "FLAC"
            ),
            LocalFileEntry(
                id = "m_6",
                name = "ARABIC - Kaito Shoma.mp3",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 5_400_000L,
                sizeFormatted = "5.4 MB",
                mediaType = MediaType.AUDIO,
                duration = "02:22",
                resolutionOrBitrate = "MP3 • 320 kbps",
                extension = "MP3"
            ),
            LocalFileEntry(
                id = "m_7",
                name = "AVANGARD - Super Slowed.wav",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 42_000_000L,
                sizeFormatted = "42.0 MB",
                mediaType = MediaType.AUDIO,
                duration = "02:54",
                resolutionOrBitrate = "WAV • 24-bit",
                extension = "WAV"
            ),
            LocalFileEntry(
                id = "m_8",
                name = "Metamorphosis - INTERWORLD.flac",
                isDirectory = false,
                parentFolderId = "folder_music",
                sizeBytes = 26_200_000L,
                sizeFormatted = "26.2 MB",
                mediaType = MediaType.AUDIO,
                duration = "02:22",
                resolutionOrBitrate = "FLAC • Lossless",
                extension = "FLAC"
            ),

            // --- MOVIES FOLDER ITEMS ---
            LocalFileEntry(
                id = "mov_1",
                name = "Dune_Part_Two_4K_HDR.mkv",
                isDirectory = false,
                parentFolderId = "folder_movies",
                sizeBytes = 8_400_000_000L,
                sizeFormatted = "8.4 GB",
                mediaType = MediaType.VIDEO,
                duration = "02:46:12",
                resolutionOrBitrate = "3840x2160 • HEVC 10-bit",
                extension = "MKV"
            ),
            LocalFileEntry(
                id = "mov_2",
                name = "Oppenheimer_Dolby_Atmos.mp4",
                isDirectory = false,
                parentFolderId = "folder_movies",
                sizeBytes = 6_100_000_000L,
                sizeFormatted = "6.1 GB",
                mediaType = MediaType.VIDEO,
                duration = "03:00:21",
                resolutionOrBitrate = "3840x2160 • HDR10",
                extension = "MP4"
            ),
            LocalFileEntry(
                id = "mov_3",
                name = "Interstellar_IMAX_Remux.mp4",
                isDirectory = false,
                parentFolderId = "folder_movies",
                sizeBytes = 14_200_000_000L,
                sizeFormatted = "14.2 GB",
                mediaType = MediaType.VIDEO,
                duration = "02:49:04",
                resolutionOrBitrate = "1920x1080 • Dolby 5.1",
                extension = "MP4"
            ),
            LocalFileEntry(
                id = "mov_4",
                name = "Cyberpunk_Edgerunners_Ep01.mkv",
                isDirectory = false,
                parentFolderId = "folder_movies",
                sizeBytes = 1_200_000_000L,
                sizeFormatted = "1.2 GB",
                mediaType = MediaType.VIDEO,
                duration = "00:24:18",
                resolutionOrBitrate = "1920x1080 • 60 FPS",
                extension = "MKV"
            ),
            LocalFileEntry(
                id = "mov_5",
                name = "Blade_Runner_2049.mp4",
                isDirectory = false,
                parentFolderId = "folder_movies",
                sizeBytes = 2_100_000_000L,
                sizeFormatted = "2.1 GB",
                mediaType = MediaType.VIDEO,
                duration = "00:14:40",
                resolutionOrBitrate = "3840x2160 • 60 FPS",
                extension = "MP4"
            ),

            // --- RECORDINGS FOLDER ITEMS ---
            LocalFileEntry(
                id = "rec_1",
                name = "Voice_Memo_Studio_Session_01.m4a",
                isDirectory = false,
                parentFolderId = "folder_recordings",
                sizeBytes = 48_000_000L,
                sizeFormatted = "48 MB",
                mediaType = MediaType.AUDIO,
                duration = "00:32:10",
                resolutionOrBitrate = "AAC • 256 kbps",
                extension = "M4A"
            ),
            LocalFileEntry(
                id = "rec_2",
                name = "Field_Recording_Rain_Thunder.wav",
                isDirectory = false,
                parentFolderId = "folder_recordings",
                sizeBytes = 72_000_000L,
                sizeFormatted = "72 MB",
                mediaType = MediaType.AUDIO,
                duration = "00:18:40",
                resolutionOrBitrate = "WAV • 24-bit 96kHz",
                extension = "WAV"
            ),
            LocalFileEntry(
                id = "rec_3",
                name = "Podcast_Interview_Take2.mp3",
                isDirectory = false,
                parentFolderId = "folder_recordings",
                sizeBytes = 22_000_000L,
                sizeFormatted = "22 MB",
                mediaType = MediaType.AUDIO,
                duration = "00:24:15",
                resolutionOrBitrate = "MP3 • 192 kbps",
                extension = "MP3"
            ),

            // --- PICTURES FOLDER ITEMS ---
            LocalFileEntry(
                id = "pic_1",
                name = "Cyberpunk_City_Wallpapers.png",
                isDirectory = false,
                parentFolderId = "folder_pictures",
                sizeBytes = 4_800_000L,
                sizeFormatted = "4.8 MB",
                mediaType = MediaType.PHOTO,
                resolutionOrBitrate = "3840x2160",
                extension = "PNG"
            ),
            LocalFileEntry(
                id = "pic_2",
                name = "Concert_Stage_Live_HDR.jpg",
                isDirectory = false,
                parentFolderId = "folder_pictures",
                sizeBytes = 6_200_000L,
                sizeFormatted = "6.2 MB",
                mediaType = MediaType.PHOTO,
                resolutionOrBitrate = "4032x3024",
                extension = "JPG"
            ),
            LocalFileEntry(
                id = "pic_3",
                name = "Album_Art_Collection_HD.jpg",
                isDirectory = false,
                parentFolderId = "folder_pictures",
                sizeBytes = 3_100_000L,
                sizeFormatted = "3.1 MB",
                mediaType = MediaType.PHOTO,
                resolutionOrBitrate = "3000x3000",
                extension = "JPG"
            ),
            LocalFileEntry(
                id = "pic_4",
                name = "Minimalist_Neon_Poster.webp",
                isDirectory = false,
                parentFolderId = "folder_pictures",
                sizeBytes = 1_900_000L,
                sizeFormatted = "1.9 MB",
                mediaType = MediaType.PHOTO,
                resolutionOrBitrate = "2560x1440",
                extension = "WEBP"
            ),

            // --- DOWNLOADS FOLDER ITEMS ---
            LocalFileEntry(
                id = "dl_1",
                name = "Podcast_Episode_104_Offline.mp3",
                isDirectory = false,
                parentFolderId = "folder_downloads",
                sizeBytes = 84_000_000L,
                sizeFormatted = "84 MB",
                mediaType = MediaType.AUDIO,
                duration = "00:54:12",
                resolutionOrBitrate = "MP3 • 320 kbps",
                extension = "MP3"
            ),
            LocalFileEntry(
                id = "dl_2",
                name = "OpenSource_Concert_Video_1080p.mp4",
                isDirectory = false,
                parentFolderId = "folder_downloads",
                sizeBytes = 1_650_000_000L,
                sizeFormatted = "1.65 GB",
                mediaType = MediaType.VIDEO,
                duration = "01:12:00",
                resolutionOrBitrate = "1920x1080 • H.264",
                extension = "MP4"
            ),
            LocalFileEntry(
                id = "dl_3",
                name = "LocalStream_Config_Backup.json",
                isDirectory = false,
                parentFolderId = "folder_downloads",
                sizeBytes = 120_000L,
                sizeFormatted = "120 KB",
                mediaType = null,
                extension = "JSON"
            )
        )
    }

    // Filter and Sort current files
    val displayedFiles = remember(currentFolderId, searchQuery, selectedSortMode, allFiles) {
        val baseList = if (searchQuery.isNotBlank()) {
            allFiles.filter { it.name.contains(searchQuery, ignoreCase = true) }
        } else {
            allFiles.filter { it.parentFolderId == currentFolderId }
        }

        // Sort: Folders always on top unless sorting by size/name explicitly
        baseList.sortedWith { a, b ->
            if (a.isDirectory && !b.isDirectory) -1
            else if (!a.isDirectory && b.isDirectory) 1
            else when (selectedSortMode) {
                FileSortMode.NAME_ASC -> a.name.compareTo(b.name, ignoreCase = true)
                FileSortMode.NAME_DESC -> b.name.compareTo(a.name, ignoreCase = true)
                FileSortMode.SIZE_DESC -> b.sizeBytes.compareTo(a.sizeBytes)
                FileSortMode.SIZE_ASC -> a.sizeBytes.compareTo(b.sizeBytes)
                FileSortMode.TYPE -> (a.extension).compareTo(b.extension, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Top Header: Title & Storage Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "📁 File Manager",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    ),
                    color = TextPrimary
                )
                Text(
                    text = "Local Storage: 38.6 GB / 128 GB",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Search toggle button
                IconButton(
                    onClick = {
                        isSearchActive = !isSearchActive
                        if (!isSearchActive) searchQuery = ""
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (isSearchActive) AccentViolet.copy(alpha = 0.2f) else DarkSurfaceVariant, CircleShape)
                        .testTag("btn_file_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Files",
                        tint = if (isSearchActive) Color(0xFFB388FF) else TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Sort dropdown button
                Box {
                    IconButton(
                        onClick = { isSortDropdownExpanded = !isSortDropdownExpanded },
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkSurfaceVariant, CircleShape)
                            .testTag("btn_file_sort")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort Options",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isSortDropdownExpanded,
                        onDismissRequest = { isSortDropdownExpanded = false },
                        modifier = Modifier
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        FileSortMode.values().forEach { sortMode ->
                            val isSelected = sortMode == selectedSortMode
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sortMode.label,
                                        color = if (isSelected) AccentViolet else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = AccentViolet, modifier = Modifier.size(16.dp))
                                    }
                                },
                                onClick = {
                                    selectedSortMode = sortMode
                                    isSortDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Grid / List View toggle
                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSurfaceVariant, CircleShape)
                        .testTag("btn_file_grid_list_toggle")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                        contentDescription = "Toggle View",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Search Bar (Animated Expand)
        AnimatedVisibility(
            visible = isSearchActive,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filter files by name or extension...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = AccentViolet, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB388FF),
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("input_file_search")
            )
        }

        // Breadcrumb & Path Bar with Back Navigation
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button (Enabled when not in root)
                if (currentFolderId != null) {
                    IconButton(
                        onClick = {
                            currentFolderId = null
                            currentFolderPath = listOf("LocalStream")
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .testTag("btn_folder_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to root",
                            tint = Color(0xFFB388FF),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Root Storage",
                        tint = AccentViolet,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Clickable Breadcrumbs
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentFolderPath.forEachIndexed { index, segment ->
                        Text(
                            text = segment,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (index == currentFolderPath.lastIndex) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = if (index == currentFolderPath.lastIndex) Color(0xFFB388FF) else TextSecondary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    if (index == 0) {
                                        currentFolderId = null
                                        currentFolderPath = listOf("LocalStream")
                                    }
                                }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        if (index < currentFolderPath.lastIndex) {
                            Text(
                                text = " / ",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Item count indicator
                Text(
                    text = "${displayedFiles.size} items",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
        }

        // Main File List / Grid
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            if (displayedFiles.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = Color(0xFFB388FF),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matching media found" else "No media found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (searchQuery.isNotBlank())
                            "No files match \"$searchQuery\". Try checking the filename or extension."
                        else
                            "This directory has no media items. Add audio, video or photos to share them over your local network.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 18.sp),
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            } else if (isGridView) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedFiles, key = { it.id }) { item ->
                        FileGridItem(
                            item = item,
                            onClick = {
                                if (item.isDirectory) {
                                    currentFolderId = item.id
                                    currentFolderPath = currentFolderPath + item.name
                                } else if (item.mediaType != null) {
                                    onPlayMedia(
                                        MediaItem(
                                            id = item.id,
                                            title = item.name,
                                            type = item.mediaType,
                                            size = item.sizeFormatted,
                                            duration = item.duration.ifEmpty { "03:45" },
                                            resolutionOrBitrate = item.resolutionOrBitrate,
                                            format = item.extension
                                        )
                                    )
                                } else {
                                    Toast.makeText(context, "${item.name} opened with default viewer", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onPlayMedia = onPlayMedia
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("file_manager_list")
                ) {
                    items(displayedFiles, key = { it.id }) { item ->
                        FileListItem(
                            item = item,
                            onClick = {
                                if (item.isDirectory) {
                                    currentFolderId = item.id
                                    currentFolderPath = currentFolderPath + item.name
                                } else if (item.mediaType != null) {
                                    onPlayMedia(
                                        MediaItem(
                                            id = item.id,
                                            title = item.name,
                                            type = item.mediaType,
                                            size = item.sizeFormatted,
                                            duration = item.duration.ifEmpty { "03:45" },
                                            resolutionOrBitrate = item.resolutionOrBitrate,
                                            format = item.extension
                                        )
                                    )
                                } else {
                                    Toast.makeText(context, "${item.name} opened with system viewer", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onPlayMedia = onPlayMedia,
                            serverAddress = serverConfig.baseUrl,
                            onOpenVlc = { selectedVlcFile = it }
                        )
                    }
                }
            }
        }
    }

    if (selectedVlcFile != null) {
        VlcStreamDialog(
            mediaTitle = selectedVlcFile!!.name,
            mediaPath = selectedVlcFile!!.name,
            mediaType = selectedVlcFile!!.mediaType,
            onDismiss = { selectedVlcFile = null }
        )
    }
}

// -----------------------------------------------------------------------------
// Component: File List Item
// -----------------------------------------------------------------------------
@Composable
private fun FileListItem(
    item: LocalFileEntry,
    onClick: () -> Unit,
    onPlayMedia: (MediaItem) -> Unit,
    serverAddress: String,
    onOpenVlc: (LocalFileEntry) -> Unit
) {
    val context = LocalContext.current
    var isMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("file_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Distinct Icon per Type (Folder, Audio, Video, Image, Document)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getItemIconBackground(item)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getItemIcon(item),
                    contentDescription = null,
                    tint = getItemIconTint(item),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // File Name & Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (item.isDirectory) "${item.itemCount} items" else item.sizeFormatted,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                    if (item.duration.isNotBlank()) {
                        Text("•", color = TextMuted, fontSize = 10.sp)
                        Text(
                            text = item.duration,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = TextMuted
                        )
                    }
                    if (item.extension.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkSurfaceVariant
                        ) {
                            Text(
                                text = item.extension,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = getItemIconTint(item),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }

            // Three-Dot Overflow Menu
            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "File Options",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                ) {
                    if (!item.isDirectory) {
                        val isVlcSupported = VlcStreamHelper.isVlcSupported(item.extension)

                        if (item.mediaType != null) {
                            DropdownMenuItem(
                                text = { Text("Play in Player", color = TextPrimary) },
                                leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AccentViolet) },
                                onClick = {
                                    isMenuExpanded = false
                                    onPlayMedia(
                                        MediaItem(
                                            id = item.id,
                                            title = item.name,
                                            type = item.mediaType,
                                            size = item.sizeFormatted,
                                            duration = item.duration.ifEmpty { "03:45" },
                                            resolutionOrBitrate = item.resolutionOrBitrate,
                                            format = item.extension
                                        )
                                    )
                                }
                            )
                        }

                        if (isVlcSupported) {
                            DropdownMenuItem(
                                text = { Text("Open in VLC", color = TextPrimary, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.PlayCircle, contentDescription = "Open in VLC", tint = Color(0xFFFF7700)) },
                                onClick = {
                                    isMenuExpanded = false
                                    onOpenVlc(item)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Copy VLC Stream URL", color = TextPrimary) },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = "Copy VLC Stream URL", tint = Color(0xFFB388FF)) },
                                onClick = {
                                    isMenuExpanded = false
                                    val res = VlcStreamHelper.generateVlcStreamUrl(serverAddress, item.name)
                                    val streamUrl = res.getOrDefault("")
                                    if (streamUrl.isNotBlank()) {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("VLC Stream URL", streamUrl))
                                        Toast.makeText(context, "Stream URL copied.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Open in VLC (Unsupported format)", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.PlayCircle, contentDescription = null, tint = TextMuted) },
                                enabled = false,
                                onClick = {}
                            )
                        }
                    }
                    DropdownMenuItem(
                        text = { Text("Details & Properties", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary) },
                        onClick = {
                            isMenuExpanded = false
                            val details = "${item.name}\nSize: ${item.sizeFormatted}\nType: ${item.extension.ifEmpty { "Folder" }}\nModified: ${item.dateModified}"
                            Toast.makeText(context, details, Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: File Grid Item
// -----------------------------------------------------------------------------
@Composable
private fun FileGridItem(
    item: LocalFileEntry,
    onClick: () -> Unit,
    onPlayMedia: (MediaItem) -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(getItemIconBackground(item)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getItemIcon(item),
                    contentDescription = null,
                    tint = getItemIconTint(item),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = if (item.isDirectory) "${item.itemCount} items" else item.sizeFormatted,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary
            )
        }
    }
}

// Helper styling functions for file icons
private fun getItemIcon(item: LocalFileEntry): ImageVector {
    return when {
        item.isDirectory -> Icons.Default.Folder
        item.mediaType == MediaType.AUDIO -> Icons.Default.MusicNote
        item.mediaType == MediaType.VIDEO -> Icons.Default.Movie
        item.mediaType == MediaType.PHOTO -> Icons.Default.Image
        else -> Icons.Default.InsertDriveFile
    }
}

private fun getItemIconTint(item: LocalFileEntry): Color {
    return when {
        item.isDirectory -> Color(0xFFFBBF24) // Warm amber folder
        item.mediaType == MediaType.AUDIO -> Color(0xFFB388FF) // Purple / lavender audio
        item.mediaType == MediaType.VIDEO -> Color(0xFF38BDF8) // Cyan / sky video
        item.mediaType == MediaType.PHOTO -> Color(0xFF34D399) // Emerald photo
        else -> Color(0xFF94A3B8)
    }
}

private fun getItemIconBackground(item: LocalFileEntry): Color {
    return when {
        item.isDirectory -> Color(0xFF2E2210)
        item.mediaType == MediaType.AUDIO -> Color(0xFF26193E)
        item.mediaType == MediaType.VIDEO -> Color(0xFF0C273B)
        item.mediaType == MediaType.PHOTO -> Color(0xFF0F3223)
        else -> DarkSurfaceVariant
    }
}
