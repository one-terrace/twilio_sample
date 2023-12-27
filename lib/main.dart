import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:twilio_sample/src/twilio_bridge/flutter_apis.g.dart';
import 'package:twilio_sample/src/twilio_bridge/host_apis.g.dart';

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
  String token = '';
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
                  onPressed: () {
                    _api.makeCall().then((value) {
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
