package com.daydayup.fragment_learning_demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.daydayup.fragment_learning_demo.contract.FragmentLearningHost
import com.daydayup.databinding.FragmentLearningToolsBinding


class LearningToolsFragment : Fragment() {

    private var _binding : FragmentLearningToolsBinding? = null
    private val binding get() =_binding!!

    private val host : FragmentLearningHost? get() = activity as? FragmentLearningHost

    private val sharedViewModel : FragmentLearningSharedViewModel by lazy {
        ViewModelProvider(requireActivity())[FragmentLearningSharedViewModel::class.java]
    }

    companion object {
        @JvmStatic
        fun newInstance() : LearningToolsFragment = LearningToolsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       _binding = FragmentLearningToolsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPopFromTools.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnOpenDetailFromTools.setOnClickListener {
            host?.openDetailByReplace(2002, "从 Tools 打开的新详情")
        }

        binding.btnSendToolsMsg.setOnClickListener {
            host?.sendMessageToActivity("Tools Fragment 发来消息")
        }

        sharedViewModel.counter.observe(viewLifecycleOwner) { counter ->
            val name = sharedViewModel.latestName.value.orEmpty()
            binding.tvToolsShared.text = "共享数据：counter=$counter, name=$name"
        }

        sharedViewModel.latestName.observe(viewLifecycleOwner) { name ->
            val counter = sharedViewModel.counter.value ?: 0
            binding.tvToolsShared.text = "共享数据：counter=$counter, name=$name"
        }

    }

    override fun onResume() {
        super.onResume()
        host?.updateTitleFromFragment("Tools Fragment")
        binding.tvToolsInfo.text =
            "Tools Fragment：通过 add + hide 进入，当前返回栈数量=${parentFragmentManager.backStackEntryCount}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}