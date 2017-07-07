package com.meitu.asynctasksetproject;

import android.os.AsyncTask;

/**
 * Created by zmc on 2017/7/4.
 */

public abstract class ParallelSerialTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
       private Params[] mParams;

    public ParallelSerialTask(Params... params ) {
        mParams=params;
    }

    public void execute(){
        executeOnExecutor(AsyncTaskManager.executor,mParams);
    }

}
