package com.iceagestudios.horizon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SaveFavoriteList {
    private ArrayList<String> pathList = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    public void SaveArrayList(Context context, String url,boolean save)
    {
        RetriveArrayList(context);
        if(save)
        {
            pathList.add(url);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putStringSet("FavoriteList",set).apply();
        }
        else
        {
            pathList.remove(url);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Set<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putStringSet("FavoriteList",set).apply();
        }
    }

    public ArrayList<String> RetriveArrayList(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> newSet = sharedPreferences.getStringSet("FavoriteList",null);
        if(newSet!=null)
        {
            pathList.clear();
            pathList.addAll(newSet);
            Collections.reverse(pathList);
        }
        return pathList;
    }
}
