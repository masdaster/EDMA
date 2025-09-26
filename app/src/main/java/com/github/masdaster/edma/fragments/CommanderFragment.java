package com.github.masdaster.edma.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayoutMediator;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.adapters.CommanderFragmentPagerAdapter;
import com.github.masdaster.edma.databinding.FragmentCommanderBinding;
import com.github.masdaster.edma.utils.ThemeUtils;

public class CommanderFragment extends Fragment {

    public static final String COMMANDER_FRAGMENT = "commander_fragment";

    private FragmentCommanderBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCommanderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Setup tablayout and viewpager
        binding.viewPager.setAdapter(new CommanderFragmentPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(getTabTitle(position))

        ).attach();

        // Style
        if (ThemeUtils.isDarkThemeEnabled(requireContext())) {
            binding.tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColorDark));
        } else {
            binding.tabLayout.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        }
        binding.tabLayout.setTabTextColors(getResources().getColor(R.color.tabTextSelected),
                getResources().getColor(R.color.tabText));
        return view;
    }

    private String getTabTitle(int position) {
        return switch (position) {
            case 1 -> this.getString(R.string.fleet);
            case 2 -> this.getString(R.string.loadouts);
            default -> this.getString(R.string.commander);
        };
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
