// JournalScreen.kt — port of iOS Features/Journal/JournalListView.swift.
//
// Shows entries grouped Today / Yesterday / This Week / Older.
// Each row shows source icon, strength score chip (when present),
// preview text (italic if not yet coached), and a status tag.
package com.katafract.meritmosaic.ui.journal

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.katafract.meritmosaic.data.JournalEntry
import com.katafract.meritmosaic.ui.components.MosaicEmptyState
import com.katafract.meritmosaic.ui.components.MosaicTag
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccent
import com.katafract.meritmosaic.ui.theme.MosaicAmberSecondary
import com.katafract.meritmosaic.ui.theme.MosaicEmerald
import com.katafract.meritmosaic.viewmodel.JournalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    onAddEntry: () -> Unit,
    onEntryClick: (JournalEntry) -> Unit = {},
    viewModel: JournalViewModel = viewModel()
) {
    val entries by viewModel.entries.collectAsState()
    val grouped = viewModel.grouped()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEntry,
                containerColor = MosaicAmberAccent,
                contentColor   = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log a win")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                MosaicEmptyState(
                    title   = "No entries yet",
                    message = "Tap + to log your first proof of progress."
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical   = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            grouped.forEach { (label, sectionEntries) ->
                item(key = "header-$label") {
                    SectionHeader(label)
                }
                items(sectionEntries, key = { it.id }) { entry ->
                    JournalRow(entry = entry, onClick = { onEntryClick(entry) })
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                item(key = "spacer-$label") { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MosaicAmberSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}

@Composable
private fun JournalRow(entry: JournalEntry, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = sourceIcon(entry.source),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))

            entry.strengthScore?.let { score ->
                ScoreChip(score = score)
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = entry.preview,
                style = MaterialTheme.typography.bodyMedium,
                color = if (entry.resumeBullet != null)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))
            ClassificationTag(entry)
        }
    }
}

@Composable
private fun ScoreChip(score: Double) {
    val tint = when {
        score >= 0.8 -> MosaicEmerald
        score >= 0.6 -> Color(0xFF22C55E)
        score >= 0.4 -> MosaicAmberSecondary
        else         -> MosaicAmberAccent
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .padding(0.dp)
    ) {
        MosaicTag(text = "${(score * 100).toInt()}%", tint = tint)
    }
}

@Composable
private fun ClassificationTag(entry: JournalEntry) {
    when {
        entry.hasCoachingQuestion -> MosaicTag("Sharpen", MosaicAmberAccent)
        entry.isCoached           -> MosaicTag("Coached", MosaicEmerald)
        else                      -> MosaicTag("Pending", MosaicAmberSecondary)
    }
}

private fun sourceIcon(source: String): ImageVector = when (source) {
    "siri"   -> Icons.Default.Mic
    "widget" -> Icons.Default.Widgets
    "web"    -> Icons.Default.Public
    "email"  -> Icons.Default.Email
    else     -> Icons.Default.PhoneAndroid
}
