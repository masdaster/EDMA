package com.github.masdaster.edma.fragments

import androidx.fragment.app.activityViewModels
import com.github.masdaster.edma.adapters.CommanderLoadoutsAdapter
import com.github.masdaster.edma.models.exceptions.DataNotInitializedException
import com.github.masdaster.edma.models.exceptions.FrontierAuthNeededException
import com.github.masdaster.edma.utils.NotificationsUtils
import com.github.masdaster.edma.view_models.CommanderViewModel

class CommanderLoadoutsFragment : AbstractListFragment<CommanderLoadoutsAdapter>() {

    private val viewModel: CommanderViewModel by activityViewModels()

    override fun getNewRecyclerViewAdapter(): CommanderLoadoutsAdapter {
        return CommanderLoadoutsAdapter(context)
    }

    override fun getData() {
        viewModel.getAllLoadouts().observe(viewLifecycleOwner) { result ->
            endLoading(false)

            if (result?.data == null || result.error != null) {

                // For frontier auth error there will be a popup displayed by the other fragment anyway
                if (result.error !is FrontierAuthNeededException && result.error !is DataNotInitializedException) {
                    NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
                }
            } else {
                recyclerViewAdapter.submitList(result.data.loadouts)
            }

        }
        viewModel.fetchAllLoadouts()
    }
}
