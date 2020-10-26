package com.vango.rickandmorty

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.vango.rickandmorty.databinding.MainFragmentBinding
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.ui.CharacterListAdapter
import com.vango.rickandmorty.ui.CharacterOnClickRecycler
import com.vango.rickandmorty.ui.MainAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor() : Fragment()
    , CharacterOnClickRecycler,
    CharacterListAdapter.Interaction{

    lateinit var binding: MainFragmentBinding

    lateinit var chracterListAdapter : CharacterListAdapter
    lateinit var mainAdapter : MainAdapter
    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()//create viewmodel using delegation

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.main_fragment, container, false
            )

        viewModel.print()
//        viewModel.characters.observe(viewLifecycleOwner,{
//            Log.i("size", it.size.toString())
//            chracterListAdapter.submitList(it)
//            //setRecyclerView(it)
//        })
        viewModel.getAllCharacters().observe(viewLifecycleOwner,{
            chracterListAdapter.submitList(it)
        })
        initRecycler()
        return binding.root
    }

    fun setRecyclerView(list : List<Results>){
        mainAdapter= context?.let { MainAdapter(it,list,this) }!!
        binding.characterRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mainAdapter
        }
    }

    private fun initRecycler(){
        binding.characterRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            chracterListAdapter = CharacterListAdapter(this@MainFragment)
            adapter= chracterListAdapter
        }
    }
    override fun characterClicked(position: Int) {
        Log.i("clickedAdapter",position.toString())
        //TODO("Not yet implemented")
    }

    override fun onItemSelected(position: Int, item: Results) {
        Log.i("clickedAdapter",position.toString())
    }

    override fun onStarSelected(position: Int, item: Results, favourite: Boolean) {
        Log.i("star",position.toString()+" "+ favourite.toString())
    }
}