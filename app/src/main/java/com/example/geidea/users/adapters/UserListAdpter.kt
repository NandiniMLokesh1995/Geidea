package com.example.geidea.users.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.geidea.R
import com.example.geidea.users.Entities.UserList
import com.example.geidea.users.activities.UserDataActivity
import com.example.geidea.users.interfaces.ClickListener


class UserListAdpter :
    RecyclerView.Adapter<UserListAdpter.MyViewHolder>() {
    lateinit var context:Context
    private var clickListener: ClickListener<UserList>? = null
    private var itemsList= mutableListOf <UserList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_user, parent, false)
       this.context=parent.context
        return MyViewHolder(view)
    }

    fun setData(item: List<UserList>) {
        itemsList = item.toMutableList()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = itemsList[position]
        holder.id.text = item.id.toString()
        holder.fname.text = item.first_name
        holder.lname.text = item.last_name

        holder.itemLayout.setOnClickListener {
                //v -> clickListener!!.onClick(v, item, position)
            Log.d("TAG", "onBindViewHolder: "+holder.absoluteAdapterPosition)
            context.startActivity(Intent(context, UserDataActivity::class.java).putExtra("ID",item.id))
        }
    }

    override fun getItemCount(): Int {
        return itemsList?.size ?:0
    }

    fun setOnItemClickListener(clickListener: ClickListener<UserList>) {
        this.clickListener = clickListener
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var fname: TextView = itemView.findViewById(R.id.tv_first_name)
        var lname: TextView = itemView.findViewById(R.id.tv_last_name)
        var id: TextView = itemView.findViewById(R.id.tv_user_id)
        val itemLayout: CardView = itemView.findViewById(R.id.layout_items)

    }
}