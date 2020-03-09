package ltd.royalgreen.pacenet.profile

data class UserPackageResponse(val resdata: UserPackageResdata?)

data class UserPackageResdata(val listContactForPackService: String?)

data class UserPackage(val userConnectionId: Number?, val connectionNo: Number?,
                       val connectionTypeId: Number?, val zoneId: Number?,
                       val ispUserId: Number?, val parentUserId: Number?,
                       val accountId: Number?, val username: String?,
                       val responsiblePersonName: String?, val email: String?,
                       val phoneNumber: String?, val address: String?,
                       val packServices: String?, val packServiceList: List<PackService>?,
                       val isDiabled: Boolean?, val listZone: List<ListZone>?)

data class ListZone(val zoneId: Number?, val zoneName: String?)

data class PackService(val userPackServiceId: Number?, val connectionNo: Number?,
                       val connectionTypeId: Number?, val ispUserId: Number?,
                       val parentUserId: Number?, val accountId: Number?,
                       val packId: Number?, val packServiceId: Number?,
                       val packServiceName: String?, val packServiceTypeId: Number?,
                       val zoneId: Number?, val packServiceType: String?,
                       val packServicePrice: Number?, val packServiceInstallCharge: Number?,
                       val packServiceOthersCharge: Number?, val isDefault: Boolean?,
                       val expireDate: String?, val activeDate: String?,
                       val isNew: Boolean?, val isUpdate: Boolean?, val isDelete: Boolean?)