package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TaskAlertController;
import controller.TasktypeController;
import model.Newtask;
import model.TUser;
import model.TaskAlert;
import model.Tasktype;
import receiver.MyReceiver;

public class TaskFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    private Spinner submitbutton;
    private EditText content;
    private String scontent;
    private Spinner stypes;
    private String tasktype;
    private View taskfragmentview;
    public static int currentIntent = 0;
    private int sid;
    private int oldposition = 2;//存储任务类型的位置
    private FloatingActionButton addnewtask;//浮动按钮，点击添加任务
    private ArrayAdapter tasktypeAdapter;//任务类型适配器
    private ArrayList<String> data_list;//存储提醒  和 不提醒
    private ArrayAdapter<String> arr_adapter;//data_list适配器
    private ArrayList<String> types;//类型存储
    final Calendar calendar = Calendar.getInstance();
    final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
    public static final String TIMEPICKER_TAG = "timepicker";//显示提醒时间
    private Newtask nt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        taskfragmentview = inflater.inflate(R.layout.simpletask_fragment, container, false);
        //设置home 和 后退键监听事件
        //初始化
        init(taskfragmentview);
        if (savedInstanceState != null) {
            String cont = savedInstanceState.getString("taskcontent");
            content.setText(cont);
        }

        setAlert();


        //设置任务处理监听事件
        setType();
        //获取修改任务，如果不为空，则表示是修改任务
        if (AppApplication.getUpdatetask() != null) {
            updateTask();
        } else {
            insertTask();
        }

        return taskfragmentview;
    }


    //初始化控件
    private void init(View view) {
        submitbutton = (Spinner) view.findViewById(R.id.submittask);
        content = (EditText) view.findViewById(R.id.desc);
        stypes = (Spinner) view.findViewById(R.id.stype);
        addnewtask = (FloatingActionButton) view.findViewById(R.id.addnewtask);
        content.setSaveEnabled(false);
        stypes.setSaveEnabled(false);
        submitbutton.setSaveEnabled(false);
        nt = null;
        types = new TasktypeController().selectAllTypes(AppApplication.getUser().getuId());
        // 建立Adapter并且绑定数据源
        tasktypeAdapter = new ArrayAdapter(AppApplication.getContext(), R.layout.tasktype_item, types);
        //任务类型绑定 Adapter到控件
        tasktypeAdapter.setDropDownViewResource(R.layout.tasktype_item);
        //数据
        stypes.setAdapter(tasktypeAdapter);


        sid = 0;
        data_list = new ArrayList<String>();
        data_list.add("不提醒");
        data_list.add("提醒");
        arr_adapter = new ArrayAdapter<String>(AppApplication.getContext(), R.layout.tasktype_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(R.layout.tasktype_item);
        //加载适配器
        submitbutton.setAdapter(arr_adapter);
        String gettempcontent = AppApplication.getTaskcontenttemp();
        if (gettempcontent != null) {
            if (!("".equals(gettempcontent))) {
                content.setText(gettempcontent);
                AppApplication.setTaskcontenttemp(null);
            }
        }
        int id = AppApplication.getSid();
        if (id != -1) {
            sid = id;
            String currenttype = new TasktypeController().getTstyleBySid(id);
            stypes.setSelection(types.indexOf(currenttype));
        } else {
            stypes.setSelection(0);
        }

        String ntime = AppApplication.getAlertstring();
        if (ntime != null) {
            if (!(ntime.equals("不提醒") || ntime.equals("提醒"))) {
                data_list.add(ntime);
                submitbutton.setSelection(data_list.indexOf(ntime), true);
            }
        }

    }

    //设置提醒spinner
    private void setAlert() {
        //显示闹钟
        submitbutton.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView v = (TextView) view;
                v.setTextColor(Color.WHITE);
                String note = data_list.get(position);
                if (note.equals("提醒")) {
                    submitbutton.setSelection(data_list.indexOf("不提醒"), true);
                    //当点击提醒时，删除掉 不提醒 和 提醒 之外的所有选项
                    for (String deletestring : data_list) {
                        if (!(deletestring.equals("不提醒") || deletestring.equals("提醒"))) {
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
    }

    //设置类型监听
    private void setType() {

        stypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 获取类型值
                TextView v = (TextView) view;
                v.setTextColor(Color.WHITE);
                tasktype = stypes.getSelectedItem().toString();
                //tasktype = tasktype.substring(0,tasktype.length()-2);
                //根据tasktype的值获取id
                sid = new TasktypeController().getSidByTstyle(tasktype);
                if (sid != 1) {
                    oldposition = position;
                }
                //如果是自定义，那么添加内容
                if (sid == 1) {
                    stypes.setSelection(oldposition, true);
                    AlertDialog.Builder newdialog = new AlertDialog.Builder(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    final EditText et = new EditText(getActivity());
                    et.setMinHeight(300);
                    et.setHint("请输入要添加的清单类型\n添加的类型不超过2个字");
                    et.setBackground(null);
                    et.setGravity(Gravity.TOP | Gravity.LEFT);
                    newdialog.setView(et);
                    newdialog.setTitle("添加清单类型");
                    newdialog.setPositiveButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    stypes.setSelection(oldposition, true);
                                }
                            });
                    newdialog.setNegativeButton("添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //首先判断是否存在该类型，如果存在，那么添加失败
                            String newtype = et.getText().toString();
                            if (("").equals(newtype) || newtype == null) {
                                stypes.setSelection(oldposition, true);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                View updateView = layoutInflater.inflate(R.layout.content, null);
                                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                titlealert.setText("添加失败");
                                contentalert.setText("\n" +
                                        "\n" +
                                        "\n" +
                                        "\n        内容不能为空！");
                                dialog.setView(updateView);
                               /* dialog.setTitle("添加失败");
                                dialog.setMessage("内容不能为空！");*/
                                dialog.setPositiveButton("好吧，那我输入信息嘛", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                                dialog.show();
                            } else if (newtype.length() > 2) {
                                stypes.setSelection(oldposition, true);
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                View updateView = layoutInflater.inflate(R.layout.content, null);
                                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                titlealert.setText("添加失败");
                                contentalert.setText("\n" +
                                        "\n" +
                                        "\n" +
                                        "\n        添加的类型不超过2个字！");
                                dialog.setView(updateView);
                                dialog.setPositiveButton("好吧，那我重新输入信息嘛", new DialogInterface.OnClickListener() {
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
                                    boolean rs = new TasktypeController().addType(newtype, AppApplication.getUser().getuId());
                                    //如果为真，那么添加成功，否则提示添加失败
                                    if (rs) {
                                        // 建立Adapter并且绑定数据源
                                        types.add(0, newtype);
                                        //重新设置sid
                                        sid = new TasktypeController().getSidByTstyle(newtype);
                                        tasktypeAdapter.notifyDataSetChanged();
                                        stypes.setSelection(types.indexOf(newtype), true);
                                    } else {
                                        stypes.setSelection(oldposition, true);
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                        View updateView = layoutInflater.inflate(R.layout.content, null);
                                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                        titlealert.setText("添加失败");
                                        contentalert.setText("\n" +
                                                "\n" +
                                                "\n" +
                                                "\n        不好意思，由于未知原因，添加失败！");
                                        dialog.setView(updateView);
                                        dialog.setPositiveButton("倒霉，什么app嘛！", new DialogInterface.OnClickListener() {
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
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("添加失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        不好意思，该类型已经存在！");
                                    dialog.setView(updateView);
                                    dialog.setPositiveButton("好吧，那我重新想嘛", new DialogInterface.OnClickListener() {
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("taskcontent", content.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            String cont = savedInstanceState.getString("taskcontent");
            content.setText(cont);
        }
    }

    // 处理更新任务
    public void updateTask() {
        nt = AppApplication.getUpdatetask();
        String text = nt.getNcontent();
        String ntime = nt.getNotetime();
        if (!(ntime.equals("不提醒") || ntime.equals("提醒"))) {
            data_list.add(ntime);
            submitbutton.setSelection(data_list.indexOf(ntime), true);
        }
        content.setText(text);
        AppApplication.setUpdatetask(null);
        //1.获取sid
        int id = nt.getSid();
        String currenttype = new TasktypeController().getTstyleBySid(id);
        stypes.setSelection(types.indexOf(currenttype), true);
        addnewtask.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // 判断输入的任务是否为空，如果为空，那么弹出错误提示
                scontent = content.getText().toString();
                String time = null;
                if ("".equals(scontent) || scontent == null) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View updateView = layoutInflater.inflate(R.layout.content, null);
                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                    titlealert.setText("修改清单失败");
                    contentalert.setText("\n" +
                            "\n" +
                            "\n" +
                            "\n        修改的清单内容不能为空哟");
                    dialog.setView(updateView);
                    dialog.setPositiveButton("哦，那我输入信息了哟",
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
                    time = submitbutton.getSelectedItem().toString();
                    Newtask newtask = new Newtask(nt.getNtId(), nt.getuId(), sid, content.getText().toString(),
                            nt.getNfinish(), nt.getaTime(), time, nt.getNtasktime());
                    //到数据库中去处理数据
                    boolean rs = new NewtaskController().updtateTask(newtask);
                    if (rs) {
                        //如果不是不提醒或者提醒，那么添加该条信息到广播通知中
                        if (!(time.equals("不提醒") || time.equals("提醒"))) {
                            //如果之前有时间，那么先删除数据库中提醒表中的提醒事件
                            String orgin = nt.getNotetime();
                            if (!("不提醒".equals(orgin))) {
                                //修改数据库中提醒表中的提醒事件的状态
                                //取消闹钟提醒事件，去AlarmManager中处理
                                //根据ntid 获得aid
                                int aid = new TaskAlertController().selectAlertAid(nt.getNtId());
                                new TaskAlertController().ChangeToFinish(aid);

                            }
                            // if (!(time.equals(orgin))) {
                            setReminder(true, nt.getaTime(), time, scontent, nt.getNtId());
                            //}
                        }
                        Toast.makeText(getActivity(), "修改清单成功", Toast.LENGTH_SHORT).show();
                        nt = null;
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
                        content.setText(null);
                        //stypes.setSelection(sid);
                        submitbutton.setSelection(data_list.indexOf("不提醒"), true);

                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        View updateView = layoutInflater.inflate(R.layout.content, null);
                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                        titlealert.setText("修改清单失败");
                        contentalert.setText("\n" +
                                "\n" +
                                "\n" +
                                "\n        修改清单失败");
                        dialog.setView(updateView);
                        dialog.setPositiveButton("唉，什么app嘛!!",
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
            content.setHint("清单时间为：" + AppApplication.getTouchtime());
        } else {
            DateFormat format = new SimpleDateFormat("yyyy.MM.dd");
            content.setHint("清单时间为：" + format.format(new Date()));
        }

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
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View updateView = layoutInflater.inflate(R.layout.content, null);
                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                    titlealert.setText("创建清单失败");
                    contentalert.setText("\n" +
                            "\n" +
                            "\n" +
                            "\n        创建的清单内容不能为空哟");
                    dialog.setView(updateView);
                    dialog.setPositiveButton("好吧，那我输入信息嘛(*^__^*) ",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                }
                            });
                    dialog.show();
                } else if (sid == 1) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(
                            getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                    View updateView = layoutInflater.inflate(R.layout.content, null);
                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                    titlealert.setText("创建清单失败");
                    contentalert.setText("\n" +
                            "\n" +
                            "\n" +
                            "\n        清单类型不能为自定义哟(⊙o⊙)");
                    dialog.setView(updateView);
                    dialog.setPositiveButton("呵呵",
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
                    String reminderdate = null;
                    //同时添加通知功能，获取提醒spinner中的内容
                    String time = submitbutton.getSelectedItem().toString();

                    //如果不是不提醒或者提醒，那么添加该条信息到广播通知中
                    if (!(time.equals("不提醒") || time.equals("提醒"))) {
                        if (AppApplication.getTouchtime() != null) {
                            reminderdate = AppApplication.getTouchtime();
                        } else {
                            reminderdate = format.format(new Date());
                        }
                        //setReminder(true,reminderdate,time,scontent);
                    }
                    if (AppApplication.getTouchtime() != null) {
                        newtask.setaTime(AppApplication.getTouchtime());
                        AppApplication.setTouchtime(null);
                    } else {
                        newtask.setaTime(format.format(new Date()));
                    }
                    //设置提醒时间
                    newtask.setNotetime(time);
                    newtask.setNtasktime(new Date().getTime());
                    //去数据库中添加数据，同时更新到AppApplication中
                    int rs = new NewtaskController().addTask(newtask);
                    if (rs != -1) {
                        //那么添加该条信息到广播通知中
                        if (!(time.equals("不提醒") || time.equals("提醒"))) {
                            setReminder(true, reminderdate, time, scontent, rs);
                        }
                        Toast.makeText(getActivity(), "创建清单成功", Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getActivity()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
                        content.setText(null);
                        //stypes.setSelection(sid);
                        submitbutton.setSelection(data_list.indexOf("不提醒"), true);
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                        View updateView = layoutInflater.inflate(R.layout.content, null);
                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                        titlealert.setText("创建清单失败");
                        contentalert.setText("\n" +
                                "\n" +
                                "\n" +
                                "\n        创建清单失败(^o^)");
                        dialog.setView(updateView);
                        dialog.setPositiveButton("好吧，什么app嘛/(ㄒoㄒ)/~~",
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
        data_list.add(time);
        submitbutton.setSelection(data_list.indexOf(time), true);
    }

    /**
     * @param b       传入是否添加通知
     * @param rd      2016-01-02 reminder date
     * @param sf      12:04 shi fen
     * @param content 任务内容
     */
    private void setReminder(boolean b, String rd, String sf, String content, int ntid) {
        //设置时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm");
        String alarmtime = rd + "-" + sf;
        Date date = null;
        try {
            date = sdf.parse(alarmtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //首先存储任务通知到数据库中
        TaskAlert taskalert = new TaskAlert();
        taskalert.setNtid(ntid);
        taskalert.setAlertTime(date.getTime());
        taskalert.setAlertContent(content);
        taskalert.setAlertFinish(0);
        taskalert.setCreateTime(new Date().getTime());
        int rs = new TaskAlertController().addTaskAlert(taskalert);
        //创建提醒任务成功
        if (rs != -1) {
            taskalert.setAid(rs);
            //获得AlarmManager实例
            AlarmManager am = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
            //设置需要发送的intent
            Intent intent = new Intent(getActivity(), MyReceiver.class);
            //通过Bundle 传递该对象
            Bundle mExtra = new Bundle();
            mExtra.putSerializable("taskalert", taskalert);
            intent.putExtras(mExtra);
            //创建一个将会运行广播的intent
            // PendingIntent这个类用于处理即将发生的事情
            PendingIntent pi = PendingIntent.getBroadcast(getActivity(), currentIntent++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (b) {
                //设置闹钟的时间，当时间到达后，会触发MyReceiver广播
                am.set(AlarmManager.RTC_WAKEUP, date.getTime(), pi);
            } else {
                // cancel current alarm
                am.cancel(pi);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //如果nt不为空，那么表示当前是修改任务，否则是添加任务
        if (nt != null) {
            //1 设置内容
            Newtask taskfragmenttask = new Newtask();
            taskfragmenttask.setNtId(nt.getNtId());
            taskfragmenttask.setuId(nt.getuId());
            taskfragmenttask.setSid(nt.getSid());
            taskfragmenttask.setNcontent(nt.getNcontent());
            taskfragmenttask.setNfinish(nt.getNfinish());
            taskfragmenttask.setaTime(nt.getaTime());
            taskfragmenttask.setNotetime(nt.getNotetime());
            taskfragmenttask.setNtasktime(nt.getNtasktime());
            String getcontent = content.getText().toString();
            if (!(getcontent.equals("") || getcontent == null)) {
                taskfragmenttask.setNcontent(getcontent);
                //2 设置类型
            }
            taskfragmenttask.setSid(sid);
            String time = submitbutton.getSelectedItem().toString();
            taskfragmenttask.setNotetime(time);
            AppApplication.setUpdatetask(taskfragmenttask);
        } else {
            String getcontent = content.getText().toString();
            if (!(getcontent.equals("") || getcontent == null)) {
                AppApplication.setTaskcontenttemp(getcontent);
            }
            AppApplication.setSid(sid);
            String time = submitbutton.getSelectedItem().toString();
            AppApplication.setAlertstring(time);
        }
    }
}