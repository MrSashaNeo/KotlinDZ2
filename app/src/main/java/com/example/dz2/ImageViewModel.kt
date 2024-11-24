package com.example.dz2

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private val _images = MutableLiveData<List<NekosImageData>>()
    val images: LiveData<List<NekosImageData>> get() = _images

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private var currentPage = 1

    fun loadImages(amount: Int, page: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitInstance.api.getImages(amount, page)
                if (response.isSuccessful) {
                    val newImages = response.body()?.results ?: emptyList()
                    val currentImages = _images.value ?: emptyList()
                    _images.value = currentImages + newImages
                    _errorMessage.value = null
                } else {
                    _errorMessage.value = "Ошибка загрузки данных: ${response.code()} ${response.message()}"
                    println("Response error: ${response.errorBody()?.string()}")
                }
            } catch (e: HttpException) {
                _errorMessage.value = "HttpException: ${e.message()}"
                println("HttpException: ${e}")
            } catch (e: IOException) {
                _errorMessage.value = "IOException: ${e.message}"
                println("IOException: ${e}")
            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                println("Exception: ${e}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage(amount: Int) {
        loadImages(amount, currentPage)
        currentPage++
    }
}


