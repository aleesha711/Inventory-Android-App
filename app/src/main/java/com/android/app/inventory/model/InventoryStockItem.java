package com.android.app.inventory.model;

public class InventoryStockItem {

    private final String productName;
    private final String price;
    private final int quantity;
    private final String supplierName;
    private final String supplierPhone;
    private final String image;

    public InventoryStockItem(String productName, String price, int quantity, String supplierName, String supplierPhone, String image) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhone = supplierPhone;
        this.image = image;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }


    public String getImage() {
        return image;
    }
    @Override
    public String toString() {
        return "InventoryStockItem{" +
                "productName='" + productName + '\'' +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                ", supplierName='" + supplierName + '\'' +
                ", supplierPhone='" + supplierPhone + '\'' +
                '}';
    }

}
