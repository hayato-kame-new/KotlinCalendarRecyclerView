package to.msn.wings.kotlincalendarrecyclerview

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class TimeScheduleFragment : Fragment() {

    // 大画面かどうかの判定フラグ インスタンスフィールド onViewStateRestoredコールバックメソッドをオーバーライドする
    private var _isLayoutXLarge = true // ここでは 初期値は trueにしておく

    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    // lateinit 変数は var で宣言しないといけないことに注意してください
    private lateinit var _helper : TimeScheduleDatabaseHelper

    private lateinit var _titleText : TextView

    private lateinit var _addButton : Button

    private lateinit var _returnMonButton : Button

    private lateinit var _currentMonButton : Button

    private lateinit var _day : TextView

    private lateinit var _day_today : TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_time_schedule, container, false)

        val parentActivity: Activity? = activity
        // 遷移してきたので、遷移先から、データを取得する
        val intent = parentActivity!!.intent // CalendarAdapterクラスのリスナーで画面遷移するように実装してる

        val extras = intent.extras
        var scheduleDayText = ""
        var todayString = ""
        var date: Date? = null // 文字列から　Date型へ変換するため  リスナーの匿名クラス(無名クラス インナークラス)で使用するので 後で、finalをつけて定数にする

        if (extras != null) {
            scheduleDayText = intent.getStringExtra("scheduleDayText")!!
            todayString = intent.getStringExtra("todayString")!!
            if (scheduleDayText != null && scheduleDayText !== "") {
                try {
                    date = SimpleDateFormat("yyyy/MM/dd").parse(scheduleDayText) // 文字列から　Date型へ変換する
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }

        // インナークラスで使うために final で定数に
        val DATE = date!!
        _currentMonButton = view.findViewById(R.id.currentMonButton)
        _returnMonButton = view.findViewById(R.id.returnMonButton)
        _day = view.findViewById(R.id.titleText)
        _day.setText(scheduleDayText)

        _day_today = view.findViewById(R.id.day_today)
        if (todayString != "") {
            todayString = "今日の予定 $todayString "
        }
        _day_today.setText(todayString)

        val year = scheduleDayText.substring(0, 4).toInt()
        val month = scheduleDayText.substring(5, 7).toInt()
        _returnMonButton.setText(year.toString() + "年" + month + "月カレンダーへ戻る")


        // 現在を取得して 比較する
        val localdateToday: LocalDate = LocalDate.now()
        // returnMonButton は、今月ならば 非表示にしています
        // returnMonButton は、今月ならば 非表示にしています
        if (year === localdateToday.getYear() && month === localdateToday.getMonthValue()) {
            _returnMonButton.visibility = View.GONE // これで表示しない なおかつ 非表示にしたスペースを詰める
        }

        val list = mutableListOf<Schedule>()

        _helper = TimeScheduleDatabaseHelper(parentActivity) // _helper.close();  して解放すること

        _helper.writableDatabase.use { db ->
            // SELECT文 指定の日のだけを 開始時間の順番にして取得する   SELECT * FROM テーブル名 WHERE scheduledate >= '2011-08-20' AND scheduledate < '2011-08-21' ORDER BY カラム名 ASC もしくは DESC
            //  SELECT * FROM テーブル名 WHERE date BETWEEN '2011-08-20' AND '2011-08-27'          BETWEEN  AND でもいいが 1日だけ取得したい場合では使えない ANDは、その日を含んでしまうので
            // scheduleDayText  DATE を使う
            // Date型の日付を加算するには、Calendarクラスに変換後、Calendarクラスのaddメソッドを使用します。
            // Calendarクラスのインスタンスを生成  1日後を取得したいため
            val cal = Calendar.getInstance()
            cal.time = DATE
            cal.add(Calendar.DATE, 1) // 1日後
            // 1日後を文字列で取得する  "2000/09/09" ではダメなので "2000-09-09" の形にする
            val next = SimpleDateFormat("yyyy-MM-dd").format(cal.time)
            // 指定の日を  フォーマットし直す  "2000/09/09" ではダメなので "2000-09-09" の形にする
            val dd = SimpleDateFormat("yyyy-MM-dd").format(DATE)
            // 注意 指定した日が1日だけでも、このように >= 　< 　を使って期間で指定をする 2006/09/13のデータが欲しいとき Where scheduledate = '2006/09/13' ではなく
            // Where scheduledate >= '2006/09/13' And scheduledate < '2006/09/14'
            val sqlSelect =
                "SELECT * FROM timeschedule WHERE scheduledate >= ? AND scheduledate < ? ORDER BY starttime ASC"
            val params = arrayOf(dd, next)
            val cursor: Cursor = db.rawQuery(sqlSelect, params) // 第二引数は、配列にすること
            var _id = 0
            var scheduledate = ""
            var starttime = ""
            var endtime = ""
            var scheduletitle = ""
            var schedulememo = ""
            var schedule: Schedule
            while (cursor.moveToNext()) {
                // SELECT分によって、インデックスは変わってくるので getColumnIndexで、インデックスを取得します
                val index__id: Int = cursor.getColumnIndex("_id")
                val index_scheduledate: Int =
                    cursor.getColumnIndex("scheduledate") // 引数には カラム名を指定してください
                val index_starttime: Int =
                    cursor.getColumnIndex("starttime") // 引数には カラム名を指定してください
                val index_endtime: Int = cursor.getColumnIndex("endtime") // 引数には カラム名を指定してください
                val index_scheduletitle: Int =
                    cursor.getColumnIndex("scheduletitle") // 引数には カラム名を指定してください
                val index_schedulememo: Int =
                    cursor.getColumnIndex("schedulememo") // 引数には カラム名を指定してください

                // カラムのインデックスを元に、　実際のデータを取得する
                _id = cursor.getInt(index__id)
                scheduledate = cursor.getString(index_scheduledate)
                starttime = cursor.getString(index_starttime)
                endtime = cursor.getString(index_endtime)
                scheduletitle = cursor.getString(index_scheduletitle)
                schedulememo = cursor.getString(index_schedulememo)

                // インスタンス生成
                schedule =
                    Schedule(_id, scheduledate, starttime, endtime, scheduletitle, schedulememo)
                list.add(schedule)
            }
           //  _helper.close();  // ヘルパーを解放する  ここでいいかな
        }
        _helper.close();  // ヘルパーを解放する  ここでいいかな

        /**
         * 表示だけのテキストのリスト データベースから取得したlistを使用
         */
        val data = mutableListOf<TimeScheduleListItem>()
        val item : TimeScheduleListItem? = null
        for (i in list.indices) {

          //  var schedule : Schedule= list[i]
            var (_id, scheduledate, starttime, endtime, scheduletitle, schedulememo) = list[i]

            // 主キーのデータセット _id.toLong() 後で文字列にして、アダプターで非表示にする
            val item : TimeScheduleListItem = TimeScheduleListItem(_id.toLong(), scheduleDayText , starttime,endtime, scheduletitle, schedulememo )

            data.add(item)
        }

        // 表示してる月のカレンダーへ戻るボタンにリスナーをつける  今月なら、このボタンは非表示になっております
        _returnMonButton.setOnClickListener { // 画面遷移する
            // インナークラスなので 定数 DATEを使う
            val intent = Intent(parentActivity, MonthCalendarActivity::class.java)
            // 指定した年と月のカレンダーを表示するために Date型情報を渡します
            intent.putExtra("specifyDate", DATE) //  Date型情報を渡します
            startActivity(intent)

            // 最後に 自分自身が所属するアクティビティを終了させます
            val parentActivity: Activity? = activity
            parentActivity!!.finish()
        }


        // 今月のカレンダーへ戻ります
        _currentMonButton.setOnClickListener { // 画面遷移する
            //  今月の表示に戻る MainActivityに戻る
            val intent = Intent(parentActivity, MainActivity::class.java)
            startActivity(intent)
            // 自分自身が所属するアクティビティを終了させます
            val parentActivity: Activity? = activity
            parentActivity!!.finish()
        }


        // タイムスケジュールを新規登録するボタンにリスナーをつける
        _addButton = view.findViewById(R.id.addButton)
        val FINALDATE: Date = date // 内部クラスで使うので final  定数にする

        _addButton.setOnClickListener {
            // 大画面の場合 追加
            // 大画面の場合 同じアクティビティ上 の右に　フラグメントを新たに乗せます FrameLayoutにしてあるので、上に乗せられるのです
            val bundle = Bundle()
            bundle.putSerializable("date", FINALDATE) // DATE型
            bundle.putString("action", "add")
            if (_isLayoutXLarge) { // 大画面の場合
                val manager: FragmentManager = parentFragmentManager // getFragmentManager() 非推奨になった
                val transaction: FragmentTransaction = manager.beginTransaction()
                // フォームのフラグメント生成
                val scheduleFormFragment = ScheduleFormFragment()
                // 引き継ぎデータをセット
                scheduleFormFragment.arguments = bundle
                // 生成したフラグメントを、
                // id が　timeScheduleFrame　の　FrameLayoutの上に乗せます (FrameLayoutは上に追加できます)replaceメソッドで置き換えます
                transaction.replace(
                    R.id.timeScheduleFrame,
                    scheduleFormFragment
                ) // 第一引数の上に 第二引数を乗せて表示する
                transaction.commit()
                // 同じアクティビティ上なので、所属するアクティビティを終了させません
            } else {
                // 通常画面の場合
                val intent =
                    Intent(parentActivity, ScheduleFormActivity::class.java) // 新しくintentオブジェクトを作る
                intent.putExtra("date", FINALDATE) // 日付を送ってる Date型情報を渡します インナークラスで使うので finalにしてる
                intent.putExtra(
                    "action",
                    "add"
                ) // 新規ということもわかるようにデータを送る キーが "action"  値が String型の "add"
                startActivity(intent)
                // 通常画面の場合は、別のアクティビティを起動させて表示するので 自分自身が所属するアクティビティをfinish()で終わらせます
                val parentActivity: Activity? = activity
                parentActivity!!.finish()
            }
        }

        val rv: RecyclerView = view.findViewById(R.id.rv)
        rv.setHasFixedSize(true) // パフォーマンス向上

        val manager = LinearLayoutManager(parentActivity)
        manager.orientation = LinearLayoutManager.VERTICAL
        rv.layoutManager = manager

        val adapter: RecyclerView.Adapter<*> = TimeScheduleListAdapter(data) //  dataはデータベースから取得

        rv.adapter = adapter

        // 最後にreturn viewをすること
        return view
    }

    /**
     *  onActivityCreated() メソッドは非推奨になりました。 onViewStateRestored に書いてください
     *  ここでViewの状態を復元する
     *   onCreate  onCreateView  onViewCreated 非推奨のonActivityCreated  推奨のonViewStateRestored  の順で呼ばれる
     * @param savedInstanceState
     */
    override fun onViewStateRestored(@Nullable savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val parentActivity: Activity? = activity // このフラグメントの自分　が所属するアクティビティを取得する

        // 自分が所属するアクティビティから、 id が　timeScheduleFrame　の　FrameLayoutを取得する  timeScheduleFrame
        val timeScheduleFrame = parentActivity!!.findViewById<View>(R.id.timeScheduleFrame)
        // この判定は新規ボタンが押される時に使う
        if (timeScheduleFrame == null) {  // nullならば、大画面ではないので
            // 画面判定フラグを通常画面とする
            _isLayoutXLarge = false
            // 次にTimeScheduleFragmentで、RecycleViewの CardViewのタップ時の処理(編集や削除) と　新規スケジュールボタンのタップ時の処理で
            // 画面サイズによって分岐する処理を書く
        }
    }

    /**
     * XLargeサイズかどうか
     * @return true: 大画面である <br /> false: 通常サイズ（スマホサイズ)である
     */
    fun is_isLayoutXLarge(): Boolean {
        return _isLayoutXLarge
    }

}