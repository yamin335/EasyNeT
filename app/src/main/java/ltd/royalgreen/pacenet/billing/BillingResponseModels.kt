package ltd.royalgreen.pacenet.billing

data class PaymentHistory(val resdata: PaymentHistoryResdata?)

data class PaymentHistoryResdata(val listPayment: String?)

data class PaymentTransaction(val ispPaymentID: Long?, val ispUserID: Long?, val paidAmount: Double?,
                              val paymentStatus: String?, val invoiceNo: Long?,
                              val transactionDate: String?, val recordsTotal: Int?)

data class RechargeHistory(val resdata: RechargeHistoryResdata?)

data class RechargeHistoryResdata(val listRecharge: String?)

data class RechargeTransaction(val ispUserLedgerID: Long?, val ispUserID: Long?,
                               val debitAmount: Double?, val creditAmount: Double?,
                               val balanceAmount: Double?, val particulars: String?,
                               val transactionDate: String?, val recordsTotal: Int?)

//Foster Recharge response model
data class RechargeResponse(val resdata: RechargeResdata?)

data class RechargeResdata(val message: String?, val resstate: Boolean?, val paymentProcessUrl: String?, val paymentStatusUrl: String?, val amount: String?)

data class RechargeStatusFosterCheckModel(val resdata: RechargeStatusFosterResdata)

data class RechargeStatusFosterResdata(val resstate: Boolean, val fosterRes: String)

data class FosterModel(val MerchantTxnNo: String?, val TxnResponse: String?, val TxnAmount: String?,
                       val Currency: String?, val ConvertionRate: String?, val OrderNo: String?,
                       val fosterid: String?, val hashkey: String?, val message: String?)

// BKash Payment Token Generation Models
data class BKashTokenResponse(val resdata: BKashTokenResdata?)

data class BKashTokenResdata(val resstate: Boolean?, val tModel: TModel?)

data class TModel(val token: String?, val appKey: String?, val currency: String?, val marchantInvNo: String?)

data class BKashCreatePaymentResponse(val resdata: BKashCreatePaymentResdata?)

data class BKashCreatePaymentResdata(val resstate: Boolean?, val resbKash: String?)

data class BKashExecutePaymentResponse(val resdata: BKashExecutePaymentResdata?)

data class BKashExecutePaymentResdata(val resstate: Boolean?, val resExecuteBk: String?)