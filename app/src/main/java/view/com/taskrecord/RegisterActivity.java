package view.com.taskrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import controller.TUserController;
import model.TUser;

public class RegisterActivity extends BaseActivity {
    private ImageButton iback;
    private ImageView iphoto;
    private EditText eusername;
    private EditText epwd;
    private EditText erepwd;
    private Button regbtn;
    private String susername;
    private String semail;
    private String spwd;
    private String srpwd;
    private String sgender = "男";//性别
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
        setContentView(R.layout.activity_register);
        //初始化控件
        init();
        //为iback按钮注册监听事件
        iback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        //注册按钮监听处理事件
        regbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //1 获得注册信息中的值
                susername = eusername.getText().toString();
                semail = "tzjsmile@qq.com";
                spwd = epwd.getText().toString();
                srpwd = erepwd.getText().toString();
                //2 验证注册信息的值是否合理
                if(susername == null || susername.equals("")|| semail == null || semail.equals("") || spwd == null || spwd.equals("")|| srpwd == null || srpwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                    LayoutInflater layoutInflater = LayoutInflater.from(RegisterActivity.this);
                    View updateView = layoutInflater.inflate(R.layout.content, null);
                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                    titlealert.setText("注册失败");
                    contentalert.setText("\n" +
                            "\n" +
                            "\n\n        用户信息不能为空哟O(∩_∩)O~" );
                    dialog.setView(updateView);
                    dialog.setPositiveButton("好吧，那我输入信息嘛",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog.show();
                }else if(!spwd.equals(srpwd)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                    LayoutInflater layoutInflater = LayoutInflater.from(RegisterActivity.this);
                    View updateView = layoutInflater.inflate(R.layout.content, null);
                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                    titlealert.setText("注册失败");
                    contentalert.setText("\n" +
                            "\n" +
                            "\n\n        两次密码应该相同哟O(∩_∩)O哈哈~" );
                    dialog.setView(updateView);
                    dialog.setPositiveButton("好吧，那我输入信息相同的密码哟",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog.show();
                    //判断数据库中是否已经存在该用户，根据username进行判断
                }else if(new TUserController().isExistUsername(susername)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                    LayoutInflater layoutInflater = LayoutInflater.from(RegisterActivity.this);
                    View updateView = layoutInflater.inflate(R.layout.content, null);
                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                    titlealert.setText("注册失败");
                    contentalert.setText("\n" +
                            "\n" +
                            "\n\n        不好意思，该用户名已经存在！" );
                    dialog.setView(updateView);
                    dialog.setPositiveButton("好吧，那我只有输入新的名字了",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
                    dialog.show();
                }
                else{
                    //将注册信息封装到TUser对象中
                    DateFormat format= new SimpleDateFormat("yyyy.MM.dd");
                    TUser user = new TUser();
                    user.setuName(susername);
                    user.setuEmail(semail);
                    user.setuPwd(spwd);
                    //设置头像存储路径
                    user.setuImage(Environment.getExternalStorageDirectory() + "output_image.jpg");//存储是相对于SD根目录下的位置
                    user.setuGender(sgender);
                    user.setuTime(format.format(new Date()));
                    //信息没有问题了，那么就调用Controller层插入数据吧
                    boolean result = new TUserController().registerUser(user);
                    if(result) {
                        //如果插入成功，跳转到登录界面
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                        LayoutInflater layoutInflater = LayoutInflater.from(RegisterActivity.this);
                        View updateView = layoutInflater.inflate(R.layout.content, null);
                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                        titlealert.setText("注册成功");
                        contentalert.setText("\n" +
                                "\n" +
                                "\n\n        点击进入登录页面" );
                        dialog.setView(updateView);
                        dialog.setPositiveButton("那我进去登录了哟",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.show();
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                        LayoutInflater layoutInflater = LayoutInflater.from(RegisterActivity.this);
                        View updateView = layoutInflater.inflate(R.layout.content, null);
                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                        titlealert.setText("注册失败");
                        contentalert.setText("\n" +
                                "\n" +
                                "\n\n        注册信息有错误" );
                        dialog.setView(updateView);
                        dialog.setPositiveButton("好吧，你赢了",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });
    }

    /**
     * 初始化控件
     */
    private void init() {
        iback = (ImageButton) findViewById(R.id.array);
        iphoto = (ImageView) findViewById(R.id.photo);
        eusername = (EditText) findViewById(R.id.userName);
        epwd = (EditText) findViewById(R.id.pwd);
        erepwd = (EditText) findViewById(R.id.repwd);
        regbtn = (Button) findViewById(R.id.registerbtn);
    }
}