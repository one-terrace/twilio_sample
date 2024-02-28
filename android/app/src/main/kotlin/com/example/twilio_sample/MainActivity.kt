package com.example.twilio_sample

import MakeCallStatus
import TwilioBridgeHostApi
import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.telecom.DisconnectCause
import android.util.Log
import android.widget.Chronometer
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.twilio.audioswitch.AudioDevice
import com.twilio.audioswitch.AudioSwitch
import com.twilio.voice.Call
import com.twilio.voice.CallException
import com.twilio.voice.CallInvite
import com.twilio.voice.ConnectOptions
import com.twilio.voice.RegistrationException
import com.twilio.voice.RegistrationListener
import com.twilio.voice.Voice
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import java.util.Locale
import java.util.Objects


private class TwilioBridgeHostApiImplementation(val context: Context, val activity: Activity): TwilioBridgeHostApi, MultiplePermissionsListener, RegistrationListener {

  private var voiceBroadcastReceiver: VoiceBroadcastReceiver? = null
  var callListener: Call.Listener = callListener()
  private var isReceiverRegistered = false
  // Empty HashMap, never populated for the Quickstart
  var params: HashMap<String, String> = HashMap()
  private var chronometer: Chronometer? = null
  private var notificationManager: NotificationManager? = null
  private var activeCallInvite: CallInvite? = null
  private var activeCall: Call? = null
  private var activeCallNotificationId = 0
  private var audioSwitch: AudioSwitch = AudioSwitch(context)
  companion object {
    private var permissionApprove = false
    private var ACTION_OUTGOING_CALL: String = "ACTION_OUTGOING_CALL"
    private var ACTION_DISCONNECT_CALL: String = "ACTION_DISCONNECT_CALL"
    private var OUTGOING_CALL_RECIPIENT: String = "OUTGOING_CALL_RECIPIENT"
    private var INCOMING_CALL_INVITE: String = "INCOMING_CALL_INVITE"
    private var ACTION_INCOMING_CALL: String = "ACTION_INCOMING_CALL"
    private var ACTION_INCOMING_CALL_NOTIFICATION: String = "ACTION_INCOMING_CALL_NOTIFICATION"
    private var ACTION_CANCEL_CALL: String = "ACTION_CANCEL_CALL"
    private var ACTION_ACCEPT: String = "ACTION_ACCEPT"
    private var ACTION_FCM_TOKEN: String = "ACTION_FCM_TOKEN"
    private var ACTION_DTMF_SEND: String = "ACTION_DTMF_SEND"
    private var INCOMING_CALL_NOTIFICATION_ID: String = "INCOMING_CALL_NOTIFICATION_ID"
    private var DTMF: String = "DTMF"
    private var accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTS2Y5YTljN2U2NTBkMmFlOGI2ZTE4MGRjMzA4ZjliZWQ3LTE3MDkxMzU2ODgiLCJncmFudHMiOnsiaWRlbnRpdHkiOiJDcmVhdGl2ZUdyYWNpZVdhcnNhdyIsInZvaWNlIjp7ImluY29taW5nIjp7ImFsbG93Ijp0cnVlfSwib3V0Z29pbmciOnsiYXBwbGljYXRpb25fc2lkIjoiQVBkY2I1OGJkODYyOGU2NzQwZmNkNWE3MDRjMGNkYTZiMSJ9fX0sImlhdCI6MTcwOTEzNTY4OCwiZXhwIjoxNzA5MTM5Mjg4LCJpc3MiOiJTS2Y5YTljN2U2NTBkMmFlOGI2ZTE4MGRjMzA4ZjliZWQ3Iiwic3ViIjoiQUMxZDQ3YjAzODU0ODRjZGYzODAxZWY3MzdiM2FiZmI3YSJ9.f4_PuE8SYCbViNnLX9tVmmwE3W6Vq3SsMSdwDb2n2gI"
  }
  override fun getLanguage(): String {
    return "Android"
  }

  override fun sendFromNative(callback: (Result<Boolean>) -> Unit) {
    callback(Result.success(true))
  }

    fun joinCall(accessToken: String, to: String) {
      audioSwitch.start { audioDevices, audioDevice ->
        Toast.makeText(context, audioDevice?.name.toString(), Toast.LENGTH_SHORT).show();
      }
      val selectedDevice: AudioDevice? = audioSwitch.selectedAudioDevice
      val availableAudioDevices: List<AudioDevice> = audioSwitch.availableAudioDevices
      Log.d("asdf", availableAudioDevices.toString())
      audioSwitch.selectDevice(availableAudioDevices[0])
      audioSwitch.activate()
//      val connectOptions = ConnectOptions.Builder(accessToken)
//        .to(to)
//        .build()
      Log.i("JoiningCall", to)
      params.put("to", to)
      val connectOptions: ConnectOptions = ConnectOptions.Builder(Companion.accessToken)
        .params(params)
        .build()
//        .params(params)
      activeCall = Voice.connect(context, connectOptions, callListener)

//      call = Voice.connect(this, connectOptions, object : Call.Listener() {
//        override fun onConnected(call: Call) {
//          android.util.Log.i(TAG, "onConnected: ", call)
//        }
//
//        override fun onConnectFailure(call: Call, error: CallException) {
//          android.util.Log.i(TAG, "onConnectionFailed: ")
//        }
//
//      })
    }
  private fun registerReceiver() {
    if (!isReceiverRegistered) {
      val intentFilter = IntentFilter()
      intentFilter.addAction(ACTION_DISCONNECT_CALL)
      intentFilter.addAction(ACTION_DTMF_SEND)
      intentFilter.addAction(ACTION_INCOMING_CALL)
      intentFilter.addAction(ACTION_OUTGOING_CALL)
      intentFilter.addAction(ACTION_CANCEL_CALL)
      intentFilter.addAction(ACTION_FCM_TOKEN)
      voiceBroadcastReceiver?.let {
        LocalBroadcastManager.getInstance(context).registerReceiver(
          it, intentFilter
        )
      }
      isReceiverRegistered = true
    }
  }

  private inner class VoiceBroadcastReceiver : BroadcastReceiver() {
    @Override
    override fun onReceive(context: Context?, intent: Intent) {
      val action: String? = intent.getAction()
      when (action) {
        ACTION_OUTGOING_CALL -> handleCallRequest(intent)
        ACTION_DISCONNECT_CALL -> activeCall?.disconnect()

        ACTION_DTMF_SEND -> Objects.requireNonNull(intent.getStringExtra(DTMF))
          ?.let { activeCall?.sendDigits(it) }
      }
    }
  }

  private fun handleCallRequest(intent: Intent?) {
    if (intent != null && intent.getAction() != null) {
      val extras: Bundle? = intent.getExtras()
      val recipient: Uri? = extras?.getParcelable(OUTGOING_CALL_RECIPIENT)
      if (recipient != null) {
        params.put("to", recipient.getEncodedSchemeSpecificPart())
      }
      val connectOptions: ConnectOptions = ConnectOptions.Builder(accessToken)
        .params(params)
        .build()
      activeCall = Voice.connect(context, connectOptions, callListener)
    }
  }

  private fun handleIncomingCallIntent(intent: Intent?) {
    if (intent != null && intent.getAction() != null) {
      val action: String = intent.getAction()!!
      activeCallInvite = intent.getParcelableExtra(INCOMING_CALL_INVITE)
      activeCallNotificationId =
        intent.getIntExtra(INCOMING_CALL_NOTIFICATION_ID, 0)
      when (action) {
        ACTION_INCOMING_CALL -> handleIncomingCall()
        ACTION_INCOMING_CALL_NOTIFICATION -> showIncomingCallDialog()
        ACTION_CANCEL_CALL -> handleCancel()
        ACTION_FCM_TOKEN -> registerCallInvites()
        ACTION_ACCEPT -> answer()
        else -> {}
      }
    }
  }

  private fun handleCancel() {
    /*if (alertDialog != null && alertDialog.isShowing()) {
      SoundPoolManager.getInstance(this)!!.stopRinging()
      alertDialog.cancel()
    }*/
  }



  private fun showIncomingCallDialog() {
    SoundPoolManager.getInstance(context)!!.playRinging()
    /*if (activeCallInvite != null) {
      alertDialog = createIncomingCallDialog(
        this@VoiceActivity,
        activeCallInvite,
        answerCallClickListener(),
        cancelCallClickListener()
      )
      alertDialog.show()
    }*/
  }

  private fun handleIncomingCall() {
    if (isAppVisible()) {
      showIncomingCallDialog()
    }
  }

  private fun isAppVisible(): Boolean {
    return ProcessLifecycleOwner.get()
      .lifecycle
      .currentState
      .isAtLeast(Lifecycle.State.STARTED)
  }

  private fun answer() {
    //SoundPoolManager.getInstance(this).stopRinging()
    activeCallInvite?.accept(context, callListener)
    notificationManager?.cancel(activeCallNotificationId)
    //stopService(Intent(context, IncomingCallNotificationService::class.java))
    //setCallUI()
    /*if (alertDialog != null && alertDialog.isShowing()) {
      alertDialog.dismiss()
    }*/
  }

  private fun disconnect() {
    if (activeCall != null) {
      activeCall!!.disconnect()
      activeCall = null
    }
  }

  private fun hold() {
    if (activeCall != null) {
      val hold: Boolean = !activeCall!!.isOnHold()!!
      activeCall?.hold(hold)
//      applyFabState(holdActionFab, hold)
    }
  }

  private fun mute() {
    if (activeCall != null) {
      val mute: Boolean = !activeCall!!.isMuted()
      activeCall!!.mute(mute)
//      applyFabState(muteActionFab, mute)
    }
  }

  private fun callListener(): Call.Listener {
    return object : Call.Listener {
      /*
       * This callback is emitted once before the Call.Listener.onConnected() callback when
       * the callee is being alerted of a Call. The behavior of this callback is determined by
       * the answerOnBridge flag provided in the Dial verb of your TwiML application
       * associated with this client. If the answerOnBridge flag is false, which is the
       * default, the Call.Listener.onConnected() callback will be emitted immediately after
       * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
       * call to emit the onConnected callback only after the call is answered.
       * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
       * twiML response contains a Say verb, then the call will emit the
       * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
       * raised, irrespective of the value of answerOnBridge being set to true or false
       */
      override fun onRinging(call: Call) {
        Log.d(TAG, "Ringing")
        /*
         * When [answerOnBridge](https://www.twilio.com/docs/voice/twiml/dial#answeronbridge)
         * is enabled in the <Dial> TwiML verb, the caller will not hear the ringback while
         * the call is ringing and awaiting to be accepted on the callee's side. The application
         * can use the `SoundPoolManager` to play custom audio files between the
         * `Call.Listener.onRinging()` and the `Call.Listener.onConnected()` callbacks.
         *//*if (BuildConfig.playCustomRingback) {
          SoundPoolManager.getInstance(this).playRinging()
        }*/
      }

      override fun onConnectFailure(call: Call, callException: CallException)  {
        Log.d(TAG, "Connect failure")
        Log.d("sddssd", callException.message.toString())
        audioSwitch.deactivate()
        /*if (BuildConfig.playCustomRingback) {
          SoundPoolManager.getInstance(this).stopRinging()
        }*/
        resetConnectionService()
        val message: String = String.format(
          Locale.US,
          "Call Error: %d, %s",
          callException.getErrorCode(),
          callException.message
        )
        Log.e(TAG, message)
      }

      override fun onConnected(call: Call) {
        audioSwitch.activate()
        /*if (BuildConfig.playCustomRingback) {
          SoundPoolManager.getInstance(context).stopRinging()
        }*/
        Log.d(TAG, "Connected")
        activeCall = call
      }

      override fun onReconnecting(call: Call, callException: CallException) {
        Log.d(TAG, "onReconnecting")
      }


      override fun onReconnected(call: Call) {
        Log.d(TAG, "onReconnected")
      }

      override fun onDisconnected(call: Call, error: CallException?) {
        Log.d(TAG, "Disconnected")
        audioSwitch.deactivate()
        VoiceConnectionService.connection?.setDisconnected(
          DisconnectCause(DisconnectCause.UNKNOWN)
        )
        //resetConnectionService()
        if (error != null) {
          val message: String = String.format(
            Locale.US,
            "Call Error: %d, %s",
            error.getErrorCode(),
            error.message
          )
          Log.e(TAG, message)
//          Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show()
        }
//        resetUI()
      }

      /*
       * currentWarnings: existing quality warnings that have not been cleared yet
       * previousWarnings: last set of warnings prior to receiving this callback
       *
       * Example:
       *   - currentWarnings: { A, B }
       *   - previousWarnings: { B, C }
       *
       * Newly raised warnings = currentWarnings - intersection = { A }
       * Newly cleared warnings = previousWarnings - intersection = { C }
       */
      /*fun onCallQualityWarningsChanged(
        call: Call?,
        currentWarnings: MutableSet<Call.CallQualityWarning?>,
        previousWarnings: MutableSet<Call.CallQualityWarning?>
      ) {
        if (previousWarnings.size > 1) {
          val intersection: HashSet<Call.CallQualityWarning?> = HashSet(currentWarnings)
          currentWarnings.removeAll(previousWarnings)
          intersection.retainAll(previousWarnings)
          previousWarnings.removeAll(intersection)
        }
        val message: String = String.format(
          Locale.US,
          "Newly raised warnings: $currentWarnings Clear warnings $previousWarnings"
        )
        Log.e(TAG, message)
      }*/
    }
  }

  override fun initialize(callback: (Result<Unit>) -> Unit) {
    Log.d("TAG", "This is context! 👉 " + this.context.toString())
    registerReceiver()
    callback(Result.success(Unit));
//    registerCallInvites()
  }

  override fun deinitialize(callback: (Result<Unit>) -> Unit) {
    callback(Result.success(Unit));
  }

  override fun toggleAudioRoute(toSpeaker: Boolean, callback: (Result<Boolean>) -> Unit) {

    callback(Result.success(true));
  }

  override fun makeCall(token: String?, callback: (Result<MakeCallStatus>) -> Unit) {
    permissionHandle()
  }

  override fun hangUp(callback: (Result<Unit>) -> Unit) {
    if(activeCall != null) {
      activeCall!!.disconnect()
    }
  }

  override fun muteUnmute(callback: (Result<Unit>) -> Unit) {
    if(activeCall != null) {
      if(activeCall!!.isMuted) {
        activeCall!!.mute(false)
      } else {
        activeCall!!.mute(true)
      }
    }
  }

  override fun changeAudioOutput(callback: (Result<Unit>) -> Unit) {
    val selectedDevice: AudioDevice? = audioSwitch.selectedAudioDevice
    val availableAudioDevices: List<AudioDevice> = audioSwitch.availableAudioDevices
    Log.d("asdf", availableAudioDevices.toString())
    audioSwitch.selectDevice(availableAudioDevices[1])
    audioSwitch.activate()
  }

  fun providePermission() : ArrayList<String> {
    var permissionList = ArrayList<String>()
    permissionList.add(Manifest.permission.RECORD_AUDIO)
    permissionList.add(Manifest.permission.MANAGE_OWN_CALLS)
    return permissionList
  }

  private fun resetConnectionService() {
    if (null != VoiceConnectionService.connection) {
      VoiceConnectionService.releaseConnection()
    }
  }

  fun permissionHandle() {
    Dexter.withActivity(activity).withPermissions(
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.MANAGE_OWN_CALLS
    ).withListener(this).check()
    print(permissionApprove)
    if(permissionApprove) {
      print("Accept Permission")
//      registerCallInvites()
      joinCall(accessToken, "")

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
