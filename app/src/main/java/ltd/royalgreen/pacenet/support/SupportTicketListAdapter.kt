package ltd.royalgreen.pacenet.support

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.support_ticket_row.view.*
import ltd.royalgreen.pacenet.R



class SupportTicketListAdapter(private val listener: OnItemClickListenerCallback) : PagedListAdapter<SupportTicket, SupportTicketListViewHolder>(SupportTicketHistDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportTicketListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.support_ticket_row, parent, false)
        return SupportTicketListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SupportTicketListViewHolder, position: Int) {
        val item = getItem(position)!!
        val context = holder.itemView.context
        holder.itemView.ticketNo.text = "No: ${item.ispTicketNo}"
        holder.itemView.subject.text = item.ticketSummary
        val status = item.status ?: "Unknown"
        holder.itemView.status.text = status
        holder.itemView.date.text = formatDate(item.createDate)
        holder.itemView.time.text = formatTime(item.createDate)

        when {
            status.equals("Pending", true) -> {
                holder.itemView.status.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg_red)
            }
            status.equals("Processing", true) -> {
                holder.itemView.status.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg_yellow)
            }
            status.equals("Resolved", true) -> {
                holder.itemView.status.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg_green)
            }
            else -> {
                holder.itemView.status.background = ContextCompat.getDrawable(context, R.drawable.rounded_bg_light_grey)
            }
        }

        holder.itemView.setOnClickListener {
            listener.onItemClicked(item)
        }
    }

    interface OnItemClickListenerCallback {
        fun onItemClicked(supportTicket: SupportTicket) {

        }

        fun onAttachmentClicked(supportTicket: SupportTicket) {

        }
    }

    private fun formatDate(value: String?): String {
        val months = arrayOf("Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec")
        var formattedDate = ""
        if (value != null && value.contains("T")) {
            val tempStringArray = value.split("T")
            val year = tempStringArray[0].split("-")[0]
            val month = tempStringArray[0].split("-")[1]
            val day = tempStringArray[0].split("-")[2]
            formattedDate = "$day ${months[month.toInt() - 1]} $year"
        } else {
            formattedDate = value ?: ""
        }
        return  formattedDate
    }

    private fun formatTime(value: String?): String {
        var formattedTime = ""
        if (value != null && value.contains("T")) {
            val tempStringArray = value.split("T")
            var tempString1 = tempStringArray[1]
            if (tempString1.contains(".")){
                tempString1 = tempString1.split(".")[0]
                tempString1 = when {
                    tempString1.split(":")[0].toInt()>12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour-12}:$minute PM"
                    }
                    tempString1.split(":")[0].toInt() == 0 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour+12}:$minute AM"
                    }
                    tempString1.split(":")[0].toInt()==12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute PM"
                    }
                    else -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute AM"
                    }
                }
            } else {
                tempString1 = when {
                    tempString1.split(":")[0].toInt()>12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour-12}:$minute PM"
                    }
                    tempString1.split(":")[0].toInt() == 0 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "${hour+12}:$minute AM"
                    }
                    tempString1.split(":")[0].toInt()==12 -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute PM"
                    }
                    else -> {
                        val hour = tempString1.split(":")[0].toInt()
                        val minute = tempString1.split(":")[1].toInt()
                        val seconds = tempString1.split(":")[2].toInt()
                        "$hour:$minute AM"
                    }
                }
            }
            formattedTime = tempString1
        } else {
            formattedTime = value ?: ""
        }
        return  formattedTime
    }
}

class SupportTicketListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
