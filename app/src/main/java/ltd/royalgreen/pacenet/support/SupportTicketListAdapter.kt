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
        holder.itemView.ticketNo.text = "Ticket No: ${item.ispTicketNo}"
        holder.itemView.subject.text = item.ticketSummary
        val status = item.status ?: "Unknown"
        holder.itemView.status.text = status
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
}

class SupportTicketListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
