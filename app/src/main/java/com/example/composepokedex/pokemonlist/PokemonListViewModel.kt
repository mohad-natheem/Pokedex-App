package com.example.composepokedex.pokemonlist

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.example.composepokedex.data.remote.models.PokedexListEntry
import com.example.composepokedex.repository.PokemonRepository
import com.example.composepokedex.util.Constants.PAGE_SIZE
import com.example.composepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class   PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) :ViewModel() {
    private var currPage = 0
    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isAsc = mutableStateOf(true)
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var sortedPokemonList = listOf<PokedexListEntry>()
    private var pokemonListCopy = listOf<PokedexListEntry>()


    var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init {
        pokemonPaginated()
    }
    fun reloadPokemonList()
    {
        pokemonList.value = listOf()
        pokemonList.value = pokemonListCopy
    }
    fun sortPokemon(){
        viewModelScope.launch(Dispatchers.Default){
            isLoading.value = true
            if (isAsc.value) {
                sortedPokemonList = pokemonListCopy.sortedBy { pokemon -> pokemon.pokemonName }
                isAsc.value = false
            }
            else{
            sortedPokemonList =
                pokemonListCopy.sortedByDescending { pokemon -> pokemon.pokemonName }
            isAsc.value = true
        }
            pokemonList.value = listOf()
            pokemonList.value = sortedPokemonList
            isLoading.value = false
            Log.i("sorted List", "-----------------PokemonListSorted-----------------------")
            Log.i("List", sortedPokemonList.toString())
            Log.i("Pokemon List", pokemonList.value.toString())
        }
    }


    fun searchPokemonList(query: String) {
        val listToSearch = if (isSearchStarting) {
            pokemonList.value
        } else {
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(query, ignoreCase = true) ||
                        it.number.toString() == query.trim()
            }
            if (isSearchStarting) {
                cachedPokemonList = pokemonList.value
                isSearchStarting = false

            }
            pokemonList.value = results
            isSearching.value = true
        }

    }

    fun pokemonPaginated() {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(100000,0)
            when (result) {
                is Resource.Success -> {
                    endReached.value = currPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else {
                            entry.url.takeLastWhile { it.isDigit() }
                        }
                        val url =
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.capitalize(Locale.ROOT), url, number.toInt())
                    }
                    currPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                    pokemonListCopy += pokedexEntries
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                else -> {}
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {

//            val bmp = Bitmap.createBitmap(
//                drawable.intrinsicWidth,
//                drawable.intrinsicHeight,
//                Bitmap.Config.ARGB_8888
//            )
//            val bitmap = drawable.toBitmap()
//
//            Palette.from(bitmap).generate { palette ->
//                palette?.dominantSwatch?.rgb?.let { colorValue ->
//                    onFinish(Color(colorValue))
//                }
//            }
        viewModelScope.launch(Dispatchers.IO) {
            val bitmap = (drawable as BitmapDrawable).bitmap

            Palette.from(bitmap).generate { palette ->
                palette?.dominantSwatch?.rgb?.let { colorValue ->
                    onFinish(Color(colorValue))

                }


            }


        }
    }
}