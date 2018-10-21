package de.klimek.kingdombuilder.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.klimek.kingdombuilder.R
import de.klimek.kingdombuilder.model.Stats


@BindingAdapter("visible")
fun FloatingActionButton.setVisible(visible: Boolean) {
    if (visible) show() else hide()
}

@BindingAdapter("items")
fun ViewPager.setItems(items: List<Stats>?) {
    if (items == null) return
    var adapter = this.adapter
    if (adapter !is StatsAdapter) {
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        adapter = StatsAdapter(context, fragmentManager)
        this.adapter = adapter
    }
    adapter.items = items
}

@BindingAdapter("page", "pageAttrChanged", requireAll = false)
fun ViewPager.setPage(page: Int, pageAttrChanged: InverseBindingListener?) {
    setCurrentItem(page, true)
    val newListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            pageAttrChanged?.onChange()
        }
    }
    val oldListener = ListenerUtil.trackListener(this, newListener, R.id.page_listener)
    oldListener?.let { removeOnPageChangeListener(it) }
    addOnPageChangeListener(newListener)
}

@InverseBindingAdapter(attribute = "page")
fun ViewPager.getPage(): Int = currentItem
