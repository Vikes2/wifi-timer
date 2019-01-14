package com.github.vikes2.wifi_timer;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();

    private ConnectivityReceiver mConnectivityReceiver;
    private RouterDatabase db;

    private long lastConnect = 0;
    private long lastDc = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");
        mConnectivityReceiver = new ConnectivityReceiver(this);

//        db = Room.databaseBuilder(getApplicationContext(),
//                RouterDatabase.class, "database-name").build();
//
//        db.routerDao().getAll().observe(this, new Observer<List<Router>>() {
//            @Override
//            public void onChanged(@Nullable List<Router> routers) {
//                if (!routerList.isEmpty()) {
//                    routerList.clear();
//                }
//                routerList.addAll(routers);
//                mAdapter.notifyDataSetChanged();
//            }
//        });
    }



    /**
     * When the app's NetworkConnectionActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return START_NOT_STICKY;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob" + mConnectivityReceiver);
        registerReceiver(mConnectivityReceiver, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob");
        unregisterReceiver(mConnectivityReceiver);
        return true;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        String message = isConnected ? "Good! Connected to Internet" : "Sorry! Not connected to internet";


        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo activeNetwork = wm.getConnectionInfo();

        int state = wm.getWifiState();
        long milis = Calendar.getInstance().getTimeInMillis();

        if(isConnected == true){
            if (milis - lastConnect < 3000){
                //stabilizacja
                return;
            }else{
                lastConnect = milis;
                Log.d("pawelski", "Dodaje connect if " + activeNetwork.getNetworkId());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            }

        }else{
            if (milis - lastDc < 3000){
                //stabilizacja
                return;
            }else{
                lastDc = milis;
                Log.d("pawelski", "Dodaje dc ");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            }




        }
        //if activeNetwork.getNetworkId() in database





        //Log.d("pawelski",message + activeNetwork.getNetworkId() + "         " + state);
    }

}