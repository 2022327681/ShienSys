package com.project.shiensys;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class AppContext extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    @Override public void onCreate() { super.onCreate(); ctx = getApplicationContext(); }
    public static Context get() { return ctx; }
}
