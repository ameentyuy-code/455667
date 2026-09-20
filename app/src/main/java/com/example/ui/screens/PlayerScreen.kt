package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.model.MediaItem
import com.example.ui.components.DarkCard
import com.example.ui.theme.*

enum class PlayerMediaTab(val label: String) {
    AUDIO("Audio"),
    VIDEO("Video"),
    IMAGE("Image")
}

enum class AudioFilter(val label: String) {
    TRACKS("Tracks"),
    ALBUMS("Albums"),
    ARTISTS("Artists")
}

data class AudioTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val format: String,
    val gradient: List<Color>
)

data class AlbumItem(
    val title: String,
    val artist: String,
    val trackCount: Int,
    val gradient: List<Color>
)

data class ArtistItem(
    val name: String,
    val trackCount: Int,
    val gradient: List<Color>
)

data class VideoItem(
    val title: String,
    val duration: String,
    val resolution: String,
    val size: String
)

data class PhotoItem(
    val title: String,
    val dimensions: String,
    val size: String,
    val gradient: List<Color>
)

@Composable
fun PlayerScreen(
    currentMedia: MediaItem? = null,
    onCastToRenderer: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedMediaTab by remember { mutableStateOf(PlayerMediaTab.AUDIO) }
    var selectedAudioFilter by remember { mutableStateOf(AudioFilter.TRACKS) }
    var isGridView by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val allTracks = remember {
        listOf(
            AudioTrack(
                id = "1",
                title = "ŞULTANRŞ - Going Dark (Slow...)",
                artist = "ŞULTANRŞ",
                album = "Going Dark",
                duration = "02:48",
                format = "MP3 • 320 kbps",
                gradient = listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B))
            ),
            AudioTrack(
                id = "2",
                title = "AETERNUM",
                artist = "LXST CXNTURY",
                album = "Aeternum EP",
                duration = "03:15",
                format = "FLAC • Lossless",
                gradient = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
            ),
            AudioTrack(
                id = "3",
                title = "ARABIC",
                artist = "Kaito Shoma",
                album = "Arabic Dreams",
                duration = "02:22",
                format = "MP3 • 320 kbps",
                gradient = listOf(Color(0xFF78350F), Color(0xFF451A03))
            ),
            AudioTrack(
                id = "4",
                title = "AVANGARD - Super Slowed + ...",
                artist = "DVRST & Igor",
                album = "Avangard",
                duration = "02:54",
                format = "WAV • 24-bit",
                gradient = listOf(Color(0xFF831843), Color(0xFF500724))
            ),
            AudioTrack(
                id = "5",
                title = "After Hours",
                artist = "The Weeknd",
                album = "After Hours",
                duration = "06:01",
                format = "FLAC • Lossless",
                gradient = listOf(Color(0xFF7F1D1D), Color(0xFF450A0A))
            ),
            AudioTrack(
                id = "6",
                title = "All Eyez On Me (Groove Edit)",
                artist = "2Pac ft. Big Syke",
                album = "All Eyez On Me",
                duration = "05:08",
                format = "MP3 • 320 kbps",
                gradient = listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
            ),
            AudioTrack(
                id = "7",
                title = "All Eyez On Me - Remix",
                artist = "DJ Belite",
                album = "West Coast Heat",
                duration = "03:42",
                format = "MP3 • 320 kbps",
                gradient = listOf(Color(0xFF14532D), Color(0xFF064E3B))
            ),
            AudioTrack(
                id = "8",
                title = "Alors on Phonk",
                artist = "Stromae x Phonk Edit",
                album = "Phonk Sessions",
                duration = "02:30",
                format = "FLAC • Lossless",
                gradient = listOf(Color(0xFF581C87), Color(0xFF3B0764))
            ),
            AudioTrack(
                id = "9",
                title = "Metamorphosis",
                artist = "INTERWORLD",
                album = "Metamorphosis",
                duration = "02:22",
                format = "FLAC • Lossless",
                gradient = listOf(Color(0xFF064E3B), Color(0xFF022C22))
            ),
            AudioTrack(
                id = "10",
                title = "Murder In My Mind",
                artist = "Kordhell",
                album = "Murder In My Mind",
                duration = "02:25",
                format = "MP3 • 320 kbps",
                gradient = listOf(Color(0xFF881337), Color(0xFF4C0519))
            ),
            AudioTrack(
                id = "11",
                title = "RAVE",
                artist = "Dxrk ダーク",
                album = "RAVE Sessions",
                duration = "02:49",
                format = "WAV • 24-bit",
                gradient = listOf(Color(0xFF1E1B4B), Color(0xFF0F0B1E))
            ),
            AudioTrack(
                id = "12",
                title = "Close Eyes",
                artist = "DVRST",
                album = "Close Eyes",
                duration = "02:12",
                format = "FLAC • Lossless",
                gradient = listOf(Color(0xFF3730A3), Color(0xFF1E1B4B))
            )
        )
    }

    val sampleAlbums = remember {
        listOf(
            AlbumItem("Going Dark", "ŞULTANRŞ", 4, listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B))),
            AlbumItem("Aeternum EP", "LXST CXNTURY", 6, listOf(Color(0xFF1E293B), Color(0xFF0F172A))),
            AlbumItem("After Hours", "The Weeknd", 14, listOf(Color(0xFF7F1D1D), Color(0xFF450A0A))),
            AlbumItem("All Eyez On Me", "2Pac", 27, listOf(Color(0xFF312E81), Color(0xFF1E1B4B))),
            AlbumItem("Phonk Sessions", "Various Artists", 18, listOf(Color(0xFF581C87), Color(0xFF3B0764))),
            AlbumItem("Arabic Dreams", "Kaito Shoma", 5, listOf(Color(0xFF78350F), Color(0xFF451A03)))
        )
    }

    val sampleArtists = remember {
        listOf(
            ArtistItem("ŞULTANRŞ", 3, listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B))),
            ArtistItem("LXST CXNTURY", 8, listOf(Color(0xFF1E293B), Color(0xFF0F172A))),
            ArtistItem("The Weeknd", 14, listOf(Color(0xFF7F1D1D), Color(0xFF450A0A))),
            ArtistItem("2Pac", 27, listOf(Color(0xFF312E81), Color(0xFF1E1B4B))),
            ArtistItem("DVRST", 6, listOf(Color(0xFF831843), Color(0xFF500724))),
            ArtistItem("Kordhell", 10, listOf(Color(0xFF881337), Color(0xFF4C0519)))
        )
    }

    val sampleVideos = remember {
        listOf(
            VideoItem("Dune_Part_Two_4K_HDR.mkv", "02:46:12", "3840x2160 • HEVC 10-bit", "8.4 GB"),
            VideoItem("Oppenheimer_IMAX_Direct.mp4", "03:00:21", "3840x2160 • HDR10", "12.1 GB"),
            VideoItem("Cyberpunk_Edgerunners_S01E01.mkv", "00:24:18", "1920x1080 • H.264", "1.2 GB"),
            VideoItem("Interstellar_Remaster_1080p.mkv", "02:49:03", "1920x1080 • Dolby 5.1", "5.6 GB"),
            VideoItem("Blade_Runner_2049_Scene.mp4", "00:14:40", "3840x2160 • 60 FPS", "2.1 GB")
        )
    }

    val samplePhotos = remember {
        listOf(
            PhotoItem("Cyberpunk_City_Wallpapers.png", "3840x2160", "4.8 MB", listOf(Color(0xFF4C1D95), Color(0xFFE11D48))),
            PhotoItem("Concert_Stage_Live_HDR.jpg", "4032x3024", "6.2 MB", listOf(Color(0xFF1E293B), Color(0xFF06B6D4))),
            PhotoItem("Album_Art_Collection_HD.jpg", "3000x3000", "3.1 MB", listOf(Color(0xFF78350F), Color(0xFFF59E0B))),
            PhotoItem("Minimalist_Neon_Poster.webp", "2560x1440", "1.9 MB", listOf(Color(0xFF581C87), Color(0xFF3B82F6)))
        )
    }

    var currentlyPlayingTrack by remember { mutableStateOf<AudioTrack?>(allTracks[0]) }
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0.42f) }
    var volumeLevel by remember { mutableFloatStateOf(0.80f) }

    var showAudioDetailDialog by remember { mutableStateOf(false) }
    var selectedVideoForDetail by remember { mutableStateOf<VideoItem?>(null) }
    var selectedPhotoForDetail by remember { mutableStateOf<PhotoItem?>(null) }

    fun playPreviousTrack() {
        val idx = allTracks.indexOfFirst { it.id == currentlyPlayingTrack?.id }
        if (idx > 0) {
            currentlyPlayingTrack = allTracks[idx - 1]
        } else {
            currentlyPlayingTrack = allTracks.last()
        }
        isPlaying = true
        playbackProgress = 0f
    }

    fun playNextTrack() {
        val idx = allTracks.indexOfFirst { it.id == currentlyPlayingTrack?.id }
        if (idx != -1 && idx < allTracks.lastIndex) {
            currentlyPlayingTrack = allTracks[idx + 1]
        } else {
            currentlyPlayingTrack = allTracks.first()
        }
        isPlaying = true
        playbackProgress = 0f
    }

    LaunchedEffect(currentMedia) {
        if (currentMedia != null) {
            when (currentMedia.type) {
                com.example.model.MediaType.VIDEO -> {
                    selectedMediaTab = PlayerMediaTab.VIDEO
                }
                com.example.model.MediaType.PHOTO -> {
                    selectedMediaTab = PlayerMediaTab.IMAGE
                }
                com.example.model.MediaType.AUDIO -> {
                    selectedMediaTab = PlayerMediaTab.AUDIO
                    val match = allTracks.find { it.title.contains(currentMedia.title, ignoreCase = true) || currentMedia.title.contains(it.title, ignoreCase = true) }
                    if (match != null) {
                        currentlyPlayingTrack = match
                    } else {
                        currentlyPlayingTrack = AudioTrack(
                            id = currentMedia.id,
                            title = currentMedia.title,
                            artist = "Local Audio",
                            album = "Shared Storage",
                            duration = currentMedia.duration,
                            format = currentMedia.format,
                            gradient = listOf(Color(0xFF4C1D95), Color(0xFF1E1B4B))
                        )
                    }
                }
                else -> {
                    // Other media types
                }
            }
            isPlaying = true
        }
    }

    // Filter tracks based on search query
    val filteredTracks = remember(allTracks, searchQuery) {
        if (searchQuery.isBlank()) allTracks
        else allTracks.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.album.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // 1. Top Media Tabs (Audio, Video, Image) with Purple/Lavender Underline
        TabRow(
            selectedTabIndex = selectedMediaTab.ordinal,
            containerColor = DarkBg,
            contentColor = TextPrimary,
            indicator = { tabPositions ->
                if (selectedMediaTab.ordinal < tabPositions.size) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedMediaTab.ordinal])
                            .height(3.dp)
                            .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                            .background(Color(0xFFB388FF)) // Purple / Lavender underline
                    )
                }
            },
            divider = {
                HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f), thickness = 1.dp)
            }
        ) {
            PlayerMediaTab.values().forEach { tab ->
                val isSelected = selectedMediaTab == tab
                Tab(
                    selected = isSelected,
                    onClick = {
                        selectedMediaTab = tab
                        isSearchActive = false
                        searchQuery = ""
                    },
                    text = {
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                )
            }
        }

        // 2. Sub-Bar: Horizontally Scrollable Filter Chips + Circular Search & Grid/List Buttons
        if (selectedMediaTab == PlayerMediaTab.AUDIO) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Horizontally Scrollable Filter Chips (Tracks, Albums, Artists)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AudioFilter.values().forEach { filter ->
                        val isSelected = selectedAudioFilter == filter
                        Surface(
                            onClick = { selectedAudioFilter = filter },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ServerCardAccent else DarkSurfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) Color(0xFFB388FF) else DarkCardBorder
                            ),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("chip_${filter.name.lowercase()}")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    text = filter.label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    ),
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Action Buttons: Circular Search & Circular Grid/List View
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Search Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSearchActive) AccentViolet.copy(alpha = 0.25f) else DarkSurfaceVariant)
                            .border(1.dp, if (isSearchActive) Color(0xFFB388FF) else DarkCardBorder, CircleShape)
                            .clickable {
                                isSearchActive = !isSearchActive
                                if (!isSearchActive) searchQuery = ""
                            }
                            .testTag("btn_player_search"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Tracks",
                            tint = if (isSearchActive) Color(0xFFB388FF) else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Circular Grid / List View Toggle Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkCardBorder, CircleShape)
                            .clickable {
                                isGridView = !isGridView
                                val mode = if (isGridView) "Grid view" else "List view"
                                Toast.makeText(context, mode, Toast.LENGTH_SHORT).show()
                            }
                            .testTag("btn_player_grid_list_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                            contentDescription = if (isGridView) "Switch to List View" else "Switch to Grid View",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Functional Search Bar (Animated Expand/Collapse)
        AnimatedVisibility(
            visible = isSearchActive,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search songs, artists, albums...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = AccentViolet, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextSecondary, modifier = Modifier.size(16.dp))
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
                    unfocusedContainerColor = DarkSurfaceVariant,
                    cursorColor = AccentViolet
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("input_player_search")
            )
        }

        // 3. Media Content Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedMediaTab) {
                PlayerMediaTab.AUDIO -> {
                    when (selectedAudioFilter) {
                        AudioFilter.TRACKS -> {
                            if (isGridView) {
                                // Grid View presentation
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(filteredTracks, key = { it.id }) { track ->
                                        TrackGridCard(
                                            track = track,
                                            isPlaying = currentlyPlayingTrack?.id == track.id,
                                            onTrackClick = {
                                                currentlyPlayingTrack = track
                                                isPlaying = true
                                                Toast.makeText(context, "Playing ${track.title}", Toast.LENGTH_SHORT).show()
                                            },
                                            onCastClick = onCastToRenderer
                                        )
                                    }
                                }
                            } else {
                                // Scrollable List View of Local Audio Tracks
                                LazyColumn(
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("player_tracks_list")
                                ) {
                                    items(filteredTracks, key = { it.id }) { track ->
                                        TrackListItem(
                                            track = track,
                                            isPlaying = currentlyPlayingTrack?.id == track.id,
                                            onTrackClick = {
                                                currentlyPlayingTrack = track
                                                isPlaying = true
                                                Toast.makeText(context, "Playing ${track.title}", Toast.LENGTH_SHORT).show()
                                            },
                                            onCastClick = onCastToRenderer
                                        )
                                    }
                                }
                            }
                        }

                        AudioFilter.ALBUMS -> {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(sampleAlbums) { album ->
                                    AlbumGridCard(
                                        album = album,
                                        onClick = {
                                            selectedAudioFilter = AudioFilter.TRACKS
                                            searchQuery = album.title
                                            isSearchActive = true
                                            Toast.makeText(context, "Filtering album: ${album.title}", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }

                        AudioFilter.ARTISTS -> {
                            LazyColumn(
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(sampleArtists) { artist ->
                                    ArtistListItem(
                                        artist = artist,
                                        onClick = {
                                            selectedAudioFilter = AudioFilter.TRACKS
                                            searchQuery = artist.name
                                            isSearchActive = true
                                            Toast.makeText(context, "Filtering artist: ${artist.name}", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                PlayerMediaTab.VIDEO -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sampleVideos) { video ->
                            VideoListItem(
                                video = video,
                                onPlay = {
                                    selectedVideoForDetail = video
                                    Toast.makeText(context, "Playing video: ${video.title}", Toast.LENGTH_SHORT).show()
                                },
                                onCast = onCastToRenderer
                            )
                        }
                    }
                }

                PlayerMediaTab.IMAGE -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(samplePhotos) { photo ->
                            PhotoGridCard(
                                photo = photo,
                                onClick = {
                                    selectedPhotoForDetail = photo
                                    Toast.makeText(context, "Displaying ${photo.title}", Toast.LENGTH_SHORT).show()
                                },
                                onCast = onCastToRenderer
                            )
                        }
                    }
                }
            }
        }

        // 4. Floating / Sticky "Now Playing" Mini Player
        currentlyPlayingTrack?.let { track ->
            NowPlayingMiniBar(
                track = track,
                isPlaying = isPlaying,
                playbackProgress = playbackProgress,
                onBarClick = { showAudioDetailDialog = true },
                onTogglePlay = { isPlaying = !isPlaying },
                onCastClick = onCastToRenderer
            )
        }
    }

    // Audio Detail Modal
    if (showAudioDetailDialog && currentlyPlayingTrack != null) {
        AudioDetailDialog(
            track = currentlyPlayingTrack!!,
            isPlaying = isPlaying,
            playbackProgress = playbackProgress,
            volumeLevel = volumeLevel,
            onTogglePlay = { isPlaying = !isPlaying },
            onPrevious = { playPreviousTrack() },
            onNext = { playNextTrack() },
            onSeek = { playbackProgress = it },
            onVolumeChange = { volumeLevel = it },
            onCast = onCastToRenderer,
            onDismiss = { showAudioDetailDialog = false }
        )
    }

    // Video Player Modal
    selectedVideoForDetail?.let { video ->
        VideoPlayerDialog(
            video = video,
            onCast = onCastToRenderer,
            onDismiss = { selectedVideoForDetail = null }
        )
    }

    // Photo Viewer Modal
    selectedPhotoForDetail?.let { photo ->
        PhotoViewerDialog(
            photo = photo,
            onCast = onCastToRenderer,
            onDismiss = { selectedPhotoForDetail = null }
        )
    }
}

// -----------------------------------------------------------------------------
// Component: Track List Item
// -----------------------------------------------------------------------------
@Composable
private fun TrackListItem(
    track: AudioTrack,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
    onCastClick: () -> Unit
) {
    val context = LocalContext.current
    var isMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick = onTrackClick,
        shape = RoundedCornerShape(14.dp),
        color = if (isPlaying) Color(0xFF1E1736) else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) Color(0xFFB388FF).copy(alpha = 0.5f) else DarkCardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("track_item_${track.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Square Rounded Album Artwork Thumbnail
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(track.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isPlaying) Color(0xFFB388FF) else Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Large Bold Song Title + Smaller Artist Name
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = if (isPlaying) Color(0xFFB388FF) else TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = TextMuted
                    )
                    Text(
                        text = track.duration,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = TextMuted
                    )
                }
            }

            // Three-Dot Overflow Menu on the Right
            Box {
                IconButton(
                    onClick = { isMenuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Track options",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text("Play Now", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AccentViolet) },
                        onClick = {
                            isMenuExpanded = false
                            onTrackClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Cast to Renderer", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Cast, contentDescription = null, tint = AccentCyan) },
                        onClick = {
                            isMenuExpanded = false
                            onCastClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Add to Queue", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = null, tint = TextSecondary) },
                        onClick = {
                            isMenuExpanded = false
                            Toast.makeText(context, "Added ${track.title} to queue", Toast.LENGTH_SHORT).show()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Details & Format", color = TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondary) },
                        onClick = {
                            isMenuExpanded = false
                            Toast.makeText(context, "${track.title} • ${track.format}", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Track Grid Card
// -----------------------------------------------------------------------------
@Composable
private fun TrackGridCard(
    track: AudioTrack,
    isPlaying: Boolean,
    onTrackClick: () -> Unit,
    onCastClick: () -> Unit
) {
    val context = LocalContext.current
    var isMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        onClick = onTrackClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isPlaying) Color(0xFF1E1736) else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPlaying) Color(0xFFB388FF).copy(alpha = 0.5f) else DarkCardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Square Artwork
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(track.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isPlaying) Color(0xFFB388FF) else Color.White.copy(alpha = 0.75f),
                    modifier = Modifier.size(36.dp)
                )

                // Duration badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = track.duration,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Overflow Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = if (isPlaying) Color(0xFFB388FF) else TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(
                        onClick = { isMenuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false },
                        modifier = Modifier
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    ) {
                        DropdownMenuItem(
                            text = { Text("Play", color = TextPrimary) },
                            onClick = {
                                isMenuExpanded = false
                                onTrackClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cast", color = TextPrimary) },
                            onClick = {
                                isMenuExpanded = false
                                onCastClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Album Grid Card
// -----------------------------------------------------------------------------
@Composable
private fun AlbumGridCard(
    album: AlbumItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(album.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = album.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${album.artist} • ${album.trackCount} tracks",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Artist List Item
// -----------------------------------------------------------------------------
@Composable
private fun ArtistListItem(
    artist: ArtistItem,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(artist.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                Text(
                    text = "${artist.trackCount} songs available locally",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = TextSecondary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Video List Item
// -----------------------------------------------------------------------------
@Composable
private fun VideoListItem(
    video: VideoItem,
    onPlay: () -> Unit,
    onCast: () -> Unit
) {
    Surface(
        onClick = onPlay,
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 72.dp, height = 50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E1435)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color(0xFFB388FF),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${video.resolution} • ${video.duration}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextSecondary
                )
            }

            IconButton(onClick = onCast) {
                Icon(Icons.Default.Cast, contentDescription = "Cast Video", tint = AccentCyan, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Photo Grid Card
// -----------------------------------------------------------------------------
@Composable
private fun PhotoGridCard(
    photo: PhotoItem,
    onClick: () -> Unit,
    onCast: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(photo.gradient)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = photo.title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${photo.dimensions} • ${photo.size}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = TextSecondary
            )
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Now Playing Mini Bar
// -----------------------------------------------------------------------------
@Composable
private fun NowPlayingMiniBar(
    track: AudioTrack,
    isPlaying: Boolean,
    playbackProgress: Float,
    onBarClick: () -> Unit,
    onTogglePlay: () -> Unit,
    onCastClick: () -> Unit
) {
    Surface(
        onClick = onBarClick,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = Color(0xFF161324),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
        tonalElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("now_playing_mini_bar")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Progress Indicator Line
            LinearProgressIndicator(
                progress = { playbackProgress },
                color = Color(0xFFB388FF),
                trackColor = Color.White.copy(alpha = 0.1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Artwork
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(track.gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Song Title & Artist
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFFB388FF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Cast Action
                IconButton(
                    onClick = onCastClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cast,
                        contentDescription = "Cast",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Play / Pause Action
                IconButton(
                    onClick = onTogglePlay,
                    modifier = Modifier
                        .size(38.dp)
                        .background(ServerCardAccent, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Audio Detail & Full Player Modal
// -----------------------------------------------------------------------------
@Composable
fun AudioDetailDialog(
    track: AudioTrack,
    isPlaying: Boolean,
    playbackProgress: Float,
    volumeLevel: Float,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeek: (Float) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onCast: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(26.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(26.dp))
                .testTag("dialog_audio_detail"),
            color = Color(0xFF13111E),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Bar: Close & Cast buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(38.dp)
                            .background(DarkSurfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Close player", tint = TextPrimary)
                    }

                    Text(
                        text = "NOW STREAMING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFB388FF)
                    )

                    IconButton(
                        onClick = onCast,
                        modifier = Modifier
                            .size(38.dp)
                            .background(DarkSurfaceVariant, CircleShape)
                    ) {
                        Icon(Icons.Default.Cast, contentDescription = "Cast audio", tint = TextPrimary)
                    }
                }

                // Artwork Card with animated border
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(track.gradient))
                        .border(2.dp, Color(0xFFB388FF).copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(72.dp)
                    )
                }

                // Metadata: Title, Artist, Album, Format
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = track.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp
                        ),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = track.artist,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = Color(0xFFB388FF)
                    )
                    Text(
                        text = "${track.album} • ${track.format}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }

                // Progress Bar & Elapsed Time
                Column(modifier = Modifier.fillMaxWidth()) {
                    Slider(
                        value = playbackProgress,
                        onValueChange = onSeek,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFB388FF),
                            activeTrackColor = Color(0xFFB388FF),
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "01:14",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = TextSecondary
                        )
                        Text(
                            text = track.duration,
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = TextSecondary
                        )
                    }
                }

                // Transport Controls (Previous, Play/Pause, Next)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onPrevious,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous Track", tint = TextPrimary, modifier = Modifier.size(30.dp))
                    }

                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier
                            .size(64.dp)
                            .background(ServerCardAccent, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Track", tint = TextPrimary, modifier = Modifier.size(30.dp))
                    }
                }

                // Volume Slider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.VolumeDown, contentDescription = "Volume down", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Slider(
                        value = volumeLevel,
                        onValueChange = onVolumeChange,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = AccentViolet,
                            inactiveTrackColor = DarkCardBorder
                        )
                    )
                    Icon(Icons.Default.VolumeUp, contentDescription = "Volume up", tint = TextSecondary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Video Player Preview Modal
// -----------------------------------------------------------------------------
@Composable
fun VideoPlayerDialog(
    video: VideoItem,
    onCast: () -> Unit,
    onDismiss: () -> Unit
) {
    var isVideoPlaying by remember { mutableStateOf(true) }
    var videoProgress by remember { mutableFloatStateOf(0.18f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp))
                .testTag("dialog_video_player"),
            color = Color(0xFF0F101A),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Video Stream Preview",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // 16:9 Video Canvas Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                        .clickable { isVideoPlaying = !isVideoPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVideoPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                        contentDescription = "Toggle Video",
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(54.dp)
                    )

                    // Resolution Overlay Tag
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = video.resolution,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            color = AccentCyan,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Video Title & Duration
                Column {
                    Text(
                        text = video.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Duration: ${video.duration} • Size: ${video.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                // Video Progress Bar
                Slider(
                    value = videoProgress,
                    onValueChange = { videoProgress = it },
                    colors = SliderDefaults.colors(
                        thumbColor = AccentCyan,
                        activeTrackColor = AccentCyan,
                        inactiveTrackColor = DarkSurfaceVariant
                    )
                )

                // Actions: Cast to TV / Done
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            onCast()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Cast, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Cast to Screen", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text("Done", color = TextPrimary)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Component: Photo Viewer Modal
// -----------------------------------------------------------------------------
@Composable
fun PhotoViewerDialog(
    photo: PhotoItem,
    onCast: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp))
                .testTag("dialog_photo_viewer"),
            color = Color(0xFF0C0D14),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = photo.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Large Image Preview Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(photo.gradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(64.dp)
                    )
                }

                // Metadata Details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Resolution: ${photo.dimensions}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("File Size: ${photo.size}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }

                // Cast to Display
                Button(
                    onClick = {
                        onCast()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentViolet),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Cast, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cast to Receiver / TV", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
