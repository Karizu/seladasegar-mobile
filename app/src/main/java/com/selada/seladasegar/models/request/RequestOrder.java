package com.selada.seladasegar.models.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.selada.seladasegar.models.response.orders.CouponLines;
import com.selada.seladasegar.models.response.orders.FeeLine;

import java.util.List;

public class RequestOrder {
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("payment_method")
    @Expose
    private String paymentMethod;
    @SerializedName("payment_method_title")
    @Expose
    private String paymentMethodTitle;
    @SerializedName("customer_note")
    @Expose
    private String customer_note;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("set_paid")
    @Expose
    private Boolean setPaid;
    @SerializedName("billing")
    @Expose
    private Billing billing;
    @SerializedName("shipping")
    @Expose
    private Shipping shipping;
    @SerializedName("line_items")
    @Expose
    private List<LineItem> lineItems;
    @SerializedName("shipping_lines")
    @Expose
    private List<ShippingLine> shippingLines;
    @SerializedName("fee_lines")
    @Expose
    private List<FeeLine> feeLines;
    @SerializedName("coupon_lines")
    @Expose
    private List<CouponLines> coupon_lines;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethodTitle() {
        return paymentMethodTitle;
    }

    public void setPaymentMethodTitle(String paymentMethodTitle) {
        this.paymentMethodTitle = paymentMethodTitle;
    }

    public Boolean getSetPaid() {
        return setPaid;
    }

    public void setSetPaid(Boolean setPaid) {
        this.setPaid = setPaid;
    }

    public Billing getBilling() {
        return billing;
    }

    public void setBilling(Billing billing) {
        this.billing = billing;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public List<ShippingLine> getShippingLines() {
        return shippingLines;
    }

    public void setShippingLines(List<ShippingLine> shippingLines) {
        this.shippingLines = shippingLines;
    }

    public List<FeeLine> getFeeLines() {
        return feeLines;
    }

    public void setFeeLines(List<FeeLine> feeLines) {
        this.feeLines = feeLines;
    }

    public String getCustomer_note() {
        return customer_note;
    }

    public void setCustomer_note(String customer_note) {
        this.customer_note = customer_note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CouponLines> getCoupon_lines() {
        return coupon_lines;
    }

    public void setCoupon_lines(List<CouponLines> coupon_lines) {
        this.coupon_lines = coupon_lines;
    }
}
