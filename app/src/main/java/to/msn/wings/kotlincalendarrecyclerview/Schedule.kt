package to.msn.wings.kotlincalendarrecyclerview

/**
 * 何もしない、データを保持するためだけのクラス.
 * データクラス と呼ばれ、 data としてマークされています.
 * プライマリコンストラクタで宣言されたすべてのプロパティから、コンパイラは自動的に次のメンバを推論します.
 * プライマリコンストラクタは、少なくとも1つのパラメータを持っている必要があります
 * すべてのプライマリコンストラクタのパラメータは、 val または var としてマークする必要があります
 * データクラスは、 abstract, open, sealed または inner にすることはできません
 * データクラスは他のクラスを拡張しない場合があります（ただし、インターフェイスを実装することはできます）
 * equals() / hashCode() のペア
 * toString()の形式
 * 宣言した順番でプロパティに対応する componentN() 関数
 * copy() 関数
 * 例えばデータベースやファイルからデータを読み込んだ場合、
 * オブジェクト指向プログラミングでは、読み込んだデータをクラスに保存してプログラム中で使用することがあります。
 * そのような場合に便利なのがデータクラスです
 */
data class Schedule(
    val _id: Int,
    val scheduledate: String,
    val starttime: String,
    val endtime: String,
    val scheduletitle: String,
    val schedulememo: String
)
