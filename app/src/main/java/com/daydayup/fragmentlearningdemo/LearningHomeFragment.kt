package com.daydayup.fragmentlearningdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.daydayup.fragmentlearningdemo.contract.FragmentLearningHost
import com.daydayup.databinding.FragmentLearningHomeBinding

class LearningHomeFragment : Fragment() {
   private var _binding: FragmentLearningHomeBinding? = null
    private val binding get() = _binding!!

    private val host : FragmentLearningHost? get() = activity as? FragmentLearningHost

    private val sharedViewModel : FragmentLearningSharedViewModel by lazy {
        ViewModelProvider(requireActivity())[FragmentLearningSharedViewModel::class.java]
    }
    override fun onCreateView(
        inflater : LayoutInflater,
        container : ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        //true false 的意思是 是否立刻挂载到父容器 通常用false
        _binding = FragmentLearningHomeBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        bindObserver()
    }

    override fun onResume() {
        super.onResume()
        host?.updateTitleFromFragment("这里是 HomeFragment")
    }

    private fun bindView(){
        binding.btnOpenDetailByReplace.setOnClickListener {
            host?.openDetailByReplace(1000, "HomeFragment")
        }
        binding.btnOpenToolsByAdd.setOnClickListener {
            host?.openToolsByAddHide()
        }

        binding.btnIncreaseCounter.setOnClickListener {
            sharedViewModel.increaseCounter()
        }

        binding.btnShowTips.setOnClickListener {
            host?.showTipsDialog()
        }

        binding.btnSendActivityMessage.setOnClickListener {
            val draft = binding.etDraft.text?.toString().orEmpty().ifBlank { "来自 Home Fragment 的消息" }
            host?.sendMessageToActivity(draft)
        }

    }
    private fun bindObserver(){
        sharedViewModel.counter.observe(viewLifecycleOwner){
            binding.tvSharedCounter.text = "计数器：$it"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance() : LearningHomeFragment = LearningHomeFragment()
    }



}