package com.german.keyboard.app.free.inputmethod.dictionarypack

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.AbstractCursor
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.TextUtils
import android.util.Log
import com.german.keyboard.app.free.inputmethod.dictionarypack.ActionBatch.MarkPreInstalledAction
import com.german.keyboard.app.free.R
import com.german.keyboard.app.free.latin.common.LocaleUtils
import com.german.keyboard.app.free.latin.utils.DebugLogUtils
import java.io.FileNotFoundException
import java.util.*

class DictionaryProvider : ContentProvider() {
    companion object {
        private val TAG = DictionaryProvider::class.java.simpleName
        const val DEBUG = false
        val CONTENT_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + DictionaryPackConstants.AUTHORITY)
        private const val QUERY_PARAMETER_MAY_PROMPT_USER = "mayPrompt"
        private const val QUERY_PARAMETER_TRUE = "true"
        private const val QUERY_PARAMETER_DELETE_RESULT = "result"
        private const val QUERY_PARAMETER_FAILURE = "failure"
        const val QUERY_PARAMETER_PROTOCOL_VERSION = "protocol"
        private const val NO_MATCH = 0
        private const val DICTIONARY_V1_WHOLE_LIST = 1
        private const val DICTIONARY_V1_DICT_INFO = 2
        private const val DICTIONARY_V2_METADATA = 3
        private const val DICTIONARY_V2_WHOLE_LIST = 4
        private const val DICTIONARY_V2_DICT_INFO = 5
        private const val DICTIONARY_V2_DATAFILE = 6
        private val sUriMatcherV1 = UriMatcher(NO_MATCH)
        private val sUriMatcherV2 = UriMatcher(NO_MATCH)
        // MIME types for dictionary and dictionary list, as required by ContentProvider contract.
        const val DICT_LIST_MIME_TYPE = "vnd.android.cursor.item/vnd.google.dictionarylist"
        const val DICT_DATAFILE_MIME_TYPE = "vnd.android.cursor.item/vnd.google.dictionary"
        const val ID_CATEGORY_SEPARATOR = ":"
        private fun matchUri(uri: Uri): Int {
            var protocolVersion = 1
            val protocolVersionArg = uri.getQueryParameter(QUERY_PARAMETER_PROTOCOL_VERSION)
            if ("2" == protocolVersionArg) protocolVersion = 2
            return when (protocolVersion) {
                1 -> sUriMatcherV1.match(uri)
                2 -> sUriMatcherV2.match(uri)
                else -> NO_MATCH
            }
        }

        private fun getClientId(uri: Uri): String? {
            var protocolVersion = 1
            val protocolVersionArg = uri.getQueryParameter(QUERY_PARAMETER_PROTOCOL_VERSION)
            if ("2" == protocolVersionArg) protocolVersion = 2
            return when (protocolVersion) {
                1 -> null // In protocol 1, the client ID is always null.
                2 -> uri.pathSegments[0]
                else -> null
            }
        }

        init {
            sUriMatcherV1.addURI(DictionaryPackConstants.AUTHORITY, "list", DICTIONARY_V1_WHOLE_LIST)
            sUriMatcherV1.addURI(DictionaryPackConstants.AUTHORITY, "*", DICTIONARY_V1_DICT_INFO)
            sUriMatcherV2.addURI(
                DictionaryPackConstants.AUTHORITY, "*/metadata",
                    DICTIONARY_V2_METADATA
            )
            sUriMatcherV2.addURI(DictionaryPackConstants.AUTHORITY, "*/list", DICTIONARY_V2_WHOLE_LIST)
            sUriMatcherV2.addURI(
                DictionaryPackConstants.AUTHORITY, "*/dict/*",
                    DICTIONARY_V2_DICT_INFO
            )
            sUriMatcherV2.addURI(
                DictionaryPackConstants.AUTHORITY, "*/datafile/*",
                    DICTIONARY_V2_DATAFILE
            )
        }
    }

    private class WordListInfo(val mId: String, val mLocale: String, val mRawChecksum: String,
                               val mMatchLevel: Int)

    private class ResourcePathCursor(wordLists: Collection<WordListInfo>) : AbstractCursor() {
        // The list of word lists served by this provider that match the client request.
        val mWordLists: Array<WordListInfo>

        override fun getColumnNames(): Array<String> {
            return Companion.columnNames
        }

        override fun getCount(): Int {
            return mWordLists.size
        }

        override fun getDouble(column: Int): Double {
            return 0.0
        }

        override fun getFloat(column: Int): Float {
            return 0F
        }

        override fun getInt(column: Int): Int {
            return 0
        }

        override fun getShort(column: Int): Short {
            return 0
        }

        override fun getLong(column: Int): Long {
            return 0
        }

        override fun getString(column: Int): String? {
            return when (column) {
                0 -> mWordLists[mPos].mId
                1 -> mWordLists[mPos].mLocale
                2 -> mWordLists[mPos].mRawChecksum
                else -> null
            }
        }

        override fun isNull(column: Int): Boolean {
            return if (mPos >= mWordLists.size) true else column != 0
        }

        companion object {
            // Column names for the cursor returned by this content provider.
            private val columnNames = arrayOf<String>(
                MetadataDbHelper.WORDLISTID_COLUMN,
                MetadataDbHelper.LOCALE_COLUMN, MetadataDbHelper.RAW_CHECKSUM_COLUMN
            )
        }

        init {
            mWordLists = wordLists.toTypedArray()
            mPos = 0
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String? {
        PrivateLog.log("Asked for type of : $uri")
        val match = matchUri(uri)
        return when (match) {
            NO_MATCH -> null
            DICTIONARY_V1_WHOLE_LIST, DICTIONARY_V1_DICT_INFO, DICTIONARY_V2_WHOLE_LIST, DICTIONARY_V2_DICT_INFO -> DICT_LIST_MIME_TYPE
            DICTIONARY_V2_DATAFILE -> DICT_DATAFILE_MIME_TYPE
            else -> null
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        DebugLogUtils.l("Uri =", uri)
        PrivateLog.log("Query : $uri")
        val clientId = getClientId(uri)
        val match = matchUri(uri)
        return when (match) {
            DICTIONARY_V1_WHOLE_LIST, DICTIONARY_V2_WHOLE_LIST -> {
                val c: Cursor = MetadataDbHelper.queryDictionaries(context, clientId)
                DebugLogUtils.l("List of dictionaries with count", c.count)
                PrivateLog.log("Returned a list of " + c.count + " items")
                c
            }
            DICTIONARY_V2_DICT_INFO -> {
                if (!MetadataDbHelper.isClientKnown(context, clientId)) return null
                val locale = uri.lastPathSegment
                val dictFiles = getDictionaryWordListsForLocale(clientId, locale)
                // TODO: pass clientId to the following function
                if (null != dictFiles && dictFiles.size > 0) {
                    PrivateLog.log("Returned " + dictFiles.size + " files")
                    return ResourcePathCursor(dictFiles)
                }
                PrivateLog.log("No dictionary files for this URL")
                ResourcePathCursor(emptyList())
            }
            DICTIONARY_V1_DICT_INFO -> {
                val locale = uri.lastPathSegment
                val dictFiles = getDictionaryWordListsForLocale(clientId, locale)
                if (null != dictFiles && dictFiles.size > 0) {
                    PrivateLog.log("Returned " + dictFiles.size + " files")
                    return ResourcePathCursor(dictFiles)
                }
                PrivateLog.log("No dictionary files for this URL")
                ResourcePathCursor(emptyList())
            }
            else -> null
        }
    }

    private fun getWordlistMetadataForWordlistId(clientId: String?,
                                                 wordlistId: String?): ContentValues? {
        val context = context
        if (TextUtils.isEmpty(wordlistId)) return null
        val db: SQLiteDatabase = MetadataDbHelper.getDb(context, clientId)
        return MetadataDbHelper.getInstalledOrDeletingWordListContentValuesByWordListId(
            db, wordlistId
        )
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        if (null == mode || "r" != mode) return null
        val match = matchUri(uri)
        if (DICTIONARY_V1_DICT_INFO != match && DICTIONARY_V2_DATAFILE != match) { // Unsupported URI for openAssetFile
            Log.w(TAG, "Unsupported URI for openAssetFile : $uri")
            return null
        }
        val wordlistId = uri.lastPathSegment
        val clientId = getClientId(uri)
        val wordList = getWordlistMetadataForWordlistId(clientId, wordlistId) ?: return null
        try {
            val status = wordList.getAsInteger(MetadataDbHelper.STATUS_COLUMN)
            if (MetadataDbHelper.STATUS_DELETING == status) {
                return context!!.resources.openRawResourceFd(
                        R.raw.empty)
            }
            val localFilename = wordList.getAsString(MetadataDbHelper.LOCAL_FILENAME_COLUMN)
            val f = context!!.getFileStreamPath(localFilename)
            val pfd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY)
            return AssetFileDescriptor(pfd, 0, pfd.statSize)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

    private fun getDictionaryWordListsForLocale(clientId: String?,
                                                locale: String?): Collection<WordListInfo> {
        val context = context
        val results: Cursor =
            MetadataDbHelper.queryInstalledOrDeletingOrAvailableDictionaryMetadata(
                context,
                clientId
            )
        return try {
            val dicts = HashMap<String, WordListInfo>()
            val idIndex = results.getColumnIndex(MetadataDbHelper.WORDLISTID_COLUMN)
            val localeIndex = results.getColumnIndex(MetadataDbHelper.LOCALE_COLUMN)
            val localFileNameIndex = results.getColumnIndex(MetadataDbHelper.LOCAL_FILENAME_COLUMN)
            val rawChecksumIndex = results.getColumnIndex(MetadataDbHelper.RAW_CHECKSUM_COLUMN)
            val statusIndex = results.getColumnIndex(MetadataDbHelper.STATUS_COLUMN)
            if (results.moveToFirst()) {
                do {
                    val wordListId = results.getString(idIndex)
                    if (TextUtils.isEmpty(wordListId)) continue
                    val wordListIdArray = TextUtils.split(wordListId, ID_CATEGORY_SEPARATOR)
                    val wordListCategory: String
                    // This is at the category:manual_id format.
                    wordListCategory = wordListIdArray[0]
                    val wordListLocale = results.getString(localeIndex)
                    val wordListLocalFilename = results.getString(localFileNameIndex)
                    val wordListRawChecksum = results.getString(rawChecksumIndex)
                    val wordListStatus = results.getInt(statusIndex)

                    val matchLevel = LocaleUtils.getMatchLevel(wordListLocale, locale)
                    if (!LocaleUtils.isMatch(matchLevel)) {
                        continue
                    }
                    if (MetadataDbHelper.STATUS_INSTALLED == wordListStatus) {
                        val f = getContext()!!.getFileStreamPath(wordListLocalFilename)
                        if (!f.isFile) {
                            continue
                        }
                    }
                    val currentBestMatch = dicts[wordListCategory]
                    if (null == currentBestMatch
                            || currentBestMatch.mMatchLevel < matchLevel) {
                        dicts[wordListCategory] = WordListInfo(wordListId, wordListLocale,
                                wordListRawChecksum, matchLevel)
                    }
                } while (results.moveToNext())
            }
            Collections.unmodifiableCollection(dicts.values)
        } finally {
            results.close()
        }
    }

    @Throws(UnsupportedOperationException::class)
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val match = matchUri(uri)
        if (DICTIONARY_V1_DICT_INFO == match || DICTIONARY_V2_DATAFILE == match) {
            return deleteDataFile(uri)
        }
        return if (DICTIONARY_V2_METADATA == match) {
            if (MetadataDbHelper.deleteClient(context, getClientId(uri))) {
                1
            } else 0
        } else 0
    }

    private fun deleteDataFile(uri: Uri): Int {
        val wordlistId = uri.lastPathSegment
        val clientId = getClientId(uri)
        val wordList = getWordlistMetadataForWordlistId(clientId, wordlistId) ?: return 0
        val status = wordList.getAsInteger(MetadataDbHelper.STATUS_COLUMN)
        val version = wordList.getAsInteger(MetadataDbHelper.VERSION_COLUMN)
        return 0
    }

    @Throws(UnsupportedOperationException::class)
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (null == uri || null == values) return null // Should never happen but let's be safe
        PrivateLog.log("Insert, uri = $uri")
        val clientId = getClientId(uri)
        when (matchUri(uri)) {
            DICTIONARY_V2_METADATA ->
                MetadataDbHelper.updateClientInfo(context, clientId, values)
            DICTIONARY_V2_DICT_INFO -> try {
                val newDictionaryMetadata: WordListMetadata =
                    WordListMetadata.createFromContentValues(
                        MetadataDbHelper.completeWithDefaultValues(values)
                    )
                MarkPreInstalledAction(clientId, newDictionaryMetadata)
                        .execute(context)
            } catch (e: BadFormatException) {
                Log.w(TAG, "Not enough information to insert this dictionary $values", e)
            }
            DICTIONARY_V1_WHOLE_LIST, DICTIONARY_V1_DICT_INFO -> {
                PrivateLog.log("Attempt to insert : $uri")
                throw UnsupportedOperationException(
                        "Insertion in the dictionary is not supported in this version")
            }
        }
        return uri
    }

    @Throws(UnsupportedOperationException::class)
    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        PrivateLog.log("Attempt to update : $uri")
        throw UnsupportedOperationException("Updating dictionary words is not supported")
    }
}