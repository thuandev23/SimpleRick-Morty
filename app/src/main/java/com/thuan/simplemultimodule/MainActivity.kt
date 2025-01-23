package com.thuan.simplemultimodule

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.thuan.network.models.KtorClient
import com.thuan.simplemultimodule.screens.CharacterDetailsScreen
import com.thuan.simplemultimodule.screens.CharacterEpisodeScreen
import com.thuan.simplemultimodule.ui.theme.RickPrimary
import com.thuan.simplemultimodule.ui.theme.SimpleMultiModuleTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var ktorClient: KtorClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            SimpleMultiModuleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = RickPrimary
                ) {
                    NavHost(navController = navController, startDestination = "character_details") {
                        composable("character_details") {
                            CharacterDetailsScreen(
                                characterId = 114
                            ){
                                navController.navigate("character_episodes/$it")
                            }
                        }
                        composable(route = "character_episodes/{characterId}",
                            arguments = listOf(navArgument("characterId"){type = NavType.IntType})) { backStackEntry ->
                            val characterId :Int = backStackEntry.arguments?.getInt("characterId")?:-1
                            CharacterEpisodeScreen(
                                characterId = characterId,
                                ktorClient = ktorClient,
                                onBackClicked = {navController.navigateUp()}
                            )
                        }
                    }

                }
            }
        }
    }
}
