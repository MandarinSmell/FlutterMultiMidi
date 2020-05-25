package com.mandarin.fluttermultimidi

import androidx.annotation.NonNull;
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer

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
import java.lang.Exception

/** FluttermultimidiPlugin */
public class FluttermultimidiPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var synth : SoftSynthesizer
  private lateinit var recv : Receiver

  private var sf2Channel = 0

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_multi_midi")
    channel.setMethodCallHandler(this);
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
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
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
        }
        "loadSf2" -> {
          try {
            val path = call.argument("path") as String? ?: return
            val f = File(path)

            val sf2 = SF2Soundbank(f)

            synth = SoftSynthesizer()
            synth.open()
            synth.loadAllInstruments(sf2)

            for(i in synth.channels.indices) {
              synth.channels[i].programChange(i)
            }

            recv = synth.receiver
          } catch (e: IOException) {
            e.printStackTrace()
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
        "changeChannel" -> {
          var ch = call.argument("channel") as Int? ?: 0

          if(ch < 0) {
            ch = 0
          } else if(ch >= synth.channels.size) {
            ch = synth.channels.size-1
          }

          sf2Channel = ch
        }
        "playNote" -> {
          val n = call.argument("note") as Int? ?: 60

          try {
            val msg = ShortMessage()

            msg.setMessage(ShortMessage.NOTE_ON, sf2Channel, n, 127)

            recv.send(msg, -1)
          } catch (e: InvalidMidiDataException) {
            e.printStackTrace()
          }
        }
        "stopNote" -> {
          val n = call.argument("note") as Int? ?: return

          try {
            val msg = ShortMessage()

            msg.setMessage(ShortMessage.NOTE_OFF, sf2Channel, n, 127)

            recv.send(msg, -1)
          } catch (e: InvalidMidiDataException) {
            e.printStackTrace()
          }
        }
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
