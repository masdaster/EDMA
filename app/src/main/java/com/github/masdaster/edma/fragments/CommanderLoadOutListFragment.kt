package com.github.masdaster.edma.fragments

import androidx.fragment.app.activityViewModels
import com.github.masdaster.edma.adapters.CommanderLoadOutListAdapter
import com.github.masdaster.edma.models.exceptions.DataNotInitializedException
import com.github.masdaster.edma.models.exceptions.FrontierAuthNeededException
import com.github.masdaster.edma.utils.NotificationsUtils
import com.github.masdaster.edma.view_models.CommanderViewModel

class CommanderLoadOutListFragment : AbstractListFragment<CommanderLoadOutListAdapter>() {

    private val viewModel: CommanderViewModel by activityViewModels()

    override fun getNewRecyclerViewAdapter(): CommanderLoadOutListAdapter = CommanderLoadOutListAdapter()

    override fun getData() {
        viewModel.getLoadOutList().observe(viewLifecycleOwner, { result ->
            endLoading(false)
            if (result?.data == null || result.error != null) {
                // For frontier auth error there will be a popup displayed by the other fragment anyway
                if (result.error !is FrontierAuthNeededException && result.error !is DataNotInitializedException) {
                    NotificationsUtils.displayGenericDownloadErrorSnackbar(activity)
                }
            } else {
                recyclerViewAdapter.submitList(result.data.loadOutInformationList)
            }

        })
        viewModel.fetchLoadOutList()
    }

    override fun needEventBus(): Boolean {
        return false
    }

    companion object {
        const val COMMANDER_LOAD_OUT_LIST_FRAGMENT_TAG = "commander_load_out_list_fragment"
    }
}
