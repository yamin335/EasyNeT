package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.package_row.view.*
import ltd.royalgreen.pacenet.R



class PackageAdapter internal constructor(private val packageList: ArrayList<PackageService>, private val listener: OnItemSelectListener) : RecyclerView.Adapter<PackageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.package_row, parent, false)
        return PackageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageViewHolder, position: Int) {
        val item = packageList[position]
        holder.itemView.name.text = item.packServiceName
        holder.itemView.price.text = item.packServicePrice.toString()
        if (item.isPurchased) {
            holder.itemView.packCheck.isChecked = true
            holder.itemView.packCheck.isEnabled = false
        } else {
            holder.itemView.packCheck.isChecked = false
            holder.itemView.packCheck.isEnabled = true
        }
        holder.itemView.packCheck.isChecked = item.isChecked == true
        holder.itemView.packCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            val adapterPosition = holder.adapterPosition
            if (isChecked) {
                packageList[adapterPosition].isChecked = true
                listener.onItemChecked(packageList[adapterPosition], adapterPosition)
            } else {
                packageList[adapterPosition].isChecked = false
                listener.onItemUnChecked(packageList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return packageList.size
    }

    interface OnItemSelectListener {
        fun onItemChecked(packageService: PackageService, position: Int) {

        }

        fun onItemUnChecked(packageService: PackageService, position: Int) {

        }
    }
}

class PackageViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
