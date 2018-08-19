package fr.corenting.edcompanion.fragments;

import org.greenrobot.eventbus.Subscribe;

import fr.corenting.edcompanion.adapters.CommunityGoalsAdapter;
import fr.corenting.edcompanion.models.CommunityGoals;
import fr.corenting.edcompanion.network.CommunityGoalsNetwork;
import fr.corenting.edcompanion.utils.NotificationsUtils;

public class CommanderFleetFragment extends ListFragment<CommunityGoalsAdapter> {

    public static final String COMMANDER_FLEET_FRAGMENT_TAG = "commander_fleet_fragment";

    @Subscribe
    public void onCommunityGoalEvent(CommunityGoals goals) {
        // Error
        if (!goals.Success) {
            endLoading(true);
            NotificationsUtils.displayDownloadErrorSnackbar(getActivity());
            return;
        }

        // Else setup adapter
        endLoading(false);
    }

    @Override
    void getData() {
        CommunityGoalsNetwork.getCommunityGoals(getContext());
    }

    @Override
    CommunityGoalsAdapter getAdapter() {
        return new CommunityGoalsAdapter(getContext(), recyclerView, false);
    }
}
