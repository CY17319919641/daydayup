package com.daydayup.fragmentlearningdemo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.daydayup.R
import com.daydayup.fragmentlearningdemo.contract.FragmentLearningContract
import com.daydayup.fragmentlearningdemo.contract.FragmentLearningHost
import com.daydayup.databinding.ActivityFragmentLearningBinding

class FragmentLearningActivity : AppCompatActivity() , FragmentLearningHost {


    private lateinit var binding : ActivityFragmentLearningBinding

    private val sharedViewModel : FragmentLearningSharedViewModel by lazy {
        ViewModelProvider(this)[FragmentLearningSharedViewModel::class.java]
    }

    companion object {
        private const val TAG_HOME = "home"
        private const val TAG_EDIT = "edit"
        private const val TAG_TIPS_DiALOG = "tips_dialog"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragmentLearningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindActivityViews()
        bindGlobalObservers()

        if(savedInstanceState == null){
            openRoot()
        }else{
            updateBackStackInfo()
        }
    }

    private fun bindActivityViews()  {
        binding.btnLoadRoot.setOnClickListener { openRoot() }
        binding.btnBackOne.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.btnPopToRoot.setOnClickListener {
            clearFragmentBackStack("已执行 popBackStack(null, INCLUSIVE)")
        }

        supportFragmentManager.addOnBackStackChangedListener { updateBackStackInfo() }

        supportFragmentManager.setFragmentResultListener(
            FragmentLearningContract.REQUEST_EDIT_NAME,
            this
        ) { _, bundle ->
            val newName = bundle.getString(FragmentLearningContract.BUNDLE_NEW_NAME).orEmpty()
            if (newName.isNotBlank()) {
                sharedViewModel.updateName(newName)
                sharedViewModel.postMessage("收到 FragmentResult：name=$newName")
                Toast.makeText(this, "已收到新名称：$newName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindGlobalObservers() {
        sharedViewModel.screenTitle.observe(this) {
            binding.tvScreenTitle.text = "当前标题：$it"
        }

        sharedViewModel.latestMessage.observe(this) {
            binding.tvActivityMessage.text = "Activity 消息：$it"
        }
    }

    private fun updateBackStackInfo() {
        val count = supportFragmentManager.backStackEntryCount

        var entries = ""
        for (i in 0 until count) {
            val name = supportFragmentManager.getBackStackEntryAt(i).name
            entries += name
            if (i != count - 1) {
                entries += " -> "
            }
        }

        if (entries.isBlank()) {
            binding.tvBackStackInfo.text = "返回栈：空"
        } else {
            binding.tvBackStackInfo.text = "返回栈($count)：$entries"
        }
    }


    private fun openRoot() {
        // 清空 Fragment 返回栈，再回到 Home。
        clearFragmentBackStack()
        sharedViewModel.updateTitle("Home Fragment")
        sharedViewModel.postMessage("进入 Home Fragment")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LearningHomeFragment.newInstance(),TAG_HOME)
            .commit()
    }

    private fun clearFragmentBackStack(message: String? = null) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        message?.let(sharedViewModel::postMessage)
        updateBackStackInfo()
    }

    override fun openDetailByReplace(itemId: Int, itemName: String) {
        sharedViewModel.updateTitle("Detail Fragment")
        sharedViewModel.postMessage("使用replace 打开 Detail")
        //当前毫秒时间戳
        val tag = "detail_${System.currentTimeMillis()}"
        replaceFragment(
            LearningDetailFragment.newInstance(itemId,itemName),
            tag,
            true
        )
    }

    override fun openToolsByAddHide() {
        sharedViewModel.updateTitle("Tools Fragment")
        sharedViewModel.postMessage("使用 add + hide 打开 Tools")
        val tag = "tools_${System.currentTimeMillis()}"
        addAndHideCurrent(LearningToolsFragment.newInstance(), tag)
    }

    override fun openEditName(defaultName: String) {
        sharedViewModel.updateTitle("Edit Fragment")
        sharedViewModel.postMessage("打开 Edit Fragment")
        replaceFragment(
            fragment = LearningEditFragment.newInstance(defaultName),
            tag = TAG_EDIT,
            addToBackStack = true
        )
    }

    override fun updateTitleFromFragment(title: String) {
        sharedViewModel.updateTitle(title)
    }

    override fun showTipsDialog() {
        LearningTipsDialogFragment.newInstance()
            .show(supportFragmentManager, TAG_TIPS_DiALOG)
    }

    override fun sendMessageToActivity(message: String) {
        sharedViewModel.postMessage(message)
    }
    private fun replaceFragment(fragment: Fragment, tag: String, addToBackStack: Boolean) {
        val tx = supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.fragmentContainer,fragment,tag)

        if(addToBackStack) tx.addToBackStack(tag)
        tx.commit()
    }
    private fun addAndHideCurrent(fragment: Fragment, tag: String) {
        val tx = supportFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
        //从当前activity找到第一个符合要求的fragment
        //在 fragmentContainer 里、已 add、可见、且没 hidden。
        supportFragmentManager.fragments.firstOrNull{
            it.id == R.id.fragmentContainer && it.isAdded && it.isVisible && !it.isHidden
        }?.let {
            tx.hide(it)
        }

        tx.add(R.id.fragmentContainer,fragment,tag)
            .addToBackStack( tag)
            .commit()

    }
}