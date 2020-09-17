package com.kazanneft.assetsmanager.activities

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.NumberFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kazanneft.assetsmanager.adapters.ImagesAdapter
import com.kazanneft.assetsmanager.callbacks.DateTextWatcher
import com.kazanneft.assetsmanager.databinding.ActivityAssetDetailsBinding
import com.kazanneft.assetsmanager.decorators.PaddingDecorator
import com.kazanneft.assetsmanager.models.*
import com.kazanneft.assetsmanager.utils.hasReadStoragePermission
import com.kazanneft.assetsmanager.utils.loadPhotos
import com.kazanneft.assetsmanager.utils.readFileBytes
import com.kazanneft.assetsmanager.utils.tryParseDate
import com.kazanneft.assetsmanager.viewmodels.AssetDetailsViewModel
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.LocalTime

private const val BROWSE_IMAGE_RESULT = 1
private const val READ_STORAGE_PERMISSION_REQUEST = 2
private const val CAPTURE_IMAGE_REQUEST = 3

const val EXTRA_EDIT_ASSET = "ExtraEditAsset"

class AssetDetailsActivtiy : AppCompatActivity() {

    private lateinit var viewModel: AssetDetailsViewModel
    private val imagesAdapter = ImagesAdapter()
    lateinit var asset: Asset
    private lateinit var binding: ActivityAssetDetailsBinding
    private var isEdit: Boolean = false

    public val locations = MutableLiveData<List<Location>>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)
            .get(AssetDetailsViewModel::class.java)

        binding = ActivityAssetDetailsBinding.inflate(layoutInflater)

        binding.setLifecycleOwner(this)

        binding.model = viewModel
        binding.uiController = this

        setContentView(binding.root)

        setSupportActionBar(binding.toolbarActivityDetails)

        binding.toolbarActivityDetails.setNavigationOnClickListener {
            finish()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        val tempAsset = intent.extras?.getParcelable<Asset>(EXTRA_EDIT_ASSET)

        if(tempAsset == null) {
            asset = Asset()
            isEdit = false
        }
        else {
            asset = tempAsset
            isEdit = true
        }

        binding.asset = asset


        binding.edWarranty.addTextChangedListener(DateTextWatcher(binding.layoutWarranty))

        binding.edWarranty.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, hasFocus: Boolean) {
                if(!hasFocus){
                    val date = tryParseDate(binding.edWarranty.text!!)
                    if(date == null) {
                        binding.layoutWarranty.error =
                            "Error date format\n" +
                                    "It must be Y/M/D"
                    }
                    else {
                        asset.warrantyDate = LocalDateTime.of(date, LocalTime.now())
                    }
                }
            }
        }

        binding.btnSubmit.setOnClickListener {

            asset.departmentLocation = viewModel.findDepartmentLocation(
                binding.spinnerAssetEditDepartments.selectedItem as Department,
                binding.spinnerAssetDetailsLocations.selectedItem as Location)!!

            if(!viewModel.isValidAssetName(asset.assetName,
                    asset.departmentLocation.location!!)) {
                binding.layoutAssetName.error = "The asset with the same name already exists in location " +
                        asset.departmentLocation.location!!.name
                return@setOnClickListener
            }

            asset.assetSN = binding.tvAssetSn.text.toString()

            if(binding.edWarranty.text != null &&
                binding.edWarranty.text!!.isNotEmpty()) {
                val date = tryParseDate(binding.edWarranty.text!!)
                if (date == null) {
                    binding.layoutWarranty.error =
                        "Error date format\n" +
                                "It must be Y/M/D"
                    return@setOnClickListener
                } else {
                    asset.warrantyDate = LocalDateTime.of(date, LocalTime.now())
                }
            }

            if(isEdit) {
                viewModel.updateAsset(asset, imagesAdapter.getImages())
            }
            else
                viewModel.addAsset(asset,  imagesAdapter.getImages())

            val intent = Intent()
            intent.putExtra("asset", asset)

            setResult(RESULT_OK, intent)
            finish()
        }



        binding.rvImages.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@AssetDetailsActivtiy)
            addItemDecoration(PaddingDecorator(0,5,0,5))
            adapter = imagesAdapter
        }

        binding.btnBrowse.setOnClickListener {

            if (hasReadStoragePermission(this)) {
                requestImage()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_REQUEST)
            }
        }


        if(isEdit) {

            binding.spinnerAssetDetailsLocations.isClickable = false
            binding.spinnerAssetEditDepartments.isClickable = false
            binding.spinnerAssetEditAssetGroups.isClickable = false

            lifecycleScope.launch {

                val photos = loadPhotos(asset.id)
                var counter = 1
                val imageItems = photos.map { ImageItem(counter++.toString(),
                                                        it, false) }

                withContext(Dispatchers.Main) {
                    imagesAdapter.editor
                        .addAll(imageItems)
                        .commit()
                }
            }

        }
    }

    fun departmentChanged() {
        updateLocations()
        updateAssetSN()
    }

    fun updateLocations() {
        locations.value = viewModel.findLocations(
            asset.departmentLocation.department!!)
    }

    fun updateAssetSN(){

        if(isEdit) {
            binding.tvAssetSn.text = asset.assetSN
            return
        }

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

        binding.tvAssetSn.text = "$dd/$gg/$nnnn"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode != RESULT_OK)
            return super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {

            BROWSE_IMAGE_RESULT -> {

                val selectedImage = data?.data

                val columns = arrayOf(MediaStore.Images.Media.DATA,
                                            MediaStore.Images.Media.DISPLAY_NAME)

                val cursor = contentResolver.query(selectedImage!!, columns,
                    null, null, null)

                if(cursor == null)
                    return

                cursor.moveToFirst()

                val pathIndex = cursor.getColumnIndex(columns[0])
                val nameIndex = cursor.getColumnIndex(columns[1])

                val picturePath = cursor.getString(pathIndex)
                val pictureName = cursor.getString(nameIndex)

                cursor.close()

                val bytes = readFileBytes(picturePath)

                imagesAdapter.editor
                    .add(ImageItem(pictureName, bytes))
                    .commit()
            }

            READ_STORAGE_PERMISSION_REQUEST -> {
                requestImage()
            }

            CAPTURE_IMAGE_REQUEST -> {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                val streamBytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamBytes)
                val imageItem = ImageItem(
                    "Camera photo", streamBytes.toByteArray()
                )
                streamBytes.close()
                imagesAdapter.editor
                    .add(imageItem)
                    .commit()
            }
        }
    }

    private fun requestImage(){
        val intent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, BROWSE_IMAGE_RESULT)
    }
}




/*binding.spinnerAssetDetailsLocations.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

     override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

         val location = binding.spinnerAssetDetailsLocations.selectedItem
                 as Location

         asset.departmentLocation.location = location
     }

     override fun onNothingSelected(p0: AdapterView<*>?) {

     }
 }


 binding.spinnerAssetEditAssetGroups.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

     override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

         asset.assetGroup = binding.spinnerAssetEditAssetGroups.selectedItem
                 as AssetGroup

         updateAssetSN()
     }

     override fun onNothingSelected(p0: AdapterView<*>?) {

     }
 }

  binding.spinnerAssetEditAssetGroups.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

     override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

         asset.assetGroup = binding.spinnerAssetEditAssetGroups.selectedItem
                 as AssetGroup

         updateAssetSN()
     }

     override fun onNothingSelected(p0: AdapterView<*>?) {

     }
 }

 binding.spinnerAssetEditDepartments.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

     override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

         val department = viewModel.departments.value!![p2]

         val locations: List<Location>

         if(isEdit) {
             locations = listOf(asset.departmentLocation.location!!)
         }
         else {
             locations = viewModel.findLocations(department)
         }

         val adapter = ArrayAdapter<Location>(this@AssetDetailsActivtiy,
             android.R.layout.simple_spinner_item,
             locations)

         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
         binding.spinnerAssetDetailsLocations.adapter = adapter
         asset.departmentLocation.department = department

         updateAssetSN()
     }

     override fun onNothingSelected(p0: AdapterView<*>?) {

     }
 }


 binding.btnCaptureImage.setOnClickListener {
     val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
         intent ->
         intent.resolveActivity(packageManager)?.also {
             startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
         }
     }
 }*/

/* viewModel.assetGroups.observe(this, { collection ->

val groups: List<AssetGroup>

if(isEdit) {
 groups = listOf(asset.assetGroup!!)
}
else {
 groups = collection
}

val adapter = ArrayAdapter(
 this, android.R.layout.simple_spinner_item, groups)
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
binding.spinnerAssetEditAssetGroups.adapter = adapter
})

viewModel.departments.observe(this, { collection ->

val departments: List<Department>

if(isEdit) {
 departments =
     listOf(asset.departmentLocation.department!!)
}
else {
 departments = collection
}

val adapter = ArrayAdapter(
 this, android.R.layout.simple_spinner_item, departments)

adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
binding.spinnerAssetEditDepartments.adapter = adapter
})


*/
