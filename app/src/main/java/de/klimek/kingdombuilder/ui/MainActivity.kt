package de.klimek.kingdombuilder.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import de.klimek.kingdombuilder.R
import de.klimek.kingdombuilder.databinding.ActivityMainBinding
import de.klimek.kingdombuilder.util.observe
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*


private const val CODE_CREATE_BACKUP = 0
private const val CODE_RESTORE_BACKUP = 1

class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        observeViewModel()
    }

    private fun initLayout() {
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
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
        vm.message.observe(this) { it?.let { showMessage(it) } }
    }

    private fun goToEndOfList() {
        vm.selectedStatsIndex.value = vm.stats.value?.lastIndex
    }

    private fun showMessage(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val deleteItem = menu.findItem(R.id.action_delete)
        deleteItem.isVisible = vm.isLastSelected.value == true && vm.isFirstSelected.value == false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> showDeleteDialog()
            R.id.action_backup -> showCreateBackupFilePicker()
            R.id.action_restore -> showRestoreBackupFilePicker()
            else -> return false
        }
        return true
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.discard)
            .setPositiveButton(android.R.string.yes) { _, _ -> vm.deleteMonth() }
            .setNegativeButton(android.R.string.cancel, null).show()
    }

    private fun showCreateBackupFilePicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/x-sqlite3"
        intent.putExtra(Intent.EXTRA_TITLE, "kingdom-stats-${Date().toDateFormat()}.db")
        startActivityForResult(intent, CODE_CREATE_BACKUP)
    }

    private fun showRestoreBackupFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/x-sqlite3"
        startActivityForResult(intent, CODE_RESTORE_BACKUP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uri = data?.data
        if (resultCode == Activity.RESULT_OK && uri != null) {
            when (requestCode) {
                CODE_CREATE_BACKUP -> vm.createBackup(uri)
                CODE_RESTORE_BACKUP -> vm.restoreBackup(uri)
            }
        }
    }
}

private fun Activity.hideKeyboard() {
    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputManager?.hideSoftInputFromWindow(pager.windowToken, 0)
}

private fun Date.toDateFormat(locale: Locale = Locale.getDefault()) =
    SimpleDateFormat("yyyy-MM-dd", locale).format(this)
