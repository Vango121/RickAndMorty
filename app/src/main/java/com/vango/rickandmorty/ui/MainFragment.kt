package com.vango.rickandmorty.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vango.rickandmorty.MainActivity
import com.vango.rickandmorty.R
import com.vango.rickandmorty.databinding.MainFragmentBinding
import com.vango.rickandmorty.model.Results
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor() : Fragment(), CharacterListAdapter.Interaction {

    private lateinit var binding: MainFragmentBinding
    private var favourites: List<Results> = ArrayList() // list with favourite characters
    private var characterList: List<Results> = ArrayList() // list with favourite characters
    private lateinit var charcterListAdapter: CharacterListAdapter // recyclerview adapter

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels() //create viewmodel using delegation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.main_fragment, container, false
            )
        viewModel.getAllCharacters().observe(viewLifecycleOwner, {
            charcterListAdapter.submitList(it) // submit list to recyclerview
            characterList = it
            viewModel.setList(it) //pass list to viewmodel
            binding.splash.visibility = View.GONE // hide splash screen when data is ready
        })
        viewModel.favourites.observe(viewLifecycleOwner, {
            favourites = it
            charcterListAdapter.passFavourites(it)
        })
        viewModel.characters.observe(viewLifecycleOwner, {
            charcterListAdapter.submitList(it)
        })
        viewModel.favButtonEnabled.observe(viewLifecycleOwner, {
            charcterListAdapter.setEnabled(it)
        })
        GlobalScope.launch {
            viewModel.getnewFav()
        }
        binding.mainViewModel = viewModel
        initRecycler() // init recycler view

        return binding.root
    }

    private fun initRecycler() {
        binding.characterRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            charcterListAdapter = CharacterListAdapter(this@MainFragment)
            adapter = charcterListAdapter
        }
    }

    override fun onItemSelected(position: Int, item: Results) {
        (activity as MainActivity?)?.replaceFragment(CharacterDetails::class.java, item)
    }

    override fun onStarSelected(position: Int, item: Results, favourite: Boolean) {
        if (!favourites.contains(item)) {
            viewModel.addFavourite(item)
        } else {
            viewModel.removeFavourite(item)
        }
    }
}