package model;

/**
 * Created by tangzhijing on 2016/8/31.
 */
public class TUser {
    private int uId;//用户id
    private String uName;//用户名
    private String uEmail;//邮箱
    private String uPwd;//密码
    private String uGender;//性别
    private String uImage;//图片 ， 如果没有添加 则为默认图片
    private String uTime;//注册时间

    public TUser() {

    }
    //构造函数
    public TUser(String uName, String uEmail, String uPwd, String uGender,
                 String uImage, String uTime) {
        super();
        this.uName = uName;
        this.uEmail = uEmail;
        this.uPwd = uPwd;
        this.uGender = uGender;
        this.uImage = uImage;
        this.uTime = uTime;
    }


    public int getuId() {
        return uId;
    }
    public void setuId(int uId) {
        this.uId = uId;
    }
    public String getuName() {
        return uName;
    }
    public void setuName(String uName) {
        this.uName = uName;
    }
    public String getuEmail() {
        return uEmail;
    }
    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }
    public String getuPwd() {
        return uPwd;
    }
    public void setuPwd(String uPwd) {
        this.uPwd = uPwd;
    }
    public String getuGender() {
        return uGender;
    }
    public void setuGender(String uGender) {
        this.uGender = uGender;
    }
    public String getuImage() {
        return uImage;
    }
    public void setuImage(String uImage) {
        this.uImage = uImage;
    }
    public String getuTime() {
        return uTime;
    }
    public void setuTime(String uTime) {
        this.uTime = uTime;
    }


}