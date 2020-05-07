package ltd.royalgreen.pacenet.billing

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerDialogFragment
import kotlinx.android.synthetic.main.billing_invoice_details_dialog.*
import kotlinx.android.synthetic.main.support_ticket_conversation_fragment.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentViewModel
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.BillingBkashWebDialogBinding
import ltd.royalgreen.pacenet.databinding.BillingInvoiceDetailsDialogBinding
import ltd.royalgreen.pacenet.dinjectors.Injectable
import ltd.royalgreen.pacenet.util.autoCleared
import ltd.royalgreen.pacenet.util.formatDateTime
import javax.inject.Inject

class InvoiceDetailDialog internal constructor(private val callBack: InvoiceDetailCallback, private val invoice: Invoice) : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: InvoiceDetailViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private var binding by autoCleared<BillingInvoiceDetailsDialogBinding>()
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

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
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.billing_invoice_details_dialog,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.getInvoiceDetail(invoice.fromDate ?: "", invoice.toDate ?: "",
            invoice.createDate ?: "", invoice.ispInvoiceId ?: 0,
            invoice.userPackServiceId ?: 0).observe(viewLifecycleOwner, Observer {
            it?.let { detailList ->
                val adapter = InvoiceDetailListAdapter(detailList)
                binding.particularsRecycler.adapter = adapter
            }
        })


        binding.title.text = if (invoice.genMonth.isNullOrBlank()) "Month: N/A" else invoice.genMonth

        val date = invoice.createDate?.formatDateTime()?.first
        binding.date.text = if (!date.isNullOrBlank()) "Date: $date" else "Date: N/A"

        binding.invoiceNo.text = if (!invoice.invoiceNo.isNullOrBlank()) "Invoice No: ${invoice.invoiceNo}" else "Invoice No: N/A"
        binding.name.text = if (invoice.fullName.isNullOrBlank()) "Unknown" else invoice.fullName
        binding.clientId.text = if (!invoice.userCode.isNullOrBlank()) "Client ID: ${invoice.userCode}" else "Client ID: N/A"

        val fromDate = invoice.fromDate?.formatDateTime()?.first ?: "N/A"
        val toDate = invoice.toDate?.formatDateTime()?.first ?: "N/A"
        binding.period.text = "Billing Period: $fromDate to $toDate"

        binding.address.text = if (!invoice.address.isNullOrBlank()) "Address: ${invoice.address}" else "Address: N/A"
        binding.tax.text = if (invoice.taxAmount != null) "${invoice.taxAmount} BDT" else "0.0 BDT"
        binding.discount.text = if (invoice.discountAmount != null) "${invoice.discountAmount} BDT" else "0.0 BDT"
        binding.total.text = if (invoice.invoiceTotal != null) "${invoice.invoiceTotal} BDT" else "0.0 BDT"

        binding.payBill.visibility = if (invoice.isPaid == false) View.VISIBLE else View.GONE

        binding.closeDialog.setOnClickListener {
            dismiss()
        }

        binding.payBill.setOnClickListener {
            if (invoice.ispInvoiceId != null && invoice.userPackServiceId != null && invoice.dueAmount != null) {
                callBack.onPayBill(invoice.ispInvoiceId, invoice.userPackServiceId, invoice.dueAmount)
            }
            dismiss()
        }
    }

    interface InvoiceDetailCallback{
        fun onPayBill(invoiceId: Int, userPackServiceId: Int, amount: Double)
    }
}