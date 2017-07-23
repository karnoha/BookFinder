package com.example.android.bookfinder;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Books>> {

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

    private EditText mSearch;

    // show progress bar when loading images
    private View mProgressBar;

    private Button mGoButton;

    private static final int BOOKS_LOADER_ID = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView booksListView = (ListView) findViewById(R.id.list);
        mSearch = (EditText) findViewById(R.id.search);
        mGoButton = (Button) findViewById(R.id.gobabygo);
        mProgressBar = findViewById(R.id.loading_indicator);

        // empty view for no result line
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyStateTextView);

        mEmptyStateTextView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

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

        if (checkNet()){
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(BOOKS_LOADER_ID, null, MainActivity.this);
        }


        mGoButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                boolean netOK = checkNet();
                if (netOK) {
                    getLoaderManager().restartLoader(BOOKS_LOADER_ID, null, MainActivity.this);
                } else {
                    mEmptyStateTextView.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
            }
        });
    }


    public boolean checkNet() {
        // all function for search button were moved to this method from the oncreate method.
        // Now we check if we loose an internet connection during app sessoin

        // initialize a con mgr to get state of network
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        final boolean networkOk = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return networkOk;
    }

    @Override
    public Loader<List<Books>> onCreateLoader(int id, Bundle args) {
        String userSearch = mSearch.getText().toString();
        Log.v(LOG_TAG, "LOG - string usersearch: " + userSearch);
        Uri baseUri = Uri.parse(API_URL + userSearch + LIMIT_RESULTS);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        return new BooksLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> books) {
        mAdapter.clear();
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        mAdapter.clear();
    }
}