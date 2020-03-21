package com.iceagestudios.horizon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class History {
 private ArrayList<String> pathList = new ArrayList<>();
 private Context context;
 private SharedPreferences sharedPreferences;
    public void SaveHistory(Context context,String url,boolean save)
    {
        FetchHistory(context);
        if(save)
        {
            pathList.add(url);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            LinkedHashSet<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putStringSet("History",set).apply();

        }
        else
        {
            pathList.remove(url);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            LinkedHashSet<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putStringSet("History",set).apply();
        }

    }

    public ArrayList<String> FetchHistory(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> newSet =  sharedPreferences.getStringSet("History",null);
        if(newSet!=null)
        {
            pathList.clear();
            pathList.addAll(newSet);
            Collections.reverse(pathList);
        }
        return pathList;
    }
}
