package com.example.episodes.Character

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import coil3.load
import com.example.episodes.R
import com.example.episodes.databinding.FragmentCharacterBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.example.navigation.R as NavigationR

@AndroidEntryPoint
class CharacterFragment : Fragment(R.layout.fragment_character) {

    private var _binding: FragmentCharacterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharacterViewModel by viewModels()
    private val args: CharacterFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCharacterBinding.bind(view)

        setupObservers()

        viewModel.handleEvent(CharacterDetailEvent.LoadDetails(args.characterId))
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.handleEvent(CharacterDetailEvent.Refresh)
        }

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

    private fun render(state: CharacterDetailState) {
        with(binding) {
            swipeRefresh.isRefreshing = state.isLoading

            val hasNoData = state.character == null
            val isFullErrorVisible = state.error != null && hasNoData && !state.isLoading

            errorLayout.isVisible = isFullErrorVisible
            contentScroll.isVisible = !isFullErrorVisible

            if (isFullErrorVisible) { errorText.text = getString(R.string.not_found) }

            state.character?.let { character ->
                nameText.text = character.name
                genderText.text = character.gender
                locationText.text = character.location
                characterImage.load(character.image)
            }

            if (state.error != null && !state.isLoading) {
                showErrorSnackbar(getString(R.string.error_load))
            }
        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAnchorView(requireActivity().findViewById(NavigationR.id.bottom_navigation))
            .setAction(R.string.error_retry) {
                viewModel.handleEvent(CharacterDetailEvent.Refresh)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}