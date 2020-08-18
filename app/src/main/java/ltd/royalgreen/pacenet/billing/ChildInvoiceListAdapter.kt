package ltd.royalgreen.pacenet.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.billing_invoice_child_row.view.*
import ltd.royalgreen.pacenet.R


class ChildInvoiceListAdapter internal constructor(
    private val childInvoiceList: ArrayList<ChildInvoice>,
    private val listener: PayCallback
) : RecyclerView.Adapter<ChildInvoiceListAdapter.InvoiceDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_invoice_child_row, parent, false)
        return InvoiceDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceDetailViewHolder, position: Int) {
        holder.bind(childInvoiceList[position])
    }

    override fun getItemCount(): Int {
        return childInvoiceList.size
    }

    inner class InvoiceDetailViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ChildInvoice) {
            itemView.serviceName.text = if (item.packageName.isNullOrBlank()) "Unknown" else item.packageName
            itemView.invoiceNo.text = if (item.invoiceNo.isNullOrBlank()) "No: N/A" else "No: ${item.invoiceNo}"
            itemView.dueAmount.text = if (item.dueAmount == null) "Due: 0.0 BDT" else "Due: ${item.dueAmount} BDT"
            val tax = if (item.taxAmount == null) "Tax: 0.0" else "Tax: ${item.taxAmount}"
            val discount = if (item.discountAmount == null) "Discount: 0.0" else "Discount: ${item.discountAmount}"
            itemView.taxAndDiscountAmount.text = "$tax | $discount"
            itemView.totalAmount.text = if (item.invoiceTotal == null) "0.0 BDT" else "${item.invoiceTotal} BDT"
            itemView.pay.visibility = if (item.isPaid == false) View.VISIBLE else View.GONE
            itemView.pay.setOnClickListener {
                if (item.ispInvoiceId != null && item.userPackServiceId != null && item.dueAmount != null)
                    listener.onPay(item.ispInvoiceId, item.userPackServiceId, item.dueAmount, isChild = true)
            }
        }
    }

    interface PayCallback{
        fun onPay(invoiceId: Int, userPackServiceId: Int, amount: Double, isChild: Boolean)
    }
}
