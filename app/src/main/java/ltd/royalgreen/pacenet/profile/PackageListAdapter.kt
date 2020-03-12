package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.package_list_row.view.*
import ltd.royalgreen.pacenet.R



class PackageListAdapter internal constructor
    (private val packageList: ArrayList<PackageService>,
     private val listener: OnItemSelectListener
) : RecyclerView.Adapter<PackageListAdapter.PackageListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.package_list_row, parent, false)
        return PackageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageListViewHolder, position: Int) {
        holder.bind(packageList[position])
    }

    override fun getItemCount(): Int {
        return packageList.size
    }

    interface OnItemSelectListener {
        fun onItemClicked(packageService: PackageService)
    }

    inner class PackageListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PackageService) {
            itemView.packageName.text = item.packServiceName
            itemView.packagePrice.text = item.packServicePrice.toString()
            itemView.setOnClickListener {
                listener.onItemClicked(packageList[adapterPosition])
            }
        }
    }
}
