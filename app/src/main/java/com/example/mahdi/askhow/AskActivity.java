package com.example.mahdi.askhow;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Created by Mahdi on 3/12/2016.
 */
public class AskActivity extends AppCompatActivity{

    EditText postTitle;
    EditText postContent;
    EditText postTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        postTitle = (EditText)findViewById(R.id.postTitle);
        postContent = (EditText)findViewById(R.id.postContent);
        postTags = (EditText)findViewById(R.id.postTags);

        final Typeface font = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");

        postTitle.setTypeface(font);
        postContent.setTypeface(font);
        postTags.setTypeface(font);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.ask_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
