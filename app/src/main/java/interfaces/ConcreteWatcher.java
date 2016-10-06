package interfaces;

import controller.TaskAlertController;

/**
 * Created by tangzhijing on 2016/10/3.
 */
public class ConcreteWatcher implements Watcher
{

    @Override
    public void update(int taid)
    {
        //更新id 去数据库
        new TaskAlertController().ChangeToFinish(taid);
    }

}
