package com.iceagestudios.horizon;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
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
            Gson gson = new Gson();
            String json = gson.toJson(pathList);
           // LinkedHashSet<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putString("History1",json).apply();

        }
        else
        {
            pathList.remove(url);
           /* sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            LinkedHashSet<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putStringSet("History",set).apply();

            */
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            Gson gson = new Gson();
            String json = gson.toJson(pathList);
            // LinkedHashSet<String> set = new LinkedHashSet<>(pathList);
            sharedPreferences.edit().putString("History1",json).apply();
        }

    }

    public ArrayList<String> FetchHistory(Context context)
    {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json =  sharedPreferences.getString("History1",null);
        Gson gson = new Gson();
        if(json!=null)
        {
            pathList.clear();
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            pathList = gson.fromJson(json,type);
            LinkedHashSet<String> set = new LinkedHashSet<>(pathList);
            pathList.clear();
            pathList.addAll(set);
            Collections.reverse(pathList);
        }
        return pathList;
    }
}
