package com.meitu.asynctasksetproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import plan2.MTAsyncTask;
import plan2.MTAsyncTaskSet;

public class MainActivity extends AppCompatActivity {

    MyTask AA = new MyTask("AA");
    MyTask Awith1 = new MyTask("Awith1");
    MyTask Awith2 = new MyTask("Awith2");
    MyTask BB = new MyTask("BB");
    MyTask CC = new MyTask("CC");
    plan2.MTAsyncTaskSet taskSet = new MTAsyncTaskSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //进行AWith1限制
        test4();
        taskSet.start();
    }

    void test1() {
        taskSet.run(AA).before(BB).after(CC).with(Awith1).with(Awith2);
    }

    void test2(){//先执行C，然后AA，AWith1，Awith2并行，都执行完后执行B
        test1();
        taskSet.run(Awith1).before(BB);
        taskSet.run(Awith2).before(BB);
    }

    void test3(){
        test2();
        taskSet.run(Awith1).after(BB);//逻辑错误，和test相反，出现相距依赖，闭环问题，造成只能执行c
    }

    void test4(){
        taskSet.run(null);
        taskSet.run(Awith1).after(null);

    }


    class MyTask extends MTAsyncTask<String, String, String> {

        public MyTask(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {
            if (params != null && params.length >= 1) {
                Log.d("任务在运行中", "" + params[0]);
            }
            return "";
        }
    }


}



