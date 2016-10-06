package interfaces;

/**
 * Created by tangzhijing on 2016/10/3.
 */
public interface Watched {
    public void addWatcher(Watcher watcher);

    public void removeWatcher(Watcher watcher);

    public void notifyWatchers(int at);
}
