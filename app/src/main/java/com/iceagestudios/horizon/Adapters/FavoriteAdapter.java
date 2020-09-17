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
import com.iceagestudios.horizon.MainActivity;
import com.iceagestudios.horizon.R;
import com.iceagestudios.horizon.SaveFavoriteList;
import com.iceagestudios.horizon.VideoPlayer;

import java.io.File;
import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.Holder> {
    private Context context;
    private ArrayList<File> arrayList;
    private SaveFavoriteList saveFavoriteList;

    public FavoriteAdapter(Context context, ArrayList<File> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.favorite_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        String name = arrayList.get(holder.getAdapterPosition()).getName();
        name = name.substring(0,name.lastIndexOf("."));
        holder.videoTextView.setText(name);
        RequestOptions options = new RequestOptions().override(500,700);
        options.centerCrop();
        RequestOptions requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(arrayList.get(holder.getAdapterPosition()).getPath())
                .placeholder(R.drawable.ic_launcher_background)
                .apply(options)
                .dontAnimate()
                .apply(requestOptions)
                .into(holder.img_Thumbnail);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView videoTextView;
        CardView videoCardView;
        ImageView img_Thumbnail;
        ImageButton imageButton;

        private Holder(@NonNull View itemView) {
            super(itemView);

            videoTextView = itemView.findViewById(R.id.videos_text_name);
            img_Thumbnail = itemView.findViewById(R.id.image_thumbnail);
            videoCardView = itemView.findViewById(R.id.videos_card_view);
            imageButton = itemView.findViewById(R.id.more_btn);

            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);

            saveFavoriteList = new SaveFavoriteList();
            imageButton.setOnClickListener(view -> {
                saveFavoriteList.SaveArrayList(context,arrayList.get(getAdapterPosition()).getAbsolutePath(),false);
                Toast.makeText(context, "Removed from favorite!", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            });
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("VideoPath",arrayList.get(getAdapterPosition()).getAbsolutePath());
            intent.putExtra("VideoName",arrayList.get(getAdapterPosition()).getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context, arrayList.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
