package com.selada.seladasegar.presentation.favourite;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.selada.seladasegar.R;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.models.KatalogModel;
import com.selada.seladasegar.presentation.home.HomeFeedAdapter;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.SQLDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouriteFragment extends Fragment {

    private List<KatalogModel> cartsList;
    private DBManager dbManager;

    @BindView(R.id.rvFavourite)
    RecyclerView rvFavourite;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.fetchFavourite();
        initComponent();
    }

    private void initComponent(){
        cartsList = new ArrayList<>();
        rvFavourite.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        swipeContainer.setOnRefreshListener(() -> {
            cartsList.clear();
            populateData();
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateData();

    }

    private void populateData(){
        swipeContainer.setRefreshing(true);
        Cursor cursor = dbManager.readAllDataFavourite();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Carts newCarts = new Carts();
                    newCarts.itemId = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_ID));
                    newCarts.itemName = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_NAME));
                    newCarts.itemImage = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_IMAGE));
                    newCarts.itemPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_PRICE));
                    newCarts.itemQty = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                    cartsList.add(new KatalogModel(newCarts.itemId,
                            newCarts.itemImage,
                            newCarts.itemName,
                            "",
                            newCarts.itemPrice,
                            newCarts.itemPrice,
                            newCarts.itemPrice,
                            newCarts.itemQty,
                            true));

                } while(cursor.moveToNext());

                HomeFeedAdapter adapter = new HomeFeedAdapter(cartsList, getContext(), getActivity(), true, tvNoData);
                rvFavourite.setAdapter(adapter);
                rvFavourite.scheduleLayoutAnimation();
            }
        } catch (Exception e) {
            String TAG = "SQLDatabase";
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cartsList.size() == 0) {
                tvNoData.setVisibility(View.VISIBLE);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        swipeContainer.setRefreshing(false);
    }
}
