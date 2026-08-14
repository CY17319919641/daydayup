package com.daydayup.animationdemo

import com.daydayup.R
import com.daydayup.base.BaseActivity
import com.daydayup.base.BaseFragment
import com.daydayup.databinding.ActivityAnimationBinding


class AnimationActivity : BaseActivity<ActivityAnimationBinding>() {

    private var currentFragment : BaseFragment<*>? = null

    override fun provideViewBinding(): ActivityAnimationBinding {
        return ActivityAnimationBinding.inflate(layoutInflater)
    }

    override fun initUI() {
        binding.animation1.setOnClickListener {
            val fragment: BaseFragment<*> = AnimationFragment1()
            replaceFragment(fragment, true)
        }
        binding.lottie.setOnClickListener {
            val fragment: BaseFragment<*> = AnimationFragment2()
            replaceFragment(fragment, true)
        }
        binding.flyMoney.setOnClickListener {
            val fragment: BaseFragment<*> = AnimationFragment3()
            replaceFragment(fragment, true)
        }
    }

    override fun initData() {
    }

    private fun replaceFragment(
        fragment : BaseFragment<*>,
        addToBackStack :Boolean = true,
    ){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        val fragmentTag = fragment.javaClass.simpleName
        transaction.replace(R.id.fragment_container,fragment,fragmentTag)

        if(addToBackStack){
            transaction.addToBackStack(fragmentTag)
        }
        transaction.commitAllowingStateLoss()
        currentFragment = fragment
    }
}