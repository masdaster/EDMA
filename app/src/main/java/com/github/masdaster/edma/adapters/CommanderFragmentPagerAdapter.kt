package com.github.masdaster.edma.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.masdaster.edma.R
import com.github.masdaster.edma.fragments.CommanderFleetFragment
import com.github.masdaster.edma.fragments.CommanderLoadoutsFragment
import com.github.masdaster.edma.fragments.CommanderStatusFragment
import com.github.masdaster.edma.utils.CommanderUtils.hasFleetData
import com.github.masdaster.edma.utils.CommanderUtils.hasLoadoutData
import com.github.masdaster.edma.utils.SettingsUtils

class CommanderFragmentPagerAdapter(val fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        val displayFleet = SettingsUtils.getBoolean(
            fragment.requireContext(),
            fragment.requireContext().getString(R.string.settings_cmdr_loadout_display_enable),
            true
        ) && hasLoadoutData(fragment.requireContext())
        return 1 + (if (hasFleetData(fragment.requireContext())) 1 else 0) + if (displayFleet) 1 else 0
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> CommanderFleetFragment()
            2 -> CommanderLoadoutsFragment()
            else -> CommanderStatusFragment()
        }
    }
}
