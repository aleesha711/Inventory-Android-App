package com.android.app.inventory.dbcontract;

import android.provider.BaseColumns;


public class InventoryStockContract {

    public InventoryStockContract() {
    }

    public static final class StockEntry implements BaseColumns {

        public static final String TABLE_NAME = "stockInventory";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_IMAGE = "image";

        public static final String CREATE_TABLE_STOCK = "CREATE TABLE " +
                InventoryStockContract.StockEntry.TABLE_NAME + "(" +
                InventoryStockContract.StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryStockContract.StockEntry.COLUMN_NAME + " TEXT NOT NULL," +
                InventoryStockContract.StockEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                InventoryStockContract.StockEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0," +
                InventoryStockContract.StockEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                InventoryStockContract.StockEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL," +
                StockEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";
    }
}
