package com.example.firebasetest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Item(val id: String, val title: String, val content: String, val price: Int, val isSold: Boolean, val sellerID: String, val sellerEmail: String) {
    constructor(doc: QueryDocumentSnapshot) :
            this(
                doc.id,
                doc["title"].toString(),
                doc["content"].toString(),
                doc["price"].toString().toIntOrNull() ?: 0,
                doc["isSold"] as? Boolean ?: false,
                doc["sellerID"].toString(),
                doc.getString("sellerEmail") ?: ""
            )

    constructor(key: String, map: Map<*, *>) :
            this(
                key,
                map["title"].toString(),
                map["content"].toString(),
                map["price"].toString().toIntOrNull() ?: 0,
                map["isSold"] as? Boolean ?: false,
                map["sellerID"].toString(),
                map["sellerEmail"].toString()
            )
}




class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)

class MyAdapter(private val context: Context, private var sales: List<Item>)
    : RecyclerView.Adapter<MyViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(item: Item)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun updateList(newList: List<Item>) {
        sales = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = sales[position]
        holder.view.findViewById<TextView>(R.id.textTitle).text = "제목: ${item.title}"
        holder.view.findViewById<TextView>(R.id.textIsSold).text ="판매여부: ${if (item.isSold) "판매중" else "판매완료"}"
        holder.view.findViewById<TextView>(R.id.textPrice).text = "가격: ${item.price}"
        holder.view.findViewById<TextView>(R.id.textTitle).setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
        holder.view.findViewById<TextView>(R.id.textPrice).setOnClickListener {
            itemClickListener?.onItemClick(item)
        }

    }

    override fun getItemCount() = sales.size
}
