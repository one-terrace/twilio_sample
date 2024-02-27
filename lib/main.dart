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
  String token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzZlYmUwYTI3MGE2ODI5YmI1NmRiNmRlYTM2ZmU1N2RhLTE3MDkwMTYyNDYiLCJncmFudHMiOnsiaWRlbnRpdHkiOiJXb25kZXJmdWxVbHlzc2VzTmV2aXMiLCJ2b2ljZSI6eyJpbmNvbWluZyI6eyJhbGxvdyI6dHJ1ZX0sIm91dGdvaW5nIjp7ImFwcGxpY2F0aW9uX3NpZCI6IkFQNDZhNTk3ZjM1OGY4MzM3NTIxODJhMGU5YmExMzg4MDkifX19LCJpYXQiOjE3MDkwMTYyNDYsImV4cCI6MTcwOTAxOTg0NiwiaXNzIjoiU0s2ZWJlMGEyNzBhNjgyOWJiNTZkYjZkZWEzNmZlNTdkYSIsInN1YiI6IkFDMzhiZmYzZDk1ZDM0MjZkZTIyOWUzNmY5NDY5M2M3MTMifQ.r5N9xgzl3aM8PB_ZQBUDPQj2Ol0V3PfHuhifw0q9MHQ';
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
                        debugPrint('${value}aung myin');
                      }).onError((error, stackTrace) {
                        debugPrint(error.toString());
                      });
                  },
                  child: const Text('Call me'),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
