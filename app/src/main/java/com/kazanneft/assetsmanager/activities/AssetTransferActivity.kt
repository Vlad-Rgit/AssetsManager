package com.kazanneft.assetsmanager.activities

import android.icu.text.NumberFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.kazanneft.assetsmanager.R
import com.kazanneft.assetsmanager.databinding.ActivityAssetTransferBinding
import com.kazanneft.assetsmanager.models.Asset
import com.kazanneft.assetsmanager.models.AssetTransfer
import com.kazanneft.assetsmanager.models.Department
import com.kazanneft.assetsmanager.models.Location
import com.kazanneft.assetsmanager.viewmodels.AssetTransferViewModel

const val EXTRA_TRANSFERED_ASSET = "ExtraTransferedAsset"

class AssetTransferActivity : AppCompatActivity() {

    private lateinit var viewModel: AssetTransferViewModel
    private lateinit var asset: Asset
    private lateinit var assetTransfer: AssetTransfer
    private lateinit var binding: ActivityAssetTransferBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        asset = intent.getParcelableExtra(EXTRA_TRANSFERED_ASSET)!!

        assetTransfer = AssetTransfer(0, asset.id,
                                        asset.assetSN, asset.assetSN,
                                        asset.departmentLocation.id,
                                        asset.departmentLocation.id)

        viewModel = ViewModelProvider(this)
            .get(AssetTransferViewModel::class.java)


        binding = ActivityAssetTransferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarActivityTransfer.setNavigationOnClickListener {
            finish()
        }

        binding.asset = asset

        viewModel.departments.observe(this, { departments->

            val collection = departments.filter { department ->
                department.id != asset.departmentLocation.department!!.id
            }

            val adapter = ArrayAdapter<Department>(
                this, android.R.layout.simple_spinner_item, collection
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDestinationDepartments.adapter = adapter
        })

        binding.spinnerDestinationDepartments.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val department =
                    binding.spinnerDestinationDepartments.selectedItem as Department

                asset.departmentLocation.department = department

                val locations = viewModel.getDepartmentLocations(department)

                val locationAdapter = ArrayAdapter<Location>(
                    this@AssetTransferActivity,
                    android.R.layout.simple_spinner_item,
                    locations
                )

                locationAdapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item)

                binding.spinnerTransferLocation.adapter = locationAdapter

                updateAssetSN()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.spinnerTransferLocation.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val location = p0!!.selectedItem as Location
                    asset.departmentLocation.location = location
                    updateAssetSN()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        binding.btnTransferSubmit.setOnClickListener {

            asset.departmentLocation = viewModel.findDepartmentLocation(
                asset.departmentLocation.department!!, asset.departmentLocation.location!!)!!

            assetTransfer.toAssetSN = asset.assetSN
            assetTransfer.toDepartmentLocationId = asset.departmentLocation.id

            viewModel.submitTransfer(assetTransfer)
            setResult(RESULT_OK)
            finish()
        }

        binding.btnTransferCancel.setOnClickListener {
            finish()
        }


    }

    fun updateAssetSN(){

        if(asset.departmentLocation.department == null ||
            asset.assetGroup == null)
            return

        val numberFormat = NumberFormat.getNumberInstance()

        numberFormat.minimumIntegerDigits = 2
        numberFormat.maximumIntegerDigits = 2

        val dd = numberFormat
            .format(asset.departmentLocation.department!!.id)

        val gg = numberFormat
            .format(asset.assetGroup!!.id)

        val nid = viewModel.findAssetNID(asset)

        val nnnn = String.format("%04d", nid)

        asset.assetSN = "$dd/$gg/$nnnn"
        binding.tvTransferAssetSn.text = asset.assetSN
    }

}