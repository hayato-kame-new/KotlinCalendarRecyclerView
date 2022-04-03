package to.msn.wings.kotlincalendarrecyclerview

data class TimeScheduleListItem(
    val id: Long, //  データベースからの _id カラムの値を
    val date: String,
    val startTime: String,
    val endTime: String,
    val scheduleTitle: String,
    val scheduleMemo: String
)
