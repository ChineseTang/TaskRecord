package myview;

/**
 * Created by tangzhijing on 2016/10/18.
 */
public interface BannerViewListener {

    /**
     * 请求广告成功
     */
    void onRequestSuccess();

    /**
     * 切换广告条
     */
    void onSwitchBanner();

    /**
     * 请求广告失败
     */
    void onRequestFailed();
}