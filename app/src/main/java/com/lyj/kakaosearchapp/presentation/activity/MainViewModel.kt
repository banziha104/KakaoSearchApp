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
class MainViewModel @Inject constructor(

) : ViewModel() {

    val storedContents: InitializedMutableLiveData<MutableMap<String, KakaoSearchModel>> by lazy {
        InitializedMutableLiveData(mutableMapOf())
    }

    val latestSearchKeyword: InitializedMutableLiveData<String> by lazy {
        InitializedMutableLiveData("")
    }

    @Throws(NullPointerException::class)
    inline fun insertOrDeleteIfExists(
        crossinline onError: (Throwable) -> Unit
    ): (KakaoSearchModel) -> Unit = { model ->
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
            onError(e)
            e.printStackTrace()
        }
    }
}


enum class MainTabsType(
    @StringRes val titleRes: Int,
    val fragment: Fragment,
) {
    SEARCH(R.string.main_tab_item_search, SearchFragment.instance),
    STORE(R.string.main_tab_item_store, StoreFragment.instance);

    companion object {
        private val tabTypes = values()
        fun indexOf(position: Int): MainTabsType? = try {
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

