package com.example.drawapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.example.drawapp.ui.theme.DrawAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val pathData = remember {
                mutableStateOf(PathData())
            }
            val pathList = remember {
                mutableStateListOf<PathData>()
            }
            val futurePathList = remember {
                mutableStateListOf<PathData>()
            }
            val bitmap = remember { mutableStateOf<Bitmap?>(null) }
            val canvasSize = remember { mutableStateOf(IntSize(0, 0)) }
            val context = LocalContext.current

            // Вибір зображення
            val pickImageLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val selectedBitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap.value = selectedBitmap
                }
            }

            DrawAppTheme {
                DrawerMenu(
                    pathList = pathList,
                    futurePathList = futurePathList,
                    context = context,
                    pickImageLauncher = pickImageLauncher,
                    bitmap = bitmap,
                    canvasSize = canvasSize,
                    content = { paddingValues ->
                        Column (modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .background(Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                DrawCanvas(pathData, pathList, bitmap, canvasSize, futurePathList)
                            }
                            BottomPanel(
                                { color ->
                                    pathData.value = pathData.value.copy(
                                        color = color
                                    )
                                },
                                { lineWidth ->
                                    pathData.value = pathData.value.copy(
                                        lineWidth = lineWidth
                                    )

                                    if (pathData.value.pathEffect != null) {
                                        val scaleFactor = lineWidth / 5f
                                        val adjustedIntervals = floatArrayOf(20f * scaleFactor, 10f * scaleFactor)
                                        pathData.value = pathData.value.copy(
                                            pathEffect = PathEffect.dashPathEffect(adjustedIntervals, 0f)
                                        )
                                    }
                                },
                                {
                                    if (!pathList.isEmpty()) {
                                        pathList.lastOrNull()?.let {
                                            futurePathList.add(it)
                                        }
                                        pathList.removeAt(pathList.size - 1)
                                        pathList.removeAt(pathList.size - 1)
                                        pathList.lastOrNull()?.let {
                                            pathList.add(it)
                                        }
                                    }
                                    Log.d("MyLog", "Size: ${pathList.size}, ${futurePathList.size}} ")
                                },
                                {
                                    if (futurePathList.isNotEmpty()) {
                                        futurePathList.last().let {
                                            if (!pathList.isEmpty()) {
                                                pathList.removeAt(pathList.size - 1)
                                            }
                                            pathList.add(it)
                                            pathList.add(it)
                                            futurePathList.removeAt(futurePathList.size - 1)
                                        }
                                    }
                                    Log.d("MyLog", "Size: ${pathList.size}, ${futurePathList.size}} ")
                                },
                                {cap ->
                                    pathData.value = pathData.value.copy(
                                        cap = cap,
                                        pathEffect = null
                                    )
                                }
                            ){
                                pathData.value = pathData.value.copy(
                                    cap = StrokeCap.Round,
                                    pathEffect = if (pathData.value.pathEffect == null) {
                                        val scaleFactor = pathData.value.lineWidth / 5f
                                        val adjustedIntervals = floatArrayOf(20f * scaleFactor, 10f * scaleFactor)
                                        PathEffect.dashPathEffect(adjustedIntervals, 0f)
                                    } else {
                                        null
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
