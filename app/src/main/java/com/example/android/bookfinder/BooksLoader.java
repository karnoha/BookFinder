package com.example.android.bookfinder;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by nohava on 22. 7. 2017.
 */

public class BooksLoader extends AsyncTaskLoader<List<Books>> {

    private static final String LOG_TAG = BooksLoader.class.getName();

    // query url
    private String mUrl;

    //cunstructor
    public BooksLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public List<Books> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Books> books = QueryUtils.getData(mUrl);
        return books;
    }

    @Override
    public void onStartLoading() {
        forceLoad();
    }
}
