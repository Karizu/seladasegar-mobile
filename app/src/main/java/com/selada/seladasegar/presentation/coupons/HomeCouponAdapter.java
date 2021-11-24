package com.selada.seladasegar.presentation.coupons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.seladasegar.R;
import com.selada.seladasegar.models.response.CouponResponse;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeCouponAdapter extends RecyclerView.Adapter<HomeCouponAdapter.ViewHolder> {
    private List<CouponResponse> transactionModels;
    private Context context;
    private View v;

    public HomeCouponAdapter(List<CouponResponse> transactionModels, Context context) {
        this.transactionModels = transactionModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_coupons, parent, false);

        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final CouponResponse carts = transactionModels.get(position);
        final String code = carts.getCode();
        final String[] expired = MethodUtil.formatDateAndTime(carts.getDateExpires());
        String dateExp = "";
        final int minimunTrx = Integer.parseInt(carts.getMinimumAmount().replace(".00",""));
        String diskon;
        if (carts.getDiscountType().equals(Constant.DISCOUNT_TYPE_PERCENT)){
            diskon = carts.getAmount().replace(".00","") + "%";
        } else {
            diskon = "Rp " + MethodUtil.toCurrencyFormat(carts.getAmount().replace(".00", ""));
        }

        String finalMinimunTrx = minimunTrx>0?"Rp " + MethodUtil.toCurrencyFormat(String.valueOf(minimunTrx)):"Tanpa minimum transaksi";

        String dateExpired;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date strDate = null;

        try {
            dateExpired = carts.getDateExpires().replace("T", " ");
            strDate = sdf.parse(dateExpired);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (new Date().after(strDate)) {
            holder.frameOutOfStock.setVisibility(View.VISIBLE);
            holder.tvExpiredDate.setText("Kupon Kadaluarsa");
        } else {
            dateExp = expired[0];
            String finalDateExp = dateExp;
            holder.frameOutOfStock.setVisibility(View.GONE);
            holder.tvExpiredDate.setText(expired[0]);
            holder.menu_layout.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailCouponActivity.class);
                intent.putExtra("expiredDate", finalDateExp);
                intent.putExtra("diskon", "Diskon " + diskon);
                intent.putExtra("minimumTrx", finalMinimunTrx);
                intent.putExtra("code", code);
                context.startActivity(intent);
            });
        }

        holder.tvMinimumTrx.setText(finalMinimunTrx);
        holder.tvDiskon.setText("Diskon " + diskon);
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDiskon;
        TextView tvExpiredDate;
        TextView tvMinimumTrx;
        View frameExpired;
        LinearLayout menu_layout;
        FrameLayout frameOutOfStock;

        ViewHolder(View v) {
            super(v);
            tvDiskon = v.findViewById(R.id.tvDiskon);
            tvExpiredDate = v.findViewById(R.id.tvExpiredDate);
            tvMinimumTrx = v.findViewById(R.id.tvMinimumTrx);
            frameExpired = v.findViewById(R.id.frameExpired);
            menu_layout = v.findViewById(R.id.menu_layout);
            frameOutOfStock = v.findViewById(R.id.frameOutOfStock);
        }
    }
}
