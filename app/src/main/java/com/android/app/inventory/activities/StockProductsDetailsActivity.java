package com.android.app.inventory.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.app.inventory.R;
import com.android.app.inventory.dbhelper.InventoryStockDbHelper;
import com.android.app.inventory.dbcontract.InventoryStockContract;
import com.android.app.inventory.model.InventoryStockItem;


public class StockProductsDetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = StockProductsDetailsActivity.class.getCanonicalName();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private InventoryStockDbHelper dbHelper;
    EditText nameEdit;
    EditText priceEdit;
    EditText quantityEdit;
    EditText supplierNameEdit;
    EditText supplierPhoneEdit;
    long currentItemId;
    ImageButton decreaseQuantity;
    ImageButton increaseQuantity;
    Button imageBtn,delete,order,update;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameEdit = (EditText) findViewById(R.id.product_name_edit);
        priceEdit = (EditText) findViewById(R.id.price_edit);
        quantityEdit = (EditText) findViewById(R.id.quantity_edit);
        supplierNameEdit = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = (EditText) findViewById(R.id.supplier_phone_edit);
        decreaseQuantity = (ImageButton) findViewById(R.id.decrease_quantity);
        increaseQuantity = (ImageButton) findViewById(R.id.increase_quantity);
        imageBtn = (Button) findViewById(R.id.select_image);
        delete = (Button) findViewById(R.id.delete_btn);
        order = (Button) findViewById(R.id.order_btn);
        update = (Button) findViewById(R.id.save_btn);
        imageView = (ImageView) findViewById(R.id.image_view);

        dbHelper = new InventoryStockDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0) {
            setTitle(getString(R.string.add_product));
        } else {
            setTitle(getString(R.string.update_product));
            updateProducts(currentItemId);
        }

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelctorForProducts();
                infoItemHasChanged = true;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(currentItemId);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProductstoDB();
                finish();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneEdit.getText().toString().trim()));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!infoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_updating, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void subtractOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEdit.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = quantityEdit.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEdit.setText(String.valueOf(previousValue + 1));
    }

    private boolean addProductstoDB() {
        boolean isAllOk = true;
        if (!checkIfValueSet(nameEdit, "name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(priceEdit, "price")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(quantityEdit, "quantity")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierNameEdit, "supplier name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierPhoneEdit, "supplier phone")) {
            isAllOk = false;
        }

        if (actualUri == null && currentItemId == 0) {
            isAllOk = false;
            imageBtn.setError("Missing image");
        }
        if (!isAllOk) {
            return false;
        }

        if (currentItemId == 0) {
            InventoryStockItem item = new InventoryStockItem(
                    nameEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(quantityEdit.getText().toString().trim()),
                    supplierNameEdit.getText().toString().trim(),
                    supplierPhoneEdit.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(quantityEdit.getText().toString().trim());
            dbHelper.updateStockItem(currentItemId, quantity);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void updateProducts(long itemId) {
        Cursor cursor = dbHelper.readStockItem(itemId);
        cursor.moveToFirst();
        nameEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_NAME)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_PRICE)));
        quantityEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_QUANTITY)));
        supplierNameEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhoneEdit.setText(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_SUPPLIER_PHONE)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryStockContract.StockEntry.COLUMN_IMAGE))));
        nameEdit.setEnabled(false);
        priceEdit.setEnabled(false);
        supplierNameEdit.setEnabled(false);
        supplierPhoneEdit.setEnabled(false);
        imageBtn.setEnabled(false);
    }



    private int deleteAllRowsFromTable() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(InventoryStockContract.StockEntry.TABLE_NAME, null, null);
    }

    private int deleteOneItemFromTable(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = InventoryStockContract.StockEntry._ID + "=?";
        String[] selectionArgs = { String.valueOf(itemId) };
        int rowsDeleted = database.delete(
                InventoryStockContract.StockEntry.TABLE_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (itemId == 0) {
                    deleteAllRowsFromTable();
                } else {
                    deleteOneItemFromTable(itemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void imageSelctorForProducts() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }
}
