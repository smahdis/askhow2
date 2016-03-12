package com.example.mahdi.askhow;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

public class SearchResultsActivity extends AppCompatActivity {
    // Create an array
    String[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity_layout);

//        Intent intent = getIntent();
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            // At the end of doMySearch(), you can populate
//            // a String Array as resultStringArray[] and set the Adapter
//            doMySearch search = new doMySearch();
//            search.execute(query);
        }
    }

//    class doMySearch extends AsyncTask<String,Void,String> {
//        @Override
//        protected String doInBackground(String... params) {
//            // Connect to a SQLite DataBase, do some stuff..
//            // Populate the Array, and return a succeed message
//            // As String succeed = "Loaded";
//            return "Loaded";
//        }
//        @Override
//        protected void onPostExecute(String result) {
//            if(result.equals("Loaded")) {
//                // You can create and populate an Adapter
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//                        SearchResultsActivity.this,
//                        android.R.layout.simple_list_item_1, values);
//                setListAdapter(adapter);
//            }
//        }
//    }
//}