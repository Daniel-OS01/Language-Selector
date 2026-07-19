package vegabobo.languageselector.ui.screen.settings

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import vegabobo.languageselector.R
import vegabobo.languageselector.ui.components.BackButton
import vegabobo.languageselector.ui.components.Title
import vegabobo.languageselector.ui.screen.BaseScreen
import vegabobo.languageselector.ui.screen.about.PreferenceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    settingsVm: SettingsVm = hiltViewModel(),
) {
    val uiState by settingsVm.uiState.collectAsState()
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { settingsVm.exportBackup(it) }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { settingsVm.importBackup(it) }
        }
    }

    LaunchedEffect(uiState.message) {
        val message = uiState.message ?: return@LaunchedEffect
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        settingsVm.clearMessage()
    }

    if (uiState.showSaveDialog) {
        AlertDialog(
            onDismissRequest = { settingsVm.dismissSaveDialog() },
            title = { Text(stringResource(R.string.preset_save_title)) },
            text = {
                OutlinedTextField(
                    value = uiState.saveNameDraft,
                    onValueChange = settingsVm::onSaveNameChange,
                    singleLine = true,
                    label = { Text(stringResource(R.string.preset_name)) },
                )
            },
            confirmButton = {
                TextButton(
                    enabled = uiState.saveNameDraft.isNotBlank() && !uiState.isBusy,
                    onClick = { settingsVm.saveCurrentAsPreset() },
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { settingsVm.dismissSaveDialog() }) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    BaseScreen(
        title = stringResource(R.string.settings),
        navIcon = { BackButton { navigateBack() } },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            item {
                Title(stringResource(R.string.presets_section))
                PreferenceItem(
                    title = stringResource(R.string.preset_save_current),
                    description = stringResource(R.string.preset_save_current_desc),
                ) {
                    if (!uiState.isBusy) settingsVm.openSaveDialog()
                }
            }

            if (uiState.presets.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.presets_empty),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    )
                }
            } else {
                items(uiState.presets, key = { it.id }) { preset ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(text = preset.name)
                        Text(
                            text = stringResource(
                                R.string.preset_apps_count,
                                preset.apps.size,
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp),
                        ) {
                            Button(
                                enabled = !uiState.isBusy,
                                onClick = { settingsVm.applyPreset(preset.id) },
                            ) {
                                Text(stringResource(R.string.preset_apply))
                            }
                            OutlinedButton(
                                enabled = !uiState.isBusy,
                                onClick = { settingsVm.deletePreset(preset.id) },
                            ) {
                                Text(stringResource(R.string.preset_delete))
                            }
                        }
                    }
                }
            }

            item {
                Title(stringResource(R.string.backup_section))
                PreferenceItem(
                    title = stringResource(R.string.export_backup),
                    description = stringResource(R.string.export_backup_desc),
                ) {
                    if (!uiState.isBusy) {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/json"
                            putExtra(Intent.EXTRA_TITLE, "language-selector-backup.json")
                        }
                        exportLauncher.launch(intent)
                    }
                }
                PreferenceItem(
                    title = stringResource(R.string.import_backup),
                    description = stringResource(R.string.import_backup_desc),
                ) {
                    if (!uiState.isBusy) {
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/json"
                        }
                        importLauncher.launch(intent)
                    }
                }
            }

            if (uiState.isBusy) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            item { Spacer(modifier = Modifier.padding(bottom = padding.calculateBottomPadding())) }
        }
    }
}
