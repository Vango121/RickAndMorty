package com.vango.rickandmorty.ui

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.squareup.picasso.Picasso
import com.vango.rickandmorty.R
import com.vango.rickandmorty.model.Results
import kotlinx.android.synthetic.main.character_row.view.*

class CharacterListAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isStarEnabled = false
    var favourites: MutableList<Results> = ArrayList()
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Results>() {

        override fun areItemsTheSame(oldItem: Results, newItem: Results): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Results, newItem: Results): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CharacterVIewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.character_row,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CharacterVIewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<Results>) {
        differ.submitList(list)
    }

    fun setEnabled(boolean: Boolean) {
        isStarEnabled = boolean
    }

    fun passFavourites(fav: List<Results>) {
        favourites = fav as MutableList<Results>
    }

    inner class CharacterVIewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: Results) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(bindingAdapterPosition, item)
            }
            if (favourites.contains(item)) {
                itemView.star.setBackgroundResource(R.drawable.baseline_star_rate_black_24)
            } else {
                itemView.star.setBackgroundResource(R.drawable.outline_star_rate_black_24)
            }
            itemView.star.setOnClickListener {
                if (favourites.contains(item) && !isStarEnabled) { // star on click  when in favourites it is disabled
                    interaction?.onStarSelected(bindingAdapterPosition, item, false)
                    itemView.star.setBackgroundResource(R.drawable.outline_star_rate_black_24)
                } else if (!favourites.contains(item.id) && !isStarEnabled) {
                    itemView.star.setBackgroundResource(R.drawable.baseline_star_rate_black_24)
                    interaction?.onStarSelected(bindingAdapterPosition, item, true)
                }

            }

            itemView.name.setText(item.name)
            itemView.gender.setText(item.gender)
            itemView.status.setText(item.status)
            Picasso.get()
                .load(item.image)
                .into(itemView.avatar)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Results)
        fun onStarSelected(position: Int, item: Results, favourite: Boolean)
    }
}