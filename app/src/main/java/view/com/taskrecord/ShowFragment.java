package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TasktypeController;
import model.Newtask;
import model.TUser;
import myview.MonthDateView;
import myview.MonthDateView.DateClick;
import myview.WeekDayView;
import com.melnykov.fab.FloatingActionButton;
public class ShowFragment extends Fragment {
    private ImageView iv_left;
    private ImageView iv_right;
    private TextView tv_date;
    private TextView tv_week;
    private TextView tv_today;
    private MonthDateView monthDateView;
    private ListView tasklist;
    private String touchTime;//默认为当天时间，就是日历上面当前点击的日期
    private TaskAdapter taskadapter;
    private FloatingActionButton fab;
    private GestureDetector gesture; //手势识别
    private String scontent;//添加内容
    private ArrayList<Newtask> tasks = new ArrayList<Newtask>();
    private ArrayList<Newtask> drawtasks = new ArrayList<Newtask>();
    private List<Integer> list = new ArrayList<Integer>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_show, container, false);

        // 初始化控件
        init(view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });
        tasklist.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });

      fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                final EditText et = new EditText(getActivity());

                et.setHint("请输入内容");
                et.setMinHeight(500);
                et.setBackground(null);
                et.setGravity(Gravity.TOP | Gravity.LEFT);
                dialog.setView(et);
                dialog.setTitle("添加任务时间为: " + touchTime);
                dialog.setNegativeButton("类型",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                dialog.setPositiveButton("创建任务",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        scontent = et.getText().toString();
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
                        }else {
                            // 将前端用户填入的数据插入 到 数据库中
                            // 将注册信息封装到Newtask对象中
                            Newtask newtask = new Newtask();
                            TUser user = AppApplication.getUser();
                            newtask.setuId(user.getuId());
                            newtask.setNcontent(scontent);
                            newtask.setNfinish(0);// 0表示没有完成任务
                            newtask.setaTime(touchTime);//设置任务时间
                            newtask.setNtasktime(new Date().getTime());//设置任务创建时间

                            boolean rs = new NewtaskController().addTask(newtask);

                            if (rs) {
                                // 如果插入成功，跳转到登录界面
                                drawMonthColors(touchTime);
                                tasks = new NewtaskController().searchByTime(AppApplication
                                        .getUser().getuId(), touchTime);
                                taskadapter = new TaskAdapter(AppApplication
                                        .getContext(), R.layout.task_item, tasks);
                                tasklist.setAdapter(taskadapter);
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
                dialog.show();
            }
        });
        monthDateView.setTextView(tv_date, tv_week);
        monthDateView.setDateClick(new DateClick() {
            @Override
            public void onClickOnDate() {
                // 显示当天的任务
				/*
				 * Toast.makeText(AppApplication.getContext(), "点击了：" +
				 * monthDateView.getmSelYear() +
				 * (monthDateView.getmSelMonth()+1) +
				 * monthDateView.getmSelDay(), Toast.LENGTH_SHORT).show();
				 */
                int omonth = monthDateView.getmSelMonth() + 1;
                int oday = monthDateView.getmSelDay();
                String smonth;
                String sday;
                if (omonth >= 0 && omonth <= 9) {
                    smonth = '0' + String.valueOf(omonth);
                } else {
                    smonth = String.valueOf(omonth);
                }
                if (oday >= 0 && oday <= 9) {
                    sday = '0' + String.valueOf(oday);
                } else {
                    sday = String.valueOf(oday);
                }
                String gettime = monthDateView.getmSelYear() + "." + smonth
                        + "." + sday;
                touchTime = new String(gettime);
                // 然后根据日期去数据库查找对应的当日任务
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), gettime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);
            }
        });

        tasklist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Newtask task = tasks.get(position);
                final int positionin = position;
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                dialog.setTitle("任务内容");
                dialog.setMessage(task.getNcontent());
                dialog.setPositiveButton("删除任务",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                new NewtaskController().deleteTaskById(task.getNtId());
                                tasks.remove(positionin);
                                taskadapter.notifyDataSetChanged();
                            }
                        });
                //如果任务完成，那么可以修改为未完成
                if(task.getNfinish() == 1 ) {
                    dialog.setNegativeButton("未完成任务",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    new NewtaskController().changeToNotFinish(task
                                            .getNtId());
                                    String datetime = task.getaTime();
                                    tasks = new NewtaskController().searchByTime(
                                            AppApplication.getUser().getuId(),
                                            datetime);
                                    taskadapter = new TaskAdapter(
                                            AppApplication.getContext(),
                                            R.layout.task_item, tasks);
                                    tasklist.setAdapter(taskadapter);
                                    taskadapter.notifyDataSetChanged();
                                }
                            });
                    //如果任务未完成，可以修改为完成
                }else if(task.getNfinish() == 0){
                    dialog.setNegativeButton("完成任务",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    new NewtaskController().changeToFinish(task
                                            .getNtId());
                                    String datetime = task.getaTime();
                                    tasks = new NewtaskController().searchByTime(
                                            AppApplication.getUser().getuId(),
                                            datetime);
                                    taskadapter = new TaskAdapter(
                                            AppApplication.getContext(),
                                            R.layout.task_item, tasks);
                                    tasklist.setAdapter(taskadapter);
                                    taskadapter.notifyDataSetChanged();
                                }
                            });
                }

                //点击修改该条任务
                dialog.setNeutralButton("修改任务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        AlertDialog.Builder newdialog = new AlertDialog.Builder(
                                getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                        final EditText et = new EditText(getActivity());
                        et.setText(task.getNcontent());
                        et.setSelection(et.getText().length());
                        et.setMinHeight(300);
                        et.setBackground(null);
                        et.setGravity(Gravity.TOP | Gravity.LEFT);
                        newdialog.setView(et);

                        newdialog.setTitle("任务内容");
                        //newdialog.setMessage(task.getNcontent());
                        newdialog.setPositiveButton("取消修改任务",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
										/*new NewtaskController().deleteTaskById(task.getNtId());
										tasks.remove(positionin);
										taskadapter.notifyDataSetChanged();*/
                                    }
                                });
                        newdialog.setNegativeButton("修改任务",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //1、新建一个Task
                                Newtask newtask = new Newtask(task.getNtId(),task.getuId(),task.getSid(),et.getText().toString(),
                                        0,task.getaTime(),task.getNtasktime());
                                //修改任务
                                boolean rs = new NewtaskController().updtateTask(newtask);
                                if(rs) {
                                    String datetime = task.getaTime();
                                    tasks = new NewtaskController().searchByTime(
                                            AppApplication.getUser().getuId(),
                                            datetime);
                                    taskadapter = new TaskAdapter(
                                            AppApplication.getContext(),
                                            R.layout.task_item, tasks);
                                    tasklist.setAdapter(taskadapter);
                                    taskadapter.notifyDataSetChanged();
                                }
								/*new NewtaskController().deleteTaskById(task.getNtId());
								tasks.remove(positionin);
								taskadapter.notifyDataSetChanged();*/
                            }
                        });
                        newdialog.show();
                    }

                });
                dialog.show();
            }
        });
        //2016.01.05
        String datetime = monthDateView.getTodayToView();
        //String newdatetime = datetime.substring(0, datetime.length()-2);
        tasks = new NewtaskController().searchByTime(AppApplication.getUser()
                .getuId(), datetime);
        taskadapter = new TaskAdapter(AppApplication.getContext(),
                R.layout.task_item, tasks);
        tasklist.setAdapter(taskadapter);
        drawMonthColors(datetime);
        setOnlistener();
        return view;
    }

    /**
     * 初始化空间
     */
    private void init(View view) {
        iv_left = (ImageView) view.findViewById(R.id.iv_left);
        iv_right = (ImageView) view.findViewById(R.id.iv_right);
        monthDateView = (MonthDateView) view.findViewById(R.id.monthDateView);
        tv_date = (TextView) view.findViewById(R.id.date_text);
        tv_week = (TextView) view.findViewById(R.id.week_text);
        tv_today = (TextView) view.findViewById(R.id.tv_today);
        tasklist = (ListView) view.findViewById(R.id.tasklist);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        gesture = new GestureDetector(this.getActivity(), new MyOnGestureListener());
        fab.attachToListView(tasklist);

        touchTime = monthDateView.getTodayToView();
    }

    /**
     * 根据tasks画出任务的那天，有红色的小圆点
     *
     * @param tasks
     * @return
     */
    private ArrayList<Integer> getDrawColors(ArrayList<Newtask> tasks) {
        ArrayList<Integer> drawlists = new ArrayList<Integer>();
        int day;
        String sday;
        for (Newtask newtask : tasks) {
            sday = newtask.getaTime();
            sday = sday.substring(sday.length() - 2, sday.length());
            day = Integer.valueOf(sday);
            drawlists.add(Integer.valueOf(day));
        }
        return drawlists;
    }

    private void setOnlistener() {
        iv_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.onLeftClick();
                String gettime = monthDateView.onLeftGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
				/*
				 * Toast.makeText(AppApplication.getContext(), "点击了：" + gettime,
				 * Toast.LENGTH_SHORT).show();
				 */
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), gettime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);
            }
        });

        iv_right.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.onRightClick();
                String gettime = monthDateView.onRightGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
				/*
				 * Toast.makeText(AppApplication.getContext(), "点击了：" + gettime,
				 * Toast.LENGTH_SHORT).show();
				 */
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), gettime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);
            }
        });

        tv_today.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                monthDateView.setTodayToView();
                String datetime = monthDateView.getTodayToView();
                touchTime = new String(datetime);
                drawMonthColors(datetime);
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), datetime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);

            }
        });
    }
    private void drawMonthColors(String gettime) {
        String newgettime = gettime.substring(0, gettime.length()-2);
        drawtasks = new NewtaskController().searchDrawByTime(AppApplication.getUser()
                .getuId(), newgettime);
        //为任务当月画上小圆点
        if (drawtasks != null) {
            list = getDrawColors(drawtasks);
            monthDateView.setDaysHasThingList(list);
        }
    }
    class TaskAdapter extends ArrayAdapter<Newtask> {
        private int resourceId;

        public TaskAdapter(Context context, int resource, List<Newtask> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            Newtask anewtask = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId,
                        null);
                viewHolder = new ViewHolder();
                // match
                viewHolder.tasknumber = (TextView) view
                        .findViewById(R.id.tasknumber);
                viewHolder.taskcontent = (TextView) view
                        .findViewById(R.id.taskcontent);
                viewHolder.tasktype = (TextView) view.findViewById(R.id.tasktype);
                viewHolder.taskfinish = (TextView) view
                        .findViewById(R.id.taskfinish);
                viewHolder.tasktime = (TextView) view
                        .findViewById(R.id.tasktime);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            // viewHolder.tasknumber.setText(String.valueOf(anewtask.getNtId())+"、");
            viewHolder.tasknumber.setText(String.valueOf(position + 1) + "、");
            viewHolder.taskcontent.setText(anewtask.getNcontent());
            String type = new TasktypeController().getTstyleBySid(anewtask.getSid());
            viewHolder.tasktype.setText(type);
            if (anewtask.getNfinish() == 1) {
                viewHolder.taskfinish.setText("已完成");
                viewHolder.taskfinish.setTextColor(Color.RED);
            } else {
                viewHolder.taskfinish.setText("未完成");
                viewHolder.taskfinish.setTextColor(Color.WHITE);
            }
            viewHolder.tasktime.setText(anewtask.getaTime());
            if(position%2 == 0 ) {
                view.setBackgroundResource(R.drawable.listitembgcolor1);
            }else if(position%2 == 1 ) {
                view.setBackgroundResource(R.drawable.listitembgcolor2);
            }
            return view;
        }

        class ViewHolder {
            TextView tasknumber;
            TextView taskcontent;
            TextView tasktype;
            TextView taskfinish;
            TextView tasktime;
        }
    }
    //设置手势识别监听器
    private class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener
    {

        @Override//此方法必须重写且返回真，否则onFling不起效
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if((e1.getX()- e2.getX()>120)&&Math.abs(velocityX)>200){
                monthDateView.onRightClick();
                String gettime = monthDateView.onRightGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
				/*
				 * Toast.makeText(AppApplication.getContext(), "点击了：" + gettime,
				 * Toast.LENGTH_SHORT).show();
				 */
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), gettime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);
                return true;
            }else if((e2.getX()- e1.getX()>120)&&Math.abs(velocityX)>200){

                monthDateView.onLeftClick();
                String gettime = monthDateView.onLeftGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
				/*
				 * Toast.makeText(AppApplication.getContext(), "点击了：" + gettime,
				 * Toast.LENGTH_SHORT).show();
				 */
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), gettime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);
                return true;
            }
            return false;
        }
    }
}