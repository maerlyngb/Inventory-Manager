package io.maerlyn.inventorymanager.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * API contract for the book inventory manager
 *
 * @author Maerlyn Broadbent
 */
class InventoryContract {

    static final String CONTENT_AUTHORITY = "io.maerlyn.inventorymanager";
    // base content URI
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_BOOKS = "books";
    static final String PATH_SUPPLIERS = "suppliers";

    private InventoryContract() {
    }

    /**
     * Book Table
     */
    static final class BookEntry implements BaseColumns {
        // URI to access book data
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        //The MIME type of the {@link #CONTENT_URI} for a list of books.
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //The MIME type of the {@link #CONTENT_URI} for a single book.
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        final static String TABLE_NAME = "book";
        final static String _ID = BaseColumns._ID;
        final static String COLUMN_BOOK_SUPPLIER_ID = "supplier_id";
        final static String COLUMN_BOOK_NAME = "name";
        final static String COLUMN_BOOK_PRICE = "price";
        final static String COLUMN_BOOK_QUANTITY = "quantity";
        final static String COLUMN_BOOK_IMAGE = "image";
        final static String[] COLUMNS_ALL = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_SUPPLIER_ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_IMAGE
        };

        // Return a URI for a given id
        public static Uri getBookUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * Supplier Table
     */
    static final class SupplierEntry implements BaseColumns {
        // URI to access supplier data
        static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);

        //The MIME type of the {@link #CONTENT_URI} for a list of suppliers.
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        //The MIME type of the {@link #CONTENT_URI} for a single supplier.
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;


        final static String TABLE_NAME = "supplier";
        final static String _ID = BaseColumns._ID;
        final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        final static String COLUMN_SUPPLIER_PHONE_NUM = "supplier_phone_num";
        final static String[] COLUMNS_ALL = {
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_EMAIL,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM
        };

        // Return a URI for a given id
        public static Uri getSupplierUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
