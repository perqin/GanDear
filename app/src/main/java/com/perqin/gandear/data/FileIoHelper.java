package com.perqin.gandear.data;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Author   : perqin
 * Date     : 17-4-26
 */

public class FileIoHelper {
    public static String readStringFromAssets(Context context, String filename) {
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

    public static void saveToFile(String data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data.getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
