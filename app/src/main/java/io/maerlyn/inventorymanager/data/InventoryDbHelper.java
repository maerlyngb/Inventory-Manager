package io.maerlyn.inventorymanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;
import io.maerlyn.inventorymanager.data.InventoryContract.SupplierEntry;

/**
 * Helper class for working with the application database
 *
 * @author Maerlyn Broadbent
 */
public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    // name of the database file
    private static final String DATABASE_NAME = "inventory.db";

    // common SQL strings
    private static final String CREATE_TABLE = "CREATE TABLE";
    private static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS";
    private static final String TEXT = "TEXT";
    private static final String INTEGER = "INTEGER";
    private static final String NOT_NULL = "NOT NULL";
    private static final String TEXT_NOT_NULL = TEXT + " " + NOT_NULL;
    private static final String INTEGER_NOT_NULL = INTEGER + " " + NOT_NULL;
    private static final String INTEGER_PRIMARY_KEY_AUTOINCREMENT = "INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String FOREIGN_KEY = "FOREIGN KEY";
    private static final String REFERENCES = "REFERENCES";
    private static final String DEFAULT = "DEFAULT";
    private static final String SPACE = " ";
    private static final String BL = "(";
    private static final String BR = ")";
    private static final String STATEMENT_END = ";";
    private static final String CONT = "," + SPACE;


    // DB version. this must be incremented if the schema changes
    private static final int DATABASE_VERSION = 4;

    // instance of this class for application use
    private static InventoryDbHelper instance;

    /**
     * This class should not be directly instantiated.
     * Get an instance using the getInstance() method.
     *
     * @param context Activity Context
     */
    private InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Return an instance of {@link InventoryDbHelper}
     *
     * @param context Activity Context
     * @return instance of {@link InventoryDbHelper}
     */
    public static synchronized InventoryDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new InventoryDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Called when the database connection is being configured
     *
     * @param db application database
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);

        // enable foreign key support
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Called when a database is created for the first time.
     * if a database already exists with the same DATABASE_NAME, this method will not be called
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        createSupplierTable(db);
        createBookTable(db);
    }

    /**
     * Inserts the supplier table into the database.
     *
     * @param db The SQLiteDatabase the table is being inserted into.
     */

    private void createSupplierTable(SQLiteDatabase db) {
        /*
        CREATE TABLE supplier
        (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL,
            phone_num TEXT
        );
         */
        db.execSQL(CREATE_TABLE + SPACE + SupplierEntry.TABLE_NAME +
                BL +
                SupplierEntry._ID + SPACE + INTEGER_PRIMARY_KEY_AUTOINCREMENT + CONT +
                SupplierEntry.COLUMN_SUPPLIER_NAME + SPACE + TEXT_NOT_NULL + CONT +
                SupplierEntry.COLUMN_SUPPLIER_EMAIL + SPACE + TEXT_NOT_NULL + CONT +
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM + SPACE + TEXT +
                BR + STATEMENT_END);
    }

    /**
     * Inserts the book table into the database.
     *
     * @param db The SQLiteDatabase the table is being inserted into.
     */

    private void createBookTable(SQLiteDatabase db) {
        final int zero = 0;

        /*
        CREATE TABLE book
        (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            supplier_id INTEGER,
            name TEXT NOT NULL,
            price INTEGER NOT NULL DEFAULT 0,
            quantity INTEGER NOT NULL DEFAULT 0,
            image INTEGER,
            FOREIGN KEY (supplier_id) REFERENCES supplier (_id)
        );
         */
        db.execSQL(CREATE_TABLE + SPACE + BookEntry.TABLE_NAME +
                BL +
                BookEntry._ID + SPACE + INTEGER_PRIMARY_KEY_AUTOINCREMENT + CONT +
                BookEntry.COLUMN_BOOK_SUPPLIER_ID + SPACE + INTEGER + CONT +
                BookEntry.COLUMN_BOOK_TITLE + SPACE + TEXT_NOT_NULL + CONT +
                BookEntry.COLUMN_BOOK_PRICE + SPACE + INTEGER_NOT_NULL + SPACE + DEFAULT + SPACE + zero + CONT +
                BookEntry.COLUMN_BOOK_QUANTITY + SPACE + INTEGER_NOT_NULL + SPACE + DEFAULT + SPACE + zero + CONT +
                BookEntry.COLUMN_BOOK_IMAGE + SPACE + INTEGER + CONT +
                FOREIGN_KEY + SPACE + BL + BookEntry.COLUMN_BOOK_SUPPLIER_ID + BR + SPACE +
                REFERENCES + SPACE + SupplierEntry.TABLE_NAME + SPACE + BL + SupplierEntry._ID + BR +
                BR + STATEMENT_END);
    }

    /**
     * Called when schema changes need to be made.
     * <p>
     * This method will only be called if a database already
     * exists on disk with the same DATABASE_NAME,
     * but a different DATABASE_VERSION.
     *
     * @param db          application database
     * @param prevVersion version number of the previous db
     * @param newVersion  version number of the current db
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int prevVersion, int newVersion) {
        // migrate data and update
    }
}
