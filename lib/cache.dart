import 'dart:async';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

Future<File> writeToFile(ByteData data, {String name = "instrument.sf2"}) async {
  if(kIsWeb)
    return null;

  final buff = data.buffer;
  final d = await getApplicationDocumentsDirectory();
  final p = "${d.path}/$name";

  return File(p).writeAsBytes(buff.asUint8List(data.offsetInBytes, data.lengthInBytes));
}

