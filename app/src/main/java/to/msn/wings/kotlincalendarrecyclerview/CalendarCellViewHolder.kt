package to.msn.wings.kotlincalendarrecyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CalendarCellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    //　プロパティ
    var view : View = itemView  // ルート要素のビューここだと ConstraintLayoutのこと

    var dateText : TextView = view.findViewById(R.id.dateText)
    var textViewToday : TextView = view.findViewById(R.id.textViewToday)
    var textViewGone : TextView =  view.findViewById(R.id.textViewGone) // Adapterで非表示にする
    var schedules : TextView = view.findViewById(R.id.schedules)

}