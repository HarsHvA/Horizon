package com.iceagestudios.horizon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    PagerAdapter(Context context, ArrayList<String> arrayList){

        this.arrayList = arrayList;
        this.context  = context;

    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        Glide.with(context)
                .load(arrayList.get(position))
                .placeholder(R.drawable.ic_launcher_background)
                .fitCenter()
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
