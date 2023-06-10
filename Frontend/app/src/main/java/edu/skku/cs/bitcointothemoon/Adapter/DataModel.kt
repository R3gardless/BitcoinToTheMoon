package edu.skku.cs.bitcointothemoon.Adapter
class DataModel {
    data class Newsdata(val status: String, val totalResults: Int, val articles: ArrayList<News>)
    data class Source(val id: String, val name : String)
    data class News(val source : Source, val author: String, val title : String, val description : String, val url : String, val urlToImage: String, val publishedAt: String, val content : String)
}