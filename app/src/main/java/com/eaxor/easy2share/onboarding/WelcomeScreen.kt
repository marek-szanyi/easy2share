package com.eaxor.easy2share.onboarding

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaxor.easy2share.R
import com.eaxor.easy2share.ui.theme.BrandIndigo
import com.eaxor.easy2share.ui.theme.BrandTeal
import com.eaxor.easy2share.ui.theme.BrandViolet
import com.eaxor.easy2share.ui.theme.DarkOnSurfaceVariant
import com.eaxor.easy2share.ui.theme.DarkOutline
import com.eaxor.easy2share.ui.theme.DarkSurfaceDim
import com.eaxor.easy2share.ui.theme.Easy2shareColors
import com.eaxor.easy2share.ui.theme.Easy2shareTheme
import com.eaxor.easy2share.ui.theme.LightBackground
import com.eaxor.easy2share.ui.theme.LightOnSurface
import com.eaxor.easy2share.ui.theme.LightOnSurfaceVariant
import com.eaxor.easy2share.ui.theme.LightOutline
import com.eaxor.easy2share.ui.theme.LightSurface
import com.eaxor.easy2share.ui.theme.LightSurfaceDim
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.typeOf

/**
 * Immutable description of one onboarding page: its copy and the [Canvas]-drawn
 * illustration that heads it. Accent colours are supplied by the theme at render
 * time rather than baked in here.
 */
private data class WelcomePage(
    @StringRes val kicker: Int,
    @StringRes val title: Int,
    @StringRes val body: Int,
    val illustration: @Composable (accent: Color, accentSecondary: Color, modifier: Modifier) -> Unit,
)

private val welcomePages: List<WelcomePage> = listOf(
    WelcomePage(
        kicker = R.string.welcome_1_kicker,
        title = R.string.welcome_1_title,
        body = R.string.welcome_1_body,
        illustration = { accent, accentSecondary, modifier ->
            PhoneToPcIllustration(accent, accentSecondary, modifier)
        },
    ),
    WelcomePage(
        kicker = R.string.welcome_2_kicker,
        title = R.string.welcome_2_title,
        body = R.string.welcome_2_body,
        illustration = { accent, accentSecondary, modifier ->
            WirelessSyncIllustration(accent, accentSecondary, modifier)
        },
    ),
)

/**
 * The first-run welcome experience.
 *
 * A horizontally paged story with a living aurora backdrop. Each page pairs an
 * animated illustration with a short text section, and a set of custom controls
 * (page indicator, Skip and a morphing Next / Get&nbsp;Started button) drives the
 * flow. [onFinished] is invoked when the user skips or reaches the end.
 */
@Composable
fun WelcomeScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pages = welcomePages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // Per-page accent pairs drawn straight from the themed colour scheme.

    val accentPairs = List(pages.size) { index ->
        when (index) {
            0 -> LightOutline to LightSurfaceDim
            1 -> DarkOnSurfaceVariant to LightOnSurfaceVariant
            else -> BrandIndigo to LightBackground
        }
    }

    // Continuous position across the pager, e.g. 0.0 → 1.0 while swiping.
    val position = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val lower = position.toInt().coerceIn(0, pages.lastIndex)
    val upper = (lower + 1).coerceAtMost(pages.lastIndex)
    val blend = (position - lower).coerceIn(0f, 1f)

    // Aurora / control accents smoothly interpolate between neighbouring pages.
    val accent = lerp(accentPairs[lower].first, accentPairs[upper].first, blend)
    val accentSecondary =
        lerp(accentPairs[lower].second, accentPairs[upper].second, blend)

    val isLastPage = pagerState.currentPage == pages.lastIndex

    Box(modifier = modifier.fillMaxSize()) {
        AuroraBackground(
            primary = accentSecondary,
            secondary = BrandTeal,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
        ) {
            // --- Top bar: Skip -------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedContent(
                    targetState = isLastPage,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "skip",
                ) { last ->
                    if (!last) {
                        TextButton(onClick = onFinished) {
                            Text(
                                text = stringResource(R.string.welcome_skip),
                                color = MaterialTheme.colorScheme.outline,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    } else {
                        Spacer(Modifier.height(48.dp))
                    }
                }
            }

            // --- Paged story ---------------------------------------------------
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { pageIndex ->
                val page = pages[pageIndex]
                // Parallax: how far this page is from the viewport centre.
                val pageOffset =
                    (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                WelcomePageContent(
                    page = page,
                    accent = accentPairs[pageIndex].first,
                    accentSecondary = accentPairs[pageIndex].second,
                    pageOffset = pageOffset,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // --- Custom controls ----------------------------------------------
            WelcomeControls(
                pageCount = pages.size,
                position = position,
                accent = accent,
                isLastPage = isLastPage,
                onNext = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                onFinished = onFinished,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp, bottom = 24.dp),
            )
        }
    }
}

/**
 * A single onboarding page: its illustration sits above a left-aligned text
 * section. [pageOffset] drives a subtle horizontal parallax + fade as it scrolls.
 */
@Composable
private fun WelcomePageContent(
    page: WelcomePage,
    accent: Color,
    accentSecondary: Color,
    pageOffset: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .graphicsLayer {
                    // Illustration drifts a little slower than the swipe for depth.
                    translationX = pageOffset * size.width * 0.15f
                    alpha = 1f - (abs(pageOffset) * 0.6f).coerceIn(0f, 1f)
                },
            contentAlignment = Alignment.Center,
        ) {
            page.illustration(accent, accentSecondary, Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .graphicsLayer {
                    // Text trails slightly faster for a layered feel.
                    translationX = pageOffset * size.width * 0.35f
                    alpha = 1f - (abs(pageOffset) * 0.9f).coerceIn(0f, 1f)
                },
        ) {
            Text(
                text = stringResource(page.kicker),
                color = accent,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                style = MaterialTheme.typography.labelLarge,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(page.title),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displaySmall,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(page.body),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

/**
 * Page indicator plus the primary action button, which morphs from *Next* into
 * *Get Started* on the final page. Its container colour follows the live accent.
 */
@Composable
private fun WelcomeControls(
    pageCount: Int,
    position: Float,
    accent: Color,
    isLastPage: Boolean,
    onNext: () -> Unit,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttonColor by animateColorAsState(targetValue = LightOnSurfaceVariant, label = "buttonColor")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PageIndicator(
            pageCount = pageCount,
            position = position,
            accent = accent,
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { if (isLastPage) onFinished() else onNext() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = LightSurface,
            ),
        ) {
            AnimatedContent(
                targetState = isLastPage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "primaryLabel",
            ) { last ->
                Text(
                    text = stringResource(
                        if (last) R.string.welcome_get_started else R.string.welcome_next,
                    ),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

/**
 * A row of dots where the active page is drawn as an elongated pill. The active
 * index and its width interpolate with the pager [position] for a fluid slide.
 */
@Composable
private fun PageIndicator(
    pageCount: Int,
    position: Float,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val active = position.toInt().coerceIn(0, pageCount - 1)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(pageCount) { index ->
            val selected = index == active
            val width by animateDpAsState(
                targetValue = if (selected) 24.dp else 8.dp,
                label = "dotWidth",
            )
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .background(
                        color = if (selected) {
                            accent
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f)
                        },
                        shape = CircleShape,
                    ),
            )
        }
    }
}

/**
 * A slow-moving aurora rendered as overlapping radial-gradient blobs over the
 * themed surface. Blob hues are seeded from the live [primary] / [secondary]
 * accents (so the backdrop shifts as the user pages through the story) plus two
 * fixed accents pulled from the theme's [ExtendedColors].
 */
@Composable
private fun AuroraBackground(
    primary: Color,
    secondary: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "drift",
    )

    val extended = Easy2shareColors.extended
    val blobColors = listOf(
        extended.auroraTop,
        extended.auroraBottom,
        extended.auroraBlobViolet,
        extended.auroraBlobTeal,
    )

    Canvas(
        modifier = modifier.background(
            Brush.verticalGradient(
                colors = listOf(extended.auroraTop, extended.auroraBottom),
            ),
        ),
    ) {
        val w = size.width
        val h = size.height

        blobColors.forEachIndexed { i, color ->
            val phase = drift + i * (PI.toFloat() / 2f)
            val cx = w * (0.5f + 0.34f * cos(phase + i))
            val cy = h * (0.42f + 0.30f * sin(phase * 1.2f + i))
            val radius = size.minDimension * (0.55f + 0.08f * sin(phase).let { it * it })
            val center = Offset(cx, cy)
            // Soft pastel wash so blobs tint — never darken — the light backdrop.
            val tint = lerp(color, Color.White, 0.5f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(tint.copy(alpha = 0.5f), Color.Transparent),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
private fun WelcomeScreenPreview() {
    Easy2shareTheme {
        WelcomeScreen(onFinished = {})
    }
}
