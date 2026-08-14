package com.daydayup.viewpager2demo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daydayup.R

class QuestionFragment : Fragment() {

    private var questionData: QuizData? = null
    private var selectedAnswer: String? = null
    private var isAnswered = false  // 是否已答题
    private var isCorrect = false    // 答案是否正确

    // 回调接口，用于通知Activity答题结果
    interface OnAnswerListener {
        fun onAnswerSelected(isCorrect: Boolean)
    }

    private var answerListener: OnAnswerListener? = null

    fun setAnswerListener(listener: OnAnswerListener) {
        answerListener = listener
    }


    companion object {
        private const val ARG_QUESTION = "question"

        fun newInstance(question: QuizData): QuestionFragment {
            val fragment = QuestionFragment()
            val args = Bundle()
            args.putSerializable(ARG_QUESTION, question)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionData = it.getSerializable(ARG_QUESTION) as? QuizData
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_option, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        questionData?.let { question ->
            // 更新Activity中的题目显示
            (activity as? RadioGroupActivity)?.updateQuestionDisplay(question.question)

            // 设置选项文本和点击事件
            setupOptions(view, question)
        }
    }

    private fun setupOptions(view: View, question: QuizData) {
        val correctAnswer = question.getOptionA()  // 正确答案通常是第一个选项

        view.findViewById<TextView>(R.id.itemA).apply {
            text = "A. ${question.getOptionA()}"
            setOnClickListener {
                if (!isAnswered) {
                    selectedAnswer = question.getOptionA()
                    checkAnswer(view, this, correctAnswer, question.getOptionA())
                }
            }
        }

        view.findViewById<TextView>(R.id.itemB).apply {
            text = "B. ${question.option_b}"
            setOnClickListener {
                if (!isAnswered) {
                    selectedAnswer = question.option_b
                    checkAnswer(view, this, correctAnswer, question.option_b)
                }
            }
        }

        view.findViewById<TextView>(R.id.itemC).apply {
            text = "C. ${question.option_c}"
            setOnClickListener {
                if (!isAnswered) {
                    selectedAnswer = question.option_c
                    checkAnswer(view, this, correctAnswer, question.option_c)
                }
            }
        }

        view.findViewById<TextView>(R.id.itemD).apply {
            text = "D. ${question.option_d}"
            setOnClickListener {
                if (!isAnswered) {
                    selectedAnswer = question.option_d
                    checkAnswer(view, this, correctAnswer, question.option_d)
                }
            }
        }
    }

    /**
     * 检查答案并显示结果
     */
    private fun checkAnswer(view: View, selectedView: TextView, correctAnswer: String, userAnswer: String) {
        isAnswered = true
        isCorrect = userAnswer == correctAnswer

        // 获取所有选项视图
        val optionA = view.findViewById<TextView>(R.id.itemA)
        val optionB = view.findViewById<TextView>(R.id.itemB)
        val optionC = view.findViewById<TextView>(R.id.itemC)
        val optionD = view.findViewById<TextView>(R.id.itemD)

        // 找到正确答案的视图
        val correctView = when (correctAnswer) {
            questionData?.getOptionA() -> optionA
            questionData?.option_b -> optionB
            questionData?.option_c -> optionC
            questionData?.option_d -> optionD
            else -> null
        }

        // 禁用所有选项的点击
        optionA.isClickable = false
        optionB.isClickable = false
        optionC.isClickable = false
        optionD.isClickable = false

        if (isCorrect) {
            // 答案正确：选中项显示绿色
            selectedView.setBackgroundResource(R.drawable.item_green)  // 绿色

        } else {
            // 答案错误：选中项显示红色，正确答案显示绿色
            selectedView.setBackgroundResource(R.drawable.item_red)  // 红色


            correctView?.let {
                it.setBackgroundResource(R.drawable.item_green) // 绿色

            }
        }

        // 通知Activity答题结果
        answerListener?.onAnswerSelected(isCorrect)

        // 也可以通过Activity更新
        (activity as? RadioGroupActivity)?.onAnswerResult(isCorrect)
    }

    /**
     * 重置答题状态（用于切换题目时）
     */
    private fun resetAnswerState(view: View) {
        isAnswered = false
        isCorrect = false
        selectedAnswer = null

        val views = listOf(
            view.findViewById<TextView>(R.id.itemA),
            view.findViewById<TextView>(R.id.itemB),
            view.findViewById<TextView>(R.id.itemC),
            view.findViewById<TextView>(R.id.itemD)
        )

        views.forEach { tv ->
            tv.background = resources.getDrawable(R.drawable.item_bg, null)
            tv.setTextColor(Color.WHITE)
            tv.isClickable = true
        }
    }

    fun getSelectedAnswer(): String? = selectedAnswer
    fun getQuestionData(): QuizData? = questionData

    // 更新题目内容（用于切换Tab时重新随机加载题目）
    fun updateQuestion(newQuestion: QuizData) {
        questionData = newQuestion
        selectedAnswer = null

        view?.let { view ->
            // 重置答题状态
            resetAnswerState(view)

            // 更新Activity中的题目显示
            (activity as? RadioGroupActivity)?.updateQuestionDisplay(newQuestion.question)

            // 重新设置选项
            setupOptions(view, newQuestion)
        }
    }

    /**
     * 获取答题状态
     */
    fun isAnswered(): Boolean = isAnswered

    /**
     * 获取答案是否正确
     */
    fun isAnswerCorrect(): Boolean = isCorrect
}