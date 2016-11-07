package view.com.taskrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import controller.AppApplication;

public class MainTabActivity extends FragmentActivity {
    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // 定义一个布局
    private LayoutInflater layoutInflater;
    // 定义数组来存放Fragment界面 4个
    private Class fragmentArray[] = { ShowFragment.class, TaskFragment.class,
                                        SearchFragment.class, MyFragment.class};
    // 定义数组来存放按钮图片
    private int mImageViewArray[] = { R.drawable.tab_home_btn, R.drawable.tab_message_btn,
                                         R.drawable.tab_square_btn,R.drawable.tab_selfinfo_btn };
    // Tab选项卡的文字
    private String mTextviewArray[] = { "主页", "清单", "查询", "我的" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main_tab);
        //初始化
        initView();
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
    }
    @Override
    protected void onDestroy() {
        AppApplication.destroyAllAlerts();
        super.onDestroy();
    }
    // 初始化组件
    private void initView() {
        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //设置实际内容部分
        mTabHost.setup(this, getSupportFragmentManager(),R.id.realtabcontent);
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中 ，添加Fragment到mTabHost中去
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }

    }
    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }

}