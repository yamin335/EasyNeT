package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.profile_user_packservice_row.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.formatDateTime


class UserPackServiceListAdapter internal constructor(
    private val packServiceList: MutableList<UserPackService>
) : RecyclerView.Adapter<UserPackServiceListAdapter.UserPackServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserPackServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_user_packservice_row, parent, false)
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
        }
    }
}
