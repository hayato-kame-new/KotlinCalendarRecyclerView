package to.msn.wings.kotlincalendarrecyclerview

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TimeScheduleDatabaseHelper(context: Context): SQLiteOpenHelper(context, DBNAME, null, VERSION){

    // クラス内のprivate定数を宣言するためにcompanion objectブロックとする。
    companion object {
        /**
         * データベースファイル名の定数フィールド。
         */
        private const val DBNAME = "timeschedule.sqlite"
        /**
         * バージョン情報の定数フィールド。
         */
        private const val VERSION = 1
    }



    override fun onCreate(p0: SQLiteDatabase?) {
        // テーブル作成用SQL文字列の作成。
        val sb = StringBuilder()
        sb.append("CREATE TABLE timeschedule (")
        sb.append("_id INTEGER PRIMARY KEY,")
        sb.append("scheduledate TEXT NOT NULL,")
        sb.append("starttime TEXT NOT NULL,")
        sb.append("endtime TEXT NOT NULL,")
        sb.append("scheduletitle TEXT NOT NULL,")
        sb.append("schedulememo TEXT")
        sb.append(");")
        val sql = sb.toString()

        // SQLの実行。 ?. セーフコール演算子
        p0?.execSQL(sql)
    }

    // データベースをバージョンアップした時、テーブルを再作成
    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.let {
            it.execSQL("DROP TABLE IF EXISTS timeschedule")
            onCreate(it)
        }
    }


}