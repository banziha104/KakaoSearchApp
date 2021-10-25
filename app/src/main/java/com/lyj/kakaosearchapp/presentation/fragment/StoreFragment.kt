package com.lyj.kakaosearchapp.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import com.lyj.kakaosearchapp.common.rx.RxLifecycleController
import com.lyj.kakaosearchapp.common.rx.RxLifecycleObserver
import com.lyj.kakaosearchapp.databinding.StoreFragmentBinding
import com.lyj.kakaosearchapp.domain.model.KakaoSearchListModel
import com.lyj.kakaosearchapp.presentation.activity.MainViewModel
import com.lyj.kakaosearchapp.presentation.activity.StoredDataControlErrorHandler
import com.lyj.kakaosearchapp.presentation.adapter.recycler.ThumbnailAdapter
import io.reactivex.rxjava3.core.Flowable

class StoreFragment : Fragment(), RxLifecycleController {

    companion object {
        private const val GRID_SPAN_COUNT = 2
        internal val instance: StoreFragment by lazy {
            StoreFragment()
        }
    }

    override val rxLifecycleObserver: RxLifecycleObserver = RxLifecycleObserver(this)

    private lateinit var binding: StoreFragmentBinding

    private val viewModel: StoreViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()

    private val dataChangeObserver: Flowable<List<KakaoSearchListModel>> by lazy {
        viewModel.mapToKakaoSearchListModel(
            activityViewModel
                .storedContents
                .toPublisher(viewLifecycleOwner),
            this
        ).map {
            it.apply {
                binding.textView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private val storedThumbnailAdapter: ThumbnailAdapter by lazy {
        val handler = (activity as? StoredDataControlErrorHandler) ?: throw NotImplementedError()
        val onClicked = activityViewModel.insertOrDeleteIfExists(handler::onError)
        ThumbnailAdapter(dataChangeObserver, onClicked)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StoreFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding
            .storeRecyclerView
            .apply {
                adapter = storedThumbnailAdapter
                layoutManager = GridLayoutManager(requireContext(), GRID_SPAN_COUNT)
            }
    }
}