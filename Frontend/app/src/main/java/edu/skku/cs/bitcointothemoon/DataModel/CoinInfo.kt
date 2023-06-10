package edu.skku.cs.bitcointothemoon.DataModel

data class CoinInfo(
    var id: Int,
    val name: String,
    val name2: String,
    val api_name: String,
    val exchange: Int,
    val change: Int,
    val like: Int,
    val price: String
)