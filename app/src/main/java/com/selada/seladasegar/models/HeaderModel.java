package com.selada.seladasegar.models;

import java.util.List;

public class HeaderModel {
    private final String categoryName;
    private final List<KatalogModel> katalogModels;

    public HeaderModel(String categoryName, List<KatalogModel> articleList) {
        this.categoryName = categoryName;
        this.katalogModels = articleList;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public List<KatalogModel> getKatalogModels() {
        return katalogModels;
    }
}
