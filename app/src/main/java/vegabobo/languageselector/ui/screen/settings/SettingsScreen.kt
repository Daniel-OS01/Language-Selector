package vegabobo.languageselector.ui.screen.settings

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import vegabobo.languageselector.R
import vegabobo.languageselector.ui.components.SimpleCard
import vegabobo.languageselector.ui.screen.BaseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showImportDialog by remember { mutableStateOf(false) }

    val exportLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    viewModel.exportSettings(context, uri)
                }
            }
        }

    val importLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri ->
                    viewModel.importSettings(context, uri)
                }
            }
        }

    LaunchedEffect(uiState) {
        if (uiState.isExportSuccess) {
            Toast.makeText(context, R.string.config_exported, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
        if (uiState.isImportSuccess) {
            Toast.makeText(context, R.string.config_imported, Toast.LENGTH_SHORT).show()
            viewModel.resetState()
        }
        when (uiState.errorType) {
            ErrorType.IMPORT -> {
                Toast.makeText(context, R.string.error_importing, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            ErrorType.EXPORT -> {
                Toast.makeText(context, R.string.error_exporting, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            ErrorType.NONE -> {}
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(stringResource(id = R.string.import_config)) },
            text = { Text(stringResource(id = R.string.import_config_dialog)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/json"
                        }
                        importLauncher.launch(intent)
                    }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    BaseScreen(
        title = stringResource(id = R.string.settings),
        navIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                SimpleCard(
                    title = stringResource(id = R.string.export_config),
                    onClick = {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/json"
                            putExtra(Intent.EXTRA_TITLE, "config.json")
                        }
                        exportLauncher.launch(intent)
                    }
                )
                SimpleCard(
                    title = stringResource(id = R.string.import_config),
                    onClick = { showImportDialog = true }
                )
                SimpleCard(
                    title = stringResource(id = R.string.about),
                    onClick = { navController.navigate("about") }
                )
            }
        }
    )
}