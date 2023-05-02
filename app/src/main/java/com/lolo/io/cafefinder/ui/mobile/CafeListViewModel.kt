package com.lolo.io.cafefinder.ui.mobile

import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lolo.io.cafefinder.domain.Repository
import com.lolo.io.cafefinder.domain.model.POI
import com.lolo.io.cafefinder.util.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class CafeListUiState(
    val pois: List<POI> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class CafeListViewModel(
    private val repository: Repository
) : ViewModel() {
    var state by mutableStateOf(CafeListUiState())

    fun getNearestCafes(location: Location?) {
        if (location == null) return
        viewModelScope.launch {
            repository.getCafesNearBy(location.latitude, location.longitude).onEach {
                state = when (it) {
                    is Resource.Success -> {
                        CafeListUiState(
                            pois = it.data,
                        )
                    }

                    is Resource.Error -> {
                        CafeListUiState(
                            error = it.errorMessage,
                        )
                    }

                    is Resource.Loading -> {
                        CafeListUiState(
                            pois = state.pois,
                            loading = true,
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
}