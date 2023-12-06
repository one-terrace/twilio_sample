import 'package:flutter/material.dart';
import 'package:twilio_sample/src/twilio_bridge/host_apis.g.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final TwilioBridgeHostApi _api = TwilioBridgeHostApi();
  String language = '';

  @override
  void initState() {
    super.initState();

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
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: Text('Came from $language'),
        ),
      ),
    );
  }
}
