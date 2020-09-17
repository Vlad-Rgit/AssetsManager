package com.kazanneft.assetsmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.kazanneft.assetsmanager.callbacks.SortedListCallback
import com.kazanneft.assetsmanager.comparators.AssetTransferComparator
import com.kazanneft.assetsmanager.databinding.TransferHistoryItemBinding
import com.kazanneft.assetsmanager.models.AssetTransfer
import com.kazanneft.assetsmanager.utils.SortedListEditor

class AssetTransfersAdapter
    : RecyclerView.Adapter<AssetTransfersAdapter.ViewHolder>() {


    private val sortedList = SortedList(AssetTransfer::class.java,
        SortedListCallback(this, AssetTransferComparator()))

    val editor = SortedListEditor(sortedList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TransferHistoryItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.performBind(sortedList[position])
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }

    class ViewHolder(val binding: TransferHistoryItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun performBind(assetTransfer: AssetTransfer) {
            binding.log = assetTransfer
        }
    }

}