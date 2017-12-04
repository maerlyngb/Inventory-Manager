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

import java.util.List;

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

        // adapter used to display inventory data
        cursorAdapter = new InventoryCursorAdapter(this, null);
        inventoryListView.setAdapter(cursorAdapter);

        // click listener to edit a book's details
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

        createDefaultSupplier();

        // get inventory data
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

        // make sure we still have out default supplier
        createDefaultSupplier();

        // display toast showing how many rows were deleted
        Toast.makeText(this, rowsDeleted[0] + " " + getString(R.string.suppliers) +
                        " " + getString(R.string.and) + " " +
                        rowsDeleted[1] + " " + getString(R.string.books_deleted),
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Create a default supplier for new books to use
     */
    private void createDefaultSupplier() {
        List<Supplier> suppliers = Inventory.getAllSuppliers(this);
        if (suppliers.size() < 1) {
            Supplier emptySupplier = new Supplier(
                    "No Supplier",
                    "blank@email.com",
                    "0000 000 000");

            long supplierId = Inventory.insert(emptySupplier, this);
            emptySupplier.setId(supplierId);
        }
    }

    /**
     * Insert testing data into the database
     */
    private void insertDummyData() {

        Supplier amazon = new Supplier(
            "Amazon",
            "books@google.com",
            "0432 345 634");

        long amazonId = Inventory.insert(amazon, this);
        amazon.setId(amazonId);

        Supplier googleBooks = new Supplier(
                "Google Books",
                "books@google.com",
                "0432 345 654");

        long supplierId = Inventory.insert(googleBooks, this);
        googleBooks.setId(supplierId);

        Book dune = new Book(
                amazon,
                "Dune",
                1199, // price in cents
                5, // quantity
                ImageUtil.getBytes(R.drawable.dune_cover, this)
        );
        long duneId = Inventory.insert(dune, this);
        dune.setId(duneId);

        Book time = new Book(
                googleBooks,
                "A Brief History Of Time",
                1299, // price in cents
                7, // quantity
                ImageUtil.getBytes(
                        R.drawable.brief_history_of_time_cover, this)
        );
        long timeId = Inventory.insert(time, this);
        time.setId(timeId);

        Book neuromancer = new Book(
                amazon,
                "Neuromancer",
                1199, // price in cents
                11, // quantity
                ImageUtil.getBytes(R.drawable.neuromancer_cover, this)
        );

        long neuromancerid = Inventory.insert(neuromancer, this);
        neuromancer.setId(neuromancerid);

        Book animalFarm = new Book(
                googleBooks,
                "Animal Farm",
                99, // price in cents
                6, // quantity
                ImageUtil.getBytes(R.drawable.animal_farm_cover, this)
        );

        long animalFarmId = Inventory.insert(animalFarm, this);
        animalFarm.setId(animalFarmId);

        Book hgg = new Book(
                googleBooks,
                "The Hitchhiker's Guide to the Galaxy",
                999, // price in cents
                42, // quantity
                ImageUtil.getBytes(R.drawable.hgg_cover, this)
        );

        long hggId = Inventory.insert(hgg, this);
        hgg.setId(hggId);

        Book timeMachine = new Book(
                googleBooks,
                "The Time Machine",
                999, // price in cents
                12, // quantity
                ImageUtil.getBytes(R.drawable.time_machine_cover, this)
        );

        long timeMachineid = Inventory.insert(timeMachine, this);
        timeMachine.setId(timeMachineid);

    }
}
