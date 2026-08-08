/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.scanning

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eaxor.easy2share.R
import com.eaxor.easy2share.presentation.components.BrutalistActionButton
import com.eaxor.easy2share.ui.theme.HazardYellow

@Composable
fun ScannerScreen(
    onScanFinished: () -> Unit,
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.initialize() }
    LaunchedEffect(uiState) {
        if (uiState is ScannerUiState.Finished) {
            viewModel.onScanResultHandled()
            onScanFinished()
        }
    }

    CameraScaffold(permissions = listOf(android.Manifest.permission.CAMERA)) {
        when (val state = uiState) {
            ScannerUiState.Initial -> {
                LoadingView()
            }

            is ScannerUiState.Error -> {
                ErrorView(errorMessage = state.errorMessage, onRetry = viewModel::resetError)
            }

            is ScannerUiState.Scanning -> {
                ScanningContent(
                    state = state,
                    onQrCodes = viewModel::setQrCodes,
                )
            }

            is ScannerUiState.Finished -> {
                Unit
            }
        }
    }
}

@Composable
private fun BoxScope.ScanningContent(
    state: ScannerUiState.Scanning,
    onQrCodes: (List<DetectedQr>, Int, Int) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller =
        rememberScannerController(
            context = context,
            lifecycleOwner = lifecycleOwner,
            onQrCodes = onQrCodes,
        )

    DisposableEffect(lifecycleOwner, controller) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_CREATE, Lifecycle.Event.ON_RESUME -> {
                        controller.openCamera()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        controller.closeCamera()
                    }

                    else -> {}
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            controller.release()
        }
    }

    controller.surfaceRequest?.let { request ->
        CameraXPreview(surfaceRequest = request)
    }

    QrOverlay(
        qrCodes = state.qrCodes,
        sourceWidth = state.sourceWidth,
        sourceHeight = state.sourceHeight,
        modifier = Modifier.fillMaxSize(),
    )

//    val latestValue = state.qrCodes.firstOrNull { it.value.isNotEmpty() }?.value
//    if (latestValue != null) {
//        BarcodeValueChip(
//            value = latestValue,
//            modifier =
//                Modifier
//                    .align(Alignment.BottomCenter)
//                    .padding(32.dp),
//        )
//    }

//    ViewfinderTopBar(
//        title = null,
//        onClose = onBack,
//        closeIcon = Icons.AutoMirrored.Filled.ArrowBack,
//    )
}

@Composable
private fun QrOverlay(
    qrCodes: List<DetectedQr>,
    sourceWidth: Int,
    sourceHeight: Int,
    modifier: Modifier = Modifier,
) {
    if (sourceWidth <= 0 || sourceHeight <= 0) return

    val strokeWidthPx = with(LocalDensity.current) { 3.dp.toPx() }
    val cornerRadiusPx = with(LocalDensity.current) { 8.dp.toPx() }

    Canvas(modifier = modifier) {
        val scaleX = size.width / sourceWidth
        val scaleY = size.height / sourceHeight
        qrCodes.forEach { barcode ->
            val bounds = barcode.bounds ?: return@forEach
            drawRoundRect(
                color = Color.Red,
                topLeft =
                    Offset(
                        x = bounds.left.dp.toPx(),
                        y = bounds.exactCenterY().dp.toPx(),
                    ),
                size =
                    Size(
                        width = bounds.width().dp.toPx() * scaleX,
                        height = bounds.height().dp.toPx() * scaleX,
                    ),
                cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                style = Stroke(width = strokeWidthPx),
            )
        }
    }
}

/**
 * Renders a CameraX [SurfaceRequest] into a [CameraXViewfinder] with tap-to-focus support.
 *
 * @param onTapToFocus receives the tap point transformed into surface coordinates together with the
 * surface width/height, ready to hand to `CameraControl.startFocusAndMetering`.
 * @param onFocusTap receives the raw tap offset (in view pixels) so a sample can show a focus ring.
 */
@Composable
fun CameraXPreview(
    surfaceRequest: SurfaceRequest,
    modifier: Modifier = Modifier,
    onTapToFocus: ((surfaceCoords: Offset, width: Float, height: Float) -> Unit)? = null,
    onFocusTap: ((Offset) -> Unit)? = null,
) {
    val coordinateTransformer = remember { MutableCoordinateTransformer() }
    CameraXViewfinder(
        surfaceRequest = surfaceRequest,
        coordinateTransformer = coordinateTransformer,
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(surfaceRequest) {
                    detectTapGestures(
                        onTap = { offset ->
                            val surfaceCoords = with(coordinateTransformer) { offset.transform() }
                            onTapToFocus?.invoke(
                                surfaceCoords,
                                surfaceRequest.resolution.width.toFloat(),
                                surfaceRequest.resolution.height.toFloat(),
                            )
                            onFocusTap?.invoke(offset)
                        },
                    )
                },
    )
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

/** Full-screen error message with a retry action. */
@Composable
fun ErrorView(
    errorMessage: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Text(
            text = errorMessage ?: "Unknown Error",
            color = Color.Red,
            modifier = Modifier.align(Alignment.Center),
        )
        BrutalistActionButton(
            icon = Icons.Rounded.QrCode,
            label = stringResource(R.string.try_again),
            containerColor = HazardYellow,
            onClick = onRetry,
        )
    }
}

/**
 * Full-screen message shown when a sample cannot run on the current device (e.g. the camera lacks a
 * required capability such as extensions or high-speed recording).
 */
@Composable
fun UnsupportedView(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = message, color = Color.White)
    }
}
