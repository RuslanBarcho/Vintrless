package pw.vintr.vintrless.tools

import android.annotation.SuppressLint
import android.content.*
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Build
import androidx.preference.PreferenceManager

class MultiprocessPreferences : ContentProvider() {

    override fun onCreate(): Boolean {
        if (matcher == null) {
            init()
        }
        return true
    }

    override fun getType(p0: Uri): String {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd." + PREFERENCE_AUTHORITY + ".item"
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String?>?): Int {
        when (matcher!!.match(uri)) {
            MATCH_DATA -> PreferenceManager.getDefaultSharedPreferences(
                context!!.applicationContext
            ).edit().clear().commit()

            else -> throw IllegalArgumentException("Unsupported uri $uri")
        }

        return 0
    }

    @SuppressLint("NewApi")
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (matcher!!.match(uri)) {
            MATCH_DATA -> {
                val editor: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(
                    context!!.applicationContext
                ).edit()
                for ((key, value) in values?.valueSet().orEmpty()) {
                    when (value) {
                        null -> { editor.remove(key) }
                        is String -> editor.putString(key, value)
                        is Boolean -> editor.putBoolean(key, value)
                        is Long -> editor.putLong(key, value)
                        is Int -> editor.putInt(key, value)
                        is Float -> editor.putFloat(key, value)
                        else -> {
                            throw IllegalArgumentException("Unsupported type $uri")
                        }
                    }
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
                    editor.apply()
                } else {
                    editor.commit()
                }
            }

            else -> throw IllegalArgumentException("Unsupported uri $uri")
        }

        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?
    ): Cursor {
        val cursor: MatrixCursor?
        when (requireNotNull(matcher).match(uri)) {
            MATCH_DATA -> {
                val key: String = uri.pathSegments[0]
                val type: String = uri.pathSegments[1]
                cursor = MatrixCursor(arrayOf(key))
                val sharedPreferences: SharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context!!.applicationContext)
                if (!sharedPreferences.contains(key)) return cursor
                val rowBuilder = cursor.newRow()
                val item: Any? = if (STRING_TYPE == type) {
                    sharedPreferences.getString(key, null)
                } else if (BOOLEAN_TYPE == type) {
                    if (sharedPreferences.getBoolean(key, false)) 1 else 0
                } else if (LONG_TYPE == type) {
                    sharedPreferences.getLong(key, 0L)
                } else if (INT_TYPE == type) {
                    sharedPreferences.getInt(key, 0)
                } else if (FLOAT_TYPE == type) {
                    sharedPreferences.getFloat(key, 0f)
                } else {
                    throw IllegalArgumentException("Unsupported type $uri")
                }
                rowBuilder.add(item)
            }

            else -> throw IllegalArgumentException("Unsupported uri $uri")
        }
        return cursor
    }

    override fun update(p0: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    class Editor(private val context: Context) {
        private val values = ContentValues()

        fun apply() {
            context.contentResolver.insert(getContentUri(KEY, TYPE), values)
        }

        fun commit() {
            apply()
        }

        fun putString(key: String?, value: String?): Editor {
            values.put(key, value)
            return this
        }

        fun putLong(key: String?, value: Long): Editor {
            values.put(key, value)
            return this
        }

        fun putBoolean(key: String?, value: Boolean): Editor {
            values.put(key, value)
            return this
        }

        fun putInt(key: String?, value: Int): Editor {
            values.put(key, value)
            return this
        }

        fun putFloat(key: String?, value: Float): Editor {
            values.put(key, value)
            return this
        }

        fun remove(key: String?) {
            values.putNull(key)
        }

        /**
         * Call content provider method immediately. apply or commit is not required for this case
         * So it's sync method.
         */
        fun clear() {
            context.contentResolver.delete(getContentUri(KEY, TYPE), null, null)
        }
    }

    class MultiprocessSharedPreferences(private val context: Context) {
        fun edit(): Editor {
            return Editor(context)
        }

        fun getString(key: String, def: String): String {
            val cursor = context.contentResolver.query(
                getContentUri(
                    key, STRING_TYPE
                ), null, null, null, null
            )
            return getStringValue(cursor, def)
        }

        fun getLong(key: String, def: Long): Long {
            val cursor = context.contentResolver.query(
                getContentUri(
                    key, LONG_TYPE
                ), null, null, null, null
            )
            return getLongValue(cursor, def)
        }

        fun getFloat(key: String, def: Float): Float {
            val cursor = context.contentResolver.query(
                getContentUri(
                    key, FLOAT_TYPE
                ), null, null, null, null
            )
            return getFloatValue(cursor, def)
        }

        fun getBoolean(key: String, def: Boolean): Boolean {
            val cursor = context.contentResolver.query(
                getContentUri(
                    key, BOOLEAN_TYPE
                ), null, null, null, null
            )
            return getBooleanValue(cursor, def)
        }

        fun getInt(key: String, def: Int): Int {
            val cursor = context.contentResolver.query(
                getContentUri(
                    key, INT_TYPE
                ), null, null, null, null
            )
            return getIntValue(cursor, def)
        }
    }

    companion object {
        const val PREFERENCE_AUTHORITY: String = "pw.vintr.vintrless.PREFFERENCE_AUTHORITY"
        private val BASE_URI: Uri? = Uri.parse("content://$PREFERENCE_AUTHORITY")

        private const val TYPE = "type"
        private const val KEY = "key"

        private const val INT_TYPE = "integer"
        private const val LONG_TYPE = "long"
        private const val FLOAT_TYPE = "float"
        private const val BOOLEAN_TYPE = "boolean"
        private const val STRING_TYPE = "string"

        private const val MATCH_DATA = 0x010000

        private var matcher: UriMatcher? = null

        private fun init() {
            matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher!!.addURI(PREFERENCE_AUTHORITY, "*/*", MATCH_DATA)
        }

        private fun getStringValue(cursor: Cursor?, def: String): String {
            if (cursor == null) return def
            var value = def
            if (cursor.moveToFirst()) {
                value = cursor.getString(0)
            }
            cursor.close()
            return value
        }

        private fun getBooleanValue(cursor: Cursor?, def: Boolean): Boolean {
            if (cursor == null) return def
            var value = def
            if (cursor.moveToFirst()) {
                value = cursor.getInt(0) > 0
            }
            cursor.close()
            return value
        }

        private fun getIntValue(cursor: Cursor?, def: Int): Int {
            if (cursor == null) return def
            var value = def
            if (cursor.moveToFirst()) {
                value = cursor.getInt(0)
            }
            cursor.close()
            return value
        }

        private fun getLongValue(cursor: Cursor?, def: Long): Long {
            if (cursor == null) return def
            var value = def
            if (cursor.moveToFirst()) {
                value = cursor.getLong(0)
            }
            cursor.close()
            return value
        }

        private fun getFloatValue(cursor: Cursor?, def: Float): Float {
            if (cursor == null) return def
            var value = def
            if (cursor.moveToFirst()) {
                value = cursor.getFloat(0)
            }
            cursor.close()
            return value
        }

        fun edit(context: Context): Editor {
            return Editor(context)
        }

        fun getDefaultSharedPreferences(context: Context): MultiprocessSharedPreferences {
            return MultiprocessSharedPreferences(context)
        }

        private fun getContentUri(key: String, type: String): Uri {
            if (BASE_URI == null) {
                init()
            }
            return requireNotNull(BASE_URI).buildUpon().appendPath(key).appendPath(type).build()
        }
    }
}
