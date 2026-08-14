package com.daydayup.fragmentlearningdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daydayup.databinding.FragmentLearningChildBinding
import androidx.lifecycle.ViewModelProvider

class LearningChildFragment : Fragment() {

    private var _binding: FragmentLearningChildBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: FragmentLearningSharedViewModel by lazy {
        ViewModelProvider(requireActivity())[FragmentLearningSharedViewModel::class.java]
    }
    companion object {
        fun newInstance() : LearningChildFragment = LearningChildFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearningChildBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       binding.btnChildIncreaseCounter.setOnClickListener {
           sharedViewModel.increaseCounter()
       }
        sharedViewModel.counter.observe(viewLifecycleOwner) {
            binding.tvChildTitle.text = "Child Fragment（共享计数器=$it）"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}