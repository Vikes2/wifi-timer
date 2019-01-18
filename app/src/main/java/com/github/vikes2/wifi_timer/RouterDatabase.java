package com.github.vikes2.wifi_timer;


import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

@Database(entities = {Router.class, Action.class}, version = 1)
public abstract class RouterDatabase extends RoomDatabase {
    public abstract RouterDao routerDao();
    public abstract ActionDao actionDao();

}
