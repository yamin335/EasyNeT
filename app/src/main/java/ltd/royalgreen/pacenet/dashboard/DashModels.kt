package ltd.royalgreen.pacenet.dashboard

data class DashboardChart(val resdata: DashboardChartResdata?)

data class DashboardChartData(val dataMonth: String?, val dataRecharge: Float?, val dataPayment: Float?)

data class DashboardChartResdata(val dashboardchartdata: ArrayList<DashboardChartData>?)