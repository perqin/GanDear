package com.perqin.gandear.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Author   : perqin
 * Date     : 17-4-26
 */

public class OcrTask extends AsyncTask<Bitmap, Void, ArrayList<String>> {
    private static final String TAG = "OcrTask";

    private final String mDataPath;
    private OnOcrDoneListener mListener;

    public OcrTask(Context context, OnOcrDoneListener listener) {
        File file = new File(context.getFilesDir(), "tesseract");
        boolean mkdirs = file.mkdirs();
        if (!mkdirs) {
            Log.w(TAG, "OcrTask: Failed to mkdirs: " + file.getAbsolutePath());
        }
        mDataPath = file.getAbsolutePath();
        mListener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(Bitmap... bitmaps) {
        ArrayList<String> strings = new ArrayList<>();
        // TODO: Need trained data!
        TessBaseAPI api = new TessBaseAPI();
        api.init(mDataPath, "eng");
        for (Bitmap bitmap : bitmaps) {
            api.setImage(bitmap);
            api.setRectangle(0, 48, bitmap.getWidth(), 40);
            strings.add(api.getUTF8Text());
        }
        return strings;
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
        if (mListener != null) {
            mListener.onOcrDone(strings);
        }
    }

    public interface OnOcrDoneListener {
        void onOcrDone(List<String> strings);
    }
}
