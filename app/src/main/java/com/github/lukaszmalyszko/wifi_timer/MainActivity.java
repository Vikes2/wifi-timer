package com.github.lukaszmalyszko.wifi_timer;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        db = Room.databaseBuilder(getApplicationContext(),
                RouterDatabase.class, "database-name").build();

        mRecyclerView = findViewById(R.id.statsRecycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new StatsAdapter(routerList, actionList, processTimeData(routerList, actionList));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        //uruchamianie sevice do nasluchiwania routerow
        scheduleJob();
    }

    // create toolbar favourite
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    //obsluga favourite
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent myIntent = new Intent(this, FavouriteActivity.class);
                startActivityForResult(myIntent, 1);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //Starting another activity doesn't have to be one-way. You can also start another activity and receive a result back. To receive a result,
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                processData();
            }
        }
    }


    private void processData() {
        if(routerList != null && routerList.size() > 0 ) {
            // przeliczanie czasu konkretnego router (dict ma nazwe oraz time)
            mTimeData = processTimeData(routerList, actionList);
            // uzupelnienie czasu w adapterze
            mAdapter.mData = mTimeData;

            // wskazanie zmiany (odswiezenie adaptera)
            mAdapter.notifyDataSetChanged();
        }
    }

    // params: lista obserwowanych routerow oraz wszystkie actiony
    public HashMap<String, String> processTimeData(ArrayList<Router> mRouterList, ArrayList<Action> mActionList){
        HashMap<String, Long> resultMap = new HashMap<>();

        for(Router router : mRouterList){
            //uzupelnienie hashmapy zerami
            resultMap.put(router.networkId, 0l);
        }
        // jesli sa dane akcje
        if(mActionList != null && mActionList.size() > 0){

            String last_connection_id = "0";
            long last_connection_time = 0;
            Boolean isEmpty = true;
            for(Action action : mActionList){
                if(action.connected == true && isEmpty){
                    // zapisanie czasu dla danego urzadzenia
                    last_connection_id = action.network_id;
                    last_connection_time = action.time;
                    // czekanie na sygnal z falsem(disconnected)
                    isEmpty = false;
                }else if(action.connected == false ){
                    //sprawdzanie czy byl wczesniej true z connect
                    if(!isEmpty){
                        //odejmowanie od false true i przypisanie do zmiennych
                        long currentTime = resultMap.get(last_connection_id);
                        long toAddTime = action.time - last_connection_time;

                        for(Router curRouter : mRouterList){
                            //przejscie po routerach zeby sprawdzic czy mamy go w ulubionych
                            if(curRouter.networkId.equals(last_connection_id)){
                                //dodanie koncowego czasu do poprzedniego z listy
                                resultMap.put(last_connection_id, currentTime + toAddTime);
                            }
                        }
                    }
                    //oczekiwanie na nastepnego true(connect)
                    isEmpty = true;
                }
            }
            if(mActionList.size()>0){
                Action  lastAction = mActionList.get( mActionList.size() - 1 );
                // sprawdzenie czy ostatni action jest true(connect)
                if( lastAction.connected == true){
                    long now = Calendar.getInstance().getTimeInMillis();
                    long toAddTime = now - lastAction.time;
                    long currentTime = resultMap.get(lastAction.network_id);
                    // jezeli tak to od aktualnego czasu(z calendar) odejmujemy ostatniego true i dodajemy go do całego wyniku
                    resultMap.put(lastAction.network_id, currentTime + toAddTime);
                }
            }
        }
        //zmiana na stringa w celu latwiejszego wyswietlania(lepszy interface uzytkownika)
        HashMap<String, String> resultStringMap = new HashMap<>();
        for(String currentKey : resultMap.keySet()){
            // konwertowanie ms na czas używany defaultowo przez uzytkownikow
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

        String dayStr = getResources().getQuantityString(R.plurals.day, day, day);

        if(day> 0){
            out = dayStr +" "+ hour +" "+ getString(R.string.hour)+ " " + min + " " + getString(R.string.minutes);
        }else if(hour > 0){
            out = hour +" "+ getString(R.string.hour)+ " " + min + " " + getString(R.string.minutes) + " " + sec +" "+ getString(R.string.seconds);
        }else if(min > 0){
            out = min + " " + getString(R.string.minutes) + " " + sec +" "+ getString(R.string.seconds);
        }else{
            out = sec +" "+ getString(R.string.seconds);
        }
        return out;
    }

    //obserwator do ponownego uruchamiania processData(przy ponownym uruchomieniu lub zmianie w bazie danych)
    @Override
    public void onResume() {
        db.routerDao().getAll().observe(this, new Observer<List<Router>>() {
            @Override
            public void onChanged(@Nullable List<Router> routers) {
                if (!routerList.isEmpty()) {
                    routerList.clear();
                }
                routerList.addAll(routers);

                Log.d("wifi-timer", "MainActivity.onCreate(): Pobrano routery w ilości: " + routers.size());

                db.actionDao().getAll().observe(MainActivity.this, new Observer<List<Action>>() {
                    @Override
                    public void onChanged(@Nullable List<Action> actions) {
                        if (!actionList.isEmpty()) {
                            actionList.clear();
                        }
                        actionList.addAll(actions);
                        processData();

                        Log.d("wifi-timer", "MainActivity.onCreate(): Pobrano akcje w ilości: " + actions.size());
                    }
                });
            }
        });
        super.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                //unmetered, bo wifi domyslnie jest niepoliczalna(transmisja)
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

}
