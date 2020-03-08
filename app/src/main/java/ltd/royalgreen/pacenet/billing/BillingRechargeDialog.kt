package ltd.royalgreen.pacenet.billing

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.billing_recharge_dialog.*
import ltd.royalgreen.pacenet.R
import java.text.SimpleDateFormat
import java.util.*

class BillingRechargeDialog internal constructor(private val callBack: RechargeCallback, private val userFullName: String?, private val rechargeAmount: String?) : DialogFragment(), View.OnClickListener {

    var selectedDate = ""
    var rechargeNote = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.billing_recharge_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userFullName?.let {
            clientName.text = it
        }

        rechargeAmount?.let {
            amount.setText(isValidAmount(it))
        }

        val amountWatcher = object : TextWatcher {
            override fun beforeTextChanged(value: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(value: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    value.toString().equals("", ignoreCase = true) -> {
                        save.isEnabled = false
                        amountInputLayout.isErrorEnabled = true
                        amountInputLayout.error = "Empty Amount!"
                    }

                    "^(?=\\d)(?=.*[1-9])(\\d*)\\.?\\d+".toRegex().matches(value) -> {
                        save.isEnabled = true
                        amountInputLayout.isErrorEnabled = false
                        //rechargeAmount = value.toString()
                        amountInputLayout.helperText = ""
                    }

                    else -> {
                        save.isEnabled = false
                        amountInputLayout.isErrorEnabled = true
                        amountInputLayout.error = "Invalid Amount!"
                    }

                }
            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        amount.addTextChangedListener(amountWatcher)

        //Current Date
        val today = Calendar.getInstance().time
        val df = SimpleDateFormat("yyyy-MM-dd")
        selectedDate = df.format(today)
        save.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.save -> {
                rechargeNote = note.text.toString()
                callBack.onSavePressed(selectedDate, rechargeAmount ?: "0", rechargeNote)
                dismiss()
            }
            R.id.cancel -> dismiss()
        }
    }

    interface RechargeCallback{
        fun onSavePressed(date: String, amount: String, note: String)
    }

    private fun isValidAmount(amount: String?): String {
        return if (!amount.isNullOrBlank() && "^(?=\\d)(?=.*[1-9])(\\d*)\\.?\\d+".toRegex().matches(amount)) {
            save.isEnabled = true
            amountInputLayout.isErrorEnabled = false
            amountInputLayout.helperText = ""
            amount
        } else {
            save.isEnabled = false
            amountInputLayout.isErrorEnabled = true
            amountInputLayout.error = "Invalid Amount!"
            ""
        }
    }
}