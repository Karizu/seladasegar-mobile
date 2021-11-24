package com.selada.seladasegar.presentation.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.models.Category;
import com.selada.seladasegar.models.KatalogModel;
import com.selada.seladasegar.presentation.checkout.CheckoutActivity;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.services.ViewImageDetail;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

public class HomeFeedAdapter extends RecyclerView.Adapter<HomeFeedAdapter.ViewHolder> {
    private static List<KatalogModel> transactionModels;
    private Context context;
    private Activity activity;
    private DBManager dbManager;
    private View v;
    private TextView view;
    private Cursor cursor;
    private boolean isFromFavourite = false;
    private boolean isForRelatedItem = false;
    private int count = 0;

    public HomeFeedAdapter(List<KatalogModel> transactionModels, Context context, Activity activity) {
        this.transactionModels = transactionModels;
        this.context = context;
        this.activity = activity;
    }

    public HomeFeedAdapter(List<KatalogModel> transactionModels, Context context, Activity activity, boolean isForRelatedItem) {
        this.transactionModels = transactionModels;
        this.context = context;
        this.activity = activity;
        this.isForRelatedItem = isForRelatedItem;
    }

    public HomeFeedAdapter(List<KatalogModel> transactionModels, Context context, Activity activity, boolean isFromFavourite, TextView view) {
        this.transactionModels = transactionModels;
        this.context = context;
        this.activity = activity;
        this.view = view;
        this.isFromFavourite = isFromFavourite;
    }

    @NonNull
    @Override
    public HomeFeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isForRelatedItem) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_feed_related_item, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_feed_home, parent, false);
        }

        dbManager = new DBManager(activity);
        dbManager.open();
        dbManager.fetch();
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "ResourceAsColor", "CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull HomeFeedAdapter.ViewHolder holder, int position) {
        final KatalogModel transactionModel = transactionModels.get(position);
        final String id = transactionModel.getId();
        final String name = transactionModel.getName();
        String description = transactionModel.getDeskripsi();
        final String image = transactionModel.getImage();
        final String regularPrice = transactionModel.getRegular_price();
        String salePrice = transactionModel.getSale_price();
        final String stockStatus = transactionModel.getStock_status();
        final String permalink = transactionModel.getPermalink();
        final int stock = 100;
//        if (transactionModel.getStock_quantity() == null || transactionModel.getStock_quantity().equals("0")) {
//            stock = 100;
//        } else {
//            stock = Integer.parseInt(transactionModel.getStock_quantity());
//        }

        final List<Category> categories = transactionModel.getCategories();
        final String categoryName = categories != null ? String.valueOf(categories.get(0).getName()) : "None";
        String categoryId = "0";
        if (categories!=null){
            if (categories.size()>0){
                for (Category category: categories){
                    categoryId = category.getId() + "-";
                }
                if (categoryId.endsWith("-")){
                    categoryId.substring(0, categoryId.length()-1);
                }
            }
        }

        if (salePrice.equals("") || salePrice.equalsIgnoreCase("null")) {
            salePrice = regularPrice;
        }

        if (description.equals("") || description.equalsIgnoreCase("null")) {
            description = "-";
        }

        holder.tvName.setText(name);
        holder.tvDescription.setVisibility(View.GONE);
        holder.tvHarga.setText("Rp " + MethodUtil.toCurrencyFormat(salePrice));
        if (regularPrice.equals(salePrice)) {
            holder.tvItemPriceStrike.setVisibility(View.GONE);
        } else {
            holder.tvItemPriceStrike.setText(MethodUtil.formatStrikeString("Rp " + MethodUtil.toCurrencyFormat(regularPrice)));
        }

        holder.tvPercentageDiscount.setText("-2%");
        holder.tvPercentageDiscount.setVisibility(View.GONE);

        String finalDescription = description;
        String finalSalePrice = salePrice;

        try {
            cursor = dbManager.readDataFavourite(id);
            if (cursor.moveToFirst()) {
                holder.imgFav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_green_24));
                holder.btnFav.setOnClickListener(view -> {
                    dbManager.deleteFavourite(id);
                    holder.imgFav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                    Toast.makeText(context, "Produk dihapus dari daftar favorit anda", Toast.LENGTH_SHORT).show();
                    if (isFromFavourite) {
                        removeRecyclerItem(position);
                    }
                });
            } else {
                holder.imgFav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_border_24));
                holder.btnFav.setOnClickListener(view -> {
                    dbManager.insertFavourite(id, name, finalSalePrice, image, "");
                    holder.imgFav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_baseline_favorite_green_24));
                    Toast.makeText(context, "Produk ditambahkan ke daftar favorit anda", Toast.LENGTH_SHORT).show();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        Carts carts = new Carts();
        carts.itemId = id;
        carts.itemName = name;
        carts.itemPrice = salePrice;
        carts.itemTotalPrice = salePrice;
        carts.itemImage = image;
        carts.itemStok = String.valueOf(stock);
        carts.itemCategoryId = categoryId;

        holder.btnItem.setOnClickListener(view -> {
            ((MainActivity) context).handleCart(false);
            initDialogCart(name, finalDescription, finalSalePrice, regularPrice, categoryName, image, stockStatus, carts, stock, permalink);
        });
        holder.btnReadMore.setOnClickListener(view1 -> {
            ((MainActivity) context).handleCart(false);
            initDialogCart(name, finalDescription, finalSalePrice, regularPrice, categoryName, image, stockStatus, carts, stock, permalink);
        });

        if (!transactionModel.isClickAdd()) {
            holder.cvAddMinCart.setVisibility(View.GONE);
            holder.cvBtnBeli.setVisibility(View.VISIBLE);
            holder.btnBeli.setVisibility(View.VISIBLE);
        }

        holder.btnBeli.setOnClickListener(view -> {
            ((MainActivity) context).handleCart(true);
            transactionModel.setClickAdd(true);
            int quantity = 1;
            proccessItemToCart(carts, true);
            ((MainActivity) activity).updateFloatingCart(true);
            holder.cvBtnBeli.setVisibility(View.GONE);
            holder.btnBeli.setVisibility(View.GONE);
            holder.cvAddMinCart.setVisibility(View.VISIBLE);
            holder.tvQtyItemCart.setText(String.valueOf(quantity));
            holder.btnAddCart.setOnClickListener(view1 -> {
                ((MainActivity) context).handleCart(true);
                int count = Integer.parseInt(holder.tvQtyItemCart.getText().toString());
                count += 1;
                if (count <= stock) {
                    transactionModel.setTotal_today_qty(String.valueOf(count));
                    holder.tvQtyItemCart.setText(String.valueOf(count));
                    proccessItemToCart(carts, true);
                    ((MainActivity) activity).updateFloatingCart(true);
                } else {
                    holder.btnAddCart.setEnabled(false);
                }
            });
            holder.btnMinCart.setOnClickListener(view1 -> {
                ((MainActivity) context).handleCart(true);
                int count = Integer.parseInt(holder.tvQtyItemCart.getText().toString());
                if (count == 1) {
                    transactionModel.setClickAdd(false);
                    holder.cvAddMinCart.setVisibility(View.GONE);
                    holder.cvBtnBeli.setVisibility(View.VISIBLE);
                    holder.btnBeli.setVisibility(View.VISIBLE);
                } else {
                    count -= 1;
                    transactionModel.setTotal_today_qty(String.valueOf(count));
                    holder.tvQtyItemCart.setText(String.valueOf(count));
                }

                proccessItemToCart(carts, false);
                ((MainActivity) activity).updateFloatingCart(true);
            });
        });

        holder.btnAddCart.setOnClickListener(view1 -> {
            ((MainActivity) context).handleCart(true);
            int count = Integer.parseInt(holder.tvQtyItemCart.getText().toString());
            count += 1;
            if (count <= stock) {
                transactionModel.setTotal_today_qty(String.valueOf(count));
                holder.tvQtyItemCart.setText(String.valueOf(count));
                proccessItemToCart(carts, true);
                ((MainActivity) activity).updateFloatingCart(true);
            } else {
                holder.btnAddCart.setEnabled(false);
            }
        });

        holder.btnMinCart.setOnClickListener(view1 -> {
            ((MainActivity) context).handleCart(true);
            int count = Integer.parseInt(holder.tvQtyItemCart.getText().toString());
            if (count == 1) {
                transactionModel.setClickAdd(false);
                holder.cvAddMinCart.setVisibility(View.GONE);
                holder.cvBtnBeli.setVisibility(View.VISIBLE);
                holder.btnBeli.setVisibility(View.VISIBLE);
            } else {
                count -= 1;
                transactionModel.setTotal_today_qty(String.valueOf(count));
                holder.tvQtyItemCart.setText(String.valueOf(count));
            }
            proccessItemToCart(carts, false);
            ((MainActivity) activity).updateFloatingCart(true);
        });

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.resto_default);
        requestOptions.fitCenter().dontAnimate();
        Glide.with(context).load(image)
                .apply(requestOptions)
                .into(holder.imgBarang);

//        if (((MainActivity) context).isIntentFromLink) {
//            if (permalink.contains(((MainActivity) context).linkData)) {
//                initDialogCart(name, finalDescription, finalSalePrice, regularPrice, categoryName, image, stockStatus, carts, stock, permalink);
//                ((MainActivity) context).isIntentFromLink = false;
//            }
//        }

        if (stockStatus != null) {
            switch (stockStatus) {
                case Constant.IN_STOCK:
                    holder.btnItem.setEnabled(true);
                    holder.frameOutOfStock.setVisibility(View.GONE);
                    holder.btnBeli.setEnabled(true);
                    holder.btnBeli.setBackgroundColor(context.getResources().getColor(R.color.light_green));
                    holder.tvBeli.setText("BELI");
                    holder.btnReadMore.setVisibility(View.GONE);
                    holder.cvBtnBeli.setVisibility(View.VISIBLE);
                    holder.tvName.setText(name);
                    break;
                case Constant.OUT_OF_STOCK:
                    holder.btnItem.setEnabled(false);
                    holder.frameOutOfStock.setVisibility(View.VISIBLE);
                    holder.cvBtnBeli.setVisibility(View.GONE);
                    holder.tvBeli.setText("BELI");
                    holder.btnReadMore.setVisibility(View.VISIBLE);
                    holder.tvName.setText(name);
                    break;
                case Constant.ON_BACK_ORDER:
                    holder.btnItem.setEnabled(true);
                    holder.btnBeli.setEnabled(true);
                    holder.btnBeli.setBackgroundColor(context.getResources().getColor(R.color.light_green));
                    holder.tvBeli.setText("PRE ORDER");
                    holder.cvBtnBeli.setVisibility(View.VISIBLE);
                    holder.frameOutOfStock.setVisibility(View.GONE);
                    holder.btnReadMore.setVisibility(View.GONE);
                    holder.tvName.setText(name + "\n");
                    String text = "<font color='#83BB58'><b>(PRE ORDER)</b></font>";
                    holder.tvName.append(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
                    break;
            }
        }

        getItemFromDb(id, holder, null);
    }

    private void removeRecyclerItem(int position) {
//        Animation anim = AnimationUtils.loadAnimation(context,
//                android.R.anim.fade_out);
//        anim.setDuration(1000);
//        v.startAnimation(anim);
//
//        new Handler().postDelayed(() -> {
//            if (transactionModels.size() == 0) {
//                // adding empty view instead of the RecyclerView
//                view.setVisibility(View.VISIBLE);
//                return;
//            }
//            transactionModels.remove(position); //Remove the current content from the array
//            notifyDataSetChanged();
//        }, anim.getDuration());

        transactionModels.remove(position);
        notifyDataSetChanged();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "CheckResult"})
    public void initDialogCart(String productName, String itemDescription, String price, String priceStrike, String categoryName, String img, String stockStatus, Carts carts, int stock, String permalink) {
        Dialog dialog = MethodUtil.getDialogCart(R.layout.dialog_item_detail, context);
        ImageView imgItem = dialog.findViewById(R.id.imgItem);
        ImageView btnCloseDialog = dialog.findViewById(R.id.imgClose);
        FrameLayout btnShare = dialog.findViewById(R.id.btnShare);
        TextView tvPriceStrike = dialog.findViewById(R.id.tvPriceStrike);
        TextView tvPrice = dialog.findViewById(R.id.tvPrice);
        TextView tvProductName = dialog.findViewById(R.id.tvProductName);
        TextView tvKategoriItem = dialog.findViewById(R.id.tvKategoriItem);
        TextView tvProductDesc = dialog.findViewById(R.id.tvProductDesc);
        TextView tvQtyItemCart = dialog.findViewById(R.id.tvQtyItemCart);
        FrameLayout btnMinCart = dialog.findViewById(R.id.btnMinCart);
        FrameLayout btnAddCart = dialog.findViewById(R.id.btnAddCart);
        LinearLayout layoutButtonCart = dialog.findViewById(R.id.layoutButtonCart);
        LinearLayout layoutOnOutOfStock = dialog.findViewById(R.id.layoutOnOutOfStock);
        btnCloseDialog.setOnClickListener(view1 -> dialog.dismiss());

        btnShare.setOnClickListener(view1 -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                    productName +
                    "\n" + permalink);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
        });

        tvProductName.setText(productName);
        tvPrice.setText("Rp " + MethodUtil.toCurrencyFormat(price));
        if (Integer.parseInt(priceStrike) == Integer.parseInt(price)){
            tvPriceStrike.setVisibility(View.GONE);
        }
        tvPriceStrike.setText(MethodUtil.formatStrikeString("Rp " + MethodUtil.toCurrencyFormat(priceStrike)));
        tvKategoriItem.setText(categoryName);
        tvProductDesc.setText(Html.fromHtml("<b>Deskripsi Produk: </b> <br>" + itemDescription));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.resto_default);
        requestOptions.fitCenter().dontAnimate();
        Glide.with(context).load(img)
                .apply(requestOptions)
                .into(imgItem);

        imgItem.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, ViewImageDetail.class);
            intent.putExtra("source_image", img);
            Objects.requireNonNull(context).startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle());
        });

        getItemFromDb(carts.itemId, null, tvQtyItemCart);

        count = Integer.parseInt(tvQtyItemCart.getText().toString());
        btnMinCart.setOnClickListener(view1 -> {
            if (Integer.parseInt(tvQtyItemCart.getText().toString()) > 1) {
                int count = Integer.parseInt(tvQtyItemCart.getText().toString());
                count -= 1;
                int totalPrice = Integer.parseInt(price) * count;
                addMinCount(carts.itemId, count, tvQtyItemCart, totalPrice, carts);
            } else {
                if (Integer.parseInt(tvQtyItemCart.getText().toString()) == 1) {
                    tvQtyItemCart.setText("0");
                    deleteItem(carts.itemId);
                    ((MainActivity) context).updateFloatingCart(true);
                    ((MainActivity) context).updateFloatingCart();
                }
            }
            ((MainActivity) context).notifyFeedAdapter();
        });

        btnAddCart.setOnClickListener(view1 -> {
            int count = Integer.parseInt(tvQtyItemCart.getText().toString());
            count += 1;
            if (count <= stock) {
                int totalPrice = Integer.parseInt(price) * count;
                addMinCount(carts.itemId, count, tvQtyItemCart, totalPrice, carts);
                ((MainActivity) context).notifyFeedAdapter();
            }
        });

        switch (stockStatus) {
            case Constant.IN_STOCK:
                layoutButtonCart.setVisibility(View.VISIBLE);
                layoutOnOutOfStock.setVisibility(View.GONE);
                break;
            case Constant.OUT_OF_STOCK:
                layoutButtonCart.setVisibility(View.GONE);
                layoutOnOutOfStock.setVisibility(View.VISIBLE);
                break;
            case Constant.ON_BACK_ORDER:
                layoutButtonCart.setVisibility(View.VISIBLE);
                layoutOnOutOfStock.setVisibility(View.GONE);
                tvProductName.setText(productName + "\n");
                String text = "<font color='#83BB58'><b>(PRE ORDER)</b></font>";
                tvProductName.append(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));
                break;
        }
    }

    private void deleteItem(String itemId) {
        dbManager.delete(itemId);
        notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void addMinCount(String itemId, int qtyFromPick, TextView tvQtyItemCart, int totalPrice, Carts carts) {
        Cursor cLog = dbManager.readData(carts.itemId);
        boolean isItemAvailable = false;
        if (cLog != null) {
            if (cLog.moveToFirst()) {
                String itemQty = cLog.getString(cLog.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                if (itemQty == null || itemQty.equalsIgnoreCase("null") || itemQty.equals("")) {
                    //skip
                    Log.d("SQLite", "SKIP");
                } else {
                    isItemAvailable = true;
                }
            }
        }
        try {
            if (isItemAvailable) {
                dbManager.update(itemId, String.valueOf(qtyFromPick), String.valueOf(totalPrice));
            } else {
                dbManager.insert(carts.itemId, carts.itemName, carts.itemPrice, String.valueOf(totalPrice), carts.itemImage, String.valueOf(qtyFromPick), carts.itemStok, carts.itemCategoryId);
            }
            tvQtyItemCart.setText(String.valueOf(qtyFromPick));
            ((MainActivity) activity).onClickBtnClose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ((MainActivity) activity).updateFloatingCart(true);
    }

    private void proccessItemToCart(Carts carts, boolean isAddQuantity) {
        Cursor cLog = dbManager.readData(carts.itemId);
        boolean isItemAvailable = false;
        int count = 0;
        if (cLog != null) {
            if (cLog.moveToFirst()) {
                String itemQty = cLog.getString(cLog.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                if (itemQty == null || itemQty.equalsIgnoreCase("null") || itemQty.equals("")) {
                    //skip
                    Log.d("SQLite", "SKIP");
                } else {
                    Log.d("SQLite Quantity", itemQty);
                    count = Integer.parseInt(itemQty);
                    isItemAvailable = true;
                }
            }
        }

        if (isAddQuantity) {
            count += 1;
        } else {
            count -= 1;
        }

        int totalPrice = Integer.parseInt(carts.itemPrice) * count;

        if (isItemAvailable) {
            dbManager.update(carts.itemId, String.valueOf(count), String.valueOf(totalPrice));
            if (count == 0) {
                dbManager.delete(carts.itemId);
                ((MainActivity) context).updateFloatingCart(true);
                ((MainActivity) context).updateFloatingCart();
            }
        } else {
            dbManager.insert(carts.itemId, carts.itemName, carts.itemPrice, String.valueOf(totalPrice), carts.itemImage, String.valueOf(count), carts.itemStok, carts.itemCategoryId);
        }
    }

    private void getItemFromDb(String itemId, ViewHolder viewHolder, TextView tvQtyItemCart) {
        try {
            Cursor cLog = dbManager.readData(itemId);
            if (cLog != null) {
                if (cLog.moveToFirst()) {
                    String itemQty = cLog.getString(cLog.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                    if (itemQty == null || itemQty.equalsIgnoreCase("null") || itemQty.equals("")) {
                        Log.d("SQLite", "SKIP");
                    } else {
                        int itemQuantity = Integer.parseInt(itemQty);
                        if (itemQuantity > 0) {
                            if (viewHolder != null) {
                                viewHolder.cvBtnBeli.setVisibility(View.GONE);
                                viewHolder.btnBeli.setVisibility(View.GONE);
                                viewHolder.cvAddMinCart.setVisibility(View.VISIBLE);
                                viewHolder.tvQtyItemCart.setText(String.valueOf(itemQuantity));
                            } else {
                                tvQtyItemCart.setText(String.valueOf(itemQuantity));
                            }
                        } else {
                            if (viewHolder != null) {
                                viewHolder.cvAddMinCart.setVisibility(View.GONE);
                                viewHolder.cvBtnBeli.setVisibility(View.VISIBLE);
                                viewHolder.btnBeli.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getStokFromDb(String itemId) {
        int stok = 0;
        try {
            Cursor cLog = dbManager.readData(itemId);
            if (cLog != null) {
                if (cLog.moveToFirst()) {
                    String itemStok = cLog.getString(cLog.getColumnIndex(SQLDatabaseHelper.ITEM_STOK));
                    stok = Integer.parseInt(itemStok);
                    Log.d("STOK", itemStok);
                    return stok;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stok;
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvHarga;
        ImageView imgBarang, imgFav;
        LinearLayout layout, btnBeli;
        RelativeLayout btnItem;
        FrameLayout btnFav, btnMinCart, btnAddCart, frameOutOfStock, cvAddMinCart, btnReadMore;
        TextView tvPercentageDiscount, tvItemPriceStrike, tvDescription, tvQtyItemCart, tvBeli;
        CardView cvBtnBeli;

        ViewHolder(View v) {
            super(v);

            tvName = v.findViewById(R.id.item_title);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvPercentageDiscount = v.findViewById(R.id.tvPercentageDiscount);
            tvHarga = v.findViewById(R.id.item_price);
            tvItemPriceStrike = v.findViewById(R.id.item_price_strike);
            imgBarang = v.findViewById(R.id.item_icon);
            imgFav = v.findViewById(R.id.imgFav);
            layout = v.findViewById(R.id.menu_layout);
            btnItem = v.findViewById(R.id.btnItem);
            btnBeli = v.findViewById(R.id.btnBeli);
            btnBeli.setOnClickListener(view1 -> {
                KatalogModel modelList = transactionModels.get(getAdapterPosition());
                modelList.setClickAdd(true);
                modelList.setTotal_today_qty(tvQtyItemCart.getText().toString());
            });

            btnFav = v.findViewById(R.id.btnFav);
            cvAddMinCart = v.findViewById(R.id.cvAddMinCart);
            btnMinCart = v.findViewById(R.id.btnMinCart);
            btnAddCart = v.findViewById(R.id.btnAddCart);
            tvQtyItemCart = v.findViewById(R.id.tvQtyItemCart);
            frameOutOfStock = v.findViewById(R.id.frameOutOfStock);
            tvBeli = v.findViewById(R.id.tvBeli);
            btnReadMore = v.findViewById(R.id.btnReadMore);
            cvBtnBeli = v.findViewById(R.id.cvBtnBeli);
        }
    }

    public void updateData(List<KatalogModel> list) {
        transactionModels = list;
        notifyDataSetChanged();
    }
}
