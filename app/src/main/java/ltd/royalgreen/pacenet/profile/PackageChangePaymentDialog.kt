package ltd.royalgreen.pacenet.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.profile_package_change_payment_dialog.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.showErrorToast

class PackageChangePaymentDialog internal constructor(private val packageName: String, private val requiredAmount: Double, private val payMethods: ArrayList<PayMethod>, private val callBack: PayCallback) : DialogFragment() {

    private var selectedPayMethod: PayMethod? = null
    override fun getTheme(): Int {
        return R.style.CustomDialog
    }

    override fun onResume() {
        super.onResume()
        val params = dialog?.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.profile_package_change_payment_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = "Additional payment for - $packageName"
        reqAmount.text = "$requiredAmount BDT"
        payableAmount.setText(requiredAmount.toString())

        val temp = Array(payMethods.size + 1){""}
        temp[0] = "--Select Category--"

        payMethods.forEachIndexed { index, payMethod ->
            temp[index + 1] = payMethod.methodName ?: "Unknown"
        }

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, temp)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPayMethods.adapter = adapter

        spinnerPayMethods.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                if (position != 0) {
                    try {
                        selectedPayMethod = payMethods[position - 1]
                    } catch (e: IndexOutOfBoundsException) {

                    }
                } else {
                    selectedPayMethod = null
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        closeDialog.setOnClickListener {
            dismiss()
        }

        payNow.setOnClickListener {
            val amountText = payableAmount.text.toString()
            if (amountText.isBlank()) {
                showErrorToast(requireContext(), "Enter valid amount!")
            } else if (selectedPayMethod == null) {
                showErrorToast(requireContext(), "Please select a payment method!")
            } else if (!amountText.isBlank() && "^(?=\\d)(?=.*[1-9])(\\d*)\\.?\\d+".toRegex().matches(amountText) && selectedPayMethod != null) {
                val amount = amountText.toDouble()
                if (requiredAmount == amount) {
                    callBack.onPayClicked()
                    dismiss()
                } else {
                    showErrorToast(requireContext(), "Enter valid amount!")
                }
            } else {
                showErrorToast(requireContext(), "Enter valid amount!")
            }
        }
    }

    interface PayCallback {
        fun onPayClicked()
    }
}