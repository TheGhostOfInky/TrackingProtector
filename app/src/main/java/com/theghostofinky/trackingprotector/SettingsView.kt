@file:OptIn(ExperimentalMaterial3Api::class)

package com.theghostofinky.trackingprotector

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun SettingsView(onExitSettings: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onExitSettings) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Return to main screen"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            SettingsElement(text = "Share action:") {
                ActionDropDown(
                    name = "Action",
                    prefKey = PrefKeys.SHARE_ACTION
                )
            }
            Divider()
            SettingsElement(text = "Open action:") {
                ActionDropDown(
                    name = "Action",
                    prefKey = PrefKeys.OPEN_ACTION
                )
            }
            Divider()
            SettingsElement(text = "Comment context") {
                NumericInput(
                    name = "Context",
                    prefKey = PrefKeys.CONTEXT_COUNT
                )
            }
            Divider()
            SettingsElement(text = "Instance") {
                TextInput(
                    name = "URL",
                    prefKey = PrefKeys.INSTANCE,
                    default = "www.reddit.com"
                )
            }
        }
    }
}

@Composable
fun SettingsElement(
    text: String,
    content: @Composable () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .height(64.dp)
            .padding(4.dp, 0.dp)
    ) {
        Text(
            text,
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier.width(200.dp)
        ){
            content()
        }
    }
}

val ActionIcons = hashMapOf<Actions, ImageVector>(
    Actions.SHARE to Icons.Filled.Share,
    Actions.COPY to Icons.Filled.Create,
    Actions.OPEN to Icons.Filled.ExitToApp
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ActionDropDown(name: String, prefKey: Preferences.Key<Int>) {
    val scope = rememberCoroutineScope()
    val settings = PreferencesDatabase(MainActivity.appContext)
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(Actions.NONE) }
    scope.launch {
        selectedOption = Actions.fromIndex(
            settings.getPreference(prefKey, Actions.SHARE.ordinal).first()
        )
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .padding(4.dp),
    ) {
        TextField(
            readOnly = true,
            value = selectedOption.name.lowercase(),
            label = { Text(name) },
            onValueChange = {/*NEVER USED*/ },
            modifier = Modifier.menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Actions.values().slice(1..3).forEach {
                DropdownMenuItem(
                    text = { Text(it.name.lowercase()) },
                    leadingIcon = {
                        Icon(
                            ActionIcons[it]!!,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        scope.launch {
                            selectedOption = it
                            settings.setPreference(prefKey, it.ordinal)
                        }
                    }
                )
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun NumericInput(name: String, prefKey: Preferences.Key<Int>) {
    val scope = rememberCoroutineScope()
    val settings = PreferencesDatabase(MainActivity.appContext)
    var value by remember { mutableStateOf("") }
    var modified by remember { mutableStateOf(false) }
    scope.launch {
        if (!modified) {
            value = settings.getPreference(prefKey, 1).first().toString()
        }
    }
    TextField(
        value = value,
        label = { Text(name) },
        modifier = Modifier
            .padding(4.dp)
            .wrapContentWidth(),
        onValueChange = { newValue ->
            value = newValue
            modified = true
            scope.launch {
                val trimmedValue = newValue.trim()
                if (trimmedValue.isEmpty()) {
                    return@launch
                }
                val intVal = value.runCatching { trimmedValue.toInt() }
                intVal.fold(
                    onSuccess = {
                        if (it > 0) {
                            settings.setPreference(prefKey, it)
                        } else {
                            sendToast("Invalid value")
                        }
                    },
                    onFailure = {
                        sendToast("Invalid value")
                    }
                )
            }
        }
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TextInput(name: String, prefKey: Preferences.Key<String>, default: String) {
    val scope = rememberCoroutineScope()
    val settings = PreferencesDatabase(MainActivity.appContext)
    var value by remember { mutableStateOf("") }
    scope.launch {
        value = settings.getPreference(prefKey, default).first()
    }
    TextField(
        value = value,
        label = { Text(name) },
        modifier = Modifier
            .padding(4.dp)
            .wrapContentWidth(),
        onValueChange = { newValue ->
            value = newValue
            scope.launch {
                settings.setPreference(prefKey, newValue.trim())
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsViewPreview() {
    SettingsView {
        /*EMPTY*/
    }
}