package com.github.masdaster.edma.network;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import com.github.masdaster.edma.models.Station;
import com.github.masdaster.edma.models.System;
import com.github.masdaster.edma.models.SystemFinderResult;
import com.github.masdaster.edma.models.SystemHistoryResult;
import com.github.masdaster.edma.models.apis.EDAPIV4.NewsArticleResponse;
import com.github.masdaster.edma.models.apis.EDAPIV4.StationResponse;
import com.github.masdaster.edma.models.apis.EDAPIV4.SystemResponse;
import com.github.masdaster.edma.models.apis.EDSM.EDSMSystemResponse;
import com.github.masdaster.edma.models.events.ResultsList;
import com.github.masdaster.edma.models.events.SystemDetails;
import com.github.masdaster.edma.models.events.SystemHistory;
import com.github.masdaster.edma.models.events.SystemStations;
import com.github.masdaster.edma.network.retrofit.EDApiV4Retrofit;
import com.github.masdaster.edma.network.retrofit.EDSMRetrofit;
import com.github.masdaster.edma.singletons.RetrofitSingleton;
import retrofit2.Call;
import retrofit2.internal.EverythingIsNonNull;

public class SystemNetwork {
    public static void findSystem(Context ctx, String system) {
        EDSMRetrofit retrofit = RetrofitSingleton.getInstance()
                .getEDSMRetrofit(ctx.getApplicationContext());

        retrofit2.Callback<List<EDSMSystemResponse>> callback = new retrofit2.Callback<List<EDSMSystemResponse>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<EDSMSystemResponse>> call,
                                   retrofit2.Response<List<EDSMSystemResponse>> response) {

                List<EDSMSystemResponse> body = response.body();
                ResultsList<SystemFinderResult> convertedResults;
                if (!response.isSuccessful() || body == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    List<SystemFinderResult> resultsList = new ArrayList<>();
                    try {
                        for (EDSMSystemResponse resultItem : body) {
                            resultsList.add(
                                    SystemFinderResult.Companion.fromEDSMSystem(resultItem));
                        }
                        convertedResults = new ResultsList<>(true, resultsList);

                    } catch (Exception ex) {
                        convertedResults = new ResultsList<>(false,
                                new ArrayList<>());
                    }
                    EventBus.getDefault().post(convertedResults);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<EDSMSystemResponse>> call, Throwable t) {
                EventBus.getDefault().post(new ResultsList<>(false,
                        new ArrayList<SystemFinderResult>()));
            }
        };

        retrofit.getSystems(system, 1, 1, 1).enqueue(callback);
    }

    public static void getSystemDetails(Context ctx, String system) {
        EDApiV4Retrofit retrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        retrofit2.Callback<SystemResponse> callback = new retrofit2.Callback<SystemResponse>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<SystemResponse> call,
                                   retrofit2.Response<SystemResponse> response) {

                SystemResponse body = response.body();
                if (!response.isSuccessful() || body == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    SystemDetails event;
                    try {
                        System convertedSystem = System.Companion.fromSystemResponse(body);
                        event = new SystemDetails(true, convertedSystem);
                    } catch (Exception ex) {
                        event = new SystemDetails(false, null);
                    }
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<SystemResponse> call, Throwable t) {
                EventBus.getDefault().post(new ResultsList<>(false,
                        new ArrayList<SystemFinderResult>()));
            }
        };

        retrofit.getSystemDetails(system).enqueue(callback);
    }

    public static void getSystemHistory(Context ctx, String system) {
        EDApiV4Retrofit retrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        retrofit2.Callback<List<NewsArticleResponse.SystemHistoryResponse>> callback =
                new retrofit2.Callback<List<NewsArticleResponse.SystemHistoryResponse>>() {
                    @Override
                    @EverythingIsNonNull
                    public void onResponse(Call<List<NewsArticleResponse.SystemHistoryResponse>> call,
                                           retrofit2.Response<List<NewsArticleResponse.SystemHistoryResponse>> response) {

                        List<NewsArticleResponse.SystemHistoryResponse> body = response.body();
                        if (!response.isSuccessful() || body == null) {
                            onFailure(call, new Exception("Invalid response"));
                        } else {
                            SystemHistory event;
                            try {
                                List<SystemHistoryResult> results = new ArrayList<>();
                                for (NewsArticleResponse.SystemHistoryResponse historyResult : body) {
                                    results.add(SystemHistoryResult.Companion
                                            .fromSystemHistoryResponse(historyResult));
                                }

                                event = new SystemHistory(true, results);
                            } catch (Exception ex) {
                                event = new SystemHistory(false,
                                        new ArrayList<>());
                            }
                            EventBus.getDefault().post(event);
                        }
                    }

                    @Override
                    @EverythingIsNonNull
                    public void onFailure(Call<List<NewsArticleResponse.SystemHistoryResponse>> call,
                                          Throwable t) {
                        EventBus.getDefault().post(new SystemHistory(false,
                                new ArrayList<>()));

                    }
                };

        retrofit.getSystemHistory(system).enqueue(callback);
    }

    public static void getSystemStations(Context ctx, String systemName) {
        EDApiV4Retrofit retrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        retrofit2.Callback<List<StationResponse>> callback = new retrofit2.Callback<List<StationResponse>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<StationResponse>> call,
                                   retrofit2.Response<List<StationResponse>> response) {

                List<StationResponse> body = response.body();
                if (!response.isSuccessful() || body == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    SystemStations event;
                    try {
                        List<Station> results = new ArrayList<>();
                        for (StationResponse responseItem : body) {
                            results.add(Station.Companion
                                    .fromStationResponse(responseItem));
                        }
                        event = new SystemStations(true, results);
                    } catch (Exception ex) {
                        event = new SystemStations(false, new ArrayList<>());
                    }
                    EventBus.getDefault().post(event);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<StationResponse>> call, Throwable t) {
                EventBus.getDefault().post(new ResultsList<>(false,
                        new ArrayList<SystemFinderResult>()));
            }
        };

        retrofit.getSystemStations(systemName).enqueue(callback);
    }
}
