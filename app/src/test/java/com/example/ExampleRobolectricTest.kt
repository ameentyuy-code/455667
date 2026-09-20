package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("LocalStream", appName)
  }

  @Test
  fun `verify server config defaults`() {
    val config = com.example.model.ServerConfig()
    assertEquals("LocalStream", config.serverName)
    assertEquals(true, config.httpWebServer)
    assertEquals(true, config.upnpContentStreaming)
    assertEquals(true, config.upnpMediaReceiver)
    assertEquals("Name (A-Z)", config.upnpSortOrder)
    assertEquals(false, config.smbServerSearch)
    assertEquals(true, config.transcodeFlacToWav)
    assertEquals(false, config.autoStartServer)
  }

  @Test
  fun `verify player media tab and filter enums`() {
    assertEquals("Audio", com.example.ui.screens.PlayerMediaTab.AUDIO.label)
    assertEquals("Video", com.example.ui.screens.PlayerMediaTab.VIDEO.label)
    assertEquals("Image", com.example.ui.screens.PlayerMediaTab.IMAGE.label)

    assertEquals("Tracks", com.example.ui.screens.AudioFilter.TRACKS.label)
    assertEquals("Albums", com.example.ui.screens.AudioFilter.ALBUMS.label)
    assertEquals("Artists", com.example.ui.screens.AudioFilter.ARTISTS.label)
  }

  @Test
  fun `verify casting target device model`() {
    val device = com.example.ui.screens.CastingTargetDevice(
        id = "local",
        name = "Play Locally (This Device)",
        type = "Internal Audio & Video Engine",
        icon = androidx.compose.material.icons.Icons.Default.PhoneAndroid,
        isLocal = true
    )
    assertEquals("Play Locally (This Device)", device.name)
    assertEquals(true, device.isLocal)
  }

  @Test
  fun `verify file sort mode and log filter categories`() {
    assertEquals("Name (A-Z)", com.example.ui.screens.FileSortMode.NAME_ASC.label)
    assertEquals("Size (Largest)", com.example.ui.screens.FileSortMode.SIZE_DESC.label)
    assertEquals("All Logs", com.example.ui.screens.LogFilterCategory.ALL.label)
    assertEquals("DLNA/UPnP", com.example.ui.screens.LogFilterCategory.DLNA.label)
  }
}
