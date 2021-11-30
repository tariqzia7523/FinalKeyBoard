package com.german.keyboard.app.free.inputmethod.dictionarypack

import android.view.View
import java.util.*

class DictionaryListInterfaceState {
    internal class State {
        var mOpen = false
        var mStatus: Int = MetadataDbHelper.STATUS_UNKNOWN
    }

    private val mWordlistToState = HashMap<String, State>()
    private val mViewCache = ArrayList<View>()
    fun isOpen(wordlistId: String?): Boolean {
        val state = mWordlistToState[wordlistId] ?: return false
        return state.mOpen
    }

    fun getStatus(wordlistId: String?): Int {
        val state = mWordlistToState[wordlistId] ?: return MetadataDbHelper.STATUS_UNKNOWN
        return state.mStatus
    }

    fun setOpen(wordlistId: String, status: Int) {
        val newState: State
        val state = mWordlistToState[wordlistId]
        newState = state ?: State()
        newState.mOpen = true
        newState.mStatus = status
        mWordlistToState[wordlistId] = newState
    }

    fun closeAll() {
        for (state in mWordlistToState.values) {
            state.mOpen = false
        }
    }

    fun findFirstOrphanedView(): View? {
        for (v in mViewCache) {
            if (null == v.parent) return v
        }
        return null
    }

    fun addToCacheAndReturnView(view: View): View {
        mViewCache.add(view)
        return view
    }

    fun removeFromCache(view: View?) {
        mViewCache.remove(view)
    }
}