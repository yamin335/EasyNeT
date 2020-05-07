package ltd.royalgreen.pacenet.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.billing_payment_row.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.formatDateTime
import ltd.royalgreen.pacenet.util.toRounded
import java.math.BigDecimal
import java.math.RoundingMode



class PaymentListAdapter : PagedListAdapter<PaymentTransaction, PaymentListViewHolder>(PayHistDiffUtilCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentListViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_payment_row, parent, false)
    return PaymentListViewHolder(view)
  }

  override fun onBindViewHolder(holder: PaymentListViewHolder, position: Int) {
      val item = getItem(position)
      val title = item?.paymentStatus ?: "Unknown"
      holder.itemView.particulars.text = if (title.contains("::", true)) title.split("::")[0] else title
      val amount = item?.paidAmount?.toRounded(2) ?: 0.00
      holder.itemView.amount.text = "$amount BDT"
      val time = item?.transactionDate?.formatDateTime()
      holder.itemView.date.text = "Time: ${time?.first ?: "N/A"}  ${time?.second ?: ""}"
  }
}

class PaymentListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
