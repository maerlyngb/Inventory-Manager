package io.maerlyn.inventorymanager.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;
import io.maerlyn.inventorymanager.data.InventoryContract.SupplierEntry;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

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
    private static final String SELECT = "SELECT";
    private static final String SELECT_ALL = SELECT + SPACE + "*";
    private static final String FROM = "FROM";


    // DB version. this must be incremented if the schema changes
    private static final int DATABASE_VERSION = 1;

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
        /*
        CREATE TABLE supplier
        (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL,
            phone_num TEXT
        );
         */
        String CREATE_SUPPLIER_TABLE = CREATE_TABLE + SPACE + SupplierEntry.TABLE_NAME +
                BL +
                SupplierEntry._ID + SPACE + INTEGER_PRIMARY_KEY_AUTOINCREMENT + CONT +
                SupplierEntry.COLUMN_SUPPLIER_NAME + SPACE + TEXT_NOT_NULL + CONT +
                SupplierEntry.COLUMN_SUPPLIER_EMAIL + SPACE + TEXT_NOT_NULL + CONT +
                SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM + SPACE + TEXT +
                BR + STATEMENT_END;

        int defaultQty = 0;

        /*
        CREATE TABLE book
        (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            supplier_id INTEGER NOT NULL,
            name TEXT NOT NULL,
            price INTEGER NOT NULL,
            quantity INTEGER NOT NULL DEFAULT 0,
            image INTEGER,
            FOREIGN KEY (supplier_id) REFERENCES supplier (_id)
        );
         */
        String CREATE_BOOK_TABLE = CREATE_TABLE + SPACE + BookEntry.TABLE_NAME +
                BL +
                BookEntry._ID + SPACE + INTEGER_PRIMARY_KEY_AUTOINCREMENT + CONT +
                BookEntry.COLUMN_BOOK_SUPPLIER_ID + SPACE + INTEGER_NOT_NULL + CONT +
                BookEntry.COLUMN_BOOK_NAME + SPACE + TEXT_NOT_NULL + CONT +
                BookEntry.COLUMN_BOOK_PRICE + SPACE + INTEGER_NOT_NULL + CONT +
                BookEntry.COLUMN_BOOK_QUANTITY + SPACE + INTEGER_NOT_NULL + SPACE + DEFAULT + SPACE + defaultQty + CONT +
                BookEntry.COLUMN_BOOK_IMAGE + SPACE + INTEGER + CONT +
                FOREIGN_KEY + SPACE + BL + BookEntry.COLUMN_BOOK_SUPPLIER_ID + BR + SPACE +
                REFERENCES + SPACE + SupplierEntry.TABLE_NAME + SPACE + BL + SupplierEntry._ID + BR +
                BR + STATEMENT_END;

        // create database tables
        db.execSQL(CREATE_SUPPLIER_TABLE);
        db.execSQL(CREATE_BOOK_TABLE);
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
        if (prevVersion != newVersion) {
            // drop current tables losing all data
            // TODO: migrate data in production
            db.execSQL(DROP_TABLE_IF_EXISTS + SPACE + BookEntry.TABLE_NAME);
            db.execSQL(DROP_TABLE_IF_EXISTS + SPACE + SupplierEntry.TABLE_NAME);

            // recreate database
            onCreate(db);
        }
    }

    /**
     * Return a list of all books in the database
     *
     * @return list of {@link Book} objects
     */
    public Cursor getBooks() {
        SQLiteDatabase db = getReadableDatabase();

        // execute the query on the database
        return db.query(                // distinct
                BookEntry.TABLE_NAME,   // table
                null,                   // columns. null = all
                null,                   // WHERE
                null,                   // The values for the WHERE clause
                null,                   // group by
                null,                   // having
                null);                  // order by
    }

    /**
     * Return a list of all suppliers in the database
     *
     * @return list of {@link Supplier} objects
     */
    public Cursor getSuppliers() {

        SQLiteDatabase db = getReadableDatabase();

        // execute the query on the database
        return db.query(                    // distinct
                SupplierEntry.TABLE_NAME,   // table
                null,                       // columns. null = all
                null,                       // WHERE
                null,                       // The values for the WHERE clause
                null,                       // group by
                null,                       // having
                null);                      // order by
    }

    /**
     * Insert or Update a book
     *
     * @param book to insert or update
     * @return primary key of the row we updated
     */
    public long save(Book book) {
        SQLiteDatabase db = getWritableDatabase();
        long bookId = -1;

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, book.getName());
            values.put(BookEntry.COLUMN_BOOK_PRICE, book.getPrice());
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, book.getQuantity());
            values.put(BookEntry.COLUMN_BOOK_IMAGE, book.getImageResourceId());
            values.put(BookEntry.COLUMN_BOOK_SUPPLIER_ID, book.getSupplier().getId());

            String id = String.valueOf(book.getId());

            // try to update an existing row
            int rows = db.update(
                    BookEntry.TABLE_NAME,
                    values,
                    BookEntry._ID + " = ?",
                    new String[]{id});

            if (rows == 1) {
                // update success
                bookId = book.getId();
            } else {
                // we need to insert a new row
                bookId = db.insertOrThrow(BookEntry.TABLE_NAME, null, values);
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while inserting or updating a book", e);
        } finally {
            db.endTransaction();
        }
        return bookId;
    }

    /**
     * Insert or Update a supplier
     *
     * @param supplier to insert or update
     * @return primary key of the row we updated
     */
    public long save(Supplier supplier) {
        SQLiteDatabase db = getWritableDatabase();
        long supplierId = -1;

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            values.put(SupplierEntry.COLUMN_SUPPLIER_NAME, supplier.getName());
            values.put(SupplierEntry.COLUMN_SUPPLIER_EMAIL, supplier.getEmail());
            values.put(SupplierEntry.COLUMN_SUPPLIER_PHONE_NUM, supplier.getPhoneNum());

            String id = String.valueOf(supplier.getId());

            // try to update an existing row
            int rows = db.update(SupplierEntry.TABLE_NAME, values,
                    SupplierEntry._ID + " = ?", new String[]{id});

            if (rows == 1) {
                // update success
                supplierId = supplier.getId();
            } else {
                // we need to insert a new row
                supplierId = db.insertOrThrow(SupplierEntry.TABLE_NAME, null, values);
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception while inserting or updating a supplier", e);
        } finally {
            db.endTransaction();
        }
        return supplierId;
    }

    /**
     * Return a supplier with a given id
     *
     * @param supplierId of the supplier to get from the database
     * @return supplier
     */
    public Supplier getSupplierById(long supplierId) {
        Supplier supplier = null;

        SQLiteDatabase db = getReadableDatabase();

        db.beginTransaction();
        Cursor cursor = db.query(
                true,                                       // distinct
                SupplierEntry.TABLE_NAME,                   // table
                null,                                       // columns. null = all
                SupplierEntry._ID + " = ?",                 // WHERE
                new String[]{String.valueOf(supplierId)},  // The values for the WHERE clause
                null,                                       // group by
                null,                                       // having
                null,                                       // order by
                null);                                      // having
        try {
            if (cursor.getCount() > 0) {

                // we're asking for a distinct row and selecting by id
                // so we should never receive multiple rows
                cursor.moveToFirst();

                supplier = new Supplier(
                        cursor.getInt(cursor.getColumnIndex(
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
            db.endTransaction();
        }

        return supplier;
    }
}
