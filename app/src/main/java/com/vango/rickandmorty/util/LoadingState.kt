package com.vango.rickandmorty.util

sealed class LoadingState{ // class representing loading state for pagination
    object loading : LoadingState()
    object wait : LoadingState()
}
