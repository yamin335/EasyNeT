package ltd.royalgreen.pacenet.billing

import androidx.recyclerview.widget.DiffUtil

class InvoiceDiffUtilCallback : DiffUtil.ItemCallback<Invoice>() {

  override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
    return oldItem.ispInvoiceParentId == newItem.ispInvoiceParentId
  }

  override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean {
  return oldItem.ispInvoiceParentId == newItem.ispInvoiceParentId
      && oldItem.genMonth == newItem.genMonth
  }
}
