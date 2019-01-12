package com.github.vikes2.wifi_timer;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity implements AddDialogFragment.AddDialogListener {
    private RouterDatabase db;
    private ArrayList<Router> routerList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
//        EditText name = findViewById(R.id.wifiName);
//        EditText mac = findViewById(R.id.mac);

        EditText name = dialog.getDialog().findViewById(R.id.wifiName);
        EditText mac = dialog.getDialog().findViewById(R.id.mac);

        String nameText = name.getText().toString();
        String macText = mac.getText().toString();

        Toast.makeText(this, nameText , Toast.LENGTH_SHORT).show();

        AsyncTask.execute(new Runnable() {
            Router router;
            @Override
            public void run() {
                db.routerDao().insert(router);
            }
            public Runnable init(Router _router){
                this.router = _router;
                return(this);
            }
        }.init(new Router(nameText, macText)));



        for( Router el : routerList){
            Log.d("pawelski", el.name);
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button

        dialog.getDialog().cancel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = Room.databaseBuilder(getApplicationContext(),
                RouterDatabase.class, "database-name").build();

        db.routerDao().getAll().observe(this, new Observer<List<Router>>() {
            @Override
            public void onChanged(@Nullable List<Router> routers) {
                if (!routerList.isEmpty()) {
                    routerList.clear();
                }
                routerList.addAll(routers);
                mAdapter.notifyDataSetChanged();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                //AddDialogFragment dialog = new AddDialogFragment();
                DialogFragment dialog = new AddDialogFragment();
                dialog.show(getSupportFragmentManager(), "AddDialogFragment");
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new RouterAdapter(routerList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

}
