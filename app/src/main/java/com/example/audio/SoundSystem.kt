package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Procedural synthesizer for retro-cyber sound effects and haptic engine for Dhaka 2077.
 */
class SoundSystem(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    var isMuted: Boolean = false
    var isHapticsEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Synthesize and play a frequency sweep or tone.
     */
    fun playTone(startFreq: Float, endFreq: Float, durationMs: Int, volume: Float = 0.8f) {
        if (isMuted) return
        scope.launch {
            try {
                val sampleRate = 22050
                val numSamples = (durationMs * sampleRate) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val currentFreq = startFreq + (endFreq - startFreq) * progress
                    val time = i.toDouble() / sampleRate
                    // Sine wave with slight harmonic for cyber buzz
                    val wave = (sin(2.0 * Math.PI * currentFreq * time) * 0.8 +
                            sin(4.0 * Math.PI * currentFreq * time) * 0.2)
                    // Fade envelope
                    val envelope = if (progress < 0.1f) {
                        progress / 0.1f
                    } else if (progress > 0.8f) {
                        (1f - progress) / 0.2f
                    } else {
                        1f
                    }
                    val sample = (wave * envelope * volume * 32767).toInt().coerceIn(-32768, 32767)
                    buffer[i] = sample.toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                // Let it finish and clean up
                Thread.sleep(durationMs.toLong() + 20)
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore audio generation error on device
            }
        }
    }

    // Iconic Dhaka Honk Sound (dual harsh discordant frequencies)
    fun playDhakaHonk() {
        if (isMuted) return
        scope.launch {
            try {
                val sampleRate = 22050
                val durationMs = 180
                val numSamples = (durationMs * sampleRate) / 1000
                val buffer = ShortArray(numSamples)

                val freq1 = 440.0 // Standard A4
                val freq2 = 470.0 // Discordant horn drone

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val progress = i.toFloat() / numSamples
                    val wave = (sin(2.0 * Math.PI * freq1 * time) * 0.5 +
                            sin(2.0 * Math.PI * freq2 * time) * 0.5)
                    val envelope = if (progress < 0.1f) progress / 0.1f else (1f - progress)
                    val sample = (wave * envelope * 0.7f * 32767).toInt().coerceIn(-32768, 32767)
                    buffer[i] = sample.toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()
                Thread.sleep(200)
                track.release()
            } catch (_: Exception) {
            }
        }
        vibrate(35)
    }

    // Cyber Police Whistle
    fun playPoliceWhistle() {
        playTone(1800f, 2400f, 150, 0.9f)
        vibrate(60)
    }

    // Cyber Drone Tractor Beam Swoop
    fun playDroneAction() {
        playTone(300f, 950f, 320, 0.85f)
        vibrate(80)
    }

    // VIP Siren
    fun playVipSiren() {
        playTone(880f, 520f, 250, 0.9f)
        vibrate(100)
    }

    // EMP Shockwave
    fun playEmpBlast() {
        playTone(1200f, 120f, 400, 1.0f)
        vibrate(150)
    }

    // Signal Click
    fun playSignalClick() {
        playTone(700f, 850f, 60, 0.6f)
        vibrate(20)
    }

    // Coin Pickup
    fun playCoinChime() {
        playTone(980f, 1320f, 100, 0.7f)
    }

    fun vibrate(durationMs: Long) {
        if (!isHapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }
}
