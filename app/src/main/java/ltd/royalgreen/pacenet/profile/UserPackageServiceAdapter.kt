package ltd.royalgreen.pacenet.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.user_package_row.view.*
import ltd.royalgreen.pacenet.R



class UserPackageServiceAdapter internal constructor(private val packServiceList: ArrayList<PackService>) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_package_row, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = packServiceList[position]
        holder.itemView.name.text = item.packServiceName
        holder.itemView.type.text = item.packServiceType
        holder.itemView.price.text = item.packServicePrice.toString()
        if (item.isNew == true) {
            //holder.itemView.packServeCheck.isChecked = true
            holder.itemView.isNew.visibility = View.VISIBLE
            holder.itemView.packServeCheck.isEnabled = false
        } else {
            holder.itemView.isNew.visibility = View.GONE
        }

        holder.itemView.packServeCheck.setOnCheckedChangeListener { buttonView, isChecked ->

        }
    }

    override fun getItemCount(): Int {
        return packServiceList.size
    }
}

class MyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView)
