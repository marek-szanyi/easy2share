/*
 * Copyright (c) 2026 Eaxor LLC.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import com.eaxor.easy2share.Constants
import com.eaxor.easy2share.MainActivity
import com.eaxor.easy2share.R
import com.eaxor.easy2share.data.webengine.TlsWebsocketEngine
import com.eaxor.easy2share.data.webengine.buildWebsocketEngine
import com.eaxor.easy2share.data.webengine.resetWebEngineState
import kotlinx.coroutines.Dispatchers

/**
 * Foreground service hosting the embedded WebEngine server,
 * so sharing keeps running while the app is backgrounded.
 *
 * Started after a successful QR scan or when the user flips the home screen
 * switch to "SHARING ON", and fully disposed when sharing is switched off.
 */
class WebEngineService : Service() {
    private var webEngine: TlsWebsocketEngine? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        if (intent?.action == ACTION_START) {
            val linkKey = intent.getByteArrayExtra(EXTRA_LINK_KEY) ?: ByteArray(0)
            val port = intent.getIntExtra(EXTRA_PORT, Constants.DEFAULT_PORT)

            startForeground(
                NOTIFICATION_ID,
                buildNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
            startWebEngine(port, linkKey)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        stopWebEngine()
        super.onDestroy()
    }

    private fun startWebEngine(
        port: Int,
        linkKey: ByteArray,
    ) {
        if (webEngine != null) stopWebEngine()
        webEngine =
            buildWebsocketEngine(Dispatchers.IO, port, linkKey).also {
                it.start(wait = false)
            }
        Log.i(TAG, "WebEngine embedded server started on port $port")
    }

    private fun stopWebEngine() {
        webEngine?.stop(SERVER_STOP_GRACE_MILLIS, SERVER_STOP_TIMEOUT_MILLIS)
        resetWebEngineState()
        webEngine = null
        Log.i(TAG, "WebEngine embedded server stopped")
    }

    private fun buildNotification(): Notification {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.sharing_notification_channel_name),
                NotificationManager.IMPORTANCE_LOW,
            ),
        )

        val contentIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE,
            )

        return Notification
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_contactless)
            .setContentTitle(getString(R.string.sharing_notification_title))
            .setContentText(getString(R.string.sharing_notification_text))
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val TAG = "WebEngineService"
        private const val CHANNEL_ID = "web_engine_sharing"
        private const val NOTIFICATION_ID = 1001
        private const val ACTION_START = "com.eaxor.easy2share.action.START_SHARING"
        private const val EXTRA_LINK_KEY = "extra_link_key"
        private const val EXTRA_PORT = "extra_port"
        private const val SERVER_STOP_GRACE_MILLIS = 500L
        private const val SERVER_STOP_TIMEOUT_MILLIS = 1_000L

        /** Starts the foreground service and the embedded web server. */
        fun start(
            context: Context,
            linkKey: ByteArray,
            port: Int = Constants.DEFAULT_PORT,
        ) {
            val intent =
                Intent(context, WebEngineService::class.java)
                    .setAction(ACTION_START)
                    .putExtra(EXTRA_LINK_KEY, linkKey)
                    .putExtra(EXTRA_PORT, port)
            context.startForegroundService(intent)
        }

        /** Disposes the foreground service together with the embedded web server. */
        fun stop(context: Context) {
            context.stopService(Intent(context, WebEngineService::class.java))
        }
    }
}
