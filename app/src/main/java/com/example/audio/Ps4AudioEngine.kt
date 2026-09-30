package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.BgmPreset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class Ps4AudioEngine(private val context: Context) {
    private val sampleRate = 44100
    private var bgmJob: Job? = null
    private var bgmAudioTrack: AudioTrack? = null
    private var customMediaPlayer: MediaPlayer? = null

    private var currentPreset = BgmPreset.PS4_AMBIENT
    private var currentCustomUri: String? = null
    private var isPlaying = true
    private var bgmVolume = 0.7f
    private var sfxEnabled = true
    private var sfxVolume = 0.85f
    private var activeGameIndex = 0

    fun onGameSelected(gameIndex: Int, isAddButton: Boolean = false) {
        activeGameIndex = if (isAddButton) 7 else gameIndex.coerceAtLeast(0)
        playNavTick()
        if (isPlaying && (bgmJob == null || bgmJob?.isActive == false) && currentPreset != BgmPreset.MUTED) {
            startBgm()
        }
    }

    private val audioScope = CoroutineScope(Dispatchers.Default)

    fun startBgm() {
        isPlaying = true
        if (currentPreset == BgmPreset.CUSTOM_AUDIO && !currentCustomUri.isNullOrBlank()) {
            startCustomAudio()
        } else if (currentPreset != BgmPreset.MUTED) {
            startSynthBgm()
        }
    }

    fun stopBgm() {
        isPlaying = false
        stopSynthBgm()
        stopCustomAudio()
    }

    fun updateSettings(
        preset: BgmPreset,
        customUri: String?,
        playing: Boolean,
        volume: Float,
        sfxOn: Boolean,
        sfxVol: Float
    ) {
        val presetChanged = currentPreset != preset
        val uriChanged = currentCustomUri != customUri
        val playChanged = isPlaying != playing
        val volChanged = bgmVolume != volume

        currentPreset = preset
        currentCustomUri = customUri
        isPlaying = playing
        bgmVolume = volume
        sfxEnabled = sfxOn
        sfxVolume = sfxVol

        if (volChanged) {
            try {
                customMediaPlayer?.setVolume(volume, volume)
            } catch (_: Exception) {}
        }

        if (preset == BgmPreset.MUTED || !playing) {
            stopBgm()
            return
        }

        if (preset == BgmPreset.CUSTOM_AUDIO) {
            stopSynthBgm()
            if (uriChanged || playChanged || customMediaPlayer == null) {
                if (!customUri.isNullOrBlank()) {
                    startCustomAudio()
                } else {
                    // User provides their own audio; do not play unsolicited synth if no custom audio is selected yet
                    stopCustomAudio()
                }
            }
        } else {
            stopCustomAudio()
            if (presetChanged || playChanged || bgmJob == null) {
                stopSynthBgm()
                startSynthBgm()
            }
        }
    }

    private fun startCustomAudio() {
        stopCustomAudio()
        val uriStr = currentCustomUri ?: return
        try {
            val mp = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                if (uriStr.startsWith("/")) {
                    setDataSource(uriStr)
                } else {
                    val uri = Uri.parse(uriStr)
                    setDataSource(context, uri)
                }
                isLooping = true
                setVolume(bgmVolume, bgmVolume)
                setOnPreparedListener { player ->
                    if (isPlaying) {
                        player.start()
                    }
                }
                setOnErrorListener { _, _, _ ->
                    true
                }
                prepareAsync()
            }
            customMediaPlayer = mp
        } catch (e: Exception) {
            Log.e("Ps4AudioEngine", "Error loading custom audio: ${e.message}")
        }
    }

    private fun stopCustomAudio() {
        try {
            customMediaPlayer?.stop()
            customMediaPlayer?.reset()
            customMediaPlayer?.release()
        } catch (_: Exception) {}
        customMediaPlayer = null
    }

    private fun startSynthBgm() {
        if (bgmJob?.isActive == true) return
        bgmJob = audioScope.launch {
            try {
                val bufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                ).coerceAtLeast(4096)

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                bgmAudioTrack = track
                track.play()

                val buffer = ShortArray(2048)
                var sampleIndex = 0L

                val chordsPs4 = arrayOf(
                    doubleArrayOf(130.81, 164.81, 196.00, 246.94, 392.00), // Cmaj7
                    doubleArrayOf(146.83, 174.61, 220.00, 261.63, 440.00), // Dm7
                    doubleArrayOf(110.00, 164.81, 196.00, 261.63, 329.63), // Am7
                    doubleArrayOf(174.61, 220.00, 261.63, 329.63, 523.25)  // Fmaj7
                )

                val synthFrequencies = arrayOf(
                    doubleArrayOf(146.83, 220.0, 293.66, 440.0, 587.33),
                    doubleArrayOf(130.81, 196.0, 261.63, 392.0, 523.25),
                    doubleArrayOf(116.54, 174.61, 233.08, 349.23, 466.16)
                )

                // Backsound tersendiri untuk setiap game
                val gameThematicChords = arrayOf(
                    doubleArrayOf(130.81, 164.81, 196.00, 246.94, 392.00), // Game 0: PES Stadium dynamic Cmaj9
                    doubleArrayOf(146.83, 174.61, 220.00, 293.66, 440.00), // Game 1: Witcher 3 mystic Dm7
                    doubleArrayOf(110.00, 164.81, 196.00, 261.63, 329.63), // Game 2: God of War II deep Am7
                    doubleArrayOf(174.61, 220.00, 261.63, 329.63, 523.25), // Game 3: Zelda gentle Fmaj7
                    doubleArrayOf(164.81, 196.00, 246.94, 329.63, 493.88), // Game 4: GameHub Em7
                    doubleArrayOf(123.47, 185.00, 246.94, 311.13, 369.99), // Game 5: Mobile Legends Bm
                    doubleArrayOf(138.59, 185.00, 220.00, 277.18, 415.30), // Game 6: Assassin's seafaring chord
                    doubleArrayOf(98.00, 146.83, 196.00, 246.94, 293.66, 392.00) // 7: Konsol Baru / Tambah Game ambient Gmaj7
                )

                while (isActive && isPlaying && currentPreset != BgmPreset.MUTED) {
                    val chordTimeSeconds = (sampleIndex.toDouble() / sampleRate)
                    val activeChordSet = if (currentPreset == BgmPreset.SYNTH_CHILL) synthFrequencies else chordsPs4
                    val gameChord = gameThematicChords[activeGameIndex.coerceIn(0, gameThematicChords.size - 1)]
                    val freqs = if (currentPreset == BgmPreset.PS4_AMBIENT || currentPreset == BgmPreset.CUSTOM_AUDIO) gameChord else activeChordSet[((chordTimeSeconds / 5.0).toInt()) % activeChordSet.size]

                    val masterVol = (bgmVolume.coerceIn(0f, 1f) * 0.38f)

                    for (i in buffer.indices) {
                        val t = (sampleIndex + i).toDouble() / sampleRate
                        var sample = 0.0

                        val lfo = 0.75 + 0.25 * sin(2.0 * PI * 0.2 * t)
                        val slowLfo2 = 0.85 + 0.15 * sin(2.0 * PI * 0.08 * t + 1.0)

                        for (fIdx in freqs.indices) {
                            val f = freqs[fIdx]
                            val detune = 1.0 + (fIdx - 2) * 0.0015
                            val s1 = sin(2.0 * PI * f * t)
                            val s2 = sin(2.0 * PI * (f * detune) * t + 0.5) * 0.5
                            val subHarmonic = sin(2.0 * PI * (f * 0.5) * t) * 0.3
                            sample += (s1 + s2 + subHarmonic) / (freqs.size * 1.2)
                        }

                        sample *= lfo * slowLfo2 * masterVol
                        val clamped = (sample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                        buffer[i] = clamped
                    }

                    track.write(buffer, 0, buffer.size)
                    sampleIndex += buffer.size
                }
            } catch (e: Exception) {
                Log.d("Ps4AudioEngine", "BGM loop ended: ${e.message}")
            } finally {
                stopSynthBgm()
            }
        }
    }

    private fun stopSynthBgm() {
        bgmJob?.cancel()
        bgmJob = null
        try {
            bgmAudioTrack?.pause()
            bgmAudioTrack?.flush()
            bgmAudioTrack?.stop()
            bgmAudioTrack?.release()
        } catch (_: Exception) {}
        bgmAudioTrack = null
    }

    fun playNavTick() {
        if (!sfxEnabled) return
        audioScope.launch {
            try {
                val durationMs = 32
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val soundBuffer = ShortArray(numSamples)
                val vol = (sfxVolume * 0.50f).coerceIn(0f, 1f)

                // Signature PlayStation UI navigation tick sound
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 190.0)
                    val freq = 1250.0 - (t * 5500.0)
                    val s = (sin(2.0 * PI * freq.coerceAtLeast(320.0) * t) * 0.75 + sin(2.0 * PI * 880.0 * t) * 0.25) * env * vol
                    soundBuffer[i] = (s * 32767).toInt().toShort()
                }

                playOneShot(soundBuffer)
            } catch (_: Exception) {}
        }
    }

    fun playSelectChime() {
        if (!sfxEnabled) return
        audioScope.launch {
            try {
                val durationMs = 280
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val soundBuffer = ShortArray(numSamples)
                val vol = (sfxVolume * 0.45f).coerceIn(0f, 1f)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 12.0)
                    val s = (sin(2.0 * PI * 1318.51 * t) * 0.6 + sin(2.0 * PI * 1975.53 * t) * 0.4) * env * vol
                    soundBuffer[i] = (s * 32767).toInt().toShort()
                }

                playOneShot(soundBuffer)
            } catch (_: Exception) {}
        }
    }

    fun playBackChime() {
        if (!sfxEnabled) return
        audioScope.launch {
            try {
                val durationMs = 180
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val soundBuffer = ShortArray(numSamples)
                val vol = (sfxVolume * 0.40f).coerceIn(0f, 1f)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-t * 16.0)
                    val freq = 650.0 - (t * 1200.0)
                    val s = sin(2.0 * PI * freq.coerceAtLeast(300.0) * t) * env * vol
                    soundBuffer[i] = (s * 32767).toInt().toShort()
                }

                playOneShot(soundBuffer)
            } catch (_: Exception) {}
        }
    }

    fun playStartGameSweep() {
        if (!sfxEnabled) return
        audioScope.launch {
            try {
                val durationMs = 450
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val soundBuffer = ShortArray(numSamples)
                val vol = (sfxVolume * 0.50f).coerceIn(0f, 1f)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = (sin(PI * (i.toDouble() / numSamples))).coerceIn(0.0, 1.0)
                    val freq = 440.0 + (t * 880.0)
                    val s = sin(2.0 * PI * freq * t) * env * vol
                    soundBuffer[i] = (s * 32767).toInt().toShort()
                }

                playOneShot(soundBuffer)
            } catch (_: Exception) {}
        }
    }

    private fun playOneShot(samples: ShortArray) {
        val sfxTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(samples.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        sfxTrack.write(samples, 0, samples.size)
        sfxTrack.play()
        sfxTrack.setNotificationMarkerPosition(samples.size)
        sfxTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) {
                try {
                    track?.stop()
                    track?.release()
                } catch (_: Exception) {}
            }
            override fun onPeriodicNotification(track: AudioTrack?) {}
        })
    }

    fun release() {
        stopBgm()
    }
}
