package ltd.royalgreen.pacenet.profile

import java.io.Serializable

data class PackServiceResponse(val resdata: PackServiceResdata?)

data class PackServiceResdata(val ispservices: String?)

data class PackService(val packServiceId: Int?, val packServiceName: String?,
                       val packServicePrice: Double?, val packServiceTypeId: Int?,
                       val packServiceType: String?, val parentPackServiceId: Int?,
                       val parentPackServiceName: String?, val isChecked: Boolean?,
                       val isParent: Boolean?, val childPackServices: ArrayList<ChildPackService>?)

data class ChildPackService(val packServiceId: Int?, val packServiceName: String?,
                       val packServicePrice: Double?, val packServiceTypeId: Int?,
                       val packServiceType: String?, val parentPackServiceId: Int?,
                       val parentPackServiceName: String?, val isChecked: Boolean?,
                       val isParent: Boolean?)

data class UserPackServiceResponse(val resdata: UserPackServiceResdata?)

data class UserPackServiceResdata(val listProfileUser: String?)

data class UserPackServiceList(
    val userConnectionId: Int?, val ispUserId: Int?,
    val ContactPerson: String, val packServices: String?,
    val packList: ArrayList<UserPackService>?
)

data class UserPackService(
    val userId: Int?, val ispUserId: Int?, val connectionNo: Int?,
    val username: String?,
    val userTypeId: Int?, val connectionTypeId: Int?, val parentUserId: Int?, val accountId: Int?,
    val profileId: Int?,
    val password: String?,
    val userPackServiceId: Int,
    val packServiceId: Int?, val packServiceTypeId: Int?,
    val packServiceType: String?, val packServiceName: String?,
    val parentPackServiceId: Int?,
    val parentPackServiceName: String?, val particulars: String?,
    val zoneId: Int?,
    val packServicePrice: Double?, val payablePackService: Double?,
    val packServiceInstallCharge: Double?, val packServiceOthersCharge: Double?,
    val activeDate: String?, val expireDate: String?, val billingStartDate: String?, val billingEndDate: String?,
    val expireDay: Int?, val graceDay: Int?,
    val lastPayDate: String?,
    val isActive: Boolean?, val tempInActive: Boolean?, val isNoneStop: Boolean?, val isDefault: Boolean?,
    val status: String?,
    val enabled: Boolean?,
    val actualPayAmount: Double?, val payAmount: Double?, val saveAmount: Double?,
    val methodId: Int?,
    val isParent: Boolean?, val isUpGrade: Boolean?, val isDownGrade: Boolean?, val isNew: Boolean?,
    val isUpdate: Boolean?, val isDelete: Boolean?, val isChecked: Boolean? ) : Serializable

data class ConsumeDataResponse(
    val resdata: ConsumeData?
)

data class ConsumeData(
    val consumAmount: Double?,
    val restAmount: Double?,
    val restDays: Int?,
    val isPossibleChange: Boolean?,
    val isDue: Boolean?,
    val message: String?,
    val todays: String?
) : Serializable

data class PayMethodResponse(
    val resdata : PayMethodResdata?
)

data class PayMethodResdata(
    val listPaymentMethod: ArrayList<PayMethod>?
)

data class PayMethod(
    val methodId: Int,
    var methodName: String?
)

data class PackageChangeHelper(
    val isUpgrade: Boolean,
    val requiredAmount: Double,
    val actualPayAmount: Double,
    val payAmount: Double,
    val savedAmount: Double,
    val deductedAmount: Double
)