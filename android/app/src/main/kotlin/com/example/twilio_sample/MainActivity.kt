package com.example.twilio_sample

import TwilioBridgeHostApi
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.FlutterPlugin

private class TwilioBridgeHostApiImplementation: TwilioBridgeHostApi {
  override fun getLanguage(): String {
    return "Android"
  }

  override fun sendFromNative(callback: (Result<Boolean>) -> Unit) {
    callback(Result.success(true))
  }
}

class MainActivity: FlutterActivity() {
  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)

    val api = TwilioBridgeHostApiImplementation()
    TwilioBridgeHostApi.setUp(flutterEngine.dartExecutor.binaryMessenger, api)
  }
}
