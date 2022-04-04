package to.msn.wings.kotlincalendarrecyclerview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView



class CalendarAdapter(private val _data : List<CalendarCellItem>) : RecyclerView.Adapter<CalendarCellViewHolder>() {

    private var _isLayoutXlarge : Boolean = false
    /**
     * ビューホルダーを生成.
     * import android.R を書くとエラーになるので注意.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarCellViewHolder {
        val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.calendar_cell, parent, false)

        // appCompatActivity は　MainActivity　の時もあるし、 MonthCalendarActivity の時もあるので 条件分岐する
        val context : Context = parent.context
        val appCompatActivity : AppCompatActivity = context as AppCompatActivity

      //  val aactivity : Activity = appCompatActivity as Activity

        var mainActivity : MainActivity? = null
         var monthCalendarActivity : MonthCalendarActivity? = null

        if (appCompatActivity.javaClass == MainActivity::class.java) { // MainActivity::class.java.simpleName
            mainActivity = appCompatActivity as MainActivity
        } else if (appCompatActivity.javaClass == MonthCalendarActivity::class.java) {
            monthCalendarActivity = appCompatActivity as MonthCalendarActivity
        }

        // 大画面の場合 追加  androidx(テン)のパッケージの方ですので注意
        var fmanager: FragmentManager? = null
        val FINAL_F_TRANSACTION = arrayOf<FragmentTransaction?>(null) // 匿名クラスの中で使うので finalの配列にする

        // 条件分岐
        if (mainActivity != null) {
            fmanager = (mainActivity as FragmentActivity).supportFragmentManager

            //  フラグメントマネージャーから、MainActivityに所属しているフラグメントを取得できます   findFragmentById メソッドを使う
            // MainActivytyには 上に CurrentMonthFragmentが乗ってるので
            val currentMonthFragment =
                fmanager.findFragmentById(R.id.currentMonthFragment) as CurrentMonthFragment?

            // このクラスのインスタンスフィールドに値をセット このあと、onBindViewHolderでも使うため
            _isLayoutXlarge =
                currentMonthFragment!!.is_isLayoutXLarge() // currentMonthFragmentインスタンスのゲッターメソッドを使う
        } else if (monthCalendarActivity != null) {
            fmanager = (monthCalendarActivity as FragmentActivity).supportFragmentManager
            // MonthCalendarActivityの 上には MonthCalendarFragment が乗っているので
            val monthCalendarFragment =
                fmanager.findFragmentById(R.id.monthCalendarFragment) as MonthCalendarFragment?
            _isLayoutXlarge = monthCalendarFragment!!.is_isLayoutXLarge()
        }

        cardView.setOnClickListener(View.OnClickListener { view ->
            val context = view.context // MainActivity が取得できてる
            val intent = Intent(context, TimeScheduleActivity::class.java)
            // 文字列になった 日付の情報を intentに putExtraする
            val textViewGone = view.findViewById<TextView>(R.id.textViewGone)
            val scheduleDayText = textViewGone.text.toString() //  "2022/03/16"  などが取れる
            intent.putExtra("scheduleDayText", scheduleDayText) //  "2022/03/16"  日付の文字列情報を送るのにセットする
            val textViewToday = view.findViewById<TextView>(R.id.textViewToday)
            val todayString = textViewToday.text.toString()
            intent.putExtra("todayString", todayString)
            context.startActivity(intent)
        })
        return CalendarCellViewHolder(cardView)
    }

    /**
     * ビューにデータを割り当て、リスト項目を生成.
     */
    override fun onBindViewHolder(holder: CalendarCellViewHolder, position: Int) {

        holder.dateText.text = this._data.get(position).dateText
        holder.textViewToday.text = this._data.get(position).todayText
        holder.textViewGone.text = this._data.get(position).viewGoneText
        holder.schedules.text = this._data.get(position).schedules

        // holder.view は CardViewです  holder.view::class
//        val c = holder.view::class
//        val cardView : CardView = holder.view as CardView

        val cardView : CardView = holder.view.findViewById(R.id.cardView);
     // カードビューの背景色 クリーム色
        cardView.setBackgroundColor(ContextCompat.getColor(holder.view.context, R.color.cardSchedule))
        val dateText: TextView = holder.view.findViewById(R.id.dateText)
        val textViewToday: TextView = holder.view.findViewById(R.id.textViewToday)
        val textViewGone: TextView = holder.view.findViewById(R.id.textViewGone)
        // textViewGone を非表示としたい  大切  View.VISIBLE・・・表示
        // View.INVISIBLE・・・非表示（非表示にしたスペースは詰めない）
        // View.GONE・・・非表示（非表示にしたスペースを詰める）
        // textViewGone を非表示としたい  大切  View.VISIBLE・・・表示
        // View.INVISIBLE・・・非表示（非表示にしたスペースは詰めない）
        // View.GONE・・・非表示（非表示にしたスペースを詰める）
        textViewGone.visibility = View.GONE // これで表示しない なおかつ 非表示にしたスペースを詰める
        val schedules: TextView = holder.view.findViewById(R.id.schedules)

        // 属性をつける
        for (i in _data.indices) {  // i　は　0からスタートになってる
            if (position === i * 7) {  // 日曜日
                cardView.setCardBackgroundColor(null)
                cardView.setBackgroundColor(ContextCompat.getColor(holder.view.context, R.color.cardScheduleSunday))
                // cardView.setCardBackgroundColor(Color.parseColor("#FF0800"))
                dateText.setTextColor(Color.parseColor("#FFFFFF"))
                textViewToday.setTextColor(Color.parseColor("#FFFFFF"))
            }
            if (position === i * 7 + 6) {  // 土曜日
                cardView.setCardBackgroundColor(null)
                cardView.setBackgroundColor(ContextCompat.getColor(holder.view.context, R.color.cardScheduleSaturday))
              //  cardView.setCardBackgroundColor(Color.parseColor("#0067c0"))
                dateText.setTextColor(Color.parseColor("#FFFFFF"))
                textViewToday.setTextColor(Color.parseColor("#FFFFFF"))
            }
            // 下線もつけられます
            //  dateText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        if (_isLayoutXlarge) {
            // もし画面サイズがタブレットサイズだったら ここで属性を変更できる
            holder.dateText.setTextSize(28F);
            holder.schedules.setTextSize(18F);
        } else {
            // 通常(スマホサイズ)画面ならば
            // ここで、属性を変更できる
        }
    }

    /**
     * データの項目数を取得
     */
    override fun getItemCount(): Int {
        return this._data.size
    }

    override fun onViewRecycled(holder: CalendarCellViewHolder) {
        super.onViewRecycled(holder)
        notifyDataSetChanged()
    }


}