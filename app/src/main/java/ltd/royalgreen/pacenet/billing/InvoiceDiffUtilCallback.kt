package ltd.royalgreen.pacenet.billing

import androidx.recyclerview.widget.DiffUtil

class InvoiceDiffUtilCallback : DiffUtil.ItemCallback<Invoice>() {

  override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
    return oldItem.ispInvoiceId == newItem.ispInvoiceId
  }

  override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
  return oldItem.ispInvoiceId == newItem.ispInvoiceId
      && oldItem.genMonth == newItem.genMonth
  }
}
