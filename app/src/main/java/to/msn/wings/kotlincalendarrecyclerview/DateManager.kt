package to.msn.wings.kotlincalendarrecyclerview

import java.util.*

class DateManager {  // クラス定義と一緒に定義されるコンストラクタ 引数なし
    /**
     * プロパティの説明.
     */
    var _calendar : Calendar


    /**
     * コンストラクタの説明.
     */
    constructor() {
        this._calendar = Calendar.getInstance()  // 現在を取得して 代入する
    }

    /**
     * 引数なしのインスタンスメソッド.
     * 引数なしだと、現在の月のカレンダーのものを取得する.
     * @return return 戻り値の説明.
     */
    fun getDays() : List<Date> {

        // 現在
        val nowDate : Date  = _calendar.time
        // 指定の日付の月のグリッドに表示するマスの合計
        val count = _calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) * 7;

        //今月のカレンダーに表示される前月分の日数を計算
        _calendar.set(Calendar.DATE, 1)  // 今月の1日をセットする
        // 1日の曜日を取得して、それから -1すれば 前の月の最後の日の曜日が取得できる 曜日は 0 が日曜日
        val dayOfWeek = _calendar.get(Calendar.DAY_OF_WEEK) - 1    // 1日の曜日を取得 してから -1 を引いてる  Int型
        // -dayOfWeek  - は 単項演算子で プラスとマイナスを逆転する 正の数と負の数を逆転  火曜日だったら 2 の Int型なので、 -2 をすると 日曜日の 0になる
        // カレンダーに載せる 最初の日曜日 の日付にする
        _calendar.add(Calendar.DATE, -dayOfWeek)  // ここで、 _calendar オブジェクトは 最初の日曜日の 日付になっている


        // でばっく用 確認のため
        val firstSunday = _calendar.getTime()



        // リストや配列のファクトリ関数である listOf や arrayOf の引数に何も指定しないと、空の配列やリストを作成することができます
        // 通常は MutableList の方を使えばいい
        val days = mutableListOf<Date>()

        for(i in 1..count) {  // 1 から countまでを含む
            // ループの一番最初では _calendar オブジェクトは 最初の日曜日の 日付になっている
            days.add(_calendar.getTime())   // リストに 追加していく
            // リストに追加したら、日にちを 1日加算する
            _calendar.add(Calendar.DATE, 1)
        }
        // ループ終了 カレンダーに記載する リストができてる
        // _calendarオブジェクトを 復元して 現在の日付に戻す
        _calendar.setTime(nowDate)
        return days
    }

    /**
     * 引数ありのメソッド.
     * オーバーロード（多重定義)したメソッド.
     * @args date 引数には、Date型オブジェクトが渡されます 指定の月の最初の土曜日の日付が入ってます.
     * @return return List<Date>  引数で渡された指定の日付の月の カレンダーの要素をリストにして返す
     *
     */
    fun getDays(date :Date) : List<Date> {
        // 引数の date には  指定の月の最初の土曜日の日付が入ってる カレンダーの最初の土曜日は 必ずその月になってるから
        _calendar.setTime(date);  // 指定の日付にする
        // 指定の日付の月のグリッドに表示するマスの合計
        val count = _calendar.getActualMaximum(Calendar.WEEK_OF_MONTH) * 7
        // さらに、1日の日付にする (計算するために)
        // 指定をした月のカレンダーに表示される前月分の日数を計算するため
        _calendar.set(Calendar.DATE, 1)  // 1日をセットする

        val dayOfWeek = _calendar.get(Calendar.DAY_OF_WEEK) - 1

        _calendar.add(Calendar.DATE, -dayOfWeek)

        val days = mutableListOf<Date>()

        for(i in 1..count) {  // 1 から countまでを含む
            // ループの一番最初では _calendar オブジェクトは 最初の日曜日の 日付になっている
            days.add(_calendar.getTime())   // リストに 追加していく
            // リストに追加したら、日にちを 1日加算する
            _calendar.add(Calendar.DATE, 1)
        }
        // ループ終了 カレンダーに記載する リストができてる
        // _calendarオブジェクトを 復元して 現在の日付に戻す
        this._calendar =  Calendar.getInstance()  // 現在を取得して 代入する
       //  _calendar.setTime(nowDate)
        return days

    }


}