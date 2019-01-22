package com.github.lukaszmalyszko.wifi_timer;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

public class NetworkSchedulerService extends JobService implements
        ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();

    private ConnectivityReceiver mConnectivityReceiver;
    private RouterDatabase db;
    private ArrayList<String> routerList = new ArrayList<>();


    private long lastConnect = 0;
    private long lastDc = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service created");

        mConnectivityReceiver = new ConnectivityReceiver(this);

        db = Room.databaseBuilder(getApplicationContext(),
                RouterDatabase.class, "database-name").build();

    }
    @Override
    public void onDestroy(){
    //obsluga liczenia czasu polaczenia z danym wifi(tzn przy destroyu przestaje liczyc, poprzez dodanie akcji disconnect)
        if(routerList.size()>0 ) {
            long milis = Calendar.getInstance().getTimeInMillis();
            AsyncTask.execute(new Runnable() {
                Boolean isConnected;
                long milis;
                @Override
                public void run() {
                    Action[] lastConnected = db.actionDao().getLastConnected();

                    if(lastConnected.length > 0){
                        db.actionDao().insert(new Action(lastConnected[0].network_id, isConnected, milis));
                    }
                }
                public Runnable init(Boolean _isConnected, long _milis){
                    this.isConnected = _isConnected;
                    this.milis = _milis;
                    return(this);
                }
            }.init( false, milis));
        }

        super.onDestroy();
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
    public void onNetworkConnectionChanged(final boolean isConnected) {
        final String message = isConnected ? "Good! Connected to Internet" : "Sorry! Not connected to internet";
        AsyncTask.execute(new Runnable() {
            public void run() {
                WifiManager wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo activeNetwork = wm.getConnectionInfo();

                int state = wm.getWifiState();
                long milis = Calendar.getInstance().getTimeInMillis();
                routerList = (ArrayList<String>)db.routerDao().getList();

                String _network_id =""+ activeNetwork.getNetworkId();

                if(isConnected == true){
                    //dodanie opoznienia zeby nie bylo przeklaman z polaczeniami i jesli router nie jest dodany
                    if (milis - lastConnect > 3000 && routerList.contains(""+activeNetwork.getNetworkId())){
                        lastConnect = milis;
                        if(routerList.contains(_network_id)) {
                            // dodawanie rekordow action do bazy
                            AsyncTask.execute(new Runnable() {
                                Action action;
                                @Override
                                public void run() {
                                    Action lastAction = db.actionDao().getLastAction(action.network_id);

                                    if(lastAction == null) {
                                        db.actionDao().insert(action);
                                    }else if(lastAction.connected) {
                                        return;
                                    }
                                    else if(!lastAction.connected) {
                                        db.actionDao().insert(action);
                                    }
                                }
                                public Runnable init(Action _action){
                                    this.action = _action;
                                    return(this);
                                }
                            }.init( new Action(_network_id, isConnected, milis)  ));
                        }
                    }

                }else{
                    if (milis - lastDc < 3000){
                        //stabilizacja
                        return;
                    }else{
                        lastDc = milis;
                        if(routerList.size()>0 ) {
                            // dodawanie do bazy rekordow action z flaga false
                            AsyncTask.execute(new Runnable() {
                                Boolean isConnected;
                                long milis;
                                @Override
                                public void run() {
                                    Action[] lastConnected = db.actionDao().getLastConnected();

                                    if(lastConnected.length > 0){
                                        db.actionDao().insert(new Action(lastConnected[0].network_id, isConnected, milis));
                                    }
                                }
                                public Runnable init(Boolean _isConnected, long _milis){
                                    this.isConnected = _isConnected;
                                    this.milis = _milis;
                                    return(this);
                                }
                            }.init( isConnected, milis));
                        }
                    }
                }
            }
        });

    }

}