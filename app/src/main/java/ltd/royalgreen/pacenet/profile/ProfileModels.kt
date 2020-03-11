package ltd.royalgreen.pacenet.profile

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
                          var isChecked: Boolean?, var isPurchased: Boolean = false)