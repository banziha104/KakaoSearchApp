package com.lyj.kakaosearchapp.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.toPublisher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.lyj.kakaosearchapp.R
import com.lyj.kakaosearchapp.common.extension.android.longToast
import com.lyj.kakaosearchapp.common.extension.android.observeEndScrollEvents
import com.lyj.kakaosearchapp.common.extension.android.refreshObserver
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.common.rx.RxLifecycleObserver
import com.lyj.kakaosearchapp.data.source.remote.service.KakaoSearchApi
import com.lyj.kakaosearchapp.databinding.SearchFragmentBinding
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.presentation.activity.MainViewModel
import com.lyj.kakaosearchapp.presentation.activity.OnStoredDataControlErrorHandler
import com.lyj.kakaosearchapp.presentation.adapter.recycler.ThumbnailAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), RxLifecycleController {

    companion object {
        const val GRID_SPAN_COUNT = 2
        internal val instance: SearchFragment by lazy {
            SearchFragment()
        }
    }

    override val rxLifecycleObserver: RxLifecycleObserver = RxLifecycleObserver(this)

    private lateinit var binding: SearchFragmentBinding

    private val viewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val searchKeywordChangeObserver: Flowable<String> by lazy {
        viewModel
            .mapToEditTextObserverByPublisher(
                activityViewModel
                    .latestSearchKeyword
                    .toPublisher(viewLifecycleOwner),
                this
            )
    }

    private val storedDataChangeObserver: Flowable<MutableMap<String, KakaoSearchModel>> by lazy {
        viewModel
            .mapToStoredDataObserverByPublisher(
                activityViewModel
                    .storedContents
                    .toPublisher(viewLifecycleOwner),
                this
            )
    }

    private val uiEventToRequestObserver: Flowable<Pair<SearchFragmentUiEventType, List<KakaoSearchModel>>> by lazy {
        Flowable
            .merge<SearchFragmentUiEventType>(
                searchKeywordChangeObserver
                    .map {
                        viewModel.page = KakaoSearchApi.DEFAULT_PAGE
                        SearchFragmentUiEventType.Search(it)
                    },
                binding.searchRecyclerView.observeEndScrollEvents(this)
                    .map {
                        viewModel.page++
                        SearchFragmentUiEventType.EndScroll(viewModel.page)
                    }
                    .toFlowable(BackpressureStrategy.LATEST),
                binding.serachSwipeRefreshLayout.refreshObserver()
                    .map {
                        viewModel.page = KakaoSearchApi.DEFAULT_PAGE
                        SearchFragmentUiEventType.Refresh
                    }
                    .toFlowable(BackpressureStrategy.LATEST)
            )
            .flatMapSingle { event ->
                Log.d("testTag","$event 여기 찍힘?")
                when (event) {
                    is SearchFragmentUiEventType.EndScroll -> viewModel.requestKakaoSearchResult(
                        activityViewModel.latestSearchKeyword.value,
                        event.page
                    )
                    SearchFragmentUiEventType.Refresh -> viewModel
                        .requestKakaoSearchResult(activityViewModel.latestSearchKeyword.value)
                        .doOnSubscribe { binding.serachSwipeRefreshLayout.isRefreshing = false }
                    is SearchFragmentUiEventType.Search -> viewModel.requestKakaoSearchResult(event.searchKeyword)
                }.map { event to it }
            }
            .disposeByOnDestory(this)

    }

    private val searchThumbnailAdapter: ThumbnailAdapter by lazy {
        val handler = (activity as? OnStoredDataControlErrorHandler) ?: throw NotImplementedError()
        val onClicked = activityViewModel.insertOrDeleteIfExists(handler::onError)
        val dataFlow = viewModel.mapToKakaoSearchList(
            remoteDataObserver = uiEventToRequestObserver,
            storedDataObserver = storedDataChangeObserver,
            this
        ){
            lifecycleScope.launch(Dispatchers.Main) {
                requireContext().longToast(R.string.search_fragment_remote_date_empty)
            }
        }
        ThumbnailAdapter(dataFlow, onClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding
            .searchRecyclerView
            .apply {
                adapter = searchThumbnailAdapter
                layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
                (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            }
    }

    sealed interface SearchFragmentUiEventType {
        object Refresh : SearchFragmentUiEventType
        class Search(val searchKeyword: String) : SearchFragmentUiEventType
        class EndScroll(val page: Int) : SearchFragmentUiEventType
    }
}

