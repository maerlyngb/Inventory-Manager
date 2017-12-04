package io.maerlyn.inventorymanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.maerlyn.inventorymanager.data.Inventory;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

/**
 * Allow a user to edit an exiting book or create a new one
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();
    public static final int PICK_IMAGE = 1;
    // id for the data loader
    private static final int EXISTING_BOOK_LOADER = 0;
    AlertDialog supplierSwitcher;
    List<Supplier> suppliers;
    int newSupplierId = 0;
    @BindView(R.id.edit_book_name)
    EditText bookTitle;
    @BindView(R.id.edit_book_price)
    EditText bookPrice;
    @BindView(R.id.edit_book_quantity)
    EditText bookQuantity;
    @BindView(R.id.book_cover_image)
    ImageView bookCover;
    @BindView(R.id.sell_book)
    Button sellBtn;
    @BindView(R.id.order_book)
    Button orderBtn;
    // uri of the book we're editing
    private Uri bookUri;
    // true if we're adding a new book
    private boolean isNewBook = false;
    private Book book;

    // keeps track of whether there have been any data modifications
    private boolean hasBookChanged = false;

    // listener for data modifications
    private View.OnTouchListener touchListener = (view, motionEvent) -> {
        view.performClick();
        hasBookChanged = true;
        return false;
    };
    private Uri newImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // bind annotated views
        ButterKnife.bind(this);

        // retrieve the uri passed with the intent
        Intent intent = getIntent();
        bookUri = intent.getData();

        // if we don't have a uri, we're creating a new book
        if (bookUri == null) {
            this.isNewBook = true;
            setTitle(getString(R.string.add_new_book));

            bookCover.setImageResource(R.drawable.blank_cover);

            LinearLayout supplierLayout = findViewById(R.id.supplierLayout);
            supplierLayout.setVisibility(View.GONE);

            // the options menu is only for editing an existing book
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        bookTitle.setOnTouchListener(touchListener);
        bookPrice.setOnTouchListener(touchListener);
        bookQuantity.setOnTouchListener(touchListener);

        if (isNewBook) {
            sellBtn.setVisibility(View.GONE);
            orderBtn.setVisibility(View.GONE);
        } else {
            sellBtn.setOnClickListener(view -> sellBook(this));
            orderBtn.setOnClickListener(view -> orderBook());
        }

        if (isNewBook) {
            setupSpinner();
        }
    }

    /**
     * Create the supplier selection dropdown
     * https://stackoverflow.com/questions/15404206/populating-spinner-using-arraylist-in-android
     */
    private void setupSpinner() {
        Spinner supplierSpinner = findViewById(R.id.spinner_supplier);
        supplierSpinner.setVisibility(View.VISIBLE);

        if (this.suppliers == null) {
            this.suppliers = Inventory.getAllSuppliers(this);
        }

        List<SpinnerOption> options = new ArrayList<>();

        for (Supplier supplier : this.suppliers) {
            SpinnerOption option = new SpinnerOption();
            option.setKEY_SETID(String.valueOf(supplier.getId()));
            option.setKEY_SETNAME(String.valueOf(supplier.getName()));
            options.add(option);
        }

        ArrayAdapter<SpinnerOption> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, options);

        supplierSpinner.setAdapter(adapter);

        // Set the integer mSelected to the constant values
        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newSupplierId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                if (saveBook()) {
                    finish();
                }
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
        builder.setNegativeButton(R.string.cancel, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Remove a book from inventory
     */
    private void deleteBook() {
        // can't delete a book if we don't have the uri
        if (bookUri != null) {
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

    private boolean saveBook() {
        String title = bookTitle.getText().toString().trim();
        String priceStr = bookPrice.getText().toString().trim();
        String quantityStr = bookQuantity.getText().toString().trim();

        if (isNewBook && TextUtils.isEmpty(title) && TextUtils.isEmpty(priceStr) &&
                TextUtils.isEmpty(quantityStr)) {

            // if this is a new book and all of the fields are empty, no need to create anything
            return true;
        }

        if (isNewBook) {
            this.book = new Book();
        }

        this.book.setName(title);

        if (TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, R.string.please_enter_price, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            // parse string dollar value to double
            double price = Double.parseDouble(priceStr);

            // convert dollars to cents so we don't have bad values
            this.book.setPrice((int) Math.floor(price * 100));

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.please_enter_valid_price, Toast.LENGTH_SHORT).show();
            return false;
        }


        if (TextUtils.isEmpty(quantityStr)) {
            Toast.makeText(this, R.string.please_enter_quantity, Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            this.book.setQuantity(Integer.valueOf(quantityStr));

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.please_enter_valid_qty, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newImageUri != null){
            Bitmap bitmap = null;
            try {
                bitmap = ImageUtil.getImage(this.newImageUri, this);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unable to load image from uri", e);
            }

            if (bitmap != null){
                this.book.setImage(ImageUtil.getBytes(bitmap));
            }

        }

        if (isNewBook) {
            this.book.setSupplier(suppliers.get(newSupplierId));
            long newBookId = Inventory.insert(this.book, this);
            if (newBookId > 0) {
                Toast.makeText(this, R.string.book_inserted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.could_not_insert_book, Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            if (Inventory.update(this.book, this)) {
                Toast.makeText(this, R.string.book_updated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.could_not_update_book, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
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
        return Inventory.getBookDetailCursor(this, ContentUris.parseId(bookUri));
    }

    /**
     * The loader has just returned our data
     *
     * @param loader that just finished getting data
     * @param cursor data set
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            this.book = Inventory.bookDetailCursorToBook(cursor);
            this.book.setId(ContentUris.parseId(bookUri));

            // Update the views on the screen with the values from the database
            this.bookTitle.setText(this.book.getName());
            this.bookPrice.setText(this.book.getPriceString());
            this.bookQuantity.setText(String.valueOf(book.getQuantity()));

            Bitmap coverBitmap = ImageUtil.getImage(this.book.getImage());
            this.bookCover.setImageBitmap(coverBitmap);

            setupSupplierSection(this.book.getSupplier());
        }
    }

    /**
     * Allow the user to switch to a new supplier
     */
    private void switchSupplier() {
        // dialog with supplier options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select a new supplier");

        if (this.suppliers == null) {
            this.suppliers = Inventory.getAllSuppliers(this);
        }

        CharSequence[] supplierOptions = new String[suppliers.size()];

        for (int i = 0; i < suppliers.size(); i++) {
            supplierOptions[i] = suppliers.get(i).getName();
        }

        builder.setSingleChoiceItems(supplierOptions, -1, (dialog, item) -> {
            // update the book with the newly selected supplier
            updateSupplier(suppliers.get(item));

            // close the options dialog
            supplierSwitcher.dismiss();
        });
        supplierSwitcher = builder.create();
        supplierSwitcher.show();
    }

    private void updateSupplier(Supplier newSupplier) {
        if (newSupplier.getId() == this.book.getSupplier().getId()) {
            // the user has chosen the same supplier, so do nothing.
            return;
        }

        this.hasBookChanged = true;

        // assign the new supplier to the book
        this.book.setSupplier(newSupplier);

        // redraw supplier ui
        setupSupplierSection(newSupplier);

        // don't persist any data until the user presses save
    }

    /**
     * Pick a new cover image for this book
     */
    @OnClick(R.id.book_cover_image)
    void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        startActivityForResult(intent, PICK_IMAGE);
    }

    /**
     * Process an image that a user has selected from the gallery
     *
     * @param requestCode code of the request
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            // make sure to confirm changes before navigating away
            this.hasBookChanged = true;

            // save the image url
            this.newImageUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = ImageUtil.getImage(this.newImageUri, this);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Unable to load image from uri", e);
            }

            bookCover.setImageBitmap(bitmap);
        }
    }

    /**
     * Callback called when the data needs to be deleted
     *
     * @param loader that was reset
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.bookTitle.setText("");
        this.bookPrice.setText("");
        this.bookQuantity.setText("");
    }

    /**
     * Setup the supplier section of the edit screen
     *
     * @param supplier details to use
     */
    private void setupSupplierSection(Supplier supplier) {
        TextView supplierName = findViewById(R.id.supplier_name);
        supplierName.setText(supplier.getName());

        // enable the user to email the supplier
        TextView supplierEmail = findViewById(R.id.supplier_email_action);
        supplierEmail.setOnClickListener((view) -> email(supplier.getEmail()));

        // enable the user to call the supplier
        TextView supplierPhone = findViewById(R.id.supplier_phone_action);
        supplierPhone.setOnClickListener((view) -> call(supplier.getPhoneNum()));

        // enable the user to switch suppliers
        TextView supplierSwitch = findViewById(R.id.supplier_switch_action);
        supplierSwitch.setOnClickListener((view) ->
                switchSupplier());
    }

    /**
     * Sell a single book and save updated quantity to db
     *
     * @param context app context
     */
    private void sellBook(Context context) {
        if (!this.book.sellBook()) {
            Toast.makeText(context, R.string.no_books_to_sell, Toast.LENGTH_SHORT).show();
            // don't persis data until the user clicks on save
        } else {
            this.bookQuantity.setText(String.valueOf(this.book.getQuantity()));
        }
    }

    /**
     * Sell a single book and save updated quantity to db
     */
    private void orderBook() {
        this.book.orderBook();
        this.bookQuantity.setText(String.valueOf(this.book.getQuantity()));
        // don't persis data until the user clicks on save
    }

    /**
     * Start an implicit intent to call a phone number
     */
    private void call(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Start an implicit intent to sent an email
     */
    private void email(String emailAddr) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddr});
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, ""));
        }
    }

}
