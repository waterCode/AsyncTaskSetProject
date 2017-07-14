package plan2;

/**
 * Created by zmc on 2017/7/14.
 */

public interface MTAsyncTaskListener {
    void onAsyncTaskFinish(MTAsyncTask<?,?,?> task);
}
