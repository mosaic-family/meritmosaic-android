// ActivitiesScreen.kt — derived activities (rolled-up wins by category).
//
// Lightweight port for the initial pass: shows seed activities grouped
// by strength label. The full iOS Activities tab includes inline edit,
// strength sliders, parent-child aggregation (per project_meritmosaic
// _v1_1_design_2026_04_26 hybrid clustering work), but those land in
// follow-up PRs.
package com.katafract.meritmosaic.ui.journal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.katafract.meritmosaic.data.Activity
import com.katafract.meritmosaic.data.MockDataSeeder
import com.katafract.meritmosaic.ui.components.MosaicTag
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccent
import com.katafract.meritmosaic.ui.theme.MosaicEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen() {
    // Local seed for now; real impl wires ActivityService.
    val activities = remember { MockDataSeeder.seedActivities }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activities", fontWeight = FontWeight.SemiBold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(activities, key = { it.id }) { activity ->
                ActivityCard(activity)
            }
        }
    }
}

@Composable
private fun ActivityCard(activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MosaicAmberAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.size(12.dp))
            Text(
                activity.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            MosaicTag(
                text = activity.strengthLabel,
                tint = if (activity.strengthScore >= 0.7) MosaicEmerald else MosaicAmberAccent
            )
        }
        activity.description?.let {
            Text(it,
                 style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        LinearProgressIndicator(
            progress  = { activity.strengthScore.toFloat().coerceIn(0f, 1f) },
            color     = MosaicAmberAccent,
            trackColor = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.fillMaxWidth().height(4.dp)
        )
    }
}
