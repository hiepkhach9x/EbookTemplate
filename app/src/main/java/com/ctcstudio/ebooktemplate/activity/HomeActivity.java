package com.ctcstudio.ebooktemplate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ctcstudio.ebooktemplate.R;
import com.ctcstudio.ebooktemplate.adapter.CustomDrawerAdapter;
import com.ctcstudio.ebooktemplate.entities.DrawerItem;
import com.ctcstudio.ebooktemplate.utils.LogUtils;
import com.ctcstudio.ebooktemplate.utils.Variables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;


public class HomeActivity extends ActionBarActivity {

    private ActionBar mActionBar;

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    private ListView mDrawerList;

    private CustomDrawerAdapter mDrawerAdapter;

    private Book mBook;

    private ListView mListChapView;

    private ArrayList<String> mListchap = new ArrayList<>();

    private ArrayAdapter<String> chapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mBook = Variables.HM_DATA_BOOK.get(Variables.DATA_BOOK);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mListChapView = (ListView) findViewById(R.id.list_chap);

        setSupportActionBar(toolbar);
        mDrawerAdapter = new CustomDrawerAdapter(this, R.layout.item_navigation, getListDrawerItem());
        mDrawerList.setAdapter(mDrawerAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtils.d(Variables.TAG, "Choose item menu: " + mDrawerAdapter.getItem(position).getItemName());
            }
        });

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                mActionBar.setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mListchap = logTableOfContents(mBook.getTableOfContents().getTocReferences(), 0);
        chapAdapter = new ArrayAdapter<String>
                (this, R.layout.item_chap, R.id.name_chap, mListchap);

        mListChapView.setAdapter(chapAdapter);
        mListChapView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(HomeActivity.this, ContentChapActivity.class);
                    intent.putExtra(Variables.DATA_CHAP, new String(mBook.getContents().get(position + 1).getData()));
                    startActivity(intent);
                } catch (IOException e) {
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private ArrayList<String> logTableOfContents(List<TOCReference> tocReferences, int depth) {
        ArrayList<String> mList = new ArrayList<>();
        if (tocReferences == null) {
            return null;
        }

        for (TOCReference tocReference : tocReferences) {

            StringBuilder tocString = new StringBuilder();

            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }

            tocString.append(tocReference.getTitle());

            mList.add(tocString.toString());
            logTableOfContents(tocReference.getChildren(), depth + 1);
        }
        return mList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }

    private ArrayList<DrawerItem> getListDrawerItem() {
        ArrayList<DrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new DrawerItem(getString(R.string.app_name)));
        drawerItems.add(new DrawerItem(getString(R.string.common_store), R.drawable.ic_store));
        drawerItems.add(new DrawerItem(getString(R.string.common_about), R.drawable.ic_about));
        drawerItems.add(new DrawerItem(getString(R.string.common_truyenkhac)));
        drawerItems.add(new DrawerItem(getString(R.string.common_tienhiep), R.drawable.ic_tienhiep));
        drawerItems.add(new DrawerItem(getString(R.string.common_kiemhiep), R.drawable.ic_kiemhiep));
        drawerItems.add(new DrawerItem(getString(R.string.common_ngontinh), R.drawable.ic_ic_ngontinh));
        return drawerItems;
    }
}
