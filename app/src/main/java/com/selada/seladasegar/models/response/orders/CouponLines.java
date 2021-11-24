
package com.selada.seladasegar.models.response.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.selada.seladasegar.models.request.MetaData;

import java.util.List;

public class CouponLines {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("discount_tax")
    @Expose
    private String discount_tax;
    @SerializedName("meta_data")
    @Expose
    private List<MetaData> meta_data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscount_tax() {
        return discount_tax;
    }

    public void setDiscount_tax(String discount_tax) {
        this.discount_tax = discount_tax;
    }

    public List<MetaData> getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(List<MetaData> meta_data) {
        this.meta_data = meta_data;
    }
}
