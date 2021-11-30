package com.german.keyboard.app.free.inputmethod.dictionarypack

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log
import com.german.keyboard.app.free.latin.utils.DebugLogUtils
import java.util.*

class ActionBatch {
    interface Action {
        fun execute(context: Context?)
    }

    class MarkPreInstalledAction(clientId: String?, wordlist: WordListMetadata?) : Action {
        private val mClientId: String?

        // The word list to mark pre-installed. May not be null.
        val mWordList: WordListMetadata?

        override fun execute(context: Context?) {
            if (null == mWordList) { // This should never happen
                Log.e(TAG, "MarkPreInstalledAction with a null word list!")
                return
            }
            val db: SQLiteDatabase = MetadataDbHelper.Companion.getDb(context, mClientId)
            if (null != MetadataDbHelper.Companion.getContentValuesByWordListId(
                    db,
                    mWordList.mId, mWordList.mVersion
                )
            ) {
                Log.e(
                    TAG, "Unexpected state of the word list '" + mWordList.mId + "' "
                            + " for a markpreinstalled action. Marking as preinstalled anyway."
                )
            }
            DebugLogUtils.l("Marking word list preinstalled : $mWordList")
            val values: ContentValues = MetadataDbHelper.Companion.makeContentValues(
                0,
                MetadataDbHelper.Companion.TYPE_BULK, MetadataDbHelper.Companion.STATUS_INSTALLED,
                mWordList.mId, mWordList.mLocale, mWordList.mDescription,
                if (TextUtils.isEmpty(mWordList.mLocalFilename)) "" else mWordList.mLocalFilename,
                mWordList.mRemoteFilename, mWordList.mLastUpdate,
                mWordList.mRawChecksum, mWordList.mChecksum, mWordList.mRetryCount,
                mWordList.mFileSize, mWordList.mVersion, mWordList.mFormatVersion
            )
            PrivateLog.log(
                "Insert 'preinstalled' record for " + mWordList.mDescription
                        + " and locale " + mWordList.mLocale
            )
            db.insert(MetadataDbHelper.Companion.METADATA_TABLE_NAME, null, values)
        }

        companion object {
            val TAG = ("DictionaryProvider:"
                    + MarkPreInstalledAction::class.java.simpleName)
        }

        init {
            DebugLogUtils.l("New MarkPreInstalled action", clientId, " : ", wordlist)
            mClientId = clientId
            mWordList = wordlist
        }
    }

    private val mActions: Queue<Action>

    fun add(a: Action) {
        mActions.add(a)
    }

    fun append(that: ActionBatch) {
        for (a in that.mActions) {
            add(a)
        }
    }

    fun execute(context: Context?, reporter: ProblemReporter?) {
        DebugLogUtils.l("Executing a batch of actions")
        val remainingActions = mActions
        while (!remainingActions.isEmpty()) {
            val a = remainingActions.poll()
            try {
                a.execute(context)
            } catch (e: Exception) {
                reporter?.report(e)
            }
        }
    }

    init {
        mActions = LinkedList()
    }
}