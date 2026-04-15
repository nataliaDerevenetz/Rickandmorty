package com.example.episodes.Episodes

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.episodes.Episodes.adapter.EpisodeAdapter
import com.example.episodes.Episodes.adapter.EpisodesLoadStateAdapter
import com.example.episodes.R
import com.example.episodes.databinding.FragmentEpisodesBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.example.navigation.R as NavigationR

@AndroidEntryPoint
class EpisodesFragment : Fragment(R.layout.fragment_episodes) {

    private var _binding: FragmentEpisodesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EpisodesViewModel by viewModels()
    private val episodeAdapter by lazy {
        EpisodeAdapter { episode ->
            val action = EpisodesFragmentDirections.actionEpisodesToEpisodeDetail(episode.id)
            findNavController().navigate(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEpisodesBinding.bind(view)

        binding.recyclerViewEpisode.adapter = episodeAdapter.withLoadStateFooter(
            footer = EpisodesLoadStateAdapter { episodeAdapter.retry() }
        )

        binding.swipeRefresh.setOnRefreshListener {
            episodeAdapter.refresh()
        }
        setupLoadStateListener()
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.episodesPagingFlow.collectLatest { pagingData ->
                        episodeAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                    }
                }
            }
        }
    }

    private fun setupLoadStateListener() {
        episodeAdapter.addLoadStateListener { loadState ->
            val isListEmpty = loadState.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    episodeAdapter.itemCount == 0

            binding.textViewEmptyState.isVisible = isListEmpty

            val isNetworkLoading = loadState.mediator?.refresh is LoadState.Loading

            binding.progressBar.isVisible = isNetworkLoading && episodeAdapter.itemCount == 0
            binding.swipeRefresh.isRefreshing = isNetworkLoading && episodeAdapter.itemCount > 0

            val errorState = loadState.mediator?.refresh as? LoadState.Error
                ?: loadState.mediator?.append as? LoadState.Error
                ?: loadState.source.refresh as? LoadState.Error
                ?: loadState.source.append as? LoadState.Error

            errorState?.let {
                binding.swipeRefresh.isRefreshing = false
                binding.progressBar.isVisible = false
                showErrorSnackbar(it.error)
            }
        }
    }

    private fun showErrorSnackbar(error: Throwable) {
        val message = when (error) {
            is SocketTimeoutException -> getString(R.string.error_timeout)
            is UnknownHostException -> getString(R.string.error_no_internet)
            else -> getString(R.string.error_unknown, error.localizedMessage)
        }


        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAnchorView(requireActivity().findViewById(NavigationR.id.bottom_navigation))
            .setAction(R.string.error_retry) {
                episodeAdapter.retry()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewEpisode.adapter = null
        _binding = null
    }
}