package to.msn.wings.kotlincalendarrecyclerview


import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class MonthCalendarFragment : Fragment() {

    private var _isLayoutXlarge : Boolean = true  // 初期値を trueにしておく

    // 変数を lateinit で宣言することにより、初期化タイミングを onCreate() 呼び出しまで遅延させています。
    // lateinit 変数は var で宣言しないといけないことに注意してください
    private lateinit var _helper : TimeScheduleDatabaseHelper

    private lateinit var _titleText : TextView

    private lateinit var _prevButton : Button

    private lateinit var _nextButton : Button

    private lateinit var _currentMonthButton : Button

    private lateinit var _dateManager : DateManager
    // 読み取り専用 val
    private val _SPAN_COUNT : Int = 7

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_month_calendar, container, false)

      //  val context : Context? = this.context
        val intent: Intent? = this.activity?.intent
        var prevButtonDate: Date? = null
        var nextButtonDate: Date? = null
        var specifyDate: Date? = null
        val extras = intent?.extras

        if (extras != null) {
            //  null かどうかのチェックが必要です どっちのボタンから遷移してきたのか
            prevButtonDate = intent.getSerializableExtra("prevButtonDate") as Date? // nullが入ってる可能性
            //  null かどうかのチェックが必要です どっちのボタンから遷移してきたのか
            nextButtonDate = intent.getSerializableExtra("nextButtonDate") as Date? // nullが入ってる可能性
            // null かどうかのチェックが必要です
            specifyDate = intent.getSerializableExtra("specifyDate") as Date? // nullが入ってる可能性
        }
        _dateManager = DateManager()

        var dates: List<Date>? = null

        _titleText = view.findViewById(R.id.titleText)
        var format = SimpleDateFormat("yyyy年 MM月")

        // ここで条件分岐します
        // ここで条件分岐します
        var title: String? = ""
        if (prevButtonDate != null) {  //  null かどうかのチェックが必要
            title = format.format(prevButtonDate)
            dates = _dateManager.getDays(prevButtonDate) // 引数ありのgetDays(Date date)　を呼び出す
        } else if (nextButtonDate != null) {  //  null かどうかのチェックが必要
            title = format.format(nextButtonDate)
            dates = _dateManager.getDays(nextButtonDate) // 引数ありのgetDays(Date date)　を呼び出す
            //  null かどうかのチェックが必要
        } else if (specifyDate != null) {  // 指定の日付のカレンダーを表示するならば
            title = format.format(specifyDate)
            dates = _dateManager.getDays(specifyDate) // 引数ありのgetDays(Date date)　を呼び出す
        }
        _titleText.text = title


        val firstDate = dates?.get(0)  // Date型
        val lastDate = dates?.get(dates.size -1 )   // Date型
        // SQLのバインドに使う 期間を指定してSELECTするため
        // SQLのバインドに使う 期間を指定してSELECTするため
        val firstDatestr = SimpleDateFormat("yyyy-MM-dd").format(firstDate)
        val lastDatestr = SimpleDateFormat("yyyy-MM-dd").format(lastDate)

        // 月のカレンダー(１週目に表示した前の月や　最後の週に表示してある後ろの月　の分も含む)に表示するリスト

        // 月のカレンダー(１週目に表示した前の月や　最後の週に表示してある後ろの月　の分も含む)に表示するリスト
       //  val list: List<Schedule> = ArrayList()
        val list = mutableListOf<Schedule>()
        _helper = TimeScheduleDatabaseHelper(this.activity) // _helper.close();をすること
        //  データベースを取得する
        var db: SQLiteDatabase? = null

        try {
            db = _helper.getWritableDatabase()
            val sqlSelect =
                "SELECT * FROM timeschedule WHERE scheduledate >= ? AND scheduledate <= ? ORDER BY starttime ASC"
            val params = arrayOf<String>(firstDatestr, lastDatestr)

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

        // 表示用のフォーマットし直し
        format = SimpleDateFormat("d") // "dd" だと　　01  02 となってしまう

        // もう一つ必要  ループの中で使うためここでインスタンスを生成しておく  表示はしないけど必要
        val sdFormat = SimpleDateFormat("yyyy/MM/dd") //  2017/03/02

        // 比較をするために本日現在を取得する
        val calendar = Calendar.getInstance()
        val todayMonth = calendar[Calendar.MONTH] + 1 // 現在の月 Calendar.MONTHは 0　から始まるので注意

        val todayDay = calendar[Calendar.DATE] // 現在の日


        /**
         * 表示だけのテキストのリスト
         */
        val data = mutableListOf<CalendarCellItem>()  // CalendarCellItem は　データクラスです
        var item : CalendarCellItem? = null


        for ( i in 0.. dates?.size!! - 1) {  // 0 からにする リストの添字だから
            var date : Date = dates?.get(i)
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

    fun is_isLayoutXLarge(): Boolean {
        return _isLayoutXlarge
    }

}