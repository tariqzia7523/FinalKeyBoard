package com.german.keyboard.app.free.inputmethod.dictionarypack

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object MetadataHandler {
    val TAG = MetadataHandler::class.java.simpleName
    const val METADATA_FILENAME = "metadata.json"

    private fun makeMetadataObject(results: Cursor?): List<WordListMetadata> {
        val buildingMetadata = ArrayList<WordListMetadata>()
        if (null != results && results.moveToFirst()) {
            val localeColumn = results.getColumnIndex(MetadataDbHelper.LOCALE_COLUMN)
            val typeColumn = results.getColumnIndex(MetadataDbHelper.TYPE_COLUMN)
            val descriptionColumn = results.getColumnIndex(MetadataDbHelper.DESCRIPTION_COLUMN)
            val idIndex = results.getColumnIndex(MetadataDbHelper.WORDLISTID_COLUMN)
            val updateIndex = results.getColumnIndex(MetadataDbHelper.DATE_COLUMN)
            val fileSizeIndex = results.getColumnIndex(MetadataDbHelper.FILESIZE_COLUMN)
            val rawChecksumIndex = results.getColumnIndex(MetadataDbHelper.RAW_CHECKSUM_COLUMN)
            val checksumIndex = results.getColumnIndex(MetadataDbHelper.CHECKSUM_COLUMN)
            val retryCountIndex = results.getColumnIndex(MetadataDbHelper.RETRY_COUNT_COLUMN)
            val localFilenameIndex = results.getColumnIndex(MetadataDbHelper.LOCAL_FILENAME_COLUMN)
            val remoteFilenameIndex = results.getColumnIndex(MetadataDbHelper.REMOTE_FILENAME_COLUMN)
            val versionIndex = results.getColumnIndex(MetadataDbHelper.VERSION_COLUMN)
            val formatVersionIndex = results.getColumnIndex(MetadataDbHelper.FORMATVERSION_COLUMN)
            do {
                buildingMetadata.add(
                    WordListMetadata(results.getString(idIndex),
                        results.getInt(typeColumn),
                        results.getString(descriptionColumn),
                        results.getLong(updateIndex),
                        results.getLong(fileSizeIndex),
                        results.getString(rawChecksumIndex),
                        results.getString(checksumIndex),
                        results.getInt(retryCountIndex),
                        results.getString(localFilenameIndex),
                        results.getString(remoteFilenameIndex),
                        results.getInt(versionIndex),
                        results.getInt(formatVersionIndex),
                        0, results.getString(localeColumn))
                )
            } while (results.moveToNext())
        }
        return Collections.unmodifiableList(buildingMetadata)
    }

    fun getCurrentMetadata(context: Context?,
                           clientId: String?): List<WordListMetadata> {
        val results: Cursor = MetadataDbHelper.queryCurrentMetadata(context, clientId)
        return try {
            makeMetadataObject(results)
        } finally {
            results.close()
        }
    }

    fun getCurrentMetadataForWordList(context: Context?,
                                      clientId: String?, wordListId: String?, version: Int): WordListMetadata? {
        val contentValues: ContentValues = MetadataDbHelper.getContentValuesByWordListId(
            MetadataDbHelper.getDb(context, clientId), wordListId, version
        )!!
        if (contentValues == null) { // TODO: Figure out why this would happen.
            Log.e(
                TAG, String.format("Unable to find the current metadata for wordlist "
                    + "(clientId=%s, wordListId=%s, version=%d) on the database",
                    clientId, wordListId, version))
            return null
        }
        return WordListMetadata.createFromContentValues(contentValues)
    }

    @Throws(IOException::class, BadFormatException::class)
    fun readMetadata(input: InputStreamReader?): List<WordListMetadata?>? {
        return MetadataParser.parseMetadata(input)
    }

    fun findWordListById(metadata: List<WordListMetadata>,
                         id: String): WordListMetadata? {
        var bestWordList: WordListMetadata? = null
        var bestFormatVersion = Int.MIN_VALUE
        for (wordList in metadata) {
            if (id == wordList.mId && wordList.mFormatVersion > bestFormatVersion) {
                bestWordList = wordList
                bestFormatVersion = wordList.mFormatVersion
            }
        }
        return bestWordList
    }
}