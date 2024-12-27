package com.example.drawapp

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
fun DrawCanvas(
    pathData: MutableState<PathData>,
    pathList: SnapshotStateList<PathData>,
    bitmap: MutableState<Bitmap?>,
    canvasSize: MutableState<IntSize>,
    futurePathList: SnapshotStateList<PathData>
) {

    var tempPath = Path()
    // Отримуємо розміри зображення або використовуємо стандартні розміри
    val canvasWidth = bitmap.value?.width?.toFloat() ?: 1080f
    val canvasHeight = bitmap.value?.height?.toFloat() ?: 1920f

    Box(modifier = Modifier
        .aspectRatio(canvasWidth / canvasHeight)
        .border(width = 5.dp, color = Color.Black)
    ){

        bitmap.value?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().padding(all = 5.dp)
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 5.dp)
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = {
                            futurePathList.clear()
                            tempPath = Path()
                            tempPath.moveTo(it.x, it.y)
                        },

                        onDragEnd = {
                            pathList.add(
                                pathData.value.copy(
                                    path = tempPath
                                )
                            )
                        }
                    ) { change, _ ->

                        tempPath.lineTo(
                            change.position.x,
                            change.position.y
                        )

                        if (pathList.size > 0) {
                            pathList.removeAt(pathList.size - 1)
                        }
                        pathList.add(
                            pathData.value.copy(
                                path = tempPath
                            )
                        )
                    }
                }
                .clipToBounds()
                .onGloballyPositioned { coordinates ->
                    canvasSize.value = coordinates.size
                }

        ) {
            pathList.forEach { pathData ->
                drawPath(
                    pathData.path,
                    color = pathData.color,
                    style = Stroke(pathData.lineWidth, cap=pathData.cap,
                        pathEffect =pathData.pathEffect)

                )
            }
            Log.d("MyLog", "Size: ${pathList.size}")
        }
    }
}