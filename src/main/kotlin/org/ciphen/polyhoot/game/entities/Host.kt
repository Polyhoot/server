package org.ciphen.polyhoot.game.entities

import org.ciphen.polyhoot.services.entities.Client

data class Host(val client: Client, val gameId: Int)