package com.perqin.gandear;

import android.app.Application;

import com.perqin.gandear.data.AppRepository;

/**
 * Author: perqin
 * Date  : 5/21/17
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppRepository.getInstance(this).init(this);
    }
}
