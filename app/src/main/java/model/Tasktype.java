package model;

/**
 * Created by tangzhijing on 2016/9/5.
 */
public class Tasktype {
    private int sid;
    private String tstyle;

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getTstyle() {
        return tstyle;
    }

    public void setTstyle(String tstyle) {
        this.tstyle = tstyle;
    }

    public Tasktype() {

    }

    public Tasktype(int sid, String tstyle) {
        super();
        this.sid = sid;
        this.tstyle = tstyle;
    }
}
