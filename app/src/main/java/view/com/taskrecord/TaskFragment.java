package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TasktypeController;
import model.Newtask;
import model.TUser;
import model.Tasktype;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class TaskFragment extends Fragment {
    private Button submitbutton;
    private EditText content;
    private String scontent;
    private Spinner stypes;
    private String tasktype;
    private int sid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.simpletask_fragment, container,
                false);
        //初始化
        submitbutton = (Button) view.findViewById(R.id.submittask);
        content = (EditText) view.findViewById(R.id.desc);
        stypes = (Spinner) view.findViewById(R.id.stype);
        tasktype = "学习";
        ArrayList<String> types = new TasktypeController().selectAllTypes();
        // 建立Adapter并且绑定数据源
        ArrayAdapter tasktypeAdapter = new ArrayAdapter(AppApplication.getContext(),R.layout.tasktype_item,types);
        //绑定 Adapter到控件
        tasktypeAdapter.setDropDownViewResource(R.layout.tasktype_item);
        stypes.setAdapter(tasktypeAdapter);
        stypes.setSelection(0);
        stypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int position, long id) {
                // 获取类型值
                tasktype = stypes.getSelectedItem().toString();
                tasktype = tasktype.substring(0,tasktype.length()-2);
                //根据tasktype的值获取id
                sid = new TasktypeController().getSidByTstyle(tasktype);
               // Log.w("task",tasktype + "sid = " + sid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        //然后根据类型值
        submitbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // 判断输入的任务是否为空，如果为空，那么弹出错误提示
                scontent = content.getText().toString();
                if ("".equals(scontent) || scontent == null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                    dialog.setTitle("创建任务失败");
                    dialog.setMessage("任务内容不能为空");
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {

                                }
                            });
                    dialog.show();
                } else {
                    // 将前端用户填入的数据插入 到 数据库中
                    // 将注册信息封装到Newtask对象中
                    DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                    Newtask newtask = new Newtask();
                    TUser user = AppApplication.getUser();
                    newtask.setuId(user.getuId());
                    newtask.setSid(sid);
                    newtask.setNcontent(scontent);
                    newtask.setNfinish(0);// 0表示没有完成任务
                    newtask.setaTime(format.format(new Date()));
                    newtask.setNtasktime(new Date().getTime());

                    boolean rs = new NewtaskController().addTask(newtask);

                    if (rs) {
                        // 如果插入成功，跳转到登录界面
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("成功");
                        dialog.setMessage("创建任务成功");
                        dialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {

                                    }
                                });
                        dialog.show();
                        InputMethodManager imm = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
                        content.setText(null);

                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("失败");
                        dialog.setMessage("创建任务失败");
                        dialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {

                                    }
                                });
                        dialog.show();
                    }
                }
            }
        });


        return view;
    }

}