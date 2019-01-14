package com.github.vikes2.wifi_timer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WifiWorker extends Worker {

    public WifiWorker(@NonNull Context context,
                      @NonNull WorkerParameters params){
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.d("pawelski", "affect send");
        return Result.success();
    }
}
