package com.example.android.bookfinder;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Karnoha on 13.07.2017.
 */

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    public static final int READ_TIMEOUT = 10000;
    public static final int CONNECT_TIMEOUT = 15000;

    // constructor is empty, object instance of QueryUtils is not needed
    private QueryUtils() {
    }

    public static List<Books> getData(String requestUrl) {
        // Create URL
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP request failed", e);
        }

        return parseJson(jsonResponse);
    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building URL", e);
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        // prepare a variable where we'll store the whole response from the server
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;




        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the books JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    // converts the raw stream of data to a String with JSON and returns it back
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    // Creates an Array list from the JSON String. Take just what we need from the JSON
    private static List<Books> parseJson(String jsonResponse) {
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<Books> booksArrayList = new ArrayList<>();

        try {
            // This creates an object we can parse from the raw json data
            JSONObject jsonObject = new JSONObject(jsonResponse);
            // now we create an array with items - each is one entry with a book
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            // for each book parse more info and create
            for (int i = 0; i < itemsArray.length(); i++) {
                //create an object from current (i( book
                JSONObject currentBook = itemsArray.getJSONObject(i);
                JSONObject volumeInfoObject = currentBook.getJSONObject("volumeInfo");
                //////////////////title
                String title = volumeInfoObject.getString("title");


                //////////////////authors
                // Books have often more then 1 author, therefore they're stored in an Array
                // We need another loop and create a string with all authors on one line
                // using a StringBuilder
                JSONArray authorsArray;
                StringBuilder authors = new StringBuilder();
                if (volumeInfoObject.has("authors")) {
                    authorsArray = volumeInfoObject.getJSONArray("authors");
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authors.append(authorsArray.getString(j));
                        if (j < authorsArray.length()) {
                            authors.append(", ");
                        }
                    }
                } else {
                    authors.append("No authors found");
                }

                ///////////////////pagecount
                // extract pagecount, language and url from volumeInfo object
                int pageCount;
                if (volumeInfoObject.has("pageCount")) {
                    pageCount = volumeInfoObject.getInt("pageCount");
                } else {
                    pageCount = 0;
                }

                ///////////////////////language
                String lang;
                if (volumeInfoObject.has("language")) {
                    lang = volumeInfoObject.getString("language");
                } else {
                    lang = "?";
                }


                //////////////////////url
                String url = null;
                if (volumeInfoObject.has("infoLink")) {
                    url = volumeInfoObject.getString("infoLink");
                }


                //////////////////////price
                // price is stored in another object. I chose listPrice, this is original price,
                // retail price might not be with every book
                JSONObject saleInfoObject = currentBook.getJSONObject("saleInfo");
                String price;

                if (saleInfoObject.has("listPrice")) {
                    JSONObject listPriceObject = saleInfoObject.getJSONObject("listPrice");
                    double amount = listPriceObject.getDouble("amount");
                    String currency = listPriceObject.getString("currencyCode");
                    // now just concatenate double amount and string currency together
                    price = Double.toString(amount) + " " + currency;
                } else {
                    price = "price unknown";
                }


                Books booksObject = new Books(title, authors, pageCount, price, lang, url);
                booksArrayList.add(booksObject);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem with JSON parsing", e);
        }

        return booksArrayList;
    }
}