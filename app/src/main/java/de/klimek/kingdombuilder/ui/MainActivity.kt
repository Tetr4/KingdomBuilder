package de.klimek.kingdombuilder.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.shopify.livedataktx.observe
import de.klimek.kingdombuilder.R
import de.klimek.kingdombuilder.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = vm
        binding.setLifecycleOwner(this)

        setSupportActionBar(toolbar)
        tabs.setupWithViewPager(pager)
        vm.isLastSelected.observe(this) { invalidateOptionsMenu() }
        vm.isFirstSelected.observe(this) { invalidateOptionsMenu() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (vm.isLastSelected.value == true && vm.stats.value?.size != 1) {
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

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.discard)
            .setPositiveButton(android.R.string.yes) { _, _ -> vm.delete() }
            .setNegativeButton(android.R.string.cancel, null).show()
    }
}
