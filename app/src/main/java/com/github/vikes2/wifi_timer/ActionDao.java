package com.github.vikes2.wifi_timer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ActionDao {
    @Query("SELECT * FROM `action`")
    LiveData<List<Action>> getAll();



    @Insert
    void insert(Action action);

    @Update
    void update(Action action);

    @Delete
    void delete(Action action);

}
