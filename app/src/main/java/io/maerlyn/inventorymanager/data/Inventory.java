package io.maerlyn.inventorymanager.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.maerlyn.inventorymanager.R;
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

        return cursorToSupplierList(cursor, context);
    }

    /**
     * Return a list of all books
     *
     * @param context activity context
     * @return list of {@link Book} objects
     */
    public static List<Book> getAllBooks(Context context) {
        Cursor cursor = context.getContentResolver().query(
                BookEntry.CONTENT_URI,
                BookEntry.COLUMNS_ALL,
                null,       // WHERE clause
                null,       // data for the WHERE clause
                null);      // sort order

        return cursorToBookList(cursor, context);
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

        return cursorToSupplier(cursor, context);
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

        return cursorToBook(cursor, true, true, context);
    }

    /**
     * Insert a book into the inventory database
     *
     * @param book    to insert
     * @param context activity context
     * @return URI of the inserted book
     */
    public static long insert(Book book, Context context) {
        ContentValues values = bookToContentValues(book);
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
        ContentValues values = supplierToContentValues(supplier);
        Uri uri = context.getContentResolver().insert(SupplierEntry.CONTENT_URI, values);
        return ContentUris.parseId(uri);
    }

    /**
     * Delete a book from the inventory database
     *
     * @param book    to delete
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
     * @param books   to delete
     * @param context activity context
     * @return true if deletion was successful
     */
    public static boolean delete(List<Book> books, Context context) {
        if (books.size() < 1) {
            return false;
        }

        boolean deleteSuccess = false;

        for (Book book : books) {
            deleteSuccess = delete(book, context);
        }

        return deleteSuccess;
    }

    /**
     * Delete a supplier from the inventory database
     *
     * @param supplier to delete
     * @param context  activity context
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
     * Delete all data in the database
     */
    public static int[] deleteAll(Context context) {
        int[] rowsDeleted = new int[2];

        rowsDeleted[1] = deleteAllBooks(context);
        rowsDeleted[0] = deleteAllSuppliers(context);

        return rowsDeleted;
    }

    private static int deleteAllSuppliers(Context context) {
        return context.getContentResolver().delete(
                SupplierEntry.CONTENT_URI,
                null,
                null
        );
    }

    private static int deleteAllBooks(Context context) {
        return context.getContentResolver().delete(
                BookEntry.CONTENT_URI,
                null,
                null
        );
    }

    /**
     * Update a single book in the inventory database
     *
     * @param book    to update
     * @param context activity context
     */
    public static boolean update(Book book, Context context) {
        Uri uri = BookEntry.getBookUri(book.getId());
        int rowCount = -1;

        try {
            rowCount = context.getContentResolver().update(
                    uri,
                    bookToContentValues(book),
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
                    supplierToContentValues(supplier),
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
    private static ContentValues bookToContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_TITLE, book.getName());
        values.put(BookEntry.COLUMN_BOOK_PRICE, book.getPrice());
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, book.getQuantity());
        values.put(BookEntry.COLUMN_BOOK_IMAGE, book.getImage());
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_ID, book.getSupplier().getId());
        return values;
    }

    /**
     * Return {@link ContentValues} for a given {@link Supplier}
     *
     * @param supplier used to generate {@link ContentValues}
     * @return {@link ContentValues}
     */
    private static ContentValues supplierToContentValues(Supplier supplier) {
        ContentValues values = new ContentValues();
        values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplier.getName());
        values.put(SupplierEntry.COLUMN_SUPPLIER_EMAIL, supplier.getEmail());
        values.put(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM, supplier.getPhoneNum());
        return values;
    }

    /**
     * Return a single {@link Book} for a given {@link Cursor}
     *
     * @param cursor  containing the supplier data
     * @param context app context
     * @return {@link Book}
     */
    public static Book cursorToBook(Cursor cursor, boolean shouldMove,
                                    boolean closeCursor, Context context) {
        Book book = null;
        try {
            if (cursor != null && cursor.getCount() > 0) {

                // this method should only have a single row
                if (shouldMove) {
                    cursor.moveToFirst();
                }

                long supplierId = cursor.getLong(cursor.getColumnIndex(
                        BookEntry.COLUMN_BOOK_SUPPLIER_ID));

                // get column indexes
                int titleIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
                int priceIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
                int quantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
                int imageIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_IMAGE);

                // get data if we have the columns, otherwise use a default val
                String title = titleIndex > -1
                        ? cursor.getString(titleIndex)
                        : context.getString(R.string.unknown_title);

                int price = priceIndex > -1
                        ? cursor.getInt(priceIndex)
                        : 0;

                int quantity = quantityIndex > -1
                        ? cursor.getInt(quantityIndex)
                        : 0;

                byte[] image = imageIndex >= 0
                        ? cursor.getBlob(imageIndex)
                        : new byte[0];

                // create book object from cursor data
                book = new Book(
                        cursor.getLong(cursor.getColumnIndex(BookEntry._ID)),
                        getSupplierById(supplierId, context),
                        title,
                        price,
                        quantity,
                        image
                );
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception creating a book from a cursor", e);
        } finally {
            if (closeCursor) {
                // we need to close the cursor to prevent memory leaks
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }

        return book;
    }

    /**
     * Return a single {@link Supplier} for a given {@link Cursor}
     *
     * @param cursor  containing the supplier data
     * @param context app context
     * @return {@link Supplier}
     */
    public static Supplier cursorToSupplier(Cursor cursor, Context context) {
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
            Log.e(LOG_TAG, "Exception creating a supplier from a cursor", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return supplier;
    }

    /**
     * Return a {@link List} of {@link Book} for a given {@link Cursor}
     *
     * @param cursor  containing the supplier data
     * @param context app context
     * @return {@link List} of {@link Book}
     */
    public static List<Book> cursorToBookList(Cursor cursor, Context context) {
        List<Book> books = new ArrayList<>();

        try {
            while (cursor != null && cursor.moveToNext()) {

                long supplierId = cursor.getLong(cursor.getColumnIndex(
                        BookEntry.COLUMN_BOOK_SUPPLIER_ID));

                Book book = new Book(
                        getSupplierById(supplierId, context),
                        cursor.getString(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_TITLE)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_PRICE)),
                        cursor.getInt(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_QUANTITY)),
                        cursor.getBlob(cursor.getColumnIndex(
                                BookEntry.COLUMN_BOOK_IMAGE))
                );

                // add book to the list to return
                books.add(book);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error creating a list of books from a cursor", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }

        }

        return books;
    }

    /**
     * Return a {@link List} of {@link Supplier} for a given {@link Cursor}
     *
     * @param cursor  containing the supplier data
     * @param context app context
     * @return {@link List} of {@link Supplier}
     */
    public static List<Supplier> cursorToSupplierList(Cursor cursor, Context context) {
        List<Supplier> suppliers = new ArrayList<>();

        try {
            while (cursor != null && cursor.moveToNext()) {
                Supplier supplier = new Supplier(
                        cursor.getInt(cursor.getColumnIndex(
                                SupplierEntry._ID)),
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
            Log.e(LOG_TAG, "Error creating a list of suppliers from a cursor", e);
        } finally {
            // we need to close the cursor to prevent memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return suppliers;
    }

    public static Loader<Cursor> getSummaryLoader(Context context) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_SUPPLIER_ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(
                context,                // Parent activity context
                BookEntry.CONTENT_URI,  // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    public static Loader<Cursor> getBookDetailCursor(Context context, long id) {
        return new CursorLoader(
                context,                        // Parent activity context
                BookEntry.getBookDetailUri(id),   // Provider content URI to query
                null,                           // Columns to include in the resulting Cursor
                null,                           // No selection clause
                null,                           // No selection arguments
                null);                          // Default sort order
    }

    public static Book bookDetailCursorToBook(Cursor cursor) {
        // get column indexes in cursor
        int bookTitleIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_TITLE);
        int bookPriceIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int bookQuantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int bookImageIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_IMAGE);
        int bookSupplierId = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_ID);

        int supplierNameIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_NAME);
        int supplierEmailIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_EMAIL);
        int supplierPhoneIndex = cursor.getColumnIndex(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM);

        // the supplier of this book
        Supplier supplier = new Supplier(
                cursor.getLong(bookSupplierId),
                cursor.getString(supplierNameIndex),
                cursor.getString(supplierEmailIndex),
                cursor.getString(supplierPhoneIndex));

        return new Book(
                supplier,
                cursor.getString(bookTitleIndex),
                cursor.getInt(bookPriceIndex),
                cursor.getInt(bookQuantityIndex),
                cursor.getBlob(bookImageIndex));
    }
}
