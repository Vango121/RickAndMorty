package com.vango.rickandmorty.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vango.rickandmorty.MainActivity
import com.vango.rickandmorty.R
import com.vango.rickandmorty.databinding.MainFragmentBinding
import com.vango.rickandmorty.model.Results
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor() : Fragment(), CharacterListAdapter.Interaction {

    private lateinit var binding: MainFragmentBinding
    private lateinit var charcterListAdapter: CharacterListAdapter // recyclerview adapter
    private var favourites = false
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
            viewModel.setList(it) //pass list to viewmodel
            if (it.size > 0) {
                binding.splash.visibility = View.GONE // hide splash screen when data is ready
            }
        })
        viewModel.favourites.observe(viewLifecycleOwner, {
            charcterListAdapter.passFavourites(it)
            for (result in it){
                charcterListAdapter.notifyItemChanged(result.id)
            }
        })
        GlobalScope.launch {
            viewModel.getnewFav()
            viewModel.getDataFromWeb()
        }
        viewModel.paginationLiveData.observe(viewLifecycleOwner,{
            charcterListAdapter.submitList(it)
        })
        viewModel.characters.observe(viewLifecycleOwner, {
                charcterListAdapter.submitList(it)
        })
        viewModel.favButtonEnabled.observe(viewLifecycleOwner, {
            charcterListAdapter.setEnabled(it)
            favourites = it
        })
        setBackButton()
        initRecycler() // init recycler view
        binding.mainViewModel = viewModel

        return binding.root
    }
    fun setBackButton() { // add back button
        val actionBar: ActionBar? = (activity as MainActivity?)?.getSupportActionBar()
        actionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(false)
    }

    private var loading = true
    var pastVisiblesItems = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var count = 1
    private fun initRecycler() {
        binding.characterRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            charcterListAdapter = CharacterListAdapter(this@MainFragment)
            adapter = charcterListAdapter
        }
        binding.characterRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() { // pagination
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)&&!favourites) { //check for end of current items
                    visibleItemCount =
                        recyclerView.childCount
                    totalItemCount =
                        (recyclerView.layoutManager as LinearLayoutManager).itemCount
                    pastVisiblesItems =
                        (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false
                            runBlocking {
                                ++count
                                viewModel.changePage(count)
                                loading = true
                            }

                        }
                    }
                }
            }
        })
    }

    override fun onItemSelected(position: Int, item: Results) {
        (activity as MainActivity?)?.replaceFragment(CharacterDetails::class.java, item)
    }

    override fun onStarSelected(position: Int, item: Results, favourite: Boolean) {
        viewModel.favClicked(item)
    }
}