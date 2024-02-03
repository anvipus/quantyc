package com.anvipus.explore.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.anvipus.explore.R
import com.anvipus.explore.databinding.ItemAlbumBinding
import com.anvipus.explore.databinding.ItemUserBinding
import com.anvipus.library.model.Album

class AlbumAdapter(): ListAdapter<Album, RecyclerView.ViewHolder>(COMPARATOR){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return ViewHolder(ItemAlbumBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        (holder as ViewHolder).run {
            bind(data)
        }
    }
    class ViewHolder(private val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(data: Album?){
            binding.data = data
        }
    }

    companion object{
        const val TAG:String = "AlbumAdapter"
        val COMPARATOR = object : DiffUtil.ItemCallback<Album>(){
            override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
                return oldItem == newItem
            }

        }
    }
}