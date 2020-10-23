package com.vango.rickandmorty.ui

import android.content.Context
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
import javax.inject.Inject

class MainAdapter constructor(
    context: Context,
    val modelList: List<Results>
): RecyclerView.Adapter<MainAdapter.ViewHolder>() {


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
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name : TextView = itemView.findViewById(R.id.name)
        val gender : TextView = itemView.findViewById(R.id.gender)
        val status : TextView = itemView.findViewById(R.id.status)
        val avatar : ImageView = itemView.findViewById(R.id.avatar)
    }
}