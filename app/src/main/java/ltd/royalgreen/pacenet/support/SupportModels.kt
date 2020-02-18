package ltd.royalgreen.pacenet.support

data class TicketCategoryResponse(val resdata: TicketCategoryResdata?)

data class TicketCategory(val ispTicketCategoryId: Number?, val ticketCategory: String?)

data class TicketCategoryResdata(val listTicketCategory: ArrayList<TicketCategory>?)

data class SupportTicketResponse(val resdata: SupportTicketResdata?)

data class SupportTicket(val ispTicketId: Long?, val ispTicketCategoryId: Int?, val ispTicketNo: String?,
                         val ispUserId: Long?, val ticketSummary: String?, val attachedFile: Any?,
                         val ticketDescription: String?, val isResolved: Boolean?, val isProcessed: Boolean?,
                         val ispUserName: String?, val ticketCategory: String?, val status: String?,
                         val isActive: Boolean?, val companyId: Int?, val createDate: String?,
                         val createdBy: Any?, val listIspTicketConversation: Any?, val listIspTicketAttachment: Any?)

data class SupportTicketResdata(val listCrmIspTicket: ArrayList<SupportTicket>?, val recordsTotal: Long?)

data class TicketCommentResponse(val resdata: Resdata?)

data class TicketAttachment(val ispTicketAttachmentId: Number?, val ispTicketId: Number?,
                            val attachedFile: String?, val isActive: Any?, val companyId: Number?,
                            val createDate: String?, val createdBy: Number?)

data class TicketConversation(val ispTicketConversationId: Number?, val ispTicketId: Number?,
                              val ispUserId: Long?, val systemUserId: String?,
                              val userFullName: String?, val ticketComment: String?,
                              val parentCommentId: Number?, val attachedFile: Any?,
                              val isActive: Boolean?, val companyId: Number?, val createDate: String?,
                              val createdBy: Number?)

data class ObjCrmIspTicket(val ispTicketId: Number?, val ispTicketCategoryId: Number?,
                           val ispTicketNo: String?, val ispUserId: Number?,
                           val ticketSummary: String?, val attachedFile: Any?,
                           val ticketDescription: String?, val isResolved: Boolean?,
                           val isProcessed: Boolean?, val ispUserName: Any?,
                           val ticketCategory: String?, val status: String?,
                           val isActive: Any?, val companyId: Any?, val createDate: String?,
                           val createdBy: Any?, val listIspTicketConversation: ArrayList<TicketConversation>?,
                           val listIspTicketAttachment: List<TicketAttachment>?)

data class Resdata(val objCrmIspTicket: ObjCrmIspTicket?)