package de.klimek.kingdombuilder.ui

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import de.klimek.kingdombuilder.R
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.ui.stats.StatsFragment

class StatsAdapter(private val context: Context, fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var items = emptyList<Stats>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemPosition(item: Any) = if (item is StatsFragment && item.month < items.count()) {
        item.month
    } else {
        PagerAdapter.POSITION_NONE
    }

    override fun getItem(position: Int) = StatsFragment.newInstance(position)
    override fun getCount() = items.size

    override fun getPageTitle(position: Int): String =
        context.resources.getString(R.string.month, position + 1)
}