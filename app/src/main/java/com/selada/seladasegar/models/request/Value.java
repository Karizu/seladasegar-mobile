
package com.selada.seladasegar.models.request;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Value implements Serializable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("date_created")
    @Expose
    private DateCreated dateCreated;
    @SerializedName("date_modified")
    @Expose
    private DateModified dateModified;
    private final static long serialVersionUID = 2147117046869295956L;

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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public DateCreated getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(DateCreated dateCreated) {
        this.dateCreated = dateCreated;
    }

    public DateModified getDateModified() {
        return dateModified;
    }

    public void setDateModified(DateModified dateModified) {
        this.dateModified = dateModified;
    }

}
