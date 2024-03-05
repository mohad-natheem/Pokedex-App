package com.example.composepokedex.data.remote.models

import androidx.compose.runtime.Immutable


@Immutable
data class PokedexListEntry(
    val pokemonName : String,
    val imageUrl : String,
    val number : Int
)
