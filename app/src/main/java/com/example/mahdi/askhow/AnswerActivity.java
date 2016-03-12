package com.example.mahdi.askhow;

import android.app.ProgressDialog;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Items.Post;
import adapter.AnswerAdapter;
import database_handler.PostHandler;
import libs.AppController;

public class AnswerActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {


    private static String TAG = AnswerActivity.class.getSimpleName();
    FloatingActionButton fab;
    PostHandler ph;
    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AnswerAdapter qAdapter;
    private String jsonResponse;
    // Progress dialog
    private ProgressDialog pDialog;
//    private String urlJsonArry = "http://192.168.44.242/askhow/v1/getposts/1/10/17";
    private String urlJsonArry = "http://192.168.42.190/askhow/v1/getposts/1/10/17";
    private AVLoadingIndicatorView progressbar;
    private int mStatusCode;

    Toolbar toolbar;

    int post_mysql_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        toolbar = (Toolbar)findViewById(R.id.toolbar);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressbar = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);
        ph = new PostHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            if (extras.getInt("post_mysql_id") != 0) {
                post_mysql_id = extras.getInt("post_mysql_id");
                            postList.add(0,ph.getPost(post_mysql_id));
                TypedArray tColors = getResources().obtainTypedArray(R.array.tag_colors);
                int color = Math.abs(post_mysql_id) % 17;
                recyclerView.setBackgroundColor(tColors.getColor(color, Color.BLACK));

            }
        qAdapter = new AnswerAdapter(postList);



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(qAdapter);


        urlJsonArry = "http://192.168.42.190/askhow/v1/getposts/1/10/" + post_mysql_id;
        makeJsonArrayRequest();
    }

    /**
     * Method to make json array request where response starts with [
     * */
    private void makeJsonArrayRequest() {

        showpDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonArry, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("error") == "1")
                        return;
                    JSONArray ja = response.getJSONArray("posts");
                    Post post;
                    JSONObject posts;
                    for (int i = 0; i < ja.length(); i++) {
                        post = new Post();
                        posts = (JSONObject) ja.get(i);

//                                String name = person.getString("name");
//                                String email = person.getString("email");
//                        posts = postarray.getJSONObject("posts");
                        int id = posts.getInt("id");
                        String posterName = posts.getString("posterName");
                        String posterAvatar = posts.getString("posterAvatar");
                        String posterDegree = posts.getString("posterDegree");
                        String postTitle = posts.getString("postTitle");
                        String postDesc = posts.getString("postDesc");
                        Log.d("desc", postDesc);
                        post.setPost_mysql_id(id);
                        post.setPosterName(posterName);
                        post.setPosterAvatar(posterAvatar);
                        post.setPosterDegree(posterDegree);
                        post.setPostTitle(postTitle);
                        post.setPostDesc(postDesc);
                        post.setVotes(posts.getInt("votes"));
                        post.setAnswers(posts.getInt("answers"));
                        post.setViews(posts.getInt("views"));
//                        post.setiVoted(posts.getInt("ivoted") != 0);
                        post.setBookmarked(posts.getInt("bookmarked") != 0);
                        post.setIsRead(posts.getInt("isread") != 0);
                        post.setIsMine(posts.getInt("ismine") != 0);
                        post.setPublished(posts.getInt("published") != 0);
                        post.setLocked(posts.getInt("locked") != 0);
                        post.setQuestion(posts.getInt("is_question") != 0);
                        post.setQuestionMySqlID(posts.getInt("question_id"));
                        post.setTags(posts.getString("tags"));
                        post.setDate(posts.getString("date"));
                        post.setTime(posts.getString("time"));
//                        post.setReserved1(posts.getString("reserved1"));
//                        post.setReserved2(posts.getString("reserved2"));
//                        post.setReserved3(posts.getString("reserved3"));

                        post.setTags(posts.getString("tags"));

                        postList.add(post);

                    }

//                    if(postList.get(0).getPost_mysql_id()!= post_mysql_id)
//                        postList.add(0,ph.getPost(post_mysql_id));

//                    qAdapter.notifyDataSetChanged();

//                            txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d("network error", TAG + " tag ");
//                                Snackbar.make(fab.getRootView(), error.getMessage() + "", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();


                // hide the progress dialog
                hidepDialog();
            }

        });


//        if(postList.size() <= 0 || postList.get(0).getPost_mysql_id()!= post_mysql_id)
//            postList.add(0,ph.getPost(post_mysql_id));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void makeNetworkCall() {
        showpDialog();

        StringRequest request = new StringRequest(
                Request.Method.GET,
                urlJsonArry,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        // Success

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mStatusCode == 401) {
                            // HTTP Status Code: 401 Unauthorized
                        }

                        Snackbar.make(fab.getRootView(), mStatusCode + "", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        hidepDialog();
                    }
                }) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                Log.d("MY TAG", response.statusCode + " to string: " + response.toString());
                return super.parseNetworkResponse(response);
            }
        };
        AppController.getInstance().addToRequestQueue(request);
    }


    private void showpDialog() {
        progressbar.setVisibility(View.VISIBLE);
    }

    private void hidepDialog() {
        progressbar.setVisibility(View.GONE);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);

//        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//        searchView.setSubmitButtonEnabled(true);
//        searchView.setIconifiedByDefault(true); // Iconify the widget

//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id == R.id.action_search)
//        {
//            makeJsonArrayRequest();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }
}
