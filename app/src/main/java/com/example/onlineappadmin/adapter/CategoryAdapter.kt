package com.example.onlineappadmin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.onlineappadmin.R
import com.example.onlineappadmin.model.CategoryModel

class CategoryAdapter(var context:Context,val list:ArrayList<CategoryModel>): RecyclerView.Adapter<CategoryAdapter.CateViewHodel>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CateViewHodel {
        val view=LayoutInflater.from(context).inflate(R.layout.item_category_layout,parent,false)
        return CateViewHodel(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CateViewHodel, position: Int) {
        holder.title.text=list[position].cate
        Glide.with(context).load(list[position].img).into(holder.image)
    }

    class CateViewHodel(itemView: View) :RecyclerView.ViewHolder(itemView){
        val title=itemView.findViewById<TextView>(R.id.titleRecycal)
        val image=itemView.findViewById<ImageView>(R.id.imageRecycal)

    }
}