package com.github.lukaszmalyszko.wifi_timer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(entity = Router.class,
                                    parentColumns = "networkId",
                                    childColumns = "network_id",
                                    onDelete = ForeignKey.CASCADE
                                    ))
public class Action {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="network_id")
    public String network_id;

    @ColumnInfo(name= "connected")
    public Boolean connected;

    @ColumnInfo(name= "time")
    public long time;

    public Action() {}

    public Action(String _network_id, Boolean _connected, long _time){
        this.network_id = _network_id;
        this.connected = _connected;
        this.time = _time;
    }

}




