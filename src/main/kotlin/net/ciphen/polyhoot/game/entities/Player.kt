package net.ciphen.polyhoot.game.entities

import net.ciphen.polyhoot.services.entities.Client

data class Player(
    val client: Client,
    val gameId: Int,
    val name: String,
    var score: Int = 0,
)