package com.example.part2_chapter4

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.room.Database
import androidx.room.Room
import com.example.part2_chapter4.model.History
import org.w3c.dom.Text
import java.lang.NumberFormatException
import kotlin.math.exp

class MainActivity : AppCompatActivity() {
    val expressionTextView by lazy { findViewById<TextView>(R.id.expressionTextView) }
    val resultTextView by lazy { findViewById<TextView>(R.id.resultTextView) }

    private var isOpertor = false
    private var hasOpertor = false
    private val historyLayout by lazy {
        findViewById<View>(R.id.historyLayout)
    }
    private val historyLinearLayout by lazy {
        findViewById<LinearLayout>(R.id.historyLinearLayout)
    }
    lateinit var db:AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db=Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "historyDB"
        ).build()
    }

    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.button0 -> numberButtonClicked("0")
            R.id.button1 -> numberButtonClicked("1")
            R.id.button2 -> numberButtonClicked("2")
            R.id.button3 -> numberButtonClicked("3")
            R.id.button4 -> numberButtonClicked("4")
            R.id.button5 -> numberButtonClicked("5")
            R.id.button6 -> numberButtonClicked("6")
            R.id.button7 -> numberButtonClicked("7")
            R.id.button8 -> numberButtonClicked("8")
            R.id.button9 -> numberButtonClicked("9")
            R.id.buttonPlus -> operatorButtonClicked("+")
            R.id.buttonMinus -> operatorButtonClicked("-")
            R.id.buttonModulo -> operatorButtonClicked("%")
            R.id.buttonDivider -> operatorButtonClicked("/")
            R.id.buttonMulti -> operatorButtonClicked("*")


        }

    }

    private fun numberButtonClicked(number: String) {
        if (isOpertor) {
            expressionTextView.append(" ")
        }

        isOpertor = false

        val expressionText = expressionTextView.text.split(" ")
        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리까지만 입력가능합니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isNotEmpty() && number == "0") {
            Toast.makeText(this, "0은 앞자리에 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        //TODO resulst text뷰에 결과값 넣기
        expressionTextView.append(number)
        resultTextView.text = calculateExpression()

    }

    @SuppressLint("SetTextI18n")
    private fun operatorButtonClicked(operator: String) {
        Log.d("MainActivity", "operator 초반 boolea여부::${isOpertor}")
        Log.d("MainActivity", "operator 초반 1개여부::${hasOpertor}")

        if (expressionTextView.text.isEmpty()) {
            return
        }
        when {
            isOpertor -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator
            }
            hasOpertor -> {
                Toast.makeText(this, "연산자는 한번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                expressionTextView.append(" $operator")
                isOpertor = true
            }
        }
        //operator를 초록색으로 칠해주는 과정
        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //나중에 제외해보기


        hasOpertor = true
        Log.d("MainActivity", "operator boolea여부::${isOpertor}")
        Log.d("MainActivity", "operator 1개여부::${hasOpertor}")
    }


    fun resultButtonClicked(v: View) {
        val expressionTexts = expressionTextView.text.split(" ")
        if (expressionTextView.text.isEmpty() || expressionTexts.size == 1) {
            return
        }
        if (expressionTexts.size != 3 && hasOpertor) {
            Toast.makeText(this, "완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()

        //TODO result버튼을 눌렀을때 디비에 정보를 넣는 코드
        Thread(Runnable {
            db.historyDao().insertHistory(History(null,expressionText,resultText))
        }).start()

        resultTextView.text = ""
        expressionTextView.text = resultText
        isOpertor = false
        hasOpertor = false

    }


    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")
        if (hasOpertor.not() || expressionTexts.size != 3) {
            return " "
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return " "
        }
        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]

        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> " "
        }
    }

    fun clearButtonClicked(view: android.view.View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOpertor = false
        hasOpertor = false
    }

    private fun String.isNumber(): Boolean {
        return try {
            this.toBigInteger()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }


    @SuppressLint("InflateParams")
    fun historyButtonClicked(v: View) {
        historyLayout.isVisible=true
        historyLinearLayout.removeAllViews() // 지워보고 코드 알아보기
        //TODO 디비에서 기록 가져오기
        //TODO 뷰에서 기록 할당하기
        Thread(Runnable {
            db.historyDao().getAll().reversed().forEach{
                runOnUiThread{
                    val historyView = LayoutInflater.from(this).inflate(R.layout.history_row,null,false)
                    historyView.findViewById<TextView>(R.id.expressionTextView).text = it.expression
                    historyView.findViewById<TextView>(R.id.resultTextView).text = it.result
                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()

    }

    fun closeHistoryButtonClicked(v: View) {
        //history 레이아웃 gone으로 만들기
        historyLayout.isVisible=false
    }
    fun clearHistoryButtonClicked(v: View) {
        //TODO 디비에서 모든기록 삭제
        historyLinearLayout.removeAllViews()
        //TODO 뷰에서 모든기록 삭제
        Thread(Runnable {
            db.historyDao().delectAll()
        }).start()

    }
}