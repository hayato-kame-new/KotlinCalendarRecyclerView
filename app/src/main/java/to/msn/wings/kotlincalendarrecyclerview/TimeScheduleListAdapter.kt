package to.msn.wings.kotlincalendarrecyclerview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TimeScheduleListAdapter(private val _data : List<TimeScheduleListItem>) : RecyclerView.Adapter<TimeScheduleListHolder>()  {

    // 大画面かどうかの判定フラグ
    private var _isLayoutXLarge : Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeScheduleListHolder {
        val context : Context = parent.context
        val cardView : View = LayoutInflater.from(context).inflate(R.layout.time_schedule_list_item, parent, false)

        // 大画面の場合 追加  androidx(テン)のパッケージの方
        var fmanager: FragmentManager? = null
        val FINAL_F_TRANSACTION = arrayOf<FragmentTransaction?>(null) // 匿名クラスの中で使うので finalの配列にする

        // Context型から FragmentActivity型へキャストする
        val fragmentActivity : FragmentActivity = context as FragmentActivity

        // FragmentManagerインスタンスを取得する
        fmanager = fragmentActivity.supportFragmentManager

        //  フラグメントマネージャーから、 TimeScheduleActivityに所属してる フラグメントが取得できる TimeScheduleFragmentを取得する
        val timeScheduleFragment : TimeScheduleFragment = fmanager.findFragmentById(R.id.timeScheduleFragment) as TimeScheduleFragment

        // 自分自身のクラスのインスタンスのプロパティ_isLayoutXLargeに 値を代入する  この_isLayoutXLargeの値は後で  onBindViewHolderコールバックメソッドの中で使う
        _isLayoutXLarge = timeScheduleFragment.is_isLayoutXLarge()

        val FINAL_F_MANAGER: FragmentManager = fmanager

        // CardViewをクリックした時のリスナー　　大画面の時と、スマホのサイズの時に、挙動が違う
        cardView.setOnClickListener {
           // context　は　TimeScheduleActivity
            val parentActivity = context as Activity // TimeScheduleActivity
            // クリックしたアイテムの 日にちの情報   内部クラスで取得する
            val date: TextView = cardView.findViewById(R.id.date) // 内部クラスで取得する
            // クリックした時に取得するテキストは 内部クラスで取得する
            val dateString = date.text.toString() // クリックした時に取得するテキストは "2022/03/01" という形になってる
            var editDate: Date? = null
            try {
                editDate = SimpleDateFormat("yyyy/MM/dd").parse(dateString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            // 時間の情報を送ります
            val time: TextView = cardView.findViewById(R.id.time)
            val timeString = time.text.toString()
            // タイトルの情報を送ります
            val scheduleTitle: TextView = cardView.findViewById(R.id.scheduleTitle)
            val scheduleTitleString = scheduleTitle.text.toString()
            // メモの情報を送ります
            val scheduleMemo: TextView = cardView.findViewById(R.id.scheduleMemo)
            val scheduleMemoString = scheduleMemo.text.toString()
            val id: TextView = cardView.findViewById(R.id.id)
            val strId = id.text.toString()
            // intに変換して送ります
            val intId = strId.toInt()

            // 大画面の場合 同じアクティビティ上 の右に　フラグメントを新たに乗せます FrameLayoutにしてあるので、上に乗せられる
            val bundle = Bundle()
            bundle.putSerializable("date", editDate) // DATE型
            bundle.putString("action", "edit")
            bundle.putString("timeString", timeString)
            bundle.putString("scheduleTitleString", scheduleTitleString)
            bundle.putString("scheduleMemoString", scheduleMemoString)
            bundle.putInt("intId", intId) // int型
            if (_isLayoutXLarge) { // 大画面の場合 同じアクティビティ上で、フラグメントをreplaceする
                FINAL_F_TRANSACTION[0] = FINAL_F_MANAGER.beginTransaction()
                // フォームのフラグメント生成
                val scheduleFormFragment = ScheduleFormFragment()
                // 引き継ぎデータをセット
                scheduleFormFragment.arguments = bundle
                // 生成したフラグメントを、
                // id が　timeScheduleFrame　の　FrameLayoutの上に乗せます (FrameLayoutは上に追加できます)replaceメソッドで置き換えます
                FINAL_F_TRANSACTION[0]!!.replace(
                    R.id.timeScheduleFrame,
                    scheduleFormFragment
                ) // 第一引数の上に 第二引数を乗せて表示する
                FINAL_F_TRANSACTION[0]!!.commit()
                // 同じアクティビティ上なので、所属するアクティビティを終了させません
            } else {

                // スマホサイズの時
                val intent =
                    Intent(parentActivity, ScheduleFormActivity::class.java) // 新しくintentオブジェクトを作る
                intent.putExtra("date", editDate) // 日付を送ってる Date型情報を渡します
                intent.putExtra(
                    "action",
                    "edit"
                ) // 編集ということもわかるようにデータを送る キーが "action"  値が String型の "edit"

                // 編集の時には、新規とは違って、時間やタイトル メモの情報も送ります
                intent.putExtra("timeString", timeString)
                intent.putExtra("scheduleTitleString", scheduleTitleString)
                intent.putExtra("scheduleMemoString", scheduleMemoString)
                // データベースでは _idカラムで検索するので
                intent.putExtra("intId", intId)
                parentActivity.startActivity(intent) // context.startActivity(intent); でもいい

                // 小さいスマホサイズなら、画面遷移ありなので 現在のフラグメントを乗せてるサブのアクティビティを終わらせる
                parentActivity.finish() // 小さいスマホサイズなら 自分自身が所属するアクティビティを終了させます
            }
        }
        // 最後に ビューを ビューホルダーにセットして ビューホルダーのインスタンスをリターンする
        return TimeScheduleListHolder(cardView);
    }

    override fun onBindViewHolder(holder: TimeScheduleListHolder, position: Int) {
        // 日付け
        val dateText = _data[position].date
        holder.date.text = dateText

        // 開始時間 ~ 終了時間 を表示する
        val timeText = "[ " + _data[position].startTime + " ~ " + _data[position].endTime + " ]"
        holder.time.text = timeText
        holder.time.setTextColor(Color.parseColor("#006400"))
        if (_isLayoutXLarge) {  // 大画面だったら
            holder.time.setTextSize(17F)
        } else {  // 通常スマホサイズなら
            holder.time.setTextSize(14F)
        }

        // スケジュールのタイトル
        var title = _data[position].scheduleTitle
        if (title.length > 30) {  //   制限を後で android:maxLength="30"  つけたので　大丈夫だが一応
            title = title.substring(0, 31)
        }

        holder.scheduleTitle.text = title

        // 下線もつけられます  リンクに見せるようにできる
        //  <string name="link"><u>リンク文字列</u></string>  でも下線がつけられる  android:text="@string/link" とすれいい android:clickable="true"
        val scheduleTitle: TextView = holder.view.findViewById(R.id.scheduleTitle)
        scheduleTitle.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        scheduleTitle.setTextColor(Color.parseColor("blue"))
        if (_isLayoutXLarge) {  // 大画面だったら
            holder.scheduleTitle.setTextSize(22F)
        } else {  // 通常サイズなら
            holder.scheduleTitle.setTextSize(20F)
        }

        // スケジュールのメモ
        var memo = _data[position].scheduleMemo
        if (memo.length > 80) {  // 注意エラーに
            memo = memo.substring(0, 81) // 制限を android:maxLength="80"  つけたので　大丈夫だが一応
        }

        holder.scheduleMemo.text = memo
        val scheduleMemo: TextView = holder.view.findViewById(R.id.scheduleMemo)
        scheduleMemo.setTextColor(Color.parseColor("#333132"))
        if (_isLayoutXLarge) {  // 大画面だったら
            holder.scheduleMemo.setTextSize(15F)
        } else {  // 通常サイズなら
            holder.scheduleMemo.setTextSize(13F)
        }

        // idのTextView  非表示にして、データだけをフォームに送ります 上のonClickで送ってます
        val longId = _data[position].id
        // Stringへ変換する
        // Stringへ変換する
        val strId = longId.toString()
        holder.id.text = strId
        // textViewGone を非表示としたい  大切  View.VISIBLE・・・表示
        // View.INVISIBLE・・・非表示（非表示にしたスペースは詰めない）
        // View.GONE・・・非表示（非表示にしたスペースを詰める）
        // textViewGone を非表示としたい  大切  View.VISIBLE・・・表示
        // View.INVISIBLE・・・非表示（非表示にしたスペースは詰めない）
        // View.GONE・・・非表示（非表示にしたスペースを詰める）
        val id: TextView = holder.view.findViewById(R.id.id)
        id.visibility = View.GONE // これで表示しない なおかつ 非表示にしたスペースを詰める

    }

    override fun getItemCount(): Int {
        return this._data.size;
    }
}