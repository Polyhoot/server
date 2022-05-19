package org.ciphen.polyhoot.game.entities

import org.ciphen.polyhoot.services.entities.Client

data class Player(
    val client: Client,
    val gameId: Int,
    val name: String,
    var score: Int = 0,
)