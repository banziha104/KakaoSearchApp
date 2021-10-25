package com.lyj.kakaosearchapp.presentation.fragment

import android.os.Bundle
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
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.presentation.activity.MainViewModel
import com.lyj.kakaosearchapp.presentation.activity.StoredDataControlErrorHandler
import com.lyj.kakaosearchapp.presentation.activity.ProgressBarController
import com.lyj.kakaosearchapp.presentation.adapter.recycler.ThumbnailAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
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
    private val progressBarController: ProgressBarController? by lazy {
        activity as? ProgressBarController
    }

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

    private val swipeRefreshObserver by lazy {
        binding.serachSwipeRefreshLayout.refreshObserver()
            .filter {
                (searchThumbnailAdapter.itemCount > 0).apply {
                    if (!this) {
                        binding.serachSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
            .toFlowable(BackpressureStrategy.LATEST)
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
                    },
                swipeRefreshObserver
                    .map {
                        viewModel.page = KakaoSearchApi.DEFAULT_PAGE
                        SearchFragmentUiEventType.Refresh
                    }
            )
            .filter {
                activityViewModel.latestSearchKeyword.value.isNotBlank().apply {
                    if(!this) requireContext().longToast(R.string.main_empty_result)
                }
            }
            .flatMapSingle { event ->
                when (event) {
                    is SearchFragmentUiEventType.EndScroll -> viewModel.requestKakaoSearchResult(
                        activityViewModel.latestSearchKeyword.value,
                        event.page
                    )
                    is SearchFragmentUiEventType.Search -> viewModel.requestKakaoSearchResult(event.searchKeyword)
                    SearchFragmentUiEventType.Refresh -> viewModel
                        .requestKakaoSearchResult(activityViewModel.latestSearchKeyword.value)
                        .doOnSubscribe { binding.serachSwipeRefreshLayout.isRefreshing = false }
                }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { progressBarController?.setProgressBarVisibility(View.VISIBLE) }
                    .doOnSuccess { progressBarController?.setProgressBarVisibility(View.GONE) }
                    .doOnError { progressBarController?.setProgressBarVisibility(View.GONE) }
                    .map { event to it }
            }
            .disposeByOnDestory(this)

    }

    private val searchThumbnailAdapter: ThumbnailAdapter by lazy {
        val handler = (activity as? StoredDataControlErrorHandler) ?: throw NotImplementedError()
        val onClicked = activityViewModel.insertOrDeleteIfExists(handler::onError)
        val dataFlow = viewModel.mapToKakaoSearchList(
            remoteDataObserver = uiEventToRequestObserver,
            storedDataObserver = storedDataChangeObserver,
            this
        ) {
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

