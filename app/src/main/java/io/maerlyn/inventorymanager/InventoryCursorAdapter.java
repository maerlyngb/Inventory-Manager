package io.maerlyn.inventorymanager;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import io.maerlyn.inventorymanager.data.InventoryContract.BookEntry;

/**
 * Created by maerlyn on 2/12/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    Context context;

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.context = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    /**
     * Populate a given {@link View} with data from a {@link Cursor}
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameView = view.findViewById(R.id.book_name);
        TextView priceView = view.findViewById(R.id.book_price);
        TextView quantityView = view.findViewById(R.id.book_quantity);

        // Read the pet attributes from the Cursor for the current pet
        String name = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME));
        String price = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE));
        String quantity = cursor.getString(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));

        // If the pet breed is empty string or null, then use some default text
        // that says "Unknown breed", so the TextView isn't blank.
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.unknown_book_title);
        }

        // Update the TextViews with the attributes for the current pet
        nameView.setText(name);
        priceView.setText(price);
        quantityView.setText(quantity);
    }
}
