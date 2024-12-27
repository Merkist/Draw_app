package com.example.drawapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun BottomPanel(onClick: (Color) -> Unit,
                onLineWidthChange: (Float) -> Unit,
                onClickBack: () -> Unit,
                onClickForward: () -> Unit,
                onCapClick: (StrokeCap) -> Unit,
                onEffectClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .background(Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ColorList{ color ->
            onClick(color)
        }
        CustomSlider{ lineWidth ->
            onLineWidthChange(lineWidth)
        }
        BackButtonPanel(
            {onClickBack()},
            {onClickForward()},
            {onCapClick(it)},
            {onEffectClick()}
        )}
    }

@Composable
fun ColorList(onClick: (Color) -> Unit) {
    val colors = listOf(
        Color.Red,
        Color(0xff874605),
        Color(0xfffc9003),
        Color.Yellow,
        Color(0xff3bf507),
        Color.Green,
        Color.Cyan,
        Color.Blue,
        Color.Magenta,
        Color.Black,
        Color.Gray,
        Color.White,
    )

    LazyRow(
        modifier = Modifier.padding(10.dp)
    ) {
        items(colors) { color ->
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .clickable {
                        onClick(color)
                    }
                    .size(40.dp)
                    .background(color, CircleShape)
                    .border(width = 1.dp, color = Color.Black, CircleShape)
            )
        }
    }
}

@Composable
fun CustomSlider(onChange: (Float) -> Unit){
    var position by remember {
        mutableStateOf(0.05f)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Line width: ${(position * 100).toInt()}")
        Slider(
            value = position,
            onValueChange = {
                val tempPos = if (it > 0) it else 0.01f
                position = tempPos
                onChange(tempPos * 100)
            }
        )
    }
}

@Composable
fun BackButtonPanel(onClickBack: () -> Unit, onClickForward: () -> Unit,
                    onCapClick: (StrokeCap) -> Unit, onEffectClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.35f).padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = Modifier.clip(CircleShape).background(Color.White),
                onClick = {
                    onClickBack()
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Icon"
                )
            }
            IconButton(
                modifier = Modifier.clip(CircleShape).background(Color.White),
                onClick = {
                    onClickForward()
                }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Icon"
                )
            }
        }
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                modifier = Modifier.clip(CircleShape).background(Color.White),
                onClick = {
                    onCapClick(StrokeCap.Round)
                }) {
                Icon(
                    modifier = Modifier.size(27.dp),
                    painter = painterResource(id = R.drawable.round),
                    contentDescription = "Icon"
                )
            }
            IconButton(
                modifier = Modifier.clip(CircleShape).background(Color.White),
                onClick = {
                    onCapClick(StrokeCap.Square)
                }) {
                Icon(
                    modifier = Modifier.size(27.dp),
                    painter = painterResource(id = R.drawable.square),
                    contentDescription = "Icon"
                )
            }
            IconButton(
                modifier = Modifier.clip(CircleShape).background(Color.White),
                onClick = {
                    //onCapClick(StrokeCap.Butt)
                    onEffectClick()
                }) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.dash_line),
                    contentDescription = "Icon"
                )
            }
        }
    }
}