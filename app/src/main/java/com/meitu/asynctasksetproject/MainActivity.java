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
        AsyncTaskManager asyncTaskManager = new AsyncTaskManager();
        AsyncTaskSet taskSet1 = new AsyncTaskSet(){
            @Override
            public void executeTasks() {
                for (AsyncTask<?,?,?> task:getTaskList()){
                    ((AsyncTask<String,Integer,String>)(task)).executeOnExecutor(AsyncTaskManager.executor,"task1");
                }
            }
        };
        AsyncTaskSet taskSet2 = new AsyncTaskSet() {
            @Override
            public void executeTasks() {
                for (AsyncTask<?,?,?> task:getTaskList()){
                    ((AsyncTask<String,Integer,String>)(task)).executeOnExecutor(AsyncTaskManager.executor,"task2");
                }
            }
        };
        for(int i=0;i<100;i++){
            taskSet1.addTask(new MyTask());
        }

        for (int i=0;i<100;i++){
            taskSet2.addTask(new MyTask());
        }
        /*taskSet1.addTask(new MyTask1());
        taskSet1.addTask(new MyTask1());
        taskSet1.addTask(new MyTask1());
        taskSet1.addTask(new MyTask1());
        taskSet1.addTask(new MyTask1());

        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask2());*/
        asyncTaskManager.execute(taskSet1);
        asyncTaskManager.execute(taskSet2);
        //new MyTask3().execute();

    }




    private class MyTask extends AsyncTask<String,Integer,String>{

        @Override
        protected String doInBackground(String... params) {
            if (params!=null) {
                Log.d("AsyncTask",  params[0] + "__doing in background");
                return params[0];
            }else {
                return "null";
            }
        }

        @Override
        protected void onPostExecute(String integer) {
            super.onPostExecute(integer);
            Log.d("AsyncTask", "task" + integer + "__finished");
        }
    }
}



