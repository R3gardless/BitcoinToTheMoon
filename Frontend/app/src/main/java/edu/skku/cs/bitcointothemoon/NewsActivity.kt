package edu.skku.cs.bitcointothemoon

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_AUTHOR
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_CONTENT
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_DESCRIPTION
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_PUBLISHEDAT
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_SOURCE
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_TITLE
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter.Companion.EXT_URL
import edu.skku.cs.bitcointothemoon.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityNewsBinding

    private var title : String? = null
    private var author : String? = null
    private var publishedAt : String? = null
    private var source : String? = null
    private var description : String? = null
    private var content : String? = null
    private var url : String? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = intent.getStringExtra(EXT_TITLE)
        author = intent.getStringExtra(EXT_AUTHOR)
        publishedAt = intent.getStringExtra(EXT_PUBLISHEDAT)
        source = intent.getStringExtra(EXT_SOURCE)
        content = intent.getStringExtra(EXT_CONTENT)
        description = intent.getStringExtra(EXT_DESCRIPTION)
        url = intent.getStringExtra(EXT_URL)

        val HTMLregexPattern = "<.*?>".toRegex()
        val MoreRegexPattern = "\\[.*?]".toRegex()
        val AmpersandRegexPattern = "&.*;".toRegex()

        val author_tmp = author?.split(",")

        if(author_tmp != null) author = author_tmp[0]


        content = content?.replace(HTMLregexPattern, "")
        content = content?.replace(MoreRegexPattern, "")
        content = content?.replace(AmpersandRegexPattern, "")

        binding.tvNewsTitle.text = title
        binding.tvAuthor.text = "$author | "
        binding.tvPublishedAt.text = publishedAt
        binding.tvSourceName.text = source
        binding.tvDescription.text = description
        binding.tvContent.text = content
        binding.btnURL.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}