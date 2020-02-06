package ltd.royalgreen.pacenet

data class UserDataResponse(val resdata: UserIsp)

data class UserIsp(val userIsp: String?)

data class LoggedUser(val userID: Int?, val userName: String?,
                      val fullName: String?, val displayName: String?,
                      val emailAddr: String?, val email: String?,
                      val phone: String?, val balance: Double?,
                      val profileID: Int?, val srvName: String?,
                      val unitPrice: Double?, val created: String?,
                      val companyName: String?, val Address: String?)