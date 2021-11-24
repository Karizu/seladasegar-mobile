package com.selada.seladasegar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slider {
    @SerializedName("published")
    @Expose
    private String published;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("ordering")
    @Expose
    private String ordering;

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getOrdering() {
        return ordering;
    }

    public void setOrdering(String ordering) {
        this.ordering = ordering;
    }
}
