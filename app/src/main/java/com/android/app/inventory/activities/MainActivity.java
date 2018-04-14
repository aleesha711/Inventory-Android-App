package com.android.app.inventory.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.app.inventory.R;
import com.android.app.inventory.adapter.InventoryStockAdapter;
import com.android.app.inventory.dbhelper.InventoryStockDbHelper;
import com.android.app.inventory.model.InventoryStockItem;


public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    InventoryStockDbHelper dbHelper;
    InventoryStockAdapter adapter;
    int lastVisibleItem = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new InventoryStockDbHelper(this);;

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StockProductsDetailsActivity.class);
                startActivity(intent);
            }
        });


        final ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        Cursor cursor = dbHelper.readStock();

        adapter = new InventoryStockAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0) return;
                    final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > lastVisibleItem) {
                        fab.show();
                    } else if (currentFirstVisibleItem < lastVisibleItem) {
                        fab.hide();
                    }
                lastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readStock());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, StockProductsDetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellOneStockItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }

    private void addDefaultItems() {
        InventoryStockItem p1 = new InventoryStockItem(
            "Laptop Dell",
                "1000 $",
                45,
                "Robert Kinh",
                "+49 000 000 0000",
                "android.resource://com.android.app.inventory/drawable/image");
        dbHelper.insertItem(p1);

        InventoryStockItem p2 = new InventoryStockItem(
                "Laptop I5 Sony",
                "1230 $",
                24,
                "Patrick Bienert",
                "+49 000 000 0000",
                "android.resource://com.android.app.inventory/drawable/image");
        dbHelper.insertItem(p2);

        InventoryStockItem p3 = new InventoryStockItem(
                "Laptop toshiba 17",
                "1100 $",
                74,
                "Ali saeed",
                "+49 000 000 0000",
                "android.resource://com.android.app.inventory/drawable/image");
        dbHelper.insertItem(p3);

        InventoryStockItem p4 = new InventoryStockItem(
                "MacBook Pro",
                "130 $",
                44,
                "Anisely Williams",
                "+49 000 000 0000",
                "android.resource://com.android.app.inventory/drawable/image");
        dbHelper.insertItem(p4);

        InventoryStockItem p5 = new InventoryStockItem(
                "Laptop i5 Lenovo",
                "2050 $",
                34,
                "Grahm Bell",
                "+49 000 000 0000",
                "android.resource://com.android.app.inventory/drawable/image");
        dbHelper.insertItem(p5);

    }
}
