package com.github.masdaster.edma.network;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.github.masdaster.edma.models.CommodityFinderResult;
import com.github.masdaster.edma.models.apis.EDAPIV4.CommodityFinderResponse;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.network.retrofit.EDApiV4Retrofit;
import com.github.masdaster.edma.singletons.RetrofitSingleton;
import retrofit2.Call;
import retrofit2.internal.EverythingIsNonNull;


public class CommodityFinderNetwork {
    public static void findCommodity(Context ctx, String system, final String commodity,
                                     String landingPad, int minStockOrDemand,
                                     boolean isSellingMode) {

        // Init retrofit instance
        final EDApiV4Retrofit edApiRetrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        final retrofit2.Callback<List<CommodityFinderResponse>> callback = new retrofit2.Callback<>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<CommodityFinderResponse>> call,
                                   retrofit2.Response<List<CommodityFinderResponse>> response) {

                // Check response
                final List<CommodityFinderResponse> sellersResponseBody = response.body();
                if (!response.isSuccessful() || sellersResponseBody == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    processResults(sellersResponseBody);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<CommodityFinderResponse>> call,
                                  Throwable t) {
                EventBus.getDefault().post(new ResultsList<>(false,
                        new ArrayList<CommodityFinderResult>()));
            }
        };

        edApiRetrofit.findCommodity(
                system,
                commodity,
                landingPad,
                minStockOrDemand,
                isSellingMode ? "sell" : "buy"
        ).enqueue(callback);
    }

    private static void processResults(List<CommodityFinderResponse> responseBody) {


        ResultsList<CommodityFinderResult> convertedResults;
        List<CommodityFinderResult> resultsList = new ArrayList<>();
        try {
            for (CommodityFinderResponse seller : responseBody) {
                resultsList.add(CommodityFinderResult.Companion.fromCommodityFinderResponse(seller));
            }
            convertedResults = new ResultsList<>(true, resultsList);

        } catch (Exception ex) {
            convertedResults = new ResultsList<>(false, new ArrayList<>());
        }
        EventBus.getDefault().post(convertedResults);
    }
}
