package com.selada.seladasegar.presentation.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.selada.seladasegar.models.HomeCategory;
import com.selada.seladasegar.models.KatalogModel;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class BottomSheetCategoryAdapter extends RecyclerView.Adapter<BottomSheetCategoryAdapter.ViewHolder> {
    private static List<HomeCategory> transactionModels;
    private Context context;
    private HomeFragment fragment;

    public BottomSheetCategoryAdapter(List<HomeCategory> transactionModels, Context context, HomeFragment fragment) {
        this.transactionModels = transactionModels;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public BottomSheetCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_category_item, parent, false);

        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"SetTextI18n", "ResourceAsColor", "CheckResult"})
    @Override
    public void onBindViewHolder(@NonNull BottomSheetCategoryAdapter.ViewHolder holder, int position) {
        final HomeCategory transactionModel = transactionModels.get(position);
        final String name = transactionModel.name;

        String categoryName = null;
        try {
            categoryName = URLDecoder.decode(name, "UTF-8");
            if (categoryName.contains("&amp;")){
                String[] catName = categoryName.split("amp;");
                categoryName = catName[0] + catName[1];
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        holder.tvName.setText(categoryName);
        holder.layout.setOnClickListener(view -> {
            fragment.onClickItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return transactionModels.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        LinearLayout layout;

        ViewHolder(View v) {
            super(v);

            tvName = v.findViewById(R.id.tvItemName);
            layout = v.findViewById(R.id.menu_layout);
        }
    }
}
