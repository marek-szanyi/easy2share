/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.home

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eaxor.easy2share.R
import com.eaxor.easy2share.domain.repository.ClipboardRepository
import com.eaxor.easy2share.domain.repository.FileRepository
import com.eaxor.easy2share.domain.repository.NetworkRepository
import com.eaxor.easy2share.domain.usecase.GetClipboardContentUseCase
import com.eaxor.easy2share.domain.usecase.GetIpAddressUseCase
import com.eaxor.easy2share.domain.usecase.ShareClipboardContentUseCase
import com.eaxor.easy2share.domain.usecase.ShareFilesUseCase
import com.eaxor.easy2share.presentation.components.BrutalistActionButton
import com.eaxor.easy2share.presentation.components.BrutalistBackdrop
import com.eaxor.easy2share.presentation.components.HazardStripe
import com.eaxor.easy2share.service.WebEngineService
import com.eaxor.easy2share.ui.theme.Easy2shareTheme
import com.eaxor.easy2share.ui.theme.HazardYellow
import com.eaxor.easy2share.ui.theme.IndustrialInk
import com.eaxor.easy2share.ui.theme.IndustrialPaper
import dev.muazkadan.switchycompose.TextSwitch

/** The document picker accepts any file type. */
private const val ANY_MIME_TYPE = "*/*"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onScanQrClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val filePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            viewModel.onFilesSelected(uris.map(Uri::toString))
        }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvents.StartServer -> {
                    WebEngineService.start(context, event.linkKey, event.serverPort)
                }

                HomeEvents.StopServer -> {
                    WebEngineService.stop(context)
                }

                HomeEvents.EncryptionKeyMissing -> {
                    Toast
                        .makeText(
                            context,
                            R.string.home_key_missing_toast,
                            Toast.LENGTH_LONG,
                        ).show()
                }

                HomeEvents.ClipboardShared -> {
                    Toast
                        .makeText(
                            context,
                            R.string.home_clipboard_shared_toast,
                            Toast.LENGTH_SHORT,
                        ).show()
                }

                HomeEvents.ClipboardEmpty -> {
                    Toast
                        .makeText(
                            context,
                            R.string.home_clipboard_empty_toast,
                            Toast.LENGTH_SHORT,
                        ).show()
                }

                HomeEvents.SharingNotActive -> {
                    Toast
                        .makeText(
                            context,
                            R.string.home_sharing_not_active_toast,
                            Toast.LENGTH_LONG,
                        ).show()
                }

                HomeEvents.PickFiles -> {
                    filePickerLauncher.launch(arrayOf(ANY_MIME_TYPE))
                }

                is HomeEvents.FilesShared -> {
                    Toast
                        .makeText(
                            context,
                            context.resources.getQuantityString(
                                R.plurals.home_files_shared_toast,
                                event.fileCount,
                                event.fileCount,
                            ),
                            Toast.LENGTH_SHORT,
                        ).show()
                }

                HomeEvents.FilesShareFailed -> {
                    Toast
                        .makeText(
                            context,
                            R.string.home_files_share_failed_toast,
                            Toast.LENGTH_LONG,
                        ).show()
                }
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        viewModel = viewModel,
        modifier = modifier,
        onShareClipboardClick = viewModel::onShareClipboardClicked,
        onShareFilesClick = viewModel::onShareFilesClicked,
        onScanQrClick = onScanQrClick,
        onSharingToggle = viewModel::onSharingToggled,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
    onShareClipboardClick: () -> Unit = {},
    onShareFilesClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
    onSharingToggle: (Boolean) -> Unit = {},
) {
    val connectedClients = uiState.connectedClients()

    Box(modifier = modifier.fillMaxSize()) {
        BrutalistBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
        ) {
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    ServerLinkPanel(
                        address = viewModel.serverAddress,
                        status = uiState.statusLabel(),
                        shouldAnimate = uiState is HomeUiState.ServerRunning,
                    )
                }

                item {
                    HomeActions(
                        onShareClipboardClick = onShareClipboardClick,
                        onShareFilesClick = onShareFilesClick,
                        onScanQrClick = onScanQrClick,
                    )
                }

                item {
                    ClientListHeader(clientCount = connectedClients.size)
                }

                if (connectedClients.isEmpty()) {
                    item {
                        EmptyClientsPanel()
                    }
                } else {
                    itemsIndexed(
                        items = connectedClients,
                        key = { _, client -> client.id },
                    ) { index, client ->
                        ConnectedClientItem(
                            index = index,
                            client = client,
                        )
                    }
                }
            }

            HomeBottomNavigation(
                uiState = uiState,
                onSharingToggle = onSharingToggle,
            )
        }
    }
}

@Composable
private fun ServerLinkPanel(
    address: String,
    @StringRes status: Int,
    shouldAnimate: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .height(230.dp)
                .wrapContentHeight(),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(x = 8.dp, y = 8.dp)
                    .background(IndustrialInk),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(end = 8.dp, bottom = 8.dp)
                    .background(HazardYellow)
                    .border(4.dp, IndustrialInk),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(10.dp)
                            .background(IndustrialInk),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.home_server_link),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(status),
                    modifier =
                        Modifier
                            .background(IndustrialInk)
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                    color = HazardYellow,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(IndustrialPaper)
                        .border(3.dp, IndustrialInk)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_start_by_visiting_on_your_pc),
                            color = IndustrialInk,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                        )
                        Text(
                            text = stringResource(R.string.home_official_getlink),
                            color = IndustrialInk,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            autoSize = TextAutoSize.StepBased(minFontSize = 1.sp, maxFontSize = 22.sp),
                            maxLines = 1,
                        )
                    }
                }

                Spacer(Modifier.height(9.dp))
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(IndustrialInk),
                )
                Spacer(Modifier.height(9.dp))

                Text(
                    text = stringResource(R.string.home_server_address),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                )
                Text(
                    text = address,
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            HazardStripe(
                shouldBeAnimated = shouldAnimate,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .padding(start = 50.dp, top = 0.dp, end = 20.dp, bottom = 0.dp),
            )
        }
    }
}

@Composable
private fun HomeActions(
    onShareClipboardClick: () -> Unit,
    onShareFilesClick: () -> Unit,
    onScanQrClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        BrutalistActionButton(
            icon = Icons.Rounded.Share,
            label = stringResource(R.string.share_clipboard_title),
            containerColor = HazardYellow,
            onClick = onShareClipboardClick,
            modifier = Modifier.weight(1f),
        )
        BrutalistActionButton(
            icon = Icons.Rounded.UploadFile,
            label = stringResource(R.string.share_files_title),
            containerColor = HazardYellow,
            onClick = onShareFilesClick,
            modifier = Modifier.weight(1f),
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        BrutalistActionButton(
            icon = Icons.Rounded.QrCode,
            label = stringResource(R.string.scan_qr_code),
            containerColor = HazardYellow,
            onClick = onScanQrClick,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ClientListHeader(
    clientCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.home_connected_clients),
            color = IndustrialInk,
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.width(10.dp))
        Spacer(
            modifier =
                Modifier
                    .weight(1f)
                    .height(3.dp)
                    .background(IndustrialInk),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = clientCount.twoDigits(),
            modifier =
                Modifier
                    .background(IndustrialInk)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            color = HazardYellow,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

@Composable
private fun EmptyClientsPanel(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 118.dp)
                .background(IndustrialPaper)
                .border(3.dp, IndustrialInk),
    ) {
        Box(
            modifier =
                Modifier
                    .width(14.dp)
                    .fillMaxHeight()
                    .background(HazardYellow),
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "// 00",
                color = IndustrialInk,
                fontFamily = FontFamily.Monospace,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = stringResource(R.string.home_no_clients),
                color = IndustrialInk,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = stringResource(R.string.home_no_clients_body),
                color = IndustrialInk,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 17.sp,
            )
        }
    }
}

@Composable
private fun ConnectedClientItem(
    index: Int,
    client: ConnectedClient,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(102.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(x = 6.dp, y = 6.dp)
                    .background(IndustrialInk),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(end = 6.dp, bottom = 6.dp)
                    .background(IndustrialPaper)
                    .border(3.dp, IndustrialInk)
                    .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(HazardYellow)
                        .border(2.dp, IndustrialInk),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (index + 1).twoDigits(),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                )
            }
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.displayName,
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = client.address,
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                )
                Text(
                    text =
                        stringResource(
                            R.string.home_client_fingerprint,
                            client.fingerprint.take(12).uppercase(),
                        ),
                    color = IndustrialInk.copy(alpha = 0.72f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    maxLines = 1,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_client_secure),
                modifier =
                    Modifier
                        .background(IndustrialInk)
                        .padding(horizontal = 7.dp, vertical = 5.dp),
                color = HazardYellow,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
            )
        }
    }
}

@Composable
private fun HomeBottomNavigation(
    uiState: HomeUiState,
    onSharingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSharing = uiState is HomeUiState.ServerRunning

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(IndustrialPaper),
    ) {
        HazardStripe(
            shouldBeAnimated = false,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(4.dp, IndustrialInk)
                    .height(20.dp),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(2.dp, IndustrialInk)
                    .padding(1.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            HomeBottomNavigationItem(
                checked = isSharing,
                onCheckedChange = onSharingToggle,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun HomeBottomNavigationItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .wrapContentHeight()
                .background(IndustrialPaper)
                .padding(10.dp)
                .border(2.dp, IndustrialInk),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Unchecked (default) shows "NOT SHARING"; checked shows "SHARING ON".
        TextSwitch(
            modifier = Modifier.padding(vertical = 2.dp),
            checked = checked,
            positiveText = stringResource(R.string.home_start_server),
            negativeText = stringResource(R.string.home_server_stopped),
            color = HazardYellow,
            onCheckedChange = onCheckedChange,
        )
    }
}

private fun HomeUiState.connectedClients(): List<ConnectedClient> = (this as? HomeUiState.ServerRunning)?.connectedClients.orEmpty()

@StringRes
private fun HomeUiState.statusLabel(): Int =
    when (this) {
        HomeUiState.Stopped -> R.string.home_status_stopped
        HomeUiState.PermissionsNeeded -> R.string.home_status_permissions
        HomeUiState.WifiNotEnabled -> R.string.home_status_wifi
        HomeUiState.Scanning -> R.string.home_status_key_required
        is HomeUiState.CanStartServer -> R.string.home_status_ready_to_share
        is HomeUiState.Error -> R.string.home_status_error
        is HomeUiState.ServerRunning -> R.string.home_status_running
        is HomeUiState.AwaitingSessionKey -> R.string.home_status_key_required
        is HomeUiState.ClipboardSharing -> R.string.home_status_clipboard
    }

private fun Int.twoDigits(): String = toString().padStart(2, '0')

private val previewClipboardRepository =
    object : ClipboardRepository {
        override suspend fun getContent(): String? = null

        override suspend fun share(content: String) = Unit
    }

private val previewNetworkRepository =
    object : NetworkRepository {
        override fun getIpAddress(): String = "192.168.0.25"
    }

private val previewFileRepository =
    object : FileRepository {
        override suspend fun share(fileUris: List<String>): Int = 0
    }

private fun previewHomeViewModel() =
    HomeViewModel(
        getClipboardContent = GetClipboardContentUseCase(previewClipboardRepository),
        getIpAddress = GetIpAddressUseCase(previewNetworkRepository),
        shareClipboardContent = ShareClipboardContentUseCase(previewClipboardRepository),
        shareFiles = ShareFilesUseCase(previewFileRepository),
    )

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun HomeScreenPreview() {
    Easy2shareTheme(darkTheme = false) {
        HomeScreen(
            uiState = HomeUiState.Scanning,
            viewModel = previewHomeViewModel(),
//                HomeUiState.ServerRunning(
//                    serverAddress = "192.168.0.25",
//                    serverPort = 8080,
//                    authPin = "854652",
//                    connectedClients = sampleConnectedClients,
//                ),
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, widthDp = 411, heightDp = 891, name = "Empty clients")
@Composable
private fun EmptyHomeScreenPreview() {
    Easy2shareTheme(darkTheme = false) {
        HomeScreen(
            uiState =
                HomeUiState.ServerRunning(
                    serverAddress = "192.168.0.25",
                    serverPort = 8080,
                ),
            viewModel = previewHomeViewModel(),
        )
    }
}
