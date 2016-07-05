package com.example.mahdi.askhow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import libs.CirclePageIndicator;
import libs.ColorShades;
import libs.DeactivatableViewPager;
import libs.SessionManager;
import libs.Statics;
import libs.UsernameValidator;


public class SplashActivity extends AppCompatActivity {

    private static final String SAVING_STATE_SLIDER_ANIMATION = "SliderAnimationSavingState";
    private boolean isSliderAnimation = false;
    private int page_number;

    Typeface font;

    TextView textTitleLogin;
//    TextView loginError;
    EditText username;
    EditText phoneNumber;
    CircularProgressButton btn_request_sms;

    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.splash_activity);

        SessionManager sm = new SessionManager(this);
        if (sm.isLoggedIn())
        {
            Intent intent = new Intent (SplashActivity.this, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            SplashActivity.this.startActivity(intent);
            finish();
        }

        final DeactivatableViewPager viewPager = (DeactivatableViewPager) findViewById(R.id.pager);

        viewPager.setAdapter(new ViewPagerAdapter(R.array.icons, R.array.titles, R.array.hints));

        final CirclePageIndicator mIndicator  = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);


        viewPager.setPageTransformer(true, new CustomPageTransformer());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {

                View landingBGView = findViewById(R.id.landing_backgrond);
                int colorBg[] = getResources().getIntArray(R.array.landing_bg);


                ColorShades shades = new ColorShades();
                shades.setFromColor(colorBg[position % colorBg.length])
                        .setToColor(colorBg[(position + 1) % colorBg.length])
                        .setShade(positionOffset);

                landingBGView.setBackgroundColor(shades.generate());

            }

            public void onPageSelected(int position) {
                if(position==3) {
                    viewPager.setPagingEnabled(false);
                    mIndicator.setVisibility(View.GONE);
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });



    }

    public class ViewPagerAdapter extends PagerAdapter {

        private int iconResId, titleArrayResId, hintArrayResId;

        public ViewPagerAdapter(int iconResId, int titleArrayResId, int hintArrayResId) {

            this.iconResId = iconResId;
            this.titleArrayResId = titleArrayResId;
            this.hintArrayResId = hintArrayResId;
        }

        @Override
        public int getCount() {
            return getResources().getIntArray(iconResId).length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(position<3) {
                Drawable icon = getResources().obtainTypedArray(iconResId).getDrawable(position);
                String title = getResources().getStringArray(titleArrayResId)[position];
                String hint = getResources().getStringArray(hintArrayResId)[position];
                final Typeface fontTitle = Typeface.createFromAsset(getAssets(), "IRTitr.ttf");
                final Typeface fontHint = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");

                View itemView = getLayoutInflater().inflate(R.layout.viewpager_item, container, false);

                ImageView iconView = (ImageView) itemView.findViewById(R.id.landing_img_slide);
                TextView titleView = (TextView) itemView.findViewById(R.id.landing_txt_title);
                TextView hintView = (TextView) itemView.findViewById(R.id.landing_txt_hint);
                titleView.setTypeface(fontTitle);
                hintView.setTypeface(fontHint);
                iconView.setImageDrawable(icon);
                titleView.setText(title);
                hintView.setText(hint);

                container.addView(itemView);

                return itemView;
            }
            else
            {

                View itemView = getLayoutInflater().inflate(R.layout.login_pager, container, false);

                final Typeface font = Typeface.createFromAsset(getAssets(), "IRTerafik.ttf");

                textTitleLogin = (TextView) itemView.findViewById(R.id.textTitleLogin);
//                loginError = (TextView) itemView.findViewById(R.id.loginErrortxt);
                username = (EditText) itemView.findViewById(R.id.inputName);
                phoneNumber = (EditText) itemView.findViewById(R.id.inputMobile);
                btn_request_sms = (CircularProgressButton) itemView.findViewById(R.id.btnWithText);

                pb = (ProgressBar) itemView.findViewById(R.id.progressBar);

                textTitleLogin.setTypeface(font);
//                loginError.setTypeface(font);
                username.setTypeface(font);
                phoneNumber.setTypeface(font);

                username.addTextChangedListener(tw);
                phoneNumber.addTextChangedListener(tw);

                btn_request_sms.setTypeface(font);

                btn_request_sms.setOnClickListener(ocl);

                container.addView(itemView);

                return itemView;
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);

        }
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            btn_request_sms.setProgress(0);
            btn_request_sms.setOnClickListener(ocl);
        }
    };

    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{11}$";
        return mobile.matches(regEx);
    }
    public class CustomPageTransformer implements ViewPager.PageTransformer {


        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            View imageView = view.findViewById(R.id.landing_img_slide);
            View contentView = view.findViewById(R.id.landing_txt_hint);
            View txt_title = view.findViewById(R.id.landing_txt_title);

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left
            } else if (position <= 0) { // [-1,0]
                // This page is moving out to the left

                // Counteract the default swipe
                setTranslationX(view,pageWidth * -position);
                if (contentView != null) {
                    // But swipe the contentView
                    setTranslationX(contentView,pageWidth * position);
                    setTranslationX(txt_title,pageWidth * position);

                    setAlpha(contentView,1 + position);
                    setAlpha(txt_title,1 + position);
                }

                if (imageView != null) {
                    // Fade the image in
                    setAlpha(imageView,1 + position);
                }

            } else if (position <= 1) { // (0,1]
                // This page is moving in from the right

                // Counteract the default swipe
                setTranslationX(view, pageWidth * -position);
                if (contentView != null) {
                    // But swipe the contentView
                    setTranslationX(contentView,pageWidth * position);
                    setTranslationX(txt_title,pageWidth * position);

                    setAlpha(contentView, 1 - position);
                    setAlpha(txt_title, 1 - position);

                }
                if (imageView != null) {
                    // Fade the image out
                    setAlpha(imageView,1 - position);
                }

            }
        }
    }

    /**
     * Sets the alpha for the view. The alpha will be applied only if the running android device OS is greater than honeycomb.
     * @param view - view to which alpha to be applied.
     * @param alpha - alpha value.
     */
    private void setAlpha(View view, float alpha) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && ! isSliderAnimation) {
            view.setAlpha(alpha);
        }
    }

    /**
     * Sets the translationX for the view. The translation value will be applied only if the running android device OS is greater than honeycomb.
     * @param view - view to which alpha to be applied.
     * @param translationX - translationX value.
     */
    private void setTranslationX(View view, float translationX) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && ! isSliderAnimation) {
            view.setTranslationX(translationX);
        }
    }

    public void onSaveInstanceState(Bundle outstate) {

        if(outstate != null) {
            outstate.putBoolean(SAVING_STATE_SLIDER_ANIMATION, isSliderAnimation);
        }

        super.onSaveInstanceState(outstate);
    }

    public void onRestoreInstanceState(Bundle inState) {

        if(inState != null) {
            isSliderAnimation = inState.getBoolean(SAVING_STATE_SLIDER_ANIMATION,false);
        }
        super.onRestoreInstanceState(inState);

    }


    View.OnClickListener ocl = new View.OnClickListener(){

        @Override
        public void onClick(View v) {

            if ((new UsernameValidator()).validate(username.getText().toString()) && isValidPhoneNumber(phoneNumber.getText().toString())) {
                register(username.getText().toString(), phoneNumber.getText().toString(), pb, btn_request_sms);
            }else{
                btn_request_sms.setProgress(-1);
                btn_request_sms.setOnClickListener(null);
            }
        }
    };

    public void register(final String username, final String phone, final ProgressBar pb, final CircularProgressButton btn_reg)
    {
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

//        String url = "http://192.168.44.242/askhow/v1/register";
        String url = Statics.IP_ADDRESS + "register";

//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", username.getText().toString());
//                params.put("phone", phoneNumber.getText().toString());

//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.show();
//        pb.setVisibility(View.VISIBLE);
//        btn_reg.setEnabled(false);
//        btn_reg.setText("");
        btn_reg.setIndeterminateProgressMode(true); // turn on indeterminate progress
        btn_reg.setProgress(1);
        btn_reg.setOnClickListener(null);
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        try {
                            Log.d("registeration detail", response + " reg ");
                            JSONObject jo = new JSONObject(response);
                            String Error = jo.getString("error");
                            if(Error.equals("false")) {
                                int mysql_id = jo.getInt("mysql_id");
                                String username = jo.getString("username");
                                String phone = jo.getString("phone");
                                String token = jo.getString("token");
                                SessionManager sm = new SessionManager(getApplicationContext());
                                sm.createLoginSession(mysql_id ,username, phone, token);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                btn_reg.setProgress(100); // set progress to 0 to switch back to normal state
                                btn_reg.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Intent intent = new Intent (SplashActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        SplashActivity.this.startActivity(intent);
                                    }
                                }, 2000);


//                                Toast.makeText(getApplicationContext(), sm.getToken() + " " + sm.getUsername(), Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        pDialog.hide();
//                        pb.setVisibility(View.GONE);
//                        btn_reg.setEnabled(true);
//                        btn_reg.setText("ثبت شدید");
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        errorLogin.setVisibility(View.VISIBLE);
//                        errorLogin.setText("فکر کنم اینترنت وصل نیست, نمیدونم شایدم باید دوباره امتحان کنی.");

//                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.GONE);
                        btn_reg.setProgress(-1);

                        btn_reg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btn_reg.setProgress(0);
                                btn_reg.setOnClickListener(ocl);
                            }
                        }, 2000);

//                        btn_reg.setEnabled(true);
//                        btn_reg.setText("تایید");
//                        pDialog.hide();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("phone", phone);
                return params;
            }
        };

        queue.add(strRequest);


    }

}
