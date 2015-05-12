package com.ctcstudio.ebooktemplate.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.ctcstudio.ebooktemplate.EBookApplication;
import com.ctcstudio.ebooktemplate.R;
import com.ctcstudio.ebooktemplate.entities.SettingBook;
import com.ctcstudio.ebooktemplate.utils.Config;
import com.ctcstudio.ebooktemplate.utils.Constant;
import com.google.gson.Gson;

import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by HungHN on 5/9/2015.
 */
public class SplashActivity extends Activity {
    ProgressBar mProgressBar;

    private SettingBook settingBook;

    private int width;

    private int height;

    private int fontSize = 20;
    private int textSize = 20;

    private int textColor = 0xffA7573E;

    private int pageColor = 0xffFDF8A6;

    private int topPadding = 10, leftPadding = 10;

    private int bottomPadding = 10, rightPadding = 10;

    private float spacingAdd = 10f, spacingMult = 1.0f;

    private String font = "Roboto-Regular.ttf";

    private SharedPreferences sharedPreferences;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Display display = ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    private class IntentLauncher extends Thread {
        Book book;
        AssetManager assetManager = getAssets();

        @Override
        public void run() {
            try {
                String jsonSetting = sharedPreferences.getString(Constant.DATA_SETTING, "");
                if (jsonSetting.isEmpty()) {
                    textColor = getResources().getColor(R.color.sepia);
                    pageColor = getResources().getColor(R.color.sepia_overlay);
                    textSize = convertDimension(Constant.COMPLEX_UNIT_SP, fontSize);
                    topPadding = convertDimension(Constant.COMPLEX_UNIT_DIP, topPadding);
                    bottomPadding = convertDimension(Constant.COMPLEX_UNIT_DIP, bottomPadding);
                    leftPadding = convertDimension(Constant.COMPLEX_UNIT_DIP, leftPadding);
                    rightPadding = convertDimension(Constant.COMPLEX_UNIT_DIP, rightPadding);
                    settingBook = new SettingBook(width, height, textSize, textColor, pageColor, topPadding, bottomPadding, leftPadding, rightPadding, spacingAdd, spacingMult, font);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    jsonSetting = gson.toJson(settingBook);
                    editor.putString(Constant.DATA_SETTING, jsonSetting);
                    editor.commit();
                } else {
                    settingBook = gson.fromJson(jsonSetting, SettingBook.class);
                }
                // find InputStream for book
                InputStream inputStream = assetManager
                        .open("books/" + Config.bookName);
                // Load Book from inputStream
                book = (new EpubReader()).readEpub(inputStream);

            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
            // Start main activity
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            EBookApplication.get().setSettingBook(settingBook);
            EBookApplication.get().setBook(book);
            Intent intent = new Intent(SplashActivity.this,
                    HomeActivity.class);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();

        }
    }

    private int convertDimension(int type, int size) {
        float density = getResources().getDisplayMetrics().density;
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int returnSize = 0;
        switch (type) {
            case Constant.COMPLEX_UNIT_SP:
                returnSize = (int) (size * scaledDensity);
                break;
            case Constant.COMPLEX_UNIT_DIP:
                returnSize = (int) (size * density);
                break;
        }
        return returnSize;
    }
}
