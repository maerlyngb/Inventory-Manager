package io.maerlyn.inventorymanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

import io.maerlyn.inventorymanager.data.InventoryDbHelper;
import io.maerlyn.inventorymanager.model.Book;
import io.maerlyn.inventorymanager.model.Supplier;

public class MainActivity extends AppCompatActivity {

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
        InventoryDbHelper dbHelper = InventoryDbHelper.getInstance(this);

        // create supplier object
        Supplier newSupplier = new Supplier(
                "Bob's Books",
                "books@supplier.com",
                "0432 345 654");

        // save supplier to the db
        long newSupplierId = dbHelper.save(newSupplier);

        // set the supplier id to the val returned from dbHelper
        newSupplier.setId(newSupplierId);

        // load a supplier by id from the database
        // this should be the same one we just created
        Supplier loadedSupplier = dbHelper.getSupplierById(newSupplierId);

        // update the supplier email address
        loadedSupplier.setEmail("bob@books.com");

        // save updated supplier details
        dbHelper.save(loadedSupplier);

        // create a new book object
        Book newBook = new Book(
                loadedSupplier, // supplier
                "Lord of the Rings", // name
                523, // price in cents
                23, // quantity
                11 // image resource id
        );

        // get the id of the newly inserted book and save in the book object
        long newBookId = dbHelper.save(newBook);
        newBook.setId(newBookId);

        // update the name of the book
        newBook.setName("Treasure Island");

        // save changes to db
        dbHelper.save(newBook);

        // load all books from the database
        List<Book> books = dbHelper.getBooks();

        TextView booksTextView = findViewById(R.id.books);

        // display list of books
        for (Book book : books) {
            booksTextView.append("\n");
            booksTextView.append(book.getName() + ", ");
            booksTextView.append(book.getSupplier().getName() + ", ");
            booksTextView.append(book.getPriceString() + ", ");
            booksTextView.append(String.valueOf(book.getQuantity()));
        }

        List<Supplier> suppliers = dbHelper.getSuppliers();

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
