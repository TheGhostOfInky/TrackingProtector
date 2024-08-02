@file:OptIn(ExperimentalMaterial3Api::class)

package com.theghostofinky.trackingprotector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun MainView(onOpenSettings: () -> Unit) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    AppSideBar(drawerState, onOpenSettings) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    title = { Text("Tracking Protector") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Open side drawer"
                            )
                        }
                    }
                )
            }
        ) {
            InputBox(it)
        }
    }
}

@Composable
fun AppSideBar(
    drawerState: DrawerState,
    onOpenSettings: () -> Unit,
    content: @Composable () -> Unit
) {
    Sidebar(
        drawerState = drawerState,
        title = "Tracking Protector",
        items = {
            NavigationDrawerItem(
                label = { Text("Settings") },
                selected = false,
                icon = {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Open settings"
                    )
                },
                onClick = onOpenSettings
            )
        }
    ) {
        content()
    }
}

@Composable
fun InputBox(innerPadding: PaddingValues) {
    var textBoxInput by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val adjustedPadding = PaddingValues(
        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
        top = innerPadding.calculateTopPadding() + 20.dp,
        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
        bottom = innerPadding.calculateBottomPadding()
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(adjustedPadding)
            .fillMaxWidth()
    ) {
        TextField(
            value = textBoxInput,
            label = { Text("URL") },
            onValueChange = { textBoxInput = it },
            modifier = Modifier.padding(20.dp, 5.dp, 20.dp, 20.dp),
            singleLine = true
        )
        Button(
            modifier = Modifier.width(180.dp),
            onClick = {
                coroutineScope.launch {
                    buttonHandle(textBoxInput, Actions.SHARE)
                }
            }
        ) {
            Text("Share URL")
        }
        Button(
            modifier = Modifier.width(180.dp),
            onClick = {
                coroutineScope.launch {
                    buttonHandle(textBoxInput, Actions.COPY)
                }
            }
        ) {
            Text("Copy to clipboard")
        }
        Button(
            modifier = Modifier.width(180.dp),
            onClick = {
                coroutineScope.launch {
                    buttonHandle(textBoxInput, Actions.OPEN)
                }
            }
        ) {
            Text("Open URL")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainViewPreview() {
    MainView {
        /*EMPTY*/
    }
}