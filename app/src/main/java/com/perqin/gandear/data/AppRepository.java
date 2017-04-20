package com.perqin.gandear.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.perqin.gandear.data.models.Dungeon;
import com.perqin.gandear.data.models.Shishen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Author   : perqin
 * Date     : 17-4-7
 */

public class AppRepository {
    private static final String PK_GOAL_SHISHENS = "GOAL_SHISHENS";
    private static AppRepository sInstance;

    private SharedPreferences mSharedPreferences;
    private ArrayList<Shishen> mShishens;
    private HashMap<String, Shishen> mShishensMap;
    private ArrayList<Dungeon> mDungeons;
    private HashMap<String, ArrayList<Dungeon>> mShishenPresences;

    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppRepository(context);
        }
        return sInstance;
    }

    public void addGoalShishen(Shishen shishen) {
        Set<String> shishenSet = mSharedPreferences.getStringSet(PK_GOAL_SHISHENS, new HashSet<>());
        shishenSet.add(shishen.getId());
        mSharedPreferences.edit().putStringSet(PK_GOAL_SHISHENS, shishenSet).apply();
    }

    public void removeGoalShishen(Shishen shishen) {
        Set<String> shishenSet = mSharedPreferences.getStringSet(PK_GOAL_SHISHENS, new HashSet<>());
        shishenSet.remove(shishen.getId());
        mSharedPreferences.edit().putStringSet(PK_GOAL_SHISHENS, shishenSet).apply();
    }

    public ArrayList<Shishen> getGoalShishens() {
        Set<String> shishenSet = mSharedPreferences.getStringSet(PK_GOAL_SHISHENS, new HashSet<>());
        ArrayList<Shishen> shishens = new ArrayList<>();
        for (String id : shishenSet) {
            shishens.add(mShishensMap.get(id));
        }
        return shishens;
    }

    private AppRepository(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String shishensJson = readStringFromAssets(context, "shishens.json");
        mShishens = gson.fromJson(shishensJson, new TypeToken<ArrayList<Shishen>>(){}.getType());
        mShishensMap = new HashMap<>();
        for (Shishen shishen : mShishens) {
            mShishensMap.put(shishen.getId(), shishen);
        }
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

    public ArrayList<Shishen> queryShishens(String query) {
        ArrayList<Shishen> shishens = new ArrayList<>();
        for (Shishen shishen : mShishens) {
            for (String q : shishen.getQueries()) {
                if (q.startsWith(query)) {
                    shishens.add(shishen);
                    break;
                }
            }
        }
        return shishens;
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
