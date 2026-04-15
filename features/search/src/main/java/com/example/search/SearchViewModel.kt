package com.example.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.GetCharactersByNameUseCase
import com.example.models.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
   val getCharactersByNameUseCase: GetCharactersByNameUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    var lastQuery: String = ""
        private set

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val characterPagingFlow: Flow<PagingData<Character>> = _searchQuery
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getCharactersByNameUseCase(query)
        }
        .cachedIn(viewModelScope)

    fun onQueryChanged(query: String) {
        if (lastQuery == query) return
        lastQuery = query
        _searchQuery.value = query
    }
}
