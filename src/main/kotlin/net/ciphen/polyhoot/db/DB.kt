package net.ciphen.polyhoot.db

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object DB {
    private val client = KMongo.createClient(System.getenv("MONGOURI")).coroutine
    val database = client.getDatabase("polyhoot")
}