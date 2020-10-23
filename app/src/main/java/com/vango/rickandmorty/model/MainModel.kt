package com.vango.rickandmorty.model

import com.google.gson.annotations.SerializedName

data class MainModel (
   @SerializedName("info") val info : Info,
   @SerializedName("results") val results : List<Results>
)
