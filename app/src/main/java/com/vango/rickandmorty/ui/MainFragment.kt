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
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor() : Fragment()
    , CharacterListAdapter.Interaction{

    lateinit var binding: MainFragmentBinding
    var favourites : MutableList<Results> = ArrayList() // list with favourite characters
    var characterList : List<Results> = ArrayList() // list with favourite characters
    lateinit var charcterListAdapter : CharacterListAdapter // recyclerview adapter
     var isFavourites : Boolean = false // check if favourites are active ( activate on buttom "favourite" click)
    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()//create viewmodel using delegation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.main_fragment, container, false
            )

//        viewModel.characters.observe(viewLifecycleOwner,{
//            Log.i("size", it.size.toString())
//            chracterListAdapter.submitList(it)
//            //setRecyclerView(it)
//        })
        viewModel.getAllCharacters().observe(viewLifecycleOwner, {
            charcterListAdapter.submitList(it)
            characterList = it
            viewModel.setList(it)
        })
        initRecycler() // init recycler view
        initOnClick() // init onclick/onchecked listeners
        return binding.root
    }

    private fun initOnClick(){
        binding.favourite.setOnClickListener{// star button - favourites list
            if(!isFavourites){ //was false now clicked so change to true and submit list
                charcterListAdapter.submitList(favourites)
                isFavourites=true
                charcterListAdapter.setEnabled(isFavourites)
            }else if(isFavourites){
                if(binding.radioButtonAlive.isChecked){
                    charcterListAdapter.submitList(viewModel.filterCharacters("Alive"))
                }
                else if(binding.radioButtonDead.isChecked){
                    Log.i("dead", binding.radioButtonDead.isChecked.toString())
                    charcterListAdapter.submitList(viewModel.filterCharacters("Dead"))
                } else{
                    charcterListAdapter.submitList(characterList)
                }
                isFavourites=false
                charcterListAdapter.setEnabled(isFavourites)
            }
        }
        binding.radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
                val radioButton: View = binding.radio.findViewById(p1)
                val index: Int = binding.radio.indexOfChild(radioButton)
                when (index) {
                    0 -> charcterListAdapter.submitList(characterList) // all
                    1 -> charcterListAdapter.submitList(viewModel.filterCharacters("Alive"))
                    2 -> charcterListAdapter.submitList(viewModel.filterCharacters("Dead"))
                }
            }

        })
    }
    private fun initRecycler(){
        binding.characterRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            charcterListAdapter = CharacterListAdapter(this@MainFragment)
            adapter= charcterListAdapter
        }
    }

    override fun onItemSelected(position: Int, item: Results) {
        (activity as MainActivity?)?.replaceFragment(CharacterDetails::class.java, item)
    }

    override fun onStarSelected(position: Int, item: Results, favourite: Boolean) {
        if(!favourites.contains(item)){
            favourites.add(item)
        }else{
            var id = favourites.indexOf(item)
            favourites.removeAt(id)
        }
    }
}