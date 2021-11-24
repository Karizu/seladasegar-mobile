package com.selada.seladasegar.api;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.selada.seladasegar.util.PreferenceManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiCore {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    public ApiCore(Activity activity) {
        ApiCore.activity = activity;
    }

    public static <T> T builder(Class<T> endpoint) {
        new PreferenceManager(activity);
        return new Retrofit.Builder()
                .client(NetworkManager.client())
                .baseUrl(PreferenceManager.getBaseUrl().equals("")?ApiInterface.BASE_URL_SELADA_SEGAR_BANDUNG:PreferenceManager.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
