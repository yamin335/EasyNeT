package ltd.royalgreen.pacenet.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.billing_invoice_detail_row.view.*
import ltd.royalgreen.pacenet.R


class InvoiceDetailListAdapter internal constructor(
    private val particularList: ArrayList<InvoiceDetail>
) : RecyclerView.Adapter<InvoiceDetailListAdapter.InvoiceDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_invoice_detail_row, parent, false)
        return InvoiceDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvoiceDetailViewHolder, position: Int) {
        holder.bind(particularList[position])
    }

    override fun getItemCount(): Int {
        return particularList.size
    }

    inner class InvoiceDetailViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: InvoiceDetail) {
            itemView.particulars.text = if (item.packageName.isNullOrBlank()) "Unknown" else item.packageName
            itemView.amount.text = if (item.packagePrice == null) "0.0 BDT" else "${item.packagePrice} BDT"
        }
    }
}
