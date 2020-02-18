package ltd.royalgreen.pacenet.support

import androidx.recyclerview.widget.DiffUtil

class SupportTicketHistDiffUtilCallback : DiffUtil.ItemCallback<SupportTicket>() {

  override fun areItemsTheSame(oldItem: SupportTicket, newItem: SupportTicket): Boolean {
    return oldItem.ispTicketId == newItem.ispTicketId
  }

  override fun areContentsTheSame(oldItem: SupportTicket, newItem: SupportTicket): Boolean {
  return oldItem.ispTicketId == newItem.ispTicketId
      && oldItem.ispTicketNo == newItem.ispTicketNo
  }
}
