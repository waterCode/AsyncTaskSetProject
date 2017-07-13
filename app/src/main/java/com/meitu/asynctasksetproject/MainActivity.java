package com.meitu.asynctasksetproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyTask task_A = new MyTask("taskAAAA");
        MyTask task_A1 = new MyTask("taskAAAA111");
        MyTask task_B = new MyTask("taskBBBB");
        MyTask task_B1 = new MyTask("taskBBB11");
        MyTask task_B2 = new MyTask("taskBBBB222");
        MyTask task_B3 = new MyTask("taskBBBB333");
        MyTask task_C = new MyTask("taskCCCC");
        MyTask task_D = new MyTask("taskDDDD");
        MTAsyncTaskSet mtAsyncTaskSet = new MTAsyncTaskSet();
        mtAsyncTaskSet.run(task_B).with(task_C).with(task_B1).with(task_B2).with(task_B3).after(task_A).before(task_D);
        mtAsyncTaskSet.run(task_B).after(task_A1);
        mtAsyncTaskSet.start();


        /*AsyncTaskManager asyncTaskManager = new AsyncTaskManager();
        AsyncTaskSet taskSet1 = new AsyncTaskSet();
        AsyncTaskSet taskSet2 = new AsyncTaskSet() ;
        for(int i=0;i<10;i++){
            taskSet1.addTask(new MyTask("task1"));
        }

        for (int i=0;i<100;i++){
            taskSet2.addTask(new MyTask("task2"));
        }

        asyncTaskManager.execute(taskSet1);
        asyncTaskManager.execute(taskSet2);

    }




    private class MyTask extends ParallelSerialTask<String,Integer,String>{


        public MyTask(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {
            if (params!=null) {
                Log.d("AsyncTask",  params[0] + "__doing in background");
                Log.d("AsyncTask",  params[0] + "__finished");
                return params[0];
            }else {
                return "null";
            }
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);

        }
    }
}



