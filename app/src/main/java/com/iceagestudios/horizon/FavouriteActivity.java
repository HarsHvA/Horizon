package com.iceagestudios.horizon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.iceagestudios.horizon.Adapters.FavoriteAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private SaveFavoriteList saveFavoriteList;
    private FavoriteAdapter adapter;
    private ArrayList<File> arrayList;
    private RecyclerView recyclerView;
    private TextView textView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        LinearLayout linearLayout = findViewById(R.id.favourite_activity_background);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        saveFavoriteList = new SaveFavoriteList();
        arrayList = new ArrayList<>();
        textView = findViewById(R.id.text_view);
        recyclerView = findViewById(R.id.favorite_recycler_view);
        adapter = new FavoriteAdapter(this,arrayList);
        swipeRefreshLayout = findViewById(R.id.favoriteSwipeRefresh);
        if(saveFavoriteList.RetriveArrayList(this)!=null && saveFavoriteList.RetriveArrayList(this).size()>0) {
            for (int i = 0; i < saveFavoriteList.RetriveArrayList(this).size(); i++) {
                arrayList.add(new File(saveFavoriteList.RetriveArrayList(this).get(i)));
            }
        }
        if(arrayList!=null && arrayList.size()>0)
        {
            textView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemViewCacheSize(50);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            recyclerView.setAdapter(adapter);
        }
        else if(arrayList==null)
        {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);

        }
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_re,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            arrayList.clear();
            if(saveFavoriteList.RetriveArrayList(this)!=null && saveFavoriteList.RetriveArrayList(this).size()>0) {
                for (int i = 0; i < saveFavoriteList.RetriveArrayList(this).size(); i++) {
                    arrayList.add(new File(saveFavoriteList.RetriveArrayList(this).get(i)));
                }
            }
            if(arrayList!=null && arrayList.size()>0)
            {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }else if(arrayList==null)
            {
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        arrayList.clear();
        if(saveFavoriteList.RetriveArrayList(this)!=null && saveFavoriteList.RetriveArrayList(this).size()>0) {
            for (int i = 0; i < saveFavoriteList.RetriveArrayList(this).size(); i++) {
                arrayList.add(new File(saveFavoriteList.RetriveArrayList(this).get(i)));
            }
        }

        if(arrayList!=null && arrayList.size()>0)
        {
            recyclerView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }else
        {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
