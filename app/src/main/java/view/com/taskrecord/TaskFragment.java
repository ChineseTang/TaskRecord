package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TasktypeController;
import model.Newtask;
import model.TUser;
import model.Tasktype;
import receiver.MyReceiver;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.melnykov.fab.FloatingActionButton;
public class TaskFragment extends Fragment implements TimePickerDialog.OnTimeSetListener{
    private Spinner submitbutton;
    private EditText content;
    private String scontent;
    private Spinner stypes;
    private String tasktype;
    private int sid;
    private int oldposition = 2;
    private FloatingActionButton addnewtask;
    private  ArrayAdapter tasktypeAdapter;
    private ArrayList<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private ArrayList<String> types;
    final Calendar calendar = Calendar.getInstance();
    final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
    public static final String TIMEPICKER_TAG = "timepicker";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simpletask_fragment, container, false);
        //初始化
        init(view);
        if (savedInstanceState != null) {
            TimePickerDialog tpd = (TimePickerDialog) getActivity().getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

        //这里是添加通知功能的按钮
        /*submitbutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show(getActivity().getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });*/
        submitbutton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String note = data_list.get(position);
                if(note.equals("提醒")){
                    //当点击提醒时，删除掉 不提醒 和 提醒 之外的所有选项
                    for (String deletestring: data_list) {
                        if(!(deletestring.equals("不提醒") || deletestring.equals("提醒"))){
                            data_list.remove(deletestring);
                        }
                    }
                    timePickerDialog.show(getActivity().getSupportFragmentManager(), TIMEPICKER_TAG);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //绑定 Adapter到控件
        tasktypeAdapter.setDropDownViewResource(R.layout.tasktype_item);
        stypes.setAdapter(tasktypeAdapter);
        stypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //设置选择相同的可以产生事件

                // 获取类型值
                tasktype = stypes.getSelectedItem().toString();
                tasktype = tasktype.substring(0,tasktype.length()-2);

                //根据tasktype的值获取id
                sid = new TasktypeController().getSidByTstyle(tasktype);
                if(sid != 1) {
                    oldposition = position;
                }
                //如果是自定义，那么添加内容
                if(sid == 1) {
                    /*Field field = null;
                    try {
                        field = AdapterView.class.getDeclaredField("mOldSelectedPosition");
                        field.setAccessible(true);  //设置mOldSelectedPosition可访问
                        field.setInt(stypes, AdapterView.INVALID_POSITION); //设置mOldSelectedPosition的值
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
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
                                    stypes.setSelection(oldposition,true);
                                }
                            });
                    newdialog.setNegativeButton("添加",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //首先判断是否存在该类型，如果存在，那么添加失败
                            String newtype = et.getText().toString();
                            if (("").equals(newtype) || newtype == null) {
                                stypes.setSelection(oldposition, true);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                dialog.setTitle("添加失败");
                                dialog.setMessage("内容不能为空！");
                                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                dialog.show();
                            } else {
                                int id = new TasktypeController().getSidByTstyle(newtype);
                                if (id == -1) {
                                    //新建一个TaskType类型
                                    Tasktype tt = new Tasktype(et.getText().toString());
                                    boolean rs = new TasktypeController().addType(newtype);
                                    //如果为真，那么添加成功，否则提示添加失败
                                    if (rs) {
                                        // 建立Adapter并且绑定数据源
                                        newtype = newtype + " ▼";
                                        //types.add(newtype);
                                        types.add(0, newtype);
                                        tasktypeAdapter.notifyDataSetChanged();
                                        stypes.setSelection(types.indexOf(newtype), true);
                                    } else {
                                        stypes.setSelection(oldposition, true);
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        dialog.setTitle("添加失败");
                                        dialog.setMessage("不好意思，由于未知原因，添加失败，哈哈");
                                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                        dialog.show();
                                    }
                                } else {
                                    stypes.setSelection(oldposition, true);
                                    //弹出dialog，显示该类型已经存在!
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    dialog.setTitle("添加失败");
                                    dialog.setMessage("该类型已经存在！");

                                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    dialog.show();
                                }
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
        submitbutton = (Spinner) view.findViewById(R.id.submittask);
        content = (EditText) view.findViewById(R.id.desc);
        stypes = (Spinner) view.findViewById(R.id.stype);
        addnewtask = (FloatingActionButton) view.findViewById(R.id.addnewtask);
        content.setSaveEnabled(false);
        stypes.setSaveEnabled(false);
        types = new TasktypeController().selectAllTypes();
        // 建立Adapter并且绑定数据源
        tasktypeAdapter = new ArrayAdapter(AppApplication.getContext(),R.layout.tasktype_item,types);
        //数据
        data_list = new ArrayList<String>();
        data_list.add("不提醒");
        data_list.add("提醒");
        arr_adapter= new ArrayAdapter<String>(AppApplication.getContext(), R.layout.tasktype_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.tasktype_item);
        //加载适配器
        submitbutton.setAdapter(arr_adapter);
    }
    public void updateTask() {
        final Newtask nt = AppApplication.getUpdatetask();
        //submitbutton.setText("修改任务");
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
        addnewtask.setOnClickListener(new OnClickListener() {
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
            content.setHint("任务时间为：" + AppApplication.getTouchtime());
        } else {
            DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            content.setHint("任务时间为：" + format.format(new Date()));
        }
        stypes.setSelection(0);
        //然后根据类型值
        addnewtask.setOnClickListener(new OnClickListener() {
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
                } else if(sid == 1) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    dialog.setTitle("创建任务失败");
                    dialog.setMessage("任务类型不能为自定义");
                    dialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                }
                            });
                    dialog.show();
                }else
                    {
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

                        //同时添加通知功能，获取提醒spinner中的内容
                        String time = submitbutton.getSelectedItem().toString();
                        String reminderdate = null;
                        //如果不是不提醒或者提醒，那么添加该条信息到广播通知中
                        if(!(time.equals("不提醒") || time.equals("提醒"))){
                            if (AppApplication.getTouchtime() != null) {
                                reminderdate = AppApplication.getTouchtime();
                            } else {
                                reminderdate = format.format(new Date());
                            }
                            setReminder(true,reminderdate,time,scontent);
                        }



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
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        //设置通知时间
        String time = hourOfDay + ":" + minute;
        //submitbutton.setText(time);
        data_list.add(time);
        submitbutton.setSelection(data_list.indexOf(time), true);
        //Toast.makeText(getActivity(), "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
    }

    /**
     *
     * @param b 传入是否添加通知
     * @param rd 2016-01-02 reminder date
     * @param sf 12:04 shi fen
     * @param content 任务内容
     */
    private void setReminder(boolean b,String rd,String sf,String content) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        String alarmtime = rd + "-" + sf;
        Date date = null;
        try {
           date = sdf.parse(alarmtime);
          /*  Log.w("task",alarmtime);
            Log.w("task",String.valueOf(date.getTime()));
            Log.w("task",String.valueOf(new Date().getTime()));
            Log.w("task",content);*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // get the AlarmManager instance
        AlarmManager am= (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

        Intent intent = new Intent(getActivity(),MyReceiver.class);
        intent.setAction("VIDEO_TIMER");
        intent.putExtra("content",content);
        // create a PendingIntent that will perform a broadcast
        // PendingIntent这个类用于处理即将发生的事情
        PendingIntent pi= PendingIntent.getBroadcast(getActivity(),1, intent, 0);

        if(b){
            // just use current time as the Alarm time.
            //Calendar c=Calendar.getInstance();
            // schedule an alarm
            am.set(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }

    }
}