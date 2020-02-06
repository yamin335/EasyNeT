package ltd.royalgreen.pacenet.billing

import androidx.recyclerview.widget.DiffUtil

class PayHistDiffUtilCallback : DiffUtil.ItemCallback<PaymentTransaction>() {

  override fun areItemsTheSame(oldItem: PaymentTransaction, newItem: PaymentTransaction): Boolean {
    return oldItem.invoiceNo == newItem.invoiceNo
  }

  override fun areContentsTheSame(oldItem: PaymentTransaction, newItem: PaymentTransaction): Boolean {
  return oldItem.invoiceNo == newItem.invoiceNo
      && oldItem.transactionDate == newItem.transactionDate
  }
}
