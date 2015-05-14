package com.ctcstudio.ebooktemplate.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctcstudio.ebooktemplate.EBookApplication;
import com.ctcstudio.ebooktemplate.R;
import com.ctcstudio.ebooktemplate.adapter.SlidePagerAdapter;
import com.ctcstudio.ebooktemplate.entities.SettingBook;
import com.ctcstudio.ebooktemplate.fragment.ContentChapFragment;
import com.ctcstudio.ebooktemplate.task.SplitTextTask;
import com.ctcstudio.ebooktemplate.utils.Constants;
import com.ctcstudio.ebooktemplate.utils.LogUtils;
import com.ctcstudio.ebooktemplate.utils.SystemUiHider;
import com.ctcstudio.ebooktemplate.view.EBookViewPagerView;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;

/**
 * Created by HungHN on 5/9/2015.
 */
public class ContentChapActivity extends ActionBarActivity implements SplitTextTask.onLoadAsync, ContentChapFragment.ContentCallBack, EBookViewPagerView.OnSwipeOutListener {

    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final boolean TOGGLE_ON_CLICK = true;

    private static final int HIDER_FLAGS = SystemUiHider.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES;

    private SystemUiHider mSystemUiHider;

    private ActionBar mActionBar;

    private String dataContent = "";

    private ProgressBar mProgressBar;

    private EBookViewPagerView viewPager;

    private SlidePagerAdapter slidePagerAdapter;

    private FragmentManager fragmentManager;

    private ArrayList<String> listPage = new ArrayList<>();

    private SettingBook settingBook;

    private int width;

    private int height;

    private int fontSize = 20;

    private int textColor = 0xffA7573E;

    private int pageColor = 0xffFDF8A6;

    private int topPadding = 10, leftPadding = 10;

    private int bottomPadding = 10, rightPadding = 10;

    private float spacingAdd = 10f, spacingMult = 1.0f;

    private String font = "Roboto-Regular.ttf";

    private TextView chap_name;

    private TextView locationPage;

    private int posChap = 0;

    private int maxChap = 0;

    private int sizePage = 0;

    private Book eBook;

    private SharedPreferences sharedPreferences;

    private boolean isChangeChap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_chap);
        eBook = EBookApplication.get().getBook();
        settingBook = EBookApplication.get().getSettingBook();
        dataContent = getIntent().getStringExtra(Constants.DATA_CHAP);
        posChap = getIntent().getIntExtra(Constants.CHAP_NAME, posChap);
        maxChap = getIntent().getIntExtra(Constants.MAX_CHAP, maxChap);
        dataContent = Html.fromHtml(dataContent).toString().replaceAll(System.getProperty("line.separator") + System.getProperty("line.separator"), System.getProperty("line.separator"));
        dataContent.replace("  ", " ");
        sharedPreferences = getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);

        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        initGUI();
        initHideFullscreen();

        mProgressBar.setVisibility(View.VISIBLE);
        new SplitTextTask(this, dataContent, settingBook, this).execute();

    }

    private void initGUI() {
        viewPager = (EBookViewPagerView) findViewById(R.id.viewContent);
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
        viewPager.setOnSwipeOutListener(this);

        textColor = settingBook.getTextColor();
        pageColor = settingBook.getPageColor();
        fontSize = convertDimensionToPx(Constants.COMPLEX_UNIT_SP, settingBook.getTextSize());
        topPadding = settingBook.getTopPadding();
        bottomPadding = settingBook.getBottomPadding();
        leftPadding = settingBook.getLeftPadding();
        rightPadding = settingBook.getRightPadding();
        font = settingBook.getFont();

        slidePagerAdapter = new SlidePagerAdapter(fragmentManager, listPage, settingBook);
        viewPager.setAdapter(slidePagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();
                locationPage.setText(getString(R.string.common_page_position, position + 1, sizePage));
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        EBookApplication.get().setBookMark(posChap);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonString = gson.toJson(settingBook);
        editor.putInt(Constants.DATA_BOOKMARK, posChap);
        editor.putString(Constants.DATA_SETTING, jsonString);
        editor.commit();
    }

    private void loadNewChap(int position) {
        try {
            mProgressBar.setVisibility(View.VISIBLE);
            dataContent = new String(eBook.getContents().get(position).getData());
            dataContent = Html.fromHtml(dataContent).toString().replaceAll(System.getProperty("line.separator") + System.getProperty("line.separator"), System.getProperty("line.separator"));
            dataContent.replace("  ", " ");
            isChangeChap = true;
            new SplitTextTask(ContentChapActivity.this, dataContent, settingBook, ContentChapActivity.this).execute();
            chap_name.setText(getString(R.string.common_chap_name, posChap));
        } catch (IOException ex) {
        }
    }

    private int convertDimension(int type, int size) {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int returnSize = 0;
        switch (type) {
            case Constants.COMPLEX_UNIT_SP:
                returnSize = (int) (size * scaledDensity);
                break;
            case Constants.COMPLEX_UNIT_DIP:
                returnSize = (int) (size * density);
                break;
        }
        return returnSize;
    }

    @Override
    public void finish() {
        super.finish();
    }

    private int convertDimensionToPx(int type, int size) {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int returnSize = 0;
        switch (type) {
            case Constants.COMPLEX_UNIT_SP:
                returnSize = (int) (size / scaledDensity);
                break;
            case Constants.COMPLEX_UNIT_DIP:
                returnSize = (int) (size / density);
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
                LogUtils.e(Constants.TAG, "CLICK");
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
        if (id == R.id.action_font_setting) {
            showFontSetting();
            return true;
        }
        if (id == R.id.action_next) {
            if (posChap <= maxChap) {
                posChap++;
                loadNewChap(posChap);
            } else {
                showDialogChap(getString(R.string.dialog_message_chap_end));
            }
            return true;
        }
        if (id == R.id.action_previous) {
            if (posChap >= 1) {
                posChap--;
                loadNewChap(posChap);
            } else {
                showDialogChap(getString(R.string.dialog_message_chap_first));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFontSetting() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_setting);
        final TextView fontTextView = (TextView) dialog.findViewById(R.id.font);
        final TextView margins = (TextView) dialog.findViewById(R.id.margins);
        final TextView lineSpace = (TextView) dialog.findViewById(R.id.line_space);
        final TextView textFontSize = (TextView) dialog.findViewById(R.id.textSize);
        Button btnOk = (Button) dialog.findViewById(R.id.ok);
        ImageButton btnInCrease = (ImageButton) dialog.findViewById(R.id.inCrease);
        ImageButton btnDeCrease = (ImageButton) dialog.findViewById(R.id.deCrease);
        Button backgroundWhite = (Button) dialog.findViewById(R.id.background_white);
        Button backgroundSepia = (Button) dialog.findViewById(R.id.background_sepia);
        Button backgroundBlack = (Button) dialog.findViewById(R.id.background_black);
        final ImageView imgWhite = (ImageView) dialog.findViewById(R.id.state_background_white);
        final ImageView imgSepia = (ImageView) dialog.findViewById(R.id.state_background_sepia);
        final ImageView imgBlack = (ImageView) dialog.findViewById(R.id.state_background_black);

        if ("DroidSerif-Regular.ttf".equals(font)) {
            fontTextView.setText(R.string.common_droid_serif);

        } else if ("Roboto-Regular.ttf".equals(font)) {
            fontTextView.setText(R.string.common_roboto);

        } else if ("DroidSans.ttf".equals(font)) {
            fontTextView.setText(R.string.common_droid_sans);
        }
        textFontSize.setText(fontSize + "");
        if (textColor == getResources().getColor(R.color.white_light)) {
            imgWhite.setImageResource(R.drawable.ic_circle);
            imgBlack.setImageResource(R.drawable.ic_circle_v);
            imgSepia.setImageResource(R.drawable.ic_circle);
        } else if (textColor == getResources().getColor(R.color.sepia)) {
            imgWhite.setImageResource(R.drawable.ic_circle);
            imgBlack.setImageResource(R.drawable.ic_circle);
            imgSepia.setImageResource(R.drawable.ic_circle_v);

        } else if (textColor == getResources().getColor(R.color.black)) {
            imgWhite.setImageResource(R.drawable.ic_circle_v);
            imgBlack.setImageResource(R.drawable.ic_circle);
            imgSepia.setImageResource(R.drawable.ic_circle);
        }
        if (leftPadding == convertDimension(Constants.COMPLEX_UNIT_DIP, 10)) {
            margins.setText(R.string.common_narrow);

        } else if (leftPadding == convertDimension(Constants.COMPLEX_UNIT_DIP, 20)) {
            margins.setText(R.string.common_normal);

        } else if (leftPadding == convertDimension(Constants.COMPLEX_UNIT_DIP, 30)) {
            margins.setText(R.string.common_wide);
        }
        if (spacingMult == 1.0f) {
            lineSpace.setText(R.string.common_narrow);

        } else if (spacingMult == 1.5f) {
            lineSpace.setText(R.string.common_normal);

        } else if (spacingMult == 2.0f) {
            lineSpace.setText(R.string.common_wide);
        }
        fontTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v, fontTextView, R.menu.menu_font);
            }
        });
        margins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v, margins, R.menu.menu_margin);
            }
        });
        lineSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v, lineSpace, R.menu.menu_margin);
            }
        });
        btnInCrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontSize++;
                textFontSize.setText(fontSize + "");
            }
        });
        btnDeCrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fontSize--;
                textFontSize.setText(fontSize + "");
            }
        });
        backgroundWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textColor = getResources().getColor(R.color.black);
                pageColor = getResources().getColor(R.color.white);
                imgWhite.setImageResource(R.drawable.ic_circle_v);
                imgBlack.setImageResource(R.drawable.ic_circle);
                imgSepia.setImageResource(R.drawable.ic_circle);
            }
        });
        backgroundSepia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textColor = getResources().getColor(R.color.sepia);
                pageColor = getResources().getColor(R.color.sepia_overlay);
                imgWhite.setImageResource(R.drawable.ic_circle);
                imgBlack.setImageResource(R.drawable.ic_circle);
                imgSepia.setImageResource(R.drawable.ic_circle_v);
            }
        });
        backgroundBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textColor = getResources().getColor(R.color.white_light);
                pageColor = getResources().getColor(R.color.black);
                imgWhite.setImageResource(R.drawable.ic_circle);
                imgBlack.setImageResource(R.drawable.ic_circle_v);
                imgSepia.setImageResource(R.drawable.ic_circle);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (margins.getText().toString().trim().equals(getString(R.string.common_narrow).trim())) {
                    leftPadding = convertDimension(Constants.COMPLEX_UNIT_DIP, 10);

                } else if (margins.getText().toString().trim().equals(getString(R.string.common_normal).trim())) {
                    leftPadding = convertDimension(Constants.COMPLEX_UNIT_DIP, 20);

                } else if (margins.getText().toString().trim().equals(getString(R.string.common_wide).trim())) {
                    leftPadding = convertDimension(Constants.COMPLEX_UNIT_DIP, 30);
                }
                if (lineSpace.getText().toString().trim().equals(getString(R.string.common_narrow).trim())) {
                    spacingMult = 1.0f;

                } else if (lineSpace.getText().toString().trim().equals(getString(R.string.common_normal).trim())) {
                    spacingMult = 1.5f;

                } else if (lineSpace.getText().toString().trim().equals(getString(R.string.common_wide).trim())) {
                    spacingMult = 2.0f;
                }
                settingBook.setTextColor(textColor);
                settingBook.setPageColor(pageColor);
                settingBook.setTextSize(convertDimension(Constants.COMPLEX_UNIT_SP, fontSize));
                settingBook.setLeftPadding(leftPadding);
                settingBook.setRightPadding(rightPadding);
                settingBook.setSpacingMult(spacingMult);
                settingBook.setFont(font);
                EBookApplication.get().setSettingBook(settingBook);
                mProgressBar.setVisibility(View.VISIBLE);
                new SplitTextTask(ContentChapActivity.this, dataContent, settingBook, ContentChapActivity.this).execute();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    void showPopupWindow(View view, final TextView textView, int menu) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemDroidSerif:
                        textView.setText(R.string.common_droid_serif);
                        font = "DroidSerif-Regular.ttf";
                        break;

                    case R.id.itemRoboto:
                        textView.setText(R.string.common_roboto);
                        font = "Roboto-Regular.ttf";
                        break;

                    case R.id.itemDroidSans:
                        textView.setText(R.string.common_droid_sans);
                        font = "DroidSans.ttf";
                        break;

                    case R.id.itemNarrow:
                        textView.setText(R.string.common_narrow);
                        break;

                    case R.id.itemNormal:
                        textView.setText(R.string.common_normal);
                        break;

                    case R.id.itemWide:
                        textView.setText(R.string.common_wide);
                        break;
                }
                return true;
            }
        });
        popup.show();
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
        slidePagerAdapter.setSettingBook(settingBook);
        slidePagerAdapter.notifyDataSetChanged();
        if (isChangeChap) {
            viewPager.setCurrentItem(0);
            isChangeChap = false;
        }
    }

    @Override
    public void onLoadFail(ArrayList<String> results) {
        mProgressBar.setVisibility(View.GONE);
        slidePagerAdapter.setListPage(results);
    }

    @Override
    public void ContentClick() {
        if (TOGGLE_ON_CLICK) {
            mSystemUiHider.toggle();
        } else {
            mSystemUiHider.show();
        }
    }

    public ContentChapActivity() {
        super();
    }

    @Override
    public void onSwipeOutAtEnd() {
        if (posChap <= maxChap) {
            posChap++;
            loadNewChap(posChap);
        } else {
            showDialogChap(getString(R.string.dialog_message_chap_end));
        }
    }

    @Override
    public void onSwipeOutAtStart() {
        if (posChap >= 1) {
            posChap--;
            loadNewChap(posChap);
        } else {
            showDialogChap(getString(R.string.dialog_message_chap_first));
        }
    }

    private void showDialogChap(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
