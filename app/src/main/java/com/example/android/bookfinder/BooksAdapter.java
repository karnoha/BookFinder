package com.example.android.bookfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Karnoha on 13.07.2017.
 */

public class BooksAdapter extends ArrayAdapter<Books> {

    public BooksAdapter(Context context, List<Books> books){
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listBookView = convertView;
        if (listBookView ==null){
            listBookView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_book, parent, false);
        }


        Books currentBook = getItem(position);

        TextView titleView = (TextView) listBookView.findViewById(R.id.list_title);
        titleView.setText(currentBook.getTitle());

        TextView authorsView = (TextView) listBookView.findViewById(R.id.list_authors);
        authorsView.setText(currentBook.getAuthors());

        TextView priceView = (TextView) listBookView.findViewById(R.id.list_price);
        priceView.setText(currentBook.getPrice());

        TextView langView = (TextView) listBookView.findViewById(R.id.list_lang);
        langView.setText(currentBook.getLang());

        TextView pagesView = (TextView) listBookView.findViewById(R.id.list_pages);
        pagesView.setText(currentBook.getPageCount());

        return listBookView;
    }
}
