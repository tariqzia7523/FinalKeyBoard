package io.github.otobikb.inputmethod.changes

import io.github.otobikb.inputmethod.latin.R
import io.github.otobikb.inputmethod.latin.settings.Settings
import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.graphics.Bitmap
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ThemeActivity : AppCompatActivity() {

    lateinit var colorImagesList : ArrayList<Int>
    lateinit var ImagesList : ArrayList<Int>
    lateinit var viewPager : ViewPager
    lateinit var tab_layout : TabLayout
    lateinit var viewPagerAdapter : ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        viewPager = findViewById(R.id.pager)
        tab_layout = findViewById(R.id.tab_layout)
        dataInitialer()

        findViewById<View>(R.id.gallry_image).setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            someActivityResultLauncher.launch(intent)
        }
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val bundle = Bundle()
        bundle.putIntegerArrayList(getString(R.string.selected_list), colorImagesList)
        val globelListFragment = ImagesFragment()
        globelListFragment.setArguments(bundle)
        val bundle2 = Bundle()
        bundle2.putIntegerArrayList(getString(R.string.selected_list), ImagesList)
        val myFeedListFragment = ImagesFragment()
        myFeedListFragment.setArguments(bundle2)

        viewPagerAdapter.add(globelListFragment,getString(R.string.colors))
        viewPagerAdapter.add(myFeedListFragment,getString(R.string.images))

        viewPager.adapter = viewPagerAdapter
        tab_layout.setupWithViewPager(viewPager)

    }


//    var someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val imageUri = result.data!!.data
//            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//            imageCroper(bitmap)
//        }
//    }
var someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val imageUri = result.data!!.data
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        imageCroper(bitmap)
    }
}


    fun imageCroper(bitmap: Bitmap){
        val dialog = Dialog(this@ThemeActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.image_croper_layout)
        val cropImageView: CropImageView = dialog.findViewById(R.id.image_view)
        cropImageView.setImageBitmap(bitmap);
        cropImageView.setAspectRatio(60,40)

        val cancel_button: TextView = dialog.findViewById(R.id.cancel_button)
        val save: TextView = dialog.findViewById(R.id.save)
        val rotate: TextView = dialog.findViewById(R.id.rotate)
        rotate.setOnClickListener {
            cropImageView.rotateImage(90)
        }
        save.setOnClickListener { v: View? ->
            dialog.dismiss()
            saveBitmap(cropImageView.getCroppedImage(),100)
        }
        cancel_button.setOnClickListener { v: View? ->
            dialog.dismiss()

        }
        dialog.show()
        val window: Window = dialog.getWindow()!!
        window.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
    fun saveBitmap(bitmap: Bitmap, quality: Int): String? {
        val directory = File(getExternalFilesDir(null), getString(R.string.english_ime_name))
        var fileName: String? = null
        var isDirectoryCreated = directory.exists()
        if (!isDirectoryCreated) {
            isDirectoryCreated = directory.mkdir()
        }
        if (isDirectoryCreated) {
            fileName = "currant_image.png"
            try {
                val outFile = File(directory, fileName)
                val outStream = FileOutputStream(outFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outStream)
                outStream.flush()
                outStream.close()
                Toast.makeText(this@ThemeActivity, getString(R.string.fil_saved), Toast.LENGTH_LONG).show()
                Log.e("***FilePath","File path "+outFile.absolutePath)
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                Settings.setBgImageFilePath(prefs, outFile.absolutePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return fileName
    }

    private fun dataInitialer() {
        ImagesList = ArrayList()
        ImagesList.add(R.drawable.theme_1)
        ImagesList.add(R.drawable.theme_2)
        ImagesList.add(R.drawable.theme_3)
        ImagesList.add(R.drawable.theme_4)
        ImagesList.add(R.drawable.theme_5)
        ImagesList.add(R.drawable.theme_6)
        ImagesList.add(R.drawable.theme_7)
        ImagesList.add(R.drawable.theme_8)
        ImagesList.add(R.drawable.theme_9)
        ImagesList.add(R.drawable.theme_11)
        ImagesList.add(R.drawable.theme_12)
        ImagesList.add(R.drawable.theme_13)
        ImagesList.add(R.drawable.theme_14)
        ImagesList.add(R.drawable.theme17)
        ImagesList.add(R.drawable.theme18)


        colorImagesList = ArrayList()
        colorImagesList.add(R.drawable.theme_37)
        colorImagesList.add(R.drawable.theme_38)
        colorImagesList.add(R.drawable.theme_39)
        colorImagesList.add(R.drawable.theme_41)
        colorImagesList.add(R.drawable.theme_42)
        colorImagesList.add(R.drawable.theme_43)
        colorImagesList.add(R.drawable.theme_44)
        colorImagesList.add(R.drawable.theme_45)
        colorImagesList.add(R.drawable.theme_46)
        colorImagesList.add(R.drawable.theme_47)
        colorImagesList.add(R.drawable.theme_48)
        colorImagesList.add(R.drawable.theme_49)
        colorImagesList.add(R.drawable.theme_50)
    }




}