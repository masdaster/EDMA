package fr.corenting.edcompanion.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.threeten.bp.Instant;

import java.text.NumberFormat;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.corenting.edcompanion.R;
import fr.corenting.edcompanion.models.CommodityFinderResult;
import fr.corenting.edcompanion.models.CommodityFinderSearchEvent;
import fr.corenting.edcompanion.utils.SettingsUtils;
import fr.corenting.edcompanion.utils.ViewUtils;
import fr.corenting.edcompanion.views.ClickToSelectEditText;
import fr.corenting.edcompanion.views.DelayAutoCompleteTextView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;

public class CommodityFinderAdapter extends FinderAdapter<CommodityFinderAdapter.HeaderViewHolder, CommodityFinderAdapter.ResultViewHolder, CommodityFinderResult> {

    public CommodityFinderAdapter(Context context) {
        super(context);
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
    protected int getHeaderResId() {
        return R.layout.fragment_find_commodity_header;
    }

    @Override
    protected int getResultResId() {
        return R.layout.commodity_finder_list_item;
    }

    @Override
    protected void bindHeaderViewHolder(final HeaderViewHolder holder) {
        // System autocomplete
        holder.systemInputEditText.setThreshold(3);
        holder.systemInputEditText.setLoadingIndicator(holder.systemProgressBar);
        holder.systemInputEditText.setAdapter(new AutoCompleteAdapter(context, AutoCompleteAdapter.TYPE_AUTOCOMPLETE_SYSTEMS));
        holder.systemInputEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                holder.systemInputEditText.setText((String) adapterView.getItemAtPosition(position));
            }
        });

        // Ship autocomplete
        holder.commodityInputEditText.setThreshold(3);
        holder.commodityInputEditText.setLoadingIndicator(holder.commodityProgressBar);
        holder.commodityInputEditText.setAdapter(new AutoCompleteAdapter(context, AutoCompleteAdapter.TYPE_AUTOCOMPLETE_COMMODITIES));
        holder.commodityInputEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                holder.commodityInputEditText.setText((String) adapterView.getItemAtPosition(position));
            }
        });

        // Landing pad size adapter
        if (holder.landingPadSizeSpinner.getItems() == null || holder.landingPadSizeSpinner.getItems().size() == 0) {
            String[] landingPadSizeArray = context.getResources().getStringArray(R.array.landing_pad_size);
            holder.landingPadSizeSpinner.setItems(Arrays.asList(landingPadSizeArray));
        }

        // Find button
        final Runnable onSubmit = new Runnable() {
            @Override
            public void run() {
                ViewUtils.hideSoftKeyboard(holder.findButton.getRootView());

                // Convert stock value to int
                String stockString = holder.stockInputEditText.getText().toString();
                int stock = 1;
                if (stockString.length() != 0) {
                    try {
                        stock = Integer.parseInt(stockString);
                    } catch (NumberFormatException e) {
                        stock = 1;
                    }
                }

                CommodityFinderSearchEvent result = new CommodityFinderSearchEvent();
                result.CommodityName = holder.commodityInputEditText.getText().toString();
                result.SystemName = holder.systemInputEditText.getText().toString();
                result.LandingPadSize = holder.landingPadSizeSpinner.getText().toString();
                result.Stock = stock;
                EventBus.getDefault().post(result);
            }
        };
        holder.findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit.run();
            }
        });
        holder.stockInputEditText.setOnSubmit(onSubmit);
        holder.commodityInputEditText.setOnSubmit(onSubmit);
        holder.systemInputEditText.setOnSubmit(onSubmit);


        // Bind empty text view
        emptyTextView = holder.emptyText;
    }

    @Override
    protected void bindResultViewHolder(ResultViewHolder holder, int position) {
        CommodityFinderResult currentResult = results.get(position - 1);
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(SettingsUtils.getUserLocale(context));

        // TODO : get price difference from new model
        // For price, also display the difference with the avg galactic price
        /*String priceDifference = getPriceDifference(results.AveragePrice,
                currentResult.BuyPrice);
        String buyPrice = numberFormat.format(currentResult.BuyPrice);
        holder.priceTextView.setText(buyPrice + " (" + priceDifference + "%)");*/

        // Update date
        String date = android.text.format.DateUtils.getRelativeTimeSpanString(currentResult.LastPriceUpdate.toEpochMilli(),
                Instant.now().toEpochMilli(), 0, FORMAT_ABBREV_RELATIVE).toString();
        holder.lastUpdateTextView.setText(date);

        // Other informations
        holder.titleTextView.setText(String.format("%s - %s", currentResult.System, currentResult.Station));
        holder.permitRequiredTextView.setVisibility(currentResult.PermitRequired ? View.VISIBLE : View.GONE);
        holder.isPlanetaryImageView.setVisibility(currentResult.IsPlanetary ? View.VISIBLE : View.GONE);
        holder.landingPadTextView.setText(currentResult.LandingPad);
        holder.stockTextView.setText(numberFormat.format(currentResult.Stock));
        holder.distanceTextView.setText(context.getString(R.string.distance_ly,
                currentResult.Distance));
        holder.distanceToStarTextView.setText(context.getString(R.string.distance_ls,
                NumberFormat.getIntegerInstance(SettingsUtils.getUserLocale(context)).format(currentResult.DistanceToStar)));
    }

    public class ResultViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.titleTextView)
        TextView titleTextView;

        @BindView(R.id.isPlanetaryImageView)
        ImageView isPlanetaryImageView;

        @BindView(R.id.permitRequiredTextView)
        TextView permitRequiredTextView;

        @BindView(R.id.distanceTextView)
        TextView distanceTextView;

        @BindView(R.id.distanceToStarTextView)
        TextView distanceToStarTextView;

        @BindView(R.id.stockTextView)
        TextView stockTextView;

        @BindView(R.id.priceTextView)
        TextView priceTextView;

        @BindView(R.id.landingPadTextView)
        TextView landingPadTextView;

        @BindView(R.id.lastUpdateTextView)
        TextView lastUpdateTextView;

        public ResultViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.systemInputEditText)
        DelayAutoCompleteTextView systemInputEditText;

        @BindView(R.id.commodityInputEditText)
        DelayAutoCompleteTextView commodityInputEditText;

        @BindView(R.id.landingPadSizeSpinner)
        ClickToSelectEditText landingPadSizeSpinner;

        @BindView(R.id.stockInputEditText)
        DelayAutoCompleteTextView stockInputEditText;

        @BindView(R.id.systemProgressBar)
        MaterialProgressBar systemProgressBar;

        @BindView(R.id.commodityProgressBar)
        MaterialProgressBar commodityProgressBar;

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