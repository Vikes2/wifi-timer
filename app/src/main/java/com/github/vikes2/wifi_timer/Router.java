package com.github.vikes2.wifi_timer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Router {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name= "name")
    public String name;

    @ColumnInfo(name = "mac")
    public String mac;

    public Router(){}

    public Router(String _name, String _mac){
        this.name = _name;
        this.mac = _mac;
    }

    public String getName(){
        return name;
    }

    public String getMac(){
        return mac;
    }
}

