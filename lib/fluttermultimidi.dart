import 'dart:async';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'cache.dart';

class FlutterMultiMidi {
  static const MethodChannel _channel =
      const MethodChannel('flutter_multi_midi');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  /// Load [sf2] file with specified byte data.<br>
  /// This method will create sf2 file in temporary directory and load sf2 with OS internal codes
  Future<String> loadSf2({@required ByteData sf2, String name = "instrument.sf2", int midi = 16}) async {
    if(kIsWeb)
      return _channel.invokeMethod("loadSf2");

    File f = await writeToFile(sf2, name: name);

    final String result = await _channel.invokeMethod("loadSf2", {"path": f.path, "midiNumber" : midi});

    print("Result : $result");

    return result;
  }

  Future<String> changeChannel({@required int channel}) async {
    final String r = await _channel.invokeMethod("changeChannel", {"channel" : channel});

    return r;
  }

  Future<String> playNote({@required int note, int vel}) async {
    final String r = await _channel.invokeMethod("playNote", {"note" : note, "vel" : vel});

    return r;
  }

  Future<String> stopNote({@required int note, int vel}) async {
    final String r = await _channel.invokeMethod("stopNote", {"note" : note, "vel" : vel});

    return r;
  }

  Future<int> getChannelSize() async {
    final int r = await _channel.invokeMethod("getChannelSize");

    return r;
  }
}
