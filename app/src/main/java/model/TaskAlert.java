package model;

import java.io.Serializable;

/**
 * Created by tangzhijing on 2016/10/3.
 */
public class TaskAlert implements Serializable{
    private int aid;
    private int ntid;
    private long alertTime;
    private String alertContent;
    private int alertFinish;
    private long createTime;




    public TaskAlert() {

    }

    public int getNtid() {
        return ntid;
    }

    public void setNtid(int ntid) {
        this.ntid = ntid;
    }

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public long getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(long alertTime) {
        this.alertTime = alertTime;
    }

    public String getAlertContent() {
        return alertContent;
    }

    public void setAlertContent(String alertContent) {
        this.alertContent = alertContent;
    }

    public int getAlertFinish() {
        return alertFinish;
    }

    public void setAlertFinish(int alertFinish) {
        this.alertFinish = alertFinish;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
