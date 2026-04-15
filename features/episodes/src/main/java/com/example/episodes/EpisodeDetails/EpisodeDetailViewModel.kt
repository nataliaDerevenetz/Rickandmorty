package com.example.episodes.EpisodeDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.GetEpisodeDetailsUseCase
import com.example.models.Character
import com.example.models.EpisodeInfo
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

data class EpisodeInfoState(
    val isLoading: Boolean = false,
    val episode: EpisodeInfo? = null,
    val characters: List<Character> = emptyList(),
    val error: String? = null
)

sealed class EpisodeDetailEvent {
    data class LoadDetails(val episodeId: Int) : EpisodeDetailEvent()
    object Refresh : EpisodeDetailEvent()
}

@HiltViewModel
class EpisodeDetailViewModel @Inject constructor(
    private val getEpisodeDetailsUseCase: GetEpisodeDetailsUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val episodeIdFlow = savedStateHandle.getStateFlow<Int?>("episode_id", null)
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<EpisodeInfoState> = combine(episodeIdFlow.filterNotNull(), refreshTrigger.onStart { emit(Unit) }) { id, _ -> id }
        .flatMapLatest { id ->
            getEpisodeDetailsUseCase(id)
                .map { data ->
                    EpisodeInfoState(
                        isLoading = false,
                        episode = data.episode,
                        characters = data.characters,
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
            initialValue = EpisodeInfoState(isLoading = true)
        )

    fun handleEvent(event: EpisodeDetailEvent) {
        when (event) {
            is EpisodeDetailEvent.LoadDetails -> {
                savedStateHandle["episode_id"] = event.episodeId
            }
            is EpisodeDetailEvent.Refresh -> {
                refreshTrigger.tryEmit(Unit)
            }
        }
    }

    private fun latestState(): EpisodeInfoState = state.value
}
