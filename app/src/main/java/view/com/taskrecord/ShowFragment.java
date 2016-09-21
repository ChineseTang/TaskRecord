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
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import controller.AppApplication;
import controller.NewtaskController;
import model.Newtask;
import myview.MonthDateView;
import myview.MonthDateView.DateClick;
import com.melnykov.fab.FloatingActionButton;
import myadapter.TaskAdapter;
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
    private FragmentTabHost fth;
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
      /*  view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);//返回手势识别触发的事件
            }
        });*/
      fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击跳转到TaskFragment界面中去，同时将时间参数传递过去
                fth.setup(getActivity(), getActivity().getSupportFragmentManager(), R.id.realtabcontent);
                AppApplication.setTouchtime(touchTime);
                fth.setCurrentTab(1);
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
                setListViewHeightBasedOnChildren(tasklist);
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
                EditText et = new EditText(getActivity());
                et.setText(task.getNcontent());
                et.setEnabled(false);
                et.setBackground(null);
                et.setGravity(Gravity.TOP | Gravity.LEFT);
                et.setMinHeight(450);
                et.setTextColor(Color.rgb(51, 51, 51));
                dialog.setView(et);
                dialog.setTitle("任务内容");
                //dialog.setMessage(task.getNcontent());
                dialog.setPositiveButton("删除任务",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                new NewtaskController().deleteTaskById(task.getNtId());
                                tasks.remove(positionin); //monthDateView.postInvalidate();
                                setListViewHeightBasedOnChildren(tasklist);
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
                                    setListViewHeightBasedOnChildren(tasklist);
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
                                    setListViewHeightBasedOnChildren(tasklist);
                                    taskadapter.notifyDataSetChanged();
                                }
                            });
                }

                //点击修改该条任务
                dialog.setNeutralButton("修改任务", new DialogInterface.OnClickListener() {
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
        //String newdatetime = datetime.substring(0, datetime.length()-2);
        tasks = new NewtaskController().searchByTime(AppApplication.getUser()
                .getuId(), datetime);
        taskadapter = new TaskAdapter(AppApplication.getContext(),
                R.layout.task_item, tasks);
        tasklist.setAdapter(taskadapter);
        setListViewHeightBasedOnChildren(tasklist);
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

    private void setOnlistener() {
        iv_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                monthDateView.onLeftClick();
                String gettime = monthDateView.onLeftGetTime();
                touchTime = new String(gettime);
                drawMonthColors(gettime);
                tasks = new NewtaskController().searchByTime(AppApplication
                        .getUser().getuId(), gettime);
                taskadapter = new TaskAdapter(AppApplication
                        .getContext(), R.layout.task_item, tasks);
                tasklist.setAdapter(taskadapter);
                setListViewHeightBasedOnChildren(tasklist);
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
                setListViewHeightBasedOnChildren(tasklist);
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
                //设置listview的高度
                setListViewHeightBasedOnChildren(tasklist);

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
}