package com.selada.seladasegar.models;

import java.util.List;

public class KatalogModel {

    private String id;
    private String image;
    private String name;
    private String deskripsi;
    private String harga;
    private String sale_price;
    private String regular_price;
    private String kategori;
    private String stock_quantity;
    private String stock_status;
    private String total_today_qty;
    private String date;
    private boolean isClickAdd;
    private String permalink;
    private List<Integer> related_ids;
    private List<Category> categories;
    private boolean is_from_favourite = false;


    public KatalogModel(String id, String image, String name, String deskripsi, String harga, String sale_price, String regular_price, String stock_quantity, String stock_status, List<Integer> related_ids, List<Category> categories, String permalink) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.sale_price = sale_price;
        this.regular_price = regular_price;
        this.stock_quantity = stock_quantity;
        this.stock_status = stock_status;
        this.related_ids = related_ids;
        this.categories = categories;
        this.permalink = permalink;
    }

    public KatalogModel(String id, String image, String name, String deskripsi, String harga, String sale_price, String regular_price, String stock_quantity, boolean is_from_favourite) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.sale_price = sale_price;
        this.regular_price = regular_price;
        this.stock_quantity = stock_quantity;
        this.is_from_favourite = is_from_favourite;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getRegular_price() {
        return regular_price;
    }

    public void setRegular_price(String regular_price) {
        this.regular_price = regular_price;
    }

    public String getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(String stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public String getStock_status() {
        return stock_status;
    }

    public void setStock_status(String stock_status) {
        this.stock_status = stock_status;
    }

    public String getTotal_today_qty() {
        return total_today_qty;
    }

    public void setTotal_today_qty(String total_today_qty) {
        this.total_today_qty = total_today_qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public boolean isIs_from_favourite() {
        return is_from_favourite;
    }

    public void setIs_from_favourite(boolean is_from_favourite) {
        this.is_from_favourite = is_from_favourite;
    }

    public List<Integer> getRelated_ids() {
        return related_ids;
    }

    public void setRelated_ids(List<Integer> related_ids) {
        this.related_ids = related_ids;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public boolean isClickAdd() {
        return isClickAdd;
    }

    public void setClickAdd(boolean clickAdd) {
        isClickAdd = clickAdd;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }
}
