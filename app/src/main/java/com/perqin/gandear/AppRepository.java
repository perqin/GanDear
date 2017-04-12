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
        for (Shishen shishen : mShishens) {
            ArrayList<Dungeon> dungeons = new ArrayList<>();
            for (Dungeon dungeon : mDungeons) {
                boolean has = false;
                for (Dungeon.Round round : dungeon.getRounds()) {
                    if (round.getEnemies().containsKey(shishen.getId()) && round.getEnemies().get(shishen.getId()) > 0) {
                        has = true;
                        break;
                    }
                }
                if (has) {
                    dungeons.add(dungeon);
                }
            }
            sortDungeons(dungeons, shishen.getId());
            mShishenPresences.put(shishen.getId(), dungeons);
        }
    }

    private void sortDungeons(ArrayList<Dungeon> dungeons, String id) {
        int[] counts = new int[dungeons.size()];
        int tmp;
        Dungeon tmpD;
        for (int i = 0; i < dungeons.size(); ++i) {
            counts[i] = 0;
            for (Dungeon.Round round : dungeons.get(i).getRounds()) {
                if (round.getEnemies().containsKey(id)) {
                    counts[i] += round.getEnemies().get(id);
                }
            }
        }
        // Use selection sort
        for (int i = 0; i < counts.length - 1; ++i) {
            for (int j = i + 1; j < counts.length; ++j) {
                if (counts[i] < counts[j] || (counts[i] == counts[j] && dungeons.get(i).getSushi() > dungeons.get(j).getSushi())) {
                    tmp = counts[i];
                    counts[i] = counts[j];
                    counts[j] = tmp;
                    tmpD = dungeons.get(i);
                    dungeons.set(i, dungeons.get(j));
                    dungeons.set(j, tmpD);
                }
            }
        }
    }

    public ArrayList<Shishen> getShishens() {
        return mShishens;
    }

    public ArrayList<Dungeon> getShishenPresences(String id) {
        if (mShishenPresences.containsKey(id)) {
            return mShishenPresences.get(id);
        } else {
            return new ArrayList<>();
        }
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
