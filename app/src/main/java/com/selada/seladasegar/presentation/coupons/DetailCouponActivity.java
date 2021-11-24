package com.selada.seladasegar.presentation.coupons;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.response.CouponResponse;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailCouponActivity extends AppCompatActivity {

    private Context context;
    private String code;

    @BindView(R.id.tvDiskon)
    TextView tvDiskon;
    @BindView(R.id.tvExpiredDate)
    TextView tvExpiredDate;
    @BindView(R.id.tvMinimumTrx)
    TextView tvMinimumTrx;
    @BindView(R.id.tvDiskonTitle)
    TextView tvDiskonTitle;
    @BindView(R.id.tvSyaratKetentuan)
    TextView tvSyaratKetentuan;
    @BindView(R.id.btnGunakanKupon)
    Button btnGunakanKupon;

    @OnClick(R.id.ic_back)
    void onClickBack(){
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_coupon);
        ButterKnife.bind(this);

        context = this;

        new PreferenceManager(this);
        populateIntent();
    }

    private void populateIntent() {
        String diskon = getIntent().getStringExtra("diskon");
        String expiredDate = getIntent().getStringExtra("expiredDate");
        String minimumTrx = getIntent().getStringExtra("minimumTrx");
        code = getIntent().getStringExtra("code");
        boolean isFromMain = getIntent().getBooleanExtra("isFromMain", false);
        if (isFromMain){
            checkCoupon();
            btnGunakanKupon.setText("HAPUS KUPON");
            btnGunakanKupon.setBackgroundColor(getResources().getColor(R.color.red_text));
            btnGunakanKupon.setOnClickListener(view -> {
                PreferenceManager.setCodeCoupon("");
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("isFromDetail", true);
                startActivity(intent);
                Toast.makeText(context, "Kupon telah dihapus", Toast.LENGTH_SHORT).show();
            });
        } else {
            tvDiskonTitle.setText(diskon);
            tvDiskon.setText(diskon);
            tvExpiredDate.setText(expiredDate);
            tvMinimumTrx.setText(minimumTrx);
            btnGunakanKupon.setOnClickListener(view -> {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("isFromDetail", true);
                PreferenceManager.setCodeCoupon(code);
                startActivity(intent);
                Toast.makeText(context, "Kupon terpasang", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void checkCoupon() {
        ProgressDialog dialog = MethodUtil.getLoadingBar(context, "Mohon tunggu");
        ApiCore.apiInterface().getCoupons(code, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<CouponResponse>>() {
            @Override
            public void onResponse(Call<List<CouponResponse>> call, Response<List<CouponResponse>> response) {
                try {
                    dialog.dismiss();
                    if (response.isSuccessful()) {
                        List<CouponResponse> responses = response.body();
                        if (responses != null) {
                            if (responses.size() > 0) {
                                for (int i = 0; i < responses.size(); i++){
                                    CouponResponse couponRes = responses.get(i);
                                    if (couponRes.getDateExpires()!=null){
                                        String[] expiredDate = MethodUtil.formatDateAndTime(couponRes.getDateExpires());
                                        int minimunTrx = Integer.parseInt(couponRes.getMinimumAmount().replace(".00",""));
                                        String diskon;
                                        if (couponRes.getDiscountType().equals(Constant.DISCOUNT_TYPE_PERCENT)){
                                            diskon = couponRes.getAmount().replace(".00","") + "%";
                                        } else {
                                            diskon = "Rp " + MethodUtil.toCurrencyFormat(couponRes.getAmount().replace(".00", ""));
                                        }
                                        String finalMinimunTrx = minimunTrx>0?"Minimum Transaksi Rp " + MethodUtil.toCurrencyFormat(String.valueOf(minimunTrx)):"Tanpa minimum transaksi";
                                        tvDiskonTitle.setText("Diskon " + diskon);
                                        tvDiskon.setText("Diskon " + diskon);
                                        tvExpiredDate.setText(expiredDate[0]);
                                        tvMinimumTrx.setText(finalMinimunTrx);
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Terjadi kesalahan pada saat membuka kupon", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
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
}