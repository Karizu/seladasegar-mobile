package com.selada.seladasegar.presentation.home;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiAjax;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.models.Category;
import com.selada.seladasegar.models.HeaderModel;
import com.selada.seladasegar.models.HomeCategory;
import com.selada.seladasegar.models.KatalogModel;
import com.selada.seladasegar.models.Slider;
import com.selada.seladasegar.models.StockResponse;
import com.selada.seladasegar.models.response.CouponResponse;
import com.selada.seladasegar.presentation.coupons.CouponActivity;
import com.selada.seladasegar.presentation.coupons.HomeCouponAdapter;
import com.selada.seladasegar.services.AdapterSliderPager;
import com.selada.seladasegar.services.CustomLayoutManager;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private List<HomeCategory> categoryList;
    private List<HomeCategory> categoryListTemp;
    private List<HeaderModel> feedList;
    private HomeFeedAdapter homeFeedAdapter;
    private HeaderAdapter headerAdapter;
    private List<KatalogModel> katalogModelPBD, listCategoriesFromCache;
    private ViewGroup viewGroup;
    private int limit = 100;
    private int offset = 0;

    @BindView(R.id.frameSearch) FrameLayout frameSearch;
    @BindView(R.id.pagesContainer) LinearLayout linearLayout;
    @BindView(R.id.layoutFirst) LinearLayout layoutFirst;
    @BindView(R.id.layoutSecond) LinearLayout layoutSecond;
    @BindView(R.id.layout_top) RelativeLayout layoutTop;
    @BindView(R.id.banner_slider)
    ss.com.bannerslider.Slider sliderView;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.rvHomeTwo) RecyclerView rvHomeTwo;
    @BindView(R.id.shimmer_view_container_feed_one) ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.shimmer_view_container_category) ShimmerFrameLayout shimmerFrameLayoutCategory;
    @BindView(R.id.shimmer_view_container_coupon) ShimmerFrameLayout shimmer_view_container_coupon;
    @BindView(R.id.collapseActionView) CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.etSearch) EditText etSearch;
    @BindView(R.id.cvBanner) CardView cvBanner;
    @BindView(R.id.bottom_sheet) LinearLayout llBottomSheet;
    @BindView(R.id.rvBottomSheetCategory) RecyclerView rvBottomSheetCategory;
    @BindView(R.id.tvCari)
    TextView tvCari;
    @BindView(R.id.layoutSearch)
    LinearLayout layoutSearch;
    @BindView(R.id.btnShowCategory)
    RelativeLayout btnShowCategory;
    @BindView(R.id.frameSearchText)
    RelativeLayout frameSearchText;
    @BindView(R.id.layoutCategory)
    LinearLayout layoutCategory;
    @BindView(R.id.layoutCoupon)
    LinearLayout layoutCoupon;
    @BindView(R.id.tvTotalCoupon)
    TextView tvTotalCoupon;
    @BindView(R.id.tvCategoryName)
    TextView tvCategoryName;

    private void setComponentSearch(){
        tvCari.setVisibility(View.GONE);
        layoutSearch.setVisibility(View.VISIBLE);
        frameSearchText.startAnimation(inFromRightAnimation());
        btnShowCategory.setVisibility(View.GONE);

        View v = views.findViewById(R.id.frameSearchText);
        LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v.getLayoutParams();
        loparams.weight = 3;
        v.setLayoutParams(loparams);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 3){
                    List<KatalogModel> katalogModels = new ArrayList<>();
                    List<HeaderModel> headerModels = new ArrayList<>();
                    Map<String, List<KatalogModel>> sourceModels = new HashMap<>();
                    try {
//                                for (KatalogModel model: listCategoriesFromCache) {
//                                    String name = model.getName() != null ? model.getName().toLowerCase() : "";
//                                    if (name.equals("")) {
//                                    } else {
//                                        if (name.contains(editable.toString().toLowerCase())){
//                                            katalogModels.add(model);
//                                        }
//                                    }
//                                }

                        for (HomeCategory category: categoryList) {
                            for (KatalogModel model : listCategoriesFromCache) {
                                String categoryId = String.valueOf(category.id);
                                String name = model.getName() != null ? model.getName().toLowerCase() : "";
                                if (name.equals("")) {
                                } else {
                                    if (name.contains(editable.toString().toLowerCase())){
                                        for (Category category1: model.getCategories()){
                                            if (category1.getId() == Integer.parseInt(categoryId)){
                                                List<KatalogModel> value = sourceModels.get(categoryId);
                                                if (value == null) value = new ArrayList<>();

                                                value.add(model);
                                                sourceModels.put(categoryId, value);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        for (HomeCategory cat: categoryList) {
                            for (Map.Entry<String, List<KatalogModel>> entry : sourceModels.entrySet()) {
                                if (entry.getKey().equals(cat.id) && entry.getValue().size() > 0){
                                    headerModels.add(new HeaderModel(cat.name, entry.getValue()));
                                }
                            }
                        }
                        Log.d("Text", editable.toString());
                        Log.d("Header Models", new Gson().toJson(headerModels));

                        headerAdapter.updateData(headerModels);
//                                homeFeedAdapter.updateData(katalogModels);
                        notifyFeedAdapter();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if (editable.length() == 0) {
                    try {
                        headerAdapter.updateData(feedList);
//                                homeFeedAdapter.updateData(listCategoriesFromCache);
                        notifyFeedAdapter();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean isAfterClickSearch = false;
    private ProgressDialog dialog;
    private LinearLayoutManager layoutManager;
    private CustomLayoutManager layoutCustomManager, layoutManagerFeed;
    private Map<String, List<KatalogModel>> sourceNewsMap;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private View views;

    @OnClick(R.id.imgDropdown)
    void onClickimgDropdown(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @OnClick(R.id.frameSearchText)
    void onClickSearchText(){
        setComponentSearch();
        ((MainActivity) requireActivity()).handleCart(false);
    }

    @OnClick(R.id.btnShowCategory)
    void onClickBtnShowCategory() {
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        switch (bottomSheetBehavior.getState()) {
            case BottomSheetBehavior.STATE_EXPANDED:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            case BottomSheetBehavior.STATE_HIDDEN:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;
            case BottomSheetBehavior.STATE_DRAGGING:
                break;
            case BottomSheetBehavior.STATE_HALF_EXPANDED:
                break;
            case BottomSheetBehavior.STATE_SETTLING:
                break;
    }
    }

    @OnClick(R.id.btnLihatKupon)
    void onClickbtnLihatKupon(){
        Intent intent = new Intent(getActivity(), CouponActivity.class);
        requireActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
    }

    @OnClick(R.id.layCoupon)
    void onClicklayCoupon(){
        Intent intent = new Intent(getContext(), CouponActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.imgClearSearch) void onClickImgClearSearch(){
        if (etSearch.getText().toString().equals("")){
            frameSearch.setVisibility(View.GONE);
            isAfterClickSearch = false;

            tvCari.setVisibility(View.VISIBLE);
            layoutSearch.setVisibility(View.GONE);
            btnShowCategory.setVisibility(View.VISIBLE);
            View v = views.findViewById(R.id.frameSearchText);
            LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v.getLayoutParams();
            loparams.weight = (float) 0.7;
            v.setLayoutParams(loparams);
        } else {
            etSearch.setText("");
            headerAdapter.updateData(feedList);
//            homeFeedAdapter.updateData(listCategoriesFromCache);
        }
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = container;
        return inflater.inflate(R.layout.fragment_home, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        views = view;
        if (((MainActivity) requireActivity()).isIntentFromLink){
            dialog = MethodUtil.getLoadingBar(getContext(), "Mohon tunggu");
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
        }
        initComponent();
        populateSlider();
        populateData();
        populateTabData();
        populateSizeBanner();
        populateCoupon();
    }

    private void populateCoupon(){
        List<CouponResponse> couponResponseList = new ArrayList<>();
        shimmer_view_container_coupon.setVisibility(View.VISIBLE);
        shimmer_view_container_coupon.startShimmer();
        layoutCoupon.setVisibility(View.GONE);
        ApiCore.apiInterface().getCoupons("", PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<CouponResponse>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<CouponResponse>> call, Response<List<CouponResponse>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<CouponResponse> responses = response.body();
                        if (responses != null) {
                            if (responses.size() > 0) {
                                for (int i = 0; i < responses.size(); i++){
                                    CouponResponse couponRes = responses.get(i);
                                    if (couponRes.getDateExpires()!=null){
                                        String dateExpired;
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date strDate = null;

                                        try {
                                            dateExpired = couponRes.getDateExpires().replace("T", " ");
                                            strDate = sdf.parse(dateExpired);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (new Date().after(strDate)) {
                                            //skipp
                                        } else {
                                            couponResponseList.add(couponRes);
                                        }
                                    }
                                }



                                int totalCoupon = Math.max(couponResponseList.size(), 0);

                                shimmer_view_container_coupon.stopShimmer();
                                shimmer_view_container_coupon.setVisibility(View.GONE);

                                if (totalCoupon > 0){
                                    tvTotalCoupon.setText("Ada " + totalCoupon + " kupon belanja menunggumu");
                                    layoutCoupon.setVisibility(View.VISIBLE);
                                } else {
                                    layoutCoupon.setVisibility(View.GONE);
                                }

                            } else {
                                shimmer_view_container_coupon.stopShimmer();
                                shimmer_view_container_coupon.setVisibility(View.GONE);
                                layoutCoupon.setVisibility(View.GONE);
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
                shimmer_view_container_coupon.stopShimmer();
                shimmer_view_container_coupon.setVisibility(View.GONE);
            }
        });
    }

    private void initComponent() {
        categoryList = new ArrayList<>();
        categoryListTemp = new ArrayList<>();
        katalogModelPBD = new ArrayList<>();
        sourceNewsMap = new HashMap<>();
        feedList = new ArrayList<>();

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManagerFeed = new CustomLayoutManager(getActivity());
        layoutCustomManager = new CustomLayoutManager(getActivity());
        rvHomeTwo.setLayoutManager(new CustomLayoutManager(getActivity()));
        rvBottomSheetCategory.setLayoutManager(layoutManager);
        rvBottomSheetCategory.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        swipeContainer.setOnRefreshListener(() -> {
            swipeContainer.setRefreshing(false);
            katalogModelPBD.clear();
            categoryList.clear();
            categoryListTemp.clear();
            offset = 0;
            populateSlider();
            populateFeedHomePBD();
            populateCategories();
            populateCoupon();
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ((MainActivity) requireActivity()).toolbarSearch.setOnClickListener(view1 -> {
            if (frameSearch.getVisibility() == View.VISIBLE){
                frameSearch.setVisibility(View.GONE);
                isAfterClickSearch = false;
//                MethodUtil.toggleTransitionSlideBottom(viewGroup, frameSearch, false);
            }
            else {
                isAfterClickSearch = true;
                collapsingToolbarLayout.requestFocus();
                frameSearch.setVisibility(View.VISIBLE);
//                MethodUtil.toggleTransitionSlideBottom(viewGroup, frameSearch, true);
                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (editable.length() > 3){
                            List<KatalogModel> katalogModels = new ArrayList<>();
                            List<HeaderModel> headerModels = new ArrayList<>();
                            Map<String, List<KatalogModel>> sourceModels = new HashMap<>();
                            try {
//                                for (KatalogModel model: listCategoriesFromCache) {
//                                    String name = model.getName() != null ? model.getName().toLowerCase() : "";
//                                    if (name.equals("")) {
//                                    } else {
//                                        if (name.contains(editable.toString().toLowerCase())){
//                                            katalogModels.add(model);
//                                        }
//                                    }
//                                }

                                for (HomeCategory category: categoryList) {
                                    for (KatalogModel model : listCategoriesFromCache) {
                                        String categoryId = String.valueOf(category.id);
                                        String name = model.getName() != null ? model.getName().toLowerCase() : "";
                                        if (name.equals("")) {
                                        } else {
                                            if (name.contains(editable.toString().toLowerCase())){
                                                for (Category category1: model.getCategories()){
                                                    if (category1.getId() == Integer.parseInt(categoryId)){
                                                        List<KatalogModel> value = sourceModels.get(categoryId);
                                                        if (value == null) value = new ArrayList<>();

                                                        value.add(model);
                                                        sourceModels.put(categoryId, value);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for (HomeCategory cat: categoryList) {
                                    for (Map.Entry<String, List<KatalogModel>> entry : sourceModels.entrySet()) {
                                        if (entry.getKey().equals(cat.id) && entry.getValue().size() > 0){
                                            headerModels.add(new HeaderModel(cat.name, entry.getValue()));
                                        }
                                    }
                                }
                                Log.d("Text", editable.toString());
                                Log.d("Header Models", new Gson().toJson(headerModels));

                                headerAdapter.updateData(headerModels);
//                                homeFeedAdapter.updateData(katalogModels);
                                notifyFeedAdapter();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        } else if (editable.length() == 0) {
                            try {
                                headerAdapter.updateData(feedList);
//                                homeFeedAdapter.updateData(listCategoriesFromCache);
                                notifyFeedAdapter();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

//        bottomSheetView.findViewById(R.id.folder).setOnClickListener(view -> {
//            Toast.makeText(ModalActivity.this, “Folder Clicked..”, Toast.LENGTH_SHORT).show();
//            bottomSheetDialog.dismiss();
//        });

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        System.out.println("KEY FROM PREF : " + PreferenceManager.getConsumerKey());
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private void populateData(){
        populateFeedHomePBD();
//        if (PreferenceManageromeFeedOne() == null || PreferenceManager.getHomeFeedOne().size() == 0 ) {
////            populateFeedHomePBD();
////        } else {
////            listCategoriesFromCache = PreferenceManager.getHomeFeedOne();
////            homeFeedAdapter = new HomeFeedAdapter(listCategoriesFromCache, getContext(), getActivity());
////            rvHomeTwo.setAdapter(homeFeedAdapter);
////            notifyFeedAdapter();
////            swipeContainer.setRefreshing(false);
////        }.getH
    }

    private void populateTabData(){
        populateCategories();
//        if (PreferenceManager.getMenuCategory() == null || PreferenceManager.getMenuCategory().size() == 0 ) {
//            populateCategories();
//        } else {
//            List<HomeCategory> categories = PreferenceManager.getMenuCategory();
//            Log.d("tabData", new Gson().toJson(categories));
//            setTabLayoutData(categories);
//        }
    }

    private void loadNextDataFromApi() {
        try {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            rvHomeTwo.setVisibility(View.GONE);

            offset += 100;
            ApiCore.apiInterface().getFeedHomeOne(String.valueOf(limit), String.valueOf(offset), Constant.PUBLISH, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<StockResponse>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<StockResponse>> call, Response<List<StockResponse>> response) {
                    try {
                        List<StockResponse> res = Objects.requireNonNull(response.body());
                        if (res.size() > 0) {
                            for (int i = 0; i < res.size(); i++) {
                                StockResponse itemResponse = res.get(i);
                                String visibility = itemResponse.getCatalog_visibility();
                                if (!itemResponse.getStockStatus().equals(Constant.OUT_OF_STOCK) && visibility.equals(Constant.STATUS_VISIBLE)) {
//                                    if (visibility.equals(Constant.STATUS_VISIBLE) && !itemResponse.getType().equals(Constant.TYPE_VARIABLE)) {
                                    katalogModelPBD.add(new KatalogModel(itemResponse.getId() + "",
                                            itemResponse.getImages() != null ? itemResponse.getImages().size() > 0 ? itemResponse.getImages().get(0).src : "" : "",
                                            itemResponse.getName(),
                                            itemResponse.getDescription(),
                                            itemResponse.getPrice_html(),
                                            itemResponse.getSale_price(),
                                            itemResponse.getRegular_price(),
                                            itemResponse.getQty()!= null ? itemResponse.getQty() : "0",
                                            itemResponse.getStockStatus(),
                                            itemResponse.getRelatedIds(),
                                            itemResponse.getCategories(),
                                            itemResponse.getPermalink()));
                                }
                            }

                            loadNextDataFromApi2();

                        } else {
                            if (katalogModelPBD.size()>0){
                                Collections.sort(katalogModelPBD, (katalogModel, t1) -> katalogModel.getCategories().get(0).getId().compareTo(t1.getCategories().get(0).getId()));
                                Collections.sort(katalogModelPBD, (katalogModel, t1) -> katalogModel.getStock_status().compareTo(t1.getStock_status()));

//                                PreferenceManager.setHomeFeedOne(katalogModelPBD);
                                listCategoriesFromCache = katalogModelPBD;
//                                listCategoriesFromCache = PreferenceManager.getHomeFeedOne();

//                            homeFeedAdapter = new HomeFeedAdapter(katalogModelPBD, getContext(), getActivity());

                                for (HomeCategory category: categoryList) {
                                    for (KatalogModel model : listCategoriesFromCache) {
                                        String categoryId = String.valueOf(category.id);
                                        for (Category category1: model.getCategories()){
                                            if (category1.getId() == Integer.parseInt(categoryId)){
                                                List<KatalogModel> value = sourceNewsMap.get(categoryId);
                                                if (value == null) value = new ArrayList<>();

                                                value.add(model);
                                                sourceNewsMap.put(categoryId, value);
                                            }
                                        }
                                    }
                                }

                                for (HomeCategory cat: categoryList) {
                                    for (Map.Entry<String, List<KatalogModel>> entry : sourceNewsMap.entrySet()) {
                                        if (entry.getKey().equals(cat.id) && entry.getValue().size() > 0){
                                            feedList.add(new HeaderModel(cat.name, entry.getValue()));
                                        }
                                    }
                                }

                                headerAdapter = new HeaderAdapter(getActivity(), getContext(), feedList, HomeFragment.this);
                                setRecyclerView();

                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                rvHomeTwo.setVisibility(View.VISIBLE);

                                showDetailItemFromLink();
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        rvHomeTwo.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<StockResponse>> call, Throwable t) {
                    t.printStackTrace();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    rvHomeTwo.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerView() {
        rvHomeTwo.setLayoutManager(layoutManagerFeed);
        rvHomeTwo.setAdapter(headerAdapter);
        rvHomeTwo.scheduleLayoutAnimation();
        rvHomeTwo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0){
                    int itemCount = layoutManagerFeed.findFirstVisibleItemPosition();
                    Log.d("itemCount", itemCount+"");
                    HeaderModel category = feedList.get(itemCount);
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
                    tvCategoryName.setText(categoryName);
                } else if (dy < 0){
                    int itemCount = layoutManagerFeed.findFirstVisibleItemPosition();
                    Log.d("itemCount", itemCount+"");
                    HeaderModel category = feedList.get(itemCount);
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
                    tvCategoryName.setText(categoryName);
                }
            }
        });
        notifyFeedAdapter();
    }

    private void loadNextDataFromApi2() {
        try {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
            rvHomeTwo.setVisibility(View.GONE);

            offset += 100;
            ApiCore.apiInterface().getFeedHomeOne(String.valueOf(limit), String.valueOf(offset), Constant.PUBLISH, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<StockResponse>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onResponse(Call<List<StockResponse>> call, Response<List<StockResponse>> response) {
                    try {
                        List<StockResponse> res = Objects.requireNonNull(response.body());
                        if (res.size() > 0) {
                            for (int i = 0; i < res.size(); i++) {
                                StockResponse itemResponse = res.get(i);
                                String visibility = itemResponse.getCatalog_visibility();
                                if (!itemResponse.getStockStatus().equals(Constant.OUT_OF_STOCK) && visibility.equals(Constant.STATUS_VISIBLE)) {
//                                    if (visibility.equals(Constant.STATUS_VISIBLE) && !itemResponse.getType().equals(Constant.TYPE_VARIABLE)) {
                                    katalogModelPBD.add(new KatalogModel(itemResponse.getId() + "",
                                            itemResponse.getImages() != null ? itemResponse.getImages().size() > 0 ? itemResponse.getImages().get(0).src : "" : "",
                                            itemResponse.getName(),
                                            itemResponse.getDescription(),
                                            itemResponse.getPrice_html(),
                                            itemResponse.getSale_price(),
                                            itemResponse.getRegular_price(),
                                            itemResponse.getQty()!= null ? itemResponse.getQty() : "0",
                                            itemResponse.getStockStatus(),
                                            itemResponse.getRelatedIds(),
                                            itemResponse.getCategories(),
                                            itemResponse.getPermalink()));
                                }
                            }

                            Collections.sort(katalogModelPBD, (katalogModel, t1) -> katalogModel.getCategories().get(0).getId().compareTo(t1.getCategories().get(0).getId()));
                            Collections.sort(katalogModelPBD, (katalogModel, t1) -> katalogModel.getStock_status().compareTo(t1.getStock_status()));

                            PreferenceManager.setHomeFeedOne(katalogModelPBD);
                            listCategoriesFromCache = katalogModelPBD;
//                            listCategoriesFromCache = PreferenceManager.getHomeFeedOne();

//                            homeFeedAdapter = new HomeFeedAdapter(katalogModelPBD, getContext(), getActivity());

                            for (HomeCategory category: categoryList) {
                                for (KatalogModel model : listCategoriesFromCache) {
                                    String categoryId = String.valueOf(category.id);
                                    for (Category category1: model.getCategories()){
                                        if (category1.getId() == Integer.parseInt(categoryId)){
                                            List<KatalogModel> value = sourceNewsMap.get(categoryId);
                                            if (value == null) value = new ArrayList<>();

                                            value.add(model);
                                            sourceNewsMap.put(categoryId, value);
                                        }
                                    }
                                }
                            }

                            for (HomeCategory cat: categoryList) {
                                for (Map.Entry<String, List<KatalogModel>> entry : sourceNewsMap.entrySet()) {
                                    if (entry.getKey().equals(cat.id) && entry.getValue().size() > 0){
                                        feedList.add(new HeaderModel(cat.name, entry.getValue()));
                                    }
                                }
                            }

                            headerAdapter = new HeaderAdapter(getActivity(), getContext(), feedList, HomeFragment.this);
                            setRecyclerView();

                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);
                            rvHomeTwo.setVisibility(View.VISIBLE);

                            showDetailItemFromLink();

                        } else {
                            if (katalogModelPBD.size()>0){
                                Collections.sort(katalogModelPBD, (katalogModel, t1) -> katalogModel.getCategories().get(0).getId().compareTo(t1.getCategories().get(0).getId()));
                                Collections.sort(katalogModelPBD, (katalogModel, t1) -> katalogModel.getStock_status().compareTo(t1.getStock_status()));

//                                PreferenceManager.setHomeFeedOne(katalogModelPBD);
                                listCategoriesFromCache = katalogModelPBD;
//                                listCategoriesFromCache = PreferenceManager.getHomeFeedOne();
//                                homeFeedAdapter = new HomeFeedAdapter(katalogModelPBD, getContext(), getActivity());

                                for (HomeCategory category: categoryList) {
                                    for (KatalogModel model : listCategoriesFromCache) {
                                        String categoryId = String.valueOf(category.id);
                                        for (Category category1: model.getCategories()){
                                            if (category1.getId() == Integer.parseInt(categoryId)){
                                                List<KatalogModel> value = sourceNewsMap.get(categoryId);
                                                if (value == null) value = new ArrayList<>();

                                                value.add(model);
                                                sourceNewsMap.put(categoryId, value);
                                            }
                                        }
                                    }
                                }

                                for (HomeCategory cat: categoryList) {
                                    for (Map.Entry<String, List<KatalogModel>> entry : sourceNewsMap.entrySet()) {
                                        if (entry.getKey().equals(cat.id) && entry.getValue().size() > 0){
                                            feedList.add(new HeaderModel(cat.name, entry.getValue()));
                                        }
                                    }
                                }

                                headerAdapter = new HeaderAdapter(getActivity(), getContext(), feedList, HomeFragment.this);
                                setRecyclerView();

                                shimmerFrameLayout.stopShimmer();
                                shimmerFrameLayout.setVisibility(View.GONE);
                                rvHomeTwo.setVisibility(View.VISIBLE);

                                showDetailItemFromLink();
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        rvHomeTwo.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<List<StockResponse>> call, Throwable t) {
                    t.printStackTrace();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    rvHomeTwo.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSizeBanner(){
        cvBanner.post(() -> {
            int width = cvBanner.getWidth();
            double heigth = width * 0.45;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cvBanner.getLayoutParams();
            params.height = (int) heigth;
            cvBanner.setLayoutParams(params);
        });
    }

    private void populateSlider() {
        try {
            swipeContainer.setRefreshing(true);
            ApiAjax.apiInterface().getSlider(Constant.ACTION_GET_SLIDERS).enqueue(new Callback<List<com.selada.seladasegar.models.Slider>>() {
                @Override
                public void onResponse(Call<List<com.selada.seladasegar.models.Slider>> call, Response<List<com.selada.seladasegar.models.Slider>> response) {
                    try {
                        swipeContainer.setRefreshing(false);
                        if (response.isSuccessful()){
                            populateImageSlider(response.body());
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<com.selada.seladasegar.models.Slider>> call, Throwable t) {
                    swipeContainer.setRefreshing(false);
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            swipeContainer.setRefreshing(false);
        }
    }

    private void populateImageSlider(List<Slider> data) {
        sliderView.setAdapter(new AdapterSliderPager(data));
        sliderView.setLoopSlides(true);
        sliderView.setInterval(3000);
        sliderView.setOnSlideClickListener(position -> {

        });
    }

    private void populateCategories() {
        tabLayout.setVisibility(View.GONE);
        layoutCategory.setVisibility(View.GONE);
        shimmerFrameLayoutCategory.setVisibility(View.VISIBLE);
        shimmerFrameLayoutCategory.startShimmer();
        ApiCore.apiInterface().getCategories(PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<HomeCategory>>() {
            @Override
            public void onResponse(Call<List<HomeCategory>> call, Response<List<HomeCategory>> response) {
                try {
                    List<HomeCategory> res = Objects.requireNonNull(response.body());
                    for (HomeCategory category: res){
                        //handle sort if desc is integer
                        if (!category.slug.equals("uncategorized")){
                            try {
                                int i = Integer.parseInt(category.description.equals("")?"null":category.description);
                                categoryList.add(category);
                            } catch (Exception e) {
                                categoryListTemp.add(category);
                                e.printStackTrace();
                            }
                        }
                    }

                    //handle sort by desc
                    if (categoryList.size()>0) Collections.sort(categoryList, (katalogModel, t1) -> katalogModel.description.compareTo(t1.description));

                    //add list if desc non int
                    if (categoryListTemp.size()>0){
                        categoryList.addAll(categoryListTemp);
                    }

                    BottomSheetCategoryAdapter adapter = new BottomSheetCategoryAdapter(categoryList, getContext(), HomeFragment.this);
                    rvBottomSheetCategory.setAdapter(adapter);

//                    setTabLayoutData(categoryList);
//                    PreferenceManager.setMenuCategory(categoryList);

                    shimmerFrameLayoutCategory.stopShimmer();
                    shimmerFrameLayoutCategory.setVisibility(View.GONE);
//                    tabLayout.setVisibility(View.VISIBLE);
                    layoutCategory.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<HomeCategory>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void onClickItem(int position){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        rvHomeTwo.smoothScrollToPosition(position);
    }

    private void populateFeedHomePBD() {
        feedList.clear();
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        rvHomeTwo.setVisibility(View.GONE);

        ApiCore.apiInterface().getFeedHomeOne(String.valueOf(limit), String.valueOf(offset), Constant.PUBLISH, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<StockResponse>>() {
            @Override
            public void onResponse(Call<List<StockResponse>> call, Response<List<StockResponse>> response) {
                try {
                    List<StockResponse> res = Objects.requireNonNull(response.body());
                    for (int i = 0; i < res.size(); i++) {
                        StockResponse itemResponse = res.get(i);
                        String visibility = itemResponse.getCatalog_visibility();
                        if (!itemResponse.getStockStatus().equals(Constant.OUT_OF_STOCK) && visibility.equals(Constant.STATUS_VISIBLE)) {
//                                    if (visibility.equals(Constant.STATUS_VISIBLE) && !itemResponse.getType().equals(Constant.TYPE_VARIABLE)) {
                            katalogModelPBD.add(new KatalogModel(itemResponse.getId() + "",
                                    itemResponse.getImages() != null ? itemResponse.getImages().size() > 0 ? itemResponse.getImages().get(0).src : "" : "",
                                    itemResponse.getName(),
                                    itemResponse.getDescription(),
                                    itemResponse.getPrice_html(),
                                    itemResponse.getSale_price(),
                                    itemResponse.getRegular_price(),
                                    itemResponse.getQty()!= null ? itemResponse.getQty() : "0",
                                    itemResponse.getStockStatus(),
                                    itemResponse.getRelatedIds(),
                                    itemResponse.getCategories(),
                                    itemResponse.getPermalink()));
                        }
                    }

                    loadNextDataFromApi();

//                    PreferenceManager.setHomeFeedOne(katalogModelPBD);
//                    homeFeedAdapter = new HomeFeedAdapter(katalogModelPBD, getContext(), getActivity());
//                    rvHomeTwo.setAdapter(homeFeedAdapter);
//                    rvHomeTwo.scheduleLayoutAnimation();
//
//                    swipeContainer.setEnabled(false);
//                    shimmerFrameLayout.stopShimmer();
//                    shimmerFrameLayout.setVisibility(View.GONE);

                } catch (Exception e) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<StockResponse>> call, Throwable t) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                rvHomeTwo.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }

    private void setTabLayoutData(List<HomeCategory> categories){
        tabLayout.removeAllTabs();
        tabLayout.setSelectedTabIndicatorHeight(0);
        List<String> listCategoryId = new ArrayList<>();
        listCategoryId.add("0");
        tabLayout.addTab(tabLayout.newTab().setText("All"));

        for (HomeCategory category: categories) {
            String categoryName = null;
            try {
                categoryName = URLDecoder.decode(category.name, "UTF-8");
                if (categoryName.contains("&amp;")){
                    String[] catName = categoryName.split("amp;");
                    categoryName = catName[0] + catName[1];
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            listCategoryId.add(category.id);
            tabLayout.addTab(tabLayout.newTab().setText(categoryName));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                listCategoriesFromCache = PreferenceManager.getHomeFeedOne();
                if (listCategoriesFromCache != null) {
                    if (tab.getPosition() == 0) {
                        homeFeedAdapter.updateData(listCategoriesFromCache);
                        notifyFeedAdapter();
                    } else {

                        rvHomeTwo.smoothScrollToPosition(10);
//                        List<KatalogModel> katalogModels = new ArrayList<>();
//                        String catId = listCategoryId.get(tab.getPosition());
//                        for (KatalogModel model: listCategoriesFromCache) {
//                            if (model.getCategories()!=null){
//                                if (model.getCategories().size() > 0){
//                                    for (Category category: model.getCategories()){
//                                        String categoryId = String.valueOf(category.getId());
//                                        if (categoryId.equals(catId)){
//                                            katalogModels.add(model);
//                                            Log.d("Item", model.getName());
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        Log.d("Models", new Gson().toJson(katalogModels));
//                        homeFeedAdapter.updateData(katalogModels);
//                        notifyFeedAdapter();

//                        onClickSearh(katalogModels);
                    }
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void onClickSearh(List<KatalogModel> katalogModels) {
        if (isAfterClickSearch){
            collapsingToolbarLayout.requestFocus();
            frameSearch.setVisibility(View.VISIBLE);
//                MethodUtil.toggleTransitionSlideBottom(viewGroup, frameSearch, true);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 3){
                        List<KatalogModel> katalog = new ArrayList<>();
                        for (KatalogModel model: katalogModels) {
                            String name = model.getName() != null ? model.getName().toLowerCase() : "";
                            if (name.equals("")) {
                            } else {
                                if (name.contains(editable.toString().toLowerCase())){
                                    katalog.add(model);
                                }
                            }
                        }
                        homeFeedAdapter.updateData(katalog);
                        notifyFeedAdapter();
                    } else if (editable.length() == 0) {
                        homeFeedAdapter.updateData(katalogModels);
                        notifyFeedAdapter();
                    }
                }
            });
        }
    }

    public void notifyFeedAdapter(){
        if (headerAdapter!=null){
            ((MainActivity) requireActivity()).setFeedAdapter(homeFeedAdapter, headerAdapter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDetailItemFromLink(){
        dialog.dismiss();
        if (headerAdapter!=null){
            if (katalogModelPBD.size() > 0 && ((MainActivity) requireActivity()).isIntentFromLink){
                for (KatalogModel model: katalogModelPBD) {
                    if (model.getPermalink().contains(((MainActivity) requireActivity()).linkData + "/")){
                        Carts carts = new Carts();
                        carts.itemId = model.getId();
                        carts.itemName = model.getName();
                        carts.itemPrice = model.getSale_price();
                        carts.itemTotalPrice = model.getSale_price();
                        carts.itemImage = model.getImage();
                        carts.itemStok = model.getStock_quantity();
                        String salePrice = model.getSale_price();
                        if (salePrice.equals("") || salePrice.equalsIgnoreCase("null")) {
                            salePrice = model.getRegular_price();
                        }
                        headerAdapter.initDialogCart(model.getName(), model.getDeskripsi(), salePrice, model.getRegular_price(), model.getKategori(), model.getImage(), model.getStock_status(), carts, Integer.parseInt(model.getStock_quantity()), model.getPermalink());
                        ((MainActivity) requireActivity()).isIntentFromLink = false;
                    }
                }
            }
        }
    }

    public List<HeaderModel> headerModel(){
        return feedList;
    }
}
