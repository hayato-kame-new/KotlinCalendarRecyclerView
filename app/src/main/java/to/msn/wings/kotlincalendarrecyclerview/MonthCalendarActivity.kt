package to.msn.wings.kotlincalendarrecyclerview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * この第二画面のアクティビティの上に、MonthCalendarFragmen.
 */
class MonthCalendarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_month_calendar)
    }
}