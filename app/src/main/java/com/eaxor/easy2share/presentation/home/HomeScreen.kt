package com.eaxor.easy2share.presentation.home

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.sharp.OpenInBrowser
import androidx.compose.material.icons.sharp.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaxor.easy2share.R
import com.eaxor.easy2share.ui.theme.DarkOnSurface
import com.eaxor.easy2share.ui.theme.Easy2shareTheme
import com.eaxor.easy2share.ui.theme.LightBackground
import com.eaxor.easy2share.ui.theme.LightSurface
import com.eaxor.easy2share.ui.theme.LightSurfaceDim
import com.eaxor.easy2share.ui.theme.LightWidgetDarkBlue
import com.eaxor.easy2share.ui.theme.LightWidgetLightBlue

@Composable
fun HomeScreen(
    viewModel: HomeViewModel
) {
    Scaffold(
        bottomBar = { BottomNavigationBar() }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightBackground)
        ) {
            val isCompactHeight = maxHeight < 600.dp
            val useHorizontalLayout = maxWidth > maxHeight || maxWidth >= 600.dp
            val horizontalPadding = when {
                maxWidth < 360.dp -> 12.dp
                maxWidth < 600.dp -> 16.dp
                else -> 24.dp
            }

            Column(modifier = Modifier.fillMaxSize()) {
                if (!isCompactHeight) {
                    Header(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp))
                }

                if (useHorizontalLayout) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(horizontalPadding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CreditCard(
                            compact = isCompactHeight,
                            modifier = Modifier.weight(2f)
                        )
                        ActionButtonsRow(
                            compact = isCompactHeight,
                            modifier = Modifier.weight(1f),
                            onShareClipboardClick = { /*TODO*/ },
                            onShareFileClick = { /*TODO*/ }
                        )
                    }
                } else {
                    CreditCard(
                        compact = isCompactHeight,
                        modifier = Modifier.padding(horizontal = horizontalPadding)
                    )
                    ActionButtonsRow(
                        compact = isCompactHeight,
                        modifier = Modifier.padding(
                            horizontal = horizontalPadding,
                            vertical = if (isCompactHeight) 8.dp else 24.dp
                        )
                    )
                }

                TransactionHeader(
                    modifier = Modifier.padding(
                        horizontal = horizontalPadding,
                        vertical = 4.dp
                    )
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(sampleTransactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            modifier = Modifier.padding(
                                horizontal = horizontalPadding,
                                vertical = 8.dp
                            )
                        )
                    }
                }
            }
        }
    }
}

val contentPadding = 24.dp

@Composable
fun Header(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(1.dp))
}

@Composable
fun CreditCard(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val cardHeight = if (compact) 180.dp else 200.dp


    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(LightWidgetDarkBlue.value), // Dark blue
                            Color(LightWidgetLightBlue.value) // Slightly lighter blue
                        )
                    )
                )
                .padding(contentPadding)
        ) {
            // Background Map Placeholder (Assuming it's an image or complex drawing, using a simple color block for now or omitting as it's complex)
            // A realistic implementation would use a subtle background image here.
            
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (compact) 4.dp else 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Sharp.OpenInBrowser), //painterResource(id = R.drawable.ic_sim_card), // Replace with actual sim chip icon if available
                        contentDescription = "Web Browser",
                        tint = LightSurface,
                        modifier = Modifier.size(if (compact) 16.dp else 24.dp)
                    )
                    Text(
                        text = stringResource(R.string.enter_url_title),
                        color = LightSurface,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontSize = if (compact) 10.sp else 12.sp
                    )
                }

                Text(
                    text = "http://192.168.0.25/",
                    color = Color.White,
                    fontSize = if (compact) 20.sp else 24.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = if (compact) 1.sp else 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)


                )
                Spacer(modifier.size(5.dp) )
//                Text(
//                    text = "AR Jonson",
//                    color = Color.White.copy(alpha = 0.8f),
//                    fontSize = if (compact) 12.sp else 14.sp
//                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row() {
                        Column(verticalArrangement = Arrangement.Bottom) {
                            Text(
                                text = "AUTH PIN",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                lineHeight = (if (compact) 12.sp else 14.sp)
                            )
                            Text(
                                text = "854652",
                                color = Color.White,
                                fontSize = if (compact) 12.sp else 14.sp,
                                letterSpacing = if (compact) 4.sp else 8.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
//                        Column {
//                            Text(text = "CVV", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
//                            Text(
//                                text = "6986",
//                                color = Color.White,
//                                fontSize = if (compact) 12.sp else 14.sp,
//                                fontWeight = FontWeight.Medium
//                            )
//                        }
                    }
                    
                    // Mastercard Logo Placeholder
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Sharp.Wifi), //painterResource(id = R.drawable.ic_sim_card), // Replace with actual sim chip icon if available
                            contentDescription = "Wifi",
                            tint = LightSurface,
                            modifier = Modifier.size(if (compact) 24.dp else 32.dp)
                        )
                      // Text(text = "Mastercard", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsRow(
    modifier: Modifier = Modifier,
    onShareClipboardClick: () -> Unit = {},
    onShareFileClick: () -> Unit = {},
    compact: Boolean = false
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
//        ActionButton(iconRes = R.drawable.ic_arrow_up_wallet, label = "Sent")
        ActionButton(
            icon = Icons.Rounded.Share,
            label = stringResource(R.string.share_clipboard_title),
            onClick = onShareClipboardClick,
            compact = compact
        )
        ActionButton(
            icon = Icons.Rounded.FileOpen,
            label = stringResource(R.string.share_files_title),
            onClick = onShareFileClick,
            compact = compact
        )
//        ActionButton(iconRes = R.drawable.ic_cloud_upload_wallet, label = "Topup")
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "actionButtonScale"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) LightWidgetDarkBlue else LightWidgetLightBlue,
        label = "actionButtonBackground"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 52.dp else 60.dp)
                .background(backgroundColor, CircleShape)
                .border(1.dp, LightWidgetDarkBlue, CircleShape),

            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = rememberVectorPainter( icon),
                contentDescription = label,
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(if (compact) 22.dp else 24.dp)
            )
        }
        Spacer(modifier = Modifier.height(if (compact) 4.dp else 8.dp))
        Text(
            text = label,
            color = Color(0xFF1E2022),
            fontSize = if (compact) 14.sp else 16.sp
        )
    }
}

@Composable
fun TransactionHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Transaction",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E2022)
        )
        TextButton(onClick = { /*TODO*/ }) {
            Text(
                text = "Sell All",
                color = Color(0xFF1E3A8A), // Darker Blue color for better contrast
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color(0xFFF3F4F6), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Use specific icons based on type or use a placeholder
            if (transaction.iconRes != null) {
                Icon(
                    painter = painterResource(id = transaction.iconRes),
                    contentDescription = transaction.title,
                    tint = transaction.iconTint ?: Color(0xFF1E2022),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                 Image(
                     painter = ColorPainter(Color.Gray),
                     contentDescription = null,
                     modifier = Modifier.size(24.dp).clip(CircleShape)
                 )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E2022)
            )
            Text(
                text = transaction.subtitle,
                fontSize = 14.sp,
                color = Color(0xFF4A5568)
            )
        }
        Text(
            text = transaction.amount,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.isPositive) Color(0xFF1E3A8A) else Color(0xFF1E2022)
        )
    }
}

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {},
    onCardsClick: () -> Unit = {},
    onStatisticsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(LightWidgetLightBlue.value),
                        Color(LightWidgetDarkBlue.value)

                    ),
                    center = Offset(0.0f,5f)
                )
            )
            .padding(vertical = 10.dp, horizontal = contentPadding),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            iconRes = R.drawable.ic_home_wallet,
            label = "Home",
            isSelected = selectedIndex == 0,
            onClick = {
                selectedIndex = 0
                onHomeClick()
            },
            modifier = Modifier.weight(1f)
        )
        BottomNavItem(
            iconRes = R.drawable.ic_wallet_wallet,
            label = "My Cards",
            isSelected = selectedIndex == 1,
            onClick = {
                selectedIndex = 1
                onCardsClick()
            },
            modifier = Modifier.weight(1f)
        )
//        BottomNavItem(
//            iconRes = R.drawable.ic_pie_chart_wallet,
//            label = "Statistics",
//            isSelected = selectedIndex == 2,
//            onClick = {
//                selectedIndex = 2
//                onStatisticsClick()
//            },
//            modifier = Modifier.weight(1f)
//        )
        BottomNavItem(
            iconRes = R.drawable.ic_settings_wallet,
            label = "Settings",
            isSelected = selectedIndex == 3,
            onClick = {
                selectedIndex = 3
                onSettingsClick()
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BottomNavItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        label = "bottomNavItemScale"
    )
    val containerColor by animateColorAsState(
        targetValue = when {
            isPressed -> Color.White.copy(alpha = 0.9f)
            isSelected -> LightSurfaceDim
            else -> Color.Transparent
        },
        label = "bottomNavItemContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected || isPressed) LightWidgetDarkBlue
        else DarkOnSurface.copy(alpha = 0.8f),
        label = "bottomNavItemContent"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .selectable(
                selected = isSelected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = if (isSelected) 13.sp else 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor,
            maxLines = 1
        )
    }
}

data class Transaction(
    val title: String,
    val subtitle: String,
    val amount: String,
    val isPositive: Boolean,
    val iconRes: Int? = null,
    val iconTint: Color? = null
)

val sampleTransactions = listOf(
    Transaction("Apple Store", "Entertainment", "- $5,99", false, R.drawable.ic_apple_wallet), // Replace with actual apple icon
    Transaction("Spotify", "Music", "- $12,99", false, R.drawable.ic_spotify_wallet, Color(0xFF14853A)), // Replace with actual spotify icon
    Transaction("Money Transfer", "Transaction", "$300", true, R.drawable.ic_download_wallet, Color(0xFF1E2022)),
    Transaction("Grocery", "Shopping", "- $88", false, R.drawable.ic_cart_wallet)
)

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, device = "id:pixel_10_pro")
@Composable
fun HomeScreenPreview() {
    Easy2shareTheme {
        HomeScreen(
            viewModel = HomeViewModel()
        )
    }
}
