package com.meitu.asynctasksetproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.Voice;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import plan2.*;
import plan2.MTAsyncTaskSet;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plan2.MTAsyncTaskSet taskSet= new MTAsyncTaskSet();
        MyTask AA = new MyTask("AA");
        MyTask Awith1= new MyTask("Awith1");
        MyTask Awith2 = new MyTask("Awith2");
        MyTask BB = new MyTask("BB");
        MyTask CC = new MyTask("CC");


        taskSet.run(AA).before(BB).after(CC).with(Awith1).with(Awith2);

        //进行AWith1限制
        taskSet.run(Awith1).before(BB);
        taskSet.start();

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
        asyncTaskManager.execute(taskSet2);*/

    }

    class MyTask extends MTAsyncTask<String,String,String>{

        public MyTask(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {
            if(params!=null&&params.length>=1) {
                Log.d("taskName", ""+params[0]);
            }
            return "";
        }
    }




}



