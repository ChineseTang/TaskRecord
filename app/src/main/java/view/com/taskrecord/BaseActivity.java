package view.com.taskrecord;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import controller.ActivityCollector;


public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置app始终竖屏
        ActivityCollector.addActivity(this);
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
