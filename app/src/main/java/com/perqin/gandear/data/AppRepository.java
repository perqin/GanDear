package com.perqin.gandear.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.perqin.gandear.data.models.Data;
import com.perqin.gandear.data.models.Dungeon;
import com.perqin.gandear.data.models.Shishen;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Author   : perqin
 * Date     : 17-4-7
 */

public class AppRepository {
    private static final String TAG = "AppRepository";
    private static final String PK_GOAL_SHISHENS = "GOAL_SHISHENS";
    private static final String PK_DATA_JSON_VERSION = "DATA_JSON_VERSION";
    private static final String PK_FLOATING_WINDOW_POS_X = "FLOATING_WINDOW_POS_X";
    private static final String PK_FLOATING_WINDOW_POS_Y = "FLOATING_WINDOW_POS_Y";

    private static AppRepository sInstance;

    private SharedPreferences mSharedPreferences;
    private ArrayList<Shishen> mShishens;
    private HashMap<String, Shishen> mShishensMap;
    private ArrayList<Dungeon> mDungeons;
    private HashMap<String, ArrayList<Dungeon>> mShishenPresences;
    private final File mDataJsonFile;
    private final ResourceService mResourceService;

    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppRepository(context);
        }
        return sInstance;
    }

    public void updateDataJsonFile() {
        mResourceService.getDataJsonFile().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Data data = new Gson().fromJson(response.body(), Data.class);
                    if (mSharedPreferences.getLong(PK_DATA_JSON_VERSION, 0) < data.version) {
                        // Update json file
                        updateLocalDataFile(response.body(), data.version);
                        reloadMemoryCache(data);
                    }
                    Log.d(TAG, "onResponse: Get latest version: " + data.version);
                } else {
                    Log.w(TAG, "onResponse: Failed to get data json file");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.e(TAG, "onFailure: ", throwable);
            }
        });
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

    private AppRepository(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        mDataJsonFile = new File(context.getFilesDir(), "data.json");
        mResourceService = new Retrofit.Builder()
                .baseUrl(ResourceService.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build().create(ResourceService.class);
        // Update local data with built-in asset
        String builtInDataJson = FileIoHelper.readStringFromAssets(context,"data.json");
        Data builtInData = new Gson().fromJson(builtInDataJson, Data.class);
        if (mSharedPreferences.getLong(PK_DATA_JSON_VERSION, 0) < builtInData.version) {
            updateLocalDataFile(builtInDataJson, builtInData.version);
        }
        reloadMemoryCache(builtInData);
    }

    private void updateLocalDataFile(String data, long newVersion) {
        FileIoHelper.saveToFile(data, mDataJsonFile);
        mSharedPreferences.edit().putLong(PK_DATA_JSON_VERSION, newVersion).apply();
    }

    private void reloadMemoryCache(Data data) {
        mShishens = data.shishens;
        mShishensMap = new HashMap<>();
        for (Shishen shishen : mShishens) {
            mShishensMap.put(shishen.getId(), shishen);
        }
        mDungeons = data.dungeons;
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

    public void readFloatingWindowPosition(int[] pos) {
        pos[0] = mSharedPreferences.getInt(PK_FLOATING_WINDOW_POS_X, 100);
        pos[1] = mSharedPreferences.getInt(PK_FLOATING_WINDOW_POS_Y, 100);
    }

    public void saveFloatingWindowPosition(int x, int y) {
        mSharedPreferences.edit()
                .putInt(PK_FLOATING_WINDOW_POS_X, x)
                .putInt(PK_FLOATING_WINDOW_POS_Y, y).apply();
    }
}
