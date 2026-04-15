package com.example.episodes.Character

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetCharacterDetailsUseCase
import com.example.models.Character
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CharacterDetailState(
    val isLoading: Boolean = false,
    val character: Character? = null,
    val error: String? = null
)

sealed class CharacterDetailEvent {
    data class LoadDetails(val characterId: Int) : CharacterDetailEvent()
    object Refresh : CharacterDetailEvent()
}

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val getCharacterDetailsUseCase: GetCharacterDetailsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val characterIdFlow = savedStateHandle.getStateFlow<Int?>("character_id", null)
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CharacterDetailState> = combine(
        characterIdFlow.filterNotNull(),
        refreshTrigger.onStart { emit(Unit) }
    ) { id, _ -> id }
        .flatMapLatest { id ->
            getCharacterDetailsUseCase(id)
                .map { data ->
                    CharacterDetailState(
                        isLoading = false,
                        character = data,
                        error = null
                    )
                }
                .onStart {
                    emit(latestState().copy(isLoading = true, error = null))
                }
                .catch { e ->
                    emit(latestState().copy(isLoading = false, error = e.message))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CharacterDetailState(isLoading = true)
        )

    fun handleEvent(event: CharacterDetailEvent) {
        when (event) {
            is CharacterDetailEvent.LoadDetails -> {
                savedStateHandle["character_id"] = event.characterId
            }
            is CharacterDetailEvent.Refresh -> {
                refreshTrigger.tryEmit(Unit)
            }
        }
    }

    private fun latestState(): CharacterDetailState = state.value
}