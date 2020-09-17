package com.kazanneft.assetsmanager.adapters

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.kazanneft.assetsmanager.API_ADDRESS
import com.kazanneft.assetsmanager.R
import com.kazanneft.assetsmanager.callbacks.SortedListCallback
import com.kazanneft.assetsmanager.comparators.AssetAlphComparator
import com.kazanneft.assetsmanager.databinding.AssetItemBinding
import com.kazanneft.assetsmanager.models.Asset
import com.kazanneft.assetsmanager.utils.SortedListEditor
import com.kazanneft.assetsmanager.utils.loadMainPhoto
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.*
import kotlin.collections.AbstractCollection
import kotlin.coroutines.CoroutineContext

/**
 * Recycler view adapter for asset_item.xml
 */
class AssetsAdapter(private val onEditClick: (asset: Asset) -> Unit,
                    private val onTransferClick: (asset: Asset) -> Unit,
                    private val onTransferLogsClick: (asset: Asset) -> Unit)
    : RecyclerView.Adapter<AssetsAdapter.ViewHolder>() {

    val sortedList = SortedList<Asset>(
        Asset::class.java,
        SortedListCallback<Asset>(this, AssetAlphComparator()))

    val editor = SortedListEditor<Asset>(sortedList)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AssetItemBinding.inflate(inflater, parent, false)
        return ViewHolder(parent.context, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.performBind(sortedList[position])
    }

    override fun getItemCount(): Int {
        return sortedList.size()
    }

    inner class ViewHolder(val context: Context,
                           val binding: AssetItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun performBind(asset: Asset){

            //Click on edit button
            binding.btnEditAssetItem.setOnClickListener {
                onEditClick(asset)
            }

            //Click on transfer click
            binding.btnTransfer.setOnClickListener {
                onTransferClick(asset)
            }

            //Click on transfer history
            binding.btnTransferHistory.setOnClickListener {
                onTransferLogsClick(asset)
            }

            val orientation = context.resources.configuration.orientation

            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                binding.textRootAssetItem.orientation = LinearLayout.HORIZONTAL
                binding.tvAssetDepartmentName.visibility = View.GONE
                binding.btnTransfer.visibility = View.GONE
                binding.btnTransferHistory.visibility = View.GONE

                val tv = TypedValue()

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )

                layoutParams.marginStart = TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    6f,
                                    context.resources.displayMetrics).toInt()

                binding.tvAssetSn.layoutParams = layoutParams
            }

            binding.asset = asset

            /*CoroutineScope(Dispatchers.IO).launch {

                val url = "$API_ADDRESS/asset/photo/${asset.id}"
                val bytes = loadMainPhoto(url)

                var bitmap: Bitmap? = null

                if(bytes.isEmpty()) {
                    bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.no_image)
                } else{
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                }

                withContext(Dispatchers.Main) {

                    binding.imgAssetItem.setImageBitmap(bitmap)
                }
            }*/
        }
    }
}