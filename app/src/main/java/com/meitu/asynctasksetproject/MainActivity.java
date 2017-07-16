package com.meitu.asynctasksetproject;

import android.content.Intent;
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
    MyTask1 PP = new MyTask1("PP");//用于接受参数设置

    plan2.MTAsyncTaskSet<String,String,String> taskSet1 = new MTAsyncTaskSet<>("tarkSet1");
    plan2.MTAsyncTaskSet<String,String,String> taskSet2 = new MTAsyncTaskSet<>("taskSet2");
    plan2.MTAsyncTaskSet<String,String,String> taskSet = new MTAsyncTaskSet<>("taskSet");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //进行AWith1限制
        test6();
    }

    void test1() {
        taskSet1.run(AA).before(BB).after(CC).with(Awith1).with(Awith2);
    }

    void test2(){//先执行C，然后AA，AWith1，Awith2并行，都执行完后执行B
        test1();
        taskSet1.run(Awith1).before(BB);
        taskSet1.run(Awith2).before(BB);
    }

    void test3(){
        test2();
        taskSet1.run(Awith1).after(BB);//逻辑错误，和test相反，出现相距依赖，闭环问题，造成只能执行c
    }

    void test4(){
        taskSet1.run(null);
        taskSet1.run(Awith1).after(null);

    }

    /**
     * 嵌套测试
     */
    public void test5(){
        taskSet1.run(AA).before(BB);
        taskSet2.run(CC);
        taskSet.run(taskSet2).before(taskSet1);
        taskSet.start();
    }

    /**
     * 参数传递测试
     */

    public void test6(){
        taskSet1.run(AA).before(PP);
        taskSet1.start();
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
            Intent intent = getTaskIntent();
            intent.putExtra("MyTask","参数");
            return "";
        }
    }

    class MyTask1 extends MTAsyncTask<String, String, String> {

        public MyTask1(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {
            Intent intent = getTaskIntent();
            Log.d("MyTask1","接受参数为" + intent.getStringExtra("MyTask"));

            if (params != null && params.length >= 1) {
                Log.d("任务在运行中", "" + params[0]);
            }
            return "";
        }
    }


}



