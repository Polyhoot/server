package org.ciphen.polyhoot.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

object DB {
    val client = KMongo.createClient(System.getenv("MONGOURI"))
    val database: MongoDatabase = client.getDatabase("polyhoot")
}