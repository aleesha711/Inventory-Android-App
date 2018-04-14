package com.android.app.inventory.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.app.inventory.dbcontract.InventoryStockContract;
import com.android.app.inventory.model.InventoryStockItem;

public class InventoryStockDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = InventoryStockDbHelper.class.getCanonicalName();

    public InventoryStockDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InventoryStockContract.StockEntry.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertItem(InventoryStockItem stockitem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryStockContract.StockEntry.COLUMN_NAME, stockitem.getProductName());
        values.put(InventoryStockContract.StockEntry.COLUMN_PRICE, stockitem.getPrice());
        values.put(InventoryStockContract.StockEntry.COLUMN_QUANTITY, stockitem.getQuantity());
        values.put(InventoryStockContract.StockEntry.COLUMN_SUPPLIER_NAME, stockitem.getSupplierName());
        values.put(InventoryStockContract.StockEntry.COLUMN_SUPPLIER_PHONE, stockitem.getSupplierPhone());
        values.put(InventoryStockContract.StockEntry.COLUMN_IMAGE, stockitem.getImage());
        db.insert(InventoryStockContract.StockEntry.TABLE_NAME, null, values);
    }

    public Cursor readStock() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                InventoryStockContract.StockEntry._ID,
                InventoryStockContract.StockEntry.COLUMN_NAME,
                InventoryStockContract.StockEntry.COLUMN_PRICE,
                InventoryStockContract.StockEntry.COLUMN_QUANTITY,
                InventoryStockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                InventoryStockContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                InventoryStockContract.StockEntry.COLUMN_IMAGE
        };
        Cursor cursor = db.query(
                InventoryStockContract.StockEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readStockItem(long itemId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                InventoryStockContract.StockEntry._ID,
                InventoryStockContract.StockEntry.COLUMN_NAME,
                InventoryStockContract.StockEntry.COLUMN_PRICE,
                InventoryStockContract.StockEntry.COLUMN_QUANTITY,
                InventoryStockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                InventoryStockContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                InventoryStockContract.StockEntry.COLUMN_IMAGE
        };
        String selection = InventoryStockContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };

        Cursor cursor = db.query(
                InventoryStockContract.StockEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateStockItem(long currentItemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryStockContract.StockEntry.COLUMN_QUANTITY, quantity);
        String selection = InventoryStockContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(currentItemId) };
        db.update(InventoryStockContract.StockEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public void sellOneStockItem(long itemId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity -1;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryStockContract.StockEntry.COLUMN_QUANTITY, newQuantity);
        String selection = InventoryStockContract.StockEntry._ID + "=?";
        String[] selectionArgs = new String[] { String.valueOf(itemId) };
        db.update(InventoryStockContract.StockEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }
}
