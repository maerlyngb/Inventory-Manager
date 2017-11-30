package io.maerlyn.inventorymanager;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.maerlyn.inventorymanager.data.InventoryContract;
import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;
import io.maerlyn.inventorymanager.data.InventoryContract.SupplierEntry;
import io.maerlyn.inventorymanager.data.InventoryDbHelper;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        InventoryDbHelper dbHelper = InventoryDbHelper.getInstance(this);

        // create supplier object
        Supplier newSupplier = new Supplier(
                "Bob's Books",
                "books@supplier.com",
                "0432 345 654");

        // save supplier to the db
        long newSupplierId = dbHelper.save(newSupplier);

        // set the supplier id to the val returned from dbHelper
        newSupplier.setId(newSupplierId);

        // load a supplier by id from the database
        // this should be the same one we just created
        Supplier loadedSupplier = dbHelper.getSupplierById(newSupplierId);

        // update the supplier email address
        loadedSupplier.setEmail("bob@books.com");

        // save updated supplier details
        dbHelper.save(loadedSupplier);

        // create a new book object
        Book newBook = new Book(
                loadedSupplier, // supplier
                "Lord of the Rings", // name
                523, // price in cents
                23, // quantity
                11 // image resource id
        );

        // get the id of the newly inserted book and save in the book object
        long newBookId = dbHelper.save(newBook);
        newBook.setId(newBookId);

        // update the name of the book
        newBook.setName("Treasure Island");

        // save changes to db
        dbHelper.save(newBook);

        // load all books from the database
        List<Book> books = getBooks(dbHelper);

        TextView booksTextView = findViewById(R.id.books);

        // display list of books
        for (Book book : books) {
            booksTextView.append("\n");
            booksTextView.append(book.getName() + ", ");
            booksTextView.append(book.getSupplier().getName() + ", ");
            booksTextView.append(book.getPriceString() + ", ");
            booksTextView.append(String.valueOf(book.getQuantity()));
        }

        List<Supplier> suppliers = getSuppliers(dbHelper);

        TextView supplierTextView = findViewById(R.id.suppliers);

        // display list of suppliers
        for (Supplier supplier : suppliers) {
            supplierTextView.append("\n");
            supplierTextView.append(supplier.getName() + ", ");
            supplierTextView.append(supplier.getEmail() + ", ");
            supplierTextView.append(supplier.getPhoneNum());
        }
    }

    private List<Book> getBooks(InventoryDbHelper dbHelper) {
        List<Book> books = new ArrayList<>();

        Cursor cursor = dbHelper.getBooks();

        // show cursor contents in logcat
        DatabaseUtils.dumpCursor(cursor);

        try {
            while (cursor.moveToNext()) {

                // Create a supplier object for the book
                // results are small so lazy loading won't make much difference
                Supplier supplier = dbHelper.getSupplierById(cursor.getInt(cursor.getColumnIndex(
                        InventoryContract.BookEntry.COLUMN_BOOK_SUPPLIER_ID)));

                Book book = new Book(
                        supplier,
                        cursor.getString(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_NAME)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_IMAGE))
                );

                // add book to the list to return
                books.add(book);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while trying to get books from database", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return books;
    }

    public List<Supplier> getSuppliers(InventoryDbHelper dbHelper) {
        List<Supplier> suppliers = new ArrayList<>();

        // execute the query on the database
        Cursor cursor = dbHelper.getSuppliers();

        // show cursor contents in logcat
        DatabaseUtils.dumpCursor(cursor);

        try {
            while (cursor.moveToNext()) {
                Supplier supplier = new Supplier(
                        cursor.getString(cursor.getColumnIndex(
                                SupplierEntry.COLUMN_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(
                                SupplierEntry.COLUMN_SUPPLIER_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(
                                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM))
                );

                // add supplier to list to return
                suppliers.add(supplier);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error while trying to get suppliers from database", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return suppliers;
    }
}
