package com.selada.seladasegar.presentation.coupons;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.response.CouponResponse;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponActivity extends AppCompatActivity {

    private Context context;

    @BindView(R.id.rvCoupons)
    RecyclerView rvCoupons;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    @OnClick(R.id.ic_back)
    void onClickBack(){
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);
        context = this;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvCoupons.setLayoutManager(linearLayoutManager);

        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            populateCoupons();
        });

        populateCoupons();
    }

    private void populateCoupons(){
        List<CouponResponse> couponResponseList = new ArrayList<>();
        ProgressDialog dialog = MethodUtil.getLoadingBar(context, "Mohon tunggu");
        ApiCore.apiInterface().getCoupons("", PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<CouponResponse>>() {
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
                                        couponResponseList.add(couponRes);
                                    }
                                }
                                HomeCouponAdapter adapter = new HomeCouponAdapter(couponResponseList, context);
                                rvCoupons.setAdapter(adapter);
                                tvNoData.setVisibility(View.GONE);
                            } else {
                                tvNoData.setVisibility(View.VISIBLE);
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