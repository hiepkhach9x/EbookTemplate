package com.ctcstudio.ebooktemplate.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.ctcstudio.ebooktemplate.R;
import com.ctcstudio.ebooktemplate.utils.Config;
import com.ctcstudio.ebooktemplate.utils.Constant;

import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;

/**
 * Created by HungHN on 5/9/2015.
 */
public class SplashActivity extends Activity {
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    private class IntentLauncher extends Thread {
        Book book;
        AssetManager assetManager = getAssets();

        @Override
        public void run() {
            try {
                // find InputStream for book
                InputStream inputStream = assetManager
                        .open("books/" + Config.bookName);
                // Load Book from inputStream
                book = (new EpubReader()).readEpub(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Start main activity
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            Intent intent = new Intent(SplashActivity.this,
                    HomeActivity.class);
            Constant.HM_DATA_BOOK.put(Constant.DATA_BOOK, book);
            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();

        }
    }
}
