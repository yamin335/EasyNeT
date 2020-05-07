package ltd.royalgreen.pacenet.billing

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.billing_recharge_confirm_dialog.*
import ltd.royalgreen.pacenet.R

class RechargeConfirmDialog internal constructor(private val callBack: RechargeConfirmCallback, private val rechargeAmount: Double) : DialogFragment(), View.OnClickListener {

    override fun getTheme(): Int {
        return R.style.AppTheme_Dialog
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.billing_recharge_confirm_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        billAmount.text = "$rechargeAmount BDT"
        visa.setOnClickListener(this)
        bkash.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.visa -> {
                callBack.onFosterClicked(amount = rechargeAmount)
                dismiss()
            }

            R.id.bkash -> {
                callBack.onBKashClicked(amount = rechargeAmount)
                dismiss()
            }

            R.id.cancel -> dismiss()
        }
    }

    interface RechargeConfirmCallback{
        fun onFosterClicked(amount: Double)
        fun onBKashClicked(amount: Double)
    }
}