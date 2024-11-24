package com.example.dz2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState // Добавлен импорт
import coil.compose.rememberImagePainter

@Composable
fun ImageListScreen(viewModel: ImageViewModel = viewModel(), modifier: Modifier = Modifier) {
    val images by viewModel.images.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState(null)

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = { viewModel.loadImages(1) }, // Загрузка одного изображения при нажатии
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Загрузить новое изображение")
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            ErrorView(message = errorMessage) {
                viewModel.loadImages(1)
            }
        } else {
            ImageList(images = images)
        }
    }
}

@Composable
fun ErrorView(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message ?: "Unknown Error")
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

@Composable
fun ImageList(images: List<NekosImageData>) {
    LazyColumn {
        items(images) { image ->
            ImageCard(image = image)
        }
    }
}

@Composable
fun ImageCard(image: NekosImageData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Image(
                painter = rememberImagePainter(data = image.url),
                contentDescription = image.artist_name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                text = image.artist_name,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
