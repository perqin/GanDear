package com.perqin.gandear.floatingwindow;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Author   : perqin
 * Date     : 17-4-25
 */

public class NewScreenshotHelper {
    private static final String TAG = "NewScreenshotHelper";
    private static final long WAIT_TIME_MILLIS = 10000;
    private Context mContext;
    private Handler mHandler;
    private OnNewScreenshotListener mListener;
    private boolean mEnabled;
    private final ContentObserver mObserver;

    public NewScreenshotHelper(Context context, Handler handler, OnNewScreenshotListener listener) {
        mContext = context;
        mHandler = handler;
        mListener = listener;
        mEnabled = false;
        mObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);

                if (!mEnabled) {
                    return;
                }

                Log.d(TAG, "onChange: URI = " + uri.toString());
                if (uri.toString().matches(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
                    Cursor cursor = null;
                    try {
                        cursor = mContext.getContentResolver().query(uri, new String[] {
                                MediaStore.Images.Media.DISPLAY_NAME,
                                MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DATE_ADDED
                        }, null, null, MediaStore.Images.Media.DATE_ADDED + " DESC");
                        if (cursor != null && cursor.moveToFirst()) {
                            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                            long dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                            long currentTime = System.currentTimeMillis() / 1000;
                            if (path.toLowerCase().contains("screenshot") && Math.abs(currentTime - dateAdded) <= WAIT_TIME_MILLIS / 1000) {
                                // screenshot added!
                                disable();
                                if (mListener != null) {
                                    mListener.onNewScreenshot(path);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "open cursor fail");
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            }
        };
    }

    public void onDestroy() {
        disable();
    }

    public void enable() {
        if (!mEnabled) {
            mEnabled = true;
            FloatingWindowServiceHelper.setStopDisabledFlag(true);
            mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mObserver);
            mHandler.postDelayed(this::disable, WAIT_TIME_MILLIS);
        }
    }

    private void disable() {
        if (mEnabled) {
            mEnabled = false;
            FloatingWindowServiceHelper.setStopDisabledFlag(false);
            mContext.getContentResolver().unregisterContentObserver(mObserver);
        }
    }

    public interface OnNewScreenshotListener {
        void onNewScreenshot(String path);
    }
}
