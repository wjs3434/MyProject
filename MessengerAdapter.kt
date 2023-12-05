package com.example.firebasetest

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Messages(val id: String, val buyerId: String, val ownerID: String, val content: String) {
    constructor(doc: QueryDocumentSnapshot) : this(
        doc.id,
        doc["buyerId"]?.toString() ?: "",
        doc["ownerID"]?.toString() ?: "",
        doc["content"]?.toString() ?: ""
    )

    constructor(key: String, map: Map<*, *>) : this(
        key,
        map["buyerId"]?.toString() ?: "",
        map["ownerID"]?.toString() ?: "",
        map["content"]?.toString() ?: ""
    )
}




class MessengerAdapter(private val context: Context, private var messages: List<Messages>)
    : RecyclerView.Adapter<MyViewHolder>() {

    fun interface OnItemClickListener {
        fun onItemClick(item: Item)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun updateList(newList: List<Messages>) {
        messages = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = messages[position]
        holder.view.findViewById<TextView>(R.id.textSender).text = "보내는 사람: ${item.buyerId}"
        holder.view.findViewById<TextView>(R.id.textContent).text = "내용: ${item.content}"
        holder.view.findViewById<TextView>(R.id.textownerID).text = "받는 사람: ${item.ownerID}"
    }

    override fun getItemCount(): Int = messages.size

}
