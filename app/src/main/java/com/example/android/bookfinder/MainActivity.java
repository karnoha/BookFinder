package com.example.android.bookfinder;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    // base of the url for query
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    // limit max results to 10.
    // possible app upgrade - add setting to set it manually
    private static final String LIMIT_RESULTS = "&maxResults=10";

    // defines a custom adapter for Books
    private BooksAdapter mAdapter;

    // text view to display no results, when something goes wrong
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView booksListView = (ListView) findViewById(R.id.list);

        // empty view for no result line
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BooksAdapter(this, new ArrayList<Books>());

        booksListView.setAdapter(mAdapter);

        // set onclick listener on a view in layout and start web intent with book URL
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Books currentBook = mAdapter.getItem(position);
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent browseUrl = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(browseUrl);
            }
        });

        // initialize a con mgr to get state of network
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


    }

    private class BookAsyncTask extends AsyncTask<String, Void, List<Books>> {

    }
}
