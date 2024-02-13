package com.example.composepokedex.pokemonlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.composepokedex.R
import com.example.composepokedex.data.remote.models.PokedexListEntry
import com.example.composepokedex.ui.theme.RobotoCondensed



@Composable
fun PokemonListScreen(
    navController: NavController,
    viewmodel : PokemonListViewModel = hiltViewModel<PokemonListViewModel>()
) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "PokemonLogo",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(CenterHorizontally)
            )
            SearchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                viewmodel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(20.dp))
//            PokemonList(navController = navController)
            PokiList(navController = navController)
        }

    }
}


@Composable
fun SearchBar(
    modifier:Modifier = Modifier,
    hint: String = "",
    onSearch:(String) -> Unit = {}
) {
    var text by remember{
        mutableStateOf("")
    }
    var isHintDisplayed by remember{
        mutableStateOf(hint != "")
    }
    Box(modifier = modifier){
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }

        )
        if(isHintDisplayed){
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }

}

@Preview(showBackground = true, widthDp = 320, heightDp = 720)
@Composable
fun PokemonListScreenPreview() {
    PokemonListScreen(navController = rememberNavController())

}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel : PokemonListViewModel = hiltViewModel<PokemonListViewModel>()
) {
    val pokemonList by remember {viewModel.pokemonList}
    val endReached by remember {viewModel.endReached}
    val loadError by remember {viewModel.loadError}
    val isLoading by remember {viewModel.isLoading}
    val isSearching by remember{ viewModel.isSearching}

    LazyColumn(contentPadding = PaddingValues(16.dp)){
        val itemCount = if(pokemonList.size %2 ==0){
            pokemonList.size/2
        }else{
            pokemonList.size/2 + 1
        }
        items(itemCount){
            if(it >= itemCount - 1 && !endReached && !isLoading && !isSearching){
                LaunchedEffect(key1 = true){
                    viewModel.pokemonPaginated()
                }            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if(loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.pokemonPaginated()
            }
        }
    }
}

@Composable
fun PokiList(
    navController: NavController,
    viewModel : PokemonListViewModel = hiltViewModel<PokemonListViewModel>()
) {
    val pokemonList by remember {viewModel.pokemonList}
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(pokemonList){entry ->
            PokedexEntry(entry = entry, navController = navController )
        }
    }
}

@Composable
fun PokedexEntry(
    entry : PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewmodel : PokemonListViewModel = hiltViewModel<PokemonListViewModel>()
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    val color = colorResource(id = R.color.purple_200)
    var dominantColor by remember{
        mutableStateOf(color)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
//                viewmodel.searchPokemonList("")
                viewmodel.reloadPokemonList()

            }
    ){
        print(".............Image Url:${entry.imageUrl}.......................")
        Column {
//            CoilImage(
//                request = ImageRequest.Builder(LocalContext.current)
//                    .data(entry.imageUrl)
//                    .target{
//                        viewmodel.calcDominantColor(it){color->
//                            dominantColor = color
//
//                        }
//                    }
//                    .build(),
//                contentDescription = entry.pokemonName,
//                fadeIn = true,
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(Alignment.CenterHorizontally)
//            ) {
//                CircularProgressIndicator(
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.scale(0.5f)
//
//                )
//
//            }
//            AsyncImage(
//                model = ImageRequest.Builder(LocalContext.current)
//                    .data(entry.imageUrl)
//                    .target {
//                        viewmodel.calcDominantColor(it){color->
//                            dominantColor = color
//
//                        }
//                    }
//                    .crossfade(true)
//                    .build(),
//                error = painterResource(R.drawable.ic_broken_image),
//                placeholder = painterResource(R.drawable.loading_img),
//                contentDescription = entry.pokemonName,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(120.dp)
//                    .align(CenterHorizontally)
//            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.imageUrl)
                    .crossfade(true)
                    .build()
                ,
                placeholder = painterResource(id = R.drawable.loading_img),
                contentDescription = entry.pokemonName,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
//                onSuccess =
//                {
//                    val drawable = it.result.drawable
//                    viewmodel.calcDominantColor(drawable){color->
//                        dominantColor = color
//
//                    }
//                }

            )
            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }

    }
}


@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController,
    modifier : Modifier = Modifier
) {
    Column {
        Row {
            PokedexEntry(
                entry = entries[rowIndex*2],
                navController = navController,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(16.dp))

            if(entries.size>rowIndex*2 +2){
                PokedexEntry(
                    entry = entries[rowIndex*2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }else{
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}