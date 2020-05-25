#import "FluttermultimidiPlugin.h"
#if __has_include(<fluttermultimidi/fluttermultimidi-Swift.h>)
#import <fluttermultimidi/fluttermultimidi-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "fluttermultimidi-Swift.h"
#endif

@implementation FluttermultimidiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFluttermultimidiPlugin registerWithRegistrar:registrar];
}
@end
