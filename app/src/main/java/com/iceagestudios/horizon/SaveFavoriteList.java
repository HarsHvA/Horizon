package com.iceagestudios.horizon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SaveFavoriteList {
    private Context context;
    private SharedPreferences sharedPreferences;

    public SaveFavoriteList(Context context)
    {
        this.context = context;
    }

    public void SaveArrayList()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = new HashSet<>(MainActivity.favoriteArrayList);
        sharedPreferences.edit().putStringSet("Favorite",set).apply();
    }

    public void RetriveArrayList()
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> newSet = sharedPreferences.getStringSet("Favorite",null);
        if(newSet!=null)
        {
            MainActivity.favoriteArrayList.clear();
            MainActivity.favoriteArrayList.addAll(newSet);
            Collections.reverse(MainActivity.favoriteArrayList);
        }
    }
}
