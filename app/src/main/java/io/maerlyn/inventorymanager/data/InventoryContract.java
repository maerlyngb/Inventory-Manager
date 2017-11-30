package io.maerlyn.inventorymanager.data;

import android.provider.BaseColumns;

/**
 * Stores meta data about the Inventory database
 *
 * @author Maerlyn Broadbent
 */
public class InventoryContract {

    private InventoryContract() {
    }

    /**
     * Book Table
     */
    public static final class BookEntry implements BaseColumns {
        public final static String TABLE_NAME = "book";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_SUPPLIER_ID = "supplier_id";
        public final static String COLUMN_BOOK_NAME = "name";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_BOOK_IMAGE = "image";
    }

    /**
     * Supplier Table
     */
    public static final class SupplierEntry implements BaseColumns {
        public final static String TABLE_NAME = "supplier";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public final static String COLUMN_SUPPLIER_PHONE_NUM = "supplier_phone_num";
    }
}
