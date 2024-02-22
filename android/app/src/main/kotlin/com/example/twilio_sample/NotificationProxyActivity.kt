package com.example.twilio_sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle


class NotificationProxyActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
        finish()
    }

    private fun handleIntent(intent: Intent) {
        val action = intent.action
        if (action != null) {
            val serviceIntent =
                Intent(intent).setClass(this, IncomingCallNotificationService::class.java)
            val appIntent = Intent(intent).setClass(this, MainActivity::class.java)
            when (action) {
                Constants.ACTION_INCOMING_CALL, Constants.ACTION_ACCEPT -> {
                    launchService(serviceIntent)
                    launchMainActivity(appIntent)
                }

                else -> launchService(serviceIntent)
            }
        }
    }

    private fun launchMainActivity(intent: Intent) {
        try {
            val launchIntent = Intent(intent)
            launchIntent.setClass(this, MainActivity::class.java)
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchService(intent: Intent) {
        val launchIntent = Intent(intent)
        launchIntent.setClass(this, IncomingCallNotificationService::class.java)
        startService(launchIntent)
    }
}