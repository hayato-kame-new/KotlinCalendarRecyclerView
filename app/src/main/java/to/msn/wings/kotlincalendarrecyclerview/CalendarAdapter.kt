package to.msn.wings.kotlincalendarrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private val _data : List<CalendarCellItem>) : RecyclerView.Adapter<CalendarCellViewHolder>() {

     private var _isLayoutXlarge : Boolean = false  // 初期値falseでいいのかな

    /**
     * ビューホルダーを生成.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarCellViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)






        return CalendarCellViewHolder(cardView)
    }

    /**
     * ビューにデータを割り当て、リスト項目を生成.
     */
    override fun onBindViewHolder(holder: CalendarCellViewHolder, position: Int) {

    }

    /**
     * データの項目数を取得
     */
    override fun getItemCount(): Int {
        return this._data.size
    }
}