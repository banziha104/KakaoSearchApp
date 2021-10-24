package com.lyj.kakaosearchapp.presentaion.viewmodel


import com.lyj.kakaosearchapp.common.util.DateUtils
import com.lyj.kakaosearchapp.config.TestConfig
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.mock.PublisherMock
import com.lyj.kakaosearchapp.mock.PublisherMock.Companion.mockModel
import com.lyj.kakaosearchapp.presentation.activity.MainViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [TestConfig.SDK_VERSION])
class MainViewModelTests : PublisherMock {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val viewModel : MainViewModel by lazy{
        MainViewModel()
    }

    @Before
    fun init(){
        hiltRule.inject()
    }

    @Test
    fun `실행테스트`(){
        val insertOrDelete = viewModel.insertOrDeleteIfExists {  }
        insertOrDelete(mockModel)
        assert(viewModel.storedContents.value[mockModel.siteUrl] != null)
        insertOrDelete(mockModel)
        assert(viewModel.storedContents.value[mockModel.siteUrl] == null)
    }
}