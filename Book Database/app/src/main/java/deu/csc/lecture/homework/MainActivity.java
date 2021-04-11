package deu.csc.lecture.homework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    EditText bookName;
    EditText pages;
    EditText authorName;
    BookDatabase myBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBooks = new BookDatabase(this);

        bookName = (EditText) findViewById(R.id.bookName);
        pages = (EditText) findViewById(R.id.pages);
        authorName = (EditText) findViewById(R.id.authorName);
    }

    public void insert_onClick(View view) {
       try {
            String bookNameString = bookName.getText().toString();
            int pagesInt = Integer.parseInt(pages.getText().toString());
            String authorNameString = authorName.getText().toString();
            myBooks.InsertBook(bookNameString, pagesInt, authorNameString);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void list_onClick(View view) {
        Intent intent = new Intent(this, List.class);
        ArrayList<String> queryResult = myBooks.SelectRecords();
        intent.putExtra("queryResult", queryResult);
        startActivity(intent);
    }

    public void delete_onClick(View view) {
        myBooks.dropTable();
    }
}
