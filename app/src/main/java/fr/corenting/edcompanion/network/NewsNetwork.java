package fr.corenting.edcompanion.network;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import fr.corenting.edcompanion.models.NewsArticle;
import fr.corenting.edcompanion.models.apis.EDAPIV4.GalnetArticleResponse;
import fr.corenting.edcompanion.models.apis.EDAPIV4.NewsArticleResponse;
import fr.corenting.edcompanion.models.events.GalnetNews;
import fr.corenting.edcompanion.models.events.News;
import fr.corenting.edcompanion.network.retrofit.EDApiV4Retrofit;
import fr.corenting.edcompanion.singletons.RetrofitSingleton;
import retrofit2.Call;
import retrofit2.internal.EverythingIsNonNull;

public class NewsNetwork {
    public static void getGalnetNews(Context ctx, String language) {

        EDApiV4Retrofit retrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        retrofit2.Callback<List<GalnetArticleResponse>> callback = new retrofit2.Callback<List<GalnetArticleResponse>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<GalnetArticleResponse>> call,
                                   retrofit2.Response<List<GalnetArticleResponse>> response) {
                List<GalnetArticleResponse> body = response.body();
                if (!response.isSuccessful() || body == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    GalnetNews news;
                    try {
                        List<NewsArticle> articles = new ArrayList<>();
                        for (GalnetArticleResponse item : body) {
                            articles.add(NewsArticle.Companion.fromGalnetArticleResponse(item));
                        }
                        news = new GalnetNews(true, articles);
                    } catch (Exception e) {
                        news = new GalnetNews(false, new ArrayList<>());
                    }
                    EventBus.getDefault().post(news);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<GalnetArticleResponse>> call,
                                  Throwable t) {
                GalnetNews news = new GalnetNews(false, new ArrayList<>());
                EventBus.getDefault().post(news);
            }
        };
        retrofit.getGalnetNews(language).enqueue(callback);
    }

    public static void getNews(Context ctx, String language) {

        EDApiV4Retrofit retrofit = RetrofitSingleton.getInstance()
                .getEdApiV4Retrofit(ctx.getApplicationContext());

        retrofit2.Callback<List<NewsArticleResponse>> callback = new retrofit2.Callback<List<NewsArticleResponse>>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<NewsArticleResponse>> call,
                                   retrofit2.Response<List<NewsArticleResponse>> response) {
                List<NewsArticleResponse> body = response.body();
                if (!response.isSuccessful() || body == null) {
                    onFailure(call, new Exception("Invalid response"));
                } else {
                    News news;
                    try {
                        List<NewsArticle> articles = new ArrayList<>();
                        for (NewsArticleResponse item : body) {
                            articles.add(NewsArticle.Companion.fromNewsArticleResponse(item));
                        }
                        news = new News(true, articles);
                    } catch (Exception e) {
                        news = new News(false, new ArrayList<>());
                    }
                    EventBus.getDefault().post(news);
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<NewsArticleResponse>> call,
                                  Throwable t) {
                News news = new News(false, new ArrayList<>());
                EventBus.getDefault().post(news);
            }
        };
        retrofit.getNews(language).enqueue(callback);
    }
}
