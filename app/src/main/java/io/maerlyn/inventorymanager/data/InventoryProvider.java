package io.maerlyn.inventorymanager.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;
import io.maerlyn.inventorymanager.data.InventoryContract.SupplierEntry;

/**
 * {@link ContentProvider} for book inventory manager.
 *
 * @author Maerlyn Broadbent
 */
public class InventoryProvider extends ContentProvider {
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    // ints representing each valid URI
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;
    private static final int BOOK_DETAIL_ID = 102;
    private static final int SUPPLIERS = 200;
    private static final int SUPPLIER_ID = 201;

    // used to match a given URI with the ints above
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer.
    static {
        final String authority = InventoryContract.CONTENT_AUTHORITY;

        // list of books
        uriMatcher.addURI(authority, InventoryContract.PATH_BOOKS, BOOKS);

        // single book by ID
        uriMatcher.addURI(authority, InventoryContract.PATH_BOOKS + "/#", BOOK_ID);

        // single book with supplier detail by ID
        uriMatcher.addURI(authority, InventoryContract.PATH_BOOKS_DETAIL + "/#", BOOK_DETAIL_ID);

        // list of suppliers
        uriMatcher.addURI(authority, InventoryContract.PATH_SUPPLIERS, SUPPLIERS);

        // single supplier by ID
        uriMatcher.addURI(authority, InventoryContract.PATH_SUPPLIERS + "/#", SUPPLIER_ID);
    }

    private InventoryDbHelper dbHelper;

    /**
     * Return the MIME type for a given URI
     *
     * @param uri to return MIME type for
     * @return MIME type
     */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            case BOOK_DETAIL_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            case SUPPLIERS:
                return SupplierEntry.CONTENT_LIST_TYPE;
            case SUPPLIER_ID:
                return SupplierEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = InventoryDbHelper.getInstance(getContext());
        return true;
    }

    /**
     * Insert data into a the inventory database
     *
     * @param uri    API URI to direct the data
     * @param values data to insert
     * @return URI of the inserted data
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                validateBook(values);
                return insertRow(uri, values, BookEntry.TABLE_NAME);
            case SUPPLIERS:
                validateSupplier(values);
                return insertRow(uri, values, SupplierEntry.TABLE_NAME);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert data into a the inventory database
     *
     * @param uri    API URI to direct the data
     * @param values data to insert
     * @param table  to insert the data into
     * @return URI of the inserted data
     */
    private Uri insertRow(Uri uri, ContentValues values, String table) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long newRowId = db.insert(table, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the id of the newly inserted row
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Read data from the inventory database
     *
     * @param uri           API URI to direct the data
     * @param projection    columns to return
     * @param selection     WHERE clause
     * @param selectionArgs data for the WHERE clause
     * @param sortOrder     result set sorting order
     * @return {@link Cursor} containing the data
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;

        // find out which URI we have been given
        switch (uriMatcher.match(uri)) {
            case BOOKS:
                // select multiple books
                cursor = db.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_ID:
                // select a single book by id
                cursor = db.query(
                        BookEntry.TABLE_NAME,
                        projection,
                        BookEntry._ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            case BOOK_DETAIL_ID:
                final String sql = "SELECT book._id, book.supplier_id, book.title, book.price, " + "" +
                        "book.quantity, book.image, supplier.name, supplier.email, supplier.phone_num " +
                        "FROM book " +
                        "INNER JOIN SUPPLIER " +
                        "ON book.supplier_id = supplier._id " +
                        "WHERE book._id = ?";

                cursor = db.rawQuery(
                        sql,
                        new String[]{String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            case SUPPLIERS:
                // select multiple suppliers
                cursor = db.query(
                        SupplierEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SUPPLIER_ID:
                // select a single supplier by id
                cursor = db.query(
                        SupplierEntry.TABLE_NAME,
                        projection,
                        SupplierEntry._ID + "=?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Selection is not supported for " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Cursor containing the result set
        return cursor;
    }

    /**
     * Update existing data in the inventory database
     *
     * @param uri           API URI to direct the data
     * @param values        updated data
     * @param selection     WHERE clause used to update
     * @param selectionArgs data for the WHERE clause
     * @return number of rows that were updated
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                validateBook(values);
                return updateRow(uri, values, selection, selectionArgs, BookEntry.TABLE_NAME);

            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                validateBook(values);
                return updateRow(uri, values, selection, selectionArgs, BookEntry.TABLE_NAME);

            case SUPPLIERS:
                validateSupplier(values);
                return updateRow(uri, values, selection, selectionArgs, SupplierEntry.TABLE_NAME);

            case SUPPLIER_ID:
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                validateSupplier(values);
                return updateRow(uri, values, selection, selectionArgs, SupplierEntry.TABLE_NAME);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Delete data from the inventory database
     *
     * @param uri           API URI to direct the data
     * @param selection     WHERE clause used to delete
     * @param selectionArgs data for the WHERE clause
     * @return number of rows that were deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // number of rows that were deleted
        int rowsDeleted;

        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SUPPLIERS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case SUPPLIER_ID:
                // Delete a single row given by the ID in the URI
                selection = SupplierEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(SupplierEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    private int updateRow(Uri uri, ContentValues values, String selection, String[] selectionArgs, String tableName) {
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(tableName, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    /**
     * Throw an exception of book values contain an error
     *
     * @param values to validate
     */
    private void validateBook(ContentValues values) {
        // all books require a name
        if (values.containsKey(BookEntry.COLUMN_BOOK_TITLE)) {
            String name = values.getAsString(BookEntry.COLUMN_BOOK_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Book requires a valid name");
            }
        }

        // books are allowed to be added before a supplier can be found.
        // however, the id of the supplier cannot be negative.
        // TODO: check for a valid supplier in the database
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_ID)) {
            Integer supplierId = values.getAsInteger(BookEntry.COLUMN_BOOK_SUPPLIER_ID);
            if (supplierId != null && supplierId < 0) {
                throw new IllegalArgumentException("Book has been assigned an invalid supplier");
            }
        }

        // negative prices are not valid.
        // a missing price is acceptable because it defaults to 0 at the database level
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book requires a valid price");
            }
        }

        // negative quantities are not valid.
        // a missing quantity is acceptable because it defaults to 0 at the database level
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book requires a valid price");
            }
        }

        //TODO: image support
    }

    /**
     * Throw an exception of supplier values contain an error
     *
     * @param values to validate
     */
    private void validateSupplier(ContentValues values) {
        // all suppliers require a name
        if (values.containsKey(SupplierEntry.COLUMN_SUPPLIER_NAME)) {
            String name = values.getAsString(SupplierEntry.COLUMN_SUPPLIER_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Supplier requires a valid name");
            }
        }

        if (values.containsKey(SupplierEntry.COLUMN_SUPPLIER_EMAIL)) {
            String email = values.getAsString(SupplierEntry.COLUMN_SUPPLIER_EMAIL);
            if (email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw new IllegalArgumentException("Supplier requires a valid email address");
            }
        }
    }
}
