/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.scanning

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val TAG = "CameraScanner"

@Composable
fun rememberScannerController(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onQrCodes: (qrCodes: List<DetectedQr>, sourceWidth: Int, sourceHeight: Int) -> Unit,
): ScannerController {
    val latestOnQrCodes by rememberUpdatedState(onQrCodes)
    return remember(context, lifecycleOwner) {
        ScannerController(
            context,
            lifecycleOwner,
            onQrCodes = { qrCodes, sourceWidth, sourceHeight ->
                latestOnQrCodes(qrCodes, sourceWidth, sourceHeight)
            },
        )
    }
}

@Stable
class ScannerController(
    context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onQrCodes: (
        qrCodes: List<DetectedQr>,
        sourceWidth: Int,
        sourceHeight: Int,
    ) -> Unit,
) {
    private val appContext = context.applicationContext

    private val providerScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    var surfaceRequest: SurfaceRequest? by mutableStateOf(null)
        private set

    private var cameraProvider: ProcessCameraProvider? = null

    private val analysisExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private val scanner = BarcodeScanning.getClient()

    private val preview =
        Preview.Builder().build().apply {
            setSurfaceProvider { request ->
                surfaceRequest = request
            }
        }

    private val imageAnalysis =
        ImageAnalysis
            .Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(analysisExecutor, ::analyze)
            }

    @SuppressLint("UnsafeOptInUsageError")
    private fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        val inputImage =
            InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner
            .process(inputImage)
            .addOnSuccessListener { qrCodes ->
                onQrCodes(
                    qrCodes.map { DetectedQr(it.rawValue ?: "", it.boundingBox) },
                    imageProxy.width,
                    imageProxy.height,
                )
            }.addOnFailureListener { exc ->
                Log.e(TAG, "Barcode scanning failed", exc)
            }.addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun openCamera() {
        providerScope.launch {
            val provider = ProcessCameraProvider.getInstance(appContext).await()
            cameraProvider = provider

            val cameraSelector =
                CameraSelector
                    .Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis,
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }
    }

    fun closeCamera() {
        cameraProvider?.unbindAll()
    }

    fun release() {
        closeCamera()
        providerScope.cancel()
        scanner.close()
        analysisExecutor.shutdown()
    }
}
