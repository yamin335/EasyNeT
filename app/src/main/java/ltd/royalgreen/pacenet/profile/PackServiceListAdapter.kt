package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.user_pack_service_row.view.*
import ltd.royalgreen.pacenet.R



class PackServiceListAdapter internal constructor(
    private val packServiceList: ArrayList<PackService>
) : RecyclerView.Adapter<PackServiceListAdapter.PackageListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_pack_service_row, parent, false)
        return PackageListViewHolder(view)
    }

    override fun onBindViewHolder(holder: PackageListViewHolder, position: Int) {
        holder.bind(packServiceList[position])
    }

    override fun getItemCount(): Int {
        return packServiceList.size
    }

    inner class PackageListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PackService) {
            itemView.packServiceName.text = item.packServiceName
            itemView.packServiceType.text = item.packServiceType
            itemView.packServicePrice.text = item.packServicePrice.toString()
        }
    }
}
