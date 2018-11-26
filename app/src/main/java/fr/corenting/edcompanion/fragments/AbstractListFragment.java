package fr.corenting.edcompanion.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.corenting.edcompanion.R;
import fr.corenting.edcompanion.adapters.ListAdapter;

public abstract class AbstractListFragment<TAdapter extends ListAdapter> extends Fragment {
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.swipeContainer)
    public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.emptySwipe)
    public SwipeRefreshLayout emptySwipeRefreshLayout;

    protected TAdapter recyclerViewAdapter;
    protected boolean loadDataOnCreate = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);

        // Recycler view setup
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = savedInstanceState == null ? getAdapter() : recyclerViewAdapter;
        recyclerView.setAdapter(recyclerViewAdapter);

        // Animation
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));

        //Swipe to refresh setup
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startLoading();
                getData();
            }
        };
        swipeRefreshLayout.setOnRefreshListener(listener);
        emptySwipeRefreshLayout.setOnRefreshListener(listener);

        // Load data if not restored
        if (savedInstanceState != null) {
            endLoading(recyclerViewAdapter.getItemCount() == 0);
        } else if (loadDataOnCreate) {
            startLoading();
            getData();
        }

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register eventbus for the list data
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void endLoading(final boolean empty) {
        emptySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                emptySwipeRefreshLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
                emptySwipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setVisibility(empty ? View.GONE : View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void startLoading() {
        recyclerViewAdapter.removeAllItems();

        emptySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                emptySwipeRefreshLayout.setVisibility(View.GONE);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(true);
            }
        });
    }


    abstract void getData();

    abstract TAdapter getAdapter();
}
