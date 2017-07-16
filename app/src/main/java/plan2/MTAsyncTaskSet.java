package plan2;

import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zmc on 2017/7/14.
 */
public class MTAsyncTaskSet<Params,Progress,Result>  extends MTAsyncTask<Params,Progress,Result> implements MTAsyncTaskListener {

    private MTAsyncTask<?, ?, ?> mStartTask = new TestTask("StartTask");
    private Node mStartNode = new Node(mStartTask);
    private Map<MTAsyncTask<?, ?, ?>, Node> mTaskMap;
    private ArrayList<Node> mNodeList = new ArrayList<>();
    private boolean isCreateDependencyGraph = false;//用来判断是否建立依赖图，第一次开始的时候建立一次就好


    public MTAsyncTaskSet(Params... paramses) {
        super(paramses);
        mTaskMap = new HashMap<>();
        mTaskMap.put(mStartTask, mStartNode);//init
        mStartTask.setAsyncTaskListener(this);
    }




    @Override
    protected Result doInBackground(Params... params) {
        start();
        Log.d("dsfdsf","dsf");
        return null;
    }

    public Builder run(MTAsyncTask<?, ?, ?> task) {
        if (task != null) {
            return new Builder(task);
        } else {
            return null;
        }
    }


    public void createDependencyGraph() {
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


    public void start() {
        createDependencyGraph();
        start(mStartNode);
    }

    private void start(Node node) {
        if (node != null && node.mAsyncTask != null) {
            node.mAsyncTask.setAsyncTaskListener(this);
            node.mAsyncTask.startTask();//执行任务
        }
    }

    public void onChildrenTaskEnd(MTAsyncTask<?, ?, ?> task) {
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

    @Override
    public void onAsyncTaskFinish(MTAsyncTask<?, ?, ?> task) {
        Node node = mTaskMap.get(task);
        //node.mParentFinished++;
        node.isTaskFinished = true;
        onChildrenTaskEnd(task);
    }


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

    private class Node {

        public Node(MTAsyncTask<?, ?, ?> mAsyncTask) {
            this.mAsyncTask = mAsyncTask;
        }

        private MTAsyncTask<?, ?, ?> mAsyncTask;
        private ArrayList<Node> mTaskChildrenList;
        private ArrayList<Node> mTaskParentList;
        private ArrayList<Node> mTaskSibingList;
        private boolean isTaskFinished = false;
        private int mParentFinished = 0;

        public void addParent(Node node) {
            if (mTaskParentList == null) {
                mTaskParentList = new ArrayList<>();
            }
            if (!mTaskParentList.contains(node)) {
                mTaskParentList.add(node);
                node.addChildren(this);
            }
        }

        public void addChildren(Node node) {
            if (mTaskChildrenList == null) {
                mTaskChildrenList = new ArrayList<>();
            }
            if (!mTaskChildrenList.contains(node)) {
                mTaskChildrenList.add(node);
                node.addParent(this);
            }
        }

        public void addSibing(Node node) {
            if (mTaskSibingList == null) {
                mTaskSibingList = new ArrayList<>();
            }
            if (!mTaskSibingList.contains(node)) {
                mTaskSibingList.add(node);
                node.addSibing(this);
            }
        }

        public void addParents(ArrayList<Node> parents) {
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


    }
}


class TestTask extends MTAsyncTask<String, String, String> {


    public TestTask(String... params) {
        super(params);
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
    protected String doInBackground(String[] params) {
        if (params != null && params.length > 0) {
            Log.d("mttask", "" + params[0]);
        }
        return null;
    }
}
