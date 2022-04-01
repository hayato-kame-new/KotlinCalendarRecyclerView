package to.msn.wings.kotlincalendarrecyclerview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button

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
     * コールバックメソッドは onCreate   onCreateView   onViewCreated   非推奨のonActivityCreated   推奨のonViewStateRestored  の順で呼ばれる
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        // 最後に return viewをすること
        return inflater.inflate(R.layout.fragment_current_month, container, false)
    }


}