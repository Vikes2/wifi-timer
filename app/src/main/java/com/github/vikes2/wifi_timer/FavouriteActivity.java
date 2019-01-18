package com.github.vikes2.wifi_timer;

import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity implements AddDialogFragment.AddDialogListener, EditDialogFragment.EditDialogListener {
    private RouterDatabase db;
    private ArrayList<Router> routerList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RouterAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText name = dialog.getDialog().findViewById(R.id.wifiName);
        EditText mac = dialog.getDialog().findViewById(R.id.mac);
        String nameText = name.getText().toString();
        String macText = mac.getText().toString();

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
    }

    @Override
    public void onEditDialogPositiveClick(DialogFragment dialog) {
        EditText name = dialog.getDialog().findViewById(R.id.wifiName);
        EditText mac = dialog.getDialog().findViewById(R.id.mac);
        String nameText = name.getText().toString();
        String macText = mac.getText().toString();
        int position = ((EditDialogFragment)dialog).position;
        Router router = routerList.get(position);
        router.name = nameText;
        router.mac = macText;

        AsyncTask.execute(new Runnable() {
            Router router;
            @Override
            public void run() {
                db.routerDao().update(router);
            }
            public Runnable init(Router _router){
                this.router = _router;
                return(this);
            }
        }.init(router));
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.getDialog().cancel();
    }

    private void addAdapterListeners(){
        mAdapter.setOnItemClickListener(new RouterAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                DialogFragment dialog = new EditDialogFragment();
                Bundle b = new Bundle();
                b.putString("name", routerList.get(position).name);
                b.putString("mac", routerList.get(position).mac);
                b.putInt("position", position);
                dialog.setArguments(b);
                dialog.show(getSupportFragmentManager(), "EditDialogFragment");
            }

            @Override
            public void onDeleteClick(final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FavouriteActivity.this);
                builder.setMessage(R.string.confirm_quest);
                builder.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncTask.execute(new Runnable() {
                            Router router;
                            RouterAdapter adapter;
                            int position;
                            @Override
                            public void run() {
                                db.routerDao().delete(router);
                                adapter.notifyItemRemoved(position);
                            }
                            public Runnable init(Router _router, RouterAdapter _adapter, int _position){
                                this.router = _router;
                                this.adapter = _adapter;
                                this.position = _position;
                                return(this);
                            }
                        }.init(routerList.get(position), mAdapter, position));
                    }
                });
                builder.setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
        addAdapterListeners();
    }
}
