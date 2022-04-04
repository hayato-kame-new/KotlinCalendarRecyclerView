package to.msn.wings.kotlincalendarrecyclerview

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimeScheduleListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view : View = itemView  // ルート要素のビューここだと ConstraintLayoutのこと

    var date : TextView = view.findViewById(R.id.date)
    var time : TextView = view.findViewById(R.id.time)
    var scheduleTitle : TextView =  view.findViewById(R.id.scheduleTitle) // Adapterで非表示にする
    var scheduleMemo : TextView = view.findViewById(R.id.scheduleMemo)
    var id : TextView = view.findViewById(R.id.id)  //   主キーをString型にして、非表示にして、送りたいため
}