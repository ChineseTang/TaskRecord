package view.com.taskrecord;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TUserController;
import db.TaskRecordOpenHelper;
import model.TUser;


public class MainActivity extends BaseActivity {
    private TextView register;//注册跳转
    private EditText eusername;//输入用户名
    private EditText epwd;//输入密码
    private Button loginbtn;//登录按钮
    private String username;//字符串用户名
    private String pwd;//字符串密码
    private SharedPreferences pref;//用于记录个人信息，是否记住密码
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        //设置Context
        TaskRecordOpenHelper.setContext(getApplicationContext());

        //初始化控件
        register = (TextView) findViewById(R.id.register);
        eusername = (EditText) findViewById(R.id.userNamea);
        epwd = (EditText) findViewById(R.id.pwd);
        loginbtn = (Button) findViewById(R.id.loginbtn);

        //默认是记住密码的
        boolean isRember = pref.getBoolean("rember_password", true);
        if(isRember) {
            //将账号和密码设置到文本框中
            String u = pref.getString("uName", "");
            String p = pref.getString("uPwd","");
            eusername.setText(u);
            epwd.setText(p);
        }

        //如果用户名和密码不为空，那么直接进入
        //获取EditText中的值
        username = eusername.getText().toString();
        pwd = epwd.getText().toString();
        //让EditText失去光标
        eusername.clearFocus();
        epwd.clearFocus();
        //判断
        if(!(username == null || username.equals("") || pwd == null || pwd.equals("")) && !pref.getBoolean("logout",false)) {
            TUser t = new TUser();
            t.setuName(username);
            t.setuPwd(pwd);
            TUser rs = new TUserController().loginUser(t);
            //如果rs 结果不为nul，则表示登录成功
            if (rs != null) {
                //将用户信息保存到全局中
                AppApplication.setUser(rs);
                //登录成功后，将所有任务存储到AppApplication中，以后所有的任务就在这里面修改
                /**
                 *  1、如果有查询的，就不需要修改
                 *  2、如果有插入的任务，那么就先插入 ，如果插入成功，就添加到AppApplication的tasks中
                 *  3、如果有删除的任务，那么就先删除，如果删除成功，再在AppApp中删除
                 *  4、如果有修改的任务，那么就先修改，如果修改成功，再在AppAPplication中修改
                 *  以上2,3,4都是在一个事务中进行的，要不都成功，要不都不成功，是一个事务
                 */
                AppApplication.setTasks(new NewtaskController().searchAllTasksIntoApp());
                //AppApplication.setArraytypes(new TasktypeController().selectAllTypes(AppApplication.getUser().getuId()));
                //默认保存用户信息到Preferences中
                editor = pref.edit();
                editor.putBoolean("rember_password", true);
                editor.putString("uName", username);
                editor.putString("uPwd", pwd);
                //设置注销状态，默认是没有注销的
                editor.putBoolean("logout",false);
                editor.commit();
                Intent aintent = new Intent(MainActivity.this, MainTabActivity.class);
                startActivity(aintent);
                finish();
            }
        }
        //设置注册信息文字按钮，点击进入注册界面
        String regtxt = "没有账号 点击注册";
        SpannableString span = new SpannableString(register.getText().toString());
        span.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                ds.setUnderlineText(false);
            }
            @Override
            public void onClick(View arg0) {
                Intent regintent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(regintent);
            }
        }, 0, regtxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        register.setText(span);
        register.setMovementMethod(LinkMovementMethod.getInstance());
        //登录按钮监听事件
        loginbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //获取用户登录信息
                username = eusername.getText().toString();
                pwd = epwd.getText().toString();
                if(username == null || username.equals("") || pwd == null || pwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                    dialog.setTitle("登录失败");
                    dialog.setMessage("用户名或密码不能为空");
                    dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    dialog.show();
                }else{
                    TUser t = new TUser();
                    t.setuName(username);
                    t.setuPwd(pwd);
                    TUser rs = new TUserController().loginUser(t);
                    //如果rs 结果不为nul，则表示登录成功
                    if(rs.getuName() != null) {
                        //将用户信息保存到全局中
                        //登录成功后，将所有任务存储到AppApplication中，以后所有的任务就在这里面修改
                        /**
                         *  1、如果有查询的，就不需要修改
                         *  2、如果有插入的任务，那么就先插入 ，如果插入成功，就添加到AppApplication的tasks中
                         *  3、如果有删除的任务，那么就先删除，如果删除成功，再在AppApp中删除
                         *  4、如果有修改的任务，那么就先修改，如果修改成功，再在AppAPplication中修改
                         *  以上2,3,4都是在一个事务中进行的，要不都成功，要不都不成功，是一个事务
                         */
                        AppApplication.setUser(rs);
                       AppApplication.setTasks(new NewtaskController().searchAllTasksIntoApp());
                        //AppApplication.setArraytypes(new TasktypeController().selectAllTypes(AppApplication.getUser().getuId()));

                        Log.w("taskemial", rs.getuEmail()+ "");
                        Log.w("task",rs.getuId()+rs.getuName() + "");
                        //默认保存用户信息到Preferences中
                        editor = pref.edit();
                        editor.putBoolean("rember_password", true);
                        editor.putString("uName", username);
                        editor.putString("uPwd", pwd);
                        //设置注销状态，默认是没有注销的
                        editor.putBoolean("logout",false);
                        editor.commit();
                        Intent aintent = new Intent(MainActivity.this,MainTabActivity.class);
                        startActivity(aintent);
                        finish();
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("登录失败");
                        dialog.setMessage("用户名或密码错误");
                        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
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
}
