package xyz.czanik.distanceapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class CalculateDistanceFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calculate_distance, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) = CalculateDistanceFragment()
    }
}