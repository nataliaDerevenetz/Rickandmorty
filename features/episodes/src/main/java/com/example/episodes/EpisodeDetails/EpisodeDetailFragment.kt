package com.example.episodes.EpisodeDetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.episodes.EpisodeDetails.adapter.EpisodeDetailAdapter
import com.example.episodes.EpisodeDetails.adapter.EpisodeListItem
import com.example.episodes.R
import com.example.episodes.databinding.FragmentEpisodeDetailBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.navigation.R as NavigationR

@AndroidEntryPoint
class EpisodeDetailFragment : Fragment(R.layout.fragment_episode_detail) {

    private var _binding: FragmentEpisodeDetailBinding? = null
    private val binding get() = _binding!!

    private val episodeAdapter by lazy {
        EpisodeDetailAdapter { characterId ->
            val action = EpisodeDetailFragmentDirections.actionEpisodeDetailToCharacter(characterId)
            findNavController().navigate(action)
        }
    }
    private val args: EpisodeDetailFragmentArgs by navArgs()
    private val viewModel: EpisodeDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEpisodeDetailBinding.bind(view)
        val episodeId = args.episodeId
        val layoutManager = GridLayoutManager(requireContext(), 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (episodeAdapter.getItemViewType(position)) {
                    0 -> 2
                    1 -> 1
                    else -> 1
                }
            }
        }

        with(binding) {
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = episodeAdapter
            swipeRefresh.setOnRefreshListener {
                viewModel.handleEvent(EpisodeDetailEvent.Refresh)
            }
        }
        setupObservers()

        viewModel.handleEvent(EpisodeDetailEvent.LoadDetails(episodeId))
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: EpisodeInfoState) {
        with(binding) {
            swipeRefresh.isRefreshing = state.isLoading

            val hasData = state.episode != null || state.characters.isNotEmpty()
            val hasError = state.error != null

            val showFullError = hasError && !hasData && !state.isLoading
            errorLayout.isVisible = showFullError
            swipeRefresh.isVisible = hasData || state.isLoading

            if (showFullError) { errorText.text = getString(R.string.not_found) }
            if (hasError && !state.isLoading) { showErrorSnackbar(getString(R.string.error_load)) }

            val listItems = buildList {
                state.episode?.let { add(EpisodeListItem.Header(it.name, it.airDate)) }
                addAll(state.characters.map { EpisodeListItem.CharacterItem(it) })
            }
            episodeAdapter.submitList(listItems)
        }

    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAnchorView(requireActivity().findViewById(NavigationR.id.bottom_navigation))
            .setAction(R.string.error_retry) {
                viewModel.handleEvent(EpisodeDetailEvent.Refresh)
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}