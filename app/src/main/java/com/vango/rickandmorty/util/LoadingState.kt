package com.vango.rickandmorty.util

sealed class LoadingState{
    object loading : LoadingState()
    object wait : LoadingState()
}
