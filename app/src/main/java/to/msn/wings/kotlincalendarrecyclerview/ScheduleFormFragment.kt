package to.msn.wings.kotlincalendarrecyclerview

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class ScheduleFormFragment : Fragment() {

    // 大画面かどうかの判定フラグを 追加し、 onCreate()メソッドをオーバーライドして 大画面なのか調べて _isLayoutXLarge に代入する
    private var _isLayoutXlarge = true // trueにしておく

    private lateinit var _formTitle: TextView
    private lateinit var _textViewHourError: TextView
    private lateinit var _textViewMinutesError: TextView

    private lateinit var _returnMonButton: Button
    private lateinit var _currentMonButton: Button
    private lateinit var _spinnerStartHour: Spinner
    private lateinit var _spinnerStartMinutes: Spinner
    private lateinit var _spinnerEndHour: Spinner
    private lateinit var _spinnerEndMinutes: Spinner
    private lateinit var _editTextScheTitle: EditText
    private lateinit var _editTextScheMemo: EditText
    private lateinit var _saveButton: Button
    private lateinit var _deleteButton : Button //  import android.widget.Button;

    private lateinit var _calendarView: CalendarView

    // フラグ
    var _buttonFlagMap: Map<String, Boolean>? = null


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 注意　getFragmentManager() 非推奨になりました
        val manager: FragmentManager = parentFragmentManager
        // フラグメントマネージャーから、 所属しているアクティビティの上に乗ってるフラグメントが取得できる TimeScheduleFragmentを取得する
        // nullだったらキャストできません java.lang.NullPointerException なので TimeScheduleFragment? にして null許容型にしても、キャストは nullに対してできないのでだめ
//        val timeScheduleFragment: TimeScheduleFragment? =
//            manager.findFragmentById(R.id.timeScheduleFragment) as TimeScheduleFragment

        val fragmentObject: Fragment? =
            manager.findFragmentById(R.id.timeScheduleFragment)

        // ScheduleFormFragment自分自身と、　同じアクティビティ上に、 TimeScheduleFragment が乗っていたら 大画面   nullだったらスマホサイズ
        if (fragmentObject == null) {
            // 通常(スマホサイズ)画面
            _isLayoutXlarge = false
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // TimeSheduleActivity (TimeSheduleFragment)　から  所属するアクティビティのScheduleFormActivityへ画面遷移してくる
        // このフラグメントが  所属するアクティビティのScheduleFormActivity の取得
        val parentActivity: Activity? = activity
        val view = inflater.inflate(R.layout.fragment_schedule_form, container, false)


        //  大画面だった時の処理と、スマホサイズの処理を分岐する
        var extras: Bundle

        extras = if (_isLayoutXlarge) {  // 大画面だった時には、同じアクティビティ上で、フラグメント間でのデータの引き渡しをする
            requireArguments() // TimeScheduleフラグメントから自分自身のフラグメントへデータを受け取る
        } else {  // スマホサイズの時
            //  所属するアクティビティから インテントを取得する
            val intent = parentActivity!!.intent
            // インテントから、引き継ぎデータをまとめたものを取得
            intent.extras!!
        }

        // フラグMap  インスタンス生成
        _buttonFlagMap = HashMap()

        _saveButton = view.findViewById(R.id.saveButton)
        _deleteButton = view.findViewById(R.id.deleteButton)

        // 個々のデータを取得 うまく取得できなかった時のために String型は ""で初期化  Date型は nullで初期化
        val date: Array<Date?> = arrayOf(null)
        var action = ""
        var timeString : String? = "" // 新規の時には送られこない 編集の時だけ送られてくる null許容型にしないとだめ

        var scheduleTitleString : String? = ""  // 新規の時には送られこない 編集の時だけ送られてくる null許容型にしないとだめ
        var scheduleMemoString : String? = ""  // 新規の時には送られこない 編集の時だけ送られてくる null許容型にしないとだめ
        var intId: Int? = null

        if (extras != null) {
            date[0] = extras.getSerializable("date") as Date? // Date型へキャストが必要です
            action = extras.getString("action")!! // "add" もしくは "edit" が入ってきます

            // 編集の時にだけ、 時間とタイトルとメモの情報が intentに乗っています 新規の時には送られこないので null が入ってくる

            timeString = extras.getString("timeString") // !! はつけない  新規の時には送られこないので null が入ってくる 編集の時だけ送られてくる
            scheduleTitleString = extras.getString("scheduleTitleString") //  !! はつけない 新規の時は nullになる
            scheduleMemoString = extras.getString("scheduleMemoString") //  !! はつけない 新規の時は nullになる
            intId = extras.getInt("intId") // 新規の時は nullになる
        }

        if (extras != null) {
            date[0] = extras.getSerializable("date") as Date? // Date型へキャストが必要です
            action = extras.getString("action")!! // "add" もしくは "edit" が入ってきます

            // 編集の時にだけ、 時間とタイトルとメモの情報が intentに乗っています 新規の時には送られこないので null が入ってくる
            timeString = extras.getString("timeString") // !! はつけない  新規の時には送られこないので null が入ってくる 編集の時だけ送られてくる
            scheduleTitleString = extras.getString("scheduleTitleString") //  !! はつけない 新規の時は nullになる
            scheduleMemoString = extras.getString("scheduleMemoString") //  !! はつけない 新規の時は nullになる
            intId = extras.getInt("intId") // 新規の時は nullになる
        }

        // 後でインナークラスで dateを使うので定数にしておく final つける
        val DATE = date[0]!!
        val ACTION = action
        val ID = intId!!

        var startH = ""
        var startM = ""
        var endH = ""
        var endM = ""

        if (timeString != null) {
            // 編集の時  "[ 09:00 ~ 15:00 ]"  という形になっていますので 取り除きます
            // String result = s.replace("[", "").replace("]", "").replace(" ", "").replace("~", "").replace(":", "");
            // 注意  正規表現 角括弧は　バックスラッシュを2つ書くことで、対応します
            val replaced = timeString.replace("[\\[\\]~ :]".toRegex(), "") // "09001500"
            startH = replaced.substring(0, 2) // "09"
            startM = replaced.substring(2, 4) // "00"
            endH = replaced.substring(4, 6) // "15"
            endM = replaced.substring(6) // "00"
            if (startH.substring(0, 1).equals("0")) {  // "0"で始まるならば "0"をとる
                startH = startH.substring(1, 2) // 再代入
            }
            if (endH.substring(0, 1).equals("0")) { // "0"で始まるならば "0"をとる
                endH = endH.substring(1, 2) // 再代入
            }
        }
        _formTitle = view.findViewById(R.id.formTitle);
        _spinnerStartHour = view.findViewById(R.id.spinnerStartHour);
        _spinnerStartMinutes = view.findViewById(R.id.spinnerStartMinutes);
        _spinnerEndHour = view.findViewById(R.id.spinnerEndHour);
        _spinnerEndMinutes = view.findViewById(R.id.spinnerEndMinutes);
        _editTextScheTitle = view.findViewById(R.id.editTextScheTitle);
        _editTextScheMemo = view.findViewById(R.id.editTextScheMemo);


        // ACTION の値によって分岐できるようにする
        if (ACTION == "add") {  // 新規の時
            _formTitle.setText(R.string.tvFormTitleAdd) // 新規の時に　新規スケジュール登録画面　と表示する
            _saveButton.isEnabled = false // 新規なら最初は保存ボタン押せないようになってる  false
            _deleteButton.visibility = View.GONE // 削除ボタン見えない
            // 新規の時には カレンダービューだけは初期値が入っている
        } else {  // 編集の時
            _formTitle.setText(R.string.tvFormTitleEdit) // 編集の時に　編集-スケジュール登録画面　と表示する
            _saveButton.isEnabled = true // 編集なら最初は保存ボタン押せます
            _deleteButton.visibility = View.VISIBLE // 削除ボタン見える
            // 編集の時には、時間フォーム タイトル メモ カレンダービュー に初期値を入れておくので
            this@ScheduleFormFragment.setSelection(_spinnerStartHour, startH)
            setSelection(_spinnerStartMinutes, startM)
            setSelection(_spinnerEndHour, endH)
            setSelection(_spinnerEndMinutes, endM)

//            SpinnerAdapter adapter = _spinnerStartHour.getAdapter();
//        int index = 0;
//        for (int i = 0; i < adapter.getCount(); i++) {
//            if (adapter.getItem(i).equals(startH)) {
//                index = i;
//                break;
//            }
//        }
//            _spinnerStartHour.setSelection(index);
//            String item = (String) _spinnerStartHour.getItemAtPosition(index);
            if (scheduleTitleString != null) {
                _editTextScheTitle.setText(scheduleTitleString)
            }
            if (_editTextScheMemo != null) {
                _editTextScheMemo.setText(scheduleMemoString)
            }
        }

        // カレンダービューに初期値をセット
        _calendarView = view.findViewById(R.id.calendarView)

        // ここで 新規の時も 編集の時にも CalendarViewに  初期値として 送られてきた 日時を設定します。
        _calendarView.date = DATE.time // 引数には long型 カレンダービューに初期値設定



        // ボタンの大きさなどを設定     getColor(int id)を使いたいのだが　非推奨なので API 23 から Deprecated（非推奨）
        //  代わりに public int getColor (int id, Resources.Theme theme) を使います
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // API 23 以上 は 新しいメソッドを使います
            _deleteButton.setBackgroundColor(
                resources.getColor(
                    R.color.colorAccent,
                    requireActivity().theme
                )
            )
        } else {
            // API 23 未満 の時には　非推奨メソッドを使用します
            _deleteButton.setBackgroundColor(requireActivity().resources.getColor(R.color.colorAccent))
        }

        // ボタン を少し小さくするには xmlファイルで設定するには「wrap_content」になってるので一旦0にする
        _deleteButton.minimumWidth = 0 // ボタンの最小幅がデフォルトで64dipである  一旦0にする

        _deleteButton.width = 180


        // Date型の getYear getMonth getDay　は　非推奨メソッドなので、SimpleDateFormatを使い、文字列として取得する
        var sdf = SimpleDateFormat("yyyy年M月") // MM に　すると 01 02 03   M にすると 1  2  3

        val str: String = sdf.format(date[0]) // ボタンに表示するための

        _returnMonButton = view.findViewById(R.id.returnMonButton)
        _returnMonButton.text = str + "カレンダーに戻る"

        // 比較するために フォーマットし直して
        sdf = SimpleDateFormat("yyyy年MM月") // MM に　すると 01 02 03

        val strMM: String = sdf.format(date[0])
        val year = strMM.substring(0, 4).toInt()
        val month = strMM.substring(5, 7).toInt()

        // もし、今月ならば returnMonButtonを非表示にする
        // 現在を取得して
        val localdateToday: LocalDate = LocalDate.now()
        if (year == localdateToday.getYear() && month == localdateToday.getMonthValue()) {
            _returnMonButton.visibility = View.GONE // これで表示しない なおかつ 非表示にしたスペースを詰める
        }

        _currentMonButton = view.findViewById(R.id.currentMonButton)

        _textViewHourError = view.findViewById(R.id.textViewHourError)
        _textViewMinutesError = view.findViewById(R.id.textViewMinutesError)


        // スピナーの 　動的にリストを作るやりかた
//        List<String> endMList = new ArrayList<>();
//        endMList.add("00");
//        endMList.add("30");
//        ArrayAdapter<String> adapterEM = new ArrayAdapter<>(parentActivity, android.R.layout.simple_spinner_item, endMList);
//        adapterEM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        _spinnerEndMinutes.setAdapter(adapterEM);

        //  SQLite のテーブルのスキーマでは文字列の最大の長さを指定することはできない
        //  タイトルは android:maxLength="30"   メモは android:maxLength="80"  など xmlで入力文字数を制限することができる
        // タイトルは 新規ではの状態では　　""になってる  編集では必ず入ってる  (20文字以内で)
        if (_editTextScheTitle.getText().toString().isEmpty() || _editTextScheTitle.getText().toString().equals("")) {  // 新規の時最初は　""　空文字が入ってくる
            _editTextScheTitle.setError("スケジュールのタイトルに文字を入力してください");
            // 保存ボタン押せない 新規登録画面では　最初は押せないようになってる ""になってるから
            (_buttonFlagMap as HashMap<String, Boolean>).put("scheTitle", false );
        } else {  // 編集
            (_buttonFlagMap as HashMap<String, Boolean>).put("scheTitle", true );  // 編集では、最初は押せるようになってる
        }

        // 遷移してくる前に表示していた　カレンダーの年と月に戻るために、
        _returnMonButton.setOnClickListener { // インナークラスなので 定数 DATEを使う
            val intent = Intent(parentActivity, MonthCalendarActivity::class.java)
            // 指定した年と月のカレンダーを表示するために Date型情報を渡します
            intent.putExtra("specifyDate", DATE) //  Date型情報を渡します
            startActivity(intent)
            // 最後に 自分自身が所属するアクティビティを終了させます
            val parentActivity: Activity? = activity
            parentActivity!!.finish()
        }

        //  現在(今月)のカレンダーの表示へ遷移する MainActivityに戻る  自分自身が所属するアクティビティを終了させます
        _currentMonButton.setOnClickListener {
            val intent = Intent(parentActivity, MainActivity::class.java)
            startActivity(intent)
            // 自分自身が所属するアクティビティを終了させます
            val parentActivity: Activity? = activity
            parentActivity!!.finish()
        }

        // スケジュールタイトルのEditTextにイベントリスナーをつける  何か入力をしたら、保存ボタンが押せるフラグMapに 値をtrueで登録する
        _editTextScheTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                val word = editable.toString()
                if (word != "") {
                    (_buttonFlagMap as HashMap<String, Boolean>).put("scheTitle", true) // ""空文字じゃなかったら 保存ボタンが押せる
                }
                // もし、保存ボタンが押せるか押せないか切り替えるメソッドを呼び出す  Mapの値が全て trueならばボタンを押せるようになってる
                changeSaveButton(_buttonFlagMap)
            }
        })

        val setL =
            _calendarView.date // 上で  _calendarView.setDate(DATE.getTime());　CalendarViewに日時を設定してるので


        val setDaySql = java.sql.Date(setL)
        //  内部クラスで使うから final にしておく 初期値は、遷移してきた時に選択してあった日付にしておくので
        val sqlDateArray = arrayOf(setDaySql) // 配列の中身なら書き換え可能だから 配列にする

        // new 以降は　無名クラス 匿名クラスなので　　その中で使うなら　定数にするのでDATEを使う
        _calendarView.setOnDateChangeListener { calendarView, year, month, dayOfMonth ->
            //  CalendarViewで日にちが選択された時に呼び出されるリスナークラス
            // 注意  引数の　monthは　The month that was set [0-11]
            // データベースへ新規登録するためにデータを取得します
            val c = Calendar.getInstance() // 現在
            c[Calendar.YEAR] = year
            c[Calendar.MONTH] = month // 月は0始まりだが、引数の monthも0始まりなので同じにして大丈夫です
            c[Calendar.DAY_OF_MONTH] = dayOfMonth
            val utilDate = c.time
            val sqlDate = java.sql.Date(utilDate.time) // 本当は変換必要なかった java.util.Date　のままで良かったです
            sqlDateArray[0] = sqlDate
        }

        // データベースへ登録するための フィールド 内部クラスで使うから final にしておく
        // 開始時間を表す文字列の定数　インナークラスで使うから final で定数化しておく必要がある。また、配列にすると、要素を書き換えるようにできる
        val _START_HOUR_STR_ARRAY = arrayOf("")

        // 終了時間を表す文字列の定数　インナークラスで使うから final で定数化しておく必要がある。また、配列にすると、要素を書き換えるようにできる
        val _END_HOUR_STR_ARRAY = arrayOf("")


        // 開始の分を表す文字列の定数　インナークラスで使うから final で定数化しておく必要がある。また、配列にすると、要素を書き換えるようにできる
        val _START_MINUTES_STR_ARRAY = arrayOf("")

        // 終了時間を表す文字列の定数　インナークラスで使うから final で定数化しておく必要がある。また、配列にすると、要素を書き換えるようにできる
        val _END_MINUTES_STR_ARRAY = arrayOf("")


        // 開始  時間にリスナーつける
        _spinnerStartHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                val getPositionStr =
                    adapterView.getItemAtPosition(i) as String // 新規の時 最初は一番上 "0" が選択された状態になってる
                if (_END_HOUR_STR_ARRAY[0].equals("")) {   // END_HOUR_STR_ARRAY[0] が""空文字じゃなければ　　Integer.parseIntします
                    // 何もしない
                } else if (getPositionStr.toInt() > _END_HOUR_STR_ARRAY[0].toInt()) {
                    _textViewHourError.error = "開始時間と終了時間を確認してください"
                    _textViewHourError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startHour", false)
                    // ここを追加しました！！！
                } else if (getPositionStr.toInt() == _END_HOUR_STR_ARRAY[0].toInt() && _START_MINUTES_STR_ARRAY[0].toInt() > _END_MINUTES_STR_ARRAY[0].toInt()) {
                    _textViewHourError.error = "開始時間と終了時間を確認してください"
                    _textViewHourError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startHour", false)
                } else {
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startHour", true)
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endHour", true) // 押せる
                    _textViewHourError.error = null
                    _textViewHourError.text = ""
                    // 追加
                    _textViewMinutesError.error = null
                    _textViewMinutesError.text = ""
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endMinutes", true) // 押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startMinutes", true) // 押せる
                }
                _START_HOUR_STR_ARRAY[0] =
                    adapterView.getItemAtPosition(i) as String // 選択されたアイテムを　親のアダプタービューから ポジションを指定して取得する

                // ここでボタン変更するメソッドをよぶ
                changeSaveButton(_buttonFlagMap)
            }

            /**
             * onNothingSelectedメソッドは既に選択された項目をクリックした時に呼び出され、
             * その後onItemSelectedメソッドが呼び出されます。
             * @param adapterView
             */
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        // 終了時間にリスナーつける
        _spinnerEndHour.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                val getPositionStr =
                    adapterView.getItemAtPosition(i) as String // 新規の時 最初は一番上 "0" が選択された状態になってる

                // 入力チェック
                if (_START_HOUR_STR_ARRAY[0].equals("") || _START_MINUTES_STR_ARRAY[0].equals("") || _END_MINUTES_STR_ARRAY[0].equals(
                        ""
                    )
                ) {
                    // 何もしない
                } else if (getPositionStr.toInt () < _START_HOUR_STR_ARRAY[0].toInt()) {
                    _textViewHourError.error = "開始時間と終了時間を確認してください"
                    _textViewHourError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endHour", false)
                } else if (getPositionStr.toInt () == _START_HOUR_STR_ARRAY[0].toInt() && _START_MINUTES_STR_ARRAY[0].toInt() > _END_MINUTES_STR_ARRAY[0].toInt()) {
                    _textViewHourError.error = "開始時間と終了時間を確認してください"
                    _textViewHourError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endHour", false)
                } else {
                    _textViewHourError.error = null
                    _textViewHourError.text = ""
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endHour", true) // 押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startHour", true) // 押せる
                    _textViewMinutesError.error = null
                    _textViewMinutesError.text = ""
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endMinutes", true) // 押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startMinutes", true) // 押せる
                }
                _END_HOUR_STR_ARRAY[0] =
                    adapterView.getItemAtPosition(i) as String // 選択されたアイテムを　親のアダプタービューから ポジションを指定して取得する
                // ここでボタン変更するメソッドをよぶ
                changeSaveButton(_buttonFlagMap)
            }

            /**
             * onNothingSelectedメソッドは既に選択された項目をクリックした時に呼び出され、
             * その後onItemSelectedメソッドが呼び出されます。
             * @param adapterView
             */
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        // 開始の　分にリスナーつける
        _spinnerStartMinutes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                val getPositionStr =
                    adapterView.getItemAtPosition(i) as String // 新規の時 最初は一番上 "00" が選択された状態になってる

                _START_MINUTES_STR_ARRAY[0] =
                    adapterView.getItemAtPosition(i) as String // 選択されたアイテムを　親のアダプタービューから ポジションを指定して取得する
                if (_END_MINUTES_STR_ARRAY[0].equals("") || _END_HOUR_STR_ARRAY[0].equals("") || _START_HOUR_STR_ARRAY[0].equals(
                        ""
                    )
                ) {   // _END_MINUTES_STR_ARRAY[0] が""空文字じゃなければ　　Integer.parseIntします
                    //   何もしない
                } else if (_END_HOUR_STR_ARRAY[0].toInt() == _START_HOUR_STR_ARRAY[0].toInt() && getPositionStr.toInt () > _END_MINUTES_STR_ARRAY[0].toInt()
                ) {
                    _textViewMinutesError.error = "開始時間と終了時間を確認してください"
                    _textViewMinutesError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startMinutes", false)
                } else {
                    _textViewMinutesError.error = null
                    _textViewMinutesError.text = ""
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startMinutes", true) //押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endMinutes", true) //押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startHour", true) // 押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endHour", true) // 押せる
                    _textViewHourError.error = null
                    _textViewHourError.text = ""
                }
                // ここでボタン変更するメソッドをよぶ
                changeSaveButton(_buttonFlagMap)
            }

            /**
             * onNothingSelectedメソッドは既に選択された項目をクリックした時に呼び出され、
             * その後onItemSelectedメソッドが呼び出されます。
             * @param adapterView
             */
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        // 終了の分にリスナーをつける
        _spinnerEndMinutes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {

                val getPositionStr =
                    adapterView.getItemAtPosition(i) as String // 新規の時 最初は一番上 "00" が選択された状態になってる

                // データベースに登録するため 情報を取得する
                _END_MINUTES_STR_ARRAY[0] =
                    adapterView.getItemAtPosition(i) as String // 選択されたアイテムを　親のアダプタービューから ポジションを指定して取得する
                if (_START_MINUTES_STR_ARRAY[0].equals("") || _END_HOUR_STR_ARRAY[0].equals("") || _START_HOUR_STR_ARRAY[0].equals(
                        ""
                    )
                ) {
                    //   何もしない
                } else if (_END_HOUR_STR_ARRAY[0].toInt() < _START_HOUR_STR_ARRAY[0].toInt()) {  // ここ追加した
                    _textViewMinutesError.error = "開始時間と終了時間を確認してください"
                    _textViewMinutesError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endMinutes", false)
                } else if (_END_HOUR_STR_ARRAY[0].toInt() == _START_HOUR_STR_ARRAY[0].toInt() && getPositionStr.toInt () < _START_MINUTES_STR_ARRAY[0].toInt()
                ) {
                    _textViewMinutesError.error = "開始時間と終了時間を確認してください"
                    _textViewMinutesError.text = "開始時間と終了時間を確認してください"
                    // 押せない
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endMinutes", false)
                } else {
                    _textViewMinutesError.error = null
                    _textViewMinutesError.text = ""
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endMinutes", true) // 押せる
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startMinutes", true) // 押せる
                    // 追加
                    (_buttonFlagMap as HashMap<String, Boolean>).put("startHour", true)
                    (_buttonFlagMap as HashMap<String, Boolean>).put("endHour", true) // 押せる
                    _textViewHourError.error = null
                    _textViewHourError.text = ""
                }
                // ここでボタン変更するメソッドをよぶ
                changeSaveButton(_buttonFlagMap)
            }

            /**
             * onNothingSelectedメソッドは既に選択された項目をクリックした時に呼び出され、
             * その後onItemSelectedメソッドが呼び出されます。
             * @param adapterView
             */
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        // 保存ボタンを押した時にリスナーをつける 新規作成と 編集の時 条件分岐する
        _saveButton.setOnClickListener { // _id　は　主キーで もし主キーを連番のIDにしたい場合、INTEGERで「PRIMARY KEY」を指定するようにします
            // そうすると　autoincrement をつけなくても自動採番する insetの時に _idを書かない 自動採番するので
            // SQLite3では、主キー　_idカラムを　INTEGERで「PRIMARY KEY」を指定すれば「AUTO INCREMENT」を指定する必要はありません。つけない方がいいらしい
            val date: Date = sqlDateArray[0] // "2022-03-19"
            val strDate = SimpleDateFormat("yyyy-MM-dd").format(date) // String型 にしてデータベースへ登録する
            val sh = _START_HOUR_STR_ARRAY[0] // "0" や　"1"　
            // "0" "1" "2" を　"00" "01" "02" へ成形してる
            val paddingStr = sh.format("%2s", _START_HOUR_STR_ARRAY[0]).replace(" ", "0")
            val sm = _START_MINUTES_STR_ARRAY[0] // "00" または　"30"　
            val eh = _END_HOUR_STR_ARRAY[0] //  "0" や　"1"
            val paddingStr2 = eh.format("%2s", _END_HOUR_STR_ARRAY[0]).replace(" ", "0")
            val em = _END_MINUTES_STR_ARRAY[0] // "00" または　"30"
            val insertST = "$paddingStr:$sm"
            val insertET = "$paddingStr2:$em"
            val etTitle = _editTextScheTitle.text.toString()
            val etMemo = _editTextScheMemo.text.toString() // 何も書いてないと ""空文字になってる
            val helper = TimeScheduleDatabaseHelper(parentActivity)
            helper.writableDatabase.use { db ->   // SQLiteDatabase db は try-catch-resources構文なので 自動でクローズをおこなってくれる
                // もし、action が "add" なら INSERT  "edit"なら UPDATE します
                if (ACTION == "add") {  // 新規作成なら
                    val sqlInsert =
                        "INSERT INTO timeschedule (scheduledate, starttime, endtime, scheduletitle, schedulememo) VALUES (?,?,?,?,?)"
                    val stmt = db.compileStatement(sqlInsert)
                    stmt.bindString(4, etTitle)
                    stmt.bindString(5, etMemo)
                    stmt.bindString(1, strDate)
                    stmt.bindString(2, insertST)
                    stmt.bindString(3, insertET)
                    stmt.executeInsert()
                } else { // 編集なら
                    // 編集では 主キーが必要　final 定数の ID　使う  ""では途中で改行しないように書く
                    val sqlUpdate =
                        "UPDATE timeschedule SET scheduledate = ?, starttime = ?, endtime = ?, scheduletitle = ?, schedulememo = ?  WHERE _id = ?"
                    val stmt = db.compileStatement(sqlUpdate)
                    stmt.bindString(4, etTitle)
                    stmt.bindString(5, etMemo)
                    stmt.bindString(1, strDate)
                    stmt.bindString(2, insertST)
                    stmt.bindString(3, insertET)
                    stmt.bindLong(6, ID.toLong()) // 主キーを指定する
                    stmt.executeUpdateDelete()
                }
            }
            helper.close() // ヘルパーを解放する
            if (ACTION == "add") {  // 新規作成なら
                Toast.makeText(activity, "スケジュールを新規登録しました", Toast.LENGTH_LONG).show()
            } else if (ACTION == "edit") {
                Toast.makeText(activity, "スケジュールを編集しました", Toast.LENGTH_LONG).show()
            }
            // 保存したスケジュールの年月が、現在の年月なら MainActivityへ　それ以外の月ならMonthCalendarActivityへ遷移する "2022-03-19"
            val year = strDate.substring(0, 4).toInt()
            val month = strDate.substring(5, 7).toInt()
            // 現在を取得して 比較する
            val localdateToday = LocalDate.now()
            var intent: Intent? = null
            if (year == localdateToday.year && month == localdateToday.monthValue) {
                // 保存したスケジュールの年月が 現在と同じだったら、MainActivityへ遷移する
                intent = Intent(parentActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                // 保存したスケジュールの年月が 現在と同じではない、MonthCalendarActivityへ遷移する
                intent = Intent(parentActivity, MonthCalendarActivity::class.java)
                intent.putExtra("specifyDate", DATE) //  Date型情報を渡します
                startActivity(intent)
            }
            val parentActivity: Activity? = activity
            // 最後に 自分自身が所属するアクティビティを終了させます
            parentActivity!!.finish()
        }


        // 削除ボタンにリスナーつける  ダイアログフラグメント DeleteConfirmDialogFragment を表示する
        _deleteButton.setOnClickListener { // 定数 DATE を使う  "yyyy/MM/dd"
            val strDate = SimpleDateFormat("yyyy/MM/dd").format(DATE)

            // ダイアログを表示させます DialogFragmentを継承したダイアログフラグメントクラスを作ったので newして インスタンスを生成
            val dialogFragment = DeleteDialogFragment()
            val args = Bundle()
            args.putString("strDate", strDate) // 日付もつける
            // 　final 定数の ID　使う
            args.putString("strId", ID.toString())
            args.putString("scheduleTitle", _editTextScheTitle.text.toString())
            dialogFragment.setArguments(args)
            val parentActivity: Activity? = activity
            //   削除ボタン押すと  ダイアログフラグメント DeleteConfirmDialogFragment を表示
            // Activityクラスではダメです FragmentActivityクラスにキャストをしてください。
            val fragmentActivity = parentActivity as FragmentActivity?
            // 第二引数は任意だから、クラス名にしておいた ダイアログを識別するための 名前を付けている
            dialogFragment.show(
                fragmentActivity!!.supportFragmentManager,
                "DeleteConfirmDialogFragment"
            )
        }

        // 最後ビューをreturnする
        return view;
    }

    /**
     * 同じものを探して、スピナーに選択済みにする.
     * メソッドメンバ staticな静的メソッド(クラスメソッド).
     */
    fun setSelection(spinner: Spinner, item: String) {
        val adapter = spinner.adapter
        var index = 0
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == item) {
                index = i
                break
            }
        }
        spinner.setSelection(index)
    }

    /**
     * メソッドメンバ  エラーがある限り、保存ボタンは押せないようにしてる.
     * フラグのMapにキーと値を代入もしくは 上書きをする
     * @param map Map<String, Boolean> キー: 入力の項目 <br /> 値: true: エラーなし false: エラーあり
     * 戻り値なし.
     */
    fun changeSaveButton(map: Map<String, Boolean>?) {
        if (map != null) {
            for (`val` in map.values) {
                if (`val` == false) {
                    _saveButton.isEnabled = false
                    // falseが見つかった時点で ボタンを付加にして
                    return  // 即終了その後のループは実行しないで 呼び出し元へ戻る
                } else {
                    // 全部 trueだったら
                    _saveButton.isEnabled = true
                }
            }
        }
    }


}