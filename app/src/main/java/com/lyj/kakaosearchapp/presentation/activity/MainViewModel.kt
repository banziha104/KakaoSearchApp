package com.lyj.kakaosearchapp.presentation.activity


import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.common.view.InitializedMutableLiveData
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.presentation.fragment.SearchFragment
import com.lyj.kakaosearchapp.presentation.fragment.StoreFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    /**
     * 저장된 데이터 객체
     * API를 통해 얻은 데이터와 저장된 데이터간에
     * 비교 연산수를 줄이기 위해 [Map]으로 구현
     */
    val storedContents: InitializedMutableLiveData<MutableMap<String, KakaoSearchModel>> by lazy {
        InitializedMutableLiveData(mutableMapOf())
    }

    /**
     * 마지막으로 검색된 키워드
     */
    val latestSearchKeyword: InitializedMutableLiveData<String> by lazy {
        InitializedMutableLiveData("")
    }

    /**
     * 종속된 프래그먼트에서 데이터 저장 및 삭제시 사용
     * 토글 형태로 비어있다면 저장, 저장되어있다면 삭제
     *
     * @param model 저장 혹은 삭제할 객체
     */
    @Throws(NullPointerException::class)
    fun insertOrDeleteIfExists(model: KakaoSearchModel) {
        val map = storedContents.value
        try {
            val url = model.siteUrl!!
            val item = map[url]
            if (item != null) {
                map.remove(url)
            } else {
                map[url] = model
            }
            storedContents.value = map
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * MainActivity의 Tab Type 을 정의
 *
 * @param titleRes Title 관련 String Resource ID
 * @param fragment Fragment
 */
enum class MainTabType(
    @StringRes val titleRes: Int,
    val fragment: Fragment,
) {
    SEARCH(R.string.main_tab_item_search, SearchFragment.instance),
    STORE(R.string.main_tab_item_store, StoreFragment.instance);

    companion object {
        private val tabTypes = values()

        /**
         * Index로 MainTabsType을 가져오는 함수
         *
         * @param position 찾을 MainTabType의 포지션
         * @return 포지션에 해당하는 MainTabType, 없은 경우 null 반환
         */
        operator fun get(position: Int): MainTabType? = try {
            if (position >= tabTypes.size) throw IndexOutOfBoundsException()
            tabTypes[position]

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

