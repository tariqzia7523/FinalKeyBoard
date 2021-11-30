package com.german.keyboard.app.free.inputmethod.changes


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.german.keyboard.app.free.R

class ImagesFragment : Fragment() {
    var themeChnagedListener: OnThemeChnagedListener? = null
    lateinit var recylerView : RecyclerView
    lateinit var adapter : MyAdapterForImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.recyler_view_layout, container, false)
        return view
    }
    fun setListener(inter : OnThemeChnagedListener){
        themeChnagedListener = inter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = requireArguments().getIntegerArrayList(getString(R.string.selected_list))
        adapter = MyAdapterForImage(requireActivity(),list!!,themeChnagedListener)
        recylerView = view.findViewById(R.id.theme_list)
        recylerView.layoutManager = GridLayoutManager(requireActivity(),2)
        recylerView.adapter = adapter
    }

}