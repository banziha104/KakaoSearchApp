package com.lyj.kakaosearchapp.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.toPublisher
import androidx.recyclerview.widget.GridLayoutManager
import com.lyj.kakaosearchapp.common.extension.lang.disposeByOnDestory
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.common.rx.RxLifecycleObserver
import com.lyj.kakaosearchapp.databinding.SearchFragmentBinding
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.domain.model.KakaoSearchModel
import com.lyj.kakaosearchapp.presentation.activity.MainViewModel
import com.lyj.kakaosearchapp.presentation.activity.OnStoredDataControlErrorHandler
import com.lyj.kakaosearchapp.presentation.adapter.recycler.ThumbnailAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Flowable

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

    private val searchKeywordChangeObserver: Flowable<List<KakaoSearchModel>> by lazy {
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

    private val searchThumbnailAdapter: ThumbnailAdapter by lazy {
        val handler = (activity as? OnStoredDataControlErrorHandler) ?: throw NotImplementedError()
        val onClicked = activityViewModel.insertOrDeleteIfExists(handler::onError)
        val dataFlow = viewModel.mapToKakaoSearchList(
            remoteDataObserver = searchKeywordChangeObserver,
            storedDataObserver = storedDataChangeObserver,
            this
        )
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
            }
    }
}