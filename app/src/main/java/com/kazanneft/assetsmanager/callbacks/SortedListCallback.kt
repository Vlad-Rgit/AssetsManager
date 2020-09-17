package com.kazanneft.assetsmanager.callbacks

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.kazanneft.assetsmanager.models.AreItemsSameComparator

class SortedListCallback<T: AreItemsSameComparator<T>>(
    private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    private val comparator: Comparator<T>)
    : SortedList.Callback<T>() {


    override fun compare(o1: T, o2: T): Int {
        return comparator.compare(o1, o2)
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onChanged(position: Int, count: Int) {
        adapter.notifyItemRangeChanged(position, count)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(item1: T, item2: T): Boolean {
        return item1.areItemsSame(item2)
    }
}