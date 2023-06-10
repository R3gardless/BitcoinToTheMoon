package edu.skku.cs.bitcointothemoon.Adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import edu.skku.cs.bitcointothemoon.DataModel.CoinInfo
import edu.skku.cs.bitcointothemoon.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class CoinListRecyclerViewAdapter(val context: Context, val coinInfoList : ArrayList<CoinInfo>, val username: String): RecyclerView.Adapter<CoinListRecyclerViewAdapter.ViewHolder>() {

    private data class Like(val username : String, val coin_id: Int)
    private data class DisLike(val username : String, val coin_id : Int)

    private val likeCoinList = ArrayList<Int>()
    private val client = OkHttpClient()
    private val host = "http://127.0.0.1:8000"
    private val path = "/coin"

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val tvName : TextView = view.findViewById(R.id.tvName)
        val tvName2 : TextView = view.findViewById(R.id.tvName2)
        val tvPrice : TextView = view.findViewById(R.id.tvPrice)
        val ivLike : ImageView = view.findViewById(R.id.ivLike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.coin_entry, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return coinInfoList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ivLike = holder.ivLike
        holder.tvName.text = coinInfoList[position].name
        holder.tvName2.text = coinInfoList[position].api_name
        holder.tvPrice.text = coinInfoList[position].price
        if(coinInfoList[position].change == 1) holder.tvPrice.setTextColor(Color.parseColor("#FF0000"))
        else if(coinInfoList[position].change == -1) holder.tvPrice.setTextColor(Color.parseColor("#0067a3"))
        else holder.tvPrice.setTextColor(Color.parseColor("#000000"))

        if(coinInfoList[position].like == 0) {
            holder.ivLike.setImageResource(R.drawable.star)
        }
        else {
            holder.ivLike.setImageResource(R.drawable.star_fill)
            if(!likeCoinList.contains(coinInfoList[position].id)) likeCoinList.add(coinInfoList[position].id)
        }

        ivLike.setOnClickListener {
            // 즐겨찾기 삭제
            if(likeCoinList.contains(coinInfoList[position].id)) {
                likeCoinList.remove(coinInfoList[position].id)
                // api 통신
                val json = Gson().toJson(DisLike(username, coinInfoList[position].id))
                val mediaType = "application/json; charset=utf-8".toMediaType()

                val req = Request.Builder().url(host + path + "/delete").post(json.toString().toRequestBody(mediaType)).build()

                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }
                    override fun onResponse(call: Call, response: Response) {
                        response.use{
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        }
                    }
                })

            } else {
                // 즐겨찾기 추가
                likeCoinList.add(coinInfoList[position].id)
                // api 통신
                val json = Gson().toJson(Like(username, coinInfoList[position].id))
                val mediaType = "application/json; charset=utf-8".toMediaType()

                val req = Request.Builder().url(host + path).post(json.toString().toRequestBody(mediaType)).build()
                client.newCall(req).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) { e.printStackTrace() }
                    override fun onResponse(call: Call, response: Response) {
                        response.use{
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")
                        }
                    }
                })
            }
        }
    }

}