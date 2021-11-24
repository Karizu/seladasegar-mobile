package com.selada.seladasegar.models;
import java.util.List;

public class LineItemModel {
    private List<Carts> lineItems;

    public List<Carts> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<Carts> lineItems) {
        this.lineItems = lineItems;
    }
}
