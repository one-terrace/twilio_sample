import 'dart:convert';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:twilio_sample/src/twilio_bridge/flutter_apis.g.dart';
import 'package:twilio_sample/src/twilio_bridge/host_apis.g.dart';
import 'package:http/http.dart' as http;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> implements TwilioBridgeFlutterApi {
  final TwilioBridgeHostApi _api = TwilioBridgeHostApi();
  String language = '';
  String token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2Y5YTljN2U2NTBkMmFlOGI2ZTE4MGRjMzA4ZjliZWQ3LTE3MDkxMzU2ODgiLCJncmFudHMiOnsiaWRlbnRpdHkiOiJDcmVhdGl2ZUdyYWNpZVdhcnNhdyIsInZvaWNlIjp7ImluY29taW5nIjp7ImFsbG93Ijp0cnVlfSwib3V0Z29pbmciOnsiYXBwbGljYXRpb25fc2lkIjoiQVBkY2I1OGJkODYyOGU2NzQwZmNkNWE3MDRjMGNkYTZiMSJ9fX0sImlhdCI6MTcwOTEzNTY4OCwiZXhwIjoxNzA5MTM5Mjg4LCJpc3MiOiJTS2Y5YTljN2U2NTBkMmFlOGI2ZTE4MGRjMzA4ZjliZWQ3Iiwic3ViIjoiQUMxZDQ3YjAzODU0ODRjZGYzODAxZWY3MzdiM2FiZmI3YSJ9.f4_PuE8SYCbViNnLX9tVmmwE3W6Vq3SsMSdwDb2n2gI';
  bool isCall = false;
  late TextEditingController _fcmTokenTxtFieldController;

  void initNotification() async {
    Permission.notification.request();
    await Firebase.initializeApp();

    await FirebaseMessaging.instance.requestPermission(provisional: true);

    final token = await FirebaseMessaging.instance.getToken();

    setState(() {
      _fcmTokenTxtFieldController.text = token ?? '';
    });
  }

  @override
  void initState() {
    _fcmTokenTxtFieldController = TextEditingController();

    super.initState();
    TwilioBridgeFlutterApi.setup(this);
    initNotification();

    _api.initialize();

    _api.getLanguage().then((value) {
      setState(() {
        debugPrint(value.toString());
        language = value;
      });
    }).onError((error, stackTrace) {
      debugPrint(error.toString());
    });
  }

  @override
  void onTwilioError(Object error) {
    debugPrint(error.toString());
  }

  @override
  void dispose() {
    _api.deinitialize();
    _fcmTokenTxtFieldController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: SizedBox(
          width: double.maxFinite,
          child: Padding(
            padding: const EdgeInsets.all(20),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Text('Came from $language'),
                const SizedBox(height: 16),
                TextField(
                  controller: _fcmTokenTxtFieldController,
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () async {
//                     final response = await http.get(Uri.parse("https://twilio.oneterrace-tech.site/call-admin"));
//                     Map<String, dynamic> body = jsonDecode(response.body);
//                     final token = body["token"];
//
//                     if (response.statusCode == 200) {
//
//                     }
                      _api.makeCall(token).then((value) {
                        setState(() {
                          isCall = true;
                        });
                      }).onError((error, stackTrace) {
                        debugPrint(error.toString());
                      });
                  },
                  child: const Text('Call me'),
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () async {
//                     final response = await http.get(Uri.parse("https://twilio.oneterrace-tech.site/call-admin"));
//                     Map<String, dynamic> body = jsonDecode(response.body);
//                     final token = body["token"];
//
//                     if (response.statusCode == 200) {
//
//                     }
                    await _api.muteUnmute().then((value) {
                      print('mute lite unmute lite');
                    }).onError((error, stackTrace) {
                      debugPrint(error.toString());
                    });
                  },
                  child: const Text('Mute/Unmute'),
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () async {
//                     final response = await http.get(Uri.parse("https://twilio.oneterrace-tech.site/call-admin"));
//                     Map<String, dynamic> body = jsonDecode(response.body);
//                     final token = body["token"];
//
//                     if (response.statusCode == 200) {
//
//                     }
                    await _api.hangUp().then((value) {
                    }).onError((error, stackTrace) {
                      debugPrint(error.toString());
                    });
                  },
                  child: const Text('Hang Up'),
                ),
                const SizedBox(height: 16),
                ElevatedButton(
                  onPressed: () async {
//                     final response = await http.get(Uri.parse("https://twilio.oneterrace-tech.site/call-admin"));
//                     Map<String, dynamic> body = jsonDecode(response.body);
//                     final token = body["token"];
//
//                     if (response.statusCode == 200) {
//
//                     }
                    await _api.changeAudioOutput().then((value) {
                      print('change output');
                    }).onError((error, stackTrace) {
                      debugPrint(error.toString());
                    });
                  },
                  child: const Text('Change audio output'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
