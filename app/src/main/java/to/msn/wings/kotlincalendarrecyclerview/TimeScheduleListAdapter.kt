package to.msn.wings.kotlincalendarrecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class TimeScheduleListAdapter(private val _data : List<TimeScheduleListItem>) : RecyclerView.Adapter<TimeScheduleListHolder>()  {

    // 大画面かどうかの判定フラグ インスタンスフィールド   宣言だけ
    // onCreateViewHolderでこのインスタンスフィールドに値をセット 画面サイズの状態を代入
    // その後、onBindViewHolderでもインスタンスフィールドから取得して使う
  //  private val _isLayoutXLarge = false  // 宣言だけ クラスのインスタンスフィールドの初期値は　falseになっている
    private var _isLayoutXlarge by Delegates.notNull<Boolean>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeScheduleListHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TimeScheduleListHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}