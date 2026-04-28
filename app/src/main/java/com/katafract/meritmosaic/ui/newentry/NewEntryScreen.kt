// NewEntryScreen.kt — port of iOS Features/Journal/JournalEntryView.swift
// + the EnrichmentView coaching flow.
//
// Composing → Saving → Coaching → Refining → Done.
package com.katafract.meritmosaic.ui.newentry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.katafract.meritmosaic.ui.components.MosaicCard
import com.katafract.meritmosaic.ui.theme.MosaicAmberAccent
import com.katafract.meritmosaic.viewmodel.NewEntryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEntryScreen(
    onClose: () -> Unit,
    viewModel: NewEntryViewModel = viewModel()
) {
    val text             by viewModel.text.collectAsState()
    val stage            by viewModel.stage.collectAsState()
    val coachingQuestion by viewModel.coachingQuestion.collectAsState()
    val coachingAnswer   by viewModel.coachingAnswer.collectAsState()
    val polishedBullet   by viewModel.polishedBullet.collectAsState()
    val streakDays       by viewModel.streakDays.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (stage) {
                            NewEntryViewModel.Stage.Composing -> "Log a win"
                            NewEntryViewModel.Stage.Saving    -> "Saving…"
                            NewEntryViewModel.Stage.Coaching  -> "Sharpen"
                            NewEntryViewModel.Stage.Refining  -> "Polishing…"
                            NewEntryViewModel.Stage.Done      -> "Saved"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close")
                    }
                },
                actions = {
                    if (stage == NewEntryViewModel.Stage.Composing) {
                        TextButton(
                            onClick   = { viewModel.save() },
                            enabled   = text.trim().isNotEmpty()
                        ) {
                            Text(
                                "Save",
                                color = if (text.trim().isNotEmpty())
                                    MosaicAmberAccent
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (stage) {
                NewEntryViewModel.Stage.Composing -> ComposingPane(
                    text = text,
                    onTextChange = viewModel::onTextChange
                )
                NewEntryViewModel.Stage.Saving -> CenteredSpinner("Saving…")
                NewEntryViewModel.Stage.Coaching -> CoachingPane(
                    originalText = text,
                    question     = coachingQuestion ?: "",
                    answer       = coachingAnswer,
                    onAnswerChange = viewModel::onCoachingAnswerChange,
                    onSubmit     = viewModel::submitCoachingAnswer
                )
                NewEntryViewModel.Stage.Refining -> CenteredSpinner("Polishing your bullet…")
                NewEntryViewModel.Stage.Done -> DonePane(
                    bullet = polishedBullet,
                    streak = streakDays,
                    onClose = onClose
                )
            }
        }
    }
}

@Composable
private fun ComposingPane(text: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value         = text,
        onValueChange = { if (it.length <= 5000) onTextChange(it) },
        placeholder   = { Text("Tell us one thing you did today…") },
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        keyboardOptions = KeyboardOptions.Default,
        maxLines = Int.MAX_VALUE
    )
    Text(
        text  = "${text.length}/5000",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CenteredSpinner(label: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(color = MosaicAmberAccent)
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CoachingPane(
    originalText: String,
    question: String,
    answer: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    MosaicCard {
        Text("You wrote:", style = MaterialTheme.typography.labelMedium,
             color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(originalText, style = MaterialTheme.typography.bodyMedium)
    }

    MosaicCard {
        Text("One question to sharpen this", style = MaterialTheme.typography.labelMedium,
             color = MosaicAmberAccent, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(question, style = MaterialTheme.typography.titleMedium)
    }

    OutlinedTextField(
        value         = answer,
        onValueChange = onAnswerChange,
        placeholder   = { Text("A number, a person, a result…") },
        modifier      = Modifier.fillMaxWidth().height(120.dp)
    )

    Button(
        onClick = onSubmit,
        enabled = answer.trim().isNotEmpty(),
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MosaicAmberAccent),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text("Polish my bullet", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DonePane(bullet: String?, streak: Int, onClose: () -> Unit) {
    MosaicCard {
        Text("Saved", style = MaterialTheme.typography.labelMedium,
             color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text("Resume bullet", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            bullet ?: "—",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    if (streak > 0) {
        MosaicCard {
            Text("$streak-day streak", style = MaterialTheme.typography.titleMedium,
                 color = MosaicAmberAccent, fontWeight = FontWeight.SemiBold)
            Text("Keep it going — tomorrow's entry earns a streak bonus.",
                 style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    Button(
        onClick = onClose,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MosaicAmberAccent)
    ) {
        Text("Done", fontWeight = FontWeight.SemiBold)
    }
}
