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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
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
import java.util.Objects;

public class VideosRecyclerAdapter extends RecyclerView.Adapter<VideosRecyclerAdapter.Holder> {
private Context context;
private ArrayList<File> arrayList;
private VideosFrag videosFrag;
private EditText editText;

public VideosRecyclerAdapter(Context context,VideosFrag videosFrag,EditText editText
,ArrayList<File> arrayList)
{
    this.context = context;
    this.videosFrag = videosFrag;
    this.editText = editText;
    this.arrayList = arrayList;
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
        String name = arrayList.get(holder.getAdapterPosition()).getName();
        holder.textView.setText(name);
        RequestOptions options = new RequestOptions().override(400, 600);
        options.centerCrop();
        RequestOptions requestOptions = RequestOptions
                .diskCacheStrategyOf(DiskCacheStrategy.ALL);
        Glide.with(context)
                .load(arrayList.get(holder.getAdapterPosition()).getAbsolutePath())
                .placeholder(R.drawable.ic_launcher_background)
                .apply(options)
                .dontAnimate()
                .apply(requestOptions)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
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
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            imageButton.setOnClickListener
                    (view -> videosFrag.ShowMenuDialog(arrayList.get(getAdapterPosition()).getAbsolutePath(),
                    getAdapterPosition()));
        }

        @Override
        public void onClick(View view) {

            final InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);

            Intent intent = new Intent(context, VideoPlayer.class);
            intent.putExtra("VideoPath", arrayList.get(getAdapterPosition()).getAbsolutePath());
            intent.putExtra("VideoName", arrayList.get(getAdapterPosition()).getName());
            context.startActivity(intent);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        @Override
        public boolean onLongClick(View view) {
            Toast.makeText(context, arrayList.get(getAdapterPosition()).getName(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void FilteredNames(ArrayList<File> filteredFiles)
    {
        arrayList = filteredFiles;
        notifyDataSetChanged();
    }
}
