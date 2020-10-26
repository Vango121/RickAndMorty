package com.vango.rickandmorty.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vango.rickandmorty.R
import com.vango.rickandmorty.model.Results
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.android.synthetic.main.character_row.view.*
import javax.inject.Inject

class MainAdapter constructor(
    context: Context,
    val modelList: List<Results>,
    val characterOnClickRecycler: CharacterOnClickRecycler
): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    var favourites : MutableList<Int> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.getContext()).inflate(R.layout.character_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(modelList.get(position).name)
        holder.gender.setText(modelList.get(position).gender)
        holder.status.setText(modelList.get(position).status)
        Picasso.get().load(modelList.get(position).image).into(holder.avatar)
    }

    override fun getItemCount(): Int = modelList.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.name
        val gender : TextView = itemView.gender
        val status : TextView = itemView.status
        val avatar : ImageView = itemView.avatar
        val star : ImageView = itemView.star
        val containerRow = itemView.containerRow
        init {
            //adding to favourite
            star.setOnClickListener {
                if (favourites.contains(adapterPosition)) {
                    Log.i("clicked","contains")
                    favourites.remove(adapterPosition)
                    star.setBackgroundResource(R.drawable.outline_star_rate_black_24)
                }else{
                    Log.i("clicked","dont contains")
                    favourites.add(adapterPosition)
                    star.setBackgroundResource(R.drawable.baseline_star_rate_black_24)
                }
            }

            containerRow.setOnClickListener{
                characterOnClickRecycler.characterClicked(adapterPosition)
            }
        }

    }
}