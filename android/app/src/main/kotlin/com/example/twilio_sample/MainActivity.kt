package com.example.twilio_sample

import MakeCallStatus
import TwilioBridgeHostApi
import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.voice.RegistrationException
import com.twilio.voice.RegistrationListener
import com.twilio.voice.Voice

private class TwilioBridgeHostApiImplementation(val context: Context, val activity: Activity): TwilioBridgeHostApi, MultiplePermissionsListener, RegistrationListener {

  companion object {
    private lateinit var audioSwitch: AudioSwitch
    private var permissionApprove = false
    private var accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzZlYmUwYTI3MGE2ODI5YmI1NmRiNmRlYTM2ZmU1N2RhLTE3MDQ3MjczMTciLCJncmFudHMiOnsiaWRlbnRpdHkiOiJTdW5ueUplbm92YVJhbGVpZ2giLCJ2b2ljZSI6eyJpbmNvbWluZyI6eyJhbGxvdyI6dHJ1ZX0sIm91dGdvaW5nIjp7ImFwcGxpY2F0aW9uX3NpZCI6IkFQNDZhNTk3ZjM1OGY4MzM3NTIxODJhMGU5YmExMzg4MDkifX19LCJpYXQiOjE3MDQ3MjczMTcsImV4cCI6MTcwNDczMDkxNywiaXNzIjoiU0s2ZWJlMGEyNzBhNjgyOWJiNTZkYjZkZWEzNmZlNTdkYSIsInN1YiI6IkFDMzhiZmYzZDk1ZDM0MjZkZTIyOWUzNmY5NDY5M2M3MTMifQ.QJY2dqXzp6CcjjjGD8Vp73l7gm7v8mbn7h4KWtmkuFM"
  }
  override fun getLanguage(): String {
    return "Android"
  }

  override fun sendFromNative(callback: (Result<Boolean>) -> Unit) {
    callback(Result.success(true))
  }

  override fun initialize(callback: (Result<Unit>) -> Unit) {
    Log.d("TAG", "This is context! 👉 " + this.context.toString())
    audioSwitch = AudioSwitch(this.context)
    callback(Result.success(Unit));
  }

  override fun deinitialize(callback: (Result<Unit>) -> Unit) {
    callback(Result.success(Unit));
  }

  override fun toggleAudioRoute(toSpeaker: Boolean, callback: (Result<Boolean>) -> Unit) {

    callback(Result.success(true));
  }

  override fun makeCall(callback: (Result<MakeCallStatus>) -> Unit) {
    permissionHandle()
  }

  fun providePermission() : ArrayList<String> {
    var permissionList = ArrayList<String>()
    permissionList.add(Manifest.permission.RECORD_AUDIO)
    permissionList.add(Manifest.permission.MANAGE_OWN_CALLS)
    return permissionList
  }

  fun permissionHandle() {
    Dexter.withActivity(activity).withPermissions(
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.MANAGE_OWN_CALLS
    ).withListener(this).check()
    print(permissionApprove)
    if(permissionApprove) {
      print("Accept Permission")
      registerCallInvites()

    } else {
      print("Denied Permission")
    }
  }

  fun registerCallInvites() {
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener {task ->
      Log.i("asdf", task.toString())
      //accessToken = task.toString()
      com.twilio.voice.Voice.register(accessToken, Voice.RegistrationChannel.FCM, task.getResult(), this)
    })
  }

  override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
    permissionApprove = true
  }

  override fun onPermissionRationaleShouldBeShown(
    permissions: MutableList<PermissionRequest>?,
    token: PermissionToken?
  ) {
    permissionApprove = false
  }

  override fun onRegistered(accessToken: String, fcmToken: String) {
    Log.i("asdfas", fcmToken)
  }

  override fun onError(
    registrationException: RegistrationException,
    accessToken: String,
    fcmToken: String
  ) {
    Log.i("debugErr", registrationException.message.toString())
  }


}

class
MainActivity: FlutterActivity() {
  override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
    super.configureFlutterEngine(flutterEngine)

    val api = TwilioBridgeHostApiImplementation(this.applicationContext, this.activity)
    TwilioBridgeHostApi.setUp(flutterEngine.dartExecutor.binaryMessenger, api)
  }
}
