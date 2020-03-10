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

    override fun onBindViewHolder(holder: PackageViewHolder, unusedPosition: Int) {
        val position = holder.adapterPosition
        val item = packageList[position]
        holder.itemView.name.text = item.packServiceName
        holder.itemView.price.text = item.packServicePrice.toString()
        holder.itemView.packCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) listener.onItemChecked(item) else listener.onItemUnChecked(item)
        }
    }

    override fun getItemCount(): Int {
        return packageList.size
    }

    interface OnItemSelectListener {
        fun onItemChecked(packageService: PackageService) {

        }

        fun onItemUnChecked(packageService: PackageService) {

        }
    }
}

class PackageViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
