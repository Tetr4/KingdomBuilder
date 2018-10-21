package de.klimek.kingdombuilder.ui

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import de.klimek.kingdombuilder.model.Stats
import de.klimek.kingdombuilder.ui.stats.StatsFragment

class StatsAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

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

    override fun getPageTitle(position: Int): CharSequence? {
        return "Monat ${position + 1}"
    }
}