package com.github.masdaster.edma.fragments

import androidx.fragment.app.activityViewModels
import com.github.masdaster.edma.adapters.CommanderFleetAdapter
import com.github.masdaster.edma.models.exceptions.DataNotInitializedException
import com.github.masdaster.edma.models.exceptions.FrontierAuthNeededException
import com.github.masdaster.edma.utils.NotificationsUtils
import com.github.masdaster.edma.view_models.CommanderViewModel

class CommanderFleetFragment : AbstractListFragment<CommanderFleetAdapter>() {

    private val viewModel: CommanderViewModel by activityViewModels()

    override fun getNewRecyclerViewAdapter(): CommanderFleetAdapter {
        return CommanderFleetAdapter(context)
    }

    override fun getData() {
        viewModel.getFleet().observe(viewLifecycleOwner) { result ->
            endLoading(false)

            if (result?.data == null || result.error != null) {

                // For frontier auth error there will be a popup displayed by the other fragment anyway
                if (result.error !is FrontierAuthNeededException && result.error !is DataNotInitializedException) {
                    NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
                }
            } else {
                recyclerViewAdapter.submitList(result.data.ships)
            }

        }
        viewModel.fetchFleet()
    }
}
