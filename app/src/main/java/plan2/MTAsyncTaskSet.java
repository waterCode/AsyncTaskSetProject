package plan2;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by zmc on 2017/7/14.
 */
public class MTAsyncTaskSet<Params, Progress, Result> extends MTAsyncTask<Params, Progress, Result> implements MTAsyncTaskListener {

    private static final String TAG = "MTAsyncTaskSet";
    private MTAsyncTask<?, ?, ?> mStartTask = new TestTask("StartTask");//最开始执行任务root节点
    private Node mStartNode = new Node(mStartTask);//root节点的包装node节点
    private Map<MTAsyncTask<?, ?, ?>, Node> mTaskMap;//AsyncTask 任务与节点的映射集合
    private ArrayList<Node> mNodeList = new ArrayList<>();//所有节点的集合
    private boolean isCreateDependencyGraph = false;//用来判断是否建立依赖图，第一次开始的时候建立一次就好


    public MTAsyncTaskSet(String name) {
        super(name);
        mTaskMap = new HashMap<>();
        mTaskMap.put(mStartTask, mStartNode);//init
        mStartTask.setAsyncTaskListener(this);
    }


    @Override
    protected Result doInBackground(Params... params) {
        start();
        Log.d(TAG, "doInBackground");
        return null;
    }

    public Builder run(MTAsyncTask<?, ?, ?> task) {
        if (task != null) {
            return new Builder(task);
        } else {
            return null;
        }
    }


    /**
     * 创建依赖树，主要是讲兄弟的付清统一，让依赖调整为树状结构
     */
    private void createDependencyGraph() {
        if (!isCreateDependencyGraph) {
            //主要是解决sibing和起始点的问题
            int size = mNodeList.size();
            for (int i = 0; i < size; i++) {
                Node node = mNodeList.get(i);

                if (node.mTaskSibingList == null) {
                    continue;
                }

                findAllSibings(node, node.mTaskSibingList);//拿到所有兄弟姐妹，父老乡亲
                node.mTaskSibingList.remove(node);//因为上一句递归函数会加入自己，然后就要去除自己

                for (int j = 0; j < node.mTaskSibingList.size(); j++) {//添加他们的父亲
                    node.addParents(node.mTaskSibingList.get(j).mTaskParentList);
                }

            }
            //没有父亲的依赖开始的节点
            for (int j = 0; j < size; j++) {
                Node node = mNodeList.get(j);
                if (node.mTaskParentList == null) {
                    node.addParent(mStartNode);
                    mStartNode.addChildren(node);
                }
            }
            isCreateDependencyGraph = true;
        }
    }

    /**
     * 递归实现找出所有的兄弟，比如A和B是兄弟，B和C是兄弟，那么A和C也是兄弟
     * @param node 所检查的节点
     * @param mTaskSibingList 存放兄弟集合
     */
    private void findAllSibings(Node node, ArrayList<Node> mTaskSibingList) {
        if (!mTaskSibingList.contains(node)) {//没有这个节点就会加入
            mTaskSibingList.add(node);
            if (node.mTaskSibingList == null) {
                return;
            }
            for (int i = 0; i < mTaskSibingList.size(); i++) {
                findAllSibings(mTaskSibingList.get(i), mTaskSibingList);
            }
        }
    }


    /**
     * 任务集合开始任务
     */
    public void start() {
        createDependencyGraph();
        start(mStartNode);
    }

    /**
     * 开始执行这个节点所包含任务
     * @param node 开始任务节点
     */
    private void start(Node node) {
        if (node != null && node.mAsyncTask != null) {
            //找到所有的所需结果参数
            ArrayList<Object> objectParams = getStartParams(node.mFromResult);//交给执行部分去检查
            node.mAsyncTask.setAsyncTaskListener(this);
            if (node.mResultMap != null) {
                if(objectParams !=null) {
                    node.mResultMap.map(objectParams.toArray());
                    node.mAsyncTask.startTask(node.mResultMap.map(objectParams.toArray()));//执行任务
                }else {
                    Log.e("MTAsycTaskSet","没有设置依赖任务,需要调用from依赖任务");
                }
            } else {

                node.mAsyncTask.startTask(null);//执行任务
            }


        }
    }

    /**
     * 获得开始任务的所有执行参数
     * @param fromResult 该任务所依赖的任务
     * @return 任务参数集合
     */
    private ArrayList<Object> getStartParams(ArrayList<AsyncTask<?, ?, ?>> fromResult) {
        ArrayList<Object> taskList = null;
        if (fromResult != null) {
            taskList = new ArrayList<>();
            for (AsyncTask<?, ?, ?> task : fromResult) {
                try {
                    taskList.add(task.get());//把所有参数添加到数组
                } catch (InterruptedException e) {
                    Log.e("MTAsynceTaskSet","所依赖任务还没完成");
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
        return taskList;
    }

    /**
     * 遍历孩子节点来决定是否开始孩子任务（根据父亲是否都执行完毕）
     * @param task 刚刚完成的任务
     */
    private void onChildrenTaskEnd(MTAsyncTask<?, ?, ?> task) {
        Node node = mTaskMap.get(task);
        ArrayList<Node> childrenList = node.mTaskChildrenList;
        if (childrenList == null) {
            return;
        }
        for (Node children : childrenList) {
            children.mParentFinished++;
            if (!children.isTaskFinished) {//表示没有完成的节点,为了防止混乱，闭环
                if (children.mParentFinished >= children.mTaskParentList.size()) {//表示父类任务完成，可以开始自己的任务
                    start(children);
                }
            }
        }

    }

    /**
     *
     * @param task 表示刚刚完成的任务的实例
     */
    @Override
    public void onAsyncTaskFinish(MTAsyncTask<?, ?, ?> task) {
        Node node = mTaskMap.get(task);
        //node.mParentFinished++;
        node.isTaskFinished = true;
        onChildrenTaskEnd(task);
    }


    /**
     * 任务编辑器，通过run方法返回这个类，然后可以调用before和after等编辑任务
     */
    public class Builder {
        private Node mCurrentNode;

        private Builder(MTAsyncTask<?, ?, ?> task) {
            mCurrentNode = mTaskMap.get(task);
            if (mCurrentNode == null) {//if don't exist
                mCurrentNode = new Node(task);
                mTaskMap.put(task, mCurrentNode);//add to map
                mNodeList.add(mCurrentNode);
            }
        }

        public Builder from(MTAsyncTask<?, ?, ?>... task) {
            if (task != null) {
                for (MTAsyncTask<?, ?, ?> tempTask : task) {
                    Node node = mTaskMap.get(tempTask);
                    if (node == null) {//节点不存在则添加节点
                        node = new Node(tempTask);
                        mTaskMap.put(tempTask, node);
                        mNodeList.add(node);
                    }
                    mCurrentNode.addFromResult(tempTask);
                }
            }
            return this;
        }

        /**
         * 获得结果参数的映射
         * @param map 映射实例
         * @return Builder编辑器
         */
        public Builder map(ResultMap map) {
            if (map != null) {
                mCurrentNode.addResultMap(map);
            }
            return this;
        }

        /**
         *
         * @param task 并行的任务
         * @return Builder编辑器
         */
        public Builder with(MTAsyncTask<?, ?, ?> task) {
            Node node = mTaskMap.get(task);
            if (node == null) {
                node = new Node(task);
                mTaskMap.put(task, node);
                mNodeList.add(node);
            }
            mCurrentNode.addSibing(node);
            return this;
        }

        public Builder before(MTAsyncTask<?, ?, ?> task) {
            Node node = mTaskMap.get(task);
            if (node == null) {
                node = new Node(task);
                mTaskMap.put(task, node);
                mNodeList.add(node);
            }
            mCurrentNode.addChildren(node);
            return this;
        }


        public Builder after(MTAsyncTask<?, ?, ?> task) {
            Node node = mTaskMap.get(task);
            if (node == null) {
                node = new Node(task);
                mTaskMap.put(task, node);
                mNodeList.add(node);
            }
            //set Childeren or parents
            mCurrentNode.addParent(node);
            return this;
        }


    }

    /**
     * 包装MtAsyncTask的节点
     */
    private class Node {


         Node(MTAsyncTask<?, ?, ?> mAsyncTask) {
            this.mAsyncTask = mAsyncTask;
        }

        private ResultMap mResultMap;//映射转换接口
        private MTAsyncTask<?, ?, ?> mAsyncTask;//所包装的任务
        private ArrayList<Node> mTaskChildrenList;//该节点的孩子集合
        private ArrayList<Node> mTaskParentList;//该节点的父亲集合
        private ArrayList<Node> mTaskSibingList;//该节点的兄弟集合
        private boolean isTaskFinished = false;//该任务是否完成
        private int mParentFinished = 0;//父类任务完成个数
        private ArrayList<AsyncTask<?, ?, ?>> mFromResult;//所依赖的结果

        private void addParent(Node node) {
            if (mTaskParentList == null) {
                mTaskParentList = new ArrayList<>();
            }
            if (!mTaskParentList.contains(node)) {
                mTaskParentList.add(node);
                node.addChildren(this);
            }
        }

        private void addFromResult(AsyncTask<?, ?, ?> task) {
            if (mFromResult == null) {
                mFromResult = new ArrayList<>();
            }
            if (!mFromResult.contains(task)) {//如果重复添加则跳过
                mFromResult.add(task);
            }
        }

        public void addFromResults(AsyncTask<?, ?, ?>[] tasks) {
            if (tasks != null) {
                for (AsyncTask<?, ?, ?> tempTask : tasks) {
                    addFromResult(tempTask);
                }
            }
        }

        private void addChildren(Node node) {
            if (mTaskChildrenList == null) {
                mTaskChildrenList = new ArrayList<>();
            }
            if (!mTaskChildrenList.contains(node)) {
                mTaskChildrenList.add(node);
                node.addParent(this);
            }
        }

        private void addSibing(Node node) {
            if (mTaskSibingList == null) {
                mTaskSibingList = new ArrayList<>();
            }
            if (!mTaskSibingList.contains(node)) {
                mTaskSibingList.add(node);
                node.addSibing(this);
            }
        }

        private void addParents(ArrayList<Node> parents) {
            if (parents == null) {
                return;
            }
            if (mTaskParentList == null) {
                mTaskParentList = new ArrayList<>();
            }
            for (int i = 0; i < parents.size(); i++) {//一部换为多小步
                addParent(parents.get(i));
            }
        }


        public void addResultMap(ResultMap map) {
            mResultMap = map;
        }


    }
}


class TestTask extends MTAsyncTask<String, String, String> {


     TestTask(String name) {
        super(name);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected String doInBackground(String... params) {
        if (params != null && params.length > 0) {
            Log.d("startTask", "" + params[0]);
        }
        return null;
    }
}
