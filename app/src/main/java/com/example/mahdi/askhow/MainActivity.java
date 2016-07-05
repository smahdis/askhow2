package com.example.mahdi.askhow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import libs.DelayAutoCompleteTextView;
import libs.OnLoadMoreListener;
import libs.RecyclerOnScrollListener;
import libs.SessionManager;
import adapter.SuggestionAdapter;
import libs.ShowProfileDialog;
import libs.Statics;

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
    private String urlJsonArry = Statics.IP_ADDRESS + "getposts/";//1/10/-1";
    private static String TAG = MainActivity.class.getSimpleName();
    FloatingActionButton fab;
    int defaultSearchWidth = 0;

    PostHandler ph;
    private AVLoadingIndicatorView progressbar;
    private PullToRefreshView mPullToRefreshView;

    MorphButton searchBTN;

    //    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

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
//        searchBTN = (MorphButton) findViewById(R.id.search_btn);
        final RelativeLayout searchField = (RelativeLayout) findViewById(R.id.searchview_parent);


//        makeJsonArrayRequest();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(qAdapter);

//        double y = 1.6/10;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                recyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                        if (!qAdapter.isLoading1() && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            qAdapter.setLoading();
                            //Load more data for reyclerview
                            int x = (totalItemCount / 10) + 1;
                            x = Math.round((float) totalItemCount / 10f);
                            //Load More
                            makeJsonArrayRequest(x + 1, 10, false);
                        }
//                    }
//                }, 0);

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AskActivity.class);
                startActivity(intent);
            }
        });

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        qAdapter.setOnItemClickListener(new PostsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v, View postBG) {
//                Log.d(TAG, "onItemClick position: " + position);
                int post_mysql_id = qAdapter.getItem(position).getPost_mysql_id();
//                Intent answerActivity = new Intent(MainActivity.this, AnswerActivity.class);
//                answerActivity.putExtra("post_mysql_id", post_mysql_id);
//                startActivity(answerActivity);
                Intent intent = new Intent(MainActivity.this, AnswerActivity.class);
                intent.putExtra("post_mysql_id", post_mysql_id);
                Pair<View, String> p1 = Pair.create((View) postBG, "postbg");
//                Pair<View, String> p2 = Pair.create((View) holder.poster, "username");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, p1);
                ActivityCompat.startActivity(MainActivity.this, intent,
                        options.toBundle());
            }
        });

        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                        mPullToRefreshView.setRefreshing(true);
                        showpDialog();
                        qAdapter.setLoading();
                        //update >> true >> not load more
                        makeJsonArrayRequest(1, 10, true);

            }
        });

//        mPullToRefreshView.setRefreshing(true);
        showpDialog();
        qAdapter.setLoading();
        //update not load more -->> true
        makeJsonArrayRequest(1, 10, true);


        /*
            Search Related Stuff
         */

        final DelayAutoCompleteTextView searchTextView = (DelayAutoCompleteTextView) findViewById(R.id.autoComplete);
        searchTextView.setLoadingIndicator(
                (AVLoadingIndicatorView) findViewById(R.id.pb_loading_indicator));
        searchTextView.setThreshold(4);
//        searchTextView.setAutoCompleteDelay(1500);
        searchTextView.setAdapter(new SuggestionAdapter(this, searchTextView.getText().toString()));

        searchTextView.setThreshold(4);
        searchTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
        final Drawable[] searchDrawable = searchTextView.getCompoundDrawables();
        searchDrawable[0].setAlpha(128); //set it at 128 so that it has 50% opacity. The opactiy here ranges from 0 to 255.

        final Typeface font = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");
        searchTextView.setTypeface(font);

        searchTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean gotfocus) {
                // TODO Auto-generated method stub
                if (gotfocus) {
//                    searchTextView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    defaultSearchWidth = searchTextView.getWidth();
                    ObjectAnimator animWidth = ObjectAnimator.ofInt(searchTextView, "width", defaultSearchWidth, 3000);
                    animWidth.setDuration(500);
                    animWidth.start();
//                    searchTextView.setCompoundDrawables(null, null, null, null);

                } else if (!gotfocus) {
                    if (searchTextView.getText().length() == 0) {
//                        searchTextView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                        ObjectAnimator animWidth = ObjectAnimator.ofInt(searchTextView, "width", 3000, defaultSearchWidth);
                        animWidth.setDuration(500);
                        animWidth.start();
                        searchTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.search, 0, 0, 0);
                        searchDrawable[0].setAlpha(128); //set it at 128 so that it has 50% opacity. The opactiy here ranges from 0 to 255.

                    }
                }
            }
        });


//        searchBTN.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (searchField.getVisibility() == View.VISIBLE) {
//                    searchField.setVisibility(View.INVISIBLE);
////                    searchBTN.setStartDrawable(R.drawable.vector_search);
////                    searchBTN.setEndDrawable(R.drawable.vector_search);
//                    searchTextView.setText("");
//                    searchTextView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
//
//                    searchTextView.clearFocus();
//                }
//                else
//                {
////                    searchBTN.setStartDrawable(R.drawable.vector_close);
////                    searchBTN.setEndDrawable(R.drawable.vector_close);
//                    searchField.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        searchTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Post post = (Post) adapterView.getItemAtPosition(position);
                searchTextView.setText(post.getPostTitle());
            }
        });


//        myLayout.requestFocus();

        searchTextView.setOnEditorActionListener(searchListener);


    }


    EditText.OnEditorActionListener searchListener = new EditText.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Toast.makeText(MainActivity.this, v.getText().toString(), Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };

    // handle focus error with the search edit text
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * Method to make json array request where response starts with [
     */
    private void makeJsonArrayRequest(int page, int limit, final boolean updating) {

        SessionManager sm = new SessionManager(this);
//        sm.setMySQL_ID(29);;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonArry + page + "/" + limit + "/" + "-1" + "/" + sm.getKeyMysql_id(), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("error") == "1")
                        return;
                    Log.d("json", response.toString());
                    JSONArray ja = response.getJSONArray("posts");
                    Post post;
                    JSONObject posts;
//                    postList.clear();
                    for (int i = 0; i < ja.length(); i++) {
                        post = new Post();
                        posts = (JSONObject) ja.get(i);

//                                String name = person.getString("name");
//                                String email = person.getString("email");
//                        posts = postarray.getJSONObject("posts");
                        int id = posts.getInt("id");
                        String postTitle = posts.getString("postTitle");
                        String posterName = posts.getString("posterName");
                        String posterAvatar = posts.getString("posterAvatar");
                        String posterDegree = posts.getString("posterDegree");
                        int posterMysql = posts.getInt("posterMysql");
                        String postDesc = posts.getString("postDesc");
                        post.setPost_mysql_id(id);
                        post.setPosterName(posterName);
                        post.setPosterAvatar(posterAvatar);
                        post.setPosterDegree(posterDegree);
                        post.setPosterMysqlID(posterMysql);
                        post.setPostTitle(postTitle);
                        post.setPostDesc(postDesc);
                        post.setVotes(posts.getInt("votes"));
                        post.setAnswers(posts.getInt("answers"));
//                        post.setViews(posts.getInt("views"));
                        post.setVoteType(posts.getInt("vote_type"));
//                        post.setBookmarked(posts.getInt("bookmarked") != 0);
//                        post.setIsRead(posts.getInt("isread") != 0);
//                        post.setIsMine(posts.getInt("ismine") != 0);
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
                        //is it updating or loading more?
                        if (updating) {
                            if (!ph.postExists(post.getPost_mysql_id())) {
                                postList.add(post);
                            }
                        } else
                            postList.add(post);

                        ph.addPost(post, true);
                    }

                    qAdapter.notifyDataSetChanged();
                    int x = Math.round((float) qAdapter.getItemCount() / 10f);

                    Log.d("Total Items json", qAdapter.getItemCount() + " Total Items, Page: " + (x + 1));
//                            txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

                hidepDialog();
                qAdapter.setLoaded();
                mPullToRefreshView.setRefreshing(false);

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
                qAdapter.setLoaded();
                mPullToRefreshView.setRefreshing(false);

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
