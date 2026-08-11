package com.daydayup.fragment_learning_demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.daydayup.fragment_learning_demo.contract.FragmentLearningHost
import com.daydayup.databinding.FragmentLearningEditBinding
import androidx.lifecycle.ViewModelProvider
import com.daydayup.fragment_learning_demo.contract.FragmentLearningContract


class LearningEditFragment : Fragment() {



    companion object {

        private const val ARG_DEFAULT_NAME = "arg_default_name"
        fun newInstance(defaultName: String) =
            LearningEditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DEFAULT_NAME, defaultName)
                }
            }
    }

    private var _binding: FragmentLearningEditBinding? = null
    private val binding get() = _binding!!

    private val host: FragmentLearningHost?
        get() = activity as? FragmentLearningHost

    private val sharedViewModel: FragmentLearningSharedViewModel by lazy {
        ViewModelProvider(requireActivity())[FragmentLearningSharedViewModel::class.java]
    }

    private var defaultName : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultName = arguments?.getString(ARG_DEFAULT_NAME).orEmpty()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearningEditBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etNameInput.setText(defaultName)

        binding.btnConfirmEdit.setOnClickListener {
            //拿到编辑框文字 去掉首尾空格
            val input = binding.etNameInput.text.toString().orEmpty().trim()
            if(input.isBlank()){
                binding.etNameInput.error = "名称不能为空"
            }
            //这一段是使用官方推荐的 fragment 之间的通信方式
            parentFragmentManager.setFragmentResult(
                FragmentLearningContract.REQUEST_EDIT_NAME,
                bundleOf(FragmentLearningContract.REQUEST_EDIT_NAME to input)
            )
            sharedViewModel.updateName(input)
            parentFragmentManager.popBackStack()
        }

        binding.btnCancelEdit.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        host?.updateTitleFromFragment("Edit Fragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}