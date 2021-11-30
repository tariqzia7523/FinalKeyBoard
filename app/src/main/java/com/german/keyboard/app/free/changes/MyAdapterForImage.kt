package com.german.keyboard.app.free.inputmethod.changes

import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.german.keyboard.app.free.R
import com.german.keyboard.app.free.latin.settings.Settings


class MyAdapterForImage(
    var context: Context,
    var data: ArrayList<Int>,
    var themeChnagedListener: OnThemeChnagedListener?
) : RecyclerView.Adapter<MyAdapterForImage.MyViewHolder>() {
    var TAG = "***Adapter"
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image: ImageView
        var checked: ImageView
        init {
            image = view.findViewById(R.id.image)
            checked = view.findViewById(R.id.is_checked)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.image.setImageResource(data[position])
        if(Settings.getBgImageResource(prefs) == data[position]){
            holder.checked.visibility = View.VISIBLE
        }else{
            holder.checked.visibility = View.GONE
        }
        holder.itemView.setOnClickListener {


            (context as ThemeActivity).furtherCall(prefs,data[position])
            themeChnagedListener!!.checkChanged()

        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}