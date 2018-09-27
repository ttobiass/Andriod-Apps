package com.tiffinitobiasson.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String STORY_LIST = "STORY_LIST";
    private HashMap<String, ArrayList<Source>> sourcesMap = new HashMap<>();
    private ArrayList<Source> sources = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<Article> articles = new ArrayList<>();
    private MyPageAdapter pageAdapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private Menu opt_menu;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private NewsReceiver nReceiver = new NewsReceiver();
    private String categorySelected = "All";
    private int sourceSelected = -1;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent in = new Intent(this, NewsService.class);
        startService(in);

        Log.d(TAG, "onCreate: service started");

        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(nReceiver, filter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.leftDrawer);

        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_layout, sources));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        sourceSelected = position;
                        selectItem(sourceSelected);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = new ArrayList<>();

        pageAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageAdapter);

        if(sources.isEmpty()){
            new NewsSourceDownloader(this,categorySelected).execute();
        }

    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
        unregisterReceiver(nReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("CAT_SEL", categorySelected);
        outState.putString("Action_Bar_Title", (String) getTitle());
        outState.putSerializable("Articles", articles);
        outState.putInt("pagePosition", pager.getCurrentItem());

        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);

        setTitle(savedInstanceState.getString("Action_Bar_Title"));


        categorySelected = savedInstanceState.getString("CAT_SEL");
        new NewsSourceDownloader(this,categorySelected).execute();

        articles = (ArrayList<Article>) savedInstanceState.getSerializable("Articles");
        page = savedInstanceState.getInt("pagePosition");

        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

    }

    @Override
    protected void onResume() {
        if(!articles.isEmpty()){
            pager.setBackground(null);
            Intent intent = new Intent();
            intent.setAction(ACTION_NEWS_STORY);
            intent.putExtra(STORY_LIST, articles);
            intent.putExtra("Page", page);
            sendBroadcast(intent);
        }

        super.onResume();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        opt_menu = menu;
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            //Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }

        categorySelected = item.toString();

        new NewsSourceDownloader(this,categorySelected).execute();

        return super.onOptionsItemSelected(item);

    }

    private void selectItem(int position) {
        //News source selected, load the article fragments
        pager.setBackground(null);
        setTitle(sources.get(position).getName());
        Intent in = new Intent();
        in.setAction(ACTION_MSG_TO_SERVICE);
        in.putExtra("SourceID", sources.get(position).getID());
        sendBroadcast(in);
        Log.d(TAG, "selectItem: ServiceReciever Broadcast Sent");
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public void setSources(ArrayList<Source> listIn, ArrayList<String> catList){
        sourcesMap.clear();
        sources.clear();
        for (Source s : listIn) {
            if (!sourcesMap.containsKey(s.getCategory())) {
                sourcesMap.put(s.getCategory(), new ArrayList<Source>());
            }
            sourcesMap.get(s.getCategory()).add(s);
        }

        sourcesMap.put("All", listIn);
        sources.addAll(listIn);

        Log.d(TAG, "setSources: "+sources.toString());

        ((ArrayAdapter) mDrawerList.getAdapter()).notifyDataSetChanged();


        if(categories.isEmpty()){
            categories = catList;
            categories.add(0,"All");

            //opt_menu.clear();

            if(opt_menu == null){
                invalidateOptionsMenu();
                if(opt_menu == null){
                    return;
                }
            }
            for(String c: categories){
                opt_menu.add(c);
            }
        }


    }

/////////////////////////////////////////////////////////////////////////////
    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case ACTION_NEWS_STORY:
                    articles = (ArrayList<Article>) intent.getSerializableExtra(STORY_LIST);
                    page = intent.getIntExtra("Page", 0);
                    reDoFragments(articles, page);
                    break;
            }
        }

        public void reDoFragments(ArrayList<Article> a, int p){
            for (int i = 0; i < pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);

            fragments.clear();
            int count = a.size();
            for (int i = 0; i < count; i++) {
                fragments.add(NewsFragment.newInstance(a.get(i),(i+1)+" of "+count));
            }

            pageAdapter.notifyDataSetChanged();
            pager.setCurrentItem(p);
        }
    }

///////////////////////////////////////////////////////////////////////////////
    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        public void notifyChangeInPosition(int n) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += getCount() + n;
        }

    }
}
