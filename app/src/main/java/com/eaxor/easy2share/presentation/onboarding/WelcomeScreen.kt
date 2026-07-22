/*
 * Copyright (c) 2026 Eaxor llc.
 * SPDX-License-Identifier: MIT
 * Licensed under the MIT License. See LICENSE file in the project root for full license information.
 */
package com.eaxor.easy2share.presentation.onboarding

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Stateful entry point for the welcome experience.
 *
 * Binds the [WelcomeViewModel] to the stateless [WelcomeScreen] content
 * following unidirectional data flow.
 */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    WelcomeScreen(
        uiState = uiState,
        onPageShown = viewModel::onPageShown,
        onFinished = viewModel::completeOnboarding,
        modifier = modifier,
    )
}

/**
 * Stateless first-run welcome experience.
 *
 * The industrial presentation deliberately uses exposed structure, hard borders,
 * high-contrast utility colors, and abrupt rectangular controls.
 */
@Composable
fun WelcomeScreen(
    uiState: WelcomeUiState,
    onPageShown: (Int) -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = uiState.pages
    if (pages.isEmpty()) return

    val pagerState =
        rememberPagerState(
            initialPage = uiState.currentPageIndex.coerceIn(0, pages.lastIndex),
            pageCount = { pages.size },
        )
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState, onPageShown) {
        snapshotFlow { pagerState.currentPage }.collect { onPageShown(it) }
    }

    val position =
        (
            pagerState.currentPage + pagerState.currentPageOffsetFraction
        ).coerceIn(0f, pages.lastIndex.toFloat())
    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(modifier = modifier.fillMaxSize()) {
        BrutalistBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
        ) {
            WelcomeHeader(
                currentPage = pagerState.currentPage,
                pageCount = pages.size,
                isLastPage = isLastPage,
                onFinished = onFinished,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
            )

            HorizontalPager(
                state = pagerState,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
            ) { pageIndex ->
                val pageOffset =
                    (pagerState.currentPage - pageIndex) +
                        pagerState.currentPageOffsetFraction

                WelcomePageContent(
                    page = pages[pageIndex],
                    pageNumber = pageIndex + 1,
                    pageCount = pages.size,
                    pageOffset = pageOffset,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            WelcomeControls(
                pageCount = pages.size,
                position = position,
                isLastPage = isLastPage,
                onNext = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec =
                                tween(
                                    durationMillis = 220,
                                    easing = LinearEasing,
                                ),
                        )
                    }
                },
                onFinished = onFinished,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 20.dp),
            )
        }
    }
}

@Composable
private fun WelcomeHeader(
    currentPage: Int,
    pageCount: Int,
    isLastPage: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.heightIn(min = 44.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.app_name).uppercase(),
            modifier =
                Modifier
                    .background(IndustrialInk)
                    .padding(horizontal = 10.dp, vertical = 7.dp),
            color = HazardYellow,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = "${(currentPage + 1).twoDigits()} / ${pageCount.twoDigits()}",
            color = IndustrialInk,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(12.dp))

        AnimatedContent(
            targetState = isLastPage,
            transitionSpec = {
                slideInVertically(
                    animationSpec = tween(durationMillis = 90, easing = LinearEasing),
                    initialOffsetY = { it },
                ) togetherWith
                    slideOutVertically(
                        animationSpec = tween(durationMillis = 90, easing = LinearEasing),
                        targetOffsetY = { -it },
                    )
            },
            label = "skip",
            content = { Log.d("WelcomeScreen", "isLastPage: $it") },
        )
    }
}

@Composable
private fun WelcomePageContent(
    page: WelcomePage,
    pageNumber: Int,
    pageCount: Int,
    pageOffset: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .graphicsLayer {
                    translationX = pageOffset * size.width * 0.12f
                    translationY = abs(pageOffset) * size.height * 0.015f
                },
    ) {
        BrutalistIllustrationPanel(
            title = page.frameTitle,
            illustration = page.illustration,
            pageNumber = pageNumber,
            pageCount = pageCount,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
        )

        Spacer(Modifier.height(22.dp))

//        Text(
//            text = stringResource(page.kicker).uppercase(),
//            modifier =
//                Modifier
//                    .background(HazardYellow)
//                    .border(2.dp, IndustrialInk)
//                    .padding(horizontal = 9.dp, vertical = 5.dp),
//            color = IndustrialInk,
//            fontFamily = FontFamily.Monospace,
//            fontSize = 12.sp,
//            fontWeight = FontWeight.Black,
//            letterSpacing = 2.sp,
//        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(page.title),
            color = IndustrialInk,
            fontWeight = FontWeight.Black,
            fontSize = 40.sp,
            lineHeight = 40.sp,
            letterSpacing = (-1).sp,
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier =
                Modifier
                    .width(64.dp)
                    .height(4.dp)
                    .background(IndustrialInk),
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(page.body),
            color = IndustrialInk,
            fontFamily = FontFamily.Monospace,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun BrutalistIllustrationPanel(
    title: Int,
    illustration: WelcomeIllustration,
    pageNumber: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .offset(x = 6.dp, y = 6.dp)
                    .background(IndustrialInk),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
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
                    text = stringResource(title).uppercase(),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "[${pageNumber.twoDigits()}:${pageCount.twoDigits()}]",
                    modifier =
                        Modifier
                            .background(IndustrialInk)
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                    color = HazardYellow,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(
                            border = BorderStroke(3.dp, IndustrialInk),
                            shape = RectangleShape,
                        ).background(IndustrialPaper)
                        .padding(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                PageIllustration(
                    illustration = illustration,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            HazardStripe(
                (pageNumber != pageCount),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(50.dp, 0.dp, 35.dp, 0.dp)
                        .height(24.dp),
            )
        }
    }
}

@Composable
private fun PageIllustration(
    illustration: WelcomeIllustration,
    modifier: Modifier = Modifier,
) {
    when (illustration) {
        WelcomeIllustration.FILE_PRESS -> {
            Text(
                text = stringResource(R.string.welcome_message),
                color = IndustrialInk,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Black,
                fontSize = 50.sp,
                lineHeight = 50.sp,
                letterSpacing = (-0.5).sp,
            )
        }

        WelcomeIllustration.ENCRYPTION_GATE -> {
            EncryptionGateIllustration(
                accent = IndustrialInk,
                accentSecondary = HazardYellow,
                paper = IndustrialPaper,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun WelcomeControls(
    pageCount: Int,
    position: Float,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColor by animateColorAsState(
        targetValue = if (isLastPage) IndustrialInk else HazardYellow,
        animationSpec = snap(),
        label = "buttonColor",
    )
    val buttonContentColor by animateColorAsState(
        targetValue = if (isLastPage) HazardYellow else IndustrialInk,
        animationSpec = snap(),
        label = "buttonContentColor",
    )
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonPressOffset by animateDpAsState(
        targetValue = if (isPressed) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 55, easing = LinearEasing),
        label = "buttonPressOffset",
    )

    Column(modifier = modifier) {
        PageIndicator(
            pageCount = pageCount,
            position = position,
        )
        Spacer(Modifier.height(14.dp))

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(62.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .offset(x = 6.dp, y = 6.dp)
                        .background(IndustrialInk),
            )
            Button(
                onClick = { if (isLastPage) onFinished() else onNext() },
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .offset { IntOffset(x = buttonPressOffset.toPx().toInt(), y = buttonPressOffset.toPx().toInt()) },
                shape = RectangleShape,
                border = BorderStroke(3.dp, IndustrialInk),
                interactionSource = interactionSource,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = buttonContentColor,
                    ),
                elevation =
                    ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        disabledElevation = 0.dp,
                    ),
                contentPadding = PaddingValues(horizontal = 18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AnimatedContent(
                        targetState = isLastPage,
                        transitionSpec = {
                            slideInVertically(
                                animationSpec = tween(durationMillis = 90, easing = LinearEasing),
                                initialOffsetY = { it },
                            ) togetherWith
                                slideOutVertically(
                                    animationSpec = tween(durationMillis = 90, easing = LinearEasing),
                                    targetOffsetY = { -it },
                                )
                        },
                        label = "primaryLabel",
                    ) { last ->
                        Text(
                            text =
                                stringResource(
                                    if (last) {
                                        R.string.welcome_get_started
                                    } else {
                                        R.string.welcome_next
                                    },
                                ).uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                        )
                    }
                    Text(
                        text = "→",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    pageCount: Int,
    position: Float,
    modifier: Modifier = Modifier,
) {
    val active = position.roundToInt().coerceIn(0, pageCount - 1)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == active
            Box(
                modifier =
                    Modifier
                        .width(if (selected) 38.dp else 26.dp)
                        .height(24.dp)
                        .background(if (selected) HazardYellow else IndustrialPaper)
                        .border(2.dp, IndustrialInk),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (index + 1).toString(),
                    color = IndustrialInk,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                )
            }
        }

        Spacer(
            modifier =
                Modifier
                    .weight(1f)
                    .height(3.dp)
                    .background(IndustrialInk),
        )
        Box(
            modifier =
                Modifier
                    .size(12.dp)
                    .background(HazardYellow)
                    .border(2.dp, IndustrialInk),
        )
    }
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')

@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WelcomeScreenPreview() {
    Easy2shareTheme(darkTheme = false) {
        WelcomeScreen(
            uiState = WelcomeUiState(pages = welcomePages),
            onPageShown = {},
            onFinished = {},
        )
    }
}
