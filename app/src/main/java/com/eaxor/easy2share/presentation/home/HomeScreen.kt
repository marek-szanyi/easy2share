/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.home

import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.sharp.Devices
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eaxor.easy2share.R
import com.eaxor.easy2share.presentation.components.BrutalistBackdrop
import com.eaxor.easy2share.presentation.components.HazardStripe
import com.eaxor.easy2share.ui.theme.Easy2shareTheme
import com.eaxor.easy2share.ui.theme.HazardYellow
import com.eaxor.easy2share.ui.theme.IndustrialInk
import com.eaxor.easy2share.ui.theme.IndustrialPaper

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
    onShareClipboardClick: () -> Unit = {},
    onShareFileClick: () -> Unit = {},
    onScanQrClick: () -> Unit = {},
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
                        address = uiState.serverAddress(),
                        authPin = uiState.authPin(),
                        status = uiState.statusLabel(),
                        shouldAnimate = uiState is HomeUiState.ServerRunning,
                        detail = (uiState as? HomeUiState.Error)?.message,
                    )
                }

                item {
                    HomeActions(
                        onShareClipboardClick = onShareClipboardClick,
                        onShareFileClick = onShareFileClick,
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

            HomeBottomNavigation()
        }
    }
}

@Composable
private fun ServerLinkPanel(
    address: String,
    authPin: String,
    @StringRes status: Int,
    shouldAnimate: Boolean,
    detail: String?,
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
                Spacer(Modifier.height(9.dp))
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(IndustrialInk),
                )
                Spacer(Modifier.height(9.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_auth_pin),
                            color = IndustrialInk,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                        )
                        Text(
                            text = authPin,
                            color = IndustrialInk,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                        )
                    }
                }

                if (detail != null) {
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = detail,
                        color = IndustrialInk,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
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
    onShareFileClick: () -> Unit,
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
            icon = Icons.Rounded.FileOpen,
            label = stringResource(R.string.share_files_title),
            containerColor = IndustrialPaper,
            onClick = onShareFileClick,
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
private fun BrutalistActionButton(
    icon: ImageVector,
    label: String,
    containerColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 55, easing = LinearEasing),
        label = "homeActionPressOffset",
    )

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(5.dp, 10.dp, 0.dp, 10.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(end = 0.dp, bottom = 5.dp)
                    .offset { IntOffset(x = pressOffset.toPx().toInt(), y = pressOffset.toPx().toInt()) }
                    .background(containerColor)
                    .border(3.dp, IndustrialInk)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    ).padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = label.uppercase(),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 16.sp,
                )
                Box(
                    modifier =
                        Modifier
                            .width(38.dp)
                            .height(3.dp)
                            .background(IndustrialInk),
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = IndustrialInk,
                modifier = Modifier.size(30.dp),
            )
        }
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
private fun HomeBottomNavigation(modifier: Modifier = Modifier) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val items =
        listOf(
            HomeNavigationItem(Icons.Sharp.Home, R.string.home_nav_home),
            HomeNavigationItem(Icons.Sharp.Devices, R.string.home_nav_clients),
            HomeNavigationItem(Icons.Sharp.Settings, R.string.home_nav_settings),
        )

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
                    .height(20.dp),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .border(3.dp, IndustrialInk)
                    .padding(7.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            items.forEachIndexed { index, item ->
                HomeBottomNavigationItem(
                    item = item,
                    selected = selectedIndex == index,
                    onClick = { selectedIndex = index },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun HomeBottomNavigationItem(
    item: HomeNavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = stringResource(item.label)

    Row(
        modifier =
            modifier
                .height(48.dp)
                .background(if (selected) HazardYellow else IndustrialPaper)
                .border(2.dp, IndustrialInk)
                .selectable(
                    selected = selected,
                    role = Role.Tab,
                    onClick = onClick,
                ).padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = label,
            tint = IndustrialInk,
            modifier = Modifier.size(20.dp),
        )
        if (selected) {
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = IndustrialInk,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }
    }
}

private data class HomeNavigationItem(
    val icon: ImageVector,
    @StringRes val label: Int,
)

private fun HomeUiState.connectedClients(): List<ConnectedClient> = (this as? HomeUiState.ServerRunning)?.connectedClients.orEmpty()

private fun HomeUiState.serverAddress(): String =
    when (this) {
        is HomeUiState.ServerRunning -> {
            val address =
                if (serverAddress.startsWith("http://") || serverAddress.startsWith("https://")) {
                    serverAddress
                } else {
                    "http://$serverAddress"
                }
            "$address:$serverPort/"
        }

        else -> {
            "--"
        }
    }

private fun HomeUiState.authPin(): String =
    when (this) {
        is HomeUiState.ServerRunning -> authPin ?: "------"
        is HomeUiState.AwaitingAuthentications -> pin
        else -> "------"
    }

@StringRes
private fun HomeUiState.statusLabel(): Int =
    when (this) {
        HomeUiState.Stopped -> R.string.home_status_stopped
        HomeUiState.PermissionsNeeded -> R.string.home_status_permissions
        HomeUiState.WifiNotEnabled -> R.string.home_status_wifi
        is HomeUiState.Error -> R.string.home_status_error
        is HomeUiState.ServerRunning -> R.string.home_status_running
        is HomeUiState.AwaitingAuthentications -> R.string.home_status_waiting
        is HomeUiState.ClipboardSharing -> R.string.home_status_clipboard
    }

private fun Int.twoDigits(): String = toString().padStart(2, '0')

private val sampleConnectedClients =
    listOf(
        ConnectedClient(
            id = "studio-pc",
            displayName = "STUDIO-PC",
            address = "192.168.0.14",
            fingerprint = "A9F2-7C31-18D4",
        ),
        ConnectedClient(
            id = "workstation",
            displayName = "WORKSTATION-02",
            address = "192.168.0.23",
            fingerprint = "74B1-0E6A-993C",
        ),
    )

@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun HomeScreenPreview() {
    Easy2shareTheme(darkTheme = false) {
        HomeScreen(
            uiState =
                HomeUiState.ServerRunning(
                    serverAddress = "192.168.0.25",
                    serverPort = 8080,
                    authPin = "854652",
                    connectedClients = sampleConnectedClients,
                ),
        )
    }
}

@Preview(showBackground = true, widthDp = 411, heightDp = 891, name = "Empty clients")
@Composable
private fun EmptyHomeScreenPreview() {
    Easy2shareTheme(darkTheme = false) {
        HomeScreen(
            uiState =
                HomeUiState.ServerRunning(
                    serverAddress = "192.168.0.25",
                    serverPort = 8080,
                    authPin = "854652",
                ),
        )
    }
}
