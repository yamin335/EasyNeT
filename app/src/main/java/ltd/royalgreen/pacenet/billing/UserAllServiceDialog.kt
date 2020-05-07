package ltd.royalgreen.pacenet.billing

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.billing_payhist_all_services_dialog.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.profile.UserPackService
import ltd.royalgreen.pacenet.util.RecyclerItemDivider
import java.util.ArrayList

class UserAllServiceDialog internal constructor(private val callBack: PayBillCallback, private val userAllService: ArrayList<UserPackService>) : DialogFragment() {

    override fun getTheme(): Int {
        return R.style.InvoiceDetailDialog
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
        return inflater.inflate(R.layout.billing_payhist_all_services_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        closeDialog.setOnClickListener {
            dismiss()
        }

        val adapter = UserAllServiceListAdapter(object : UserAllServiceListAdapter.PayBillClickListener {
            override fun onPayBill(invoiceId: Int, userPackServiceId: Int, amount: Double) {
                callBack.onAmountReceived(invoiceId, userPackServiceId, amount)
                dismiss()
            }
        }, userAllService)

        serviceRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 16))
        serviceRecycler.adapter = adapter

    }

    interface PayBillCallback {
        fun onAmountReceived(invoiceId: Int, userPackServiceId: Int, amount: Double)
    }
}