
package com.selada.seladasegar.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StockResponse implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("permalink")
    @Expose
    private String permalink;
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("short_description")
    @Expose
    private String description;
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("stock_quantity")
    @Expose
    private String qty;
    @SerializedName("stock_status")
    @Expose
    private String stockStatus;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("catalog_visibility")
    @Expose
    private String catalog_visibility;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("price_html")
    @Expose
    private String price_html;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("regular_price")
    @Expose
    private String regular_price;
    @SerializedName("sale_price")
    @Expose
    private String sale_price;
    @SerializedName("images")
    @Expose
    private List<Images> images;
    @SerializedName("item")
    @Expose
    private Item item;
    @SerializedName("related_ids")
    @Expose
    private List<Integer> relatedIds;
    @SerializedName("categories")
    @Expose
    private List<Category> categories;

    private final static long serialVersionUID = 8464143925706847725L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_html() {
        return price_html;
    }

    public void setPrice_html(String price_html) {
        this.price_html = price_html;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public List<Integer> getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(List<Integer> relatedIds) {
        this.relatedIds = relatedIds;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getCatalog_visibility() {
        return catalog_visibility;
    }

    public void setCatalog_visibility(String catalog_visibility) {
        this.catalog_visibility = catalog_visibility;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}
