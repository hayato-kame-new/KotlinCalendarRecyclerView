package to.msn.wings.kotlincalendarrecyclerview

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class CurrentMonthFragment : Fragment() {

    private var _isLayoutXLarge : Boolean = true  // 初期値を trueにしておく

    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    // lateinit 変数は var で宣言しないといけないことに注意してください
    private lateinit var _helper : TimeScheduleDatabaseHelper

    private lateinit var _titleText : TextView

    private lateinit var _prevButton : Button

    private lateinit var _nextButton : Button

    private lateinit var _dateManager : DateManager
    // 読み取り専用 val
    private val _SPAN_COUNT : Int = 7


    /**
     * コールバックメソッドは onCreate   onCreateView   onViewCreated
     * 非推奨のonActivityCreated   推奨のonViewStateRestored  の順で呼ばれるので
     * この onCreateViewコールバックメソッドの後に、 onViewStateRestoredコールバックメソッドが呼ばれます
     * データベースに接続して、現在の月のカレンダーを表示させる処理を書く
     * ボタンをクリックすると、前の月、後ろの月のカレンダーへ遷移する
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val parentActivity: Activity? = activity
        // 注意 import android.R　　を書いてると エラーになるので注意
        val view = inflater.inflate(R.layout.fragment_current_month, container, false)  // View型

        _dateManager = DateManager()  // コンストラクタの呼び出し DateManager型
        // _dateManagerオブジェクトから インスタンスメソッドを呼び出す 引数なしの方を呼び出す
        val dates =  _dateManager.getDays()  // List<Date>型

        val firstDate = dates.get(0)  // Date型
        val lastDate = dates.get(dates.size -1 )   // Date型

        // 文字列にする  "yyyy-MM-dd" の形にすること SQLでバインドするため  期間を指定してSELECTする
        val fDayString = SimpleDateFormat("yyyy-MM-dd").format(firstDate)  // String型
        val lDayString = SimpleDateFormat("yyyy-MM-dd").format(lastDate)  // String型

        // リストや配列のファクトリ関数である listOf や arrayOf の引数に何も指定しないと、空の配列やリストを作成することができます
        // 通常は MutableList の方を使えばいい
        // データベースから取得した データを データクラスSchedule のインスタンスにして、リストにしていく
        val list = mutableListOf<Schedule>()  // Schedule は　データクラスです

        val context = this.context  // this.activity でもいい
        // ヘルパーオブジェクトの取得
        _helper = TimeScheduleDatabaseHelper(context)
        var db: SQLiteDatabase? = null
        try {
             db = _helper.getWritableDatabase()
            val sqlSelect =
                "SELECT * FROM timeschedule WHERE scheduledate >= ? AND scheduledate <= ? ORDER BY starttime ASC"
            val params = arrayOf<String>(fDayString, lDayString)

            val cursor: Cursor = db.rawQuery(sqlSelect, params)

            var _id = 0
            var scheduledate = ""
            var starttime = ""
            var endtime = ""
            var scheduletitle = ""
            var schedulememo = ""
            var schedule : Schedule? = null

            while(cursor.moveToNext()) {
                // SELECT分によって、インデックスは変わってくるので getColumnIndexで、インデックスを取得します
                val index__id = cursor.getColumnIndex("_id") // Int型   引数には カラム名を指定してください
                val index_scheduledate = cursor.getColumnIndex("scheduledate") // 引数には カラム名を指定してください

                val index_starttime = cursor.getColumnIndex("starttime") // 引数には カラム名を指定してください

                val index_endtime = cursor.getColumnIndex("endtime") // 引数には カラム名を指定してください

                val index_scheduletitle =
                    cursor.getColumnIndex("scheduletitle") // 引数には カラム名を指定してください

                val index_schedulememo = cursor.getColumnIndex("schedulememo") // 引数には カラム名を指定してください

                // カラムのインデックスを元に、　実際のデータを取得する
                _id = cursor.getInt(index__id);
                scheduledate = cursor.getString(index_scheduledate);
                starttime = cursor.getString(index_starttime);
                endtime = cursor.getString(index_endtime);
                scheduletitle = cursor.getString(index_scheduletitle);
                schedulememo = cursor.getString(index_schedulememo);


                Log.i("SQLITE", "_id : " + _id + " " +
                        "scheduledate : " + scheduledate + " " +
                        "starttime : " + starttime + " " +
                        "endtime : " + endtime + " " +
                        "scheduletitle : " + scheduletitle + " " +
                        "schedulememo : " + schedulememo + " "
                );




                // インスタンス生成  schedule変数に null から 上書きする
                schedule =
                    Schedule(_id, scheduledate, starttime, endtime, scheduletitle, schedulememo)
                list.add(schedule)  // ループの中で どんどんリストに足していく
            }

        }catch(e: SQLiteException){
            println(e)
        }finally {
            db?.close()  // SQLiteDatabaseオブジェクトを解放する
            _helper.close()   // ヘルパーを解放する  ここで解放する
        }

        // ここから
         _titleText = view.findViewById<TextView>(R.id.titleText)
        // 最初の土曜日を取得する   最初の土曜日は、その月に必ずなってるから
        // 最初の土曜日を取得する   最初の土曜日は、その月に必ずなってるから
         val firstSaturdayDate: Date = dates.get(6)

        var format : SimpleDateFormat = SimpleDateFormat("今月のカレンダー yyyy年 MM月")
        val title : String = format.format(firstSaturdayDate)
        _titleText.text = title

        // 表示用のフォートマットし直し
        format = SimpleDateFormat("d")  // "dd" だと 01 02 となってしまうので "d" とする

        // もう一つフォーマット用意 非表示のTextViewに使う
        val sdFormat : SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd")

        // 比較をするために本日現在を取得する
        val calendar = Calendar.getInstance()
        val todayMonth = calendar[Calendar.MONTH] + 1 // 現在の月 Calendar.MONTHは 0　から始まるので注意

        val todayDay = calendar[Calendar.DATE] // 現在の日


        /**
         * RecyclerViewで CardViewに表示するリスト.
         * データクラスCalendarCellItem型のリスト
         */
        val data = mutableListOf<CalendarCellItem>()  // CalendarCellItem は　データクラスです
        var item : CalendarCellItem? = null

        for ( i in 0..dates.size - 1) {  // 0 からにする リストの添字だから
            var date : Date = dates.get(i)
            // Calendarに変換する
            calendar.time = date
            val y = calendar[Calendar.YEAR]
            val m = calendar[Calendar.MONTH] + 1
            val d = calendar[Calendar.DATE]

            var todayText : String = ""  // 初期値は空文字で
            if (todayMonth == m && todayDay == d) {
                todayText = "●"  // 本日だったら印をつける
            }

            var dateText : String = format.format(date)
            var viewGoneText : String = sdFormat.format(date) // 2017/03/02  yyyy/MM/dd という形にする

            // スケジュール表示の文字列
            var schedules : String = ""
            // データベースから 取得した listの中に、同じ日付のデータがあれば、そのデータからスケジュールの開始時間とタイトルを取得して文字列にする
            for ( schedule : Schedule in list) {
                val scheduledate: String = schedule.scheduledate // "2022-03-25"

                if ( y ==  (scheduledate.substring(0, 4)).toIntOrNull() &&  m == (scheduledate.substring(5, 7)).toIntOrNull()
                       && d == (scheduledate.substring(8)).toIntOrNull()) {
                    // 同じ日付のものが見つかったら セルの中に表示するので
                    var scheduleTitle : String =  schedule.scheduletitle
                    //  タイトルに改行があったら取り除いて、カレンダーのCardViewに表示したいので
                    scheduleTitle =  scheduleTitle.replace("[\r\n]", " ");  // Kotlinに replaceAllメソッドはない 代わりにreplace

                    if (scheduleTitle.length > 7) {
                        scheduleTitle =  scheduleTitle.substring(0, 8);
                    }
                    schedules += schedule.starttime + "~ " + scheduleTitle + "\n"
                }
            }

            // データクラスのインスタンスを生成する
           item = CalendarCellItem(i.toLong(), dateText, todayText, viewGoneText, schedules)  // コンストラクタ
            if (item != null) {
                data.add(item)
            }
        }

        // 表示してる月よりも１つ前の月を表示するためのボタン
        _prevButton = view.findViewById<Button>(R.id.prevButton)
        _prevButton.setOnClickListener {
            // MainActivityで今、表示をしている月の情報を取得する MainActivityでは、初期の画面 今月のカレンダーを表示するので
            //  アクティビティを新たに生成し、
            //  新しいアクティビティにMainActivityの firstSaturdayDateの情報から、１ヶ月前にした情報を渡す
            // Date型の計算を行いたい場合には、Calendar型に一度変換し、計算を行います。
            val calendar = Calendar.getInstance()
            calendar.time = firstSaturdayDate
            calendar.add(Calendar.MONTH, -1) // -1 をして ひと月前に
            var date: Date? = Date()
            // これで1月前の最初の土曜日の日付が取得できている
            date = calendar.time

            //  Intent intent = new Intent(MainActivity.this, PreAndNextMonthCalendarActivity.class);
            val intent = Intent(parentActivity, MonthCalendarActivity::class.java)
            intent.putExtra("prevButtonDate", date) // 1月前の最初の土曜日の日付を送る Date型情報を渡します
            startActivity(intent) // Mainアクティビティは終わらせずそのまま メインはバックキーで戻れるのでメモリの解放はありません
        }


        // 次の月を表示するためのボタン
        _nextButton = view.findViewById<Button>(R.id.nextButton)
        _nextButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = firstSaturdayDate
            calendar.add(Calendar.MONTH, 1) // +1 してる ひと月先に
            var date: Date? = Date()
            // これで1月先の最初の土曜日の日付が取得できている
            date = calendar.time
            //  Intent intent = new Intent(MainActivity.this, PreAndNextMonthCalendarActivity.class);
            val intent = Intent(parentActivity, MonthCalendarActivity::class.java)
            intent.putExtra("nextButtonDate", date) // 1月先の最初の土曜日の日付を送ってる Date型情報を渡します
            startActivity(intent) // Mainアクティビティは終わらせずそのまま メインはバックキーで戻れるのでメモリの解放はありません
        }

        val rv: RecyclerView = view.findViewById(R.id.rv)

        rv.setHasFixedSize(true) // パフォーマンス向上

        // グリッド状にカードを配置する 7つづつ
        val manager = GridLayoutManager(this.activity, _SPAN_COUNT)
        rv.layoutManager = manager

        val adapter: RecyclerView.Adapter<*> = CalendarAdapter(data)
        val c = adapter.itemCount

        // 追加してみた
        adapter.notifyDataSetChanged() // そもそもnotifyDataSetChangedは、リスト全体を更新するためのメソッドAdapterの内容を更新

        rv.adapter = adapter

        // 最後に return viewをすること
        return view
    }


    /**
     * onActivityCreated() メソッドは非推奨になりました。 onViewStateRestored に書いてください
     * ここでViewの状態を復元する
     * onCreate   onCreateView   onViewCreated   非推奨のonActivityCreated   推奨のonViewStateRestored  の順で呼ばれる
     * @param savedInstanceState
     */
    override fun onViewStateRestored(@Nullable savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val parentActivity: Activity? = activity // このフラグメントの自分　が所属するアクティビティを取得する MainActivity

        // 自分が所属するアクティビティから、 id が　activityMainFrame　の　FrameLayoutを取得する
        val activityMainFrame = parentActivity!!.findViewById<View>(R.id.activityMainFrame)

        // この判定は CardViewに表示するテキストのサイズなどの切り替えを画面サイズによって設定する時に使う CalendarAdaoterクラスで使うために必要
        if (activityMainFrame == null) {  // nullならば、大画面ではないので
            // 画面判定フラグを通常画面(スマホサイズ)とする
            _isLayoutXLarge = false // falseだと 通常画面(スマホサイズ)
        }
    }


    fun is_isLayoutXLarge(): Boolean {
        return _isLayoutXLarge
    }


}