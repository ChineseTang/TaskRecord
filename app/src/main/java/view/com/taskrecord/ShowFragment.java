package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TaskAlertController;
import model.Newtask;
import myadapter.TaskAdapter;
import myview.MonthDateView;
import myview.MonthDateView.DateClick;
public class ShowFragment extends Fragment {
    private TextView iv_left;
    private TextView iv_right;
    private TextView tv_date;
    private TextView tv_week;
    private TextView tv_today;
    private MonthDateView monthDateView;
    private ListView tasklist;
    private String touchTime;//默认为当天时间，就是日历上面当前点击的日期
    private TaskAdapter taskadapter;
    private FloatingActionButton fab;//悬浮按钮
    private FragmentTabHost fth;//设置跳转到添加事件界面
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
        //用于添加新的任务，传递时间参数
      fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击跳转到TaskFragment界面中去，同时将时间参数传递过去
                fth.setup(getActivity(), getActivity().getSupportFragmentManager(), R.id.realtabcontent);
                AppApplication.setTouchtime(touchTime);
                AppApplication.setUpdatetask(null);
                fth.setCurrentTab(1);
            }
        });
        //设置日期 和 周数
        monthDateView.setTextView(tv_date, tv_week);
        monthDateView.setDateClick(new DateClick() {
            @Override
            public void onClickOnDate() {
                // 显示当天的任务
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
                //然后根据日期去AppApplication中的tasks中去遍历
                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), gettime);
                setTasks();
            }
        });

        tasklist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                final Newtask task = tasks.get(position);
                final int positionin = position;
                final AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View updateView = layoutInflater.inflate(R.layout.content, null);
                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                titlealert.setText("清单内容");
                contentalert.setText("        " + task.getNcontent());
                dialog.setView(updateView);
                dialog.setPositiveButton("删除清单",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                String datetime = task.getaTime();
                               // Log.w("taskdelete",datetime);
                                new NewtaskController().deleteTaskById(task.getNtId());
                                //将该任务的所有通知全部修改成已经提醒的状态
                                new TaskAlertController().ChangeToFinishByNtid(task.getNtId());
                                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), datetime);
                                //重新设置任务
                                setTasks();
                                drawMonthColors(datetime);
                                taskadapter.notifyDataSetChanged();
                                monthDateView.invalidate();

                            }
                        });
                //如果任务完成，那么可以修改为未完成
                if(task.getNfinish() == 1 ) {
                    dialog.setNegativeButton("未完成清单",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //先在数据库中修改未完成任务的状态
                                    new NewtaskController().changeToNotFinish(task.getNtId());
                                    String datetime = task.getaTime();
                                    tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(),datetime);
                                    setTasks();
                                    taskadapter.notifyDataSetChanged();
                                }
                            });
                    //如果任务未完成，可以修改为完成
                }else if(task.getNfinish() == 0){
                    dialog.setNegativeButton("完成清单",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //先在数据库中修改任务的状态
                                    new NewtaskController().changeToFinish(task.getNtId());
                                    String datetime = task.getaTime();
                                    tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(),datetime);
                                    setTasks();
                                    taskadapter.notifyDataSetChanged();
                                }
                            });
                }
                //点击修改该条任务
                dialog.setNeutralButton("修改清单", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        fth.setup(getActivity(), getActivity().getSupportFragmentManager(), R.id.realtabcontent);
                        AppApplication.setUpdatetask(task);//把任务传递过去
                        fth.setCurrentTab(1);
                    }

                });
                dialog.show();
            }
        });
        //2016.01.05
        String datetime = monthDateView.getTodayToView();
        tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), datetime);
        //重新设置任务
        setTasks();
        drawMonthColors(datetime);
        setOnlistener();
        return view;
    }
    //设置任务tasklist
    private void setTasks() {
        taskadapter = new TaskAdapter(AppApplication.getContext(),R.layout.task_item, tasks);
        tasklist.setAdapter(taskadapter);
        setListViewHeightBasedOnChildren(tasklist);
    }
    /**
     * 初始化控件
     */
    private void init(View view) {
        iv_left = (TextView) view.findViewById(R.id.iv_left);
        iv_right = (TextView) view.findViewById(R.id.iv_right);
        monthDateView = (MonthDateView) view.findViewById(R.id.monthDateView);
        tv_date = (TextView) view.findViewById(R.id.date_text);
        tv_week = (TextView) view.findViewById(R.id.week_text);
        tv_today = (TextView) view.findViewById(R.id.tv_today);
        tasklist = (ListView) view.findViewById(R.id.tasklist);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        gesture = new GestureDetector(this.getActivity(), new MyOnGestureListener());
        fab.attachToListView(tasklist);
        //fab.getBackground().setAlpha(180);
        fth = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
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

    /**
     * 设置监听事件，向左，向右和当前日期
     */
    private void setOnlistener() {
        iv_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.onLeftClick();
                String gettime = monthDateView.onLeftGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), gettime);
                setTasks();
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
                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), gettime);
                setTasks();

            }
        });
        tv_today.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.setTodayToView();
                String datetime = monthDateView.getTodayToView();
                touchTime = new String(datetime);
                drawMonthColors(datetime);
                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), datetime);
                setTasks();
            }
        });
    }
    private void drawMonthColors(String gettime) {
        String newgettime = gettime.substring(0, gettime.length()-2);
        //Log.w("newgettime",newgettime);
        drawtasks = AppApplication.searchDrawByTime(AppApplication.getUser()
                .getuId(), newgettime);
        //为任务当月画上小圆点
        if (drawtasks != null) {
            list = getDrawColors(drawtasks);
            monthDateView.setDaysHasThingList(list);
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
                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), gettime);
                setTasks();
                return true;
            }else if((e2.getX()- e1.getX()>120)&&Math.abs(velocityX)>200){

                monthDateView.onLeftClick();
                String gettime = monthDateView.onLeftGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
                tasks = AppApplication.searchByTime(AppApplication.getUser().getuId(), gettime);
                setTasks();
                return true;
            }
            return false;
        }
    }
    /**
     * 设置ListView的高度
     * @param listView
     */
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}