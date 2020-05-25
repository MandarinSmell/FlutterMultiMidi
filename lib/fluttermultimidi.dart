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

  Future<String> loadSf2({@required ByteData sf2, String name = "instrument.sf2"}) async {
    if(kIsWeb)
      return _channel.invokeMethod("loadSf2");

    File f = await writeToFile(sf2, name: name);

    final String result = await _channel.invokeMethod("loadSf2", {"path": f.path});

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
}
