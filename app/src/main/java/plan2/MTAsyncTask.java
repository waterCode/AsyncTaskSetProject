package plan2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.Executor;

/**
 * Created by zmc on 2017/7/14.
 */
public abstract class MTAsyncTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result> {
    private static final String TAG="MTAsyncTask";
    private Params[] mParamses;
    private Executor mMtExcutor;
    private MTAsyncTaskListener mListener;


    {
        mMtExcutor = new MTExecutor();
    }
    public void setAsyncTaskListener(MTAsyncTaskListener mListener) {
        this.mListener = mListener;
    }



    public MTAsyncTask(Params... params) {
        mParamses = params;
    }

    /**
     *
     * @param resultParams 依赖任务的结果参数
     */
    public void startTask(Object[] resultParams){
        if(mParamses!=null&&mParamses.length>=1) {
            Log.d(TAG, "开始运行" + mParamses[0]);
        }
        //处理结果参数
        mParamses = (Params[]) resultParams;
        // TODO: 2017/7/17 这里应该怎么判断参数类型 
        executeOnExecutor(mMtExcutor,mParamses);//进行强制转换
    }



    private class MTExecutor implements Executor {

        @Override
        public void execute(@NonNull final Runnable command) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        command.run();
                    }finally {
                        if(mListener != null){
                            mListener.onAsyncTaskFinish(MTAsyncTask.this);//将此任务回传
                        }
                    }
                }
            };
            AsyncTask.THREAD_POOL_EXECUTOR.execute(task);
        }
    }

}
