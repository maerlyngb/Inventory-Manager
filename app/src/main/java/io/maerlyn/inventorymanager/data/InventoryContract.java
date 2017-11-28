package io.maerlyn.inventorymanager.data;

import android.provider.BaseColumns;

/**
 * Stores meta data about the Inventory database
 *
 * @author Maerlyn Broadbent
 */
class InventoryContract {

    private InventoryContract() {
    }

    /**
     * Book Table
     */
    static final class BookEntry implements BaseColumns {
        final static String TABLE_NAME = "book";
        final static String _ID = BaseColumns._ID;
        final static String COLUMN_BOOK_SUPPLIER_ID = "supplier_id";
        final static String COLUMN_BOOK_NAME = "name";
        final static String COLUMN_BOOK_PRICE = "price";
        final static String COLUMN_BOOK_QUANTITY = "quantity";
        final static String COLUMN_BOOK_IMAGE = "image";
    }

    /**
     * Supplier Table
     */
    static final class SupplierEntry implements BaseColumns {
        final static String TABLE_NAME = "supplier";
        final static String _ID = BaseColumns._ID;
        final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        final static String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        final static String COLUMN_SUPPLIER_PHONE_NUM = "supplier_phone_num";
    }
}
