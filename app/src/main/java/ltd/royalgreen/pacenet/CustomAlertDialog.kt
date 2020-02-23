package ltd.royalgreen.pacenet

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomAlertDialog internal constructor(private val callBack: YesCallback, private val title: String, private val subTitle: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val exitDialog: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(title)
            .setMessage(subTitle)
            .setIcon(R.mipmap.ic_launcher)
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                callBack.onYes()
                dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        return exitDialog.create()
    }

    interface YesCallback{
        fun onYes()
    }
}