import 'package:pigeon/pigeon.dart';

const path = 'twilio_bridge/host_apis.g';
const mobilePath = 'twilio_bridge/HostAPIs';

enum MakeCallStatus {
  success,
  anotherCall,
  fail,
}

@ConfigurePigeon(PigeonOptions(
  dartOut: 'lib/src/twilio_bridge/host_apis.g.dart',
  dartOptions: DartOptions(),
  cppOptions: CppOptions(namespace: 'twilio_sample'),
  cppHeaderOut: 'windows/runner/twilio_bridge/host_apis.g.h',
  cppSourceOut: 'windows/runner/twilio_bridge/host_apis.g.cpp',
  kotlinOut: 'android/app/src/main/kotlin/com/example/twilio_sample/twilio_bridge/HostAPIs.g.kt',
  kotlinOptions: KotlinOptions(errorClassName: 'HostAPIError'),
  javaOut: 'android/app/src/main/java/io/flutter/plugins/twilio_bridge/HostAPIs.java',
  javaOptions: JavaOptions(),
  swiftOut: 'ios/Runner/HostAPIs.g.swift',
  swiftOptions: SwiftOptions(),
  objcHeaderOut: 'macos/Runner/HostAPIs.g.h',
  objcSourceOut: 'macos/Runner/HostAPIs.g.m',
  // Set this to a unique prefix for your plugin or application, per Objective-C naming conventions.
  objcOptions: ObjcOptions(prefix: 'PGN'),
  dartPackageName: 'twilio_sample',
))
@HostApi()
abstract class TwilioBridgeHostApi {
  String getLanguage();

  @async
  bool sendFromNative();

  @async
  void initialize();

  @async
  void deinitialize();

  @async
  bool toggleAudioRoute(bool toSpeaker);

  @async
  MakeCallStatus makeCall();
}
