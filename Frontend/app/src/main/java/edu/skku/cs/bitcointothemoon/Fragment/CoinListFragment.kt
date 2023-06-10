package edu.skku.cs.bitcointothemoon.Fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.skku.cs.bitcointothemoon.Adapter.CoinListRecyclerViewAdapter
import edu.skku.cs.bitcointothemoon.DataModel.CoinInfo
import edu.skku.cs.bitcointothemoon.MainActivity
import edu.skku.cs.bitcointothemoon.R
import edu.skku.cs.bitcointothemoon.ViewModel.UserViewModel
import edu.skku.cs.bitcointothemoon.databinding.FragmentCoinListBinding
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException


class CoinListFragment : Fragment() {

    private var _binding: FragmentCoinListBinding? = null
    private val binding get() = _binding!!

    private val client = OkHttpClient()
    private val host = "http://127.0.0.1:8000"
    private val path = "/coin"
    private val myViewModel: UserViewModel by activityViewModels()

    private var username : String? = null
    private var nickname : String? = null

    private val binancecoinList: ArrayList<CoinInfo>  = ArrayList()
    private val upbitList: ArrayList<CoinInfo>  = ArrayList()
    private val bithumbList: ArrayList<CoinInfo>  = ArrayList()
    private var binanceJob: Job? = null
    private var upbitJob: Job? = null
    private var bithumbJob: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCoinListBinding.inflate(inflater, container,false)
        val view = binding.root

        username = myViewModel.getUsername()
        nickname = myViewModel.getNickname()

        binding.tvNickname.text = "\uD83D\uDC4B $nickname"

        val binanceRecylerAdapter = CoinListRecyclerViewAdapter(container!!.context, binancecoinList, username!!)
        val upbitRecylerAdapter = CoinListRecyclerViewAdapter(container.context, upbitList, username!!)
        val bithumbRecylerAdapter = CoinListRecyclerViewAdapter(container.context, bithumbList, username!!)

        binding.rvBinance.adapter = binanceRecylerAdapter
        binding.rvBinance.layoutManager= LinearLayoutManager(activity)
        binding.rvUpbit.adapter = upbitRecylerAdapter
        binding.rvUpbit.layoutManager = LinearLayoutManager(activity)
        binding.rvBithumb.adapter = bithumbRecylerAdapter
        binding.rvBithumb.layoutManager = LinearLayoutManager(activity)

        getBinance()
        getUpbit()
        getBithumb()
        return view
    }

    override fun onPause() {
        super.onPause()
        binanceJob?.cancel()
        upbitJob?.cancel()
        bithumbJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    fun getBinance() {
        val getpath = "/binance/like?username=${username}&nickname=${nickname}"
        val req = Request.Builder().url(host+path+getpath).build()
        binanceJob = CoroutineScope(Dispatchers.Default).async{
            while(true) {
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {  }
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call, response: Response) {
                        response.use{
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val data = response.body!!.string()
                            val typeToken = object : TypeToken<ArrayList<CoinInfo>>() {}.type
                            activity?.runOnUiThread {
                                binancecoinList.clear()
                                binding.rvBinance.adapter?.notifyDataSetChanged()
                                binancecoinList.addAll(Gson().fromJson(data, typeToken))
                                binding.rvBinance.adapter?.notifyDataSetChanged()
                            }
                        }

                    }
                })
                delay(2000)
            }
        }
    }

    fun getUpbit() {
        val getpath = "/upbit/like?username=${username}&nickname=${nickname}"
        val req = Request.Builder().url(host+path+getpath).build()
        var count = -1
        upbitJob = CoroutineScope(Dispatchers.Default).async{
            while(true) {
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {  }
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call, response: Response) {
                        response.use{
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val data = response.body!!.string()
                            val typeToken = object : TypeToken<ArrayList<CoinInfo>>() {}.type
                            activity?.runOnUiThread {
                                upbitList.clear()
                                binding.rvUpbit.adapter?.notifyDataSetChanged()
                                upbitList.addAll(Gson().fromJson(data, typeToken))
                                binding.rvUpbit.adapter?.notifyDataSetChanged()
                            }
                        }

                    }
                })
                delay(2000)
            }
        }
    }
    fun getBithumb() {
        val getpath = "/bithumb/like?username=${username}&nickname=${nickname}"
        val req = Request.Builder().url(host+path+getpath).build()
        var count = -1
        bithumbJob = CoroutineScope(Dispatchers.Default).async{
            while(true) {
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {  }
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(call: Call, response: Response) {
                        response.use{
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                            val data = response.body!!.string()
                            val typeToken = object : TypeToken<ArrayList<CoinInfo>>() {}.type
                            activity?.runOnUiThread {
                                bithumbList.clear()
                                binding.rvBithumb.adapter?.notifyDataSetChanged()
                                bithumbList.addAll(Gson().fromJson(data, typeToken))
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