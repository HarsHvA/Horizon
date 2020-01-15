package com.iceagestudios.horizon.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.iceagestudios.horizon.R;
import com.iceagestudios.horizon.VideoPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CaptionsAdapter extends RecyclerView.Adapter<CaptionsAdapter.Holder> {

    private ArrayList<File> arrayList;
    private ArrayList<File> subArrayList = new ArrayList<>();
    private Context mContext;
    VideoPlayer videoPlayer;

    public CaptionsAdapter(ArrayList<File> arrayList,Context mContext)
    {
        this.arrayList = arrayList;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.captions_files,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        holder.mText.setText(arrayList.get(position).getName());
        if(arrayList.get(position).isDirectory())
        {
            holder.mImage.setImageResource(R.drawable.folder_vector);
        }else if(arrayList.get(position).getName().endsWith("srt"))
        {
            holder.mImage.setImageResource(R.drawable.ic_subtitles_black_24dp);
        }else
        {
            holder.mImage.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        }
        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Toast.makeText(mContext, String.valueOf(holder.getAdapterPosition()), Toast.LENGTH_SHORT).show();
                if(arrayList !=null && arrayList.size()>0) {
                    File[] file = arrayList.get(holder.getAdapterPosition()).listFiles();
                    if (file != null && file.length > 0) {
                        arrayList.clear();
                        subArrayList.clear();
                        for (int i = 0; i < file.length; i++) {
                            subArrayList.add(file[i]);
                            Set<File> set = new HashSet<>(subArrayList);
                            arrayList.clear();
                            arrayList.addAll(set);
                            notifyDataSetChanged();
                            VideoPlayer.holderPosition = holder.getAdapterPosition();

                        }
                    }else
                    {
                        //
                        if(arrayList != null && arrayList.size()>0) {
                            if(arrayList.get(holder.getAdapterPosition()).getName().endsWith(".srt")) {
                                String path = arrayList.get(holder.getAdapterPosition()).getPath();
                                ((VideoPlayer) mContext).FetchSubtitles(path);
                            }else if(arrayList.get(holder.getAdapterPosition()).isDirectory())
                            {
                                arrayList.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();
                            }else
                            {
                                Toast.makeText(mContext, "Please choose a subtitle file!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder
    {
        CardView mCard;
        TextView mText;
        ImageView mImage;
        public Holder(@NonNull View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.captionCardView);
            mText = itemView.findViewById(R.id.captionText);
            mImage = itemView.findViewById(R.id.captionImg);
        }
    }
}

