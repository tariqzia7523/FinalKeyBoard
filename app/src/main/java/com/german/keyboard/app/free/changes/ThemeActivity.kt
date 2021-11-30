package com.german.keyboard.app.free.inputmethod.changes

import com.german.keyboard.app.free.R
import com.german.keyboard.app.free.latin.settings.Settings
import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.github.naz013.colorslider.ColorSlider
import com.google.android.material.tabs.TabLayout
import com.module.ads.AddInitilizer
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import android.view.WindowManager
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.activity_theme.*


class ThemeActivity : AppCompatActivity() {

    lateinit var gradientImagesList : ArrayList<Int>
    lateinit var ImagesList : ArrayList<Int>
    lateinit var viewPager : ViewPager
    lateinit var tab_layout : TabLayout
    lateinit var viewPagerAdapter : ViewPagerAdapter
    lateinit var priviewView : View
    lateinit var color_plate_picker_layout : RelativeLayout
    var addInitilizer: AddInitilizer? = null
    var globelPref : SharedPreferences? = null
    var globelId : Int? = null
    lateinit var  colorSlider : ColorSlider
    var image_uri : Uri? = null
    var color_picker_view : LinearLayout? = null
    //var no_theme_selected : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_theme)

        try {
            supportActionBar!!.hide()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        backBtn.setOnClickListener({ finish() })
        viewPager = findViewById(R.id.pager)
        tab_layout = findViewById(R.id.tab_layout)
        colorSlider = findViewById(R.id.color_slider)
        priviewView = findViewById(R.id.priview_view_color)
        color_plate_picker_layout = findViewById(R.id.color_plate_picker_layout)
        color_picker_view = findViewById(R.id.color_picker_view)
        //no_theme_selected = findViewById(R.id.no_theme_selected)
        dataInitialer()
        addInitilizer = AddInitilizer(applicationContext, this) {
            if(globelId != null && globelPref != null) {
                Settings.setBgImageResource(globelPref,globelId!!)
                Toast.makeText(this@ThemeActivity,getString(R.string.theme_applied),Toast.LENGTH_LONG).show()
                themeSelectedChangedFromAdapter()

            }

        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(this@ThemeActivity)
        val themeType = Settings.getBgImageType(prefs)
        if(themeType == 3){
            colorSlider.setSelectorColor(Settings.getBgColor(prefs))
            color_plate_picker_layout.visibility = View.VISIBLE
            priviewView.setBackgroundColor(Settings.getBgColor(prefs))
            priviewView.isVisible = true
            color_picker_view!!.isVisible =true
        }
        colorSlider.setListener(object : ColorSlider.OnColorSelectedListener {
            override fun onColorChanged(position: Int, color: Int) {

                color_plate_picker_layout.visibility = View.VISIBLE
                priviewView.setBackgroundColor(color)
                priviewView.isVisible = true
                color_picker_view!!.isVisible =true
                //no_theme_selected!!.isVisible = false
                val prefs = PreferenceManager.getDefaultSharedPreferences(this@ThemeActivity)

                Settings.setBgImageResource(prefs,0)
                Settings.setBgColor(prefs,color)
                viewPagerAdapter.checkChanged()
            }
        })



        findViewById<View>(R.id.gallry_image).setOnClickListener {

            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            someActivityResultLauncher.launch(intent)
        }
        findViewById<View>(R.id.camera_image).setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraResultLauncher.launch(cameraIntent)
        }
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        val bundle = Bundle()
        bundle.putIntegerArrayList(getString(R.string.selected_list), gradientImagesList)
        val globelListFragment = ImagesFragment()
        globelListFragment.setArguments(bundle)
        val bundle2 = Bundle()
        bundle2.putIntegerArrayList(getString(R.string.selected_list), ImagesList)
        val myFeedListFragment = ImagesFragment()
        myFeedListFragment.setArguments(bundle2)

        viewPagerAdapter.add(globelListFragment,getString(R.string.gradient))
        viewPagerAdapter.add(myFeedListFragment,getString(R.string.images))

        viewPager.adapter = viewPagerAdapter
        tab_layout.setupWithViewPager(viewPager)

    }

    fun themeSelectedChangedFromAdapter(){
        color_plate_picker_layout.visibility = View.INVISIBLE

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    var someActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data!!.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            imageCroper(bitmap)
        }
    }

    var cameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = result.data!!.extras!!.get("data") as Bitmap

            imageCroper(bitmap)
        }
    }
    fun furtherCall(prefs: SharedPreferences, id: Int){
        globelId = id
        globelPref = prefs;
        Settings.setBgImageResource(prefs,id)
        if(!addInitilizer!!.showInterstailAdd()) {
            Toast.makeText(this@ThemeActivity,getString(R.string.theme_applied),Toast.LENGTH_LONG).show()
            themeSelectedChangedFromAdapter()
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
        ImagesList.add(R.drawable.gradient99)
        ImagesList.add(R.drawable.gradient98)
        ImagesList.add(R.drawable.gradient97)
        ImagesList.add(R.drawable.gradient96)
        ImagesList.add(R.drawable.gradient95)
        ImagesList.add(R.drawable.gradient94)
        ImagesList.add(R.drawable.gradient93)
        ImagesList.add(R.drawable.gradient92)
        ImagesList.add(R.drawable.gradient91)
        ImagesList.add(R.drawable.gradient90)
        ImagesList.add(R.drawable.gradient89)
        ImagesList.add(R.drawable.gradient88)



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


        gradientImagesList = ArrayList()
        gradientImagesList.add(R.drawable.gradient1)
        gradientImagesList.add(R.drawable.gradient2)
        gradientImagesList.add(R.drawable.gradient3)
        gradientImagesList.add(R.drawable.gradient4)
        gradientImagesList.add(R.drawable.gradient5)
        gradientImagesList.add(R.drawable.gradient6)
        gradientImagesList.add(R.drawable.gradient7)
        gradientImagesList.add(R.drawable.gradient8)
        gradientImagesList.add(R.drawable.gradient9)
        gradientImagesList.add(R.drawable.gradient10)
        gradientImagesList.add(R.drawable.gradient11)
        gradientImagesList.add(R.drawable.gradient12)
        gradientImagesList.add(R.drawable.gradient13)
        gradientImagesList.add(R.drawable.gradient14)
        gradientImagesList.add(R.drawable.gradient15)
        gradientImagesList.add(R.drawable.gradient16)
        gradientImagesList.add(R.drawable.gradient17)
        gradientImagesList.add(R.drawable.gradient18)
        gradientImagesList.add(R.drawable.gradient19)
        gradientImagesList.add(R.drawable.gradient20)
        gradientImagesList.add(R.drawable.gradient21)
        gradientImagesList.add(R.drawable.gradient22)
        gradientImagesList.add(R.drawable.gradient23)
        gradientImagesList.add(R.drawable.gradient24)
        gradientImagesList.add(R.drawable.gradient25)
        gradientImagesList.add(R.drawable.gradient26)
        gradientImagesList.add(R.drawable.gradient27)
        gradientImagesList.add(R.drawable.gradient28)
        gradientImagesList.add(R.drawable.gradient29)
        gradientImagesList.add(R.drawable.gradient30)
        gradientImagesList.add(R.drawable.gradient31)
        gradientImagesList.add(R.drawable.gradient32)
        gradientImagesList.add(R.drawable.gradient33)
        gradientImagesList.add(R.drawable.gradient34)
        gradientImagesList.add(R.drawable.gradient35)
        gradientImagesList.add(R.drawable.gradient36)
        gradientImagesList.add(R.drawable.gradient37)
        gradientImagesList.add(R.drawable.gradient38)
        gradientImagesList.add(R.drawable.gradient39)
        gradientImagesList.add(R.drawable.gradient40)
    }
}