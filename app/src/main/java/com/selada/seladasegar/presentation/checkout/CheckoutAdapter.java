package com.selada.seladasegar.presentation.checkout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.seladasegar.R;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.util.MethodUtil;

import java.util.List;

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.ViewHolder> {
    private List<Carts> transactionModels;
    private Activity activity;

    public CheckoutAdapter(List<Carts> transactionModels, Context context, Activity activity) {
        this.transactionModels = transactionModels;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_checkout_item, parent, false);

        DBManager dbManager = new DBManager(activity);
        dbManager.open();
        dbManager.fetch();
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Carts checkoutItem = transactionModels.get(position);
        final String itemName = checkoutItem.itemName;
        final String itemPrice = checkoutItem.itemPrice;
        final String itemQty = checkoutItem.itemQty;
        final String itemSubTotal = checkoutItem.itemTotalPrice;

        holder.tvItemName.setText(itemName);
        holder.tvPrice.setText("Rp " + MethodUtil.toCurrencyFormat(itemPrice));
        holder.tvSubTotalItem.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(itemSubTotal)));
        holder.tvQuantity.setText(itemQty);
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout menuLayout;
        TextView tvItemName, tvPrice, tvQuantity, tvSubTotalItem;


        ViewHolder(View v) {
            super(v);

            menuLayout = v.findViewById(R.id.menu_layout);
            tvItemName = v.findViewById(R.id.tvItemName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            tvSubTotalItem = v.findViewById(R.id.tvSubTotalItem);
        }
    }
}
