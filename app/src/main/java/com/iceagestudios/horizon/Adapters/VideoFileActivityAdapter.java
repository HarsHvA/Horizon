package com.iceagestudios.horizon.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.iceagestudios.horizon.R;
import com.iceagestudios.horizon.VideoFilesActivity;
import com.iceagestudios.horizon.VideoPlayer;

import java.io.File;
import java.util.ArrayList;

public class VideoFileActivityAdapter extends RecyclerView.Adapter<VideoFileActivityAdapter.Holder> {
private Context context;
private ArrayList<File> arrayList;
private VideoFilesActivity activity;

public VideoFileActivityAdapter(Context context, ArrayList<File> arrayList, VideoFilesActivity activity)
{
    this.arrayList = arrayList;
    this.context = context;
    this.activity = activity;
}
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_files_layout,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {

    String name = arrayList.get(holder.getAdapterPosition()).getName();
    holder.textView.setText(name);

        Glide.with(context)
                .load(arrayList.get(holder.getAdapterPosition()).getPath())
                .placeholder(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(holder.imageView);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.ShowMenuDialog(arrayList.get(holder.getAdapterPosition()).getAbsolutePath(),position);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("VideoPath",arrayList.get(holder.getAdapterPosition()).getAbsolutePath());
                intent.putExtra("VideoName",arrayList.get(holder.getAdapterPosition()).getName());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {
        MaterialCardView cardView;
        ImageButton button;
        TextView textView;
        ImageView imageView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.vidFileCardView);
            imageView = itemView.findViewById(R.id.mImg);
            textView = itemView.findViewById(R.id.mText);
            button = itemView.findViewById(R.id.options_btn);
        }
    }

    public void FilteredNames(ArrayList<File> filteredFiles)
    {
        arrayList = filteredFiles;
        notifyDataSetChanged();
    }
}
