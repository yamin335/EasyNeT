package ltd.royalgreen.pacenet.dashboard

data class DashboardChart(val resdata: DashboardChartResdata?)
data class DashboardChartData(val dataMonth: String?, val dataRecharge: Float?, val dataPayment: Float?)
data class DashboardChartResdata(val dashboardchartdata: ArrayList<DashboardChartData>?)

data class DashSessionResponse(val resdata: DashSessionResdata?)
data class DashSessionResdata(val sessionChartData: ArrayList<DashSessionChart>?)
data class DashSessionChart(val dataValueUp: Float?, val dataValueDown: Float?, val dataName: String?)