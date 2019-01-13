package com.github.vikes2.wifi_timer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RouterDao {
    @Query("SELECT * FROM router")
    LiveData<List<Router>> getAll();

    @Query("SELECT * FROM router WHERE name LIKE :routerName")
    LiveData<List<Router>> searchRouters(String routerName);

    @Insert
    void insert(Router router);

    @Update
    void update(Router router);

    @Delete
    void delete(Router router);

}
