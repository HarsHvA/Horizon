package com.iceagestudios.horizon;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
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

import com.iceagestudios.horizon.Adapters.FolderRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoldersFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView folderRecyclerView;
    FolderRecyclerAdapter mAdapter;
    boolean listPermission;
    public static ArrayList<File> foldersArrayList = new ArrayList<>();
    File directory;
    File directory_sd;
    SwipeRefreshLayout mSwipeRefreshLayout;

    public FoldersFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_folders, container, false);
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
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (MainActivity.permissionGranted) {

                    foldersArrayList.clear();
                    GetFolders();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        return rootView;
    }

    private ArrayList<File> GetFolders()
    {

        mSwipeRefreshLayout.setRefreshing(true);
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String sortOrder = MediaStore.Video.Media.DATE_MODIFIED+ " ASC";
        Cursor cursor = contentResolver.query(videoUri,null,null,null,sortOrder);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                int videoPath = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                do {

                    String path = cursor.getString(videoPath);
                    File file = new File(path);
                    foldersArrayList.add(file.getParentFile());
                    Set<File> set = new HashSet<>(foldersArrayList);
                    foldersArrayList.clear();
                    foldersArrayList.addAll(set);

                } while (cursor.moveToNext());
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }catch (Exception e)
        {
            Log.i("CursorHandleException", e.getMessage());
            mSwipeRefreshLayout.setRefreshing(false);
        }
        return foldersArrayList;
    }

    @Override
    public void onRefresh() {

        foldersArrayList.clear();
        GetFolders();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i("Path_I", Environment.getExternalStorageDirectory().getAbsolutePath());
        directory_sd = new File("/storage/");
        foldersArrayList.clear();
        mAdapter = new FolderRecyclerAdapter(getContext(),foldersArrayList);
        folderRecyclerView.setAdapter(mAdapter);
        if(MainActivity.permissionGranted)
        {
            listPermission=true;
        }
    }
}
