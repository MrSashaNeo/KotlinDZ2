package com.example.dz2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import kotlinx.coroutines.flow.filter
import androidx.compose.foundation.lazy.LazyListState



@Composable
fun ImageListScreen(viewModel: ImageViewModel = viewModel(), modifier: Modifier = Modifier) {
    val images by viewModel.images.observeAsState(emptyList())
    val configuration = LocalConfiguration.current
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
            .filter { it != null && it.index == images.lastIndex }
            .collect {
                viewModel.loadNextPage(5) // Загружаем следующие 5 изображений
            }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(
            onClick = { viewModel.loadImages(1, 1) }, // Загрузка первого изображения на первой странице
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Загрузить новое изображение")
        }

        if (configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            ImageRow(images = images, listState = listState, viewModel = viewModel)
        } else {
            ImageList(images = images, listState = listState, viewModel = viewModel)
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
fun ImageList(images: List<NekosImageData>, listState: LazyListState, viewModel: ImageViewModel) {
    LazyColumn(state = listState) {
        items(images) { image ->
            ImageCard(image = image, viewModel = viewModel)
        }
    }
}

@Composable
fun ImageRow(images: List<NekosImageData>, listState: LazyListState, viewModel: ImageViewModel) {
    LazyRow(state = listState) {
        items(images) { image ->
            ImageCard(image = image, viewModel = viewModel)
        }
    }
}

@Composable
fun ImageCard(image: NekosImageData, viewModel: ImageViewModel) {
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(image.url) {
        try {
            // Simulate network delay
            kotlinx.coroutines.delay(1000)
            isLoading = false
            errorMessage = null
        } catch (e: Exception) {
            isLoading = false
            errorMessage = "Error loading image"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.6f)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                ErrorView(message = errorMessage) {
                    isLoading = true
                    viewModel.loadImages(1, 1)
                }
            } else {
                Image(
                    painter = rememberImagePainter(
                        data = image.url,
                        builder = {
                            crossfade(true)
                            diskCachePolicy(CachePolicy.ENABLED)
                            memoryCachePolicy(CachePolicy.ENABLED)
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
