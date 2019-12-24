package ltd.royalgreen.pacenet.util

data class DefaultResponse(val resdata: DefaultResponseResdata)

data class DefaultResponseResdata(val message: String, val resstate: Boolean?)

data class UserDataResponse(val resdata: UserIsp)

data class UserIsp(val userIsp: String?)