package ltd.royalgreen.pacenet.support

data class TicketCategoryResponse(val resdata: TicketCategoryResdata?)

data class TicketCategory(val ispTicketCategoryId: Number?, val ticketCategory: String?)

data class TicketCategoryResdata(val listTicketCategory: ArrayList<TicketCategory>?)