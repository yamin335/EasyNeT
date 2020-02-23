package ltd.royalgreen.pacenet.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

class FileUtils {
    companion object {
        fun getFileName(context: Context, uri: Uri): String {
            var displayName = ""

            // The query, since it only applies to a single document, will only return
            // one row. There's no need to filter, sort, or select fields, since we want
            // all fields for one document.
            val fileCursor = context.contentResolver.query(uri, null, null, null, null)

            fileCursor.use { cursor ->
                cursor?.let {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (it.moveToFirst()) {
                        // Note it's called "Display Name".  This is
                        // provider-specific, and might not necessarily be the file name.
                        displayName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }

            return displayName
        }

        fun getFileSize(context: Context, uri: Uri): String {
            var fileSize = ""

            // The query, since it only applies to a single document, will only return
            // one row. There's no need to filter, sort, or select fields, since we want
            // all fields for one document.
            val fileCursor = context.contentResolver.query(uri, null, null, null, null)

            fileCursor.use { cursor ->
                cursor?.let {
                    // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
                    // "if there's anything to look at, look at it" conditionals.
                    if (it.moveToFirst()) {
                        val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)

                        // If the size is unknown, the value stored is null.  But since an
                        // int can't be null in Java, the behavior is implementation-specific,
                        // which is just a fancy term for "unpredictable".  So as
                        // a rule, check if it's null before assigning to an int.  This will
                        // happen often:  The storage API allows for remote files, whose
                        // size might not be locally known.

                        if (!it.isNull(sizeIndex)) {
                            fileSize = it.getString(sizeIndex)
                        } else {
                            fileSize = "Unknown"
                        }
                    }
                }
            }

            return fileSize
        }
    }
}