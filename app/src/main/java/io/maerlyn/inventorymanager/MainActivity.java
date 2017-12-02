package io.maerlyn.inventorymanager;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import io.maerlyn.inventorymanager.data.Inventory;
import io.maerlyn.inventorymanager.data.InventoryContract;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

/**
 * Displays a list of available books
 */
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // id for the inventory data loader
    private static final int INVENTORY_SUMMARY_LOADER = 0;

    // Adapter for the ListView to display the inventory summary
    InventoryCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inventory summary view
        ListView inventoryListView = findViewById(R.id.inventory_list);

        // the empty view will only be displayed when we can't get any data from the database
        View emptyView = findViewById(R.id.empty_view);
        inventoryListView.setEmptyView(emptyView);

        cursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(cursorAdapter);

        // Setup the item click listener
        inventoryListView.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);

            // build up the uri of the book that was just clicked
            Uri currentPetUri = ContentUris.withAppendedId(
                    InventoryContract.BookEntry.CONTENT_URI, id);

            // add the uri to the intent data so we know which record we're editing
            intent.setData(currentPetUri);

            startActivity(intent);
        });

        // Button to create a new book
        FloatingActionButton fab = findViewById(R.id.add_new_book);
        fab.setOnClickListener((view) -> {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            startActivity(intent);
        });

        getLoaderManager().initLoader(INVENTORY_SUMMARY_LOADER, null, this);
    }

    /**
     * Return a {@link Loader<Cursor>} to populate the inventory summary {@link ListView}
     *
     * @param id     of the loader
     * @param bundle {@link Bundle} arguments
     * @return {@link Loader<Cursor>} used to populate the ListView
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        return Inventory.getSummaryLoader(this);
    }


    /**
     * Called when a previously created loader has finished its load
     *
     * @param loader that has just finished getting data
     * @param cursor containing the data
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update {@link InventoryCursorAdapter} with an updated data set
        cursorAdapter.swapCursor(cursor);
    }

    /**
     * Called when a previously created loader is being reset, and thus making its data unavailable.
     *
     * @param loader that has just been reset
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The loader has been reset so we need to remove the current data
        cursorAdapter.swapCursor(null);
    }

    /**
     * Initialise the menu in the action bar
     *
     * @param menu to be initialised
     * @return true when completed
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_summary, menu);
        return true;
    }

    /**
     * Handles clicks on menu items in the action bar
     *
     * @param item that was clicked on
     * @return false to allow normal menu processing to proceed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllData();
                return true;
        }
        return false;
    }

    /**
     * Delete all data in the database
     */
    private void deleteAllData() {
        int[] rowsDeleted = Inventory.deleteAll(this);

        // display toast showing how many rows were deleted
        Toast.makeText(this, rowsDeleted[0] + " " + getString(R.string.suppliers) +
                        " " + getString(R.string.and) + " " +
                        rowsDeleted[1] + " " + getString(R.string.books_deleted),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Insert testing data into the database
     */
    private void insertDummyData() {
        Supplier newSupplier = new Supplier(
                "Bob's Books",
                "books@supplier.com",
                "0432 345 654");

        long supplierId = Inventory.insert(newSupplier, this);
        newSupplier.setId(supplierId);

        Book newBook = new Book(
                newSupplier, // supplier
                "Lord of the Rings", // name
                523, // price in cents
                23, // quantity
                11 // image resource id
        );

        long newBookId = Inventory.insert(newBook, this);
        newBook.setId(newBookId);

    }
}
