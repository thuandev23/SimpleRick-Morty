package com.thuan.network.models.domain


sealed class CharacterStatus(val displayName: String) {
    object Alive: CharacterStatus("Alive")
    object Dead: CharacterStatus("Dead")
    object Unknown: CharacterStatus("Unknown")
}