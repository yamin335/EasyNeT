package ltd.royalgreen.pacenet.billing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.billing_payhist_all_service_row.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.profile.UserPackService
import ltd.royalgreen.pacenet.util.formatDateTime


class UserAllServiceListAdapter internal constructor(
    private val listener: PayBillClickListener,
    private val packServiceList: ArrayList<UserPackService>
) : RecyclerView.Adapter<UserAllServiceListAdapter.UserPackServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPackServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.billing_payhist_all_service_row, parent, false)
        return UserPackServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserPackServiceViewHolder, position: Int) {
        holder.bind(packServiceList[position])
    }

    override fun getItemCount(): Int {
        return packServiceList.size
    }

    inner class UserPackServiceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: UserPackService) {
            itemView.name.text = if (item.packServiceName.isNullOrBlank()) "Unknown" else item.packServiceName
            val price = if (item.packServicePrice.toString().isNullOrBlank()) "N/A" else item.packServicePrice.toString()
            itemView.price.text = "Price: $price BDT"
            val activeDate = if (item.activeDate.isNullOrBlank()) "N/A" else item.activeDate.formatDateTime().first
            val expireDate = if (item.expireDate.isNullOrBlank()) "N/A" else item.expireDate.formatDateTime().first
            itemView.activeDate.text = "Active: $activeDate to $expireDate"
            if (item.isActive == true) {
                itemView.status.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorGreenTheme))
            } else {
                itemView.status.setColorFilter(ContextCompat.getColor(itemView.context, R.color.colorRed))
            }
            itemView.payBill.setOnClickListener {
                listener.onPayBill(0, item.userPackServiceId, item.packServicePrice ?: 0.0)
            }
        }
    }

    interface PayBillClickListener {
        fun onPayBill(invoiceId: Int, userPackServiceId: Int, amount: Double)
    }
}
