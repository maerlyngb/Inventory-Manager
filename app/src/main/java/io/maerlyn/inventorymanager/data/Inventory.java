package io.maerlyn.inventorymanager.data;

import android.content.ContentUris;
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
     * Return a list of all suppliers
     *
     * @param context activity context
     * @return list of {@link Supplier} objects
     */
    public static List<Supplier> getAllSuppliers(Context context) {
        List<Supplier> suppliers = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                SupplierEntry.CONTENT_URI,
                SupplierEntry.COLUMNS_ALL,
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
                BookEntry.COLUMNS_ALL,
                null,       // WHERE clause
                null,       // data for the WHERE clause
                null);      // sort order

        try {
            while (cursor != null && cursor.moveToNext()) {

                long supplierId = cursor.getLong(cursor.getColumnIndex(
                        BookEntry.COLUMN_BOOK_SUPPLIER_ID));

                Book book = new Book(
                        getSupplierById(supplierId, context),
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

    /**
     * Return a single {@link Supplier} by id
     *
     * @param supplierId id of the supplier to return
     * @param context    activity context
     * @return {@link Supplier}
     */
    public static Supplier getSupplierById(long supplierId, Context context) {
        Uri uri = SupplierEntry.getSupplierUri(supplierId);

        Cursor cursor = context.getContentResolver().query(
                uri,
                SupplierEntry.COLUMNS_ALL,
                SupplierEntry._ID + "=?",
                new String[]{String.valueOf(ContentUris.parseId(uri))},
                null);

        Supplier supplier = null;

        try {
            if (cursor != null && cursor.getCount() > 0) {

                // we should never receive multiple rows
                cursor.moveToFirst();

                supplier = new Supplier(
                        cursor.getLong(cursor.getColumnIndex(
                                SupplierEntry._ID)),
                        cursor.getString(cursor.getColumnIndex(
                                SupplierEntry.COLUMN_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(
                                SupplierEntry.COLUMN_SUPPLIER_NAME)),
                        cursor.getString(cursor.getColumnIndex(
                                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM))
                );
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while selecting a supplier by ID", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return supplier;
    }

    /**
     * Return a single {@link Book} by id
     *
     * @param bookId  id of the supplier to return
     * @param context activity context
     * @return {@link Book}
     */
    public static Book getBookById(long bookId, Context context) {
        Uri uri = BookEntry.getBookUri(bookId);

        Cursor cursor = context.getContentResolver().query(
                uri,
                BookEntry.COLUMNS_ALL,
                BookEntry._ID + "=?",
                new String[]{String.valueOf(ContentUris.parseId(uri))},
                null);

        Book book = null;

        try {
            if (cursor != null && cursor.getCount() > 0) {

                // we should never receive multiple rows
                cursor.moveToFirst();

                long supplierId = cursor.getLong(cursor.getColumnIndex(
                        BookEntry.COLUMN_BOOK_SUPPLIER_ID));

                book = new Book(
                        cursor.getLong(cursor.getColumnIndex(
                                BookEntry._ID)),
                        getSupplierById(supplierId, context),
                        cursor.getString(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_NAME)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_QUANTITY)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_IMAGE))
                );
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while selecting a supplier by ID", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return book;
    }

    /**
     * Insert a book into the inventory database
     *
     * @param book    to insert
     * @param context activity context
     * @return URI of the inserted book
     */
    public static long insert(Book book, Context context) {
        ContentValues values = getBookContentValues(book);
        Uri uri = context.getContentResolver().insert(BookEntry.CONTENT_URI, values);
        return ContentUris.parseId(uri);
    }

    /**
     * Insert a supplier into the inventory database
     *
     * @param supplier to insert
     * @param context  activity context
     * @return URI of the inserted supplier
     */
    public static long insert(Supplier supplier, Context context) {
        ContentValues values = getSupplierContentValues(supplier);
        Uri uri = context.getContentResolver().insert(SupplierEntry.CONTENT_URI, values);
        return ContentUris.parseId(uri);
    }

    /**
     * Delete a book from the inventory database
     *
     * @param book to delete
     * @param context activity context
     * @return true if deletion was successful
     */
    public static boolean delete(Book book, Context context) {
        Uri uri = BookEntry.getBookUri(book.getId());
        int rowCount = -1;

        try {
            rowCount = context.getContentResolver().delete(
                    uri,
                    BookEntry._ID + "=?",
                    new String[]{String.valueOf(ContentUris.parseId(uri))});
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while deleting a book by ID", e);
        }
        return rowCount == 1;
    }

    /**
     * Delete a list of books from the inventory database
     *
     * @param books to delete
     * @param context activity context
     * @return true if deletion was successful
     */
    public static boolean delete(List<Book> books, Context context){
        if (books.size() < 1){
            return false;
        }

        boolean deleteSuccess = false;

        for (Book book : books){
            deleteSuccess = delete(book, context);
        }

        return deleteSuccess;
    }

    /**
     * Delete a supplier from the inventory database
     *
     * @param supplier to delete
     * @param context    activity context
     * @return true if deletion was successful
     */
    public static boolean delete(Supplier supplier, Context context) {
        Uri uri = SupplierEntry.getSupplierUri(supplier.getId());
        int rowCount = -1;

        try {
            rowCount = context.getContentResolver().delete(
                    uri,
                    SupplierEntry._ID + "=?",
                    new String[]{String.valueOf(ContentUris.parseId(uri))});
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while deleting a supplier by ID", e);
        }
        return rowCount == 1;
    }

    /**
     * Update a single book in the inventory database
     *
     * @param book  to update
     * @param context activity context
     */
    public static boolean update(Book book, Context context) {
        Uri uri = BookEntry.getBookUri(book.getId());
        int rowCount = -1;

        try {
            rowCount = context.getContentResolver().update(
                    uri,
                    getBookContentValues(book),
                    BookEntry._ID + "=?",
                    new String[]{String.valueOf(ContentUris.parseId(uri))});
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while deleting a book by ID", e);
        }
        return rowCount == 1;
    }

    /**
     * Update a single supplier in the inventory database
     *
     * @param supplier to update
     * @param context  activity context
     */
    public static boolean update(Supplier supplier, Context context) {
        Uri uri = SupplierEntry.getSupplierUri(supplier.getId());
        int rowCount = -1;

        try {
            rowCount = context.getContentResolver().update(
                    uri,
                    getSupplierContentValues(supplier),
                    SupplierEntry._ID + "=?",
                    new String[]{String.valueOf(ContentUris.parseId(uri))});
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while deleting a supplier by ID", e);
        }
        return rowCount == 1;
    }

    /**
     * Return {@link ContentValues} for a given {@link Book}
     *
     * @param book used to generate {@link ContentValues}
     * @return {@link ContentValues}
     */
    private static ContentValues getBookContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, book.getName());
        values.put(BookEntry.COLUMN_BOOK_PRICE, book.getPrice());
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, book.getQuantity());
        values.put(BookEntry.COLUMN_BOOK_IMAGE, book.getImageResourceId());
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_ID, book.getSupplier().getId());
        return values;
    }

    /**
     * Return {@link ContentValues} for a given {@link Supplier}
     *
     * @param supplier used to generate {@link ContentValues}
     * @return {@link ContentValues}
     */
    private static ContentValues getSupplierContentValues(Supplier supplier) {
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplier.getName());
        values.put(SupplierEntry.COLUMN_SUPPLIER_EMAIL, supplier.getEmail());
        values.put(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM, supplier.getPhoneNum());
        return values;
    }

}
