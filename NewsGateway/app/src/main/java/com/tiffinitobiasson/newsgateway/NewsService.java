package com.tiffinitobiasson.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.tiffinitobiasson.newsgateway.MainActivity.ACTION_MSG_TO_SERVICE;

/**
 * Created by tiffi on 4/21/2018.
 */

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean isRunning = true;
    private ArrayList<Article> storyList = new ArrayList<>();
    private ServiceReceiver sReceiver = new ServiceReceiver();
    private NewsService newsService = this;



    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    // This one is like onCreate for an Activity
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "onStartCommand: service started");

        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(sReceiver, filter);
        Log.d(TAG, "onStartCommand: ServiceReceiver Registered");

        new Thread(new Runnable() {
            @Override
            public void run() {

                while(isRunning){
                    //Log.d(TAG, "run: "+storylistempty);
                    while(storyList.isEmpty()){
                        //Log.d(TAG, "run: Story list is empty");
                        try{
                            Thread.sleep(250);
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    //Log.d(TAG, "run: Story list isn't empty");
                    sendArticles(storyList);

                }
            }
        }).start();

        return Service.START_STICKY;
    }

    public void setArticles(ArrayList<Article> a){
        storyList.clear();
        for(Article as: a){
            storyList.add(as);
        }
    }

    public void sendArticles(ArrayList<Article> sl){
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);
        intent.putExtra(MainActivity.STORY_LIST, sl);
        intent.putExtra("Page", 0);
        sendBroadcast(intent);
        storyList.clear();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(sReceiver);
        isRunning = false;
        super.onDestroy();
    }
    //////////////////////////////////////////////////////////////////////////
    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch(intent.getAction()){
                case ACTION_MSG_TO_SERVICE:
                    String id = intent.getStringExtra("SourceID");
                    new NewsArticleDownloader(newsService,id).execute();
                    Log.d(TAG, "onReceive: Action_msg_to_Service broadcast received");
                    break;
            }
        }
    }
}
