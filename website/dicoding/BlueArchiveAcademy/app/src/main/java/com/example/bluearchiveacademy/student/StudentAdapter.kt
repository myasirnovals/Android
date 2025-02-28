package com.example.bluearchiveacademy.student

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bluearchiveacademy.R

class StudentAdapter(private val listStudent: ArrayList<Student>) :
    RecyclerView.Adapter<StudentAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.blue_archive, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listStudent.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, academy, photo, overview, detail) = listStudent[position]

        Glide.with(holder.itemView.context)
            .load(photo)
            .apply(RequestOptions())
            .into(holder.imgPhoto)

        holder.tvName.text = name
        holder.tvAcademy.text = academy

        val mContext = holder.itemView.context

        holder.itemView.setOnClickListener {
            val moveDetail = Intent(mContext, StudentDetail::class.java)
            moveDetail.putExtra(StudentDetail.EXTRA_NAME, academy)
            moveDetail.putExtra(StudentDetail.EXTRA_NAME, name)
            moveDetail.putExtra(StudentDetail.EXTRA_PHOTO, photo)
            moveDetail.putExtra(StudentDetail.EXTRA_DETAIL, detail)
            moveDetail.putExtra(StudentDetail.EXTRA_OVERVIEW, overview)
            mContext.startActivity(moveDetail)
        }
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        var tvAcademy: TextView = itemView.findViewById(R.id.tv_item_academy)
        var imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
    }

}