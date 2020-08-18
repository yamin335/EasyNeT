package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.package_list_row.view.*
import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.util.toRounded


class PackageListAdapter internal constructor
    (private val packageList: ArrayList<ChildPackService>,
     private val listener: OnItemSelectListener
) : RecyclerView.Adapter<PackageListAdapter.PackageListViewHolder>() {

    private var checkedPosition = -1

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
        fun onItemClicked(packService: ChildPackService)
    }

    inner class PackageListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ChildPackService) {
            when (checkedPosition) {
                -1 -> {
                    itemView.check.visibility = View.INVISIBLE
                }
                adapterPosition -> {
                    itemView.check.visibility = View.VISIBLE
                }
                else -> {
                    itemView.check.visibility = View.INVISIBLE
                }
            }
            itemView.packageName.text = item.packServiceName
            itemView.packagePrice.text = "${item.packServicePrice?.toRounded(2)} BDT"
            itemView.setOnClickListener {
                itemView.check.visibility = View.VISIBLE
                if (checkedPosition != adapterPosition) {
                    notifyItemChanged(checkedPosition)
                    checkedPosition = adapterPosition
                }
                listener.onItemClicked(packageList[adapterPosition])
                notifyDataSetChanged()
            }
        }
    }
}
