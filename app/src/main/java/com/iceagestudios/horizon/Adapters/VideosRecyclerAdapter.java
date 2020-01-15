package com.iceagestudios.horizon.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.iceagestudios.horizon.VideoPlayer;
import com.iceagestudios.horizon.VideosFrag;

import java.io.File;
import java.util.ArrayList;

public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideosRecyclerAdapter.Holder> {
private Context context;
private ArrayList<File> arrayList;
private VideosFrag videosFrag;

public VideosRecyclerAdapter(Context context, ArrayList<File> arrayList,VideosFrag videosFrag)
{
    this.arrayList = arrayList;
    this.context = context;
    this.videosFrag = videosFrag;
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
        String name = arrayList.get(position).getName();
        name = name.substring(0,name.lastIndexOf("."));
        holder.textView.setText(name);
        RequestOptions options = new RequestOptions().override(500,700);
        options.centerCrop();
        RequestOptions requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(arrayList.get(position).getPath())
                .placeholder(R.drawable.ic_launcher_background)
                .apply(options)
                .dontAnimate()
                .apply(requestOptions)
                .into(holder.imageView);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoPlayer.class);
                intent.putExtra("VideoPath",arrayList.get(holder.getAdapterPosition()).getAbsolutePath());
                context.startActivity(intent);
            }
        });
       holder.imageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               videosFrag.ShowMenuDialog(arrayList.get(holder.getAdapterPosition()).getAbsolutePath(),position);
           }
       });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
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

    public void FilteredNames(ArrayList<File> filteredFiles)
    {
        arrayList = filteredFiles;
        notifyDataSetChanged();
    }
}
