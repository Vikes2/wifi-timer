package com.github.vikes2.wifi_timer;

public class RouterItem {
    private String mName;
    private String mMac;

    public RouterItem(String _name, String _mac){
        mName = _name;
        mMac = _mac;
    }

    public String getName(){
        return mName;
    }

    public String getMac(){
        return mMac;
    }
}
