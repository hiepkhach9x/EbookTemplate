package com.ctcstudio.ebooktemplate.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctcstudio.ebooktemplate.R;
import com.ctcstudio.ebooktemplate.adapter.SlidePagerAdapter;
import com.ctcstudio.ebooktemplate.entities.SettingBook;
import com.ctcstudio.ebooktemplate.fragment.ContentChapFragment;
import com.ctcstudio.ebooktemplate.task.SplitTextTask;
import com.ctcstudio.ebooktemplate.utils.LogUtils;
import com.ctcstudio.ebooktemplate.utils.SystemUiHider;
import com.ctcstudio.ebooktemplate.utils.Variables;

import java.util.ArrayList;

/**
 * Created by HungHN on 5/9/2015.
 */
public class ContentChapActivity extends ActionBarActivity implements SplitTextTask.onLoadAsync, ContentChapFragment.ContentCallBack {

    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final boolean TOGGLE_ON_CLICK = true;

    private static final int HIDER_FLAGS = SystemUiHider.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES;

    private SystemUiHider mSystemUiHider;

    private ActionBar mActionBar;

    private String dataContent = "";

    private ProgressBar mProgressBar;

    private ViewPager viewPager;

    private SlidePagerAdapter slidePagerAdapter;

    private FragmentManager fragmentManager;

    private ArrayList<String> listPage = new ArrayList<>();

    private SettingBook settingBook;

    private int width;

    private int height;

    private int textSize = 20;

    private int textColor = 0xffA7573E;

    private int pageColor = 0xffFDF8A6;

    private int topPadding = 10, leftPadding = 15;

    private int bottomPadding = 10, rightPadding = 15;

    private float spacingAdd = 10f, spacingMult = 1.0f;

    private String font = "Roboto-Regular.ttf";

    private TextView chap_name;

    private TextView locationPage;

    private int posChap = 0;

    private int sizePage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_content_chap);
        dataContent = getIntent().getStringExtra(Variables.DATA_CHAP);
        posChap = getIntent().getIntExtra(Variables.CHAP_NAME, posChap);
        dataContent = Html.fromHtml(dataContent).toString().replaceAll(System.getProperty("line.separator") + System.getProperty("line.separator"), System.getProperty("line.separator"));
        dataContent.replace("  ", " ");

        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        initGUI();
        initHideFullscreen();

        new SplitTextTask(this, dataContent, settingBook, this).execute();

    }


    private void initGUI() {
        viewPager = (ViewPager) findViewById(R.id.viewContent);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        chap_name = (TextView) findViewById(R.id.name_chap);
        locationPage = (TextView) findViewById(R.id.position);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        fragmentManager = getSupportFragmentManager();

        chap_name.setText(getString(R.string.common_chap_name, posChap));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        textSize = convertDimension(Variables.COMPLEX_UNIT_SP, textSize);
        topPadding = convertDimension(Variables.COMPLEX_UNIT_DIP, topPadding);
        bottomPadding = convertDimension(Variables.COMPLEX_UNIT_DIP, bottomPadding);
        leftPadding = convertDimension(Variables.COMPLEX_UNIT_DIP, leftPadding);
        rightPadding = convertDimension(Variables.COMPLEX_UNIT_DIP, rightPadding);

        settingBook = new SettingBook(width, height, textSize, textColor, pageColor, topPadding, bottomPadding, leftPadding, rightPadding, spacingAdd, spacingMult, font);

        LogUtils.e(Variables.TAG, settingBook.toString());

        slidePagerAdapter = new SlidePagerAdapter(fragmentManager, listPage, settingBook);
        viewPager.setAdapter(slidePagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
                locationPage.setText(getString(R.string.common_page_position, position + 1, sizePage));
            }
        });
        mProgressBar.setVisibility(View.GONE);
    }

    private int convertDimension(int type, int size) {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int returnSize = 0;
        switch (type) {
            case Variables.COMPLEX_UNIT_SP:
                returnSize = (int) (size * scaledDensity);
                break;
            case Variables.COMPLEX_UNIT_DIP:
                returnSize = (int) (size * density);
                break;
        }
        return returnSize;
    }

    private void initHideFullscreen() {
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }
                        if (visible) {
                            mActionBar.show();
                        } else {
                            mActionBar.hide();
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

//        Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.e(Variables.TAG, "CLICK");
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_content_chap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onLoadDone(ArrayList<String> results) {
        mProgressBar.setVisibility(View.GONE);
        listPage = results;
        sizePage = results.size();
        slidePagerAdapter.setListPage(results);
    }

    @Override
    public void onLoadFail(ArrayList<String> results) {
        mProgressBar.setVisibility(View.GONE);
        slidePagerAdapter.setListPage(results);
    }

    @Override
    public void ContentClick() {
        LogUtils.e(Variables.TAG, "CLICK");
        if (TOGGLE_ON_CLICK) {
            mSystemUiHider.toggle();
        } else {
            mSystemUiHider.show();
        }
    }
}
