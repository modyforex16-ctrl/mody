package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.viewmodel.FinancialViewModel
import com.example.viewmodel.Screen

@Composable
fun PointsMarketplaceScreen(
    viewModel: FinancialViewModel
) {
    val userPoints by viewModel.adminSettings.userPoints.collectAsStateWithLifecycle()
    val isSearching by viewModel.isMarketplaceLoading.collectAsStateWithLifecycle()
    val marketResult by viewModel.marketplaceResult.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("JOBS") } // "JOBS", "PROJECTS", "LEARNING"
    var searchQuery by remember { mutableStateOf("") }
    var insufficientPointsMessage by remember { mutableStateOf(false) }

    val serviceCost = 25 // 25 points per AI search/plan

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
        ) {
            // Header Card: Points Balance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("points_marketplace_banner"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF6B46C1), Color(0xFF4338CA))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "سوق خدمات الذكاء بالنقاط 💎",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "استثمر نقاطك لاكتشاف وظائف، مشاريع، وتعلم ذاتي",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = AmberGold,
                                modifier = Modifier.clickable { viewModel.navigateTo(Screen.Rewards) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$userPoints نقطة",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Services Navigation (الوظائف الذكية، مشاريع أونلاين، التعلم الذاتي)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = activeTab == "JOBS",
                        onClick = {
                            activeTab = "JOBS"
                            searchQuery = "أريد وظائف أونلاين عن بعد تناسب مهاراتي في الكتابة أو خدمة العملاء"
                        },
                        label = { Text("بحث وظائف ذكي", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurplePrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("service_jobs_tab")
                    )

                    FilterChip(
                        selected = activeTab == "PROJECTS",
                        onClick = {
                            activeTab = "PROJECTS"
                            searchQuery = "فكرة مشروع أونلاين برأس مال 500 ريال بدون خبرة مسبقة"
                        },
                        label = { Text("مشاريع أونلاين", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("service_projects_tab")
                    )

                    FilterChip(
                        selected = activeTab == "LEARNING",
                        onClick = {
                            activeTab = "LEARNING"
                            searchQuery = "خطة تعلم ذاتي لإتقان مهارة عالية الدخل (التجارة الإلكترونية والـ AI)"
                        },
                        label = { Text("تعلم ذاتي", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGold,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f).testTag("service_learning_tab")
                    )
                }
            }

            // Query Input Box
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (activeTab) {
                                    "JOBS" -> Icons.Default.Work
                                    "PROJECTS" -> Icons.Default.Lightbulb
                                    else -> Icons.Default.School
                                },
                                contentDescription = null,
                                tint = PurplePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when (activeTab) {
                                    "JOBS" -> "صف مهاراتك أو ما تبحث عنه بالوظائف:"
                                    "PROJECTS" -> "حدد ميزانيتك ومجال المشروع المطلوب:"
                                    else -> "ما هي المهارة التي ترغب بتعلمها ذاتياً؟"
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("اكتب بالتفصيل مثلاً: أريد وظائف أونلاين، أو اقتراحات مشروع متجر إلكتروني...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("marketplace_query_input"),
                            minLines = 2,
                            maxLines = 4
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "تكلفة الخدمة: 25 نقطة من رصيدك",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )

                            Button(
                                onClick = {
                                    if (userPoints < serviceCost) {
                                        insufficientPointsMessage = true
                                    } else {
                                        insufficientPointsMessage = false
                                        viewModel.executeMarketplaceService(
                                            type = activeTab,
                                            query = searchQuery.ifBlank { "أفضل اقتراحات لتطوير الدخل" }
                                        )
                                    }
                                },
                                enabled = !isSearching,
                                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("execute_marketplace_service_btn")
                            ) {
                                if (isSearching) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("جاري البحث...", fontSize = 12.sp)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ابحث بذكاء (خصم 25⭐)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            if (insufficientPointsMessage) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "نقاطك غير كافية! شاهد إعلانات سريعة لكسب +50 نقطة فوراً.",
                                fontSize = 12.sp,
                                color = RoseRed,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { viewModel.navigateTo(Screen.Rewards) },
                                colors = ButtonDefaults.buttonColors(containerColor = RoseRed),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("كسب نقاط", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Results Card
            if (marketResult.isNotBlank()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("marketplace_result_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تقرير المستشار المهني والريادي الذكي:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = marketResult,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
