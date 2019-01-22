package com.github.lukaszmalyszko.wifi_timer;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Router.class, Action.class}, version = 1)
public abstract class RouterDatabase extends RoomDatabase {
    public abstract RouterDao routerDao();
    public abstract ActionDao actionDao();

}
