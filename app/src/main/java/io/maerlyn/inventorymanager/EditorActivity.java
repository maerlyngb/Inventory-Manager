package io.maerlyn.inventorymanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.maerlyn.inventorymanager.data.Inventory;
import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;

/**
 * Allow a user to edit an exiting book or create a new one
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // id for the data loader
    private static final int EXISTING_BOOK_LOADER = 0;

    private Uri bookUri;

    private EditText bookName;
    private EditText bookPrice;
    private EditText bookQuantity;

    // keeps track of whether there have been any data modifications
    private boolean hasBookChanged = false;

    // listener for data modifications
    private View.OnTouchListener touchListener = (view, motionEvent) -> {
        view.performClick();
        hasBookChanged = true;
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // retrieve the uri passed with the intent
        Intent intent = getIntent();
        bookUri = intent.getData();

        // if we don't have a uri, we're creating a new book
        if (bookUri == null) {
            setTitle(getString(R.string.add_new_book));

            // the options menu is only for editing an existing book
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // book input fields
        bookName = findViewById(R.id.edit_book_name);
        bookPrice = findViewById(R.id.edit_book_price);
        bookQuantity = findViewById(R.id.edit_book_quantity);

        bookName.setOnTouchListener(touchListener);
        bookPrice.setOnTouchListener(touchListener);
        bookQuantity.setOnTouchListener(touchListener);
    }

    /**
     * Inflate the app bar menu
     *
     * @param menu where the options will go
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Called after invalidateOptionsMenu() so that we can hide
     * menu items that don't make sense for the current action
     *
     * @param menu options menu
     * @return true
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // hide the delete button if this is a new book
        if (bookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    /**
     * Handle menu item actions when clicked
     *
     * @param item that was clicked on
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                confirmAndDeleteBook();
                return true;
            case android.R.id.home:
                if (!hasBookChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // if the current record has changed, confirm that the user wants
                // to discard their changes
                DialogInterface.OnClickListener discardListener =
                        (dialogInterface, i) -> {
                            // perform 'up' action
                            NavUtils.navigateUpFromSameTask(EditorActivity.this);
                        };

                // confirm action
                showUnsavedChangesDialog(discardListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete a book if the user confirms
     */
    private void confirmAndDeleteBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirm_request);

        // delete confirm
        builder.setPositiveButton(R.string.delete, (dialog, id) -> deleteBook());

        // cancel delete
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // continue
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Remove a book from inventory
     */
    private void deleteBook() {
        // can't delete a book if we don't have the uri
        if (bookUri != null){
            int rowsDeleted = getContentResolver().delete(bookUri, null, null);

            if (rowsDeleted == 0) {
                // we have an immortal book that can not be deleted
                Toast.makeText(this, R.string.book_not_deleted, Toast.LENGTH_SHORT).show();
            } else {
                // confirm delete success
                Toast.makeText(this, R.string.book_deleted, Toast.LENGTH_SHORT).show();
            }
        }

        // close activity
        finish();
    }

    private void saveBook() {
        //
    }

    /**
     * Called when the back button is pressed
     */
    @Override
    public void onBackPressed() {
        // if nothing has changed, continue as normal
        if (!hasBookChanged) {
            super.onBackPressed();
            return;
        }

        // confirm whether or not to discard changes
        DialogInterface.OnClickListener discardListener =
                (dialogInterface, i) -> {
                    // User clicked "Discard" button, close the current activity.
                    finish();
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardListener is the click listener for what to do when
     *                        the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardListener) {
        // create a new dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_conf_msg);
        builder.setNegativeButton(R.string.discard, discardListener);
        builder.setPositiveButton(R.string.continue_editing, (dialog, id) -> {
            // continue editing
            if (dialog != null) {
                dialog.dismiss();
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return Inventory.getSummaryLoader(this);
    }

    /**
     * The loader has just returned our data
     *
     * @param loader that just finished getting data
     * @param cursor data set
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int priceIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameIndex);
            int price = cursor.getInt(priceIndex);
            int quantity = cursor.getInt(quantityIndex);

            // Update the views on the screen with the values from the database
            bookName.setText(name);
            bookPrice.setText(Integer.toString(price));
            bookQuantity.setText(Integer.toString(quantity));

        }
    }

    /**
     * Callback called when the data needs to be deleted
     *
     * @param loader that was reset
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookName.setText("");
        bookPrice.setText("");
        bookQuantity.setText("");
    }

}
