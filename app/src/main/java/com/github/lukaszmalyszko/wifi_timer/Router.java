package com.github.lukaszmalyszko.wifi_timer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Router {


    @ColumnInfo(name= "name")
    public String name;

    @NonNull
    @PrimaryKey()
    public String networkId;

    public Router(){}

    public Router(String _name, String _networkId){
        this.name = _name;
        this.networkId = _networkId;
    }

    public String getName(){
        return name;
    }

    public String getNetworkId(){
        return networkId;
    }
}

