package com.iceagestudios.horizon;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iceagestudios.horizon.Adapters.FolderRecyclerAdapter;
import com.iceagestudios.horizon.Adapters.HistoryAdapter;
import com.iceagestudios.horizon.FetchVideos.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoldersFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView folderRecyclerView;
    private RecyclerView historyRecyclerView;
    private FolderRecyclerAdapter mAdapter;
    private HistoryAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private History history = new History();
    private ArrayList<File> historyList = new ArrayList<>();
    private TextView historyListSizeText;
    private TextView foldersListSizeText;
    private LinearLayout linearLayout;

    public FoldersFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folders, container, false);
        historyListSizeText = rootView.findViewById(R.id.historyListSizeText);
        foldersListSizeText = rootView.findViewById(R.id.foldersListSizeText);
        linearLayout = rootView.findViewById(R.id.historyLinearLayout);
        HistoryView(true,rootView);
        folderRecyclerView = rootView.findViewById(R.id.foldersRecyclerView);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        folderRecyclerView.setLayoutManager(manager);
        folderRecyclerView.setHasFixedSize(true);
        folderRecyclerView.setItemViewCacheSize(20);
        folderRecyclerView.setDrawingCacheEnabled(true);
        folderRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        folderRecyclerView.smoothScrollBy(100, 100);
        mSwipeRefreshLayout = rootView.findViewById(R.id.folder_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_re,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
                mSwipeRefreshLayout.setRefreshing(true);
                HistoryList();
                adapter.notifyDataSetChanged();
                GetFolders();
                SetFoldersListSizeText(GetFolders().size());
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
        });
        HideHistoryLayout();
        return rootView;
    }


    private ArrayList<File> GetFolders()
    {
       ArrayList<File> arrayList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Constant.allMediaFoldersList.sort((file, t1) -> Long.compare(file.lastModified(), t1.lastModified()));
        }
        Collections.reverse(Constant.allMediaFoldersList);
        mSwipeRefreshLayout.setRefreshing(false);
        arrayList = Constant.allMediaFoldersList;
        Set<File> set = new HashSet<>(arrayList);
        arrayList.clear();
        arrayList.addAll(set);
        return arrayList;
    }

    @Override
    public void onRefresh() {

        HistoryList();
        adapter.notifyDataSetChanged();
        SetSizeText(HistoryList().size());
        GetFolders();
        mAdapter.notifyDataSetChanged();
        SetFoldersListSizeText(GetFolders().size());
        HideHistoryLayout();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new FolderRecyclerAdapter(getContext(),GetFolders());
        folderRecyclerView.setAdapter(mAdapter);
        SetSizeText(HistoryList().size());
        SetFoldersListSizeText(GetFolders().size());

        if(mSwipeRefreshLayout!=null)
        {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public ArrayList<File> HistoryList()
    {
        historyList.clear();
            if(history.FetchHistory(getContext())!=null && history.FetchHistory(getContext()).size()>0) {
                for (int i = 0; i < history.FetchHistory(getContext()).size(); i++) {
                    historyList.add(new File(history.FetchHistory(getContext()).get(i)));
                }
            }
        return historyList;
    }

    private void HistoryView(boolean proActivated,View rootView)
    {
        if(proActivated) {
            historyRecyclerView = rootView.findViewById(R.id.historyRecyclerView);
            LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
            historyRecyclerView.setLayoutManager(manager);
            historyRecyclerView.setAdapter(mAdapter);
            historyRecyclerView.setHasFixedSize(true);
            historyRecyclerView.setItemViewCacheSize(20);
            historyRecyclerView.setDrawingCacheEnabled(true);
            historyRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            adapter = new HistoryAdapter(getContext(), HistoryList(),this);
            historyRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HistoryList();
        GetFolders();
        SetSizeText(HistoryList().size());
        SetFoldersListSizeText(GetFolders().size());
        adapter.notifyDataSetChanged();
        mAdapter.notifyDataSetChanged();
        HideHistoryLayout();
        mSwipeRefreshLayout.setRefreshing(false);
    }
    public void SetSizeText(int size)
    {
        String size1 = " ("+size+")";
        historyListSizeText.setText(size1);
    }

    public void SetFoldersListSizeText(int size)
    {
        String size2 = " ("+size+")";
        foldersListSizeText.setText(size2);
    }

    public void HideHistoryLayout()
    {
        if(HistoryList().size()>0)
        {
            linearLayout.setVisibility(View.VISIBLE);
        }else
        {
            linearLayout.setVisibility(View.GONE);
        }
    }
}
