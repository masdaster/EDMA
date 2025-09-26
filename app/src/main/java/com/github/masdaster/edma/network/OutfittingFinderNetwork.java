package com.github.masdaster.edma.network;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.github.masdaster.edma.models.CommodityFinderResult;
import com.github.masdaster.edma.models.OutfittingFinderResult;
import com.github.masdaster.edma.models.apis.EDAPIV4.OutfittingFinderResponse;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.network.retrofit.EDApiV4Retrofit;
import com.github.masdaster.edma.singletons.RetrofitSingleton;
import retrofit2.Call;
import retrofit2.internal.EverythingIsNonNull;


public class OutfittingFinderNetwork {
    public static void findOutfitting(Context ctx, String system, final String outfitting,
                                      String landingPad) {

        // Init retrofit instance
        final EDApiV4Retrofit edApiRetrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        final retrofit2.Callback<List<OutfittingFinderResponse>> callback = new retrofit2.Callback<>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<OutfittingFinderResponse>> call,
                                   retrofit2.Response<List<OutfittingFinderResponse>> response) {

                // Check response
                final List<OutfittingFinderResponse> responseBody = response.body();
                if (!response.isSuccessful() || responseBody == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    processResults(responseBody);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<OutfittingFinderResponse>> call,
                                  Throwable t) {
                EventBus.getDefault().post(new ResultsList<>(false,
                        new ArrayList<CommodityFinderResult>()));
            }
        };

        edApiRetrofit.findOutfitting(
                system,
                outfitting,
                landingPad
        ).enqueue(callback);
    }

    private static void processResults(List<OutfittingFinderResponse> responseBody) {


        ResultsList<OutfittingFinderResult> convertedResults;
        List<OutfittingFinderResult> resultsList = new ArrayList<>();
        try {
            for (OutfittingFinderResponse item : responseBody) {
                resultsList.add(OutfittingFinderResult.Companion.fromOutfittingFinderResponse(item));
            }
            convertedResults = new ResultsList<>(true, resultsList);

        } catch (Exception ex) {
            convertedResults = new ResultsList<>(false, new ArrayList<>());
        }
        EventBus.getDefault().post(convertedResults);
    }
}
