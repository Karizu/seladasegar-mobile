package com.selada.seladasegar.presentation.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.util.MethodUtil;

import java.util.List;

public class HomeCartAdapter extends RecyclerView.Adapter<HomeCartAdapter.ViewHolder> {
    private List<Carts> transactionModels;
    private Context context;
    private Activity activity;
    private View v;
    private DBManager dbManager;
    private TextSwitcher floatCartTotal, cartTotal;

    public HomeCartAdapter(List<Carts> transactionModels, Context context, Activity activity, TextSwitcher cartTotal, TextSwitcher floatCartTotal) {
        this.transactionModels = transactionModels;
        this.context = context;
        this.activity = activity;
        this.cartTotal = cartTotal;
        this.floatCartTotal = floatCartTotal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_cart_detail, parent, false);

        dbManager = new DBManager(activity);
        dbManager.open();
        dbManager.fetch();
        return new ViewHolder(v);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Carts carts = transactionModels.get(position);
        final String id = carts.itemId;
        final String name = carts.itemName;
        final String price = carts.itemPrice;
        final String qty = carts.itemQty;
        final int stok = Integer.parseInt(carts.itemStok);

        holder.menuTitle.setText(name);
        holder.menuPrice.setText("Rp " + MethodUtil.toCurrencyFormat(price));
        holder.menuQty.setText(qty);
        holder.btnAddCart.setOnClickListener(view -> {
            int count = Integer.parseInt(holder.menuQty.getText().toString());
            count += 1;
            if (count <= stok) {
                int totalPrice = Integer.parseInt(price) * count;
                addMinCount(id, count, holder, totalPrice);
                ((MainActivity)context).notifyFeedAdapter();
            } else {
                holder.btnAddCart.setEnabled(false);
            }

        });

        holder.btnMinCart.setOnClickListener(view -> {
            if (Integer.parseInt(holder.menuQty.getText().toString()) > 1) {
                int count = Integer.parseInt(holder.menuQty.getText().toString());
                count -= 1;
                int totalPrice = Integer.parseInt(price) * count;
                addMinCount(id, count, holder, totalPrice);
                ((MainActivity)context).notifyFeedAdapter();
            } else {
                deleteItem(v, position, id, holder, price);
            }
        });

        holder.menuDelete.setOnClickListener(view -> {
            deleteItem(v, position, id, holder, price);
        });
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView menuTitle;
        TextView menuPrice;
        TextView menuQty;
        ImageView menuDelete;
        FrameLayout btnMinCart, btnAddCart;


        ViewHolder(View v) {
            super(v);

            menuTitle = v.findViewById(R.id.menu_title);
            menuPrice = v.findViewById(R.id.menu_price);
            menuQty = v.findViewById(R.id.menu_qty);
            menuDelete = v.findViewById(R.id.btnDeleteCart);
            btnMinCart = v.findViewById(R.id.btnMinCart);
            btnAddCart = v.findViewById(R.id.btnAddCart);
        }
    }

    private void deleteItem(View rowView, final int position, String item_id, ViewHolder holder, String price) {
        try {
            if (transactionModels.size() == 0) {
                return;
            }
            transactionModels.remove(position); //Remove the current content from the array
            notifyDataSetChanged();
            int count = 0;
            int totalPrice = Integer.parseInt(price) * count;
            addMinCount(item_id, count, holder, totalPrice);
            ((MainActivity)context).checkCart(true);
            ((MainActivity)context).notifyFeedAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void addMinCount(String itemId, int qtyFromPick, ViewHolder holder, int totalPrice) {
        try {
            dbManager.update(itemId, String.valueOf(qtyFromPick), String.valueOf(totalPrice));
            if(qtyFromPick==0) dbManager.delete(itemId);
            holder.menuQty.setText(String.valueOf(qtyFromPick));
            holder.menuPrice.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(totalPrice)));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        updateFloatingCart();
    }

    @SuppressLint("SetTextI18n")
    private void updateFloatingCart() {
        Cursor cursor = dbManager.readAllData();
        int total = 0;
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
                    total += Integer.parseInt(newCarts.itemTotalPrice);
                } while (cursor.moveToNext());
            }

            cartTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(total)));

            try {
                floatCartTotal.setText(MethodUtil.toCurrencyFormat(String.valueOf(total)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("SQLDatabase", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }
}
