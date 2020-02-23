package ltd.royalgreen.pacenet.util

import android.content.Context
import android.content.Intent
import android.net.Uri

class FileChooser {
    companion object {
        /**
         * Get the Intent for selecting content to be used in an Intent Chooser.
         * @return The intent for opening a file with Intent.createChooser()
         */
        fun createGetContentIntent(): Intent {
            // Implicitly allow the user to select a particular kind of data
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            // The MIME data type filter
            intent.type = "*/*"
            // Only return URIs that can be opened with ContentResolver
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            return intent
        }

        fun getFileOutputUri(context: Context) {
            var uri: Uri? = null
        }
    }
}