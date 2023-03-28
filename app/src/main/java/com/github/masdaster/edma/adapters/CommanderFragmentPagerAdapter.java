package com.github.masdaster.edma.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.fragments.CommanderFleetFragment;
import com.github.masdaster.edma.fragments.CommanderLoadOutListFragment;
import com.github.masdaster.edma.fragments.CommanderStatusFragment;
import com.github.masdaster.edma.utils.CommanderUtils;

public class CommanderFragmentPagerAdapter extends FragmentPagerAdapter {

    private final FragmentManager fragmentManager;
    private final Context context;

    public CommanderFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentManager = fm;
        this.context = context;
    }

    @Override
    public int getCount() {
        int count = 1;
        count += CommanderUtils.INSTANCE.hasFleetData(context) ? 1 : 0;
        count += CommanderUtils.INSTANCE.hasLoadOutListData(context) ? 1 : 0;
        return count;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        // Try to find existing fragment
        String tag = getTag(position);
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            return fragment;
        }

        // Else return new one
        switch (position){
            default:
                return new CommanderStatusFragment();
            case 1:
                return new CommanderFleetFragment();
            case 2:
                return new CommanderLoadOutListFragment();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            default:
                return context.getString(R.string.commander);
            case 1:
                return context.getString(R.string.fleet);
            case 2:
                return context.getString(R.string.load_out_list);
        }
    }

    private String getTag(int position) {
        switch (position){
            default:
                return CommanderStatusFragment.COMMANDER_STATUS_FRAGMENT;
            case 1:
                return CommanderFleetFragment.COMMANDER_FLEET_FRAGMENT_TAG;
            case 2:
                return CommanderLoadOutListFragment.COMMANDER_LOAD_OUT_LIST_FRAGMENT_TAG;
        }
    }
}
