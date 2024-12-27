package com.example.drawapp

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerMenu(
    content: @Composable (PaddingValues) -> Unit,
    pathList: SnapshotStateList<PathData>,
    context: Context,
    pickImageLauncher: ManagedActivityResultLauncher<String, Uri?>,
    bitmap: MutableState<Bitmap?>,
    canvasSize: MutableState<IntSize>,
    futurePathList: SnapshotStateList<PathData>
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(12.dp))
                    Text("Menu", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider()
                    NavigationDrawerItem(
                        label = { Text("New Canvas") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                bitmap.value = null
                                pathList.clear()
                                futurePathList.clear()

                                drawerState.close()
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Save Drawing") },
                        selected = false,
                        onClick = {
                            var bmp: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
                            bitmap.value?.let {
                                bmp = createBitmapFromPathsAndImage(it, pathList, canvasSize.value)
                            } ?: run {
                                bmp = createBitmapFromPaths(pathList, canvasSize.value)
                            }
                            saveBitmapToGallery(bmp, context)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Load Image") },
                        selected = false,
                        onClick = {
                            scope.launch {
                                pathList.clear()
                                futurePathList.clear()
                                pickImageLauncher.launch("image/*")
                                drawerState.close()
                            }
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Menu") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}