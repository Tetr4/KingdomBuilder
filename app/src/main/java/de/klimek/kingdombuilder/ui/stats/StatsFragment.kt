package de.klimek.kingdombuilder.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.klimek.kingdombuilder.databinding.FragmentStatsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class StatsFragment : Fragment() {

    val month by lazy { requireArguments().getInt(ARG_MONTH) }

    private val vm: StatsViewModel by viewModel { parametersOf(month) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentStatsBinding.inflate(inflater, container, false)
        binding.vm = vm
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        vm.save()
    }

    companion object {
        private const val ARG_MONTH = "ARG_MONTH"
        fun newInstance(month: Int) = StatsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_MONTH, month)
            }
        }
    }
}