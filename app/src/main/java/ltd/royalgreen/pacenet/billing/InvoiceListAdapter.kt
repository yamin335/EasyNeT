package ltd.royalgreen.pacenet.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.billing_invoice_row.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.toRounded



class InvoiceListAdapter constructor(private val listener: OnItemClickListener) : PagedListAdapter<Invoice, InvoiceListViewHolder>(InvoiceDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_invoice_row, parent, false)
        return InvoiceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceListViewHolder, position: Int) {
        val item = getItem(position)
        val name = item?.invoiceNo
        val context = holder.itemView.context
        holder.itemView.name.text = if (name.isNullOrBlank()) "Unknown" else name
        val date = item?.genMonth
        holder.itemView.date.text = if (date.isNullOrBlank()) "Month: N/A" else "Month: $date"
        val amount = item?.invoiceTotal?.toRounded(2)
        holder.itemView.amount.text = if (amount == null) "Amount: N/A" else "Amount: $amount BDT"
        val status = item?.isPaid ?: false
        holder.itemView.status.text = if (status) "Paid" else "Due"
        holder.itemView.status.background = if (status) ContextCompat.getDrawable(context, R.drawable.rounded_bg_green)
        else ContextCompat.getDrawable(context, R.drawable.rounded_bg_red)

        holder.itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(invoice: Invoice?)
    }
}

class InvoiceListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
