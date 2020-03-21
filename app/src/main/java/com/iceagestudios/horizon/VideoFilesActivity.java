package com.iceagestudios.horizon;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.iceagestudios.horizon.Adapters.VideoFileActivityAdapter;
import com.iceagestudios.horizon.FetchVideos.Constant;
import com.snatik.storage.Storage;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class VideoFilesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private ArrayList<File> folVideosArList = new ArrayList<>();
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    VideoFileActivityAdapter mAdapter;
    FoldersFrag Folders;
    Button btn;
    Button closeBtn;
    EditText editText;
    boolean search_bar = false;
    TextView txt;
    String path;
    int adapterPosition;
    private FirebaseAnalytics firebaseAnalytics;
    String uri;
    String folderName;

    File directory;
    private Dialog main_dialog;
    LinearLayout renameLayout;
    LinearLayout shareLayout;
    LinearLayout detailsLayout;

    private SaveFavoriteList saveFavoriteList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);

        LinearLayout linearLayout = findViewById(R.id.video_file_activty_background);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");
        folderName = intent.getStringExtra("FolderName");
        directory = new File(uri);
        saveFavoriteList = new SaveFavoriteList();
        btn = findViewById(R.id.search_btn_video_activity);
        closeBtn = findViewById(R.id.close_activity);
        search_bar = false;
        editText = findViewById(R.id.searchEditText);
        txt = findViewById(R.id.folderName);
        txt.setText(folderName);
        folVideosArList.clear();
        if(MainActivity.permissionGranted)
        {
            editText.addTextChangedListener(new TextWatcher() {
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
        recyclerView = findViewById(R.id.mRecyclerView);
        mAdapter = new VideoFileActivityAdapter(this,folVideosArList,this);
        LinearLayoutManager manager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        btn.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if(!search_bar) {
                editText.setVisibility(View.VISIBLE);
                search_bar = true;
                editText.requestFocus();
                editText.setShowSoftInputOnFocus(true);
                imm.showSoftInput(editText,InputMethodManager.SHOW_FORCED);
                btn.setBackgroundResource(R.drawable.ic_close_black_24dp);
            }else{
                editText.setVisibility(View.GONE);
                search_bar = false;
                imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
                btn.setBackgroundResource(R.drawable.ic_search_black_24dp);
                mAdapter.FilteredNames(folVideosArList);
                editText.setText("");
            }
        });

        closeBtn.setOnClickListener(v -> finish());
        mSwipeRefreshLayout = findViewById(R.id.video_swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_re,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            if (MainActivity.permissionGranted) {
                folVideosArList.clear();
                GetVideos(directory);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    public ArrayList<File> GetVideos(File directory)
    {
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (int i=0; i<fileList.length; i++){
                if(fileList[i].isDirectory()){
                        GetVideos(fileList[i]);
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension: Constant.videoExtensions){
                        //check the type of file
                        if(name.endsWith(extension)){
                            folVideosArList.add(fileList[i]);
                            //when we found file
                            break;
                        }
                    }
                }
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
        return folVideosArList;
    }

    private void Filter(String text)
    {
        ArrayList<File> filtered = new ArrayList<>();

        for(File s : folVideosArList)
        {
            if(s.getName().toLowerCase().contains(text.toLowerCase()))
            {
                filtered.add(s.getAbsoluteFile());
            }
        }
        mAdapter.FilteredNames(filtered);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
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
                saveFavoriteList.SaveArrayList(this,path,true);
                Toast.makeText(this, "Added to favorite!", Toast.LENGTH_SHORT).show();
                main_dialog.dismiss();
                break;

            case R.id.delete:
                DeleteFile(path);
                main_dialog.dismiss();
                break;
        }
    }

    public void DeleteFile(String path) {
        new AlertDialog
                .Builder(this,R.style.AlertDialogStyle).setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Storage storage = new Storage(this);
                    storage.deleteFile(path);
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                })
                .setNegativeButton("Cancel",null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void ShowMenuDialog(String path,int adapterPosition) {
        this.path = path;
        this.adapterPosition = adapterPosition;
        main_dialog = new Dialog(this);
        main_dialog.setContentView(R.layout.options_dialog);
        main_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        renameLayout = main_dialog.findViewById(R.id.rename);
        shareLayout = main_dialog.findViewById(R.id.share);
        detailsLayout = main_dialog.findViewById(R.id.details);
        LinearLayout favorite = main_dialog.findViewById(R.id.favorite);
        LinearLayout delete = main_dialog.findViewById(R.id.delete);
        favorite.setOnClickListener(this);
        delete.setOnClickListener(this);
        renameLayout.setOnClickListener(this);
        shareLayout.setOnClickListener(this);
        detailsLayout.setOnClickListener(this);
        main_dialog.show();

    }
    @Override
    public void onRefresh() {

        folVideosArList.clear();
        GetVideos(directory);
        mAdapter.notifyDataSetChanged();
    }

    private void ShowDetailsDialog()
    {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.details_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT
                ,ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView name = dialog.findViewById(R.id.detail_name);
        TextView detailPath = dialog.findViewById(R.id.detail_path);
        TextView detailModified = dialog.findViewById(R.id.detail_modified);
        TextView detailSize= dialog.findViewById(R.id.detail_size);
        File file = new File(path);
        long date = file.lastModified();
        String size = getFileSize(file.length());
        if(path!= null)
        {
            name.setText(file.getName());
            detailPath.setText(path);

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy h:mm a");
            String dateString = sdf.format(date);
            detailModified.setText(dateString);
            detailSize.setText(size);
        }
        dialog.show();
    }

    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void ShowRenameDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rename_dialog);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final EditText editText = dialog.findViewById(R.id.rename_edit_text);
        Button cancel = dialog.findViewById(R.id.cancel_rename_button);
        Button rename_btn = dialog.findViewById(R.id.rename_button);
        final File file = new File(path);
        String nameTxt = file.getName();
        nameTxt = nameTxt.substring(0,nameTxt.lastIndexOf("."));
        editText.setText(nameTxt);
        editText.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        cancel.setOnClickListener(v -> dialog.dismiss());
        rename_btn.setOnClickListener(v -> {
            String ext = file.getAbsolutePath();
            ext = ext.substring(ext.lastIndexOf("."));
            String onlyPath = file.getParentFile().getAbsolutePath();
            String newPath = onlyPath+"/"+editText.getText().toString()+ext;
            File newFile = new File(newPath);
            boolean rename = file.renameTo(newFile);
            if(rename)
            {
                ContentResolver resolver = getApplicationContext().getContentResolver();
                resolver.delete(
                        MediaStore.Files.getContentUri("external")
                        , MediaStore.MediaColumns.DATA + "=?", new String[] { file.getAbsolutePath() });
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(newFile));
                getApplicationContext().sendBroadcast(intent);

                Toast.makeText(getApplicationContext(), "SuccessFull!", Toast.LENGTH_SHORT).show();

            }else
            {
                Toast.makeText(getApplicationContext(), "Oops! rename failed", Toast.LENGTH_SHORT).show();
            }
            folVideosArList.clear();
            GetVideos(directory);
            mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemChanged(adapterPosition);
            finish();
            startActivity(getIntent());
            dialog.dismiss();
        });
        mAdapter.notifyDataSetChanged();
        dialog.show();
    }

    public void ShareFile()
    {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(path);

        if(fileWithinMyDir.exists()) {
            intentShareFile.setType("video/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+path));

            String shareMessage= "\nLet me recommend you this awesome video player\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            // TODO: Change Sharing File with your app name and link
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                    "HV Player");
            intentShareFile.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(intentShareFile, "Share File"));
        }
    }
}
