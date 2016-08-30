package com.luke_kim.android.stockhawk.service;

import android.support.annotation.IntDef;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.luke_kim.android.stockhawk.ui.MyStockDetailActivityFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Created by lukekim on 8/29/16.
 */
public class StockHistoryData {

    StockHistoryDataCallback callback;
    ArrayList<String> dates;
    ArrayList<String> stockPrices;
    private static final String TAG = StockHistoryData.class.getSimpleName();

    //to form the url for any symbol
    final String BASE_URL = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    final String END_URL = "/chartdata;type=quote;range=1y/json";


    //to parse the json data..
    private static final String JSON_SERIES = "series";
    private static final String JSON_DATE = "Date";
    private static final String JSON_CLOSE = "close";


    //to indicate errors incurred during parsing.
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_OK, STATUS_ERROR_JSON, STATUS_ERROR_NO_NETWORK, STATUS_ERROR_PARSE
            , STATUS_ERROR_SERVER, STATUS_ERROR_UNKNOWN})
    public @interface HistoricalDataStatuses {
    }

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR_JSON = 1;
    public static final int STATUS_ERROR_SERVER = 2;
    public static final int STATUS_ERROR_PARSE = 3;
    public static final int STATUS_ERROR_NO_NETWORK = 4;
    public static final int STATUS_ERROR_UNKNOWN = 5;


    public StockHistoryData(MyStockDetailActivityFragment object, String symbol) {
        this.callback = object;
        retrieveStockHistory(symbol);
    }

    public void retrieveStockHistory(String symbol) {

        String URL = BASE_URL + symbol + END_URL;

        final StringRequest request = new StringRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            dates = new ArrayList<String>();
                            stockPrices = new ArrayList<String>();
                            try {

                                String json = response.substring(response.indexOf("(") + 1, response.lastIndexOf(")"));
                                JSONObject mainObject = new JSONObject(json);
                                JSONArray series_data = mainObject.getJSONArray(JSON_SERIES);
                                for (int i = 0; i < series_data.length(); i += 10) {
                                    JSONObject singleObject = series_data.getJSONObject(i);
                                    String date = singleObject.getString(JSON_DATE);
                                    double close = singleObject.getDouble(JSON_CLOSE);
                                    dates.add(date);
                                    stockPrices.add(String.valueOf(close));
                                }
                                if (callback != null) {
                                    callback.onSuccess(dates, stockPrices);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

//                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
//                            setHistoricalDataStatus(STATUS_ERROR_NO_NETWORK);
//                        } else if (error instanceof ServerError) {
//                            setHistoricalDataStatus(STATUS_ERROR_SERVER);
//                        } else if (error instanceof NetworkError) {
//                            setHistoricalDataStatus( STATUS_ERROR_UNKNOWN);
//                        } else if (error instanceof ParseError) {
//                            setHistoricalDataStatus(STATUS_ERROR_PARSE);
//                        }

                        if (callback != null) {
                            callback.onFailure();
                        }
                    }
                }
        );
        AppController.getInstance().addToRequestQueue(request, TAG);
    }


    /**
     * Interface to interact with the callee class to notify regarding success, or errors if any.
     */
    public interface StockHistoryDataCallback {
        void onSuccess(ArrayList list1, ArrayList list2);
        void onFailure();
    }
}
