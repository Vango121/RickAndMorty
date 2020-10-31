package com.vango.rickandmorty.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.vango.rickandmorty.MainActivity
import com.vango.rickandmorty.R
import com.vango.rickandmorty.databinding.CharacterDetailsFragmentBinding
import com.vango.rickandmorty.model.Results

class CharacterDetails : Fragment() {

    lateinit var binding: CharacterDetailsFragmentBinding

    companion object {
        fun newInstance() = CharacterDetails()
    }

    private val viewModel: CharacterDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.character_details_fragment, container, false
            )
        val character = getDataFromPreviousFragment()
        setUiElements(character)
        setBackButton()
        return binding.root
    }

    fun setBackButton(){ // add back button
        val actionBar: ActionBar? = (activity as MainActivity?)?.getSupportActionBar()
        actionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (activity as MainActivity?)!!.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    fun getDataFromPreviousFragment() : Results{
        val characterGson : String? = arguments?.getString("character")
        return Gson().fromJson(characterGson, Results::class.java)
    }
    fun setUiElements(results: Results){
        binding.textViewName.setText(results.name)
        binding.textViewGender.setText("Gender: " + results.gender)
        binding.textViewLocation.setText("Location: " + results.location.name)
        binding.textViewType.setText("Type: " + results.type)
        binding.textViewOrigin.setText("Origin: " + results.origin.name)
        binding.textViewSpecies.setText("Species: " + results.species)
        binding.textViewStatus.setText("Status: " + results.status)
        binding.textViewEpisode.setText("Number of episodes: " + results.episode.size)
        Picasso.get()
            .load(results.image)
            .into(binding.imageViewAvatar)
    }


}