package edu.skku.cs.bitcointothemoon.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import edu.skku.cs.bitcointothemoon.Fragment.IntroFragment
import edu.skku.cs.bitcointothemoon.MainActivity
import edu.skku.cs.bitcointothemoon.NewsActivity
import edu.skku.cs.bitcointothemoon.R
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NewsRecyclerViewAdapter(val context: Context, val newsList: ArrayList<DataModel.News>) : RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder>(){

    companion object {
        const val EXT_TITLE = "extra_key_title"
        const val EXT_AUTHOR = "extra_key_author"
        const val EXT_PUBLISHEDAT = "extra_key_publishedat"
        const val EXT_SOURCE = "extra_key_source"
        const val EXT_DESCRIPTION = "extra_key_description"
        const val EXT_CONTENT = "extra_key_content"
        const val EXT_URL = "extra_key_url"
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val tvTitle : TextView = view.findViewById(R.id.tvTitle)
        val tvDate : TextView = view.findViewById(R.id.tvDate)
        val tvSource : TextView = view.findViewById(R.id.tvSource)
        val cvNewsCard : CardView = view.findViewById(R.id.cvNews)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_entry, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onBindViewHolder(holder: NewsRecyclerViewAdapter.ViewHolder, position: Int) {

        val outputFormatter = DateTimeFormatter.ofPattern("yy/MM/dd HH:mm:ss")

        val instant = Instant.parse(newsList[position].publishedAt)
        val datetime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formattedDatetime = datetime.format(outputFormatter)

        holder.tvTitle.text = newsList[position].title
        holder.tvDate.text = formattedDatetime
        holder.tvSource.text = newsList[position].source.name
        holder.cvNewsCard.setOnClickListener {
            val intent = Intent(context, NewsActivity::class.java).apply {
                putExtra(EXT_TITLE, newsList[position].title)
                putExtra(EXT_AUTHOR, newsList[position].author)
                putExtra(EXT_PUBLISHEDAT, formattedDatetime)
                putExtra(EXT_SOURCE, newsList[position].source.name)
                putExtra(EXT_CONTENT, newsList[position].content)
                putExtra(EXT_DESCRIPTION, newsList[position].description)
                putExtra(EXT_URL, newsList[position].url)
            }
            context.startActivity(intent)
        }
    }
}