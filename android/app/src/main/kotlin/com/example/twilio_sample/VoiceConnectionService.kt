package com.example.twilio_sample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.telecom.CallAudioState
import android.telecom.Connection
import android.telecom.ConnectionRequest
import android.telecom.ConnectionService
import android.telecom.DisconnectCause
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class VoiceConnectionService : ConnectionService() {
    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val incomingCallConnection = createConnection(request)!!
        incomingCallConnection.setRinging()
        return incomingCallConnection
    }

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle,
        request: ConnectionRequest
    ): Connection {
        val outgoingCallConnection = createConnection(request)!!
        outgoingCallConnection.setDialing()
        return outgoingCallConnection
    }

    private fun createConnection(request: ConnectionRequest): Connection? {
        connection = object : Connection() {
            override fun onStateChanged(state: Int) {
                if (state == STATE_DIALING) {
                    val handler = Handler()
                    handler.post { sendCallRequestToActivity(Constants.ACTION_OUTGOING_CALL) }
                }
            }

            override fun onCallAudioStateChanged(state: CallAudioState) {
                Log.d(
                    TAG,
                    "onCallAudioStateChanged called, current state is $state"
                )
            }

            override fun onPlayDtmfTone(c: Char) {
                Log.d(
                    TAG,
                    "onPlayDtmfTone called with DTMF $c"
                )
                val extras = Bundle()
                extras.putString("DTMF", Character.toString(c))
                connection!!.setExtras(extras)
                val handler = Handler()
                handler.post { sendCallRequestToActivity("ACTION_DTMF_SEND") }
            }

            override fun onDisconnect() {
                super.onDisconnect()
                connection!!.setDisconnected(DisconnectCause(DisconnectCause.LOCAL))
                releaseConnection()
                val handler = Handler()
                handler.post { sendCallRequestToActivity("ACTION_DISCONNECT_CALL") }
            }

            override fun onSeparate() {
                super.onSeparate()
            }

            override fun onAbort() {
                super.onAbort()
                connection!!.setDisconnected(DisconnectCause(DisconnectCause.CANCELED))
                releaseConnection()
            }

            override fun onAnswer() {
                super.onAnswer()
            }

            override fun onReject() {
                super.onReject()
            }

            override fun onPostDialContinue(proceed: Boolean) {
                super.onPostDialContinue(true)
            }
        }
        // setup the origin of the caller
        val recipient = request.extras.getParcelable<Uri>(Constants.OUTGOING_CALL_RECIPIENT)
        if (null != recipient) {
            connection?.setAddress(recipient, TelecomManager.PRESENTATION_ALLOWED)
        } else {
            connection?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        }
        // self managed isn't available before version O
        connection?.setConnectionProperties(Connection.PROPERTY_SELF_MANAGED)
        // set mute capability (for DTMF support?)
        connection?.setConnectionCapabilities(Connection.CAPABILITY_MUTE)
        return connection
    }

    /*
     * Send call request to the VoiceConnectionServiceActivity
     */
    private fun sendCallRequestToActivity(action: String) {
        val intent = Intent(action)
        val extras = Bundle()
        when (action) {
            Constants.ACTION_OUTGOING_CALL -> {
                val address = connection!!.address
                extras.putParcelable(Constants.OUTGOING_CALL_RECIPIENT, address)
                intent.putExtras(extras)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }

            Constants.ACTION_DISCONNECT_CALL -> {
                extras.putInt("Reason", DisconnectCause.LOCAL)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtras(extras)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }

            Constants.ACTION_DTMF_SEND -> {
                val d = connection!!.extras.getString("DTMF")
                extras.putString("DTMF", connection!!.extras.getString("DTMF"))
                intent.putExtras(extras)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }

            else -> {}
        }
    }

    companion object {
        private const val TAG = "VoiceConnectionService"
        var connection: Connection? = null

        fun releaseConnection() {
            if (null != connection) {
                connection!!.destroy()
                connection = null
            }
        }
    }
}