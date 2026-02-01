package com.project.shiensys;

import android.content.Context;
import android.os.Build;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    private static String getBaseUrl() {
        if (isEmulator()) {
            return "http://10.0.2.2/shienSys/api/";
        } else {
            return "http://192.168.137.1/shienSys/api/";
        }
    }

    public static Retrofit get(Context ctx) {
        if (retrofit == null) {

            HttpLoggingInterceptor log = new HttpLoggingInterceptor();
            log.setLevel(HttpLoggingInterceptor.Level.BODY);

            SessionManager session = new SessionManager(ctx.getApplicationContext());

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(session))
                    .addInterceptor(log)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.contains("generic")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for")
                || Build.MANUFACTURER.contains("Genymotion");
    }

    public static void reset() {
        retrofit = null;
    }
}
