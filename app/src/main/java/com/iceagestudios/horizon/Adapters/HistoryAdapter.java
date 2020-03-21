package com.iceagestudios.horizon.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.iceagestudios.horizon.FoldersFrag;
import com.iceagestudios.horizon.History;
import com.iceagestudios.horizon.R;
import com.iceagestudios.horizon.VideoPlayer;

import java.io.File;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
private Context context;
private ArrayList<File> arrayList;
private FoldersFrag foldersFrag;
private History history;
    public HistoryAdapter(Context context, ArrayList<File> arrayList,FoldersFrag foldersFrag){
        this.context = context;
        this.arrayList = arrayList;
        this.foldersFrag = foldersFrag;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = arrayList.get(position).getName();
        name = name.substring(0,name.lastIndexOf("."));
        holder.videoTextView.setText(name);
        RequestOptions options = new RequestOptions().override(500,300);
        options.centerCrop();
        RequestOptions requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(arrayList.get(position).getPath())
                .placeholder(R.drawable.ic_launcher_background)
                .dontAnimate()
                .apply(options)
                .apply(requestOptions)
                .into(holder.img_Thumbnail);
        String finalName1 = name;
        holder.videoCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("VideoPath",arrayList.get(holder.getAdapterPosition()).getAbsolutePath());
            intent.putExtra("VideoName",arrayList.get(holder.getAdapterPosition()).getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
        holder.videoCardView.setOnLongClickListener(view -> {
            Toast.makeText(context, finalName1, Toast.LENGTH_SHORT).show();
            return true;
        });

        history = new History();
        holder.imageButton.setOnClickListener(view -> {
            history.SaveHistory(context,arrayList.get(position).getAbsolutePath(),false);
            foldersFrag.HistoryList();
            foldersFrag.SetSizeText(arrayList.size());
            notifyDataSetChanged();
            Snackbar.make(holder.videoCardView, "Video removed from history", Snackbar.LENGTH_LONG)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView videoTextView;
        CardView videoCardView;
        ImageView img_Thumbnail;
        ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            videoTextView = itemView.findViewById(R.id.videos_text_name);
            img_Thumbnail = itemView.findViewById(R.id.image_thumbnail);
            videoCardView = itemView.findViewById(R.id.videos_card_view);
            imageButton = itemView.findViewById(R.id.more_btn);
        }
    }
}
