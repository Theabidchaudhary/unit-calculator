package com.orwyx.unitcalculator.ui.screens.meters

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.orwyx.unitcalculator.ui.components.LabeledTextField
import com.orwyx.unitcalculator.ui.components.ProviderPicker
import com.orwyx.unitcalculator.ui.theme.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeterEditScreen(onBack: () -> Unit, viewModel: MeterEditViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) { if (state.saved) onBack() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(state.title, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back") } },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            val input = state.input
            LabeledTextField(value = input.name, onValueChange = { v -> viewModel.update { it.copy(name = v) } }, label = "Meter name", error = state.errors.name)
            LabeledTextField(value = input.referenceNumber, onValueChange = { v -> viewModel.update { it.copy(referenceNumber = v) } }, label = "Reference number", error = state.errors.referenceNumber, numeric = true)
            ProviderPicker(selectedId = input.providerId, onSelected = { v -> viewModel.update { it.copy(providerId = v) } }, modifier = Modifier.fillMaxWidth())
            LabeledTextField(value = input.targetLimit, onValueChange = { v -> viewModel.update { it.copy(targetLimit = v) } }, label = "Target unit limit", error = state.errors.targetLimit, numeric = true)
            LabeledTextField(value = input.previousReading, onValueChange = { v -> viewModel.update { it.copy(previousReading = v) } }, label = "Previous reading", error = state.errors.previousReading, numeric = true, imeAction = ImeAction.Done)
            Spacer(Modifier.height(4.dp))
            val saveInteraction = remember { MutableInteractionSource() }
            Button(
                onClick = viewModel::save, interactionSource = saveInteraction,
                modifier = Modifier.fillMaxWidth().height(54.dp).pressScale(saveInteraction),
                shape = MaterialTheme.shapes.large,
            ) { Text("Save meter", style = MaterialTheme.typography.labelLarge) }
        }
    }
}
