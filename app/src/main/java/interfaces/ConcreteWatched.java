package interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangzhijing on 2016/10/4.
 */
public class ConcreteWatched implements Watched{
    // 存放观察者
    private List<Watcher> list = new ArrayList<Watcher>();
    @Override
    public void addWatcher(Watcher watcher) {
        list.add(watcher);
    }

    @Override
    public void removeWatcher(Watcher watcher) {
        list.remove(watcher);
    }

    @Override
    public void notifyWatchers(int at) {
// 自动调用实际上是主题进行调用的
        for (Watcher watcher : list)
        {
            watcher.update(at);
        }
    }
}
