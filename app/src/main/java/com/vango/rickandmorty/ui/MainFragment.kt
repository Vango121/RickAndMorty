package com.vango.rickandmorty.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor() : Fragment(), CharacterListAdapter.Interaction {

    lateinit var binding: MainFragmentBinding
    var favourites: List<Results> = ArrayList() // list with favourite characters
    var characterList: List<Results> = ArrayList() // list with favourite characters
    lateinit var charcterListAdapter: CharacterListAdapter // recyclerview adapter
    var isFavourites : Boolean = false // check if favourites are active ( activate on buttom "favourite" click)

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
        //viewModel.getFavourites()
        GlobalScope.launch {
            viewModel.getnewFav()
        }
        initRecycler() // init recycler view
        initOnClick() // init onclick/onchecked listeners

        return binding.root
    }

    override fun onResume() {
        setCharacterListToAdapter()
        super.onResume()
    }

    fun setCharacterListToAdapter() {
        if (binding.radioButtonAlive.isChecked) {
            charcterListAdapter.submitList(viewModel.getFilteredList("Alive"))
        } else if (binding.radioButtonDead.isChecked) {
            charcterListAdapter.submitList(viewModel.getFilteredList("Dead"))
        } else if (binding.radioButtonUnknown.isChecked) {
            charcterListAdapter.submitList(viewModel.getFilteredList("unknown"))
        } else {
            charcterListAdapter.submitList(characterList)
        }
    }

    private fun initOnClick() {
        binding.favourite.setOnClickListener{// star button - favourites list
            if(!isFavourites){ //was false now clicked so change to true and submit list
                charcterListAdapter.submitList(favourites)
                isFavourites=true
                charcterListAdapter.setEnabled(isFavourites)
            }else if(isFavourites){
                setCharacterListToAdapter()
                isFavourites=false
                charcterListAdapter.setEnabled(isFavourites)
            }
        }
        binding.radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                val radioButton: View = binding.radio.findViewById(p1)
                val index: Int = binding.radio.indexOfChild(radioButton)
                when (index) {
                    0 -> charcterListAdapter.submitList(characterList)
                    // all
                    1 -> charcterListAdapter.submitList(viewModel.getFilteredList("Alive"))
                    2 -> charcterListAdapter.submitList(viewModel.getFilteredList("Dead"))
                    3 -> charcterListAdapter.submitList(viewModel.getFilteredList("unknown"))
                }
            }

        })
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