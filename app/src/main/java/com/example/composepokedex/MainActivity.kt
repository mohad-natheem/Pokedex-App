package com.example.composepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composepokedex.pokemondetail.PokemonDetailScreen
import com.example.composepokedex.pokemonlist.PokemonListScreen
import com.example.composepokedex.pokemonlist.PokemonListViewModel
import com.example.composepokedex.ui.theme.ComposePokedexTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePokedexTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "pokemon_list_screen" ){
                    composable(route = "pokemon_list_screen"){
                        PokemonListScreen(navController = navController)
                    }
                    composable(
                        route = "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                        arguments = listOf(
                            navArgument("dominantColor"){
                                type = NavType.IntType
                            },
                            navArgument("pokemonName"){
                                type = NavType.StringType
                            }
                        )
                    ){
                        val dominantColor = remember{
                            val color = it.arguments?.getInt("dominantColor")
                            color?.let { Color(it) }?:Color.White

                        }
                        val pokemonName = remember{
                            it.arguments?.getString("pokemonName")
                        }
                        PokemonDetailScreen(dominantColor = dominantColor, pokemonName = pokemonName?.toLowerCase(
                            Locale.ROOT)?:"", navController = navController)


                    }
                }

            }
        }
    }
}
