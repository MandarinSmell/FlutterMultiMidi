# flutter_multi_midi

Similar plugin with flutter_midi plugin, but it can support multiple channels with one sound font file.
Be aware that this plugin only supports Android yet.

## Getting Started

### Implementation

To implement this plugin in Flutter, add this line in pubspec.yaml

```
fluttermultimidi: ^1.0.7
```

### Example

Let's assume that you put "instrument.sf2" file in "assets/" folder
Then you have to add this line in pubspec.yaml

```
assets:
  - assets/instrument.sf2
```

After adding sound font file into assets data, you can initialize sound font like this

```
final midi = FlutterMultiMidi();
```

To put sound font file data into this class, you have to call these lines in somewhere, wherever you want

```
rootBundle.load("assets/instrument.sf2").then((sf2) {
    midi.loadSf2(sf2, name: "optional.sf2");
});
```

To play/stop note with FlutterMultiMidi class, you can call like this

```
midi.playNote(note: 60);
midi.stopNote(note: 60);
```

When there are several channels in sound font file, you can change channel like this

```
midi.changeChannel(channel: 1);
```
