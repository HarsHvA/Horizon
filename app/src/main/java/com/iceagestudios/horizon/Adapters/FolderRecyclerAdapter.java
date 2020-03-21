package com.iceagestudios.horizon.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.iceagestudios.horizon.R;
import com.iceagestudios.horizon.VideoFilesActivity;

import java.io.File;
import java.util.ArrayList;

public class FolderRecyclerAdapter extends RecyclerView.Adapter<FolderRecyclerAdapter.FolderHolder> {
    private Context mContext;
    ArrayList<File> foldersArrayList;
    File[] listFile;
    public FolderRecyclerAdapter(Context mContext, ArrayList<File> foldersArrayList)
    {
        this.mContext = mContext;
        this.foldersArrayList = foldersArrayList;
    }

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.folder_item,parent,false);
        return new FolderHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FolderHolder holder, final int position) {
        holder.mText.setText(foldersArrayList.get(position).getName());
        int noOfVideos =0;
        File[] listFile = foldersArrayList.get(position).listFiles();
        if(listFile!=null)
        for(int i =0;i<listFile.length;i++)
        {
            if(listFile[i].getName().endsWith(".mp4")
                    || listFile[i].getName().endsWith(".mkv"))
            {
                noOfVideos++;
            }
        }
        holder.mTextNumb.setText(noOfVideos+" Videos");
        holder.mCardView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, VideoFilesActivity.class);
            intent.putExtra("uri",foldersArrayList.get(position).getAbsolutePath());
            intent.putExtra("FolderName",foldersArrayList.get(position).getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return foldersArrayList.size();
    }

    class FolderHolder extends RecyclerView.ViewHolder{
        TextView mText;
        TextView mTextNumb;
        ImageView mImage;
        MaterialCardView mCardView;
        public FolderHolder(@NonNull View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.folderTextView);
            mTextNumb = itemView.findViewById(R.id.numbText);
            mImage = itemView.findViewById(R.id.folderImageView);
            mCardView = itemView.findViewById(R.id.folderLayout);
        }
    }
}
