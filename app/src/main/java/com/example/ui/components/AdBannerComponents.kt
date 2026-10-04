package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PurplePrimary

@Composable
fun TopFixedAdBanner(
    bannerId: String,
    isEnabled: Boolean,
    isTestMode: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isEnabled) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp) // Fixed height to never obscure or jump
            .background(Color(0xFFF1F5F9))
            .border(width = 0.5.dp, color = Color(0xFFCBD5E1))
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("top_fixed_ad_banner"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE2E8F0))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = if (isTestMode) "إعلان تجريبي" else "إعلان",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Google AdMob Banner (علوي ثابت)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PurplePrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "ID: ${bannerId.take(24)}...",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "معلومات الإعلان",
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun BottomFixedAdBanner(
    bannerId: String,
    isEnabled: Boolean,
    isTestMode: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isEnabled) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp) // Fixed height
            .background(Color(0xFF0F172A))
            .border(width = 0.5.dp, color = Color(0xFF334155))
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .testTag("bottom_fixed_ad_banner"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF59E0B))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "AdMob",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "بانر جوجل إعلاني سفلي ثابت - تطبيق مليونير",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = if (isTestMode) "وضع الاختبار نشط • انقر لربح نقاط إضافية" else "شاهد العروض الحصرية",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Text(
                text = "سفلي",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
