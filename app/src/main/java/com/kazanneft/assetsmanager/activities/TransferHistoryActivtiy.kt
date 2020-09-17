package com.kazanneft.assetsmanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kazanneft.assetsmanager.R
import com.kazanneft.assetsmanager.adapters.AssetTransfersAdapter
import com.kazanneft.assetsmanager.decorators.PaddingDecorator
import com.kazanneft.assetsmanager.models.Asset
import com.kazanneft.assetsmanager.viewmodels.AssetTransferViewModel
import com.kazanneft.assetsmanager.viewmodels.TransferLogsViewModel
import com.kazanneft.assetsmanager.viewmodels.factories.TranfersLogsViewModelFactory

const val EXTRA_ASSET_HISTORY = "Extra1"

class TransferHistoryActivtiy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val asset = intent.getParcelableExtra<Asset>(EXTRA_ASSET_HISTORY)!!

        val viewModel = ViewModelProvider(this,
            TranfersLogsViewModelFactory(asset.id))
                .get(TransferLogsViewModel::class.java)

        setContentView(R.layout.activity_transfer_history_activtiy)

        val toolbar: Toolbar = findViewById(R.id.toolbar_activity_transfer_history)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val adapter = AssetTransfersAdapter()

        val rvAssetTransfers: RecyclerView = findViewById(R.id.rv_transfer_history)
        rvAssetTransfers.setHasFixedSize(true)
        rvAssetTransfers.addItemDecoration(PaddingDecorator(5,10,5,10))
        rvAssetTransfers.layoutManager = LinearLayoutManager(this)
        rvAssetTransfers.adapter = adapter

        viewModel.transfers.observe(this, { transfers ->

            if(transfers.isEmpty()) {
                AlertDialog.Builder(this)
                    .setMessage("There are no transfers with this asset in last 12 months..")
                    .setNeutralButton("Back") { _, _ -> finish() }
                    .show()
            }

            adapter.editor
                .replaceAll(transfers)
                .commit()
        })
    }
}