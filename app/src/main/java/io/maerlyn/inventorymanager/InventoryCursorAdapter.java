package io.maerlyn.inventorymanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.maerlyn.inventorymanager.data.Inventory;
import io.maerlyn.inventorymanager.model.Book;


/**
 * Adapter to get inventory data from a cursor and pass it to a ListView
 *
 * @author Maerlyn Broadbent
 */
public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.inventory_item, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
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

        ViewHolder holder = (ViewHolder) view.getTag();

        Book book = Inventory.cursorToBook(cursor, false, false, context);

        // Update the TextViews with the attributes for the current pet
        holder.nameView.setText(book.getName());
        holder.priceView.setText(context.getString(R.string.currency_symbol) + book.getPriceString());
        holder.quantityView.setText(String.valueOf(book.getQuantity()));
        holder.sellBook.setOnClickListener(btn -> sellBook(book, context));
    }

    /**
     * Sell a single book and save updated quantity to db
     *
     * @param book    to sell
     * @param context app context
     */
    private void sellBook(Book book, Context context) {
        if (book.sellBook()) {
            Inventory.update(book, context);
        } else {
            Toast.makeText(context, R.string.no_books_to_sell, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Holds references to views to improve performance
     */
    static class ViewHolder {
        @BindView(R.id.book_name) TextView nameView;
        @BindView(R.id.book_price) TextView priceView;
        @BindView(R.id.book_quantity) TextView quantityView;
        @BindView(R.id.sell_book) AppCompatImageButton sellBook;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
