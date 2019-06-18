package fr.corenting.edcompanion.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.Instant;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.corenting.edcompanion.R;
import fr.corenting.edcompanion.models.ShipFinderResult;
import fr.corenting.edcompanion.models.events.ShipFinderSearch;
import fr.corenting.edcompanion.utils.MathUtils;
import fr.corenting.edcompanion.utils.ThemeUtils;
import fr.corenting.edcompanion.utils.ViewUtils;
import fr.corenting.edcompanion.views.DelayAutoCompleteTextView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;

public class ShipFinderAdapter extends FinderAdapter<ShipFinderAdapter.HeaderViewHolder,
        ShipFinderAdapter.ResultViewHolder, ShipFinderResult> {

    private final NumberFormat numberFormat;

    public ShipFinderAdapter(Context context) {
        super(context);
        numberFormat = MathUtils.getNumberFormat(context);
    }

    @Override
    protected RecyclerView.ViewHolder getNewHeaderViewHolder(View v) {
        return new HeaderViewHolder(v);
    }

    @Override
    protected RecyclerView.ViewHolder getResultViewHolder(View v) {
        return new ResultViewHolder(v);
    }

    @Override
    public TextView getEmptyTextView() {
        return getHeader().emptyText;
    }

    @Override
    protected int getHeaderResId() {
        return R.layout.fragment_find_ship_header;
    }

    @Override
    protected int getResultResId() {
        return R.layout.list_item_ship_finder_result;
    }

    @Override
    protected void bindHeaderViewHolder(final HeaderViewHolder holder) {
        // System autocomplete
        holder.systemInputEditText.setThreshold(3);
        holder.systemInputEditText.setLoadingIndicator(holder.systemProgressBar);

        holder.systemInputEditText.setAdapter(new AutoCompleteAdapter(context,
                AutoCompleteAdapter.TYPE_AUTOCOMPLETE_SYSTEMS));
        holder.systemInputEditText.setOnItemClickListener((adapterView, view, position, id) ->
                holder.systemInputEditText.setText((String) adapterView
                .getItemAtPosition(position)));

        // Ship autocomplete
        holder.shipInputEditText.setThreshold(3);
        holder.shipInputEditText.setLoadingIndicator(holder.shipProgressBar);
        holder.shipInputEditText.setAdapter(new AutoCompleteAdapter(context,
                AutoCompleteAdapter.TYPE_AUTOCOMPLETE_SHIPS));
        holder.shipInputEditText.setOnItemClickListener((adapterView, view, position, id) ->
                holder.shipInputEditText.setText((String) adapterView.getItemAtPosition(position)));

        // Find button
        final Runnable onSubmit = () -> {
            if (!findButtonEnabled) {
                return;
            }

            ViewUtils.hideSoftKeyboard(holder.findButton.getRootView());

            ShipFinderSearch result = new ShipFinderSearch(
                    holder.shipInputEditText.getText().toString(),
                    holder.systemInputEditText.getText().toString());
            EventBus.getDefault().post(result);
        };

        // On submit stuff
        holder.findButton.setOnClickListener(view -> onSubmit.run());
        holder.shipInputEditText.setOnSubmit(onSubmit);
        holder.systemInputEditText.setOnSubmit(onSubmit);
    }

    @Override
    protected void bindResultViewHolder(ResultViewHolder holder, int position) {
        ShipFinderResult currentResult = results.get(position - 1);

        // Title
        holder.titleTextView.setText(
                String.format("%s - %s", currentResult.getSystemName(),
                        currentResult.getStationName())
        );

        // Theme button according to theme
        if (ThemeUtils.isDarkThemeEnabled(context)) {
            ImageViewCompat.setImageTintList(holder.isPlanetaryImageView, ColorStateList.valueOf(
                    ContextCompat.getColor(context, android.R.color.white)));
        }

        // Other informations
        holder.distanceTextView.setText(context.getString(R.string.distance_ly,
                currentResult.getDistance()));
        holder.starDistanceTextView.setText(context.getString(R.string.distance_ls,
                numberFormat.format(currentResult.getDistanceToStar())));
        holder.permitRequiredTextView.setVisibility(
                currentResult.isPermitRequired() ? View.VISIBLE : View.GONE);
        holder.isPlanetaryImageView.setVisibility(
                currentResult.isPlanetary() ? View.VISIBLE : View.GONE);
        holder.landingPadTextView.setText(currentResult.getMaxLandingPad());

        // Update date
        String date = android.text.format.DateUtils.getRelativeTimeSpanString(
                currentResult.getLastShipyardUpdate().toEpochMilli(), Instant.now().toEpochMilli(),
                0, FORMAT_ABBREV_RELATIVE).toString();
        holder.lastUpdateTextView.setText(date);
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.distanceTextView)
        TextView distanceTextView;

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.isPlanetaryImageView)
        ImageView isPlanetaryImageView;

        @BindView(R.id.permitRequiredTextView)
        TextView permitRequiredTextView;

        @BindView(R.id.lastUpdateTextView)
        TextView lastUpdateTextView;

        @BindView(R.id.landingPadTextView)
        TextView landingPadTextView;

        @BindView(R.id.starDistanceTextView)
        TextView starDistanceTextView;

        public ResultViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.systemInputEditText)
        DelayAutoCompleteTextView systemInputEditText;

        @BindView(R.id.shipInputEditText)
        DelayAutoCompleteTextView shipInputEditText;

        @BindView(R.id.systemProgressBar)
        MaterialProgressBar systemProgressBar;

        @BindView(R.id.shipProgressBar)
        MaterialProgressBar shipProgressBar;

        @BindView(R.id.findButton)
        Button findButton;

        @BindView(R.id.emptyText)
        TextView emptyText;

        public HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
