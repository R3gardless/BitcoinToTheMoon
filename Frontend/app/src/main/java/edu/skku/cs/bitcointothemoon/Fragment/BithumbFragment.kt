package edu.skku.cs.bitcointothemoon.Fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.skku.cs.bitcointothemoon.Adapter.CoinListRecyclerViewAdapter
import edu.skku.cs.bitcointothemoon.DataModel.CoinInfo
import edu.skku.cs.bitcointothemoon.R
import edu.skku.cs.bitcointothemoon.ViewModel.UserViewModel
import edu.skku.cs.bitcointothemoon.databinding.FragmentBithumbBinding
import edu.skku.cs.bitcointothemoon.databinding.FragmentCoinListBinding
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException


class BithumbFragment : Fragment() {

    private var _binding : FragmentBithumbBinding? = null
    private val binding get() = _binding!!

    private val client = OkHttpClient()
    private val host = "http://127.0.0.1:8000"
    private val path = "/coin"
    private val myViewModel: UserViewModel by activityViewModels()

    private var username : String? = null
    private var nickname : String? = null
    private val coinList: ArrayList<CoinInfo>  = ArrayList()
    private var etBithumb : EditText? = null

    private var bithumbJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBithumbBinding.inflate(inflater, container, false)
        val view = binding.root

        username = myViewModel.getUsername()
        nickname = myViewModel.getNickname()
        etBithumb = binding.etBithumb
        val recylcerAdapter = CoinListRecyclerViewAdapter(container!!.context, coinList, username!!)
        binding.rvBithumb.adapter = recylcerAdapter
        binding.rvBithumb.layoutManager= LinearLayoutManager(activity)
        getBithumb()
        return view
    }

    override fun onPause() {
        super.onPause()
        bithumbJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun getBithumb() {
        bithumbJob = CoroutineScope(Dispatchers.Default).async{
            while(true) {
                val getpath = "/bithumb?username=${username}&coin_api_name=${etBithumb?.text.toString()}"
                val req = Request.Builder().url(host+path+getpath).build()
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {  }
                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val data = response.body!!.string()
                            val typeToken = object : TypeToken<ArrayList<CoinInfo>>() {}.type
                            activity?.runOnUiThread {
                                coinList.clear()
                                binding.rvBithumb.adapter?.notifyDataSetChanged()
                                coinList.addAll(Gson().fromJson(data, typeToken))
                                binding.rvBithumb.adapter?.notifyDataSetChanged()
                            }
                        }
                    }
                })
                delay(2000)
            }
        }
    }
}