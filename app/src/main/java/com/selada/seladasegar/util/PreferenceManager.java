package com.selada.seladasegar.util;

import android.content.Context;

import com.orhanobut.hawk.Hawk;
import com.selada.seladasegar.api.ApiInterface;
import com.selada.seladasegar.models.HomeCategory;
import com.selada.seladasegar.models.KatalogModel;
import com.selada.seladasegar.models.response.register.ResponseRegister;

import java.util.List;

/**
 * Created by Dhimas on 10/9/17.
 */

public class PreferenceManager {

    private static final String SESSION_TOKEN = "sessionToken";
    private static final String SESSION_TOKEN_ARDI = "sessionTokenArdi";
    private static final String IS_LOGIN = "isLogin";
    private static final String IS_ARDI = "isARDI";
    private static final String USER_LOGIN = "userLogin";
    private static final String AGENT = "agent";
    private static final String MEMBER_ID = "memberId";
    private static final String SELADA_USER_ID = "seladaUserId";

    private static final String BASE_URL = "baseUrl";
    private static final String CONSUMER_KEY = "consumerKey";
    private static final String CONSUMER_SECRET = "consumerSecret";
    private static final String CODE_COUPON = "codeCoupon";

    private static final String BOOTH_ID = "booth_id";
    private static final String MASTER_KEY = "master_key";
    private static final String MENU_CATEGORY = "menu_category";
    private static final String HOME_FEED_ONE = "home_feed_one";
    private static final String HOME_FEED_TWO = "home_feed_two";
    private static final String HOME_FEED_THREE = "home_feed_three";
    private static final String NAMA_CABANG = "namaCabang";
    private static final String IS_KOPERASI = "isKoperasi";
    private static final String LOGIN_DATA = "loginData";
    private static final String LOGIN_EMAIL = "loginEmail";
    private static final String PHONE_SELADA_SEGAR = "phoneSeladaSegar";
    private static final String BASE_URL_CREATE_DEVICE = "baseUrlCreateDevice";
    private static final String IS_FIRST_OPEN = "isFirstOpen";
    private static final String IS_FIREBASE_USER = "isFirebaseUser";
    private static final String BASE_URL_SLIDER = "baseUrlSlider";

    private static Context ctx;
    private static PreferenceManager mInstance;

    public PreferenceManager(Context context) {
//        Hawk.init(context)
//                .setEncryptionMethod(HawkBuilder.EncryptionMethod.HIGHEST)
//                .setStorage(HawkBuilder.newSharedPrefStorage(context))
//                .setPassword("P@ssw0rd123")
//                .build();
        Hawk.init(context).build();
    }

    public static synchronized PreferenceManager getInstance(Context context){
        if (mInstance == null)
            mInstance = new PreferenceManager(context);
        return mInstance;
    }

    public static void setIsKoperasi(boolean isKoperasi){
        Hawk.put(IS_KOPERASI, isKoperasi);
    }

    public static boolean getIsKoperasi() {
        return Hawk.get(IS_KOPERASI, false);
    }

    public static void setNamaCabang(String namaCabang){
        Hawk.put(NAMA_CABANG, namaCabang);
    }

    public static String getNamaCabang() {
        return Hawk.get(NAMA_CABANG, "KOTA BANDUNG");
    }

    public static void setBaseUrl(String baseUrl){
        Hawk.put(BASE_URL, baseUrl);
    }

    public static String getBaseUrl() {
        return Hawk.get(BASE_URL, ApiInterface.BASE_URL_SELADA_SEGAR_BANDUNG);
    }

    public static void setBaseUrlCreateDevice(String baseUrl){
        Hawk.put(BASE_URL_CREATE_DEVICE, baseUrl);
    }

    public static String getBaseUrlCreateDevice() {
        return Hawk.get(BASE_URL_CREATE_DEVICE, ApiInterface.BASE_URL_CREATE_DEVICE_BANDUNG);
    }

    public static void setBaseUrlSlider(String baseUrl){
        Hawk.put(BASE_URL_SLIDER, baseUrl);
    }

    public static String getBaseUrlSlider() {
        return Hawk.get(BASE_URL_SLIDER, ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BANDUNG);
    }

    public static void setConsumerKey(String consumerKey){
        Hawk.put(CONSUMER_KEY, consumerKey);
    }

    public static String getConsumerKey() {
        return Hawk.get(CONSUMER_KEY, ApiInterface.consumer_key_seladasegarbandung);
    }

    public static void setConsumerSecret(String consumerSecret){
        Hawk.put(CONSUMER_SECRET, consumerSecret);
    }

    public static String getConsumerSecret() {
        return Hawk.get(CONSUMER_SECRET, ApiInterface.consumer_secret_seladasegarbandung);
    }

    public static void setCodeCoupon(String codeCoupon){
        Hawk.put(CODE_COUPON, codeCoupon);
    }

    public static String getCodeCoupon() {
        return Hawk.get(CODE_COUPON, "");
    }

    public static void setMenuCategory(List<HomeCategory> list){
        Hawk.put(MENU_CATEGORY, list);
    }

    public static List<HomeCategory> getMenuCategory(){
        return Hawk.get(MENU_CATEGORY, null);
    }

    public static void setHomeFeedOne(List<KatalogModel> list){
        Hawk.put(HOME_FEED_ONE, list);
    }

    public static List<KatalogModel> getHomeFeedOne(){
        return Hawk.get(HOME_FEED_ONE, null);
    }


    public static String getSessionToken() {
        return Hawk.get(SESSION_TOKEN, "");
    }

    public static void logOut() {
        //Hawk.put(USER_LOGIN, null);
        Hawk.put(IS_LOGIN, false);
        Hawk.deleteAll();
    }

    public static void clearHomeData() {
        Hawk.delete(MENU_CATEGORY);
        Hawk.delete(HOME_FEED_ONE);
        Hawk.delete(HOME_FEED_TWO);
        Hawk.delete(HOME_FEED_THREE);
    }

    public static boolean getIsLogin() {
        return Hawk.get(IS_LOGIN, false);
    }

    public static boolean getFirstOpen() {
        return Hawk.get(IS_FIRST_OPEN, true);
    }


    public static boolean setFirstOpenFalse() {
        return Hawk.put(IS_FIRST_OPEN, false);
    }

    public static boolean isFirebaseUser() {
        return Hawk.get(IS_FIREBASE_USER, false);
    }


    public static boolean setFirebaseUser(boolean isFirebase) {
        return Hawk.put(IS_FIREBASE_USER, isFirebase);
    }

    public static void setLoginData(ResponseRegister requestRegister){
        Hawk.put(IS_LOGIN, true);
        Hawk.put(LOGIN_DATA, requestRegister);
    }

    public static ResponseRegister getLoginData(){
        return Hawk.get(LOGIN_DATA, null);
    }

    public static void setPhoneSeladaSegar(String phoneSeladaSegar){
        Hawk.put(PHONE_SELADA_SEGAR, phoneSeladaSegar);
    }

    public static String getPhoneSeladaSegar() {
        return Hawk.get(PHONE_SELADA_SEGAR, Constant.PHONE_SELADA_SEGAR_BANDUNG);
    }

}
