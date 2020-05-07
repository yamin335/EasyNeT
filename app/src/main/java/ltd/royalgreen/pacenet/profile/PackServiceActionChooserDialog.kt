package ltd.royalgreen.pacenet.profile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.profile_package_action_chooser_dialog.*
import ltd.royalgreen.pacenet.R

class PackServiceActionChooserDialog internal constructor(private val callBack: ChooserActionCallback) : DialogFragment() {

    override fun getTheme(): Int {
        return R.style.CustomDialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_package_action_chooser_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addPackage.setOnClickListener {
            callBack.onPackageAdd()
            dismiss()
        }

        changePackage.setOnClickListener {
            callBack.onPackageChange()
        }

        addService.setOnClickListener {
            callBack.onServiceAdd()
        }

        changeService.setOnClickListener {
            callBack.onServiceChange()
        }

        showAll.setOnClickListener {
            callBack.onShowUserPackService()
        }
    }

    interface ChooserActionCallback{
        fun onPackageAdd()
        fun onPackageChange()
        fun onServiceAdd()
        fun onServiceChange()
        fun onShowUserPackService()
    }
}