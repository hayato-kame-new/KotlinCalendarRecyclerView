package to.msn.wings.kotlincalendarrecyclerview

data class CalendarCellItem(
    val id: Long, // 識別するためのID
    val dateText: String,
    val todayText: String,
    val viewGoneText: String,  // 非表示にするTextViewに
    val schedules: String  // 表示する各スケジュールを連結した文字列
)
