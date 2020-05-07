package ltd.royalgreen.pacenet.billing.bkash

import java.io.Serializable

class CreateBkashModel : Serializable {
    var authToken: String? = null
    var rechargeAmount: Double? = null
    var currency: String? = null
    var mrcntNumber: String? = null
}

class PaymentRequest : Serializable {
    var amount: Double? = null
    var intent: String? = "sale"
}

class BkashDataModel {
    var paymentRequest = PaymentRequest()
    var createBkashModel = CreateBkashModel()
}