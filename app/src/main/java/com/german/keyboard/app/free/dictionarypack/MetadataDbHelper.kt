package com.german.keyboard.app.free.inputmethod.dictionarypack

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.text.TextUtils
import android.util.Log
import com.german.keyboard.app.free.R
import com.german.keyboard.app.free.latin.utils.DebugLogUtils
import java.io.File
import java.util.*

class MetadataDbHelper private constructor(private val mContext: Context?, private val mClientId: String) : SQLiteOpenHelper(mContext,
        METADATA_DATABASE_NAME_STEM + if (TextUtils.isEmpty(mClientId)) "" else ".$mClientId",
        null, CURRENT_METADATA_DATABASE_VERSION
) {
    private fun createClientTable(db: SQLiteDatabase) { // The clients table only exists in the primary db, the one that has an empty client id
        if (!TextUtils.isEmpty(mClientId)) return
        db.execSQL(METADATA_CREATE_CLIENT_TABLE)
        val defaultMetadataUri = mContext!!.getString(R.string.default_metadata_uri)
        if (!TextUtils.isEmpty(defaultMetadataUri)) {
            val defaultMetadataValues = ContentValues()
            defaultMetadataValues.put(CLIENT_CLIENT_ID_COLUMN, "")
            defaultMetadataValues.put(CLIENT_METADATA_URI_COLUMN, defaultMetadataUri)
            db.insert(CLIENT_TABLE_NAME, null, defaultMetadataValues)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(METADATA_TABLE_CREATE)
        createClientTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (METADATA_DATABASE_INITIAL_VERSION == oldVersion && METADATA_DATABASE_VERSION_WITH_CLIENTID <= newVersion && CURRENT_METADATA_DATABASE_VERSION >= newVersion) {
            if (TextUtils.isEmpty(mClientId)) {
                createClientTable(db)
            }
        } else if (METADATA_DATABASE_VERSION_WITH_CLIENTID < newVersion
                && CURRENT_METADATA_DATABASE_VERSION >= newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $CLIENT_TABLE_NAME")
            if (TextUtils.isEmpty(mClientId)) {
                createClientTable(db)
            }
        } else {
            db.execSQL("DROP TABLE IF EXISTS $METADATA_TABLE_NAME")
            db.execSQL("DROP TABLE IF EXISTS $CLIENT_TABLE_NAME")
            onCreate(db)
        }
        addRawChecksumColumnUnlessPresent(db)
        addRetryCountColumnUnlessPresent(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion <= newVersion) {
            Log.e(
                TAG, "onDowngrade database but new version is higher? " + oldVersion + " <= "
                    + newVersion)
        }
        db.execSQL("DROP TABLE IF EXISTS $METADATA_TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $CLIENT_TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private val TAG = MetadataDbHelper::class.java.simpleName
        private const val METADATA_DATABASE_INITIAL_VERSION = 3
        private const val METADATA_DATABASE_VERSION_WITH_CLIENTID = 6
        private const val CURRENT_METADATA_DATABASE_VERSION = 16
        private const val NOT_A_DOWNLOAD_ID: Long = -1
        const val DICTIONARY_RETRY_THRESHOLD = 2
        const val METADATA_TABLE_NAME = "pendingUpdates"
        const val CLIENT_TABLE_NAME = "clients"
        const val PENDINGID_COLUMN = "pendingid" // Download Manager ID
        const val TYPE_COLUMN = "type"
        const val STATUS_COLUMN = "status"
        const val LOCALE_COLUMN = "locale"
        const val WORDLISTID_COLUMN = "id"
        const val DESCRIPTION_COLUMN = "description"
        const val LOCAL_FILENAME_COLUMN = "filename"
        const val REMOTE_FILENAME_COLUMN = "url"
        const val DATE_COLUMN = "date"
        const val CHECKSUM_COLUMN = "checksum"
        const val FILESIZE_COLUMN = "filesize"
        const val VERSION_COLUMN = "version"
        const val FORMATVERSION_COLUMN = "formatversion"
        const val FLAGS_COLUMN = "flags"
        const val RAW_CHECKSUM_COLUMN = "rawChecksum"
        const val RETRY_COUNT_COLUMN = "remainingRetries"
        const val COLUMN_COUNT = 15
        private const val CLIENT_CLIENT_ID_COLUMN = "clientid"
        private const val CLIENT_METADATA_URI_COLUMN = "uri"
        private const val CLIENT_METADATA_ADDITIONAL_ID_COLUMN = "additionalid"
        private const val CLIENT_LAST_UPDATE_DATE_COLUMN = "lastupdate"
        private const val CLIENT_PENDINGID_COLUMN = "pendingid" // Download Manager ID
        const val METADATA_DATABASE_NAME_STEM = "pendingUpdates"
        const val METADATA_UPDATE_DESCRIPTION = "metadata"
        const val DICTIONARIES_ASSETS_PATH = "dictionaries"
        const val STATUS_UNKNOWN = 0
        const val STATUS_AVAILABLE = 1
        const val STATUS_DOWNLOADING = 2
        const val STATUS_INSTALLED = 3
        const val STATUS_DISABLED = 4
        const val STATUS_DELETING = 5
        const val STATUS_RETRYING = 6
        const val TYPE_METADATA = 1
        const val TYPE_BULK = 2
        const val TYPE_UPDATE = 3
        private const val METADATA_TABLE_CREATE = ("CREATE TABLE " + METADATA_TABLE_NAME + " ("
                + PENDINGID_COLUMN + " INTEGER, "
                + TYPE_COLUMN + " INTEGER, "
                + STATUS_COLUMN + " INTEGER, "
                + WORDLISTID_COLUMN + " TEXT, "
                + LOCALE_COLUMN + " TEXT, "
                + DESCRIPTION_COLUMN + " TEXT, "
                + LOCAL_FILENAME_COLUMN + " TEXT, "
                + REMOTE_FILENAME_COLUMN + " TEXT, "
                + DATE_COLUMN + " INTEGER, "
                + CHECKSUM_COLUMN + " TEXT, "
                + FILESIZE_COLUMN + " INTEGER, "
                + VERSION_COLUMN + " INTEGER,"
                + FORMATVERSION_COLUMN + " INTEGER, "
                + FLAGS_COLUMN + " INTEGER, "
                + RAW_CHECKSUM_COLUMN + " TEXT,"
                + RETRY_COUNT_COLUMN + " INTEGER, "
                + "PRIMARY KEY (" + WORDLISTID_COLUMN + "," + VERSION_COLUMN + "));")
        private const val METADATA_CREATE_CLIENT_TABLE = ("CREATE TABLE IF NOT EXISTS " + CLIENT_TABLE_NAME + " ("
                + CLIENT_CLIENT_ID_COLUMN + " TEXT, "
                + CLIENT_METADATA_URI_COLUMN + " TEXT, "
                + CLIENT_METADATA_ADDITIONAL_ID_COLUMN + " TEXT, "
                + CLIENT_LAST_UPDATE_DATE_COLUMN + " INTEGER NOT NULL DEFAULT 0, "
                + CLIENT_PENDINGID_COLUMN + " INTEGER, "
                + FLAGS_COLUMN + " INTEGER, "
                + "PRIMARY KEY (" + CLIENT_CLIENT_ID_COLUMN + "));")
        val METADATA_TABLE_COLUMNS = arrayOf(
            PENDINGID_COLUMN, TYPE_COLUMN,
                STATUS_COLUMN, WORDLISTID_COLUMN, LOCALE_COLUMN, DESCRIPTION_COLUMN,
                LOCAL_FILENAME_COLUMN, REMOTE_FILENAME_COLUMN, DATE_COLUMN, CHECKSUM_COLUMN,
                FILESIZE_COLUMN, VERSION_COLUMN, FORMATVERSION_COLUMN, FLAGS_COLUMN,
                RAW_CHECKSUM_COLUMN, RETRY_COUNT_COLUMN
        )
        val CLIENT_TABLE_COLUMNS = arrayOf(
            CLIENT_CLIENT_ID_COLUMN,
                CLIENT_METADATA_URI_COLUMN, CLIENT_PENDINGID_COLUMN, FLAGS_COLUMN
        )
        val DICTIONARIES_LIST_PUBLIC_COLUMNS = arrayOf(
            STATUS_COLUMN, WORDLISTID_COLUMN,
                LOCALE_COLUMN, DESCRIPTION_COLUMN, DATE_COLUMN, FILESIZE_COLUMN, VERSION_COLUMN
        )
        private var sInstanceMap: TreeMap<String, MetadataDbHelper>? = null

        @Synchronized
        fun getInstance(context: Context?,
                        clientIdOrNull: String?): MetadataDbHelper {
            val clientId = clientIdOrNull ?: ""
            if (null == sInstanceMap) sInstanceMap = TreeMap()
            var helper = sInstanceMap!![clientId]
            if (null == helper) {
                helper = MetadataDbHelper(context, clientId)
                sInstanceMap!![clientId] = helper
            }
            return helper
        }

        fun getDb(context: Context?, clientId: String?): SQLiteDatabase {
            return getInstance(context, clientId).writableDatabase
        }

        private fun addRawChecksumColumnUnlessPresent(db: SQLiteDatabase) {
            try {
                db.execSQL("SELECT " + RAW_CHECKSUM_COLUMN + " FROM "
                        + METADATA_TABLE_NAME + " LIMIT 0;")
            } catch (e: SQLiteException) {
                Log.i(TAG, "No $RAW_CHECKSUM_COLUMN column : creating it")
                db.execSQL("ALTER TABLE " + METADATA_TABLE_NAME + " ADD COLUMN "
                        + RAW_CHECKSUM_COLUMN + " TEXT;")
            }
        }

        private fun addRetryCountColumnUnlessPresent(db: SQLiteDatabase) {
            try {
                db.execSQL("SELECT " + RETRY_COUNT_COLUMN + " FROM "
                        + METADATA_TABLE_NAME + " LIMIT 0;")
            } catch (e: SQLiteException) {
                Log.i(TAG, "No $RETRY_COUNT_COLUMN column : creating it")
                db.execSQL("ALTER TABLE " + METADATA_TABLE_NAME + " ADD COLUMN "
                        + RETRY_COUNT_COLUMN + " INTEGER DEFAULT " + DICTIONARY_RETRY_THRESHOLD + ";")
            }
        }

        fun isClientKnown(context: Context?, clientId: String?): Boolean {
            return null != getMetadataUriAsString(context, clientId)
        }

        fun getMetadataUriAsString(context: Context?, clientId: String?): String? {
            val defaultDb = getDb(context, null)
            val cursor = defaultDb.query(
                CLIENT_TABLE_NAME, arrayOf(CLIENT_METADATA_URI_COLUMN),
                    "$CLIENT_CLIENT_ID_COLUMN = ?", arrayOf(clientId),
                    null, null, null, null)
            return try {
                if (!cursor.moveToFirst()) null else MetadataUriGetter.getUri(
                    context,
                    cursor.getString(0)
                )
            } finally {
                cursor.close()
            }
        }

        fun saveLastUpdateTimeOfUri(context: Context?, uri: String) {
            PrivateLog.log("Save last update time of URI : " + uri + " " + System.currentTimeMillis())
            val values = ContentValues()
            values.put(CLIENT_LAST_UPDATE_DATE_COLUMN, System.currentTimeMillis())
            val defaultDb = getDb(context, null)
            val cursor = queryClientIds(context)
            try {
                if (!cursor.moveToFirst()) return
                do {
                    val clientId = cursor.getString(0)
                    val metadataUri = getMetadataUriAsString(context, clientId)
                    if (metadataUri == uri) {
                        defaultDb.update(
                            CLIENT_TABLE_NAME, values,
                                "$CLIENT_CLIENT_ID_COLUMN = ?", arrayOf(clientId))
                    }
                } while (cursor.moveToNext())
            } finally {
                cursor.close()
            }
        }

        fun getLastUpdateDateForClient(context: Context?, clientId: String?): Long {
            val defaultDb = getDb(context, null)
            val cursor = defaultDb.query(
                CLIENT_TABLE_NAME, arrayOf(CLIENT_LAST_UPDATE_DATE_COLUMN),
                    "$CLIENT_CLIENT_ID_COLUMN = ?", arrayOf(clientId ?: ""),
                    null, null, null, null)
            return try {
                if (!cursor.moveToFirst()) 0 else cursor.getLong(0)
                // Only one column, return it
            } finally {
                cursor.close()
            }
        }

        fun getOldestUpdateTime(context: Context?): Long {
            val defaultDb = getDb(context, null)
            val cursor = defaultDb.query(
                CLIENT_TABLE_NAME, arrayOf(CLIENT_LAST_UPDATE_DATE_COLUMN),
                    null, null, null, null, null)
            return try {
                if (!cursor.moveToFirst()) return 0
                val columnIndex = 0 // Only one column queried
                // Initialize the earliestTime to the largest possible value.
                var earliestTime = Long.MAX_VALUE // Almost 300 million years in the future
                do {
                    val thisTime = cursor.getLong(columnIndex)
                    earliestTime = Math.min(thisTime, earliestTime)
                } while (cursor.moveToNext())
                earliestTime
            } finally {
                cursor.close()
            }
        }

        fun makeContentValues(pendingId: Int, type: Int,
                              status: Int, wordlistId: String?, locale: String?,
                              description: String?, filename: String?, url: String?, date: Long,
                              rawChecksum: String?, checksum: String?, retryCount: Int,
                              filesize: Long, version: Int, formatVersion: Int): ContentValues {
            val result = ContentValues(COLUMN_COUNT)
            result.put(PENDINGID_COLUMN, pendingId)
            result.put(TYPE_COLUMN, type)
            result.put(WORDLISTID_COLUMN, wordlistId)
            result.put(STATUS_COLUMN, status)
            result.put(LOCALE_COLUMN, locale)
            result.put(DESCRIPTION_COLUMN, description)
            result.put(LOCAL_FILENAME_COLUMN, filename)
            result.put(REMOTE_FILENAME_COLUMN, url)
            result.put(DATE_COLUMN, date)
            result.put(RAW_CHECKSUM_COLUMN, rawChecksum)
            result.put(RETRY_COUNT_COLUMN, retryCount)
            result.put(CHECKSUM_COLUMN, checksum)
            result.put(FILESIZE_COLUMN, filesize)
            result.put(VERSION_COLUMN, version)
            result.put(FORMATVERSION_COLUMN, formatVersion)
            result.put(FLAGS_COLUMN, 0)
            return result
        }

        @Throws(BadFormatException::class)
        fun completeWithDefaultValues(result: ContentValues): ContentValues {
            if (null == result[WORDLISTID_COLUMN] || null == result[LOCALE_COLUMN]) {
                throw BadFormatException()
            }
            if (null == result[PENDINGID_COLUMN]) result.put(PENDINGID_COLUMN, 0)
            if (null == result[TYPE_COLUMN]) result.put(TYPE_COLUMN, TYPE_BULK)
            if (null == result[STATUS_COLUMN]) result.put(STATUS_COLUMN, STATUS_INSTALLED)
            if (null == result[DESCRIPTION_COLUMN]) result.put(DESCRIPTION_COLUMN, "")
            if (null == result[LOCAL_FILENAME_COLUMN]) result.put(LOCAL_FILENAME_COLUMN, "_")
            if (null == result[REMOTE_FILENAME_COLUMN]) result.put(REMOTE_FILENAME_COLUMN, "")
            if (null == result[DATE_COLUMN]) result.put(DATE_COLUMN, 0)
            if (null == result[RAW_CHECKSUM_COLUMN]) result.put(RAW_CHECKSUM_COLUMN, "")
            if (null == result[RETRY_COUNT_COLUMN]) result.put(
                RETRY_COUNT_COLUMN,
                    DICTIONARY_RETRY_THRESHOLD
            )
            if (null == result[CHECKSUM_COLUMN]) result.put(CHECKSUM_COLUMN, "")
            if (null == result[FILESIZE_COLUMN]) result.put(FILESIZE_COLUMN, 0)
            if (null == result[VERSION_COLUMN]) result.put(VERSION_COLUMN, 1)
            if (null == result[FLAGS_COLUMN]) result.put(FLAGS_COLUMN, 0)
            return result
        }

        private fun putStringResult(result: ContentValues, cursor: Cursor, columnId: String) {
            result.put(columnId, cursor.getString(cursor.getColumnIndex(columnId)))
        }

        private fun putIntResult(result: ContentValues, cursor: Cursor, columnId: String) {
            result.put(columnId, cursor.getInt(cursor.getColumnIndex(columnId)))
        }

        private fun getFirstLineAsContentValues(cursor: Cursor): ContentValues? {
            val result: ContentValues?
            if (cursor.moveToFirst()) {
                result = ContentValues(COLUMN_COUNT)
                putIntResult(result, cursor, PENDINGID_COLUMN)
                putIntResult(result, cursor, TYPE_COLUMN)
                putIntResult(result, cursor, STATUS_COLUMN)
                putStringResult(result, cursor, WORDLISTID_COLUMN)
                putStringResult(result, cursor, LOCALE_COLUMN)
                putStringResult(result, cursor, DESCRIPTION_COLUMN)
                putStringResult(result, cursor, LOCAL_FILENAME_COLUMN)
                putStringResult(result, cursor, REMOTE_FILENAME_COLUMN)
                putIntResult(result, cursor, DATE_COLUMN)
                putStringResult(result, cursor, RAW_CHECKSUM_COLUMN)
                putStringResult(result, cursor, CHECKSUM_COLUMN)
                putIntResult(result, cursor, RETRY_COUNT_COLUMN)
                putIntResult(result, cursor, FILESIZE_COLUMN)
                putIntResult(result, cursor, VERSION_COLUMN)
                putIntResult(result, cursor, FORMATVERSION_COLUMN)
                putIntResult(result, cursor, FLAGS_COLUMN)
                if (cursor.moveToNext()) { // TODO: print the second level of the stack to the log so that we know
                    Log.e(TAG, "Several SQL results when we expected only one!")
                }
            } else {
                result = null
            }
            return result
        }

        fun getContentValuesByPendingId(db: SQLiteDatabase,
                                        id: Long): ContentValues? {
            val cursor = db.query(
                METADATA_TABLE_NAME,
                    METADATA_TABLE_COLUMNS,
                    "$PENDINGID_COLUMN= ?", arrayOf(java.lang.Long.toString(id)),
                    null, null, null)
                    ?: return null
            return try {
                getFirstLineAsContentValues(cursor)
            } finally {
                cursor.close()
            }
        }

        fun getInstalledOrDeletingWordListContentValuesByWordListId(
                db: SQLiteDatabase, id: String?): ContentValues? {
            val cursor = db.query(
                METADATA_TABLE_NAME,
                    METADATA_TABLE_COLUMNS,
                    "$WORDLISTID_COLUMN=? AND ($STATUS_COLUMN=? OR $STATUS_COLUMN=?)", arrayOf(id, Integer.toString(
                    STATUS_INSTALLED
                ),
                    Integer.toString(STATUS_DELETING)),
                    null, null, null)
                    ?: return null
            return try {
                getFirstLineAsContentValues(cursor)
            } finally {
                cursor.close()
            }
        }

        fun getContentValuesByWordListId(db: SQLiteDatabase,
                                         id: String?, version: Int): ContentValues? {
            val cursor = db.query(
                METADATA_TABLE_NAME,
                    METADATA_TABLE_COLUMNS,
                    WORDLISTID_COLUMN + "= ? AND " + VERSION_COLUMN + "= ? AND "
                            + FORMATVERSION_COLUMN + "<= ?", arrayOf(id,
                    Integer.toString(version),
                    Integer.toString(version)
            ),
                    null /* groupBy */,
                    null /* having */,
                    "$FORMATVERSION_COLUMN DESC" /* orderBy */)
                    ?: return null
            return try {
                getFirstLineAsContentValues(cursor)
            } finally {
                cursor.close()
            }
        }

        fun getContentValuesOfLatestAvailableWordlistById(
                db: SQLiteDatabase, id: String): ContentValues? {
            val cursor = db.query(
                METADATA_TABLE_NAME,
                    METADATA_TABLE_COLUMNS,
                    "$WORDLISTID_COLUMN= ?", arrayOf(id), null, null, "$VERSION_COLUMN DESC", "1")
                    ?: return null
            return try { // Return the first result from the list of results.
                getFirstLineAsContentValues(cursor)
            } finally {
                cursor.close()
            }
        }

        fun queryInstalledOrDeletingOrAvailableDictionaryMetadata(
                context: Context?, clientId: String?): Cursor { // If clientId is null, we get the defaut DB (see #getInstance() for more about this)
            return getDb(context, clientId).query(
                METADATA_TABLE_NAME,
                    METADATA_TABLE_COLUMNS,
                    "$STATUS_COLUMN = ? OR $STATUS_COLUMN = ? OR $STATUS_COLUMN = ?", arrayOf(Integer.toString(
                    STATUS_INSTALLED
                ),
                    Integer.toString(STATUS_DELETING),
                    Integer.toString(STATUS_AVAILABLE)),
                    null, null, LOCALE_COLUMN
            )
        }

        fun queryCurrentMetadata(context: Context?, clientId: String?): Cursor { // If clientId is null, we get the defaut DB (see #getInstance() for more about this)
            return getDb(context, clientId).query(
                METADATA_TABLE_NAME,
                    METADATA_TABLE_COLUMNS, null, null, null, null, LOCALE_COLUMN
            )
        }

        fun queryDictionaries(context: Context?, clientId: String?): Cursor { // If clientId is null, we get the defaut DB (see #getInstance() for more about this)
            return getDb(context, clientId).query(
                METADATA_TABLE_NAME,
                    DICTIONARIES_LIST_PUBLIC_COLUMNS,
                    "$LOCALE_COLUMN != ?", arrayOf(""),  // TODO: Reinstate the following code for bulk, then implement partial updates
                    null, null, LOCALE_COLUMN
            )
        }


        fun deleteClient(context: Context?, clientId: String?): Boolean { // Remove all metadata associated with this client
            val db = getDb(context, clientId)
            db.execSQL("DROP TABLE IF EXISTS $METADATA_TABLE_NAME")
            db.execSQL(METADATA_TABLE_CREATE)
            val defaultDb = getDb(context, "")
            return 0 != defaultDb.delete(
                CLIENT_TABLE_NAME,
                    "$CLIENT_CLIENT_ID_COLUMN = ?", arrayOf(clientId))
        }

        fun updateClientInfo(context: Context?, clientId: String?,
                             values: ContentValues) { // Sanity check the content values
            val valuesClientId = values.getAsString(CLIENT_CLIENT_ID_COLUMN)
            val valuesMetadataUri = values.getAsString(CLIENT_METADATA_URI_COLUMN)
            val valuesMetadataAdditionalId = values.getAsString(CLIENT_METADATA_ADDITIONAL_ID_COLUMN)
            if (TextUtils.isEmpty(valuesClientId) || null == valuesMetadataUri || null == valuesMetadataAdditionalId) { // We need all these columns to be filled in
                DebugLogUtils.l("Missing parameter for updateClientInfo")
                return
            }
            if (clientId != valuesClientId) { // Mismatch! The client violates the protocol.
                DebugLogUtils.l("Received an updateClientInfo request for ", clientId,
                        " but the values " + "contain a different ID : ", valuesClientId)
                return
            }
            val defaultDb = getDb(context, "")
            if (-1L == defaultDb.insert(CLIENT_TABLE_NAME, null, values)) {
                defaultDb.update(
                    CLIENT_TABLE_NAME, values,
                        "$CLIENT_CLIENT_ID_COLUMN = ?", arrayOf(clientId))
            }
        }

        fun queryClientIds(context: Context?): Cursor {
            return getDb(context, null).query(CLIENT_TABLE_NAME, arrayOf(CLIENT_CLIENT_ID_COLUMN), null, null, null, null, null)
        }

        fun markEntryAsFinishedDownloadingAndInstalled(db: SQLiteDatabase,
                                                       r: ContentValues) {
            when (r.getAsInteger(TYPE_COLUMN)) {
                TYPE_BULK -> {
                    DebugLogUtils.l("Ended processing a wordlist")
                    val filenames: MutableList<String> = LinkedList()
                    val c = db.query(
                        METADATA_TABLE_NAME, arrayOf(LOCAL_FILENAME_COLUMN),
                            LOCALE_COLUMN + " = ? AND " +
                                    WORDLISTID_COLUMN + " = ? AND " + STATUS_COLUMN + " = ?", arrayOf(r.getAsString(
                            LOCALE_COLUMN
                        ),
                            r.getAsString(WORDLISTID_COLUMN),
                            Integer.toString(STATUS_INSTALLED)),
                            null, null, null)
                    try {
                        if (c.moveToFirst()) {
                            val filenameIndex = c.getColumnIndex(LOCAL_FILENAME_COLUMN)
                            do {
                                DebugLogUtils.l("Setting for removal", c.getString(filenameIndex))
                                filenames.add(c.getString(filenameIndex))
                            } while (c.moveToNext())
                        }
                    } finally {
                        c.close()
                    }
                    r.put(STATUS_COLUMN, STATUS_INSTALLED)
                    db.beginTransactionNonExclusive()
                    db.delete(
                        METADATA_TABLE_NAME,
                            "$WORDLISTID_COLUMN = ?", arrayOf(r.getAsString(WORDLISTID_COLUMN)))
                    db.insert(METADATA_TABLE_NAME, null, r)
                    db.setTransactionSuccessful()
                    db.endTransaction()
                    for (filename in filenames) {
                        try {
                            val f = File(filename)
                            f.delete()
                        } catch (e: SecurityException) {
                        }
                    }
                }
                else -> {
                }
            }
        }

        fun deleteDownloadingEntry(db: SQLiteDatabase, id: Long) {
            db.delete(
                METADATA_TABLE_NAME, "$PENDINGID_COLUMN = ? AND $STATUS_COLUMN = ?", arrayOf(java.lang.Long.toString(id), Integer.toString(
                    STATUS_DOWNLOADING
                )))
        }

        fun deleteEntry(db: SQLiteDatabase, id: String, version: Int) {
            db.delete(METADATA_TABLE_NAME, "$WORDLISTID_COLUMN = ? AND $VERSION_COLUMN = ?", arrayOf(id, Integer.toString(version)))
        }

        private fun markEntryAs(db: SQLiteDatabase, id: String?,
                                version: Int, status: Int, downloadId: Long) {
            val values = getContentValuesByWordListId(db, id, version)
            values!!.put(STATUS_COLUMN, status)
            if (NOT_A_DOWNLOAD_ID != downloadId) {
                values.put(PENDINGID_COLUMN, downloadId)
            }
            db.update(
                METADATA_TABLE_NAME, values,
                    "$WORDLISTID_COLUMN = ? AND $VERSION_COLUMN = ?", arrayOf(id, Integer.toString(version)))
        }

        fun markEntryAsEnabled(db: SQLiteDatabase, id: String?,
                               version: Int) {
            markEntryAs(db, id, version, STATUS_INSTALLED, NOT_A_DOWNLOAD_ID)
        }

        fun markEntryAsDisabled(db: SQLiteDatabase, id: String?,
                                version: Int) {
            markEntryAs(db, id, version, STATUS_DISABLED, NOT_A_DOWNLOAD_ID)
        }

        fun markEntryAsAvailable(db: SQLiteDatabase, id: String?,
                                 version: Int) {
            markEntryAs(db, id, version, STATUS_AVAILABLE, NOT_A_DOWNLOAD_ID)
        }

        fun markEntryAsDownloading(db: SQLiteDatabase, id: String?,
                                   version: Int, downloadId: Long) {
            markEntryAs(db, id, version, STATUS_DOWNLOADING, downloadId)
        }

        fun markEntryAsDeleting(db: SQLiteDatabase, id: String?,
                                version: Int) {
            markEntryAs(db, id, version, STATUS_DELETING, NOT_A_DOWNLOAD_ID)
        }

        fun maybeMarkEntryAsRetrying(db: SQLiteDatabase, id: String?,
                                     version: Int): Boolean {
            val values = getContentValuesByWordListId(db, id, version)
            val retryCount = values!!.getAsInteger(RETRY_COUNT_COLUMN)
            if (retryCount > 1) {
                values.put(STATUS_COLUMN, STATUS_RETRYING)
                values.put(RETRY_COUNT_COLUMN, retryCount - 1)
                db.update(
                    METADATA_TABLE_NAME, values,
                        "$WORDLISTID_COLUMN = ? AND $VERSION_COLUMN = ?", arrayOf(id, Integer.toString(version)))
                return true
            }
            return false
        }
    }
}