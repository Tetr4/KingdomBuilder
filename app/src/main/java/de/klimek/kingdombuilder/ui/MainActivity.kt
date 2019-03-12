package de.klimek.kingdombuilder.ui

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import de.klimek.kingdombuilder.R
import de.klimek.kingdombuilder.databinding.ActivityMainBinding
import de.klimek.kingdombuilder.util.observe
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (vm.isLastSelected.value == true && vm.isFirstSelected.value == false) {
            menuInflater.inflate(R.menu.menu, menu)
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showDeleteDialog()
            else -> return false
        }
        return true
    }

    private fun initLayout() {
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = vm
        binding.lifecycleOwner = this
        setSupportActionBar(toolbar)
        tabs.setupWithViewPager(pager)
    }

    private fun observeViewModel() {
        vm.isLastSelected.observe(this) { invalidateOptionsMenu() }
        vm.isFirstSelected.observe(this) { invalidateOptionsMenu() }
        vm.selectedStatsIndex.observe(this) { hideKeyboard() }
        vm.statsSize.observe(this) { goToEndOfList() }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.discard)
            .setPositiveButton(android.R.string.yes) { _, _ -> vm.deleteMonth() }
            .setNegativeButton(android.R.string.cancel, null).show()
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputManager?.hideSoftInputFromWindow(pager.windowToken, 0)
    }

    private fun goToEndOfList() {
        vm.selectedStatsIndex.value = vm.stats.value?.lastIndex
    }
}
