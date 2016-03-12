package com.example.mahdi.askhow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.wnafee.vector.MorphButton;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Items.Post;
import adapter.PostsAdapter;
import database_handler.DBCreator;
import database_handler.PostHandler;
import libs.AppController;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {


    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostsAdapter qAdapter;

    private FragmentDrawer drawerFragment;

    DBCreator db;
    private String jsonResponse;

    // Progress dialog
    private ProgressDialog pDialog;

//    private String urlJsonArry = "http://192.168.44.242/askhow/v1/getposts/1/10/-1";
    private String urlJsonArry = "http://192.168.42.190/askhow/v1/getposts/1/10/-1";
    private static String TAG = MainActivity.class.getSimpleName();
    FloatingActionButton fab;

    PostHandler ph;
    private AVLoadingIndicatorView progressbar;
    private PullToRefreshView mPullToRefreshView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ph = new PostHandler(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        postList.addAll(ph.getAllPosts(1));
        qAdapter = new PostsAdapter(postList);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        progressbar = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);


//        makeJsonArrayRequest();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(qAdapter);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, AskActivity.class);
                startActivity(intent);
//                makeJsonArrayRequest();
            }
        });

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        qAdapter.setOnItemClickListener(new PostsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
//                Log.d(TAG, "onItemClick position: " + position);
                int post_mysql_id = qAdapter.getItem(position).getPost_mysql_id();
                Intent answerActivity = new Intent(MainActivity.this, AnswerActivity.class);
                answerActivity.putExtra("post_mysql_id", post_mysql_id);
                startActivity(answerActivity);
            }
        });

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        makeJsonArrayRequest();
                    }
                }, 0);
            }
        });


//        ImageView mArrowImageView = (ImageView) findViewById( R.id.imageView);
//        Drawable drawable = mArrowImageView.getDrawable();
//        if (drawable instanceof Animatable) {
//            ((Animatable) drawable).start();
//        }
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
                    postList.clear();
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
                        post.setVoteType(posts.getInt("vote_type"));
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
                        ph.addPost(post, true);
                    }

                    qAdapter.notifyDataSetChanged();

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
//                Toast.makeText(getApplicationContext(),
//                        error.getMessage(), Toast.LENGTH_SHORT).show();


                // hide the progress dialog
                hidepDialog();
            }

        });


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(jsonObjReq);
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


//    private void swapView(View newView){
//        View toRemove = parent.findViewById(R.id.morph_id);
//        parent.removeView(toRemove);
//        newView.setId(R.id.morph_id);
//        parent.addView(newView, toRemove.getLayoutParams());
//    }
//
//    private View createMorphableView(int startDrawable, int endDrawable, int color){
//        MorphButton mb = new MorphButton(this);
//        mb.setForegroundTintList(ColorStateList.valueOf(color));
//        mb.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);
//        mb.setBackgroundColor(Color.TRANSPARENT);
//        mb.setStartDrawable(startDrawable);
//        mb.setEndDrawable(endDrawable);
//        mb.setState(MorphButton.MorphState.START);
//        return  mb;
//    }
}
