package com.selada.seladasegar.services;

import com.selada.seladasegar.R;
import com.selada.seladasegar.models.GeneralDataResponse;
import com.selada.seladasegar.models.Slider;
import com.selada.seladasegar.util.PreferenceManager;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class AdapterSliderPager extends SliderAdapter {
    List<Slider> sliderList;

    public AdapterSliderPager(List<Slider> sliderList) {
        this.sliderList = sliderList;
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(sliderList.get(position).getThumbnail().replace("$upload$", PreferenceManager.getBaseUrlSlider()), R.drawable.banner_1, R.drawable.banner_1);
    }
}

