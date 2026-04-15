package com.example.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.example.models.Character
import com.example.search.databinding.ItemCharacterBinding

class CharacterAdapter(
    private val onCharacterClick: (Int) -> Unit
) : PagingDataAdapter<Character, CharacterAdapter.CharacterViewHolder>(CharacterDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        val binding = ItemCharacterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CharacterViewHolder(binding, onCharacterClick)
    }

    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class CharacterViewHolder(
        private val binding: ItemCharacterBinding,
        private val onCharacterClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(character: Character) {
            with(binding) {
                characterName.text = character.name
                characterGender.text = character.gender
                characterLocation.text = character.location
                characterImage.load(character.image) {
                    apply {
                        crossfade(true)
                        crossfade(500)
                        transformations(CircleCropTransformation())
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_dialog_alert)
                    }
                }
                root.setOnClickListener {
                    onCharacterClick(character.id)
                }
            }
        }
    }

    private object CharacterDiffCallback : DiffUtil.ItemCallback<Character>() {
        override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean =
            oldItem == newItem
    }
}