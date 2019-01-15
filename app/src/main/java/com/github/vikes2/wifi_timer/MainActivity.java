package com.github.vikes2.wifi_timer;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import static java.lang.Math.floor;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RouterDatabase db;
    private ArrayList<Action> actionList = new ArrayList<>();
    private ArrayList<Router> routerList = new ArrayList<>();
    private StatsAdapter mAdapter;
    public HashMap<String, String> mTimeData;

    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        db = Room.databaseBuilder(getApplicationContext(),
                RouterDatabase.class, "database-name").build();

        db.actionDao().getAll().observe(this, new Observer<List<Action>>() {
            @Override
            public void onChanged(@Nullable List<Action> actions) {
                if (!actionList.isEmpty()) {
                    actionList.clear();
                }
                actionList.addAll(actions);
                Log.d("pawelski","dbs: "+actions.size() +"---als: "+actionList.size() );
                processData();
            }
        });



        db.routerDao().getAll().observe(this, new Observer<List<Router>>() {
            @Override
            public void onChanged(@Nullable List<Router> routers) {
                if (!routerList.isEmpty()) {
                    routerList.clear();
                }
                routerList.addAll(routers);
//                mAdapter.notifyDataSetChanged();
                processData();
            }
        });

        mRecyclerView = findViewById(R.id.statsRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new StatsAdapter(routerList, actionList, processTimeData(routerList, actionList));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        scheduleJob();
    }

    private void processData() {
        String msg = "";
        msg+= "-pd-rls("+routerList.size()+")--als("+actionList.size()+")";
//        Log.d("pawelski", msg);
        if(routerList != null && actionList != null && routerList.size() > 0 && actionList.size() > 0) {
            mTimeData = processTimeData(routerList, actionList);
            mAdapter.mData = mTimeData;

            Log.d("pawelski", "notify wth_val: "+ mTimeData.values());
            mAdapter.notifyDataSetChanged();
        }else{

        }
    }

    public HashMap<String, String> processTimeData(ArrayList<Router> mRouterList, ArrayList<Action> mActionList){
        HashMap<String, Long> resultMap = new HashMap<>();
        for(Router router : mRouterList){
            resultMap.put(router.mac, 0l);
        }
        String last_connection_id = "";
        long last_connection_time = -1;

        for(Action action : mActionList){
            if(action.connected == true && last_connection_id.isEmpty()){
                last_connection_id = action.network_id;
                last_connection_time = action.time;
            }else if(action.connected == false ){

                if(!last_connection_id.isEmpty() && last_connection_time != -1){
                    long currentTime = resultMap.get(last_connection_id);
                    long toAddTime = action.time - last_connection_time;

                    for(Router curRouter : mRouterList){
                        if(curRouter.mac == last_connection_id){
                            Log.d("pawelski", currentTime+" was + " +toAddTime);
                            resultMap.put(last_connection_id, currentTime + toAddTime);
                        }
                    }
                }



                last_connection_id = "";
                last_connection_time = -1;
            }
        }

        if(mActionList.size()>0){
            Action  lastAction = mActionList.get( mActionList.size() - 1 );
            if( lastAction.connected == true){
                long now = Calendar.getInstance().getTimeInMillis();
                long toAddTime = now - lastAction.time;
                long currentTime = resultMap.get(lastAction.network_id);
                Log.d("pawelski", currentTime+" was + " +toAddTime);

                resultMap.put(lastAction.network_id, currentTime + toAddTime);
            }
        }

//        if(mActionList.size() >0 && mActionList.get(mActionList.size() -1).connected == true){
//            long now = Calendar.getInstance().getTimeInMillis();
//            long tmpTime =now - mActionList.get(mActionList.size() -1).time ;
//
//            long tmp = resultMap.get(mActionList.get(mActionList.size() -1).network_id);
//            resultMap.put(mActionList.get(mActionList.size() -1).network_id, tmp + tmpTime);
//        }


        HashMap<String, String> resultStringMap = new HashMap<>();
        for(String currentKey : resultMap.keySet()){
            resultStringMap.put(currentKey, miliToString(resultMap.get(currentKey)));
        }

        return resultStringMap;
    }

    private String miliToString(Long mil){
        int sec = (int) floor((mil / 1000) % 60);
        int min = (int) floor(((mil / 1000) / 60) % 60);
        int hour = (int) floor((((mil / 1000) / 60) / 60) % 24);
        int day = (int) floor(((((mil / 1000) / 60) / 60) / 24) % 30);

        String out = "";
//        Log.d("pawelski", "mil: "+ mil + "min " +min);

        if(day> 0){
            out = day + " dni " + hour + " godz. " + min + " min";
        }else if(hour > 0){
            out = hour + " godz. " + min + " min";
        }else if(min > 0){
            out = min + " min " + sec + " sec";
        }else{
            out = sec + " sec";
        }
        return out;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy(){
//        long milis = Calendar.getInstance().getTimeInMillis();
//        AsyncTask.execute(new Runnable() {
//            Action action;
//            @Override
//            public void run() {
//                db.actionDao().insert(action);
//            }
//            public Runnable init(Action _action){
//                this.action = _action;
//                return(this);
//            }
//        }.init( new Action(-1+"", false, milis)  ));
        super.onDestroy();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }


    public void startCrud(View view) {
        Intent myIntent = new Intent(this, FavouriteActivity.class);
        startActivity(myIntent);
    }
}
