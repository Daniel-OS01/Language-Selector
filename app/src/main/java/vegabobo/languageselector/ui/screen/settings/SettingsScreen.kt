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
import vegabobo.languageselector.ui.screen.settings.model.UriWrapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var showImportConfirmation by remember { mutableStateOf<UriWrapper?>(null) }
    var showErrorDialog by remember { mutableStateOf<String?>(null) }

    val exportJsonLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri -> viewModel.exportAsJson(context, uri) }
            }
        }

    val importJsonLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri -> showImportConfirmation = UriWrapper(uri, "json") }
            }
        }

    val exportXmlLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri -> viewModel.exportAsXml(context, uri) }
            }
        }

    val importXmlLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.data?.let { uri -> showImportConfirmation = UriWrapper(uri, "xml") }
            }
        }

    LaunchedEffect(uiState) {
        when (uiState.exportState) {
            ExportState.SUCCESS -> {
                Toast.makeText(context, R.string.export_success, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            ExportState.ERROR -> {
                showErrorDialog = uiState.errorText
            }
            else -> {}
        }
    }

    if (showImportConfirmation != null) {
        AlertDialog(
            onDismissRequest = { showImportConfirmation = null },
            title = { Text(stringResource(id = R.string.settings)) },
            text = { Text(stringResource(id = R.string.import_confirmation)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val wrapper = showImportConfirmation!!
                        if (wrapper.type == "json") {
                            viewModel.importFromJson(context, wrapper.uri)
                        } else {
                            viewModel.importFromXml(context, wrapper.uri)
                        }
                        showImportConfirmation = null
                    }
                ) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirmation = null }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    if (showErrorDialog != null) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = null
                viewModel.resetState()
            },
            title = { Text(stringResource(id = R.string.error_title)) },
            text = { Text(showErrorDialog ?: "") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog = null
                        viewModel.resetState()
                    }
                ) {
                    Text(stringResource(id = R.string.ok))
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SimpleCard(title = stringResource(id = R.string.export_json)) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, viewModel.getFileName("json"))
                }
                exportJsonLauncher.launch(intent)
            }
            SimpleCard(title = stringResource(id = R.string.import_json)) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                }
                importJsonLauncher.launch(intent)
            }
            SimpleCard(title = stringResource(id = R.string.export_xml)) {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/xml"
                    putExtra(Intent.EXTRA_TITLE, viewModel.getFileName("xml"))
                }
                exportXmlLauncher.launch(intent)
            }
            SimpleCard(title = stringResource(id = R.string.import_xml)) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/xml"
                }
                importXmlLauncher.launch(intent)
            }
            SimpleCard(
                title = stringResource(id = R.string.about),
                onClick = { navController.navigate("about") }
            )
        }
    }
}
