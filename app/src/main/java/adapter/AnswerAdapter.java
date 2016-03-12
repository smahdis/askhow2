package adapter;

/**
 * Created by Mahdi on 2/7/2016.
 */


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahdi.askhow.AnswerActivity;
import com.example.mahdi.askhow.MainActivity;
import com.example.mahdi.askhow.R;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import Items.Post;
import libs.LetterAvatar;
import libs.MethodLibs;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyViewHolder>  {

    private List<Post> postList;
    private TypedArray mColors;
    Context context;
    public LinearLayout tags_ll;
    TextView tag;
    LinearLayout.LayoutParams lp;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, desc, poster, date, votesDigit;
        public RelativeLayout postBG;
        public ImageView avatar;
        public TextView[] tvs = new TextView[10];
        public int color;
        public int color2;
        public List<String> tags;
        public ImageButton upVote, downVote, showMore;

        public int position;

        public MyViewHolder(View view) {
            super(view);
            Post post = postList.get(position);

            title = (TextView) view.findViewById(R.id.questionTitleTV);
            desc = (TextView) view.findViewById(R.id.questionDescTV);
            poster = (TextView) view.findViewById(R.id.name);
            date = (TextView) view.findViewById(R.id.timestamp);
            avatar = (ImageView) view.findViewById(R.id.profilePic);
            postBG = (RelativeLayout) view.findViewById(R.id.post_bg);
            votesDigit = (TextView) view.findViewById(R.id.voteDisplayDigit);
            tags_ll = (LinearLayout) view.findViewById(R.id.tags_ll);
            upVote = (ImageButton) view.findViewById(R.id.upvote);
            downVote = (ImageButton) view.findViewById(R.id.downvote);
            showMore = (ImageButton) view.findViewById(R.id.show_more);

//            year = (TextView) view.findViewById(R.id.year);

            final Typeface questionTitleFont = Typeface.createFromAsset(view.getContext().getAssets(), "IRTitr.ttf");
            final Typeface questionDescFont = Typeface.createFromAsset(view.getContext().getAssets(), "IRTerafik.ttf");
            title.setTypeface(questionTitleFont);
            desc.setTypeface(questionDescFont);
            Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "Jersey M54.ttf");
            votesDigit.setTypeface(font);

//            poster.setTypeface(font);
//            date.setTypeface(font);

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            LayoutInflater inflater = LayoutInflater.from(view.getContext());
//            int tags_margin = (int)view.getContext().getResources().getDimension(R.dimen.tags_margin);
//
//
//            for (int i = 0; i < 10; i++) {
//                tag = (TextView) inflater.inflate(R.layout.tag_layout, null, false);
//                params.setMargins(tags_margin, tags_margin, tags_margin, tags_margin);
//                tag.setLayoutParams(params);
////                tag.setText(tags.get(i));
//                tag.setTypeface(questionDescFont);
//                tags_ll.addView(tag);
//                tvs[i] = tag;
//            }


            upVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = postList.get(getAdapterPosition());
                    Toast.makeText(context, post.getVotes() + "", Toast.LENGTH_LONG).show();
                }
            });



            showMore.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
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
            });//closing the setOnClickListener method
        }

    }


    public AnswerAdapter(List<Post> postList) {
        this.postList = postList;
    }


//    private final DialogInterface.OnClickListener mOnClickListener = new MyOnClickListener();

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.testlayout2, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Post post = postList.get(position);
        context = holder.itemView.getContext();

        holder.title.setText(post.getPostTitle());

        holder.desc.setText(post.getPostDesc());
        holder.poster.setText(post.getPosterName() + " - " + post.getDate());
        holder.date.setText(post.getAnswers() + " answers - " + post.getViews() + " views");
        holder.votesDigit.setText(post.getVotes() + " ");

        holder.title.setTextColor(Color.WHITE);
        holder.desc.setTextColor(Color.WHITE);
        holder.poster.setTextColor(Color.WHITE);
        holder.date.setTextColor(Color.WHITE);


        holder.tags = Arrays.asList(post.getTags().split(","));


        final Resources res = context.getResources();
        final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);
        final LetterAvatar tileProvider = new LetterAvatar(context);
        Bitmap letterTile;// = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName(), tileSize, tileSize);

        if(post.isQuestion())
            holder.title.setVisibility(View.VISIBLE);
        else
            holder.title.setVisibility(View.GONE);

        if(Math.abs(post.getPost_mysql_id()) % 8 == Math.abs(post.getPosterName().hashCode()) % 8)
        {
            letterTile = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName() + post.getPost_mysql_id(), tileSize, tileSize);
        }
        else
            letterTile = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName(), tileSize, tileSize);


        holder.avatar.setImageBitmap(MethodLibs.getCircularBitmap(letterTile));


        mColors = context.getResources().obtainTypedArray(R.array.background_colors);
        TypedArray tColors = context.getResources().obtainTypedArray(R.array.background_colors);

//        final Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
//        final Canvas c = new Canvas();
//        c.setBitmap(bitmap);

        holder.color = Math.abs(post.getPost_mysql_id()) % 8;
        holder.postBG.setBackgroundColor(tColors.getColor(holder.color, Color.BLACK));//pickColor(String.valueOf(post.getPost_mysql_id())));


//        for (int i = 0; i < 10; i++)
//        {
//            if(i >= holder.tags.size())
//                holder.tvs[i].setVisibility(View.GONE);
//            else{
//                holder.tvs[i].setVisibility(View.VISIBLE);
//                holder.tvs[i].setText(holder.tags.get(i));
////                holder.color2 = (Math.abs(holder.tags.get(i).hashCode())) % 8;
//                if(Math.abs(post.getPost_mysql_id()) % 17 == (Math.abs(holder.tags.get(i).hashCode())) % 8)
//                {
//                    holder.color = (Math.abs(holder.tags.get(i).hashCode()) + 1) % 8;
//                }
//                else
//                    holder.color = Math.abs(holder.tags.get(i).hashCode()) % 8;
//
//                holder.tvs[i].setBackgroundColor(mColors.getColor(holder.color, Color.BLACK));
//            }
//        }

//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.main, null);



//        final int tileSize = res.getDimensionPixelSize(R.dimen.letter_tile_size);
//
//        final LetterTileProvider tileProvider = new LetterTileProvider(holder.itemView.getContext());
//        final Bitmap letterTile = tileProvider.getLetterTile(post.getPostTitle(), post.getPostDesc(), 20, 20);
//        holder.postBG.setBackground(new BitmapDrawable(res, letterTile));
//        holder.avatar.setImageBitmap(letterTile);

//        getActionBar().setIcon(new BitmapDrawable(getResources(), letterTile));

//        holder.year.setText(post.getYear());
    }

    private int pickColor(String key) {
        // String.hashCode() is not supposed to change across java versions, so
        // this should guarantee the same key always maps to the same color
        final int color = Math.abs(key.hashCode()) % 8;
        try {
            return mColors.getColor(color, Color.BLACK);
        } finally {
            mColors.recycle();
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public Post getItem(int position) {
        return postList.get(position);
    }
}

