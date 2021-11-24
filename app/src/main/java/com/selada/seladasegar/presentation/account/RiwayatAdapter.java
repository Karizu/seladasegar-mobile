package com.selada.seladasegar.presentation.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.selada.seladasegar.R;
import com.selada.seladasegar.models.response.orders.OrderResponse;
import com.selada.seladasegar.presentation.checkout.AfterCheckoutActivity;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;

import java.lang.reflect.Method;
import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {
    private List<OrderResponse> orderResponseList;
    private Context context;
    private Activity activity;
    private View v;
    private DBManager dbManager;

    public RiwayatAdapter(List<OrderResponse> orderResponseList, Context context, Activity activity) {
        this.orderResponseList = orderResponseList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_riwayat_item, parent, false);

        dbManager = new DBManager(activity);
        dbManager.open();
        dbManager.fetch();
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderResponse orderResponse = orderResponseList.get(position);
        String id = String.valueOf(orderResponse.getId());
        String date = MethodUtil.formatDateAndTime(orderResponse.getDateCreated())[0];
        String time = MethodUtil.formatDateAndTime(orderResponse.getDateCreated())[1];
        String total = orderResponse.getTotal();
        String status = orderResponse.getStatus();
        String itemName = orderResponse.getLineItems() != null ? orderResponse.getLineItems().get(0).getName():"";
        String email = orderResponse.getBilling().getEmail();
        String lineItems = new Gson().toJson(orderResponse.getLineItems());
        String shipping = new Gson().toJson(orderResponse.getBilling());
        String billing = new Gson().toJson(orderResponse.getBilling());
        String feeLines = new Gson().toJson(orderResponse.getFeeLines());
        String paymentMethod = orderResponse.getPaymentMethodTitle();
        String customerNote = orderResponse.getCustomerNote();
        String orderDateTime = orderResponse.getDateCreated();
        String couponTitle = "Promo";
        String couponAmount =  "-";
        if (orderResponse.getCoupon_lines()!=null){
            if (orderResponse.getCoupon_lines().size()>0){
                couponTitle = "Promo "+orderResponse.getCoupon_lines().get(0).getCode().toUpperCase();
                couponAmount = "-Rp "+ MethodUtil.toCurrencyFormat(orderResponse.getCoupon_lines().get(0).getDiscount());
            }
        }

        String finalCouponTitle = couponTitle;
        String finalCouponAmount = couponAmount;

        holder.tvOrderId.setText("#"+id);
        holder.tvItemName.setText(Html.fromHtml(itemName+"<br/><b><u>Lihat detail...</u></b>"));
        holder.tvDate.setText(date);
        holder.tvTime.setText(time);
        holder.tvSubTotalItem.setText("Rp "+MethodUtil.toCurrencyFormat(total));
        holder.menuLayout.setOnClickListener(view -> {
            Intent intent = new Intent(activity, AfterCheckoutActivity.class);
            intent.putExtra(Constant.INTENT_IS_FROM_RIWAYAT, true);
            intent.putExtra(Constant.INTENT_ORDER_ID, id);
            intent.putExtra(Constant.INTENT_ORDER_EMAIL, email);
            intent.putExtra(Constant.INTENT_ORDER_DATE, orderDateTime);
            intent.putExtra(Constant.INTENT_ORDER_LINE_ITEMS, lineItems);
            intent.putExtra(Constant.INTENT_ORDER_SHIPPING, shipping);
            intent.putExtra(Constant.INTENT_ORDER_BILLING, billing);
            intent.putExtra(Constant.INTENT_ORDER_FEE_LINE, feeLines);
            intent.putExtra(Constant.INTENT_ORDER_TOTAL, total);
            intent.putExtra(Constant.INTENT_ORDER_PAYMENT_METHOD, paymentMethod);
            intent.putExtra(Constant.INTENT_ORDER_CUSTOMER_NOTE, customerNote);
            intent.putExtra(Constant.INTENT_ORDER_STATUS, status);
            intent.putExtra(Constant.INTENT_COUPON_TITLE, finalCouponTitle);
            intent.putExtra(Constant.INTENT_COUPON_AMOUNT, finalCouponAmount);
            activity.startActivity(intent);
        });

        switch (status){
            case Constant.ON_HOLD_STATUS:
                holder.tvStatusOrder.setText("Menunggu \nPembayaran");
                break;
            case Constant.PROCCESSING_STATUS:
                holder.tvStatusOrder.setText("Sedang Diproses");
                break;
            case Constant.COMPLETED_STATUS:
                holder.tvStatusOrder.setText("Berhasil");
                break;
            default:
                holder.tvStatusOrder.setText(status);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return orderResponseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout menuLayout;
        TextView tvOrderId, tvItemName, tvTime, tvDate, tvSubTotalItem, tvStatusOrder;


        ViewHolder(View v) {
            super(v);

            menuLayout = v.findViewById(R.id.menu_layout);
            tvStatusOrder = v.findViewById(R.id.tvStatusOrder);
            tvOrderId = v.findViewById(R.id.tvOrderId);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvTime = v.findViewById(R.id.tvTime);
            tvDate = v.findViewById(R.id.tvDate);
            tvSubTotalItem = v.findViewById(R.id.tvSubTotalItem);
        }
    }
}
