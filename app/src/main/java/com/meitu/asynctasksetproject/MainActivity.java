package com.meitu.asynctasksetproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import plan2.MTAsyncTask;
import plan2.MTAsyncTaskSet;
import plan2.ResultMap;

public class MainActivity extends AppCompatActivity {

    TaskAA AA = new TaskAA("AA");//网络

    TaskAA Awith1 = new TaskAA("Awith1");
    TaskAA Awith2 = new TaskAA("Awith2");
    TaskBB BB = new TaskBB("BB");
    TaskCC CC = new TaskCC("CC");
    TaskBB PP = new TaskBB("PP");//用于接受参数设置
    TaskEE EE = new TaskEE("EE");

    plan2.MTAsyncTaskSet<String, String, String> taskSet1 = new MTAsyncTaskSet<>("taskSet1");
    plan2.MTAsyncTaskSet<String, String, String> taskSet2 = new MTAsyncTaskSet<>("taskSet2");
    plan2.MTAsyncTaskSet<String, String, String> taskSet = new MTAsyncTaskSet<>("taskSet");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //进行AWith1限制
        test8();
    }

    void test1() {
        taskSet1.run(AA).before(BB).after(CC).with(Awith1).with(Awith2);
    }

    void test2() {//先执行C，然后AA，AWith1，Awith2并行，都执行完后执行B
        test1();
        taskSet1.run(Awith1).before(BB);
        taskSet1.run(Awith2).before(BB);
    }

    void test3() {
        test2();
        taskSet1.run(Awith1).after(BB);//逻辑错误，和test相反，出现相距依赖，闭环问题，造成只能执行c
    }

    void test4() {
        taskSet1.run(null);
        taskSet1.run(Awith1).after(null);

    }

    /**
     * 嵌套测试
     */
    public void test5() {
        taskSet1.run(AA).before(BB);
        taskSet2.run(CC);
        taskSet.run(taskSet2).before(taskSet1);
        taskSet.start();
    }


    /**
     * 多层嵌套
     */
    public void test7() {
        taskSet1.run(AA);
        taskSet2.run(taskSet1).after(BB);
        taskSet.run(taskSet2).before(CC);
        taskSet.start();
    }

    /**
     * 参数传递测试
     */

    public void test6() {
        taskSet1.run(AA).before(PP);
        taskSet1.start();
    }


    /**
     * 并行任务参数传递
     */
    public void test8() {
        taskSet1.run(AA).with(BB).before(CC);
        taskSet1.run(EE).after(CC).from(AA,BB,CC).map(new ResultMap<Object, String>() {


            @Override
            public String[] map(Object[] result) {
                String[] s = new String[result.length];
                for (int i = 0; i < result.length; i++) {
                    s[i] = (String) result[i];
                }
                return s;
            }
        });

        taskSet1.start();
    }


    class TaskAA extends MTAsyncTask<String, String, String> {

        public TaskAA(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {
            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskAA", "获得其他任务结果作为执行参数有: " + s);


            return "A的结果";
        }
    }

    class TaskBB extends MTAsyncTask<String, String, String> {

        public TaskBB(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {

            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskAA", "获得其他任务结果作为执行参数有: " + s);
            return "B的结果";
        }
    }

    class TaskCC extends MTAsyncTask<String, String, String> {

        public TaskCC(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {
            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskAA", "获得其他任务结果作为执行参数有: " + s);
            return "C任务的结果";
        }
    }

    class TaskEE extends MTAsyncTask<String, String, String> {

        public TaskEE(String... params) {
            super(params);
        }

        @Override
        protected String doInBackground(String... params) {

            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskAA", "获得其他任务结果作为执行参数有: " + s);
            return "";
        }
    }


}



