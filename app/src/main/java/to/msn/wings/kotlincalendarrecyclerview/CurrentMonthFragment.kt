package to.msn.wings.kotlincalendarrecyclerview


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
import java.text.SimpleDateFormat


class CurrentMonthFragment : Fragment() {

    private var _isLayoutXlarge : Boolean = true  // 初期値を trueにしておく

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

       // val parentActivity = this.activity

        // 注意 import android.R　　を書いてると エラーになるので注意
        val view = inflater.inflate(R.layout.fragment_current_month, container, false)  // View型

        _dateManager = DateManager()  // コンストラクタの呼び出し DateManager型
        // _dateManagerオブジェクトから インスタンスメソッドを呼び出す 引数なしの方を呼び出す
        val days =  _dateManager.getDays()  // List<Date>型

        val firstDate = days.get(0)  // Date型
        val lastDate = days.get(days.size -1 )   // Date型

        // 文字列にする  "yyyy-MM-dd" の形にすること SQLでバインドするため  期間を指定してSELECTする
        val fDayString = SimpleDateFormat("yyyy-MM-dd").format(firstDate)  // String型
        val lDayString = SimpleDateFormat("yyyy-MM-dd").format(lastDate)  // String型

        // リストや配列のファクトリ関数である listOf や arrayOf の引数に何も指定しないと、空の配列やリストを作成することができます
        // 通常は MutableList の方を使えばいい
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

     //   _titleText = view.findViewById<TextView>(R.id.titleText)
        // 最初の土曜日を取得する   最初の土曜日は、その月に必ずなってるから
        // 最初の土曜日を取得する   最初の土曜日は、その月に必ずなってるから
    //    val firstSaturdayDate: Date = dates.get(6)



        // 最後に return viewをすること
      //  return inflater.inflate(R.layout.fragment_current_month, container, false)
        return view
    }


}