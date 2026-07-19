package com.eaxor.easy2share.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eaxor.easy2share.R
import com.eaxor.easy2share.ui.theme.Easy2shareTheme
import com.eaxor.easy2share.ui.theme.LightBackground
import com.eaxor.easy2share.ui.theme.LightOnSurfaceVariant
import com.eaxor.easy2share.ui.theme.LightOutline
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(LightBackground),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            item {
                Header(modifier = Modifier.padding(horizontal = 5.dp, vertical = 5.dp))
            }
            item {
                CreditCard(modifier = Modifier.padding(horizontal = 24.dp))
            }
            item {
                ActionButtonsRow(modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp))
            }
            item {
                TransactionHeader(modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp))
            }
            items(sampleTransactions) { transaction ->
                TransactionItem(transaction = transaction, modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            }
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(1.dp))
}

@Composable
fun CreditCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
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
                .padding(24.dp)
        ) {
            // Background Map Placeholder (Assuming it's an image or complex drawing, using a simple color block for now or omitting as it's complex)
            // A realistic implementation would use a subtle background image here.
            
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), // Increased bottom padding
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sim_card), // Replace with actual sim chip icon if available
                        contentDescription = "Chip",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_contactless), // Replace with actual wifi/contactless icon
                        contentDescription = "Contactless",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "4562    1122    4595    7852",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "AR Jonson",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column {
                            Text(text = "Expiry Date", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                            Text(text = "24/2000", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Column {
                            Text(text = "CVV", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                            Text(text = "6986", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    // Mastercard Logo Placeholder
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                       Box(modifier = Modifier.width(36.dp).height(20.dp)) {
                           Box(modifier = Modifier.size(20.dp).align(Alignment.CenterStart).background(Color(0xFFEB001B), CircleShape))
                           Box(modifier = Modifier.size(20.dp).align(Alignment.CenterEnd).background(Color(0xFFF79E1B), CircleShape))
                       }
                       Text(text = "Mastercard", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
//        ActionButton(iconRes = R.drawable.ic_arrow_up_wallet, label = "Sent")
        ActionButton(iconRes = R.drawable.ic_arrow_down_wallet, label = "Receive")
        ActionButton(iconRes = R.drawable.ic_dollar_wallet, label = "Loan")
//        ActionButton(iconRes = R.drawable.ic_cloud_upload_wallet, label = "Topup")
    }
}

@Composable
fun ActionButton(iconRes: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 8.dp) // Added padding to avoid cutout
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(LightWidgetLightBlue, CircleShape)
                .border(1.dp, LightWidgetDarkBlue, CircleShape),

            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color(0xFFFFFFFF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = Color(0xFF1E2022), fontSize = 16.sp)
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
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F9FA))
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(iconRes = R.drawable.ic_home_wallet, label = "Home", isSelected = true)
        BottomNavItem(iconRes = R.drawable.ic_wallet_wallet, label = "My Cards", isSelected = false)
        BottomNavItem(iconRes = R.drawable.ic_pie_chart_wallet, label = "Statistics", isSelected = false)
        BottomNavItem(iconRes = R.drawable.ic_settings_wallet, label = "Settings", isSelected = false)
    }
}

@Composable
fun BottomNavItem(iconRes: Int, label: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(bottom = 8.dp) // Added padding to avoid cutout
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF4A5568),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF4A5568)
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
