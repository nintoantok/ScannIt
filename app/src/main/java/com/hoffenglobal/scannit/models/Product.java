package com.hoffenglobal.scannit.models;

/**
 * Created by nintoantok on 06/09/16.
 */
public class Product {

    String productId;
    String productName;

    public Product(String contents, String s) {

        this.productId = contents;
        this.productName = s;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
