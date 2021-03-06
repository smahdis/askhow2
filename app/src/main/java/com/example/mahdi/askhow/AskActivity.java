package com.example.mahdi.askhow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.wnafee.vector.MorphButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Items.Post;
import libs.AppController;
import libs.MethodLibs;
import libs.SessionManager;
import libs.Statics;
import libs.UsernameValidator;

/**
 * Created by Mahdi on 3/12/2016.
 */
public class AskActivity extends AppCompatActivity{


    EditText postTitle;
    EditText postContent;
    EditText postTags;
    MorphButton send;
    MorphButton quote;
    MorphButton link;
    MorphButton image;

    AVLoadingIndicatorView progress;

    CoordinatorLayout cl;
    Typeface font;

    Context context;

    ArrayList<TextView> tvs = new ArrayList<>();
    //    ArrayList<String> images = new ArrayList<>();
    ArrayList<HashMap<String, Integer>> images = new ArrayList<>();
    LinearLayout tags_ll;
    LinearLayout images_ll;

    private static final int SELECT_PICTURE = 100;

    private String urlJsonArry = Statics.IP_ADDRESS + "post:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        postTitle = (EditText)findViewById(R.id.postTitle);
        postContent = (EditText)findViewById(R.id.postContent);
        postTags = (EditText)findViewById(R.id.postTags);
        send = (MorphButton) findViewById(R.id.send_btn);
        cl = (CoordinatorLayout) findViewById(R.id.root);
        tags_ll = (LinearLayout) findViewById(R.id.tags_ll);
        images_ll = (LinearLayout) findViewById(R.id.images_parent);

        quote = (MorphButton) findViewById(R.id.quote_btn);
        image = (MorphButton) findViewById(R.id.image_btn);
        link = (MorphButton) findViewById(R.id.link_btn);

        progress = (AVLoadingIndicatorView)findViewById(R.id.avloadingIndicatorView);

        quote.setOnClickListener(quoteClickListener);
        link.setOnClickListener(linkClickListener);
        send.setOnClickListener(submitPostListener);
        image.setOnClickListener(imageClickListener);

        font = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");

        postTitle.setTypeface(font);
        postContent.setTypeface(font);
        postTags.setTypeface(font);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        postTags.addTextChangedListener(postTitleTW);
        postTags.setOnKeyListener(new View.OnKeyListener() {


            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && postTags.getText().length()==0 && tvs.size() > 0 && event.getAction() == KeyEvent.ACTION_UP) {
                    postTags.setText(tvs.get(tvs.size() - 1).getText());
                    postTags.setSelection(postTags.getText().length());
                    tags_ll.removeView(tvs.get(tvs.size() - 1));
                    tvs.remove(tvs.size() - 1);
                    if(tvs.size()>0)
                        postTags.setHint("کلمات کلیدی");
                    else
                        postTags.setHint("");
                }
                return false;
            }

        });

    }

    View.OnClickListener submitPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(postTitle.getText().toString().equals("")
                    || postTitle.getText().toString().length()<=0
                    || postContent.getText().toString().length()<=0
                    )
            {
                Snackbar snackbar = Snackbar.make(cl, "یه جای کار می لنگه...!", Snackbar.LENGTH_SHORT);
                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
                tv.setTypeface(font);
                snackbar.show();

                return;
            }

            post();


        }

    };


    /*
        Quote onclicklistener
     */

    View.OnClickListener quoteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(postContent.hasSelection())
            {
                String strToQuote = postContent.getText().toString().substring(postContent.getSelectionStart(),postContent.getSelectionEnd());
                //postContent.getText().replace()//(postContent.getSelectionStart(),postContent.getSelectionEnd(), "");
                String quote = "[quote]" + strToQuote + "[/quote] \n";
                int start = Math.max(postContent.getSelectionStart(), 0);
                int end = Math.max(postContent.getSelectionEnd(), 0);
                postContent.getText().replace(Math.min(start, end), Math.max(start, end),
                        quote, 0, quote.length());

            }
            else {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_editor);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                final EditText edt1 = (EditText) dialog.findViewById(R.id.dialogEditorEditText1);
                final EditText edt2 = (EditText) dialog.findViewById(R.id.dialogEditorEditText2);
                edt2.setVisibility(View.GONE);
                Button submit = (Button) dialog.findViewById(R.id.btn_submit_quote);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String quote = "[quote]";
                        if (edt1.getText().toString().length() <= 0)
                            return;
                        if (edt2.getText().toString().length() != 0)
                            quote = "[quote = " + edt2.getText().toString() + "]";
                        if (postContent.getText().toString().length() != 0)
                            postContent.append("\n");
                        postContent.append(quote + edt1.getText().toString() + "[/quote]");
                        postContent.append("\n");
                        dialog.dismiss();
                    }
                });
                edt1.setTypeface(font);
                edt2.setTypeface(font);

                dialog.show();
            }
        }
    };



    /*
        Quote onclicklistener
     */

    View.OnClickListener linkClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_editor);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            final EditText edt1 = (EditText) dialog.findViewById(R.id.dialogEditorEditText1);
            final EditText edt2 = (EditText) dialog.findViewById(R.id.dialogEditorEditText2);
            final TextView Title = (TextView) dialog.findViewById(R.id.dialogTitle);
            final MorphButton icon = (MorphButton) dialog.findViewById(R.id.dialogTitleIcon);
            Button submit = (Button) dialog.findViewById(R.id.btn_submit_quote);

            edt1.setHint("آدرس لینک");
            edt2.setHint("توضیحات لینک (اختیاری)");
            Title.setText("لینک");
            icon.setStartDrawable(R.drawable.vector_link);
            icon.setEndDrawable(R.drawable.vector_link);

            if(postContent.hasSelection())
            {
                String strToLink = postContent.getText().toString().substring(postContent.getSelectionStart(),postContent.getSelectionEnd());
                final int start = Math.max(postContent.getSelectionStart(), 0);
                final int end = Math.max(postContent.getSelectionEnd(), 0);
                edt2.setText(strToLink);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edt1.getText().toString().length() <= 0)
                            return;
                        if (!edt1.getText().toString().contains("."))
                            return;

                        String link = "[link=" + edt1.getText().toString() + "]";
                        if (edt2.getText().toString().length() != 0)
                            link = link + edt2.getText().toString() + "[/link]";
                        postContent.getText().replace(Math.min(start, end), Math.max(start, end),
                                link, 0, link.length());


                        dialog.dismiss();
                    }
                });
            }
            else {
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edt1.getText().toString().length() <= 0)
                            return;
                        if (!edt1.getText().toString().contains("."))
                            return;

                        String link = "[link=" + edt1.getText().toString() + "]";
                        if (edt2.getText().toString().length() != 0)
                            link = link + edt2.getText().toString() + "[/link]";
                        postContent.append(link);
                        dialog.dismiss();
                    }
                });
            }

            edt1.setTypeface(font);
            edt2.setTypeface(font);

            dialog.show();

        }
    };



        /*
        Image onclicklistener
     */

    View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.dialog_editor);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            final EditText edt1 = (EditText) dialog.findViewById(R.id.dialogEditorEditText1);
            final EditText edt2 = (EditText) dialog.findViewById(R.id.dialogEditorEditText2);
            edt2.setVisibility(View.GONE);
            final Button submit = (Button) dialog.findViewById(R.id.btn_submit_quote);

            final TextView Title = (TextView) dialog.findViewById(R.id.dialogTitle);
            final MorphButton icon = (MorphButton) dialog.findViewById(R.id.dialogTitleIcon);

            edt1.setHint("آدرس تصویر در اینترنت");
            submit.setText("انتخاب تصویر از گالری");
            Title.setText("قرار دادن تصویر");
            icon.setStartDrawable(R.drawable.vector_image);
            icon.setEndDrawable(R.drawable.vector_image);
            edt1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.length()>0)
                        submit.setText("تایید");
                    else
                        submit.setText("انتخاب تصویر از گالری");
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(edt1.getText().length()>0)) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_PICTURE);
                    }
                    else
                    {

                    }
                    dialog.dismiss();

                }
            });
            edt1.setTypeface(font);
            edt2.setTypeface(font);
            submit.setTypeface(font);
            dialog.show();
        }
    };


    /*
        Tags related methods
     */
    TextWatcher postTitleTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.d("tags", s.toString());
            if((s.toString().endsWith(" ") || s.toString().endsWith(",") || s.toString().endsWith("،") || s.toString().endsWith("؛")))
            {
                if(s.toString().length()>4) {

                    RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

//                params.addRule(RelativeLayout.CENTER_VERTICAL);
//
//                if(tvs.size()>0)
//                {
                    params1.addRule(RelativeLayout.LEFT_OF, tags_ll.getId());
//                    Log.d("id", tvs.get(tvs.size()-1).getId()+"");
//                }
//                else
//                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                    LayoutInflater inflater = LayoutInflater.from(context);
                    int tags_margin = (int) context.getResources().getDimension(R.dimen.tags_margin);

                    TextView tag = (TextView) inflater.inflate(R.layout.tag_layout, null, false);
                    params.setMargins(tags_margin, tags_margin, tags_margin, tags_margin);
                    tag.setLayoutParams(params);
                    tag.setText(s.toString().substring(0, s.toString().length() - 1));
                    tag.setTypeface(font);
                    tag.setTextSize((int) context.getResources().getDimension(R.dimen.tags_font_size));
                    tags_ll.addView(tag);
                    postTags.setLayoutParams(params1);
                    TypedArray mColors = context.getResources().obtainTypedArray(R.array.background_colors);
                    int color = (Math.abs(tag.getText().toString().hashCode()) + 1) % 8;
                    tag.setBackgroundColor(mColors.getColor(color, Color.BLACK));
                    postTags.setText("");
                    tag.setId(tvs.size());

                    tag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_small, 0, 0, 0);
                    tag.setCompoundDrawablePadding(10);
                    Log.d("set id", tvs.size() + "");
                    tag.setOnClickListener(tag_click_listener);
                    tvs.add(tag);
                }
                else
                {
                    postTags.getText().delete(postTags.getText().toString().length() - 1, postTags.getText().toString().length());
                }
            }



        }


        @Override
        public void afterTextChanged(Editable s) {

            if(tvs.size()>0)
                postTags.setHint("");
            else
                postTags.setHint("کلمات کلیدی");
        }
    };

    View.OnClickListener tag_click_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            tags_ll.removeView(v);
            tvs.remove(tvs.indexOf(v));
            if(tvs.size()>0)
                postTags.setHint("");
            else
                postTags.setHint("کلمات کلیدی");
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LayoutInflater inflater = LayoutInflater.from(context);

                int preview_size = (int) context.getResources().getDimension(R.dimen.postImagePreviewSize);
                int margin = (int) context.getResources().getDimension(R.dimen.tags_margin);
                params.setMargins(margin, margin, margin, margin);

                final RelativeLayout rl = new RelativeLayout(this);
                rl.setLayoutParams(params);
                rl.setBackgroundColor(Color.parseColor("#FF514096"));
                rl.setPadding(margin,margin,margin,margin);
                images_ll.addView(rl);

                final Bitmap bitmap = BitmapFactory.decodeFile(getPath(selectedImageUri));
                Bitmap bmp = MethodLibs.scaleCenterCrop(bitmap, preview_size,preview_size);


                final ImageView image = (ImageView) inflater.inflate(R.layout.post_image_layout, null, false);
                image.setImageBitmap(bmp);
                image.setLayoutParams(params);
                image.setId(MethodLibs.generateViewId());
                rl.addView(image);

                final MorphButton mb = new MorphButton(context);
                mb.setForegroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                mb.setForegroundTintMode(PorterDuff.Mode.MULTIPLY);
                mb.setBackgroundColor(Color.TRANSPARENT);
                mb.setStartDrawable(R.drawable.vector_close);
                mb.setEndDrawable(R.drawable.vector_close);
                mb.setState(MorphButton.MorphState.END,true);
                rl.addView(mb);
                mb.setVisibility(View.GONE);
//                mb.setAlpha(0.0f);
                final TextView tv = new TextView(this);

                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mb.getVisibility()==View.GONE)
                        {
                            AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.2f);
                            animation1.setDuration(500);
                            animation1.setFillAfter(true);
                            image.startAnimation(animation1);

                            AlphaAnimation animation2 = new AlphaAnimation(0.0f, 1.0f);
                            animation2.setDuration(500);
                            mb.startAnimation(animation2);
                            animation2.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    mb.setVisibility(View.VISIBLE);

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
//                                    mb.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                            mb.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fadeoutImageTransition(image, mb);
                                }
                            },2000);

                        }
                        else
                        {
                            String text = tv.getText().toString();
                            String str = text.substring(text.indexOf("[")+1, text.indexOf("]"));
                            int index = Integer.parseInt(str);
                            Log.d("index", index + "");
                            images.remove(index-1);
                            images_ll.removeView(rl);

                        }
                    }
                });


                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(preview_size/2, preview_size/2);//(RelativeLayout.LayoutParams)mb.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                mb.setLayoutParams(layoutParams);



                tv.setTypeface(font);
                layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                layoutParams.addRule(RelativeLayout.BELOW, image.getId());
                layoutParams.setMargins(0, margin, 0, 0);
                tv.setLayoutParams(layoutParams);
                tv.setText("[" + (images.size() + 1) + "]");
                tv.setTextColor(Color.parseColor("#ffffff"));
                rl.addView(tv);


                // Add the selected image to list of images for later use
                HashMap<String, Integer> map = new HashMap<>();
                map.put(getPath(selectedImageUri), images.size()+1);
                images.add(map);

//                SpannableString ss = new SpannableString("abc\n");
//                Drawable d = image.getDrawable();
//                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
//                ss.setSpan(span, 0, 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                postContent.append(ss);

                postContent.getText().insert(postContent.getSelectionStart(), "[" + images.size() + "]");

            }
        }
    }

    void fadeoutImageTransition(ImageView image, final MorphButton mb)
    {
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 1.0f);
        animation1.setDuration(500);
        animation1.setFillAfter(true);
        image.startAnimation(animation1);

        AlphaAnimation animation2 = new AlphaAnimation(1.0f, 0.0f);
        animation2.setDuration(500);
        mb.startAnimation(animation2);
        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mb.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private String getPath(Uri uri) {
        String[] data = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, uri, data, null,
                null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public void post() {
        progress.setVisibility(View.VISIBLE);
        send.setOnClickListener(null);
        final SessionManager sm = new SessionManager(context);
        StringRequest sr = new StringRequest(Request.Method.PUT, urlJsonArry, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.setVisibility(View.GONE);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error.respsonse", "Error: " + error.getMessage());
                Log.d("error", "" + error.getMessage() + "," + error.toString());
                send.setOnClickListener(submitPostListener);
                progress.setVisibility(View.GONE);
            }
        }){



            @Override
            protected Map<String, String> getParams()
            {
                String tags = "";

                for (int i = 0; i < tvs.size(); i++)
                {
                    tags = tags + tvs.get(i).getText().toString() + ",";
                }
                tags = tags.substring(0, tags.lastIndexOf(","));

                Map<String, String>  params = new HashMap<> ();
                params.put("post_id", "-1");
                params.put("post_title", postTitle.getText().toString());
                params.put("post_content", postContent.getText().toString());
                params.put("post_tags", tags);
                params.put("is_question", "1");
                params.put("question_id", "-1");
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

}
