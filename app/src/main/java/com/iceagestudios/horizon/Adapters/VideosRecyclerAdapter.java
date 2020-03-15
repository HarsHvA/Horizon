package com.iceagestudios.horizon.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iceagestudios.horizon.R;
import com.iceagestudios.horizon.Video;
import com.iceagestudios.horizon.VideoPlayer;
import com.iceagestudios.horizon.VideosFrag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideosRecyclerAdapter.Holder> {
private Context context;
private List<Video> videoList;
private VideosFrag videosFrag;
private EditText editText;

public VideosRecyclerAdapter(Context context,VideosFrag videosFrag,EditText editText
,List<Video> videoList)
{
    this.context = context;
    this.videosFrag = videosFrag;
    this.editText = editText;
    this.videoList = videoList;
}
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.video_item,parent,false);
        return new Holder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
    if(videoList!=null && videoList.size()>0) {
        final Video video = videoList.get(position);
        String name = video.name;
        if(name!=null) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        holder.textView.setText(name);

        RequestOptions options = new RequestOptions().override(500, 700);
        options.centerCrop();
        RequestOptions requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(video.uri)
                .placeholder(R.drawable.ic_launcher_background)
                .apply(options)
                .dontAnimate()
                .apply(requestOptions)
                .into(holder.imageView);

        final InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("VideoPath", String.valueOf(video.uri));
                intent.putExtra("VideoName", video.name);
                context.startActivity(intent);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        });
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videosFrag.ShowMenuDialog(video.data,position);
            }
        });
    }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    class Holder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageButton imageButton;
        ImageView imageView;
        TextView textView;

        public Holder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.videos_card_view);
            imageButton = itemView.findViewById(R.id.more_btn);
            imageView = itemView.findViewById(R.id.image_thumbnail);
            textView = itemView.findViewById(R.id.videos_text_name);
        }
    }

    public void FilteredNames(List<Video> filteredFiles)
    {
        videoList = filteredFiles;
        notifyDataSetChanged();
    }
}
