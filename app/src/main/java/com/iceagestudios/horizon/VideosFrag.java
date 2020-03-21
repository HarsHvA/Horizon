package com.iceagestudios.horizon;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.iceagestudios.horizon.Adapters.VideosRecyclerAdapter;
import com.iceagestudios.horizon.FetchVideos.Constant;
import com.snatik.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideosFrag extends Fragment implements SwipeRefreshLayout.OnRefreshListener,View.OnClickListener {
    private VideosRecyclerAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SaveFavoriteList saveFavoriteList;
    private EditText mEdit;
    private String path;
    private int adapterPosition;
    private Dialog main_dialog;
    private ImageButton search_btn;
    private boolean search_bar;

    public VideosFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        View view = inflater.inflate(R.layout.fragment_videos,container,false);
        RecyclerView recyclerView = view.findViewById(R.id.videoRecyclerView);
        mEdit = view.findViewById(R.id.editText);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        adapter = new VideosRecyclerAdapter(getContext(),this,mEdit,FetchVideoList());
        saveFavoriteList = new SaveFavoriteList();
        search_btn = Objects.requireNonNull(getActivity()).findViewById(R.id.search_btn);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(50);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_re,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (MainActivity.permissionGranted) {
                FetchVideoList();
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        search_bar = false;
        if(MainActivity.permissionGranted)
        {
            mEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Filter(s.toString());
                }
            });
        }

        search_btn.setOnClickListener(v -> {

            InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
            if(!search_bar) {
                mEdit.setVisibility(View.VISIBLE);
                mEdit.requestFocus();
                mEdit.setShowSoftInputOnFocus(true);
                Objects.requireNonNull(imm).showSoftInput(mEdit, InputMethodManager.SHOW_FORCED);
                search_bar = true;
                search_btn.setBackgroundResource(R.drawable.ic_close_black_30dp);
            }else{
                mEdit.setVisibility(View.GONE);
                search_bar = false;
                Objects.requireNonNull(imm).hideSoftInputFromWindow(mEdit.getWindowToken(),0);
                search_btn.setBackgroundResource(R.drawable.ic_search_black_48dp);
                adapter.FilteredNames(FetchVideoList());
                mEdit.setText("");
            }

        });
    }

    @Override
    public void onRefresh() {
        FetchVideoList();
        adapter.notifyDataSetChanged();
    }

    public void ShowMenuDialog(String path,int adapterPosition) {
        this.path = path;
        this.adapterPosition = adapterPosition;
        main_dialog = new Dialog(Objects.requireNonNull(getContext()));
        main_dialog.setContentView(R.layout.options_dialog);
        Objects.requireNonNull(main_dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout renameLayout = main_dialog.findViewById(R.id.rename);
        LinearLayout shareLayout = main_dialog.findViewById(R.id.share);
        LinearLayout detailsLayout = main_dialog.findViewById(R.id.details);
        LinearLayout favorite = main_dialog.findViewById(R.id.favorite);
        LinearLayout delete = main_dialog.findViewById(R.id.delete);
        delete.setOnClickListener(this);
        renameLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        detailsLayout.setOnClickListener(this);
        favorite.setOnClickListener(this);
        main_dialog.show();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.rename:
                ShowRenameDialog();
                main_dialog.dismiss();
                break;
            case R.id.share:
                ShareFile();
                main_dialog.dismiss();
                break;
            case R.id.details:
                ShowDetailsDialog();
                main_dialog.dismiss();
                break;
            case R.id.favorite:
                saveFavoriteList.SaveArrayList(getContext(),path,true);
                Toast.makeText(getContext(), "Added to favorite!", Toast.LENGTH_SHORT).show();
                main_dialog.dismiss();
                break;

            case R.id.delete:
                DeleteFile(path);
                main_dialog.dismiss();
                break;
        }

    }

    private void ShowDetailsDialog()
    {
        File file = new File(path);
        Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.details_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView name = dialog.findViewById(R.id.detail_name);
        TextView detailPath = dialog.findViewById(R.id.detail_path);
        TextView detailModified = dialog.findViewById(R.id.detail_modified);
        TextView detailSize= dialog.findViewById(R.id.detail_size);
        long date = file.lastModified();
        String size = getFileSize(file.length());
            name.setText(file.getName());
            detailPath.setText(file.getAbsolutePath());

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy h:mm a");
            String dateString = sdf.format(date);
            detailModified.setText(dateString);
            detailSize.setText(size);
        dialog.show();
    }

    private static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
    private void ShowRenameDialog()
    {
        final Dialog dialog = new Dialog(Objects.requireNonNull(getContext()));
        dialog.setContentView(R.layout.rename_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText editText = dialog.findViewById(R.id.rename_edit_text);
        Button cancel = dialog.findViewById(R.id.cancel_rename_button);
        Button rename_btn = dialog.findViewById(R.id.rename_button);
        final File file = new File(FetchVideoList().get(adapterPosition).getAbsolutePath());
        String nameText = file.getName();
        nameText = nameText.substring(0,nameText.lastIndexOf("."));
        editText.setText(nameText);
        editText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        cancel.setOnClickListener(v -> dialog.dismiss());
        rename_btn.setOnClickListener(v -> {
            String onlyPath = file.getParentFile().getAbsolutePath();
            String ext = file.getAbsolutePath();
            ext = ext.substring(ext.lastIndexOf("."));
            String newPath = onlyPath+"/"+editText.getText().toString()+ext;
            File newFile = new File(newPath);
            boolean rename = file.renameTo(newFile);
            if(rename)
            {
                ContentResolver resolver = Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver();
                resolver.delete(
                        MediaStore.Files.getContentUri("external")
                        , MediaStore.MediaColumns.DATA + "=?", new String[] { file.getAbsolutePath() });
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                getActivity().getApplicationContext().sendBroadcast(intent);

                Toast.makeText(getContext(), "SuccessFull!", Toast.LENGTH_SHORT).show();
            }else
            {
                Toast.makeText(getContext(), "Oops! rename failed", Toast.LENGTH_SHORT).show();
            }
            FetchVideoList();
            adapter.notifyDataSetChanged();
            adapter.notifyItemChanged(adapterPosition);
            dialog.dismiss();
        });
        adapter.notifyDataSetChanged();
        dialog.show();
    }

    private void ShareFile()
    {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            intentShareFile.setType("video/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse(FetchVideoList().get(adapterPosition).getAbsolutePath()));

            String shareMessage= "\nLet me recommend you this awesome video player\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";

            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "HV Player");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }

    private void Filter(String text)
    {
        ArrayList<File> filtered = new ArrayList<>();

       for(File s: FetchVideoList())
       {
           if(s.getName().toLowerCase().contains(text.toLowerCase()))
           {
               filtered.add(s.getAbsoluteFile());
           }
       }
        adapter.FilteredNames(filtered);
    }


    @SuppressLint("NewApi")
    private ArrayList<File> FetchVideoList()
    {
        Constant.allMediaList.sort((file, t1) -> Long.compare(file.lastModified(), t1.lastModified()));
        Collections.reverse(Constant.allMediaList);
        mSwipeRefreshLayout.setRefreshing(false);
        return Constant.allMediaList;
    }

    private void DeleteFile(String path)
    {
        new AlertDialog
                .Builder(Objects.requireNonNull(getContext()),R.style.AlertDialogStyle).setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Storage storage = new Storage(getContext());
                    storage.deleteFile(path);
                })
                .setNegativeButton("Cancel",null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
