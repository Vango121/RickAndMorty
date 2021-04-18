package com.vango.rickandmorty

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.vango.rickandmorty.model.Results
import com.vango.rickandmorty.ui.MainFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode())
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.host_fragment, MainFragment.newInstance())
                    .commitNow()
        }
    }
    fun replaceFragment(fragmentClass: Class<*>, character: Results) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass.newInstance() as Fragment
            val bundle = Bundle()
            bundle.putString("character", Gson().toJson(character))
            fragment.arguments = bundle
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.host_fragment, fragment!!)
            .addToBackStack("main")
            .commit()
    }
    fun replaceFragment(fragmentClass: Class<*>) {
        var fragment: Fragment? = null
        try {
            fragment = fragmentClass.newInstance() as Fragment
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Insert the fragment by replacing any existing fragment
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.host_fragment, fragment!!)
            .commit()
    }
}