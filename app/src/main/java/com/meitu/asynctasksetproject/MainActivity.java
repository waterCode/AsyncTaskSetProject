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
        AsyncTaskSet taskSet1 = new AsyncTaskSet();
        AsyncTaskSet taskSet2 = new AsyncTaskSet();
        taskSet1.addTask(new MyTask1());
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
        taskSet2.addTask(new MyTask2());
        taskSet2.addTask(new MyTask3());
        asyncTaskManager.execute(taskSet1);
        asyncTaskManager.execute(taskSet2);
        //new MyTask3().execute();

    }


    class MyTask1 extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("MyTask", "params:111");
            return null;
        }
    }

    class MyTask2 extends AsyncTask {


        @Override
        protected Void doInBackground(Object[] params) {
            Log.d("MyTask", "params:222");
            return null;
        }
    }

    class MyTask3 extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            Log.d("MyTask3","");
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
}



