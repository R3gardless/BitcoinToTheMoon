package edu.skku.cs.bitcointothemoon.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.skku.cs.bitcointothemoon.Adapter.DataModel
import edu.skku.cs.bitcointothemoon.Adapter.NewsRecyclerViewAdapter
import edu.skku.cs.bitcointothemoon.databinding.FragmentNewsBinding
import okhttp3.*
import java.io.IOException
import java.time.LocalDate


class NewsFragment : Fragment() {

    private var _binding : FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val client = OkHttpClient()
    private val host = "https://newsapi.org"
    private val path = "/v2/everything"
    private var newsList : ArrayList<DataModel.News> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root

        val newsRecyclerAdapter = NewsRecyclerViewAdapter(container!!.context, newsList)

        binding.rvNews.adapter = newsRecyclerAdapter
        binding.rvNews.layoutManager = LinearLayoutManager(activity)

        val today = LocalDate.now()
        val yesterday = today.minusDays(1).toString()
        val api_key = "News api API Key"
        val query="?q=cryptocurrency&from=${yesterday}&language=en&pageSize=70&sortBy=popularity&apikey=${api_key}"
        val req = Request.Builder()
                    .url(host+path+query).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {  }
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    val data = response.body!!.string()
                    val typeToken = object : TypeToken<DataModel.Newsdata>() {}.type
                    val newsListdata = Gson().fromJson<DataModel.Newsdata>(data, typeToken)
                    activity?.runOnUiThread {
                        newsList.clear()
                        binding.rvNews.adapter?.notifyDataSetChanged()
                        newsList.addAll(newsListdata.articles)
                        binding.rvNews.adapter?.notifyDataSetChanged()
                    }
                }
            }
        })

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}