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

public class TaskFragment extends Fragment {
    private Button submitbutton;
    private EditText content;
    private String scontent;
    private Spinner stypes;
    private String tasktype;
    private int sid;
    private  ArrayAdapter tasktypeAdapter;
    private ArrayList<String> types;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simpletask_fragment, container, false);
        //初始化
        init(view);
        //绑定 Adapter到控件
        tasktypeAdapter.setDropDownViewResource(R.layout.tasktype_item);
        stypes.setAdapter(tasktypeAdapter);
        stypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 获取类型值
                tasktype = stypes.getSelectedItem().toString();
                tasktype = tasktype.substring(0,tasktype.length()-2);
                //根据tasktype的值获取id
                sid = new TasktypeController().getSidByTstyle(tasktype);
                //如果是自定义，那么添加内容
                if(sid == 1) {
                    AlertDialog.Builder newdialog = new AlertDialog.Builder(
                            getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                    final EditText et = new EditText(getActivity());
                    et.setMinHeight(300);
                    et.setHint("请输入要添加的任务类型");
                    et.setBackground(null);
                    et.setGravity(Gravity.TOP | Gravity.LEFT);
                    newdialog.setView(et);

                    newdialog.setTitle("添加任务类型");
                    newdialog.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                }
                            });
                    newdialog.setNegativeButton("添加",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //首先判断是否存在该类型，如果存在，那么添加失败
                            String newtype = et.getText().toString();
                            int id = new TasktypeController().getSidByTstyle(newtype);
                            if(id == -1) {
                                //新建一个TaskType类型
                                Tasktype tt = new Tasktype(et.getText().toString());
                                boolean rs = new TasktypeController().addType(newtype);
                                //如果为真，那么添加成功，否则提示添加失败
                                if(rs) {
                                    // 建立Adapter并且绑定数据源
                                    newtype = newtype +" ▼";
                                    //types.add(newtype);
                                    types.add(0,newtype);
                                    tasktypeAdapter.notifyDataSetChanged();
                                    stypes.setSelection(types.indexOf(newtype),true);
                                   /* AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                                    dialog.setTitle("添加成功");
                                    dialog.setMessage("恭喜，添加成功，哈哈");
                                    dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    dialog.show();*/
                                }else{
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                                    dialog.setTitle("添加失败");
                                    dialog.setMessage("不好意思，由于未知原因，添加失败，哈哈");
                                    dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    dialog.show();
                                }

                            }else{
                                //弹出dialog，显示该类型已经存在!
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                                dialog.setTitle("添加失败");
                                dialog.setMessage("改类型已经存在！");
                                dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                dialog.show();
                            }
                            }
                        });
                        newdialog.show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        //获取修改任务，如果不为空，则表示是修改任务
        if(AppApplication.getUpdatetask() != null) {
                updateTask();
        }else {
                insertTask();
        }
        return view;
    }
    public void init(View view) {
        submitbutton = (Button) view.findViewById(R.id.submittask);
        content = (EditText) view.findViewById(R.id.desc);
        stypes = (Spinner) view.findViewById(R.id.stype);
        content.setSaveEnabled(false);
        stypes.setSaveEnabled(false);
        types = new TasktypeController().selectAllTypes();
        // 建立Adapter并且绑定数据源
        tasktypeAdapter = new ArrayAdapter(AppApplication.getContext(),R.layout.tasktype_item,types);
    }
    public void updateTask() {
        final Newtask nt = AppApplication.getUpdatetask();
        submitbutton.setText("修改任务");
        String text = nt.getNcontent();

        content.setText(text);
        AppApplication.setUpdatetask(null);
        //content.setSelection(content.getText().length());
        //1.获取sid
           int id = nt.getSid();
            String currenttype = new TasktypeController().getTstyleBySid(id);
            currenttype = currenttype +" ▼";
            stypes.setSelection(types.indexOf(currenttype),true);
            //stypes.setSelection(tasktypeAdapter.getPosition(currenttype),true);
            submitbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // 判断输入的任务是否为空，如果为空，那么弹出错误提示
                scontent = content.getText().toString();
                if ("".equals(scontent) || scontent == null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    dialog.setTitle("修改任务失败");
                    dialog.setMessage("修改的任务内容不能为空");
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
                    Newtask newtask = new Newtask(nt.getNtId(),nt.getuId(),sid,content.getText().toString(),
                            nt.getNfinish(),nt.getaTime(),nt.getNtasktime());
                    boolean rs = new NewtaskController().updtateTask(newtask);
                    if (rs) {
                        // 如果插入成功，跳转到登录界面
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("成功");
                        dialog.setMessage("修改任务成功");
                        dialog.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {

                                    }
                                });
                        dialog.show();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
                        content.setText(null);

                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("失败");
                        dialog.setMessage("修改任务失败");
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
    }
    public void insertTask() {
        //设置时间提示hint
        if (AppApplication.getTouchtime() != null) {
            content.setHint("任务时间为：" + AppApplication.getTouchtime() + "\n请输入内容");
        } else {
            DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            content.setHint("任务时间为：" + format.format(new Date()) + "\n请输入内容");
        }
        stypes.setSelection(0);
        //然后根据类型值
        submitbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // 判断输入的任务是否为空，如果为空，那么弹出错误提示
                scontent = content.getText().toString();
                if ("".equals(scontent) || scontent == null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT);
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
                    if (AppApplication.getTouchtime() != null) {
                        newtask.setaTime(AppApplication.getTouchtime());
                        AppApplication.setTouchtime(null);
                    } else {
                        newtask.setaTime(format.format(new Date()));
                    }
                    newtask.setNtasktime(new Date().getTime());
                    boolean rs = new NewtaskController().addTask(newtask);
                    if (rs) {
                        // 如果插入成功，跳转到登录界面
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(), AlertDialog.THEME_HOLO_LIGHT);
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
                                getActivity(), AlertDialog.THEME_HOLO_LIGHT);
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
    }
}