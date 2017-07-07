package com.meitu.asynctasksetproject;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zmc on 2017/6/7.
 */

public   class AsyncTaskSet {
    private List<ParallelSerialTask<?,?,?>> taskList;

    public AsyncTaskSet() {
        this.taskList = new ArrayList<>();
    }

    public void addTask(ParallelSerialTask<?,?,?> task){
        taskList.add(task);
    }

    public List<ParallelSerialTask<?,?,?>> getTaskList() {
        return taskList;
    }

    public int getSize(){
        return taskList.size();
    }


  /*  *//*
    因为有可能出现不同的参数的AsyncTask,使用抽象方法让使用者去实现
     *//*
    public abstract void executeTasks();
*/
}
