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

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val item = serviceList[position]
        holder.itemView.name.text = item.packServiceName
        holder.itemView.price.text = item.packServicePrice.toString()
        if (item.isPurchased) {
            holder.itemView.serveCheck.isChecked = true
            holder.itemView.serveCheck.isEnabled = false
        } else {
            holder.itemView.serveCheck.isChecked = false
            holder.itemView.serveCheck.isEnabled = true
        }
        holder.itemView.serveCheck.isChecked = item.isChecked == true
        holder.itemView.serveCheck.setOnClickListener {
            serviceList[holder.adapterPosition].isChecked?.not()
            if (serviceList[holder.adapterPosition].isChecked == true) {
                //serviceList[holder.adapterPosition].isChecked = true
                listener.onItemChecked(serviceList[holder.adapterPosition], holder.adapterPosition)
            } else {
                //serviceList[holder.adapterPosition].isChecked = false
                listener.onItemUnChecked(serviceList[holder.adapterPosition], holder.adapterPosition)
            }
        }
//        holder.itemView.serveCheck.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                serviceList[holder.adapterPosition].isChecked = true
//                listener.onItemChecked(serviceList[holder.adapterPosition], holder.adapterPosition)
//            } else {
//                serviceList[holder.adapterPosition].isChecked = false
//                listener.onItemUnChecked(serviceList[holder.adapterPosition], holder.adapterPosition)
//            }
//        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    interface OnItemSelectListener {
        fun onItemChecked(packageService: PackageService, position: Int) {

        }

        fun onItemUnChecked(packageService: PackageService, position: Int) {

        }
    }
}

class ServiceViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
