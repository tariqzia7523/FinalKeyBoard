package com.german.keyboard.app.free.inputmethod.changes


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.lang.Exception
import java.util.ArrayList

class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) , OnThemeChnagedListener {
    private val fragments = ArrayList<Fragment>()
    private val fragmentTitle = ArrayList<String>()
    fun add(fragment: Fragment, title: String) {
        (fragment as ImagesFragment).setListener(this@ViewPagerAdapter)
        fragments.add(fragment)
        fragmentTitle.add(title)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return fragmentTitle[position]
    }

    override fun checkChanged() {
        try{
            (fragments[0] as ImagesFragment).adapter.notifyDataSetChanged()
        }catch (e : Exception){
            e.printStackTrace()
        }
        try{
            (fragments[1] as ImagesFragment).adapter.notifyDataSetChanged()
        }catch (e : Exception){
            e.printStackTrace()
        }
        try{
            (fragments[2] as ImagesFragment).adapter.notifyDataSetChanged()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }
}