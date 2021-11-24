package com.selada.seladasegar.models.response;
import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponResponse {
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
    private String dateCreated;
    @SerializedName("date_created_gmt")
    @Expose
    private String dateCreatedGmt;
    @SerializedName("date_modified")
    @Expose
    private String dateModified;
    @SerializedName("date_modified_gmt")
    @Expose
    private String dateModifiedGmt;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("date_expires")
    @Expose
    private String dateExpires;
    @SerializedName("date_expires_gmt")
    @Expose
    private String dateExpiresGmt;
    @SerializedName("usage_count")
    @Expose
    private Integer usageCount;
    @SerializedName("individual_use")
    @Expose
    private Boolean individualUse;
    @SerializedName("product_ids")
    @Expose
    private List<Integer> productIds;
    @SerializedName("excluded_product_ids")
    @Expose
    private List<Integer> excludedProductIds;
    @SerializedName("usage_limit")
    @Expose
    private Integer usageLimit;
    @SerializedName("usage_limit_per_user")
    @Expose
    private Integer usageLimitPerUser;
    @SerializedName("limit_usage_to_x_items")
    @Expose
    private Integer limitUsageToXItems;
    @SerializedName("free_shipping")
    @Expose
    private Boolean freeShipping;
    @SerializedName("product_categories")
    @Expose
    private List<Integer> productCategories;
    @SerializedName("excluded_product_categories")
    @Expose
    private List<Integer> excludedProductCategories;
    @SerializedName("exclude_sale_items")
    @Expose
    private Boolean excludeSaleItems;
    @SerializedName("minimum_amount")
    @Expose
    private String minimumAmount;
    @SerializedName("maximum_amount")
    @Expose
    private String maximumAmount;
    @SerializedName("email_restrictions")
    @Expose
    private List<String> emailRestrictions = null;
    @SerializedName("used_by")
    @Expose
    private List<String> used_by;
    private final static long serialVersionUID = -8701204942962876718L;

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

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateCreatedGmt() {
        return dateCreatedGmt;
    }

    public void setDateCreatedGmt(String dateCreatedGmt) {
        this.dateCreatedGmt = dateCreatedGmt;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getDateModifiedGmt() {
        return dateModifiedGmt;
    }

    public void setDateModifiedGmt(String dateModifiedGmt) {
        this.dateModifiedGmt = dateModifiedGmt;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(String dateExpires) {
        this.dateExpires = dateExpires;
    }

    public String getDateExpiresGmt() {
        return dateExpiresGmt;
    }

    public void setDateExpiresGmt(String dateExpiresGmt) {
        this.dateExpiresGmt = dateExpiresGmt;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Boolean getIndividualUse() {
        return individualUse;
    }

    public void setIndividualUse(Boolean individualUse) {
        this.individualUse = individualUse;
    }

    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds;
    }

    public List<Integer> getExcludedProductIds() {
        return excludedProductIds;
    }

    public void setExcludedProductIds(List<Integer> excludedProductIds) {
        this.excludedProductIds = excludedProductIds;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageLimitPerUser() {
        return usageLimitPerUser;
    }

    public void setUsageLimitPerUser(Integer usageLimitPerUser) {
        this.usageLimitPerUser = usageLimitPerUser;
    }

    public Integer getLimitUsageToXItems() {
        return limitUsageToXItems;
    }

    public void setLimitUsageToXItems(Integer limitUsageToXItems) {
        this.limitUsageToXItems = limitUsageToXItems;
    }

    public Boolean getFreeShipping() {
        return freeShipping;
    }

    public void setFreeShipping(Boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public List<Integer> getProductCategories() {
        return productCategories;
    }

    public void setProductCategories(List<Integer> productCategories) {
        this.productCategories = productCategories;
    }

    public List<Integer> getExcludedProductCategories() {
        return excludedProductCategories;
    }

    public void setExcludedProductCategories(List<Integer> excludedProductCategories) {
        this.excludedProductCategories = excludedProductCategories;
    }

    public Boolean getExcludeSaleItems() {
        return excludeSaleItems;
    }

    public void setExcludeSaleItems(Boolean excludeSaleItems) {
        this.excludeSaleItems = excludeSaleItems;
    }

    public String getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(String minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public String getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(String maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public List<String> getEmailRestrictions() {
        return emailRestrictions;
    }

    public void setEmailRestrictions(List<String> emailRestrictions) {
        this.emailRestrictions = emailRestrictions;
    }

    public List<String> getUsed_by() {
        return used_by;
    }

    public void setUsed_by(List<String> used_by) {
        this.used_by = used_by;
    }
}
