package com.selada.seladasegar.api;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.selada.seladasegar.util.PreferenceManager;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiAjax {

    @SuppressLint("StaticFieldLeak")
    public static Activity activity;

    public ApiAjax(Activity activity) {
        ApiCore.activity = activity;
    }

    private static <T> T builder(Class<T> endpoint) {
        return new Retrofit.Builder()
                .client(NetworkManager.client())
                .baseUrl(PreferenceManager.getBaseUrlCreateDevice().equals("")?ApiInterface.BASE_URL_CREATE_DEVICE_BANDUNG:PreferenceManager.getBaseUrlCreateDevice())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(endpoint);
    }

    public static ApiInterface apiInterface() {
        return builder(ApiInterface.class);
    }

}
