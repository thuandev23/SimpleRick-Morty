package com.thuan.simplemultimodule.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.thuan.network.models.ApiOperation
import com.thuan.network.models.KtorClient
import com.thuan.network.models.domain.Character
import com.thuan.simplemultimodule.component.character.CharacterDetailsNamePlateComponent
import com.thuan.simplemultimodule.component.common.DataPoint
import com.thuan.simplemultimodule.component.common.DataPointComponent
import com.thuan.simplemultimodule.component.common.LoadingState
import com.thuan.simplemultimodule.ui.theme.RickAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterRepository @Inject constructor (private val ktorClient: KtorClient) {
    suspend fun fetchCharacter(characterId: Int): ApiOperation<Character> {
        return ktorClient.getCharacter(characterId)
    }
}

@HiltViewModel
class CharacterDetailsViewModel @Inject constructor(
    private val characterRepository: CharacterRepository
): ViewModel() {
    private val _internalStorageFlow = MutableStateFlow<CharacterDetailsViewState>(
        value = CharacterDetailsViewState.Loading
    )
    val stateFlow = _internalStorageFlow.asStateFlow()

    fun fetchCharacter(characterId: Int) = viewModelScope.launch {
        _internalStorageFlow.update { return@update CharacterDetailsViewState.Loading }
        characterRepository.fetchCharacter(characterId).onSuccess { character ->
            val dataPoints = buildList {
                add(DataPoint("Last known location", character.location.name))
                add(DataPoint("Species", character.species))
                add(DataPoint("Gender", character.gender.displayName))
                character.type.takeIf { it.isNotEmpty() }?.let { type ->
                    add(DataPoint("Type", type))
                }
                add(DataPoint("Origin", character.origin.name))
                add(DataPoint("Episode count", character.episodeIds.size.toString()))
            }
            _internalStorageFlow.update {
                return@update CharacterDetailsViewState.Success(
                    character = character,
                    characterDataPoints = dataPoints
                )
            }
        }.onFailure { exception ->
            _internalStorageFlow.update {
                return@update CharacterDetailsViewState.Error(
                    message = exception.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

sealed interface CharacterDetailsViewState {
    object Loading : CharacterDetailsViewState
    data class Error(val message: String) : CharacterDetailsViewState
    data class Success(
        val character: Character,
        val characterDataPoints: List<DataPoint>
    ) : CharacterDetailsViewState
}

@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    viewModel: CharacterDetailsViewModel = hiltViewModel(),
    onEpisodeClick: (Int) -> Unit
) {

    LaunchedEffect(key1 = Unit, block = {
        delay(200)
        viewModel.fetchCharacter(characterId)
    })
val state by viewModel.stateFlow.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)
    ) {
        when (val viewState = state) {
            CharacterDetailsViewState.Loading -> item { LoadingState() }
            is CharacterDetailsViewState.Error -> {
                // todo
            }

            is CharacterDetailsViewState.Success -> {


        // Name Plate
        item {
            CharacterDetailsNamePlateComponent(
                name = viewState.character.name,
                status = viewState.character.status ,
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            SubcomposeAsyncImage(
                loading = { LoadingState() },
                model = viewState.character.imageUrl,
                contentDescription = "Character Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        items(viewState.characterDataPoints){
            Spacer(modifier = Modifier.height(32.dp))
            DataPointComponent(dataPoint = it)
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        item {
            Text(
                text = "View all episodes",
                color = RickAction,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .border(
                        width = 1.dp,
                        color = RickAction,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onEpisodeClick(characterId)
                    }
                    .padding(vertical = 8.dp)
                    .fillMaxWidth()
            )
        }

        item { Spacer(modifier = Modifier.height(64.dp)) }
    }}}
}