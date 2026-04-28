// CreditStoreScreen.kt — port of iOS Features/Credits/CreditsView.swift.
//
// Hero balance card, "earn free credits" callout, 4 credit packs, cost
// breakdown, restore-purchases footer.
package com.katafract.meritmosaic.ui.credits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.katafract.meritmosaic.data.CreditPack
import com.katafract.meritmosaic.data.PromptBalance
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccent
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccentDeep
import com.katafract.meritmosaic.viewmodel.CreditsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditStoreScreen(
    onClose: () -> Unit,
    viewModel: CreditsViewModel = viewModel()
) {
    val balance              by viewModel.balance.collectAsState()
    val packs                by viewModel.packs.collectAsState()
    val purchaseInProgress   by viewModel.purchaseInProgress.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Credits", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { BalanceHero(balance) }
            item { EarnFreeCallout() }
            item { Text("Credit Packs", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold) }
            // Pack grid is 2 columns × N rows. Originally a LazyVerticalGrid with
            // a fixed Modifier.height(420.dp), but a fixed height inside a LazyColumn
            // clips on small screens / dynamic-text users when packs taller than the
            // height add up. Switched to a Column-of-chunked-Rows so each row
            // grows to fit its tallest card and the LazyColumn handles overall scroll.
            items(packs.chunked(2)) { rowPacks ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowPacks.forEach { pack ->
                        Box(modifier = Modifier.weight(1f)) {
                            PackCard(
                                pack = pack,
                                inProgress = purchaseInProgress == pack.productId,
                                disabled   = purchaseInProgress != null,
                                onPurchase = { viewModel.purchase(pack.productId) }
                            )
                        }
                    }
                    // If odd-count last row, pad the empty cell so the remaining
                    // card doesn't stretch the full width.
                    if (rowPacks.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            item { CostBreakdown() }
            item {
                OutlinedButton(
                    onClick = { /* restore purchases */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restore Purchases",
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun BalanceHero(balance: PromptBalance?) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(listOf(MosaicAmberAccent, MosaicAmberAccentDeep))
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Your Balance",
                     color = Color.White.copy(alpha = 0.85f),
                     style = MaterialTheme.typography.bodyMedium)
                Text(
                    text  = "${balance?.promptBalance ?: 0}",
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("credits",
                     color = Color.White.copy(alpha = 0.85f),
                     style = MaterialTheme.typography.bodyMedium)
            }

            // Streak badge top-right
            balance?.takeIf { it.streakDays > 0 }?.let {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            "${it.streakDays}-day streak",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Stats row beneath the hero
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCell("Today",       "+${balance?.earnedToday ?: 0}")
            StatCell("Entries",     "${balance?.totalEntries ?: 0}")
            StatCell("Best Streak", "${balance?.longestStreak ?: 0}d")
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(label, style = MaterialTheme.typography.bodySmall,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EarnFreeCallout() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            tint = MosaicAmberAccent,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.size(12.dp))
        Column {
            Text("Earn free credits daily", style = MaterialTheme.typography.titleSmall,
                 fontWeight = FontWeight.SemiBold)
            Text("Every journal entry earns 1–4 credits. Streak bonuses after 7 days.",
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PackCard(
    pack: CreditPack,
    inProgress: Boolean,
    disabled: Boolean,
    onPurchase: () -> Unit
) {
    Box {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "${pack.credits}",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Text("credits", style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = onPurchase,
                enabled = !disabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MosaicAmberAccent),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (inProgress) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(pack.priceLabel,
                         style = MaterialTheme.typography.titleSmall,
                         fontWeight = FontWeight.SemiBold)
                }
            }
        }

        if (pack.isBestValue) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(MosaicAmberAccent)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("Best Value",
                     color = Color.White,
                     fontSize = 10.sp,
                     fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CostBreakdown() {
    val items = listOf(
        Triple("Journal entry",       "1–4 credits", Icons.Default.Edit),
        Triple("AI classification",   "1 credit",    Icons.Default.Star),
        Triple("Resume generation",   "25 credits",  Icons.Default.Description),
        Triple("Rec letter AI draft", "25 credits",  Icons.Default.Mail)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text("What credits buy",
             style = MaterialTheme.typography.titleMedium,
             fontWeight = FontWeight.SemiBold,
             modifier = Modifier.padding(16.dp))
        items.forEachIndexed { idx, item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = item.third,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.size(12.dp))
                Text(item.first, style = MaterialTheme.typography.bodyMedium,
                     modifier = Modifier.weight(1f))
                Text(item.second,
                     style = MaterialTheme.typography.bodyMedium,
                     fontWeight = FontWeight.SemiBold,
                     color = MosaicAmberAccent)
            }
            if (idx != items.lastIndex) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(start = 48.dp)
                )
            }
        }
    }
}
