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