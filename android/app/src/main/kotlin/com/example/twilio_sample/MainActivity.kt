package com.example.twilio_sample

import MakeCallStatus
import TwilioBridgeHostApi
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine

private class TwilioBridgeHostApiImplementation(val context: Context): TwilioBridgeHostApi {
  override fun getLanguage(): String {
    return "Android"
  }

  override fun sendFromNative(callback: (Result<Boolean>) -> Unit) {
    callback(Result.success(true))
  }

  override fun initialize(callback: (Result<Unit>) -> Unit) {
    Log.d("TAG", "This is context! 👉 " + this.context.toString())
    callback(Result.success(Unit));
  }

  override fun deinitialize(callback: (Result<Unit>) -> Unit) {
    callback(Result.success(Unit));
  }

  override fun toggleAudioRoute(toSpeaker: Boolean, callback: (Result<Boolean>) -> Unit) {
    callback(Result.success(true));
  }

  override fun makeCall(callback: (Result<MakeCallStatus>) -> Unit) {

  }
}

class MainActivity: FlutterActivity() {
  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)

    val api = TwilioBridgeHostApiImplementation(this.applicationContext)
    TwilioBridgeHostApi.setUp(flutterEngine.dartExecutor.binaryMessenger, api)
  }
}
