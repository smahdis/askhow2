package adapter;

/**
 * Created by Mahdi on 2/7/2016.
 */


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database_handler.PostHandler;
import libs.AppController;
import libs.LetterAvatar;
import libs.MethodLibs;
import libs.OnLoadMoreListener;
import libs.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import Items.Post;
import libs.ShowProfileDialog;
import libs.Statics;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.style.QuoteSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.mahdi.askhow.AnswerActivity;
import com.example.mahdi.askhow.AskActivity;
import com.example.mahdi.askhow.ProfileViewDialogActivity;
import com.example.mahdi.askhow.R;
import com.wang.avi.AVLoadingIndicatorView;
import com.wnafee.vector.MorphButton;

public class AnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> postList;
    private TypedArray mColors;
    Context context;
    TextView tag;
    LinearLayout.LayoutParams lp;
    private static ClickListener clickListener;
    private String urlJsonArry = Statics.IP_ADDRESS + "vote/:";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private OnLoadMoreListener mOnLoadMoreListener;

    static final int VISIBLE_THRESHOLD = 5;


    private boolean isLoading;

    View view;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, desc, poster, date, votesDigit;
        public RelativeLayout postBG;
        public LinearLayout postContent;
        public ImageView avatar;
        public TextView[] tvs = new TextView[10];
        public int color;
        public int color2;
        public List<String> tags;
        public MorphButton showMore;
        public RelativeLayout profileContainer;
        public LinearLayout tags_ll;

        //        public View upVote, downVote;
        public ViewGroup parent;
        public boolean voteDown_isChecked;

        public MorphButton upVote, downVote;

        public Drawable drawable;
        AnimatorSet set;

        public PostHandler ph;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.questionTitleTV);
            desc = (TextView) view.findViewById(R.id.questionDescTV);
            poster = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.timestamp);
            avatar = (ImageView) view.findViewById(R.id.profilePic);
            postBG = (RelativeLayout) view.findViewById(R.id.post_bg);
            profileContainer = (RelativeLayout) view.findViewById(R.id.profileContainer);
            postContent = (LinearLayout) view.findViewById(R.id.postContent);
            votesDigit = (TextView) view.findViewById(R.id.voteDisplayDigit);
            tags_ll = (LinearLayout) view.findViewById(R.id.tags_ll);
            upVote = (MorphButton) view.findViewById(R.id.upvote);
            downVote = (MorphButton) view.findViewById(R.id.downvote);
            showMore = (MorphButton) view.findViewById(R.id.show_more);
            parent = (ViewGroup) view.findViewById(R.id.fabCL);
            ph = new PostHandler(context);
            voteDown_isChecked = false;

//            year = (TextView) view.findViewById(R.id.year);

            final Typeface questionTitleFont = Typeface.createFromAsset(view.getContext().getAssets(), "IRTitr.ttf");
            final Typeface questionDescFont = Typeface.createFromAsset(view.getContext().getAssets(), "IRTerafik.ttf");
            title.setTypeface(questionTitleFont);
            desc.setTypeface(questionDescFont);
            Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "Organo.ttf");
            votesDigit.setTypeface(font);

//            poster.setTypeface(font);
//            date.setTypeface(font);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            int tags_margin = (int) view.getContext().getResources().getDimension(R.dimen.tags_margin);


            for (int i = 0; i < 10; i++) {
                tag = (TextView) inflater.inflate(R.layout.tag_layout, null, false);
                params.setMargins(tags_margin, tags_margin, tags_margin, tags_margin);
                tag.setLayoutParams(params);
//                tag.setText(tags.get(i));
                tag.setTypeface(questionDescFont);
                tags_ll.addView(tag);
                tvs[i] = tag;
            }


            postContent.setOnClickListener(this);


            showMore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Drawable drawable = avatar.getDrawable();
                    if (drawable instanceof Animatable) {
                        poster.setText("Animated");
                        ((Animatable) drawable).start();
                    }

                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(context, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(context, "You Clicked : " + item.getTitle() + " " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

                    popup.show();//showing popup menu
                }
            });


        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        AnswerAdapter.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
    }


    public AnswerAdapter(List<Post> postList) {
        this.postList = postList;
    }


//    private final DialogInterface.OnClickListener mOnClickListener = new MyCustomCallBack();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.testlayout2, parent, false);
//
//        return new MyViewHolder(itemView);
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.testlayout2, parent, false);
            return new MyViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;
    }

    /*

    Load More codes
     */

    @Override
    public int getItemViewType(int position) {
//        return postList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        return (position == postList.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        if (postList == null) {
            return 0;
        }
        if (postList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }
        return postList.size()+1;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder1, int position) {
//        // you can cache getItemCount() in a member variable for more performance tuning
//        if(!isLoading && (getItemCount() <= (position + VISIBLE_THRESHOLD)) ) {
//            if(mOnLoadMoreListener != null) {
//                mOnLoadMoreListener.onLoadMore(position);
//            }
//            isLoading = true;
//        }

        if (holder1 instanceof MyViewHolder) {
            final Post post = postList.get(position);

            final MyViewHolder holder = (MyViewHolder) holder1;

            context = holder.itemView.getContext();
            
            holder.title.setText(post.getPostTitle());
            holder.desc.setText(post.getPostDesc());
            holder.desc.setMaxLines(Integer.MAX_VALUE);
            holder.desc.setEllipsize(null);
            holder.poster.setText(post.getPosterName() + " - " + post.getDate());
            holder.date.setText(post.getDate() + " - " + post.getTime() );
            holder.votesDigit.setText(post.getVotes() + "");

            holder.title.setTextColor(Color.WHITE);
            holder.desc.setTextColor(Color.WHITE);
            holder.poster.setTextColor(Color.WHITE);
            holder.date.setTextColor(Color.WHITE);


            holder.tags = Arrays.asList(post.getTags().split(","));


            final Resources res = context.getResources();
            final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);
            final LetterAvatar tileProvider = new LetterAvatar(context);
            Bitmap letterTile;// = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName(), tileSize, tileSize);

            if (Math.abs(postList.get(0).getPost_mysql_id()) % 8 == Math.abs(post.getPosterName().hashCode()) % 8) {
                letterTile = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName() + post.getPost_mysql_id(), tileSize, tileSize);
            } else
                letterTile = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName(), tileSize, tileSize);


            holder.avatar.setImageBitmap(MethodLibs.getCircularBitmap(letterTile));


            mColors = context.getResources().obtainTypedArray(R.array.background_colors);
            TypedArray tColors = context.getResources().obtainTypedArray(R.array.background_colors);

            holder.color = Math.abs(postList.get(0).getPost_mysql_id()) % Statics.number_of_colors;
            holder.postBG.setBackgroundColor(tColors.getColor(holder.color, Color.BLACK));//pickColor(String.valueOf(post.getPost_mysql_id())));

            if(position!=0) {
                holder.tags_ll.setVisibility(View.GONE);
                holder.title.setVisibility(View.INVISIBLE);
//                holder.postBG.setBackgroundColor(Color.TRANSPARENT);
                holder.postBG.setTransitionName("");
            }
            else {
                holder.tags_ll.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.VISIBLE);
//                holder.postBG.setBackgroundColor(tColors.getColor(holder.color, Color.BLACK));//pickColor(String.valueOf(post.getPost_mysql_id())));
                holder.postBG.setTransitionName("postbg");

            }
            //10 = max number of tags
            for (int i = 0; i < 10; i++) {
                if (i >= holder.tags.size())
                    holder.tvs[i].setVisibility(View.GONE);
                else {
                    holder.tvs[i].setVisibility(View.VISIBLE);
                    holder.tvs[i].setText(holder.tags.get(i));
//                holder.color2 = (Math.abs(holder.tags.get(i).hashCode())) % 8;
                    if (Math.abs(post.getPost_mysql_id()) % 8 == (Math.abs(holder.tags.get(i).hashCode())) % 8) {
                        holder.color = (Math.abs(holder.tags.get(i).hashCode()) + 1) % 8;
                    } else
                        holder.color = Math.abs(holder.tags.get(i).hashCode()) % 8;

                    holder.tvs[i].setBackgroundColor(mColors.getColor(holder.color, Color.BLACK));
                }
            }

            holder.upVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.upVote.setEnabled(false);
                    holder.downVote.setEnabled(false);
                    if (post.getVoteType() != 1)
                        makeNetworkCall(true, post, 1, holder);
                    else
                        makeNetworkCall(true, post, 0, holder);
                }
            });

            holder.downVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.upVote.setEnabled(false);
                    holder.downVote.setEnabled(false);
                    if (post.getVoteType() != -1)
                        makeNetworkCall(false, post, -1, holder);
                    else
                        makeNetworkCall(false, post, 0, holder);
                }
            });


            if (post.getVoteType() == 1) {
                holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#EEE333")));
                holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            } else {
                if (post.getVoteType() == -1) {
                    holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#EEE333")));
                    holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                } else {
                    holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                }

            }
            holder.upVote.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);
            holder.downVote.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);

            holder.profileContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileViewDialogActivity.class);
                    intent.putExtra("post_mysql_id", post.getPost_mysql_id());
                    intent.putExtra("poster_mysql_id", post.getPosterMysqlID());
                    Pair<View, String> p1 = Pair.create((View) holder.avatar, "profile");
                    Pair<View, String> p2 = Pair.create((View) holder.poster, "username");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, p1, p2);
//                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, (View)holder.profileContainer, "profile");
                    ActivityCompat.startActivity((Activity) context, intent,
                            options.toBundle());

                }
            });
        } else if (holder1 instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder1;
            context = loadingViewHolder.itemView.getContext();

            Post post1 = postList.get(0);
            TypedArray tColors = context.getResources().obtainTypedArray(R.array.background_colors);
            loadingViewHolder.color = Math.abs(post1.getPost_mysql_id()) % 8;
            loadingViewHolder.loadMoreContainer.setBackgroundColor(tColors.getColor(loadingViewHolder.color, Color.BLACK));//pickColor(String.valueOf(post.getPost_mysql_id())));

            if(isLoading)
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
            else
                loadingViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    public Post getItem(int position) {
        return postList.get(position);
    }



    public void makeNetworkCall(final boolean upvote, final Post post, final int vote_type, final MyViewHolder holder) {
        final SessionManager sm = new SessionManager(context);
        StringRequest sr = new StringRequest(Request.Method.PUT, urlJsonArry + post.getPost_mysql_id(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(upvote)
                    upVote(holder, post, vote_type, response);
                else
                    downVote(holder, post, vote_type, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error.respsonse", "Error: " + error.getMessage());
                Log.d("error", "" + error.getMessage() + "," + error.toString());
                if(upvote)
                    holder.upVote.setState(MorphButton.MorphState.START, true);
                else
                    holder.downVote.setState(MorphButton.MorphState.START, true);
                holder.upVote.setEnabled(true);
                holder.downVote.setEnabled(true);

            }
        }){



            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<> ();
                params.put("post_id", String.valueOf(post.getPost_mysql_id()));
                params.put("vote_type", String.valueOf(vote_type));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Accept", "application/json");
                headers.put("Authorization", sm.getToken());
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(sr);
    }


    private void upVote(final MyViewHolder holder,Post post, int vote_type, String response)
    {
        Log.d("response ok", response.toString());
        try {
            JSONObject json = new JSONObject(response);
            String Error = json.getString("error");
            if(Error.equals("false"))
            {
                int votes = json.getInt("votes");
                post.setVoteType(vote_type);
                post.setVotes(votes);
                holder.votesDigit.setText(String.valueOf(votes));
                Log.d("votes", holder.votesDigit.getText().toString() + " - " + votes);
                holder.ph.updateVoteType(post.getPost_mysql_id(), vote_type, votes);
                if(vote_type!=0) {
                    holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#EEE333")));
                    holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    holder.downVote.invalidate();

                }
                else
                {
                    holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                }

//                        holder.upVote.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);
//                        holder.downVote.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);

                holder.upVote.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.upVote.setState(MorphButton.MorphState.START, true);
                        holder.upVote.setEnabled(true);
                        holder.downVote.setEnabled(true);
                    }
                }, 2000);

//                        upVote(holder, vote_type);
            }
            else
            {
                holder.upVote.setState(MorphButton.MorphState.START, true);
                holder.upVote.setEnabled(true);
                holder.downVote.setEnabled(true);
            }




        } catch (JSONException e) {
            e.printStackTrace();
            holder.upVote.setState(MorphButton.MorphState.START, true);
            holder.upVote.setEnabled(true);
            holder.downVote.setEnabled(true);
        }
    }


    private void downVote(final MyViewHolder holder,Post post, int vote_type, String response)
    {
        Log.d("response ok", response.toString());
        try {
            JSONObject json = new JSONObject(response);
            String Error = json.getString("error");
            if(Error.equals("false"))
            {
                int votes = json.getInt("votes");
                post.setVoteType(vote_type);
                post.setVotes(votes);
                holder.votesDigit.setText(votes + "");
                holder.ph.updateVoteType(post.getPost_mysql_id(), vote_type, votes);
                if(vote_type!=0) {
                    holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#EEE333")));
                    holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    holder.upVote.invalidate();

                }
                else
                {
                    holder.downVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                    holder.upVote.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                }

//                        holder.downVote.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);
//                        holder.upVote.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);

                holder.downVote.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.downVote.setState(MorphButton.MorphState.START, true);
                        holder.downVote.setEnabled(true);
                        holder.upVote.setEnabled(true);
                    }
                }, 2000);

//                        downVote(holder, vote_type);
            }
            else
            {
                holder.downVote.setState(MorphButton.MorphState.START, true);
                holder.downVote.setEnabled(true);
                holder.upVote.setEnabled(true);
            }




        } catch (JSONException e) {
            e.printStackTrace();
            holder.downVote.setState(MorphButton.MorphState.START, true);
            holder.downVote.setEnabled(true);
            holder.upVote.setEnabled(true);
        }
    }








    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public AVLoadingIndicatorView progressBar;
        public LinearLayout loadMoreContainer;
        public int color;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (AVLoadingIndicatorView) itemView.findViewById(R.id.progressBar1);
            loadMoreContainer = (LinearLayout) itemView.findViewById(R.id.loadMoreContainer);
        }
    }

    public void setLoaded() {
        isLoading = false;
        LoadingViewHolder lvh = null;
        if(view!=null)
            lvh = new LoadingViewHolder(view);
        if(lvh!=null && lvh.progressBar!=null)
            lvh.progressBar.setVisibility(View.GONE);
    }

    public void setLoading() {
        isLoading = true;

    }

    public boolean isLoading() {
        return isLoading;

    }

}

