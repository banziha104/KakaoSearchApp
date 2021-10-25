package com.lyj.kakaosearchapp.presentation.activity

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
import java.lang.NullPointerException
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnStoredDataControlErrorHandler, RxLifecycleController,
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
        initPagerAndTablayout()
    }

    private fun observeRxSource() {
        observeClickEvent()
    }

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

    private fun initPagerAndTablayout() {
        binding.mainViewPager.adapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.mainTabLayout, binding.mainViewPager) { tab, position ->
            val tabType = MainTabsType.indexOf(position)
            if (tabType != null) {
                tab.text = getString(tabType.titleRes)
            }
        }.attach()
    }

    override fun onError(throwable: Throwable) {
        longToast(if (throwable is NullPointerException) R.string.exception_url_is_null else R.string.exception_undeclared)
    }

    override fun setProgressBarVisibility(visibility: Int){
        binding.mainProgressBar.visibility = visibility
    }
}

interface ProgressBarController {
    fun setProgressBarVisibility(visibility: Int)
}

interface OnStoredDataControlErrorHandler {
    fun onError(throwable: Throwable)
}