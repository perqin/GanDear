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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
    private static final String PK_TRAINEDDATA_VERSION = "TRAINEDDATA_VERSION";

    private static AppRepository sInstance;

    private SharedPreferences mSharedPreferences;
    private ArrayList<Shishen> mShishens;
    private HashMap<String, Shishen> mShishensMap;
    private ArrayList<Dungeon> mDungeons;
    private HashMap<String, ArrayList<Dungeon>> mShishenPresences;
    private final ResourceService mResourceService;

    // File that used
    private final File mDataJsonFile;
    private final File mTesseractDirFile;
    private final File mTraineddataFile;

    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppRepository(context);
        }
        return sInstance;
    }

    /**
     * Init the data files if needed
     * @param context Context instance for accessing assets
     */
    public void init(Context context) {
        if (!mSharedPreferences.contains(PK_DATA_JSON_VERSION)) {
            updateLocalDataFile(context);
        } else {
            loadLocalDataFile(context);
        }
        if (!mSharedPreferences.contains(PK_TRAINEDDATA_VERSION)) {
            updateLocalTraineddataFile(context);
        }
    }

    /**
     * Check update of data.json
     * @return Observable which emits the latest version, or -1 if no update
     */
    public Observable<Long> checkDataJsonUpdate() {
        final long oldVersion = mSharedPreferences.getLong(PK_DATA_JSON_VERSION, 0);
        return mResourceService.getVersionJsonFile()
                .map(version -> oldVersion < version.data ? version.data : -1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Check update of chi_sim.traineddata
     * @return Observable which emits the latest version, or -1 if no update
     */
    public Observable<Long> checkTraineddataUpdate() {
        final long oldVersion = mSharedPreferences.getLong(PK_TRAINEDDATA_VERSION, 0);
        return mResourceService.getVersionJsonFile()
                .map(version -> oldVersion < version.traineddata ? version.traineddata : -1)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Update latest data.json file
     */
    public Observable<Data> updateLatestDataJson(long newVersion) {
        return mResourceService.getDataJsonFile()
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .map(s -> {
                    updateLocalDataFile(s, newVersion);
                    return new Gson().fromJson(s, Data.class);
                });
    }

    /**
     * Update latest chi_sim.traineddata file
     */
    public void updateLatestTrainedData() {
        // TODO
    }

//    public void updateDataJsonFile() {
//        mResourceService.getVersionJsonFile().enqueue(new Callback<Version>() {
//            @Override
//            public void onResponse(Call<Version> call, Response<Version> response) {
//                // TODO: Implement onResponse
//                throw new UnsupportedOperationException("Method not implemented");
//            }
//
//            @Override
//            public void onFailure(Call<Version> call, Throwable throwable) {
////                Log.w(TAG, "onFailure: Failed to get ", );
//            }
//        });
//        mResourceService.getDataJsonFile().enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.isSuccessful()) {
//                    Data data = new Gson().fromJson(response.body(), Data.class);
//                    if (mSharedPreferences.getLong(PK_DATA_JSON_VERSION, 0) < data.version) {
//                        // Update json file
//                        updateLocalDataFile(response.body(), data.version);
//                        reloadMemoryCache(data);
//                    }
//                    Log.d(TAG, "onResponse: Get latest version: " + data.version);
//                } else {
//                    Log.w(TAG, "onResponse: Failed to get data json file");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable throwable) {
//                Log.e(TAG, "onFailure: ", throwable);
//            }
//        });
//        mResourceService.getVersionJsonFile().observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(version -> {
//             For data.json
//
//        })
//    }

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

    public void readFloatingWindowPosition(int[] pos) {
        pos[0] = mSharedPreferences.getInt(PK_FLOATING_WINDOW_POS_X, 100);
        pos[1] = mSharedPreferences.getInt(PK_FLOATING_WINDOW_POS_Y, 100);
    }

    public void saveFloatingWindowPosition(int x, int y) {
        mSharedPreferences.edit()
                .putInt(PK_FLOATING_WINDOW_POS_X, x)
                .putInt(PK_FLOATING_WINDOW_POS_Y, y).apply();
    }

    private AppRepository(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());

        final File dataDirFile = context.getApplicationContext().getFilesDir();
        mDataJsonFile = new File(dataDirFile, "data.json");
        mTesseractDirFile = new File(dataDirFile, "tesseract");
        if (!new File(mTesseractDirFile, "tessdata").mkdirs()) {
            Log.w(TAG, "AppRepository: Failed to create directory: " + new File(mTesseractDirFile, "tessdata").getAbsolutePath());
        }
        mTraineddataFile = new File(mTesseractDirFile, "tessdata/chi_sim.traineddata");

        mResourceService = new Retrofit.Builder()
                .baseUrl(ResourceService.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ResourceService.class);
    }

    /**
     * Load local data.json from file into memory
     */
    private void loadLocalDataFile(Context context) {
        final String json = FileIoHelper.readStringFromFile(mDataJsonFile);
        reloadMemoryCache(new Gson().fromJson(json, Data.class));
    }

    /**
     * Update local data.json from assets
     * @param context Context for accessing assets
     */
    private void updateLocalDataFile(Context context) {
        String builtInDataJson = FileIoHelper.readStringFromAssets(context,"data.json");
        long version = new Gson().fromJson(builtInDataJson, Data.class).version;
        updateLocalDataFile(builtInDataJson, version);
    }

    /**
     * Update local data.json from latest data and reload memory
     * @param data Latest JSON string data
     * @param newVersion The version of the data
     */
    private void updateLocalDataFile(String data, long newVersion) {
        FileIoHelper.saveToFile(data, mDataJsonFile);
        mSharedPreferences.edit().putLong(PK_DATA_JSON_VERSION, newVersion).apply();
        reloadMemoryCache(new Gson().fromJson(data, Data.class));
    }

    /**
     * Update local chi_sim.traineddata from assets
     * @param context Context for accessing assets
     */
    private void updateLocalTraineddataFile(Context context) {
        Log.d(TAG, "updateLocalTraineddataFile: " + mTraineddataFile.getAbsolutePath());
        FileIoHelper.copyFileFromAssets(context, "chi_sim.traineddata", mTraineddataFile);
        // Since no data is here, we use a fix version
        mSharedPreferences.edit().putLong(PK_TRAINEDDATA_VERSION, 1495619382454L).apply();
    }

    /**
     * Update local chi_sim.traineddata from latest data
     * @param context Context for getting dir
     * @param newFile Latest downloaded file
     * @param newVersion The version of the data file
     */
    private void updateLocalTraineddataFile(Context context, File newFile, long newVersion) {
        if (!mTraineddataFile.delete()) {
            Log.w(TAG, "updateLocalTraineddataFile: Failed to remove old chi_sim.traineddata");
            return;
        }
        if (!newFile.renameTo(mTraineddataFile)) {
            Log.w(TAG, "updateLocalTraineddataFile: Failed to rename new file to chi_sim.traineddata");
            return;
        }
        mSharedPreferences.edit().putLong(PK_TRAINEDDATA_VERSION, newVersion).apply();
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
        Log.d(TAG, "reloadMemoryCache: MemoryCache reloaded");
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
}
