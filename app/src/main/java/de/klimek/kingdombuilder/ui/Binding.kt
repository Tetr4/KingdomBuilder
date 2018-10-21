package de.klimek.kingdombuilder.ui

import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.klimek.kingdombuilder.model.Stats


@BindingAdapter("month")
fun Toolbar.setMonth(month: Int) {
    // TODO string resource
    title = "Month $month"
}

@BindingAdapter("visible")
fun FloatingActionButton.setVisible(visible: Boolean) {
    if (visible) show() else hide()
}

@BindingAdapter("items")
fun ViewPager.setItems(items: List<Stats>?) {
    var adapter = this.adapter
    if (adapter !is StatsAdapter) {
        adapter = StatsAdapter((context as FragmentActivity).supportFragmentManager)
        this.adapter = adapter
    }
    adapter.items = items ?: emptyList()

}

@BindingAdapter("page", "pageAttrChanged", requireAll = false)
fun ViewPager.setPage(page: Int, pageAttrChanged: InverseBindingListener?) {
    setCurrentItem(page, true)
    // TODO remove old listener: ListenerUtil.trackListener(this, pageChanged)
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) = Unit
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
        override fun onPageSelected(position: Int) {
            pageAttrChanged?.onChange()
        }
    })
}

@InverseBindingAdapter(attribute = "page")
fun ViewPager.getPage(): Int = currentItem
