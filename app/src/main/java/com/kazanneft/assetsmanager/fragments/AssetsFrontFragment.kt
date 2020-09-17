package com.kazanneft.assetsmanager.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanneft.assetsmanager.activities.*
import com.kazanneft.assetsmanager.adapters.AssetsAdapter
import com.kazanneft.assetsmanager.databinding.FragmentAssetsFrontBinding
import com.kazanneft.assetsmanager.decorators.PaddingDecorator
import com.kazanneft.assetsmanager.models.Asset
import com.kazanneft.assetsmanager.viewmodels.AssetsViewModel

const val REQUEST_ADD_ASSET = 1
const val REQUEST_EDIT_ASSET = 2
const val REQUEST_TRANSFER_ASSET = 3

class AssetsFrontFragment: Fragment() {

    private lateinit var viewModel: AssetsViewModel
    private lateinit var assetAdapter: AssetsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel = ViewModelProvider(requireActivity())
            .get(AssetsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentAssetsFrontBinding
            .inflate(inflater, container, false)

        binding.rvAssets.setHasFixedSize(true)
        binding.rvAssets.layoutManager = LinearLayoutManager(context)
        binding.rvAssets.addItemDecoration(
            PaddingDecorator(0,10,10,0)
        )

        assetAdapter = AssetsAdapter(this::onEditClick,
            this::onTransferClick,
            this::onTransferLogsClick)

        binding.rvAssets.adapter = assetAdapter

        viewModel.filteredAssets.observe(viewLifecycleOwner, { assets ->

            binding.tvAssetsHeader.text = "Assets (${assets.size} " +
                    "of ${viewModel.assets.value!!.size})"

            assetAdapter.editor
                .replaceAll(assets)
                .commit()
        })

        //Observe the possible errors and show them
        viewModel.responseErrorText.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context, it, Toast.LENGTH_SHORT)
                .show()
        })


        binding.btnAddAsset.setOnClickListener {
            val intent = Intent(context, AssetDetailsActivtiy::class.java)
            startActivityForResult(intent, REQUEST_ADD_ASSET)
        }

        return binding.root
    }

    private fun onEditClick(asset: Asset) {
        val intent = Intent(context, AssetDetailsActivtiy::class.java)
        intent.putExtra(EXTRA_EDIT_ASSET, asset)
        startActivityForResult(intent, REQUEST_EDIT_ASSET)
    }

    private fun onTransferClick(asset: Asset) {
        val intent = Intent(context, AssetTransferActivity::class.java)
        intent.putExtra(EXTRA_TRANSFERED_ASSET, asset)
        startActivityForResult(intent, REQUEST_TRANSFER_ASSET)
    }

    private fun onTransferLogsClick(asset: Asset) {
        val intent = Intent(context, TransferHistoryActivtiy::class.java)
        intent.putExtra(EXTRA_ASSET_HISTORY, asset)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode != RESULT_OK)
            return super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            REQUEST_ADD_ASSET -> {
                viewModel.refresh()
            }
            REQUEST_EDIT_ASSET -> {
                viewModel.refresh()
            }
            REQUEST_TRANSFER_ASSET -> {
                viewModel.refresh()
            }
        }

    }

}