package xyz.czanik.distanceapp.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import xyz.czanik.distanceapp.Container
import xyz.czanik.distanceapp.DistanceAppViewModelFactory
import xyz.czanik.distanceapp.databinding.FragmentStationsDistanceBinding
import xyz.czanik.distanceapp.entities.Station

class StationsDistanceFragment : Fragment() {

    private val source by viewModels<StationSearchablesSourceImpl> { factory() }
    private val searchViewModel by lazy { SearchStationsViewModel(source) }
    private val distanceViewModel by lazy { CalculateStationsDistanceViewModel(source, CalculateDistanceUseCase()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentStationsDistanceBinding.inflate(inflater, container, false).run {
        lifecycleOwner = viewLifecycleOwner
        calculateDistanceViewModel = distanceViewModel
        setUpStationsSearch()
        setUpStationsSelection()
        root
    }

    private fun FragmentStationsDistanceBinding.setUpStationsSelection() {
        startStationInput.doOnTextChanged { _, _, _, _ -> distanceViewModel.startStationSelected(null) }
        startStationInput.setOnItemClickListener { _, _, _, id -> distanceViewModel.startStationSelected(Station.Id(id.toInt())) }
        endStationInput.doOnTextChanged { _, _, _, _ -> distanceViewModel.endStationSelected(null) }
        endStationInput.setOnItemClickListener { _, _, _, id -> distanceViewModel.endStationSelected(Station.Id(id.toInt())) }
    }

    private fun FragmentStationsDistanceBinding.setUpStationsSearch() {
        startStationInput.setAdapter(SearchAdapter(searchViewModel))
        endStationInput.setAdapter(SearchAdapter(searchViewModel))
    }

    private fun factory() = DistanceAppViewModelFactory(container().repositoriesFactory)

    private fun container() = (requireActivity().application as Container)
}