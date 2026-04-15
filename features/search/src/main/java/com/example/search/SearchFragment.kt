package com.example.search

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.search.adapter.CharacterAdapter
import com.example.search.adapter.CharactersLoadStateAdapter
import com.example.search.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.navigation.R as NavigationR

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private val viewModel: SearchViewModel by viewModels()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val characterAdapter = CharacterAdapter { characterId ->
        val uri = "app://rickandmorty/character/$characterId".toUri()
        findNavController().navigate(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        characterAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        with(binding) {
            recyclerViewCharacterFilter.adapter = characterAdapter.withLoadStateFooter(
                footer = CharactersLoadStateAdapter { characterAdapter.retry() }
            )

            swipeRefresh.setOnRefreshListener {
                characterAdapter.refresh()
            }

            editTextSearch.doAfterTextChanged { text ->
                if (text.toString() != viewModel.lastQuery) {
                    characterAdapter.stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT
                    viewModel.onQueryChanged(text.toString())
                }
            }
        }
        setupLoadStateListener()
        setupObservers()

    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.characterPagingFlow.collectLatest { pagingData ->
                        characterAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                    }
                }

                launch {
                    var isRefreshingStarted = false

                    characterAdapter.loadStateFlow.collect { loadStates ->
                        val refreshState = loadStates.mediator?.refresh ?: loadStates.source.refresh
                        if (refreshState is LoadState.Loading) {
                            isRefreshingStarted = true
                        }
                        if (refreshState is LoadState.NotLoading && isRefreshingStarted) {
                            binding.recyclerViewCharacterFilter.post {
                                (binding.recyclerViewCharacterFilter.layoutManager as LinearLayoutManager)
                                    .scrollToPositionWithOffset(0, 0)
                                characterAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                            }
                            isRefreshingStarted = false
                        }
                    }
                }
            }
        }
    }

    private fun setupLoadStateListener() {
        characterAdapter.addLoadStateListener { loadState ->
            with(binding){
                val isListEmpty = loadState.refresh is LoadState.NotLoading &&
                        characterAdapter.itemCount == 0

                textViewEmptyState.isVisible = isListEmpty

                val isNetworkLoading = loadState.mediator?.refresh is LoadState.Loading

                progressBar.isVisible = isNetworkLoading && characterAdapter.itemCount == 0
                swipeRefresh.isRefreshing = isNetworkLoading && characterAdapter.itemCount > 0

                val errorState = loadState.mediator?.refresh as? LoadState.Error
                    ?: loadState.mediator?.append as? LoadState.Error
                    ?: loadState.source.refresh as? LoadState.Error
                    ?: loadState.source.append as? LoadState.Error

                errorState?.let {
                    swipeRefresh.isRefreshing = false
                    progressBar.isVisible = false
                    showErrorSnackbar(it.error)
                }
            }
        }
    }

    private fun showErrorSnackbar(error: Throwable) {
        val message = when (error) {
            is java.net.SocketTimeoutException -> getString(R.string.error_timeout)
            is java.net.UnknownHostException -> getString(R.string.error_no_internet)
            else -> getString(R.string.error_unknown, error.localizedMessage)
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAnchorView(requireActivity().findViewById(NavigationR.id.bottom_navigation))
            .setAction(R.string.error_retry) {
                characterAdapter.retry()
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewCharacterFilter.adapter = null
        _binding = null
    }
}