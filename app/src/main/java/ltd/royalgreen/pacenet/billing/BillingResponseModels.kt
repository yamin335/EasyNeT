package ltd.royalgreen.pacenet.billing

data class PaymentHistory(val resdata: PaymentHistoryResdata?)

data class PaymentHistoryResdata(val listPayment: String?)

data class PaymentTransaction(val ispPaymentID: Long?, val ispUserID: Long?, val paidAmount: Double?,
                              val paymentStatus: String?, val invoiceNo: Long?,
                              val transactionDate: String?, val recordsTotal: Int?)

//Foster Recharge response model
data class RechargeResponse(val resdata: RechargeResdata?)

data class RechargeResdata(val message: String?, val resstate: Boolean?,
                           val paymentProcessUrl: String?, val paymentStatusUrl: String?,
                           val amount: String?)

data class RechargeStatusFosterCheckModel(val resdata: RechargeStatusFosterResdata)

data class RechargeStatusFosterResdata(val resstate: Boolean, val fosterRes: String)

data class FosterModel(val MerchantTxnNo: String?, val TxnResponse: String?, val TxnAmount: String?,
                       val Currency: String?, val ConvertionRate: String?, val OrderNo: String?,
                       val fosterid: String?, val hashkey: String?, val message: String?)

// BKash Payment Token Generation Models
data class BKashTokenResponse(val resdata: BKashTokenResdata?)

data class BKashTokenResdata(val tModel: TModel?)

data class TModel(val token: String?, val appKey: String?, val currency: String?,
                  val marchantInvNo: String?, val idtoken: String?,
                  val tokentype: String?, val refreshtoken: String?, val expiresin: String?)

data class BKashCreatePaymentResponse(val resdata: BKashCreatePaymentResdata?)

data class BKashCreatePaymentResdata(val resbKash: String?, val message: String?)

data class BKashExecutePaymentResponse(val resdata: BKashExecutePaymentResdata?)

data class BKashExecutePaymentResdata(val resstate: Boolean?, val resExecuteBk: String?)


// Bill Payment helper model

data class BillPaymentHelper(
    val balanceAmount: Double,
    val deductedAmount: Double,
    val invoiceId : Int,
    val userPackServiceId : Int,
    val canModify: Boolean,
    val isChildInvoice: Boolean = false
)


// Invoice Models
data class InvoiceResponse(
    val resdata : InvoiceResdata?
)


data class InvoiceResdata(
    val userinvoiceList: String?,
    val recordsTotal : Int?
)

data class Invoice(
    val ispInvoiceParentId: Int?, val ispUserID: Int?,
    val fullName: String?, val emailAddr: String?, val address: String?, val phoneNumber: String?,
    val userCode: String?, val invoiceNo: String?, val genMonth: String?, val invoiceDate: String?,
    val invoiceTotal: Double?, val taxAmount: Double?, val discountAmount: Double?, val dueAmount: Double?,
    val grandTotal: Double?,
    val paymentStatus: String?,
    val isPaid: Boolean?,
    val fromDate:String?, val toDate: String?, val createDate: String?, val dueAmountInWord: String?,
    val recordsTotal : Int?
)

// Invoice Details Models
data class InvoiceDetailResponse(
    val resdata : InvoiceDetailResdata?
)

data class InvoiceDetailResdata(
    val userinvoiceDetail : String?
)

data class InvoiceDetail(
    val packageId: Int?,
    val packageName: String?,
    val packagePrice : Double?
)

//Child Invoice Models
data class ChildInvoiceResponse(
    val resdata : ChildInvoiceResdata?
)

data class ChildInvoiceResdata(
    val userChildInvoiceDetail : String?
)

data class ChildInvoice(val ispInvoiceParentId: Int?, val ispInvoiceId: Int?,
                        val ispUserID: Int?, val userPackServiceId: Int?,
                        val packageId: Int?, val packageName: String?, val fullName: String?,
                        val emailAddr: String?, val address: String?, val phoneNumber: String?,
                        val userCode: String?, val invoiceNo: String?, val genMonth: String?,
                        val invoiceDate: String?, val invoiceTotal: Double?, val taxAmount: Double?,
                        val discountAmount: Double?, val dueAmount: Double?, val grandTotal: Double?,
                        val isPaid: Boolean?, val fromDate: String?, val toDate: String?,
                        val createDate: String?, val dueAmountInWord: String?)

// User Balance Models
data class UserBalanceResponse(
    val resdata: UserBalanceResdata?
)

data class UserBalanceResdata(
    val billIspUserBalance: UserBalance?
)

data class UserBalance(
    val ispuserId: Int?, val balanceAmount: Double?,
    val duesAmount: Double?,
    val isActive: Boolean?,
    val companyId: Int?,
    val createDate: String?,
    val createdBy: Int?
)




data class RechargeHistory(val resdata: RechargeHistoryResdata?)

data class RechargeHistoryResdata(val listRecharge: String?)

data class RechargeTransaction(val ispUserLedgerID: Long?, val ispUserID: Long?,
                               val debitAmount: Double?, val creditAmount: Double?,
                               val balanceAmount: Double?, val particulars: String?,
                               val transactionDate: String?, val recordsTotal: Int?)