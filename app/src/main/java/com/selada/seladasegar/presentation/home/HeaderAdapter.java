package com.selada.seladasegar.presentation.home;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.selada.seladasegar.R;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.models.HeaderModel;
import com.selada.seladasegar.models.HomeCategory;
import com.selada.seladasegar.models.KatalogModel;
import com.selada.seladasegar.models.response.orders.Category;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

public class HeaderAdapter extends RecyclerView.Adapter<HeaderViewHolder>  {

    private final Context context;
    private List<HeaderModel> sourceList;
    private Activity activity;
    private HomeFeedAdapter adapter;
    private HomeFragment homeFragment;

    public HeaderAdapter(Activity activity, Context context, List<HeaderModel> sourceList, HomeFragment homeFragment) {
        this.activity = activity;
        this.context = context;
        this.sourceList = sourceList;
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_home_feed_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        HeaderModel category = sourceList.get(position);
        String categoryName = null;
        try {
            categoryName = URLDecoder.decode(category.getCategoryName(), "UTF-8");
            if (categoryName.contains("&amp;")){
                String[] catName = categoryName.split("amp;");
                categoryName = catName[0] + catName[1];
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.setSourceName(categoryName);
        adapter = new HomeFeedAdapter(category.getKatalogModels(), context, activity);
        holder.setupRecyclerView(context, adapter);
    }

    @Override
    public int getItemCount() {
        return sourceList == null ? 0 : sourceList.size();
    }

    public void updateData(List<HeaderModel> list) {
        sourceList = list;
        notifyDataSetChanged();
    }

    public void syncItemCart() {
        sourceList = homeFragment.headerModel();
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initDialogCart(String productName, String itemDescription, String price, String priceStrike, String categoryName, String img, String stockStatus, Carts carts, int stock, String permalink) {
        adapter.initDialogCart(productName, itemDescription, price, priceStrike, categoryName, img, stockStatus, carts, stock, permalink);
    }
}
