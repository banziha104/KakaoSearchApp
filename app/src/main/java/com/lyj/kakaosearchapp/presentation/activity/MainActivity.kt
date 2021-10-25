package com.lyj.kakaosearchapp.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.jakewharton.rxbinding4.view.clicks
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.common.extension.android.longToast
import com.lyj.kakaosearchapp.common.extension.android.searchButtonActionObserver
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.common.rx.RxLifecycleObserver
import com.lyj.kakaosearchapp.databinding.ActivityMainBinding
import com.lyj.kakaosearchapp.presentation.adapter.viewpager.MainViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.merge
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RxLifecycleController,
    ProgressBarController {

    override val rxLifecycleObserver: RxLifecycleObserver = RxLifecycleObserver(this)

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
        observeRxSource()
    }

    private fun initView() {
        initPagerAndTabLayout()
    }

    private fun observeRxSource() {
        observeClickEvent()
    }

    /**
     * MainActivity 에서 검색과 관련된 Event를 처리
     *
     * 아래 두가지를 [merge]를 통해 결합
     * 1. 검색 버튼 클릭
     * 2. Soft Keyboard 에서 Action Button 클릭
     *
     * @see searchButtonActionObserver
     */
    private fun observeClickEvent() {
        listOf(
            binding
                .mainBtnSearch
                .clicks(),
            binding
                .mainInputEditText
                .searchButtonActionObserver()
        )
            .merge()
            .disposeByOnDestory(this)
            .throttleFirst(500, TimeUnit.MILLISECONDS)
            .filter {
                (binding.mainInputEditText.text?.toString()?.isNotBlank() ?: false).apply {
                    if (!this){
                        longToast(R.string.main_empty_result)
                    }
                }
            }
            .subscribe({
                val text = binding.mainInputEditText.text?.toString()
                if (text != null) {
                    viewModel.latestSearchKeyword.value = text
                }
            }, {
                it.printStackTrace()
            })
    }

    /**
     * ViewPager와 TabLayout을 결합
     *
     * @see MainTabType
     */
    private fun initPagerAndTabLayout() {
        binding.mainViewPager.adapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, position ->
            val tabType = MainTabType[position]
            if (tabType != null) {
                tab.text = getString(tabType.titleRes)
            }
        }.attach()
    }

    /**
     * 종속된 Fragment 에서 MainActivity의 ProgressBar의
     * Visibility를 관리할 수 있도록 구현
     *
     * @see [ProgressBarController]
     */
    override fun setProgressBarVisibility(visibility: Int){
        binding.mainProgressBar.visibility = visibility
    }
}

interface ProgressBarController {
    fun setProgressBarVisibility(visibility: Int)
}
