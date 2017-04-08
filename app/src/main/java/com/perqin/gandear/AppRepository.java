package com.perqin.gandear;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author   : perqin
 * Date     : 17-4-7
 */

public class AppRepository {
    private static AppRepository sInstance;

    private ArrayList<Shishen> mShishens;
    private ArrayList<Dungeon> mDungeons;
    private HashMap<String, ArrayList<Dungeon>> mShishenPresences;

    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppRepository(context);
        }
        return sInstance;
    }

    private AppRepository(Context context) {
        Gson gson = new Gson();
        String shishensJson = readStringFromAssets(context, "shishens.json");
        mShishens = gson.fromJson(shishensJson, new TypeToken<ArrayList<Shishen>>(){}.getType());
        String dungeonsJson = readStringFromAssets(context, "dungeons.json");
        mDungeons = gson.fromJson(dungeonsJson, new TypeToken<ArrayList<Dungeon>>(){}.getType());
        mShishenPresences = new HashMap<>();
        // TODO: Generate mShishenPresences
    }

    public ArrayList<Shishen> getShishens() {
        return mShishens;
    }

    public ArrayList<Dungeon> getShishenPresences(String id) {
        return mShishenPresences.get(id);
    }

    private String readStringFromAssets(Context context, String filename) {
        String json = "";
        try {
            InputStream inputStream = context.getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            int readCount = inputStream.read(buffer);
            inputStream.close();
            if (readCount != -1) {
                json = new String(buffer, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}
