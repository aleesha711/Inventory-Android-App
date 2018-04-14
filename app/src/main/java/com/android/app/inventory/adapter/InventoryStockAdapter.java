package com.android.app.inventory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.app.inventory.activities.MainActivity;
import com.android.app.inventory.R;
import com.android.app.inventory.dbcontract.InventoryStockContract;

public class InventoryStockAdapter extends CursorAdapter {


    private final MainActivity activity;
    TextView quantityTextView;
    Button saleBtn;

    public InventoryStockAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        quantityTextView = (TextView) view.findViewById(R.id.quantity);
        saleBtn = (Button) view.findViewById(R.id.sale_btn);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView image = (ImageView) view.findViewById(R.id.image_view);

        String name = cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_PRICE));

        image.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_IMAGE))));

        nameTextView.setText(name);
        quantityTextView.setText(String.valueOf(quantity));
        priceTextView.setText(price);

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryStockContract.StockEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickOnSale(id,
                        quantity);
            }
        });


    }

}
