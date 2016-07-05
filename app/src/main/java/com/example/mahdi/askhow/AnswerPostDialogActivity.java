package com.example.mahdi.askhow;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;
import com.wnafee.vector.MorphButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Items.Post;
import Items.User;
import database_handler.AutoSaveHandler;
import database_handler.PostHandler;
import libs.AppController;
import libs.LetterAvatar;
import libs.MethodLibs;
import libs.SessionManager;
import libs.Statics;

/**
 * Created by Mahdi on 6/17/2016.
 */
public class AnswerPostDialogActivity extends AppCompatActivity {

    ImageView avatar;

    TextView usernameTitle;

    TextView number_of_questions_txt;
    TextView number_of_questions_value;
    TextView number_of_answers_txt;
    TextView number_of_answers_value;
    TextView number_of_scores_txt;
    TextView number_of_scores_values;
    TextView degree_txt;
    TextView degree_value;

    Button submiButton;

    RelativeLayout parent;

    EditText postTitle;
    EditText postContent;
    MorphButton send;
    MorphButton quote;
    MorphButton link;
    MorphButton image;

    AVLoadingIndicatorView progress;

    ArrayList<HashMap<Integer, String>> images = new ArrayList<>();


    Typeface font;

    LinearLayout images_ll;

    private static final int SELECT_PICTURE = 100;

    private String urlJsonArry = Statics.IP_ADDRESS + "post";

    Context context;

    AutoSaveHandler ASD;

    int post_mysql_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.answer_write_item);
        this.setFinishOnTouchOutside(true);

        context = this;

        parent = (RelativeLayout) findViewById(R.id.answer_post_container);

        final Activity context = this;
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
//                ActivityCompat.finishAfterTransition(context);
            }
        });

        ASD = new AutoSaveHandler(context);

//        postTitle = (EditText)findViewById(R.id.postTitle);
        postContent = (EditText) findViewById(R.id.postContent);
        send = (MorphButton) findViewById(R.id.send_btn);
        images_ll = (LinearLayout) findViewById(R.id.images_parent);

        quote = (MorphButton) findViewById(R.id.quote_btn);
        image = (MorphButton) findViewById(R.id.image_btn);
        link = (MorphButton) findViewById(R.id.link_btn);

        progress = (AVLoadingIndicatorView) findViewById(R.id.avloadingIndicatorView);

        quote.setOnClickListener(quoteClickListener);
        link.setOnClickListener(linkClickListener);
        send.setOnClickListener(submitPostListener);
        image.setOnClickListener(imageClickListener);

        font = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");

//        postTitle.setTypeface(font);
        postContent.setTypeface(font);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            if (extras.getInt("post_mysql_id") != 0) {
                post_mysql_id = extras.getInt("post_mysql_id");
            }


        Post post = ASD.getPost(post_mysql_id);

        postContent.setText(post.getPostDesc());

        if (!post.getReserved1().isEmpty() && post.getReserved1() != null) {
            Log.d("reserved", post.getReserved1() + "- res");
            String[] images_csv = post.getReserved1().split(",");
            for (int i = 0; i < images_csv.length; i++) {
                Log.d("Path", images_csv[i]);
                addImage(images_csv[i], true);

            }
        }
    }


    View.OnClickListener submitPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (postContent.getText().toString().length() <= 0) {
//                Snackbar snackbar = Snackbar.make(cl, "یه جای کار می لنگه...!", Snackbar.LENGTH_SHORT);
//                TextView tv = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
//                tv.setTypeface(font);
//                snackbar.show();

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

            if (postContent.hasSelection()) {
                String strToQuote = postContent.getText().toString().substring(postContent.getSelectionStart(), postContent.getSelectionEnd());
                //postContent.getText().replace()//(postContent.getSelectionStart(),postContent.getSelectionEnd(), "");
                String quote = "[quote]" + strToQuote + "[/quote] \n";
                int start = Math.max(postContent.getSelectionStart(), 0);
                int end = Math.max(postContent.getSelectionEnd(), 0);
                postContent.getText().replace(Math.min(start, end), Math.max(start, end),
                        quote, 0, quote.length());

            } else {
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

            if (postContent.hasSelection()) {
                String strToLink = postContent.getText().toString().substring(postContent.getSelectionStart(), postContent.getSelectionEnd());
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
            } else {
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
                    if (s.length() > 0)
                        submit.setText("تایید");
                    else
                        submit.setText("انتخاب تصویر از گالری");
                }
            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(edt1.getText().length() > 0)) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                SELECT_PICTURE);
                    } else {

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Log.d("path from result", getPath(selectedImageUri));

                addImage(getPath(selectedImageUri), false);

            }
        }
    }

    //check to see if it loaded from startup or not, if from startup, then do not add [imagecode]
    public void addImage(String selectedImageUri, boolean load_from_startup) {
        try {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LayoutInflater inflater = LayoutInflater.from(context);

            int preview_size = (int) context.getResources().getDimension(R.dimen.postImagePreviewSize);
            int margin = (int) context.getResources().getDimension(R.dimen.tags_margin);
            params.setMargins(margin, margin, margin, margin);

            final RelativeLayout rl = new RelativeLayout(this);
            rl.setLayoutParams(params);
            rl.setBackgroundColor(Color.parseColor("#FF514096"));
            rl.setPadding(margin, margin, margin, margin);
            images_ll.addView(rl);

            final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri);//getPath(selectedImageUri));
            Bitmap bmp = MethodLibs.scaleCenterCrop(bitmap, preview_size, preview_size);


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
            mb.setState(MorphButton.MorphState.END, true);
            rl.addView(mb);
            mb.setVisibility(View.GONE);
//                mb.setAlpha(0.0f);
            final TextView tv = new TextView(this);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mb.getVisibility() == View.GONE) {
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
                        }, 2000);

                    } else {
                        String text = tv.getText().toString();
                        String str = text.substring(text.indexOf("[") + 1, text.indexOf("]"));
                        int index = Integer.parseInt(str);
                        Log.d("index", index + "");
                        images.remove(index - 1);
                        images_ll.removeView(rl);

                    }
                }
            });


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(preview_size / 2, preview_size / 2);//(RelativeLayout.LayoutParams)mb.getLayoutParams();
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
            HashMap<Integer, String> map = new HashMap<>();
            map.put(images.size() + 1, selectedImageUri);
            images.add(map);

            if (!load_from_startup)
                postContent.getText().insert(postContent.getSelectionStart(), "[" + images.size() + "]");

        } catch(NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }

    void fadeoutImageTransition(ImageView image, final MorphButton mb) {
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
        String[] data = {MediaStore.Images.Media.DATA};
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
        }) {


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("post_id", "-1");// do not update but insert a new answer
                params.put("post_title", "");
                params.put("post_content", postContent.getText().toString());
                params.put("post_tags", "");
                params.put("is_question", "0");
                params.put("question_id", String.valueOf(post_mysql_id));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
//                headers.put("Content-Type", "text/plain; charset=utft-8");
                headers.put("Accept", "application/json");
                headers.put("Authorization", sm.getToken());
                Log.d("token", sm.getToken() + "x");

                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(sr);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Post post = new Post();
        Log.d("post mysql id", post_mysql_id + " id");
        post.setPost_mysql_id(post_mysql_id);
        post.setPostDesc(postContent.getText().toString());
        String images_csv = "";
        for (int i = 0; i < images.size(); i++) {
            HashMap<Integer, String> map;
//            map.put(getPath(selectedImageUri), images.size()+1);
            map = images.get(i);
            images_csv = images_csv + map.get(i + 1) + ",";

        }
        post.setReserved1(images_csv);
        ASD.addPost(post, true);

    }
}
