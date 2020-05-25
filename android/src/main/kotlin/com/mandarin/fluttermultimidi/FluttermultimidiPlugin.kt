package com.mandarin.fluttermultimidi

import android.os.Build
import androidx.annotation.NonNull
import cn.sherlock.com.sun.media.sound.SF2Instrument
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import cn.sherlock.javax.sound.sampled.AudioFormat
import cn.sherlock.javax.sound.sampled.SourceDataLine
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import jp.kshoji.javax.sound.midi.InvalidMidiDataException
import jp.kshoji.javax.sound.midi.Receiver
import jp.kshoji.javax.sound.midi.ShortMessage
import java.io.File
import java.io.IOException

/** FluttermultimidiPlugin */
public class FluttermultimidiPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var synth: SoftSynthesizer
    private lateinit var recv: Receiver

    private var sf2Channel = 0

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_multi_midi")
        channel.setMethodCallHandler(this)
    }

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "flutter_multi_midi")
            channel.setMethodCallHandler(FluttermultimidiPlugin())
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${Build.VERSION.RELEASE}")
            }
            "loadSf2" -> {
                try {
                    val path = call.argument("path") as String? ?: return
                    val midi = call.argument("midiNumber") as Int? ?: 16
                    val f = File(path)

                    val sf2 = SF2Soundbank(f)

                    synth = SoftSynthesizer()

                    val m: Int = Integer.valueOf(midi)

                    val info = hashMapOf(
                            "interpolation" to "sinc",
                            "control rate" to 147f,
                            "format" to AudioFormat(44100f, 16, 2, true, false),
                            "latency" to 120000L,
                            "device id" to 0,
                            "max polyphony" to Integer.valueOf(64),
                            "reverb" to false,
                            "chorus" to false,
                            "auto gain control" to true,
                            "large mode" to false,
                            "midi channels" to m,
                            "jitter correction" to true,
                            "light reverb" to false,
                            "load default soundbank" to true
                    )

                    synth.open(null, info)
                    synth.loadAllInstruments(sf2)

                    for (i in synth.channels.indices) {
                        synth.channels[i].programChange(i)
                    }

                    recv = synth.receiver

                    result.success("Method succeeded. Requested midi number : $midi | Real midi number : "+ synth.channels.size)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "changeChannel" -> {
                var ch = call.argument("channel") as Int? ?: 0

                if (ch < 0) {
                    ch = 0
                } else if (ch >= synth.channels.size) {
                    ch = synth.channels.size - 1
                }

                sf2Channel = ch
            }
            "playNote" -> {
                val n = call.argument("note") as Int? ?: return
                val v = call.argument("vel") as Int? ?: 127

                try {
                    val msg = ShortMessage()

                    msg.setMessage((ShortMessage.NOTE_ON and 0xf0) or (sf2Channel and 0x0f), n, v)

                    recv.send(msg, -1)
                } catch (e: InvalidMidiDataException) {
                    e.printStackTrace()
                }
            }
            "stopNote" -> {
                val n = call.argument("note") as Int? ?: return
                val v = call.argument("vel") as Int? ?: 127

                try {
                    val msg = ShortMessage()

                    msg.setMessage((ShortMessage.NOTE_OFF and 0xf0) or (sf2Channel and 0x0f), n, v)

                    recv.send(msg, -1)
                } catch (e: InvalidMidiDataException) {
                    e.printStackTrace()
                }
            }
            "getChannelSize" -> {
                val c = synth.channels.size

                println("Channel Size : $c")

                result.success(c)
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
