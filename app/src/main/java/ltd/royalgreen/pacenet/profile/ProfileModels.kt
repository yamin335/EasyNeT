package ltd.royalgreen.pacenet.profile

import java.io.Serializable

data class UserPackageResponse(val resdata: UserPackageResdata?)

data class UserPackageResdata(val listContactForPackService: String?)

data class UserPackage(val userConnectionId: Number?, val connectionNo: Number?,
                       val connectionTypeId: Number?, val zoneId: Number?,
                       val ispUserId: Number?, val parentUserId: Number?,
                       val accountId: Number?, val username: String?,
                       val responsiblePersonName: String?, val email: String?,
                       val phoneNumber: String?, val address: String?,
                       val packServices: String?, val packServiceList: ArrayList<PackService>?,
                       val isDiabled: Boolean?, val listZone: ArrayList<ListZone>?)

data class ListZone(val zoneId: Number?, val zoneName: String?)

data class PackService(val userPackServiceId: Number?, val connectionNo: Number?,
                       val connectionTypeId: Number?, val ispUserId: Number?,
                       val parentUserId: Number?, val accountId: Number?,
                       val packId: Number?, val packServiceId: Int?,
                       val packServiceName: String?, val packServiceTypeId: Number?,
                       val zoneId: Number?, val packServiceType: String?,
                       val packServicePrice: Number?, val packServiceInstallCharge: Number?,
                       val packServiceOthersCharge: Number?, val isDefault: Boolean?,
                       val expireDate: String?, val activeDate: String?,
                       var isNew: Boolean?, var isUpdate: Boolean?, var isDelete: Boolean?)


data class PackageServiceResponse(val resdata: PackageServiceResdata?)

data class PackageServiceResdata(val ispservices: String?)

data class PackageServiceList(val packServiceTypeId: Number?, val packServiceType: String?, val packServices: ArrayList<PackageService>?)

data class PackageService(val packId: Number?, val packServiceId: Int?,
                          val packServiceName: String?, val packServicePrice: Number?,
                          val packServiceTypeId: Number?, val packServiceType: String?,
                          var isChecked: Boolean?, var isPurchased: Boolean = false) : Serializable

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
    val packServicePrice: Double?,
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
    val isUpdate: Boolean?, val isDelete: Boolean?
)