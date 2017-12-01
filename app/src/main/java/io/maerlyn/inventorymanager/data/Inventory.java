package io.maerlyn.inventorymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;
import io.maerlyn.inventorymanager.data.InventoryContract.SupplierEntry;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

/**
 * Inventory data management
 *
 * @author Maerlyn Broadbent
 */
public class Inventory {

    private static final String LOG_TAG = Inventory.class.getSimpleName();

    /**
     * Insert a book into the inventory database
     *
     * @param book    to insert
     * @param context activity context
     * @return URI of the inserted book
     */
    public static Uri addNewBook(Book book, Context context) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, book.getName());
        values.put(BookEntry.COLUMN_BOOK_PRICE, book.getPrice());
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, book.getQuantity());
        values.put(BookEntry.COLUMN_BOOK_IMAGE, book.getImageResourceId());
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_ID, book.getSupplier().getId());

        return context.getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    /**
     * Insert a supplier into the inventory database
     *
     * @param supplier to insert
     * @param context  activity context
     * @return URI of the inserted supplier
     */
    public static Uri insertSupplier(Supplier supplier, Context context) {
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplier.getName());
        values.put(SupplierEntry.COLUMN_SUPPLIER_EMAIL, supplier.getEmail());
        values.put(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM, supplier.getPhoneNum());

        return context.getContentResolver().insert(SupplierEntry.CONTENT_URI, values);
    }

    /**
     * Return a list of all suppliers
     *
     * @param context activity context
     * @return list of {@link Supplier} objects
     */
    public static List<Supplier> getAllSuppliers(Context context) {
        List<Supplier> suppliers = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                SupplierEntry.CONTENT_URI,
                SupplierEntry.ALL_COLUMNS,
                null,       // WHERE clause
                null,       // data for the WHERE clause
                null);      // sort order

        try {
            while (cursor != null && cursor.moveToNext()) {
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
            Log.e(LOG_TAG, "Error loading suppliers", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return suppliers;
    }

    /**
     * Return a list of all books
     *
     * @param context activity context
     * @return list of {@link Book} objects
     */
    public static List<Book> getAllBooks(Context context) {
        List<Book> books = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                BookEntry.CONTENT_URI,
                BookEntry.ALL_COLUMNS,
                null,       // WHERE clause
                null,       // data for the WHERE clause
                null);      // sort order

        try {
            while (cursor != null && cursor.moveToNext()) {
                Book book = new Book(
                        null,
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
            Log.e(LOG_TAG, "Error loading books", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }
        return books;
    }

}
