package com.meitu.asynctasksetproject;

import java.util.LinkedList;

/**
 * Created by zmc on 2017/7/13.
 */

public class MTAsyncTaskSet {
    public static AsyncTaskManager mAsyncTaskManager = new AsyncTaskManager();

    LinkedList<AsyncTaskSet> mTaskSetList = new LinkedList<>();


    private AsyncTaskSet mRunTaskSet;//run指定的任务
    private int mRunTaskIndex;


    public MTAsyncTaskSet run(ParallelSerialTask<?, ?, ?> task) {
        int index;
        index = indexof(task);
        if (index == -1) {
            mRunTaskSet = new AsyncTaskSet();
            mRunTaskSet.addTask(task);
            mTaskSetList.add(mRunTaskSet);
            mRunTaskIndex = mRunTaskSet.getSize() - 1;//设置index为最后的下表
        } else {
            mRunTaskSet = mTaskSetList.get(index);
            mRunTaskIndex = index;//设置下表
        }
        return this;
    }

    private int indexof(ParallelSerialTask<?, ?, ?> task) {
        if (mTaskSetList !=null) {
            for (int i = 0; i < mTaskSetList.size(); i++) {
                if (mTaskSetList.get(i).getTaskList().contains(task)) {
                    return i;//有就return回这个index
                }
            }
        }
        return -1;
    }


    public MTAsyncTaskSet with(ParallelSerialTask<?, ?, ?> task) {
        if (mRunTaskSet != null) {
            mRunTaskSet.addTask(task);
            return this;
        } else {
            return null;
        }
    }

    public MTAsyncTaskSet after(ParallelSerialTask<?, ?, ?> task) {
        if(mRunTaskSet == null ||task ==null)
            return null;//目标不存在
        int beforeIndex = mRunTaskIndex-1;
        if(beforeIndex<0){//表示前面没有元素
            AsyncTaskSet set = new AsyncTaskSet();
            set.addTask(task);//添加进去
            mTaskSetList.add(0,set);//添加到最开头
        }else {
            AsyncTaskSet set = mTaskSetList.get(beforeIndex);//拿到前面的set
            set.addTask(task);
        }
        return this;
    }


    public MTAsyncTaskSet before(ParallelSerialTask<?, ?, ?> task) {
        if(mRunTaskSet == null ||task ==null)
            return null;//目标不存在
        int afterIndex = mRunTaskIndex+1;
        if(afterIndex>=mTaskSetList.size()){//表示后面没有元素
            AsyncTaskSet set = new AsyncTaskSet();
            set.addTask(task);//添加进去
            mTaskSetList.add(set);//添加到最开头
        }else {
            AsyncTaskSet set = mTaskSetList.get(afterIndex);//拿到前面的set
            set.addTask(task);
        }
        return this;
    }

    public void start() {
        for(AsyncTaskSet taskSet:mTaskSetList){
            mAsyncTaskManager.execute(taskSet);
        }

    }


}
