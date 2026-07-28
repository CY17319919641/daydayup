package com.daydayup.module_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.daydayup.R

class QuizPageFragment : Fragment() {

    companion object{
        private const val ARG_TITLE = "arg_title"

        fun newInstance(title: String): QuizPageFragment {
            val fragment = QuizPageFragment()
            val bundle = Bundle()
            bundle.putString(ARG_TITLE,title)
            fragment.arguments = bundle
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  view = inflater.inflate(R.layout.item_basic_page,container,false)
        val title = view.findViewById<TextView>(R.id.tv_page)
        title.text = arguments?.getString(ARG_TITLE)
        return view
    }
}