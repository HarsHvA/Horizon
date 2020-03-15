package com.iceagestudios.horizon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MainActivity.favoriteArrayList);
        editor.putString("Favorite",json);
        editor.apply();

         */
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = new HashSet<>(MainActivity.favoriteArrayList);
        sharedPreferences.edit().putStringSet("Favorite",set).apply();
    }

    public void RetriveArrayList()
    {
        /*sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Favorite",null);
        Type type = new TypeToken<List<Video>>(){}.getType();
        if(json!=null) {
            MainActivity.favoriteArrayList.clear();
            MainActivity.favoriteArrayList = gson.fromJson(json, type);
            Collections.reverse(MainActivity.favoriteArrayList);
        }*/
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
