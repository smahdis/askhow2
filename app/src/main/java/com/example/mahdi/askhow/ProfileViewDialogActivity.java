package com.example.mahdi.askhow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Locale;

import Items.User;
import database_handler.PostHandler;
import libs.LetterAvatar;
import libs.MethodLibs;

/**
 * Created by Mahdi on 6/17/2016.
 */
public class ProfileViewDialogActivity extends AppCompatActivity{

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.profile_dialog);
        this.setFinishOnTouchOutside(true);

        parent = (RelativeLayout) findViewById(R.id.profile_view_parent);

        final Activity context = this;
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                ActivityCompat.finishAfterTransition(context);
            }
        });


        number_of_questions_txt= (TextView) findViewById(R.id.number_of_questions_txt);
        number_of_questions_value= (TextView) findViewById(R.id.number_of_questions_value);
        number_of_answers_txt= (TextView) findViewById(R.id.number_of_answers_txt);
        number_of_answers_value= (TextView) findViewById(R.id.number_of_answers_value);
        number_of_scores_txt= (TextView) findViewById(R.id.number_of_scores_txt);
        number_of_scores_values= (TextView) findViewById(R.id.number_of_scores_value);
        degree_txt= (TextView) findViewById(R.id.degree_txt);
        degree_value= (TextView) findViewById(R.id.degree_value);
        submiButton = (Button) findViewById(R.id.btn_submit);
        avatar = (ImageView) findViewById(R.id.avatar);
        usernameTitle = (TextView) findViewById(R.id.usernameTitle);
        int[] i = {1,2,3};
        final Typeface font = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");
        NumberFormat nf = NumberFormat.getInstance(new Locale("ar","EG"));
        i[0] =  Integer.valueOf(number_of_questions_value.getText().toString());
        i[1] =  Integer.valueOf(number_of_answers_value.getText().toString());
        i[2] =  Integer.valueOf(number_of_scores_values.getText().toString());
        number_of_questions_value.setText(nf.format(i[0]));
        number_of_answers_value.setText(nf.format(i[1]));
        number_of_scores_values.setText(nf.format(i[2]));

        number_of_questions_txt.setTypeface(font);
        number_of_questions_value.setTypeface(font);
        number_of_answers_txt.setTypeface(font);
        number_of_answers_value.setTypeface(font);
        number_of_scores_txt.setTypeface(font);
        number_of_scores_values.setTypeface(font);
        degree_txt.setTypeface(font);
        degree_value.setTypeface(font);
        submiButton.setTypeface(font);

        PostHandler ph = new PostHandler(context);
        int post_id = getIntent().getExtras().getInt("post_mysql_id");
        User user = ph.returnUserInfo(post_id);
        usernameTitle.setText(user.getUserName());

        final int tileSize = getResources().getDimensionPixelSize(R.dimen.letter_tile_size);
        final LetterAvatar tileProvider = new LetterAvatar(context);
        Bitmap letterTile;// = tileProvider.getLetterTile(post.getPosterName(), post.getPosterName(), tileSize, tileSize);

        if (Math.abs(post_id) % 8 == Math.abs(user.getUserName().hashCode()) % 8) {
            letterTile = tileProvider.getLetterTile(user.getUserName(), user.getUserName() + post_id, tileSize, tileSize);
        } else
            letterTile = tileProvider.getLetterTile(user.getUserName(), user.getUserName(), tileSize, tileSize);


        avatar.setImageBitmap(MethodLibs.getCircularBitmap(letterTile));

    }
}
