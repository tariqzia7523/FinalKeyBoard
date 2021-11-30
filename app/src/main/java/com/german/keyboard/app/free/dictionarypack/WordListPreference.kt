package com.german.keyboard.app.free.inputmethod.dictionarypack

import android.content.Context
import android.preference.Preference
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.german.keyboard.app.free.R
import java.util.*

class WordListPreference(context: Context?,
                         private val mInterfaceState: DictionaryListInterfaceState,
                         val mWordlistId: String, val mVersion: Int, val mLocale: Locale,
                         val mDescription: String, status: Int,
                         private val mFilesize: Int) : Preference(context, null) {

    private var mStatus = 0

    fun setStatus(status: Int) {
        if (status == mStatus) return
        mStatus = status
        summary = getSummary(status)
    }

    fun hasStatus(status: Int): Boolean {
        return status == mStatus
    }

    public override fun onCreateView(parent: ViewGroup): View {
        val orphanedView = mInterfaceState.findFirstOrphanedView()
        if (null != orphanedView) return orphanedView // Will be sent to onBindView
        val newView = super.onCreateView(parent)
        return mInterfaceState.addToCacheAndReturnView(newView)
    }

    fun hasPriorityOver(otherPrefStatus: Int): Boolean { // Both of these should be one of MetadataDbHelper.STATUS_*
        return mStatus > otherPrefStatus
    }

    private fun getSummary(status: Int): String {
        val context = context
        return when (status) {
            MetadataDbHelper.STATUS_DELETING, MetadataDbHelper.STATUS_AVAILABLE -> context.getString(R.string.dictionary_available)
            MetadataDbHelper.STATUS_DOWNLOADING -> context.getString(R.string.dictionary_downloading)
            MetadataDbHelper.STATUS_INSTALLED -> context.getString(R.string.dictionary_installed)
            MetadataDbHelper.STATUS_DISABLED -> context.getString(R.string.dictionary_disabled)
            else -> NO_STATUS_MESSAGE
        }
    }

    private fun disableDict() {
        val context = context
        val prefs = CommonPreferences.getCommonPreferences(context)
        CommonPreferences.disable(prefs, mWordlistId)
        if (MetadataDbHelper.STATUS_DOWNLOADING == mStatus) {
            setStatus(MetadataDbHelper.STATUS_AVAILABLE)
        } else if (MetadataDbHelper.STATUS_INSTALLED == mStatus) {
            setStatus(MetadataDbHelper.STATUS_DISABLED)
        } else {
            Log.e(TAG, "Unexpected state of the word list for disabling $mStatus")
        }
    }

    private fun enableDict() {
        val context = context
        val prefs = CommonPreferences.getCommonPreferences(context)
        CommonPreferences.enable(prefs, mWordlistId)
        if (MetadataDbHelper.STATUS_AVAILABLE == mStatus) {
            setStatus(MetadataDbHelper.STATUS_DOWNLOADING)
        } else if (MetadataDbHelper.STATUS_DISABLED == mStatus
                || MetadataDbHelper.STATUS_DELETING == mStatus) {
            setStatus(MetadataDbHelper.STATUS_INSTALLED)
        } else {
            Log.e(TAG, "Unexpected state of the word list for enabling $mStatus")
        }
    }

    private fun deleteDict() {
        val context = context
        val prefs = CommonPreferences.getCommonPreferences(context)
        CommonPreferences.disable(prefs, mWordlistId)
        setStatus(MetadataDbHelper.STATUS_DELETING)
    }

    override fun onBindView(view: View) {
        super.onBindView(view)
        (view as ViewGroup).layoutTransition = null
        val buttonSwitcher = view.findViewById<View>(
                R.id.wordlist_button_switcher) as ButtonSwitcher
        buttonSwitcher.reset(mInterfaceState)
        if (mInterfaceState.isOpen(mWordlistId)) {
            val previousStatus = mInterfaceState.getStatus(mWordlistId)
            buttonSwitcher.setStatusAndUpdateVisuals(getButtonSwitcherStatus(previousStatus))
            if (previousStatus != mStatus) {
                buttonSwitcher.setStatusAndUpdateVisuals(getButtonSwitcherStatus(mStatus))
                mInterfaceState.setOpen(mWordlistId, mStatus)
            }
        } else {
            buttonSwitcher.setStatusAndUpdateVisuals(ButtonSwitcher.STATUS_NO_BUTTON)
        }
        buttonSwitcher.setInternalOnClickListener(View.OnClickListener { onActionButtonClicked() })
        view.setOnClickListener { v -> onWordListClicked(v) }
    }

    fun onWordListClicked(v: View) {
        val parent = v.parent as? ListView ?: return
        val listView = parent
        val indexToOpen: Int
        val wasOpen = mInterfaceState.isOpen(mWordlistId)
        mInterfaceState.closeAll()
        indexToOpen = if (wasOpen) {
            -1
        } else {
            mInterfaceState.setOpen(mWordlistId, mStatus)
            listView.indexOfChild(v)
        }
        val lastDisplayedIndex = listView.lastVisiblePosition - listView.firstVisiblePosition
        for (i in 0..lastDisplayedIndex) {
            val buttonSwitcher = listView.getChildAt(i)
                    .findViewById<View>(R.id.wordlist_button_switcher) as ButtonSwitcher
            if (i == indexToOpen) {
                buttonSwitcher.setStatusAndUpdateVisuals(getButtonSwitcherStatus(mStatus))
            } else {
                buttonSwitcher.setStatusAndUpdateVisuals(ButtonSwitcher.STATUS_NO_BUTTON)
            }
        }
    }

    fun onActionButtonClicked() {
        when (getActionIdFromStatusAndMenuEntry(mStatus)) {
            ACTION_ENABLE_DICT -> enableDict()
            ACTION_DISABLE_DICT -> disableDict()
            ACTION_DELETE_DICT -> deleteDict()
            else -> Log.e(TAG, "Unknown menu item pressed")
        }
    }

    companion object {
        private val TAG = WordListPreference::class.java.simpleName
        private const val NO_STATUS_MESSAGE = ""
        private const val ACTION_UNKNOWN = 0
        private const val ACTION_ENABLE_DICT = 1
        private const val ACTION_DISABLE_DICT = 2
        private const val ACTION_DELETE_DICT = 3
        private val sStatusActionList = arrayOf(intArrayOf(), intArrayOf(ButtonSwitcher.STATUS_INSTALL, ACTION_ENABLE_DICT), intArrayOf(
            ButtonSwitcher.STATUS_CANCEL, ACTION_DISABLE_DICT
        ), intArrayOf(ButtonSwitcher.STATUS_DELETE, ACTION_DELETE_DICT), intArrayOf(ButtonSwitcher.STATUS_DELETE, ACTION_DELETE_DICT), intArrayOf(
            ButtonSwitcher.STATUS_INSTALL, ACTION_ENABLE_DICT
        ))

        fun getButtonSwitcherStatus(status: Int): Int {
            if (status >= sStatusActionList.size) {
                Log.e(TAG, "Unknown status $status")
                return ButtonSwitcher.STATUS_NO_BUTTON
            }
            return sStatusActionList[status][0]
        }

        fun getActionIdFromStatusAndMenuEntry(status: Int): Int {
            if (status >= sStatusActionList.size) {
                Log.e(TAG, "Unknown status $status")
                return ACTION_UNKNOWN
            }
            return sStatusActionList[status][1]
        }
    }

    init {
        layoutResource = R.layout.dictionary_line
        title = mDescription
        setStatus(status)
        key = mWordlistId
    }
}