package plan2;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by zmc on 2017/7/14.
 */
public abstract class MTAsyncTask<Params,Progress,Result> extends AsyncTask<Params,Progress,Result> {
    private Params[] mParamses;
    private Executor mMtExcutor;

    {
        mMtExcutor = new MTExecutor();
    }
    public void setAsyncTaskListener(MTAsyncTaskListener mListener) {
        this.mListener = mListener;
    }

    MTAsyncTaskListener mListener;

    public MTAsyncTask(Params... params) {
        mParamses = params;
    }

    public void start(){
        executeOnExecutor(mMtExcutor,mParamses);//
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
