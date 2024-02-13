package com.example.composepokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.composepokedex.data.remote.responses.Pokemon
import com.example.composepokedex.repository.PokemonRepository
import com.example.composepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonInfoViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokemonName:String):Resource<Pokemon>{
        return repository.getPokemonInfo(pokemonName)

    }

}