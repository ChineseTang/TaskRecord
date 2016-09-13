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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import controller.AppApplication;
import controller.TUserController;
import db.TaskRecordOpenHelper;
import model.TUser;


public class MainActivity extends BaseActivity {
    private TextView register;
    private EditText eusername;
    private EditText epwd;
    //private CheckBox rememberpass;
    private Button loginbtn;
    private String username;
    private String pwd;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        TaskRecordOpenHelper.setContext(getApplicationContext());
        register = (TextView) findViewById(R.id.register);
        eusername = (EditText) findViewById(R.id.userNamea);
        epwd = (EditText) findViewById(R.id.pwd);
        loginbtn = (Button) findViewById(R.id.loginbtn);
        //rememberpass = (CheckBox) findViewById(R.id.rememberpwd);
        //如果用户登录成功，那么下次登录的时候就记住密码，免登录
        //默认是记住密码的
        boolean isRember = pref.getBoolean("rember_password", true);
        //boolean isfirst = pref.getBoolean("firstlogin", false);
        if(isRember) {
            //将账号和密码设置到文本框中
            String u = pref.getString("uName", "");
            String p = pref.getString("uPwd","");
            eusername.setText(u);
            epwd.setText(p);
            //rememberpass.setChecked(true);
        }
        //让EditText失去光标
        eusername.clearFocus();
        epwd.clearFocus();
        //如果用户名和密码不为空，那么直接进入
        username = eusername.getText().toString();
        pwd = epwd.getText().toString();
        if(!(username == null || username.equals("") || pwd == null || pwd.equals("")) && !AppApplication.isLoginout()) {
            TUser t = new TUser();
            t.setuName(username);
            t.setuPwd(pwd);
            TUser rs = new TUserController().loginUser(t);
            //如果rs 结果不为nul，则表示登录成功
            if (rs != null) {
                //将用户信息保存到全局中
                AppApplication.setUser(rs);
                //默认保存用户信息到Preferences中
                editor = pref.edit();
                editor.putBoolean("rember_password", true);
                editor.putString("uName", username);
                editor.putString("uPwd", pwd);
                editor.commit();
                Intent aintent = new Intent(MainActivity.this, MainTabActivity.class);
                startActivity(aintent);
                finish();
            }
        }
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
                    if(rs != null) {
                        //将用户信息保存到全局中
                        AppApplication.setUser(rs);
                        //默认保存用户信息到Preferences中
                        editor = pref.edit();
                        editor.putBoolean("rember_password", true);
                        editor.putString("uName", username);
                        editor.putString("uPwd", pwd);
                        editor.commit();
                        Intent aintent = new Intent(MainActivity.this,MainTabActivity.class);
                        startActivity(aintent);
                        finish();
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("登录失败");
                        dialog.setMessage("用户名或密码错误");
                        dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
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
