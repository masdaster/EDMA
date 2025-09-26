package com.github.masdaster.edma.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.masdaster.edma.R;
import com.github.masdaster.edma.databinding.FragmentDistanceCalculatorBinding;
import com.github.masdaster.edma.models.ProxyResult;
import com.github.masdaster.edma.models.SystemsDistance;
import com.github.masdaster.edma.utils.NotificationsUtils;
import com.github.masdaster.edma.utils.ViewUtils;
import com.github.masdaster.edma.view_models.DistanceCalculatorViewModel;
import com.github.masdaster.edma.views.SystemInputView;

public class DistanceCalculatorFragment extends Fragment {

    public static final String DISTANCE_CALCULATOR_FRAGMENT_TAG = "distance_calculator_fragment";

    private FragmentDistanceCalculatorBinding binding;

    private DistanceCalculatorViewModel viewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDistanceCalculatorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Autocomplete setup
        setAutocompleteListeners(binding.firstSystemInputView);
        setAutocompleteListeners(binding.secondSystemInputView);

        // Button event
        binding.findButton.setOnClickListener(this::onFindClick);

        viewModel = new ViewModelProvider(requireActivity()).get(DistanceCalculatorViewModel.class);
        viewModel.getDistanceBetweenSystemsResult().observe(getViewLifecycleOwner(), this::onDistanceResult);

        return view;
    }

    public void onDistanceResult(ProxyResult<SystemsDistance> result) {
        binding.progressBar.setVisibility(View.GONE);

        // Error
        if (result.getData() == null || result.getError() != null) {
            NotificationsUtils.displayGenericDownloadErrorSnackbar(getActivity());
            return;
        }

        binding.resultCardView.setVisibility(View.VISIBLE);

        // Display warning if permits are required
        SystemsDistance data = result.getData();
        if (data.getFirstSystemNeedsPermit() && data.getSecondSystemNeedsPermit()) {
            binding.warningTextView.setVisibility(View.VISIBLE);
            binding.warningTextView.setText(requireContext().getString(R.string.permit_required_both,
                    data.getFirstSystemName(), data.getSecondSystemName()));
        } else if (data.getFirstSystemNeedsPermit()) {
            binding.warningTextView.setVisibility(View.VISIBLE);
            binding.warningTextView.setText(requireContext().getString(R.string.permit_required,
                    data.getFirstSystemName()));
        } else if (data.getSecondSystemNeedsPermit()) {
            binding.warningTextView.setVisibility(View.VISIBLE);
            binding.warningTextView.setText(requireContext().getString(R.string.permit_required,
                    data.getSecondSystemName()));
        }

        binding.resultTextView.setText(requireContext().getString(R.string.distance_result,
                data.getDistanceInLy()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void onFindClick(View view) {
        ViewUtils.hideSoftKeyboard(view.getRootView());
        binding.resultCardView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        viewModel.computeDistanceBetweenSystems(
                binding.firstSystemInputView.getText().toString(),
                binding.secondSystemInputView.getText().toString()
        );
    }

    private void setAutocompleteListeners(final SystemInputView editText) {
        editText.setOnSubmit(() -> onFindClick(editText));
    }
}
