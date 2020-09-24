package xyz.czanik.distanceapp.distance

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.util.Consumer
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel
import xyz.czanik.distanceapp.distance.DistanceContract.SearchViewModel.Query
import xyz.czanik.distanceapp.distance.DistanceContract.StationViewModel

class SearchAdapter(
    private val searchViewModel: SearchViewModel
) : BaseAdapter(), Filterable, Consumer<List<StationViewModel>> {

    private var items: List<StationViewModel> = emptyList()
    private val searchFilter by lazy { SearchFilter(searchViewModel, this) }

    override fun accept(items: List<StationViewModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = searchFilter

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = TextView(parent.context).also {
        it.text = items[position].name
    }

    override fun getItem(position: Int): Any = items[position].name

    override fun getItemId(position: Int): Long = items[position].stationId.toLong()

    override fun getCount(): Int = items.size

    private class SearchFilter(
        private val searchViewModel: SearchViewModel,
        private val resultConsumer: Consumer<List<StationViewModel>>
    ) : Filter() {

        override fun performFiltering(query: CharSequence?): FilterResults = searchViewModel
                .search(Query(query.toStringOrEmpty()))
                .let(::toFilterResult)

        private fun toFilterResult(result: SearchViewModel.Result?) = FilterResults().run {
            values = result?.value
            count = result?.value?.size ?: 0
            this
        }

        override fun publishResults(query: CharSequence?, result: FilterResults) {
            resultConsumer.accept((result.values as? List<StationViewModel>).orEmpty())
        }

        private fun CharSequence?.toStringOrEmpty() = this?.toString() ?: ""
    }
}

