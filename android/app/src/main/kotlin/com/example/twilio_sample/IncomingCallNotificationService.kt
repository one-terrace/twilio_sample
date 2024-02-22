package com.example.twilio_sample

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.telecom.Connection
import android.telecom.DisconnectCause
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.twilio.voice.CallInvite
import com.example.twilio_sample.VoiceConnectionService


class IncomingCallNotificationService() : Service() {
    private var phoneAccountHandle: PhoneAccountHandle? = null
    private var telecomManager: TelecomManager? = null
    override fun onCreate() {
        super.onCreate()
        // register telecom account info
        val appContext: Context = this.applicationContext
        val appName: String = "asdf"
        phoneAccountHandle = PhoneAccountHandle(
            ComponentName(appContext, VoiceConnectionService::class.java),
            appName
        )
        telecomManager = appContext.getSystemService(TELECOM_SERVICE) as TelecomManager
        val phoneAccount: PhoneAccount = PhoneAccount.Builder(phoneAccountHandle, appName)
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
            .setCapabilities(PhoneAccount.CAPABILITY_SELF_MANAGED)
            .build()
        telecomManager!!.registerPhoneAccount(phoneAccount)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val action: String? = intent.action
        if (action != null) {
            val callInvite: CallInvite? =
                intent.getParcelableExtra<CallInvite>(Constants.INCOMING_CALL_INVITE)
            val notificationId: Int = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0)
            when (action) {
                Constants.ACTION_INCOMING_CALL -> handleIncomingCall(callInvite, notificationId)
                Constants.ACTION_OUTGOING_CALL -> handleOutgoingCall(intent)
                Constants.ACTION_ACCEPT -> accept(callInvite, notificationId)
                Constants.ACTION_REJECT -> reject(callInvite)
                Constants.ACTION_CANCEL_CALL -> handleCancelledCall(intent)
                else -> {}
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotification(
        callInvite: CallInvite?,
        notificationId: Int,
        channelImportance: Int
    ): Notification {
        val intent: Intent = Intent(this, NotificationProxyActivity::class.java)
        intent.action = Constants.ACTION_INCOMING_CALL_NOTIFICATION
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        intent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE)
        /*
         * Pass the notification id and call sid to use as an identifier to cancel the
         * notification later
         */
        val extras: Bundle = Bundle()
        extras.putString(Constants.CALL_SID_KEY, callInvite!!.callSid)
        return buildNotification(
            callInvite.from + " is calling.",
            pendingIntent,
            extras,
            callInvite,
            notificationId,
            createChannel(channelImportance)
        )
    }

    /**
     * Build a notification.
     *
     * @param text          the text of the notification
     * @param pendingIntent the body, pending intent for the notification
     * @param extras        extras passed with the notification
     * @return the builder
     */
    private fun buildNotification(
        text: String, pendingIntent: PendingIntent, extras: Bundle,
        callInvite: CallInvite?,
        notificationId: Int,
        channelId: String
    ): Notification {
        val rejectIntent: Intent =
            Intent(applicationContext, IncomingCallNotificationService::class.java)
        rejectIntent.action = Constants.ACTION_REJECT
        rejectIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
        rejectIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        val piRejectIntent: PendingIntent = PendingIntent.getService(
            applicationContext,
            notificationId,
            rejectIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val acceptIntent: Intent = Intent(applicationContext, NotificationProxyActivity::class.java)
        acceptIntent.action = Constants.ACTION_ACCEPT
        acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite)
        acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId)
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val piAcceptIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            acceptIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val builder: Notification.Builder = Notification.Builder(
            applicationContext, channelId
        )
            .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setCategory(Notification.CATEGORY_CALL)
            .setExtras(extras)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_menu_delete,
                "decline",
                piRejectIntent
            )
            .addAction(android.R.drawable.ic_menu_call, "answer", piAcceptIntent)
            .setFullScreenIntent(pendingIntent, true)
        return builder.build()
    }

    private fun createChannel(channelImportance: Int): String {
        var callInviteChannel: NotificationChannel = NotificationChannel(
            Constants.VOICE_CHANNEL_HIGH_IMPORTANCE,
            "Primary Voice Channel", NotificationManager.IMPORTANCE_HIGH
        )
        var channelId: String = Constants.VOICE_CHANNEL_HIGH_IMPORTANCE
        if (channelImportance == NotificationManager.IMPORTANCE_LOW) {
            callInviteChannel = NotificationChannel(
                Constants.VOICE_CHANNEL_LOW_IMPORTANCE,
                "Primary Voice Channel", NotificationManager.IMPORTANCE_LOW
            )
            channelId = Constants.VOICE_CHANNEL_LOW_IMPORTANCE
        }
        callInviteChannel.lightColor = Color.GREEN
        callInviteChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(callInviteChannel)
        return channelId
    }

    private fun accept(callInvite: CallInvite?, notificationId: Int) {
        endForeground()
        // notify telephony service of call approval
        VoiceConnectionService.connection?.setActive()
    }

    private fun reject(callInvite: CallInvite?) {
        endForeground()
        callInvite!!.reject(applicationContext)
        // notify telephony service of call rejection
        val cxn: Connection? = VoiceConnectionService.connection
        if (null != cxn) {
            cxn.setDisconnected(DisconnectCause(DisconnectCause.REJECTED))
            VoiceConnectionService.releaseConnection()
        }
    }

    private fun handleCancelledCall(intent: Intent) {
        endForeground()
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun handleIncomingCall(callInvite: CallInvite?, notificationId: Int) {
        setCallInProgressNotification(callInvite, notificationId)
        // register new call with telecom subsystem
        val inviteBundle: Bundle = Bundle(CallInvite::class.java.classLoader)
        inviteBundle.putParcelable(Constants.INCOMING_CALL_INVITE, callInvite)
        val callInfo: Bundle = Bundle()
        val uri: Uri = Uri.fromParts(PhoneAccount.SCHEME_TEL, callInvite!!.from, null)
        callInfo.putBundle(Constants.INCOMING_CALL_INVITE, inviteBundle)
        callInfo.putParcelable(TelecomManager.EXTRA_INCOMING_CALL_ADDRESS, uri)
        callInfo.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
        callInfo.putInt(TelecomManager.EXTRA_INCOMING_VIDEO_STATE, VideoProfile.STATE_AUDIO_ONLY)
        telecomManager!!.addNewIncomingCall(phoneAccountHandle, callInfo)
    }

    private fun handleOutgoingCall(intent: Intent) {
        // place a call with the telecom subsystem
        val extra: Bundle? = intent.extras
        if (null != extra) {
            val callInfo: Bundle = Bundle()
            val recipient: Uri? = extra.getParcelable<Uri>(Constants.OUTGOING_CALL_RECIPIENT)
            val permissionsState: Int = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.MANAGE_OWN_CALLS
            )
            callInfo.putParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, extra)
            callInfo.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            callInfo.putInt(
                TelecomManager.EXTRA_START_CALL_WITH_VIDEO_STATE,
                VideoProfile.STATE_AUDIO_ONLY
            )
            if (permissionsState == PackageManager.PERMISSION_GRANTED) {
                telecomManager!!.placeCall(recipient, callInfo)
            }
        }
    }

    private fun endForeground() {
        stopForeground(true)
    }

    private fun setCallInProgressNotification(callInvite: CallInvite?, notificationId: Int) {
        if (isAppVisible) {
            Log.i(TAG, "setCallInProgressNotification - app is visible.")
            startForeground(
                notificationId,
                createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_LOW)
            )
        } else {
            Log.i(TAG, "setCallInProgressNotification - app is NOT visible.")
            startForeground(
                notificationId,
                createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    private val isAppVisible: Boolean
        private get() = ProcessLifecycleOwner
            .get()
            .lifecycle
            .currentState
            .isAtLeast(Lifecycle.State.STARTED)

    companion object {
        private val TAG: String = IncomingCallNotificationService::class.java.simpleName
    }
}