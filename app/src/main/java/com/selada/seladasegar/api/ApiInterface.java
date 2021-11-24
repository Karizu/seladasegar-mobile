package com.selada.seladasegar.api;

import com.selada.seladasegar.models.Slider;
import com.selada.seladasegar.models.request.RequestRegister;
import com.selada.seladasegar.models.response.ApiResponse;
import com.selada.seladasegar.models.HomeCategory;
import com.selada.seladasegar.models.HomeDataResponse;
import com.selada.seladasegar.models.StockResponse;
import com.selada.seladasegar.models.request.RequestOrder;
import com.selada.seladasegar.models.response.CouponResponse;
import com.selada.seladasegar.models.response.orders.OrderResponse;
import com.selada.seladasegar.models.response.register.ResponseRegister;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    String BASE_URL = "http://superfriends-api.ap-southeast-1.elasticbeanstalk.com/api/";
    String BASE_URL_CREATE_DEVICE_BANDUNG = "https://www.seladasegar.com/wp-admin/";
//    String BASE_URL_CREATE_DEVICE_BANDUNG = "https://bandung.seladasegar.com/wp-admin/";
    String BASE_URL_CREATE_DEVICE_BELITUNG = "https://belitung.seladasegar.com/wp-admin/";
    String BASE_URL_CREATE_DEVICE_CILEGON = "https://cilegon.seladasegar.com/wp-admin/";
    String BASE_URL_CREATE_DEVICE_BEKASI = "https://bekasi.seladasegar.com/wp-admin/";
    String BASE_URL_CREATE_DEVICE_TANGERANG = "https://tangerang.seladasegar.com/wp-admin/";
    String BASE_URL_CREATE_DEVICE_KARAWANG = "https://karawang.seladasegar.com/wp-admin/";
    String BASE_URL_CREATE_DEVICE_CIREBON = "https://cirebon.seladasegar.com/wp-admin/";

    String BASE_URL_SELADA_SEGAR = "https://www.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_BANDUNG = "https://www.seladasegar.com/wp-json/wc/";
//    String BASE_URL_SELADA_SEGAR_BANDUNG = "https://bandung.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_KARAWANG = "https://karawang.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_BELITUNG = "https://belitung.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_CILEGON = "https://cilegon.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_BEKASI = "https://bekasi.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_TANGERANG = "https://tangerang.seladasegar.com/wp-json/wc/";
    String BASE_URL_SELADA_SEGAR_CIREBON = "https://cirebon.seladasegar.com/wp-json/wc/";


    String BASE_URL_SLIDER_SELADA_SEGAR_BANDUNG = "https://www.seladasegar.com/wp-content/uploads";
    String BASE_URL_SLIDER_SELADA_SEGAR_KARAWANG = "https://karawang.seladasegar.com/wp-content/uploads";
    String BASE_URL_SLIDER_SELADA_SEGAR_BELITUNG = "https://belitung.seladasegar.com/wp-content/uploads";
    String BASE_URL_SLIDER_SELADA_SEGAR_CILEGON = "https://cilegon.seladasegar.com/wp-content/uploads";
    String BASE_URL_SLIDER_SELADA_SEGAR_BEKASI = "https://bekasi.seladasegar.com/wp-content/uploads";
    String BASE_URL_SLIDER_SELADA_SEGAR_TANGERANG = "https://tangerang.seladasegar.com/wp-content/uploads";
    String BASE_URL_SLIDER_SELADA_SEGAR_CIREBON = "https://cirebon.seladasegar.com/wp-content/uploads";

    String consumer_key_seladasegar = "ck_71dcb4eed4566bf18a42dc32188d6bb55882aee5";
    String consumer_secret_seladasegar = "cs_fba4304ffce1a95ed51f0aa92527df7786b2bdda";
    String consumer_key_seladasegarbandung = "ck_d04d96b66ab13a3d4cc2388e88966b6ea74ac9d5";
    String consumer_secret_seladasegarbandung = "cs_6601bed02e3e4ca88cd9beb6b397d758620b7d60";
    String consumer_key_seladasegarkarawang = "ck_5e2750d4472f3830c5c8f3504499345f7cd7a82b";
    String consumer_secret_seladasegarkarawang = "cs_bd0d2f06fe148e2f7c9fcaf3e4edde884eac4d45";
//    String consumer_key_seladasegarbandung = "ck_5e2750d4472f3830c5c8f3504499345f7cd7a82b";
//    String consumer_secret_seladasegarbandung = "cs_bd0d2f06fe148e2f7c9fcaf3e4edde884eac4d45";

    @GET("home")
    Call<ApiResponse<HomeDataResponse>> getHome(@Query("limit") int limit,
                                                @Query("offset") int offset,
                                                @Header("Authorization") String token);

    @GET("v3/products/categories")
    Call<List<HomeCategory>> getCategories(@Query("consumer_key") String consumer_key,
                                           @Query("consumer_secret") String consumer_secret);

    @GET("v3/products")
    Call<List<StockResponse>> getFeedHomeOne(@Query("per_page") String page,
                                             @Query("offset") String offset,
                                             @Query("status") String status,
                                             @Query("consumer_key") String consumer_key,
                                             @Query("consumer_secret") String consumer_secret);

    @GET("v3/products")
    Call<List<StockResponse>> getProductByCategory(@Query("category") int categoryId,
                                                   @Query("per_page") int limit,
                                                   @Query("offset") int page,
                                                   @Query("status") String status,
                                                   @Query("consumer_key") String consumer_key,
                                                   @Query("consumer_secret") String consumer_secret);

    @GET("v3/products/{id}")
    Call<StockResponse> getProductDetail(@Path("id") String id,
                                         @Query("status") String status,
                                         @Query("consumer_key") String consumer_key,
                                         @Query("consumer_secret") String consumer_secret);

    @GET("v3/products/{id}/variations")
    Call<List<StockResponse>> getProductVariations(@Path("id") String id,
                                                   @Query("per_page") int limit,
                                                   @Query("offset") int page,
                                                   @Query("status") String status,
                                                   @Query("consumer_key") String consumer_key,
                                                   @Query("consumer_secret") String consumer_secret);

    @GET("v3/products")
    Call<List<StockResponse>> getRelatedProduct(@Query("category") int categoryId,
                                                @Query("status") String status,
                                                @Query("consumer_key") String consumer_key,
                                                @Query("consumer_secret") String consumer_secret);

    @Headers("Content-Type: application/json")
    @POST("v3/customers")
    Call<ResponseRegister> doRegister(@Body RequestRegister requestRegister,
                                      @Query("consumer_key") String consumer_key,
                                      @Query("consumer_secret") String consumer_secret);

    @GET("v3/customers")
    Call<List<ResponseRegister>> findAccount(@Query("search") String email,
                                             @Query("consumer_key") String consumer_key,
                                             @Query("consumer_secret") String consumer_secret);

    @POST("v3/customers/{id}")
    Call<ResponseRegister> updateCustomer(@Path("id") String id,
                                          @Query("method") String method,
                                          @Body RequestRegister requestRegister,
                                          @Query("consumer_key") String consumer_key,
                                          @Query("consumer_secret") String consumer_secret);

    @FormUrlEncoded
    @POST("admin-ajax.php")
    Call<ResponseBody> sendDeviceId(@Field("user_id") String userId,
                                    @Field("device_id") String deviceId,
                                    @Field("action") String action);


    @Headers("Content-Type: application/json")
    @POST("v3/orders")
    Call<OrderResponse> createOrder(@Body RequestOrder requestOrder,
                                    @Query("consumer_key") String consumer_key,
                                    @Query("consumer_secret") String consumer_secret);

    @GET("v3/orders")
    Call<List<OrderResponse>> getOrders(@Query("customer") int customer_id,
                                    @Query("consumer_key") String consumer_key,
                                    @Query("consumer_secret") String consumer_secret);

    @GET("v3/coupons")
    Call<List<CouponResponse>> getCoupons(@Query("code") String code,
                                         @Query("consumer_key") String consumer_key,
                                         @Query("consumer_secret") String consumer_secret);

    @POST("v3/orders/{id}")
    Call<OrderResponse> getDetailOrder(@Path("id") String id,
                                       @Query("consumer_key") String consumer_key,
                                       @Query("consumer_secret") String consumer_secret);

    @GET("admin-ajax.php")
    Call<List<Slider>> getSlider(@Query("action") String action);
}
