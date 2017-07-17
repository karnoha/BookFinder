package com.example.android.bookfinder;

import static android.R.attr.author;
import static android.R.attr.value;
import static android.R.attr.valueFrom;

/**
 * Created by Karnoha on 13.07.2017.
 */

public class Books {

    // Declaration of variables
    private String mTitle;
    private StringBuilder mAuthors;
    private int mPageCount;
    private String mPrice;
    private String mLang;
    private String mUrl;


    //Constructor
    public Books(String title, StringBuilder authors, int pageCount, String price, String lang, String url){
        mTitle = title;
        mAuthors = authors;
        mPageCount = pageCount;
        mPrice = price;
        mLang = lang;
        mUrl = url;
    }

    // Getters
    public String getTitle(){return mTitle;}
    public StringBuilder getAuthors(){return mAuthors;}
    public String getPageCount(){
        String PageCountString = Integer.toString(mPageCount);
        return PageCountString;}
    public String getPrice(){return mPrice;}
    public String getLang(){return mLang;}
    public String getUrl(){return mUrl;}

}
