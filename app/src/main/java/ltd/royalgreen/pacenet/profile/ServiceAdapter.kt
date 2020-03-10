package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.service_row.view.*
import ltd.royalgreen.pacenet.R



class ServiceAdapter internal constructor(private val serviceList: ArrayList<PackageService>, private val listener: OnItemSelectListener) : RecyclerView.Adapter<ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.service_row, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, unusedPosition: Int) {
        val position = holder.adapterPosition
        val item = serviceList[position]
        holder.itemView.name.text = item.packServiceName
        holder.itemView.price.text = item.packServicePrice.toString()
        holder.itemView.serveCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) listener.onItemChecked(item) else listener.onItemUnChecked(item)
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    interface OnItemSelectListener {
        fun onItemChecked(packageService: PackageService) {

        }

        fun onItemUnChecked(packageService: PackageService) {

        }
    }
}

class ServiceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
