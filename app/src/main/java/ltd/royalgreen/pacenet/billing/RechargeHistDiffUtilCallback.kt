package ltd.royalgreen.pacenet.billing

import androidx.recyclerview.widget.DiffUtil

class RechargeHistDiffUtilCallback : DiffUtil.ItemCallback<RechargeTransaction>() {

  override fun areItemsTheSame(oldItem: RechargeTransaction, newItem: RechargeTransaction): Boolean {
    return oldItem.ispUserLedgerID == newItem.ispUserLedgerID
  }

  override fun areContentsTheSame(oldItem: RechargeTransaction, newItem: RechargeTransaction): Boolean {
  return oldItem.ispUserLedgerID == newItem.ispUserLedgerID
      && oldItem.transactionDate == newItem.transactionDate
  }
}
