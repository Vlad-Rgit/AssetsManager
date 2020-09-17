package com.kazanneft.assetsmanager.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.kazanneft.assetsmanager.callbacks.DateTextWatcher
import com.kazanneft.assetsmanager.databinding.FragmentAssetsBackBinding
import com.kazanneft.assetsmanager.models.AssetGroup
import com.kazanneft.assetsmanager.models.Department
import com.kazanneft.assetsmanager.utils.tryParseDate
import com.kazanneft.assetsmanager.viewmodels.AssetsViewModel

class AssetsBackFragment: Fragment() {

    private lateinit var viewModel: AssetsViewModel

    override fun onAttach(context: Context) {

        super.onAttach(context)

        viewModel = ViewModelProvider(requireActivity())
            .get(AssetsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentAssetsBackBinding
            .inflate(inflater, container, false)

        binding.filter = viewModel.filter

        viewModel.depratmetns.observe(viewLifecycleOwner, { departments ->

            val list = departments.toMutableList()
            list.add(0, Department(name = "Show all"))

            val adapter = ArrayAdapter(context!!,
                android.R.layout.simple_spinner_item, list)

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerDepartments.adapter = adapter
        })

        viewModel.assetsGroup.observe(viewLifecycleOwner, { assetGroups ->


            val list = assetGroups.toMutableList()
            list.add(0, AssetGroup(name = "Show all"))

            val adapter = ArrayAdapter(context!!,
                android.R.layout.simple_spinner_item, list)

            adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item)

            binding.spinnerAssetsGroup.adapter = adapter
        })

        binding.spinnerDepartments.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(p2 == 0)
                    viewModel.filter.department = null
                else
                    viewModel.filter.department = viewModel.depratmetns
                        .value?.get(p2-1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.spinnerAssetsGroup.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                if(position == 0)
                    viewModel.filter.assetGroup = null
                else
                    viewModel.filter.assetGroup = viewModel.assetsGroup
                        .value?.get(position-1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.edStartDate
            .addTextChangedListener(DateTextWatcher(binding.layoutStartDate))
        binding.edEndDate
            .addTextChangedListener(DateTextWatcher(binding.layoutEndDate))

        binding.edStartDate.setOnFocusChangeListener { view, b ->

            if(b)
                return@setOnFocusChangeListener

            binding.edStartDate.text?.let {
                val date = tryParseDate(it)
                if(date == null)
                    binding.layoutStartDate.error = "Error date format\nIt must be Y/M/D"
                else
                    viewModel.filter.startDate = date
            }

        }

        binding.edEndDate.setOnFocusChangeListener { view, b ->
            if(b)
                return@setOnFocusChangeListener

            binding.edEndDate.text?.let {
                val date = tryParseDate(it)
                if(date == null)
                    binding.layoutEndDate.error = "Error date format\nIt must be Y/M/D"
                else
                    viewModel.filter.endDate = date
            }
        }

        return binding.root
    }

}