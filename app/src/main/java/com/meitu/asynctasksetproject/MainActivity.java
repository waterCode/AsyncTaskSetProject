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
                    task.executeOnExecutor(AsyncTaskManager.executor);
                }
            }
        };
        AsyncTaskSet taskSet2 = new AsyncTaskSet() {
            @Override
            public void executeTasks() {
                for (int i=0;i<getTaskList().size();i++){
                    if(i==8||i==9){
                        ( (AsyncTask<Integer,Integer,Integer>)(getTaskList().get(i))).executeOnExecutor(AsyncTaskManager.executor,0);
                    }else {
                        getTaskList().get(i).executeOnExecutor(AsyncTaskManager.executor);
                    }

                }
            }
        };
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
        taskSet2.addTask(new MyTask3());
        asyncTaskManager.execute(taskSet1);
        asyncTaskManager.execute(taskSet2);
        //new MyTask3().execute();

    }


    private class MyTask1 extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] params) {
            Log.d("MyTask1", "task1 doing in background");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("MyTask", "task1 finished");
        }
    }

    private class MyTask2 extends AsyncTask {


        @Override
        protected Void doInBackground(Object[] params) {
            Log.d("MyTask2", "task2 doing in background");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("MyTask2", "task2 finished");
        }
    }

    private class MyTask3 extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... params) {
            Log.d("MyTask3","task3 doing in background");
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
}



