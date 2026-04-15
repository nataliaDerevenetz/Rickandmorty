package com.example.episodes.Episodes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.episodes.databinding.ItemEpisodeBinding
import com.example.models.Episode


class EpisodeAdapter(
    private val onItemClick: (Episode) -> Unit
) : PagingDataAdapter<Episode, EpisodeAdapter.EpisodeViewHolder>(EpisodeDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val binding = ItemEpisodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EpisodeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class EpisodeViewHolder(
        private val binding: ItemEpisodeBinding,
        private val onItemClick: (Episode) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(episode: Episode) {
            with(binding) {
                episodeName.text = episode.name
                episodeDate.text = episode.airDate
                root.setOnClickListener {
                    onItemClick(episode)
                }
            }
        }
    }

    private object EpisodeDiffCallback : DiffUtil.ItemCallback<Episode>() {
        override fun areItemsTheSame(oldItem: Episode, newItem: Episode): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Episode, newItem: Episode): Boolean =
            oldItem == newItem
    }
}