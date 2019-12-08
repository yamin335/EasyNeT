package ltd.royalgreen.pacenet.login

data class LoginResponse(val resdata: LoginResdata?)

data class LoginResdata(val loggeduser: LoggedUser?, val message: String?)

data class LoggedUser(val userID: Number?, val userName: String?, val email: String?)