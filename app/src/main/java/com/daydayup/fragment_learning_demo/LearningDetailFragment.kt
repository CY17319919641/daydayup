package com.daydayup.fragment_learning_demo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.daydayup.R
import com.daydayup.fragment_learning_demo.contract.FragmentLearningHost
import com.daydayup.databinding.FragmentLearningDetailBinding

class LearningDetailFragment : Fragment() {

    private var _binding : FragmentLearningDetailBinding? = null
    private val binding get() = _binding!!

    private val host : FragmentLearningHost? get() =
        activity as? FragmentLearningHost

    private val  sharedViewModel : FragmentLearningSharedViewModel by lazy{
        ViewModelProvider(requireActivity())[FragmentLearningSharedViewModel::class.java]
    }

    private var itemId : Int = -1
    private var itemName : String = ""


    //伴生对象 接近于 static
    companion object{

        private const val ARG_ITEM_ID = "arg_item_id"
        private const val ARG_ITEM_NAME = "arg_item_name"
        private const val TAG_CHILD = "tag_child"

        fun newInstance(itenId : Int,itemName : String) : LearningDetailFragment{
           return LearningDetailFragment().apply {
               arguments = Bundle().apply {
                   putInt(ARG_ITEM_ID,itenId)
                   putString(ARG_ITEM_NAME,itemName)
               }
           }
        }

    }

    override fun onCreate(savedInstate : Bundle?){
        super.onCreate(savedInstate)
        itemId = arguments?.getInt(ARG_ITEM_ID) ?: -1
        itemName = arguments?.getString(ARG_ITEM_NAME).orEmpty()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDetailTitle.text = "Detail Fragment（id=$itemId）"
        binding.tvDetailDesc.text = "参数 itemName=$itemName"

        binding.btnOpenEdit.setOnClickListener {
            host?.openEditName(itemName)
        }
        binding.btnUpdateTitleFromDetail.setOnClickListener {
            host?.updateTitleFromFragment("Detail #$itemId")
        }
        binding.btnSendDetailMsg.setOnClickListener {
            host?.sendMessageToActivity("Detail Fragment 发送消息 itrmId = $itemId ")
        }
        binding.btnBackFromDetail.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnToggleChild.setOnClickListener {
            toggleChildFragment()
        }
        sharedViewModel.latestName.observe(viewLifecycleOwner) {
            binding.tvSharedNameInDetail.text = "共享姓名：$it"
        }
        sharedViewModel.counter.observe(viewLifecycleOwner) {
            binding.tvSharedCounterInDetail.text = "共享计数器：$it"
        }

    }
    override fun onResume() {
        super.onResume()
        host?.updateTitleFromFragment("Detail Fragment")
    }

    //切换子fragment
    private fun toggleChildFragment(){
        val existed = childFragmentManager.findFragmentByTag(TAG_CHILD)
        val tx = childFragmentManager.beginTransaction().setReorderingAllowed(true)
        if(existed == null){
            tx.replace(R.id.childContainer, LearningChildFragment.newInstance(),TAG_CHILD).commit()
        }else{
            tx.remove(existed).commit()
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding = null

    }


}