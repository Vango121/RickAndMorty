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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CharacterDetails : Fragment() {

    lateinit var binding: CharacterDetailsFragmentBinding

    companion object {
        fun newInstance() = CharacterDetails()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    fun setBackButton() { // add back button
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

    fun getDataFromPreviousFragment(): Results {
        val characterGson: String? = arguments?.getString("character")
        return Json.decodeFromString(characterGson!!)
    }

    fun setUiElements(results: Results) {
        binding.textViewName.setText(results.name)
        binding.textViewGender.text = getString(R.string.gender, results.gender)
        binding.textViewLocation.text = getString(R.string.location, results.location.name)
        binding.textViewType.text = getString(R.string.type, results.type)
        binding.textViewOrigin.text = getString(R.string.origin, results.origin.name)
        binding.textViewSpecies.text = getString(R.string.species, results.species)
        binding.textViewStatus.text = getString(R.string.status, results.status)
        binding.textViewEpisode.text =
            getString(R.string.number_of_episodes, results.episode.size.toString())
        Picasso.get()
            .load(results.image)
            .into(binding.imageViewAvatar)
    }


}