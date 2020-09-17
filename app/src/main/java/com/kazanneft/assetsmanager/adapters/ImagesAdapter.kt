package com.kazanneft.assetsmanager.adapters

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.kazanneft.assetsmanager.callbacks.SortedListCallback
import com.kazanneft.assetsmanager.comparators.ImageAlphComparator
import com.kazanneft.assetsmanager.databinding.ImageItemBinding
import com.kazanneft.assetsmanager.models.ImageItem
import com.kazanneft.assetsmanager.utils.SortedListEditor
import com.kazanneft.assetsmanager.utils.readStringResponse

class ImagesAdapter: RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {


    private val sortedList = SortedList(ImageItem::class.java,
        SortedListCallback(this, ImageAlphComparator()))

    val editor = SortedListEditor(sortedList)

    fun getImages(): List<ByteArray> {

        if(sortedList.size() == 0)
            return emptyList()

        val result = ArrayList<ByteArray>(sortedList.size())

        for(i in 0 until sortedList.size()) {
            if(sortedList[i].isNew)
                result.add(sortedList[i].bytes)
        }

        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("Adapter", "Create view holder")
        val inflater = LayoutInflater.from(parent.context)
        val binding = ImageItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Adapter", "bind view holder")
        val image = sortedList[position]
        holder.performBinding(image)
    }

    override fun getItemCount(): Int {
        Log.d("Adapter", "Sorted list size ${sortedList.size()}")
        return sortedList.size()
    }

    class ViewHolder(private val binding: ImageItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun performBinding(imageItem: ImageItem){
            binding.imgName.text = imageItem.name
            binding.imgItem.setImageBitmap(
                BitmapFactory.decodeByteArray(imageItem.bytes, 0, imageItem.bytes.size)
            )
        }
    }

}