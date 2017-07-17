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
    TaskBB BB = new TaskBB("BB");
    TaskCC CC = new TaskCC("CC");
    TaskEE EE = new TaskEE("EE");

    plan2.MTAsyncTaskSet<String, String, String> taskSet1 = new MTAsyncTaskSet<>("taskSet1");
    plan2.MTAsyncTaskSet<String, String, String> taskSet2 = new MTAsyncTaskSet<>("taskSet2");
    plan2.MTAsyncTaskSet<String, String, String> taskSet = new MTAsyncTaskSet<>("taskSet");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        test13();
    }

    void test1() {
        taskSet1.run(AA).before(BB).after(CC).with(EE);
        taskSet1.start();
    }



    void test2() {

        taskSet1.run(AA).after(BB);//逻辑错误，和test相反，出现相距依赖，闭环问题，导致不会执行
        taskSet1.run(BB).after(AA);
        taskSet1.start();
    }

    void test4() {//空指针测试
        taskSet1.run(null);
        taskSet1.run(Awith1).after(null);
        taskSet1.start();

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
     * 多任务并行测试
     */
    public void test8(){
        taskSet1.run(AA).with(BB).with(CC).after(EE);
        taskSet1.start();
    }

    /**
     * 多任务串行after测试
     */
    public void test9(){
        taskSet1.run(AA).after(BB);
        taskSet1.run(BB).after(CC);
        taskSet1.run(CC).after(EE);
        taskSet1.start();
    }

    /**
     * 多任务串行before测试
     */
    public void test10(){
        taskSet1.run(AA).before(BB);
        taskSet1.run(BB).before(CC);
        taskSet1.run(CC).before(EE);
        taskSet1.start();
    }





    /**
     * 并行任务参数传递
     */
    public void test11() {
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

    /**
     * 依赖参数错误
     */
    public void test12() {
        taskSet1.run(AA).after(BB).before(CC);//执行顺序为B，A，C
        taskSet1.run(AA).from(CC).map(new ResultMap<Object, String>() {//却让A去依赖C，导致A不能执行，直接在获取参数那里挂了，


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


    /**
     * 串行参数传递
     */
    public void test13(){
        taskSet1.run(BB).after(AA).from(AA).map(new ResultMap<Object, String>() {

            @Override
            public String[] map(Object[] result) {
                String[] s = new String[result.length];
                for (int i = 0; i < result.length; i++) {
                    s[i] = (String) result[i];
                }
                return s;
            }
        });
        taskSet1.run(CC).after(BB).from(BB).map(new ResultMap<Object, String>() {

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

        public TaskAA(String name ) {
            super(name);
        }

        @Override
        protected String doInBackground(String... params) {
            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskAA", " do InBackGround:获得其他任务结果作为执行参数有: " + s);


            return "A的结果";
        }
    }

    class TaskBB extends MTAsyncTask<String, String, String> {

        public TaskBB(String name) {
            super(name);
        }

        @Override
        protected String doInBackground(String... params) {

            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskBB", " do InBackGround:获得其他任务结果作为执行参数有: " + s);
            return "B的结果";
        }
    }

    class TaskCC extends MTAsyncTask<String, String, String> {

        public TaskCC(String name) {
            super(name);
        }

        @Override
        protected String doInBackground(String... params) {
            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }

            Log.d("TaskCC", " do InBackGround:获得其他任务结果作为执行参数有: " + s);
            return "C任务的结果";
        }
    }

    class TaskEE extends MTAsyncTask<String, String, String> {

        public TaskEE(String name) {
            super(name);
        }

        @Override
        protected String doInBackground(String... params) {

            String s = "";
            if (params != null) {
                for (String temp : params) {
                    s = s +" + "+ temp;
                }
            }else {
                s+="无";
            }

            Log.d("TaskEE", " do InBackGround:获得其他任务结果作为执行参数有: " + s);
            return "";
        }
    }


}



