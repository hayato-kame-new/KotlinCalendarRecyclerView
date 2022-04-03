package to.msn.wings.kotlincalendarrecyclerview

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class DeleteDialogFragment : DialogFragment() {

    private lateinit var _helper : TimeScheduleDatabaseHelper
    private lateinit var _strId : String
    private lateinit var _scheduleTitle : String
    private lateinit var _strDate : String


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // フォームで削除ボタンを押した時に、Bundleでデータを引き渡してるので、取得する
        val args = requireArguments()

        // String型のデータを渡しているので
        _strId = args.getString("strId")!! // onCreateDialogメソッドで取得しておく

        _scheduleTitle = args.getString("scheduleTitle")!!
        _strDate = args.getString("strDate")!!

        val textViewStrId = TextView(activity) // 既存のダイアログに 追加するTextView インスタンスを生成する

        val textViewScheduleTitle = TextView(activity)

        textViewStrId.text = _strId // ここに動的に _idカラムの値をString型にしたものをセットして、非表示にする

        textViewScheduleTitle.text = "タイトル: $_scheduleTitle" // こっちが上に重なって乗ってid見えない

        textViewScheduleTitle.gravity = Gravity.CENTER
        // textViewStrId.setVisibility(View.VISIBLE);
        textViewStrId.visibility = View.GONE // textViewScheduleTitleが 上に乗ってるから見えないけど

//        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
//        builder.setTitle(R.string.dialogTitle)
//        builder.setMessage(R.string.dialogMsg)

        val dialog = activity?.let {
            AlertDialog.Builder(it).apply {
                setTitle(R.string.dialogTitle)
                setMessage(R.string.dialogMsg)
                setPositiveButton(R.string.dialogBtnOk,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                                    // ここで、削除の処理を実行するので データベースへ接続をします。
                                    val parentActivity: Activity? = activity
                                    _helper =
                                        TimeScheduleDatabaseHelper(parentActivity) // _helper は解放すること
                                    _helper.writableDatabase.use { db ->   // dbはきちんとクローズ自動でしてくれます
                                        val sqlDelete = "DELETE FROM timeschedule WHERE _id = ?"
                                        val stmt = db.compileStatement(sqlDelete)
                                        stmt.bindLong(1, _strId.toLong())
                                        stmt.executeUpdateDelete()
                                    }
                                    _helper.close() // クローズしておくこと
                                    Toast.makeText(activity, "削除しました", Toast.LENGTH_LONG).show()
                                    // 削除したら、このフラグメントが所属するアクティビティを終了させる
                                    // strDate と同じ　年月の月カレンダーへ遷移する
                                    // スケジュールを挿入した年月が、現在の年月なら MainActivityへ　それ以外の月ならMonthCalendarActivityへ遷移する "2022-03-19"
                                    var date: Date? = null
                                    try {
                                        date = SimpleDateFormat("yyyy/MM/dd").parse(_strDate)
                                    } catch (e: ParseException) {
                                        e.printStackTrace()
                                    }
                                    val year = _strDate.substring(0, 4).toInt()
                                    val month = _strDate.substring(5, 7).toInt()
                                    // 現在を取得して
                                    val localdateToday: LocalDate = LocalDate.now()
                                    var intent: Intent? = null
                                    if (year == localdateToday.getYear() && month == localdateToday.getMonthValue()) {
                                        // 現在と同じなので MainActivityへ遷移する
                                        intent = Intent(parentActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        // 指定の日付のカレンダーを表示するため MonthCalendarActivityへ遷移する
                                        intent = Intent(
                                            parentActivity,
                                            MonthCalendarActivity::class.java
                                        )
                                        intent.putExtra("specifyDate", date) //  Date型情報を渡します
                                        startActivity(intent)
                                    }

                                    // 最後に 自分自身が所属するアクティビティを終了させます
                                    parentActivity!!.finish()
                                }

                    })

                setNegativeButton(R.string.dialogBtnNg,
                    object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                           // 何もしない
                        }

                    })
                setView(textViewStrId)
                setView(textViewScheduleTitle)
            }.create()
        }
        return dialog ?: throw IllegalStateException("Activity is null.")
    }
}