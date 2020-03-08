package ltd.royalgreen.pacenet.login

data class LoginResponse(val resdata: LoginResdata?)

data class LoginResdata(val loggeduser: LoggedUserID?, val message: String?)

data class LoggedUserID(val userID: Number?, val userName: String?, val email: String?,
                        val userTypeId: Int?, val ispToken: String?, val tokenValidity: Int?)