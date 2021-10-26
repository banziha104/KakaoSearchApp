package com.lyj.kakaosearchapp.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    /**
     * MainActivity의 ProgressBar의 Visibility 관리
     *
     * @see ProgressBarController
     */
    private val progressBarController: ProgressBarController? by lazy {
        activity as? ProgressBarController
    }

    /**
     * MainViewModel의 검색어 변경을 관찰
     */
    private val searchKeywordChangeObserver: Flowable<String> by lazy {
        viewModel
            .mapToEditTextObserverByPublisher(
                activityViewModel
                    .latestSearchKeyword
                    .toPublisher(viewLifecycleOwner),
                this
            )
    }

    /**
     * MainViewModel 저장된 데이터를 관찰
     */
    private val storedDataChangeObserver: Flowable<MutableMap<String, KakaoSearchModel>> by lazy {
        viewModel
            .mapToStoredDataObserverByPublisher(
                activityViewModel
                    .storedContents
                    .toPublisher(viewLifecycleOwner),
                this
            )
    }

    /**
     * SwipeRefreshLayout 동작 감지
     *
     * @see refreshObserver
     */
    private val swipeRefreshObserver by lazy {
        binding.serachSwipeRefreshLayout.refreshObserver()
            .filter {
                (searchThumbnailAdapter.itemCount > 0).apply { // 아이템이 비어있을 땐 갱신하지 않음
                    if (!this) {
                        binding.serachSwipeRefreshLayout.isRefreshing = false
                    }
                }
            }
            .toFlowable(BackpressureStrategy.LATEST)
    }

    /**
     * 데이터의 영향을 주는 Ui Event를 관찰
     *
     * @see SearchFragmentUiEventType
     * @return Ui Event 타입과 요청한 데이터를 Pair 객체로 반환
     */
    private val uiEventToRequestObserver: Flowable<Pair<SearchFragmentUiEventType, List<KakaoSearchModel>>> by lazy {
        Flowable
            .merge<SearchFragmentUiEventType>(
                searchKeywordChangeObserver // 검색어 변경 감지, 호출시 page를 초기화
                    .map {
                        viewModel.page = KakaoSearchApi.DEFAULT_PAGE
                        SearchFragmentUiEventType.Search(it)
                    },
                binding.searchRecyclerView.observeEndScrollEvents(this) // EndScroll 감지 , 호출시 page를 1 증가
                    .map {
                        viewModel.page++
                        SearchFragmentUiEventType.EndScroll(viewModel.page)
                    },
                swipeRefreshObserver // Refresh 감지 ,호출시 page를 초기화
                    .map {
                        viewModel.page = KakaoSearchApi.DEFAULT_PAGE
                        SearchFragmentUiEventType.Refresh
                    }
            )
            .filter {
                activityViewModel.latestSearchKeyword.value.isNotBlank().apply {
                    if (!this) requireContext().longToast(R.string.main_empty_result)
                }
            }
            .flatMapSingle { event ->
                viewModel.requestKakaoSearchResult(
                    if (event is SearchFragmentUiEventType.Search){ // Search Event 시 새로운 검색어, 나머지는 기존 검색어
                        event.searchKeyword
                    } else {
                        activityViewModel.latestSearchKeyword.value
                    },
                    if (event is SearchFragmentUiEventType.EndScroll) event.page else viewModel.page // Scroll Event 시 증가된 page, 나머지는 초기화된 page
                )
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { progressBarController?.setProgressBarVisibility(View.VISIBLE) }
                    .doOnSuccess {
                        binding.serachSwipeRefreshLayout.isRefreshing = false
                        progressBarController?.setProgressBarVisibility(View.GONE)
                    }
                    .doOnError {
                        binding.serachSwipeRefreshLayout.isRefreshing = false
                        progressBarController?.setProgressBarVisibility(View.GONE)
                    }
                    .map { event to it }
            }
            .disposeByOnDestory(this)

    }

    private val searchThumbnailAdapter: ThumbnailAdapter by lazy {
        val dataFlow = viewModel.mapToKakaoSearchList(
            remoteDataObserver = uiEventToRequestObserver,
            storedDataObserver = storedDataChangeObserver,
            this
        ) {
            lifecycleScope.launch(Dispatchers.Main) {
                requireContext().longToast(R.string.search_fragment_remote_date_empty)
            }
        }
        ThumbnailAdapter(dataFlow, activityViewModel::insertOrDeleteIfExists)
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

    /**
     * SearchFragment 에서 발생하는 Ui Event 를 명시
     */
    sealed interface SearchFragmentUiEventType {
        /**
         * SwipeRefresh 동작시
         */
        object Refresh : SearchFragmentUiEventType

        /**
         * 검색어 변경시
         *
         * @param searchKeyword 변경된 키워드
         */
        class Search(val searchKeyword: String) : SearchFragmentUiEventType

        /**
         * RecyclerView가 최하단에 도달했을 시
         *
         * @param page 요청할 페이지
         */
        class EndScroll(val page: Int) : SearchFragmentUiEventType
    }
}

