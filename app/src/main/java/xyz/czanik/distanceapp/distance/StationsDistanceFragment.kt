package xyz.czanik.distanceapp.distance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
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

    private fun FragmentStationsDistanceBinding.setUpStationsSearch() {
        startStationInput.setAdapter(SearchAdapter(searchViewModel))
        endStationInput.setAdapter(SearchAdapter(searchViewModel))
    }

    private fun FragmentStationsDistanceBinding.setUpStationsSelection() {
        startStationInput.onStationSelectionChanged(distanceViewModel::startStationSelected)
        endStationInput.onStationSelectionChanged(distanceViewModel::endStationSelected)
    }

    private fun AutoCompleteTextView.onStationSelectionChanged(stationSelectedConsumer: (Station.Id?) -> Unit) {
        clearSelectionOnTextChanged(stationSelectedConsumer)
        onStationSelectedListener(stationSelectedConsumer)
    }

    private fun AutoCompleteTextView.clearSelectionOnTextChanged(stationSelectedConsumer: (Station.Id?) -> Unit) {
        doOnTextChanged { _, _, _, _ -> stationSelectedConsumer(null) }
    }

    private fun AutoCompleteTextView.onStationSelectedListener(stationSelectedConsumer: (Station.Id?) -> Unit) {
        setOnItemClickListener { _, _, _, id -> stationSelectedConsumer(Station.Id(id.toInt())) }
    }

    private fun factory() = DistanceAppViewModelFactory(container().repositoriesFactory)

    private fun container() = (requireActivity().application as Container)
}