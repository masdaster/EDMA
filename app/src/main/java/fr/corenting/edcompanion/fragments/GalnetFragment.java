package fr.corenting.edcompanion.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import fr.corenting.edcompanion.R;
import fr.corenting.edcompanion.adapters.NewsAdapter;
import fr.corenting.edcompanion.models.events.GalnetNews;
import fr.corenting.edcompanion.network.NewsNetwork;
import fr.corenting.edcompanion.utils.NotificationsUtils;
import fr.corenting.edcompanion.utils.SettingsUtils;

public class GalnetFragment extends AbstractListFragment<NewsAdapter> {

    public static final String GALNET_FRAGMENT_TAG = "galnet_fragment";

    private String language;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        language = getNewsLanguage();
    }

    private String getNewsLanguage() {
        return SettingsUtils.getString(getContext(),
                getString(R.string.settings_galnet_lang));
    }

    @Override
    public void onResume() {
        super.onResume();

        // Refresh if language changed
        if (!language.equals(getNewsLanguage())) {
            language = getNewsLanguage();
            getData();
        }
    }

    @Override
    void getData() {
        NewsNetwork.getGalnetNews(getContext(), language);
    }

    @Override
    NewsAdapter getAdapter() {
        return new NewsAdapter(getContext(), recyclerView, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewsEvent(GalnetNews news) {
        // Error case
        if (!news.getSuccess()) {
            endLoading(true);
            NotificationsUtils.displayGenericDownloadErrorSnackbar(getActivity());
            return;
        }

        endLoading(news.getArticles().size() == 0);
        recyclerViewAdapter.submitList(news.getArticles());
    }
}
