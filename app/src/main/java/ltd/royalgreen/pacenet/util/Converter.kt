@file:JvmName("Converter")
package ltd.royalgreen.pacenet.util

fun convertToPackageString(intValue: Int): String {
    return "$intValue Package Selected"
}

fun convertToServiceString(intValue: Int): String {
    return "$intValue Service Selected"
}

fun convertToSessionChartType(type: String?): String {
    return when (type) {
        "monthly" -> "Monthly"
        "daily" -> "Daily"
        "hourly" -> "Hourly"
        else -> "Data Traffic in:"
    }
}

fun convertIntToMonth(month: Int?): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> ""
    }
}