package plan2;

/**
 * Created by zmc on 2017/7/14.
 */

/**
 * 回调任务接口
 */
public interface MTAsyncTaskListener {
    /**
     *
     * @param task 表示刚刚完成的任务的实例
     */
    void onAsyncTaskFinish(MTAsyncTask<?,?,?> task);
}
