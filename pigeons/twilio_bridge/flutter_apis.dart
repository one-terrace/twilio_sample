import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/src/twilio_bridge/flutter_apis.g.dart',
  dartOptions: DartOptions(),
  cppOptions: CppOptions(namespace: 'twilio_sample'),
  cppHeaderOut: 'windows/runner/twilio_bridge/flutter_apis.g.h',
  cppSourceOut: 'windows/runner/twilio_bridge/flutter_apis.g.cpp',
  kotlinOut: 'android/app/src/main/kotlin/com/example/twilio_sample/twilio_bridge/FlutterAPIs.g.kt',
  javaOut: 'android/app/src/main/java/io/flutter/plugins/twilio_bridge/FlutterAPIs.java',
  javaOptions: JavaOptions(),
  swiftOut: 'ios/Runner/FlutterAPIs.g.swift',
  swiftOptions: SwiftOptions(),
  objcHeaderOut: 'macos/Runner/FlutterAPIs.g.h',
  objcSourceOut: 'macos/Runner/FlutterAPIs.g.m',
  // Set this to a unique prefix for your plugin or application, per Objective-C naming conventions.
  objcOptions: ObjcOptions(prefix: 'PGN'),
  dartPackageName: 'twilio_sample',
))
@FlutterApi()
abstract class TwilioBridgeFlutterApi {
  void onTwilioError(Object error);
}
