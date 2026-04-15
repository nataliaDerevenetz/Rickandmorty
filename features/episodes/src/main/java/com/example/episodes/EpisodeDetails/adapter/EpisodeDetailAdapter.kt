package com.example.episodes.EpisodeDetails.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.example.episodes.databinding.ItemEpisodeCharacterBinding
import com.example.episodes.databinding.ItemEpisodeHeaderBinding

class EpisodeDetailAdapter(
    private val onCharacterClick: (Int) -> Unit
) : ListAdapter<EpisodeListItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CHARACTER = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is EpisodeListItem.Header -> TYPE_HEADER
            is EpisodeListItem.CharacterItem -> TYPE_CHARACTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                ItemEpisodeHeaderBinding.inflate(inflater, parent, false)
            )
            else -> CharacterViewHolder(
                ItemEpisodeCharacterBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as EpisodeListItem.Header)
            is CharacterViewHolder -> holder.bind(item as EpisodeListItem.CharacterItem,onCharacterClick)
        }
    }

    class HeaderViewHolder(private val binding: ItemEpisodeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(header: EpisodeListItem.Header) {
            binding.episodeName.text = header.name
            binding.airDate.text = header.airDate
        }
    }

    class CharacterViewHolder(private val binding: ItemEpisodeCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EpisodeListItem.CharacterItem, onClick: (Int) -> Unit) {
            binding.characterName.text = item.character.name
            binding.characterImage.load(item.character.image) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_dialog_alert)
            }
            binding.root.setOnClickListener {
                onClick(item.character.id)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EpisodeListItem>() {
        override fun areItemsTheSame(old: EpisodeListItem, new: EpisodeListItem): Boolean {
            return when {
                old is EpisodeListItem.Header && new is EpisodeListItem.Header -> true
                old is EpisodeListItem.CharacterItem && new is EpisodeListItem.CharacterItem ->
                    old.character.id == new.character.id
                else -> false
            }
        }
        override fun areContentsTheSame(old: EpisodeListItem, new: EpisodeListItem) = old == new
    }
}