package io.maerlyn.inventorymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import io.maerlyn.inventorymanager.data.Inventory;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // create supplier object
        Supplier newSupplier = new Supplier(
                "Bob's Books",
                "books@supplier.com",
                "0432 345 654");

        Inventory.insertSupplier(newSupplier, this);

        // create a new book object
        Book newBook = new Book(
                newSupplier, // supplier
                "Lord of the Rings", // name
                523, // price in cents
                23, // quantity
                11 // image resource id
        );

        Inventory.addNewBook(newBook, this);

        // load all books from the database
        List<Book> books = Inventory.getAllBooks(this);

        TextView booksTextView = findViewById(R.id.books);

        // display list of books
        for (Book book : books) {
            booksTextView.append("\n");
            booksTextView.append(book.getName() + ", ");
            booksTextView.append(book.getPriceString() + ", ");
            booksTextView.append(String.valueOf(book.getQuantity()));
        }

        List<Supplier> suppliers = Inventory.getAllSuppliers(this);

        TextView supplierTextView = findViewById(R.id.suppliers);

        // display list of suppliers
        for (Supplier supplier : suppliers) {
            supplierTextView.append("\n");
            supplierTextView.append(supplier.getName() + ", ");
            supplierTextView.append(supplier.getEmail() + ", ");
            supplierTextView.append(supplier.getPhoneNum());
        }
    }
}
