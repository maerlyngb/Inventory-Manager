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
public class InventoryContract {

    static final String CONTENT_AUTHORITY = "io.maerlyn.inventorymanager";
    // base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";
    public static final String PATH_SUPPLIERS = "suppliers";
    public static final String PATH_BOOKS_DETAIL = PATH_BOOKS + "/detail";

    // ensure this class is never instantiated
    private InventoryContract() {
    }

    /**
     * Book Table
     */
    public static final class BookEntry implements BaseColumns {
        // URI to access book data
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        // URI to access expanded book data
        public static final Uri CONTENT_DETAIL_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS_DETAIL);

        //The MIME type of the {@link #CONTENT_URI} for a list of books.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //The MIME type of the {@link #CONTENT_URI} for a single book.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME = "book";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_SUPPLIER_ID = "supplier_id";
        public final static String COLUMN_BOOK_TITLE = "title";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_IMAGE = "image";
        public final static String[] COLUMNS_ALL = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_SUPPLIER_ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_IMAGE
        };

        // Return a URI for a given id
        public static Uri getBookUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // Return a URI for a given id
        public static Uri getBookDetailUri(long id) {
            return ContentUris.withAppendedId(CONTENT_DETAIL_URI, id);
        }
    }

    /**
     * Supplier Table
     */
    public static final class SupplierEntry implements BaseColumns {
        // URI to access supplier data
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUPPLIERS);

        //The MIME type of the {@link #CONTENT_URI} for a list of suppliers.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;

        //The MIME type of the {@link #CONTENT_URI} for a single supplier.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUPPLIERS;


        public final static String TABLE_NAME = "supplier";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SUPPLIER_NAME = "name";
        public final static String COLUMN_SUPPLIER_EMAIL = "email";
        public final static String COLUMN_SUPPLIER_PHONE_NUM = "phone_num";
        public final static String[] COLUMNS_ALL = {
                SupplierEntry._ID,
                SupplierEntry.COLUMN_SUPPLIER_NAME,
                SupplierEntry.COLUMN_SUPPLIER_EMAIL,
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM
        };

        // Return a URI for a given id
        public static Uri getSupplierUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
