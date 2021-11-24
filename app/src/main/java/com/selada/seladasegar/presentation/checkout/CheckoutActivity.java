package com.selada.seladasegar.presentation.checkout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.models.request.Billing;
import com.selada.seladasegar.models.request.LineItem;
import com.selada.seladasegar.models.request.MetaData;
import com.selada.seladasegar.models.request.RequestOrder;
import com.selada.seladasegar.models.request.RequestRegister;
import com.selada.seladasegar.models.request.Shipping;
import com.selada.seladasegar.models.request.ShippingLine;
import com.selada.seladasegar.models.response.CouponResponse;
import com.selada.seladasegar.models.response.orders.CouponLines;
import com.selada.seladasegar.models.response.orders.OrderResponse;
import com.selada.seladasegar.models.response.register.ResponseRegister;
import com.selada.seladasegar.presentation.maps.MapsActivity;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.Loading;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.selada.seladasegar.util.Constant.COUNTRY_CODE;

public class CheckoutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.layoutBuatAkun)
    LinearLayout layoutBuatAkun;
    @BindView(R.id.frameCOD)
    LinearLayout frameCOD;
    @BindView(R.id.frameTransferBank)
    LinearLayout frameTransferBank;
    @BindView(R.id.frameQris)
    LinearLayout frameQris;
    @BindView(android.R.id.content)
    ViewGroup v;
    @BindView(R.id.checkboxSetDefaultAddress)
    CheckBox checkboxSetDefaultAddress;
    @BindView(R.id.checkboxCOD)
    CheckBox checkboxCOD;
    @BindView(R.id.checkboxTransferBank)
    CheckBox checkboxTransferBank;
    @BindView(R.id.checkboxQris)
    CheckBox checkboxQris;
    @BindView(R.id.rvItemCheckout)
    RecyclerView rvItemCheckout;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.tvSubTotal)
    TextView tvSubTotal;
    @BindView(R.id.tvOngkir)
    TextView tvOngkir;
    @BindView(R.id.tvKodeUnik)
    TextView tvKodeUnik;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvCOD)
    TextView tvCOD;
    @BindView(R.id.tvTransferBank)
    TextView tvTransferBank;
    @BindView(R.id.tvQris)
    TextView tvQris;
    @BindView(R.id.etKodePromo)
    EditText etKodePromo;
    @BindView(R.id.etNama)
    EditText etNama;

    @BindView(R.id.layout_bekasi)
    LinearLayout layout_bekasi;

    @BindView(R.id.etNIK)
    EditText etNIK;

    @BindView(R.id.etNomorTelepon)
    EditText etNomorTelepon;
    @BindView(R.id.etAlamat)
    EditText etAlamat;
    @BindView(R.id.etNote)
    EditText etNote;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.tvKodePromo)
    TextView tvKodePromo;
    @BindView(R.id.frameKodePromo)
    FrameLayout frameKodePromo;
    @BindView(R.id.tvLoginDisini)
    TextView tvLoginDisini;
    @BindView(R.id.tvTitlePromo)
    TextView tvTitlePromo;
    @BindView(R.id.layoutLineLogin)
    LinearLayout layoutLineLogin;
    @BindView(R.id.layoutTotal)
    LinearLayout layoutTotal;
    @BindView(R.id.layoutCouponTerpakai)
    LinearLayout layoutCouponTerpakai;
    @BindView(R.id.tvCouponName)
    TextView tvCouponName;

    private String code = "";

    @OnClick(R.id.tvLoginDisini)
    void onClickLoginDisini(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isLoginFromCheckout", true);
        startActivity(intent);
    }

    @OnClick(R.id.tvKodePromo)
    void onClickKodePromo(){
        MethodUtil.toggleTransitionSlideStart(viewGroup, frameKodePromo, true);
    }

    @OnClick(R.id.btnTandaiLokasi)
    void onClickbtnTandaiLokasi() {
        Intent i = new Intent(this, MapsActivity.class);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }

    @OnClick(R.id.btnTerapkanKode)
    void onClickTerapkanKode() {

        if (PreferenceManager.getCodeCoupon().equals("")){
            code = etKodePromo.getText().toString().toLowerCase();
        } else {
            code = PreferenceManager.getCodeCoupon().toLowerCase();
        }

        ProgressDialog dialog = MethodUtil.getLoadingBar(context, "Mohon tunggu");
        ApiCore.apiInterface().getCoupons(code, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<CouponResponse>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<CouponResponse>> call, Response<List<CouponResponse>> response) {
                try {
                    if (response.isSuccessful()){
                        dialog.dismiss();
                        List<CouponResponse> responses = response.body();
                        if (responses!=null){
                            if (responses.size() > 0){
                                couponLines = new CouponLines();
                                List<MetaData> metaDataList = new ArrayList<>();
                                MetaData metaData = new MetaData();
                                metaDataList.add(metaData);
                                for (CouponResponse couponResponse: responses){
                                    couponLines.setCode(couponResponse.getCode());
                                    couponLines.setDiscount_tax("0");
                                    couponLines.setMeta_data(metaDataList);

                                    String dateExpired;
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date strDate = null;
                                    
                                    if (couponResponse.getDateExpires()!=null){
                                        dateExpired = couponResponse.getDateExpires().replace("T", " ");
                                        strDate = sdf.parse(dateExpired);
                                    } else {
                                        dateExpired = "null";
                                    }

                                    int maxAmount = couponResponse.getMaximumAmount()!=null?Integer.parseInt(couponResponse.getMaximumAmount().replace(".00", "")):0;
                                    int minAmount = couponResponse.getMinimumAmount()!=null?Integer.parseInt(couponResponse.getMinimumAmount().replace(".00", "")):0;
                                    int usageLimit = couponResponse.getUsageLimit()!=null?couponResponse.getUsageLimit():0;
                                    int usegeCount = couponResponse.getUsageCount()!=null?couponResponse.getUsageCount():0;
                                    int usageLimitUser = couponResponse.getUsageLimitPerUser()!=null?couponResponse.getUsageLimitPerUser():0;
                                    List<String> userList = couponResponse.getUsed_by();
                                    ResponseRegister responseRegister = PreferenceManager.getLoginData();

                                    int count = 0;
                                    if (usageLimitUser > 0 ) {
                                        for (String val: userList){
                                            if (val.equals(responseRegister.getEmail())){
                                                count++;
                                                if (count > usageLimitUser) {
                                                    Toast.makeText(context, "Limit penggunaan kupon telah habis", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                            }
                                        }
                                    }

                                    if (usageLimit > 0 && usegeCount >= usageLimit){
                                        Toast.makeText(context, "Limit penggunaan kupon telah habis", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    switch (couponResponse.getDiscountType()) {
                                        case Constant.DISCOUNT_TYPE_FIXED_CART: {
                                            if(gPrice >= minAmount){
                                                if (maxAmount == 0 || (maxAmount > 0 && gPrice <= maxAmount)){
                                                    if (!dateExpired.equals("null")){
                                                        if (new Date().after(strDate)) {
                                                            Toast.makeText(context, "Kupon telah kadaluarsa", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }
                                                    }

                                                    boolean isProductReady = false;

                                                    if (couponResponse.getProductIds().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getProductIds()) {
                                                                if (idFromCoupon == Integer.parseInt(carts.itemId)) {
                                                                    isProductReady = true;
                                                                }
                                                            }
                                                        }
                                                    } else if (couponResponse.getProductCategories().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getProductCategories()) {
                                                                if (carts.itemCategoryId.contains("-")) {
                                                                    String[] itemCategoryIdList = carts.itemCategoryId.split("-");
                                                                    for (String itemCategoryId : itemCategoryIdList) {
                                                                        if (idFromCoupon == Integer.parseInt(itemCategoryId)) {
                                                                            isProductReady = true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if (couponResponse.getExcludedProductIds().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getExcludedProductIds()) {
                                                                if (idFromCoupon == Integer.parseInt(carts.itemId)) {
                                                                    isProductReady = false;
                                                                }
                                                            }
                                                        }
                                                    } else if (couponResponse.getExcludedProductCategories().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getExcludedProductCategories()) {
                                                                if (carts.itemCategoryId.contains("-")) {
                                                                    String[] itemCategoryIdList = carts.itemCategoryId.split("-");
                                                                    for (String itemCategoryId : itemCategoryIdList) {
                                                                        if (idFromCoupon == Integer.parseInt(itemCategoryId)) {
                                                                            isProductReady = false;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        isProductReady = true;
                                                    }

                                                    if (isProductReady){
                                                        couponLines.setDiscount(couponResponse.getAmount().replace(".00", ""));
                                                        amountCoupon = Double.parseDouble(couponResponse.getAmount().replace(".00", ""));
                                                        long amount = Long.parseLong(couponResponse.getAmount().replace(".00", ""));
                                                        tvTitlePromo.setText("Promo " + couponResponse.getCode().toUpperCase());
                                                        tvKodeUnik.setText("-Rp " + MethodUtil.toCurrencyFormat(String.valueOf(amount)));
                                                        populateData();
                                                        layoutTotal.requestFocus();
                                                        isCouponReady = true;
                                                        tvCouponName.setText("Kupon " + couponResponse.getCode().toUpperCase() + " terpasang");
                                                        Toast.makeText(context, "Kode promo berhasil digunakan", Toast.LENGTH_SHORT).show();
                                                        layoutCouponTerpakai.setVisibility(View.VISIBLE);
                                                    } else {
                                                        Toast.makeText(context, "Kupon tidak bisa digunakan pada produk tersebut", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Maksimal pembelanjaan Rp "+MethodUtil.toCurrencyFormat(String.valueOf(maxAmount)), Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Minimal pembelanjaan Rp "+MethodUtil.toCurrencyFormat(String.valueOf(minAmount)), Toast.LENGTH_SHORT).show();
                                            }

                                            break;
                                        }
                                        case Constant.DISCOUNT_TYPE_PERCENT: {

                                            if(gPrice >= minAmount){
                                                if (maxAmount == 0 || (maxAmount > 0 && gPrice <= maxAmount)){
                                                    if (!dateExpired.equals("null")){
                                                        if (new Date().after(strDate)) {
                                                            Toast.makeText(context, "Kupon telah kadaluarsa", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }
                                                    }

                                                    boolean isProductReady = false;

                                                    if (couponResponse.getProductIds().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getProductIds()) {
                                                                if (idFromCoupon == Integer.parseInt(carts.itemId)) {
                                                                    isProductReady = true;
                                                                }
                                                            }
                                                        }
                                                    } else if (couponResponse.getProductCategories().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getProductCategories()) {
                                                                if (carts.itemCategoryId.contains("-")) {
                                                                    String[] itemCategoryIdList = carts.itemCategoryId.split("-");
                                                                    for (String itemCategoryId : itemCategoryIdList) {
                                                                        if (idFromCoupon == Integer.parseInt(itemCategoryId)) {
                                                                            isProductReady = true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else if (couponResponse.getExcludedProductIds().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getExcludedProductIds()) {
                                                                if (idFromCoupon == Integer.parseInt(carts.itemId)) {
                                                                    isProductReady = false;
                                                                }
                                                            }
                                                        }
                                                    } else if (couponResponse.getExcludedProductCategories().size() > 0) {
                                                        for (Carts carts : cartModelList) {
                                                            for (Integer idFromCoupon : couponResponse.getExcludedProductCategories()) {
                                                                if (carts.itemCategoryId.contains("-")) {
                                                                    String[] itemCategoryIdList = carts.itemCategoryId.split("-");
                                                                    for (String itemCategoryId : itemCategoryIdList) {
                                                                        if (idFromCoupon == Integer.parseInt(itemCategoryId)) {
                                                                            isProductReady = false;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    else {
                                                        isProductReady = true;
                                                    }

                                                    if (isProductReady){
                                                        double res = Double.parseDouble(couponResponse.getAmount().replace(".00", ""));
                                                        double amount = (gPrice / 100.0f) * res;
                                                        Log.d("AMOUNT", String.valueOf(amount));
                                                        couponLines.setDiscount(String.valueOf(amount).replace(".0", ""));
                                                        amountCoupon = amount;
                                                        tvTitlePromo.setText("Promo " + couponResponse.getCode().toUpperCase());
                                                        tvKodeUnik.setText("-Rp " + MethodUtil.toCurrencyFormat(String.valueOf(amount).replace(".0", "")));
                                                        populateData();
                                                        layoutTotal.requestFocus();
                                                        isCouponReady = true;
                                                        tvCouponName.setText("Kupon " + couponResponse.getCode().toUpperCase() + " terpasang");
                                                        Toast.makeText(context, "Kode promo berhasil digunakan", Toast.LENGTH_SHORT).show();
                                                        layoutCouponTerpakai.setVisibility(View.VISIBLE);
                                                    } else {
                                                        Toast.makeText(context, "Kupon tidak bisa digunakan pada produk tersebut", Toast.LENGTH_SHORT).show();
                                                    }

                                                } else {
                                                    Toast.makeText(context, "Maksimal pembelanjaan Rp "+MethodUtil.toCurrencyFormat(String.valueOf(maxAmount)), Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(context, "Minimal pembelanjaan Rp "+MethodUtil.toCurrencyFormat(String.valueOf(minAmount)), Toast.LENGTH_SHORT).show();
                                            }

                                            break;
                                        }
                                        case Constant.DISCOUNT_TYPE_FIXED_PRODUCT_CART: {
                                            int amount = 0;
                                            int amountFromCoupon = Integer.parseInt(couponResponse.getAmount().replace(".00", ""));
                                            try {

                                                if(gPrice >= minAmount){
                                                    if (maxAmount == 0 || (maxAmount > 0 && gPrice <= maxAmount)){
                                                        if (!dateExpired.equals("null")){
                                                            if (new Date().after(strDate)) {
                                                                Toast.makeText(context, "Kupon telah kadaluarsa", Toast.LENGTH_SHORT).show();
                                                                break;
                                                            }
                                                        }

                                                        if (couponResponse.getProductCategories().size() > 0) {
                                                            for (Carts carts : cartModelList) {
                                                                for (Integer idFromCoupon : couponResponse.getProductCategories()) {
                                                                    if (carts.itemCategoryId.contains("-")) {
                                                                        String[] itemCategoryIdList = carts.itemCategoryId.split("-");
                                                                        for (String itemCategoryId : itemCategoryIdList) {
                                                                            if (idFromCoupon == Integer.parseInt(itemCategoryId)) {
                                                                                amount += amountFromCoupon;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        } else if (couponResponse.getProductIds().size() > 0) {
                                                            for (Carts carts : cartModelList) {
                                                                for (Integer idFromCoupon : couponResponse.getProductIds()) {
                                                                    if (idFromCoupon == Integer.parseInt(carts.itemId)) {
                                                                        amount += amountFromCoupon;
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        if (amount == 0) {
                                                            Toast.makeText(context, "Kupon tidak berlaku untuk produk tersebut", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }

                                                        couponLines.setDiscount(String.valueOf(amount));
                                                        amountCoupon = amount;
                                                        tvTitlePromo.setText("Promo " + couponResponse.getCode().toUpperCase());
                                                        tvKodeUnik.setText("-Rp " + MethodUtil.toCurrencyFormat(String.valueOf(amount).replace(".0", "")));
                                                        populateData();
                                                        isCouponReady = true;
                                                        layoutTotal.requestFocus();

                                                        tvCouponName.setText("Kupon " + couponResponse.getCode().toUpperCase() + " terpasang");
                                                        Toast.makeText(context, "Kode promo berhasil digunakan", Toast.LENGTH_SHORT).show();
                                                        layoutCouponTerpakai.setVisibility(View.VISIBLE);
                                                    } else {
                                                        Toast.makeText(context, "Maksimal pembelanjaan Rp "+MethodUtil.toCurrencyFormat(String.valueOf(maxAmount)), Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Minimal pembelanjaan Rp "+MethodUtil.toCurrencyFormat(String.valueOf(minAmount)), Toast.LENGTH_SHORT).show();
                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Kode promo tidak tersedia", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Kode promo tidak tersedia", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<CouponResponse>> call, Throwable t) {
                t.printStackTrace();
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.directLogin)
    void onClickDirectLogin() {

    }

    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack() {
        onBackPressed();
    }

    @OnClick(R.id.checkboxCOD)
    void onClickcheckboxCOD() {
        setComponent(Constant.COD_TYPE);
    }

    @OnClick(R.id.checkboxQris)
    void onClickcheckboxQris() {
        setComponent(Constant.QRIS);
    }

    @OnClick(R.id.checkboxTransferBank)
    void onClickcheckboxMandiri() {
        setComponent(Constant.TRANSFER_BANK);
    }

    @OnClick(R.id.btnPesan)
    void onClickBtnPesan() {
        if (etAlamat.getText().toString().equals("")) {
            etAlamat.setError(getString(R.string.getErrorAlamat));
            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
            snackBar.show();
        } else if (etNama.getText().toString().equals("")) {
            etNama.setError(getString(R.string.getErrorNama));
            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
            snackBar.show();
        }

//        else if (etEmail.getText().toString().equals("")) {
//            etEmail.setError(getString(R.string.getErrorEmail));
//            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
//                    getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
//            snackBar.show();
//        } else if (!etEmail.getText().toString().contains("@")) {
//            etEmail.setError("Silahkan periksa email anda");
//            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
//                    getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
//            snackBar.show();
//        }

        else if (etNomorTelepon.getText().toString().equals("")) {
            etNomorTelepon.setError(getString(R.string.getErrorNomorTelepon));
            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
            snackBar.show();
        } else if (payment_method == null) {
            Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
            snackBar.show();
        } else {
            if (checkboxSetDefaultAddress.isChecked()) {
                ResponseRegister responseRegister = PreferenceManager.getLoginData();
                if (responseRegister!=null){
                    com.selada.seladasegar.models.response.orders.Shipping shipping = responseRegister.getShipping();
                    shipping.setAddress1(etAlamat.getText().toString());
                    com.selada.seladasegar.models.response.orders.Billing billing = responseRegister.getBilling();
                    billing.setPhone(etNomorTelepon.getText().toString());
                    responseRegister.setShipping(shipping);
                    responseRegister.setBilling(billing);
                    PreferenceManager.setLoginData(responseRegister);

                    Shipping shippings = new Shipping();
                    shippings.setFirstName(responseRegister.getFirstName());
                    shippings.setLastName(responseRegister.getLastName());
                    shippings.setAddress1(etAlamat.getText().toString());

                    Billing billings = new Billing();
                    billings.setPhone(etNomorTelepon.getText().toString());

                    RequestRegister requestUpdateRegister = new RequestRegister();
                    requestUpdateRegister.setFirst_name(responseRegister.getFirstName());
                    requestUpdateRegister.setLast_name(responseRegister.getLastName());
                    requestUpdateRegister.setShipping(shippings);
                    requestUpdateRegister.setBilling(billings);

                    doUpdateCustomer(requestUpdateRegister);
                }
            }

            if (PreferenceManager.getIsKoperasi()){
                if (etNIK.getText().toString().equals("")){
                    etNIK.setError("NIK harus diisi");
                    Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                            getString(R.string.content_error_field_order), Snackbar.LENGTH_LONG);
                    snackBar.show();

                    return;
                }
            }

            createOrders();
        }
    }

    private List<Carts> cartModelList;
    private DBManager dbManager;
    private Context context;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    private String payment_method = null;
    private String payment_method_title = null;
    private String city = "null";
    private String state, postcode;
    private String payment_status;
    private ViewGroup viewGroup;
    private String latlong = "";
    private int gPrice;
    private double amountCoupon = 0;
    private CouponLines couponLines;
    private boolean isCouponReady = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Slide());
            getWindow().setExitTransition(new Slide());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar_title.setText("Checkout");
        context = this;
        new PreferenceManager(this);
        dbManager = new DBManager(this);
        dbManager.open();
        dbManager.fetch();
        cartModelList = new ArrayList<>();
        rvItemCheckout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        viewGroup = findViewById(android.R.id.content);
        populateData();
        initComponent();
    }

    @SuppressLint("SetTextI18n")
    private void initComponent() {
        ResponseRegister responseRegister = PreferenceManager.getLoginData();
        checkboxSetDefaultAddress.setEnabled(false);
        if (responseRegister != null) {
            if (!PreferenceManager.getIsKoperasi()){
                checkboxSetDefaultAddress.setEnabled(true);
                etNama.setText(responseRegister.getFirstName() + " " + responseRegister.getLastName());
                etEmail.setText(responseRegister.getEmail());
                etAlamat.setText(responseRegister.getShipping().getAddress1());
                etNomorTelepon.setText(responseRegister.getBilling().getPhone());
                tvKodePromo.setText(Html.fromHtml("<u>Klik disini</u>"));
                if (etAlamat.getText().toString().equals("null")) etAlamat.setText("");
            }
        }

        if (!PreferenceManager.isFirebaseUser()) {
            layoutLineLogin.setVisibility(View.VISIBLE);
            tvLoginDisini.setText(Html.fromHtml("<u>Login disini</u>"));
        }

        if (!PreferenceManager.getCodeCoupon().equals("")){
            onClickTerapkanKode();
        }

        if (PreferenceManager.getIsKoperasi()){
            layout_bekasi.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateData() {
        Cursor cursor = dbManager.readAllData();
        cartModelList.clear();
        gPrice = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    Carts newCarts = new Carts();
                    newCarts.itemId = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_ID));
                    newCarts.itemName = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_NAME));
                    newCarts.itemImage = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_IMAGE));
                    newCarts.itemPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_PRICE));
                    newCarts.itemQty = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                    newCarts.itemTotalPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_TOTAL_PRICE));
                    newCarts.itemCategoryId = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_CATEGORY_ID));
                    if (Integer.parseInt(newCarts.itemQty)>0)cartModelList.add(newCarts);
                } while (cursor.moveToNext());

                for (int i = 0; i < cartModelList.size(); i++) {
                    Carts newCarts = cartModelList.get(i);
                    gPrice += Integer.parseInt(newCarts.itemTotalPrice);
                }

//                final Random r = new Random();
//                uniqueCode = Integer.toString(r.nextInt(100) + 1);
//                int total = Integer.parseInt(uniqueCode) + gPrice;
                int total = gPrice;
                if (gPrice < Constant.MINIMAL_BELANJA) {
                    total += Integer.parseInt(Constant.ONGKIR);
                    tvOngkir.setText("Rp " + MethodUtil.toCurrencyFormat(Constant.ONGKIR));
                }

                if (amountCoupon != 0){
                    total -= amountCoupon;
                }

                tvSubTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                tvTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(total)));

                CheckoutAdapter checkoutAdapter = new CheckoutAdapter(cartModelList, context, CheckoutActivity.this);
                rvItemCheckout.setAdapter(checkoutAdapter);
                rvItemCheckout.scheduleLayoutAnimation();
            }
        } catch (Exception e) {
            Log.d("CHECKOUT ACTIVITY", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private void createOrders() {
        String name = etNama.getText().toString();
        String address = etAlamat.getText().toString();
        String email = etEmail.getText().toString();
        String phone = etNomorTelepon.getText().toString();
        String note = etNote.getText().toString();
//        String promoCode = etKodePromo.getText().toString();

        StringTokenizer tokens = new StringTokenizer(name, " ");
        int i = 0;
        String firstName = "";
        StringBuilder secondName = new StringBuilder();
        while (tokens.hasMoreTokens()) {
            i++;
            if (i == 1) firstName = tokens.nextToken();
            if (i > 1) secondName.append(tokens.nextToken()).append(" ");
        }

        ResponseRegister responseRegister = PreferenceManager.getLoginData();
        String latlongs = "";
        if (responseRegister!=null){
            latlongs = responseRegister.getBilling().getAddress2()!=null?responseRegister.getBilling().getAddress2():"";
            if (latlongs.equals("")) {
                latlongs = latlong!=null?latlong:"";
            }
        } else {
            latlongs = latlong!=null?latlong:"";
        }

        Billing billing = new Billing();
        billing.setFirstName(firstName);

        if (PreferenceManager.getIsKoperasi()){
            billing.setLastName(etNIK.getText().toString());
            billing.setCompany("kopkar_suzuki_tambun_2");
        } else {
            billing.setLastName(secondName.toString());
            billing.setEmail(email);
        }

        billing.setAddress1(address);
        billing.setAddress2(latlongs);
        if (!city.equals("null")){
            billing.setCity(city);
            billing.setPostcode(postcode);
            billing.setState(state);
        }
        billing.setCountry(COUNTRY_CODE);
        billing.setPhone(phone);

        Shipping shipping = new Shipping();
        shipping.setFirstName(firstName);

        if (PreferenceManager.getIsKoperasi()){
            shipping.setLastName(etNIK.getText().toString());
            shipping.setCompany("kopkar_suzuki_tambun_2");
        } else {
            shipping.setLastName(secondName.toString());
        }

        shipping.setAddress1(address);
        shipping.setAddress2(latlongs);
        if (!city.equals("null")){
            shipping.setCity(city);
            shipping.setPostcode(postcode);
            shipping.setState(state);
        }
        shipping.setCountry(COUNTRY_CODE);

        int itemId, quantityItem;

        List<LineItem> lineItemList = new ArrayList<>();
        for (Carts newCarts : cartModelList) {
            itemId = Integer.parseInt(newCarts.itemId);
            quantityItem = Integer.parseInt(newCarts.itemQty);
            LineItem lineItem = new LineItem();
            lineItem.setProductId(itemId);
            lineItem.setQuantity(quantityItem);
            lineItemList.add(lineItem);
        }

        List<ShippingLine> shippingLineList = new ArrayList<>();
        ShippingLine shippingLine = new ShippingLine();
        shippingLine.setMethodId(Constant.SHIPPING_METHOD_ID);
        shippingLine.setMethodTitle(Constant.SHIPPING_METHOD_TITLE);
        shippingLine.setTotal(Constant.SHIPPING_TOTAL);
        shippingLineList.add(shippingLine);

        List<CouponLines> couponLinesList = new ArrayList<>();
        couponLinesList.add(couponLines);

//        List<FeeLine> feeLineList = new ArrayList<>();
//        FeeLine feeLine = new FeeLine();
//        feeLine.setName(Constant.FEE_LINES_NAME);
//        feeLine.setTaxClass("");
//        feeLine.setTaxStatus(Constant.FEE_LINES_TAX_STATUS);
//        feeLine.setAmount(uniqueCode);
//        feeLine.setTotal(uniqueCode);
//        feeLineList.add(feeLine);

        RequestOrder requestOrder = new RequestOrder();
        if (responseRegister!=null){
            requestOrder.setCustomerId(String.valueOf(responseRegister.getId()));
        }

        requestOrder.setStatus(payment_status);
        requestOrder.setPaymentMethod(payment_method);
        requestOrder.setPaymentMethodTitle(payment_method_title);
        requestOrder.setCustomer_note(note);
        requestOrder.setSetPaid(false);
        requestOrder.setBilling(billing);
        requestOrder.setShipping(shipping);
        requestOrder.setLineItems(lineItemList);
        requestOrder.setShippingLines(shippingLineList);
        if (isCouponReady){
            requestOrder.setCoupon_lines(couponLinesList);
        }

//        requestOrder.setFeeLines(feeLineList);

        ProgressDialog dialog = MethodUtil.getLoadingBar(context, "Memproses order anda");
        ApiCore.apiInterface().createOrder(requestOrder, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        MethodUtil.getLoadingBar(context, "Mohon tunggu");
                        OrderResponse orderResponse = response.body();
                        assert orderResponse != null;
                        String orderId = String.valueOf(orderResponse.getId());
                        String orderDate = orderResponse.getDateCreated();
                        String email = orderResponse.getBilling().getEmail();
                        String jsonShippingItem = new Gson().toJson(orderResponse.getLineItems());
                        String jsonShippingAddress = new Gson().toJson(billing);
                        String jsonShippingLines = new Gson().toJson(shippingLine);
                        String orderStatus = orderResponse.getStatus();
                        String orderTotal = orderResponse.getTotal();
//                        String orderUniqueCode = orderResponse.getFeeLines().get(0).getTotal();
                        String orderPaymentMethod = orderResponse.getPaymentMethodTitle();
                        String orderCustomerNote = orderResponse.getCustomerNote();

                        Intent intent = new Intent(CheckoutActivity.this, AfterCheckoutActivity.class);
                        intent.putExtra(Constant.INTENT_ORDER_ID, orderId);
                        intent.putExtra(Constant.INTENT_ORDER_EMAIL, email);
                        intent.putExtra(Constant.INTENT_ORDER_DATE, orderDate);
                        intent.putExtra(Constant.INTENT_SHIPPING_ITEM, jsonShippingItem);
                        intent.putExtra(Constant.INTENT_SHIPPING_ADDRESS_ITEM, jsonShippingAddress);
                        intent.putExtra(Constant.INTENT_ORDER_TOTAL, orderTotal);
//                        intent.putExtra(Constant.INTENT_ORDER_UNIQUE_CODE, orderUniqueCode);
                        intent.putExtra(Constant.INTENT_SHIPPING_LINES, jsonShippingLines);
                        intent.putExtra(Constant.INTENT_ORDER_PAYMENT_METHOD, orderPaymentMethod);
                        intent.putExtra(Constant.INTENT_ORDER_CUSTOMER_NOTE, orderCustomerNote);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        dbManager.insertHistory(orderId, email, jsonShippingItem, jsonShippingAddress, orderStatus, orderTotal,
                                orderDate, "", jsonShippingLines, orderPaymentMethod, orderCustomerNote);

                        PreferenceManager.setCodeCoupon("");
                        startActivity(intent);
                    } else {
                        try {
                            String msg = MethodUtil.getErrorBody(Objects.requireNonNull(response.errorBody()).string());
                            dialog.dismiss();
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                            else {
//                                msg = "Pesanan anda gagal diproses";
//                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
//                                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
//                                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                            }
                        } catch (Exception e){
                            String msg = "Pesanan anda gagal diproses";
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void doUpdateCustomer(RequestRegister requestRegister) {
        try {
            Loading.show(context);
            ApiCore.apiInterface().updateCustomer(String.valueOf(PreferenceManager.getLoginData().getId()), Constant.METHOD_PUT, requestRegister, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<ResponseRegister>() {
                @Override
                public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                    Loading.hide(context);
                    try {
                        if (response.isSuccessful()) {
                            ResponseRegister responseRegister = response.body();
                            PreferenceManager.setLoginData(responseRegister);
                        } else {
                            assert response.errorBody() != null;
                            String msgError = MethodUtil.getResponseError(response.errorBody().string());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseRegister> call, Throwable t) {
                    Loading.hide(context);
                    t.printStackTrace();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setComponent(String type) {
        switch (type) {
            case Constant.COD_TYPE:
                checkboxCOD.setChecked(true);
                checkboxTransferBank.setChecked(false);
                checkboxQris.setChecked(false);
                tvCOD.setVisibility(View.VISIBLE);
                frameCOD.setBackgroundColor(getResources().getColor(R.color.shado));
                tvTransferBank.setVisibility(View.GONE);
                frameTransferBank.setBackgroundColor(getResources().getColor(R.color.white));
                tvQris.setVisibility(View.GONE);
                frameQris.setBackgroundColor(getResources().getColor(R.color.white));
                payment_status = Constant.PROCCESSING_STATUS;
                payment_method = Constant.PAYMENT_METHOD_COD;
                payment_method_title = Constant.PAYMENT_METHOD_COD_TITLE;
                break;
            case Constant.TRANSFER_BANK:
                checkboxCOD.setChecked(false);
                checkboxTransferBank.setChecked(true);
                checkboxQris.setChecked(false);
                tvCOD.setVisibility(View.GONE);
                frameCOD.setBackgroundColor(getResources().getColor(R.color.white));
                tvTransferBank.setVisibility(View.VISIBLE);
                frameTransferBank.setBackgroundColor(getResources().getColor(R.color.shado));
                tvQris.setVisibility(View.GONE);
                frameQris.setBackgroundColor(getResources().getColor(R.color.white));
                payment_status = Constant.ON_HOLD_STATUS;
                payment_method = Constant.PAYMENT_METHOD_TRANFSER_BANK;
                payment_method_title = Constant.PAYMENT_METHOD_TRANFSER_BANK_TITLE;
                break;
            case Constant.QRIS:
                checkboxCOD.setChecked(false);
                checkboxTransferBank.setChecked(false);
                checkboxQris.setChecked(true);
                tvCOD.setVisibility(View.GONE);
                frameCOD.setBackgroundColor(getResources().getColor(R.color.white));
                tvTransferBank.setVisibility(View.GONE);
                frameTransferBank.setBackgroundColor(getResources().getColor(R.color.white));
                tvQris.setVisibility(View.VISIBLE);
                frameQris.setBackgroundColor(getResources().getColor(R.color.shado));
                payment_status = Constant.ON_HOLD_STATUS;
                payment_method = Constant.PAYMENT_METHOD_QRIS;
                payment_method_title = Constant.PAYMENT_METHOD_QRIS_TITLE;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getBundleExtra(Constant.INTENT_BUNDLE);
                assert bundle != null;
                String address = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_ADDRESS));

                etAlamat.setText(address);
                city = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_CITY));
                postcode = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_POSTAL_CODE));
                state = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_STATE));
                latlong = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_LATLONG));
            }
            //Write your code if there's no result
        }
    }
}