package edu.skku.cs.bitcointothemoon.DataModel

class Binance (
        val symbol: String,
        val priceChange: String,
        val priceChangePercent:String ,
        val weightedAvgPrice:String,
        val prevClosePrice:String,
        val lastPrice: String,
        val lastQty: String,
        val bidPrice: String,
        val bidQty: String,
        val askPrice: String,
        val askQty: String,
        val openPrice: String,
        val highPrice: String,
        val lowPrice: String,
        val volume: String,
        val quoteVolume: String,
        val openTime: Int,
        val closeTime: Int,
        val firstId: Int,   // First tradeId
        val lastId: Int,    // Last tradeId
        val count: Int         // Trade count
)