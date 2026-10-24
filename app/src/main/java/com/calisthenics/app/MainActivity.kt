package com.calisthenics.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

private val Background = Color(0xFF101114)
private val Panel = Color(0xFF1D2025)
private val Lime = Color(0xFFB7F34A)
private val Muted = Color(0xFF9A9DA5)

data class Track(
    val title: String,
    val artist: String,
    val url: String
)

// Public demo streams for testing.
// Replace these URLs with audio streams you have permission to use.
private val tracks = listOf(
    Track(
        "SoundHelix 1",
        "Demo stream",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
    ),
    Track(
        "SoundHelix 2",
        "Demo stream",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"
    ),
    Track(
        "SoundHelix 3",
        "Demo stream",
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"
    )
)

class MainActivity : ComponentActivity() {

    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        player = ExoPlayer.Builder(this).build()

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Lime,
                    background = Background,
                    surface = Panel
                )
            ) {
                MusicScreen(
                    player = player,
                    onOpenUrl = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.youtube.com/")
                        )
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}

@Composable
private fun MusicScreen(
    player: ExoPlayer,
    onOpenUrl: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var currentTrack by remember { mutableStateOf<Track?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    val filteredTracks = tracks.filter {
        it.title.contains(query, ignoreCase = true) ||
            it.artist.contains(query, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Lime),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = Background,
                    modifier = Modifier.size(29.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    "CALISTHENICS",
                    color = Lime,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    "Music",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            IconButton(onClick = onOpenUrl) {
                Icon(
                    Icons.Default.OpenInBrowser,
                    contentDescription = "Open browser",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF303C23),
                            Color(0xFF20291B),
                            Panel
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                "YOUR TRAINING SOUNDTRACK",
                color = Lime,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "Move. Train.\nRepeat.",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 37.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Music for your next workout.",
                color = Muted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    val track = tracks.first()
                    currentTrack = track
                    player.setMediaItem(MediaItem.fromUri(track.url))
                    player.prepare()
                    player.play()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Background
                ),
                shape = RoundedCornerShape(50.dp)
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Spacer(Modifier.width(6.dp))
                Text("PLAY MUSIC", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Find your sound",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("Search demo tracks…", color = Muted) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = Muted
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Lime,
                unfocusedBorderColor = Color(0xFF383B42),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Lime
            )
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredTracks) { track ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Panel)
                        .clickable {
                            currentTrack = track
                            player.setMediaItem(
                                MediaItem.fromUri(track.url)
                            )
                            player.prepare()
                            player.play()
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF435B2A),
                                        Color(0xFF20291B)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Lime,
                            modifier = Modifier.size(27.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            track.title,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            track.artist,
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }

                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Play ${track.title}",
                        tint = Lime,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Spotify-inspired mini-player
        currentTrack?.let { track ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF292D32))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Album,
                    contentDescription = null,
                    tint = Lime,
                    modifier = Modifier.size(38.dp)
                )

                Spacer(Modifier.width(10.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        track.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (isPlaying) "Now playing" else "Paused",
                        color = Muted,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = {
                        if (player.isPlaying) {
                            player.pause()
                        } else {
                            player.play()
                        }
                    }
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause
                        else Icons.Default.PlayArrow,
                        contentDescription = "Play or pause",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = {
                        player.seekToNextMediaItem()
                    }
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = Lime
                    )
                }
            }
        }

        NavigationBar(
            containerColor = Color(0xFF15171B),
            contentColor = Lime
        ) {
            NavigationBarItem(
                selected = true,
                onClick = {},
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home"
                    )
                },
                label = { Text("Home") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Lime,
                    selectedTextColor = Lime,
                    indicatorColor = Color(0xFF303C23),
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )

            NavigationBarItem(
                selected = false,
                onClick = onOpenUrl,
                icon = {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = "Explore online"
                    )
                },
                label = { Text("Explore") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Lime,
                    selectedTextColor = Lime,
                    indicatorColor = Color(0xFF303C23),
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                )
            )
        }
    }
}
