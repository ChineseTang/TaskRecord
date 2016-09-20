package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import controller.AppApplication;
import controller.NewtaskController;
import model.Newtask;
import myadapter.TaskAdapter;
public class SearchFragment extends Fragment {
    private ArrayList<Newtask> tasks = new ArrayList<Newtask>();
  private Button searchComplete;
    private Button searchAlert;
    private Button searchWay;
    private Button searchTime;
   // private Spinner searchType;
    //private ArrayAdapter taskserachtypeAdapter;
    //private ArrayList<String> searchtype_list;
    private ListView lv;
    PieData mPieData;
    private PieChart mChart;
    private TaskAdapter taskadapter;
    private String[] completes = {"已完成","未完成","过期"};
    private String[] alerts = {"提醒","不提醒"};
    private String[] kindssearch;//分类查询
    private int complete = 0;
    private int[] NotCompleted = {0,0};
    //查询类型，0表示所有任务查询，1表示提醒 和不提醒的，2表示按类型进行查询的，3表示按时间查询的
    private int searchway = 0;
    private int searchwaystate = -1; //0表示选择已完成，1表示未完成，2表示过期
    private int alertnumber = 0;
    private int notalertnumber = 0;
    private int[] conditiontypes ={0,0,0};//完成情况，完成，未完成，逾期
    private int tag = 0;//用于标记目前是哪个界面，是所有，未完，还是已完，用于在删除时判断跳转到哪个界面，默认是0
    //表示所有界面，1是完成，-1是未完成
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_search, container,false);
        init(view);
        //mChart.setSaveEnabled(false);
        searchComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.设置查询的类型
                searchComplete.setTextColor(Color.rgb(250,167,50));
                searchAlert.setTextColor(Color.WHITE);
                searchWay.setTextColor(Color.WHITE);
                searchTime.setTextColor(Color.WHITE);
                searchway = 0;
                //2.设置图形的显示情况
                showCompelte();
                mChart.postInvalidate();
            }
        });

        searchAlert.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchComplete.setTextColor(Color.WHITE);
                searchAlert.setTextColor(Color.rgb(250,167,50));
                searchWay.setTextColor(Color.WHITE);
                searchTime.setTextColor(Color.WHITE);
                //1.设置查询的类型
                searchway = 1;
                //2.设置图形的显示情况
                showAlert();
                //mChart.performClick();
                mChart.postInvalidate();
            }
        });

        searchWay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchComplete.setTextColor(Color.WHITE);
                searchAlert.setTextColor(Color.WHITE);
                searchWay.setTextColor(Color.rgb(250,167,50));
                searchTime.setTextColor(Color.WHITE);
                //1.设置查询的类型
                searchway = 2;
                //2.设置图形的显示情况


            }
        });

        searchTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchComplete.setTextColor(Color.WHITE);
                searchAlert.setTextColor(Color.WHITE);
                searchWay.setTextColor(Color.WHITE);
                searchTime.setTextColor(Color.rgb(250,167,50));
                //1.设置查询的类型
                searchway = 3;
                //2.设置图形的显示情况
            }
        });
        //选择所有的任务
        /*searchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //根据选择的内容进行选择
                *//**
                 searchtype_list.add("完成情况");
                 searchtype_list.add("提醒情况");
                 searchtype_list.add("分类查询");
                 searchtype_list.add("日期查询");
                 *//*
                String chooseSearchType = searchtype_list.get(position);
                if(chooseSearchType.equals("完成情况")){
                    searchway = 0;
                    tasks = new NewtaskController().searchAlertTasks(AppApplication.getUser().getuId());
                    lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                }else if(chooseSearchType.equals("提醒情况")){
                    searchway = 1;
                    alertnumber = new NewtaskController().searchAlertTasksNumber(AppApplication.getUser().getuId());
                    notalertnumber = new NewtaskController().searchNotAlertTasksNumber(AppApplication.getUser().getuId());
                }else if(chooseSearchType.equals("分类查询")){
                    searchway = 2;
                }else if(chooseSearchType.equals("日期查询")){
                    searchway = 3;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
*/

        //对每一项作一个任务事件处理，点击则可以新建一个AlertBuilder显示一个任务的所有内容。
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final Newtask task = tasks.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                dialog.setTitle("任务内容");
                //添加EditText 设置格式
                EditText et = new EditText(getActivity());
                et.setText(task.getNcontent());
                et.setEnabled(false);
                et.setBackground(null);
                et.setGravity(Gravity.TOP | Gravity.LEFT);
                et.setMinHeight(450);
                et.setTextColor(Color.rgb(51, 51, 51));
                dialog.setView(et);
                //dialog.setMessage(task.getNcontent());
                dialog.setPositiveButton("删除任务",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //删除任务
                        new NewtaskController().deleteTaskById(task.getNtId());

                        //根据当前状态
                        if(tag == 0) {
                            tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());
                        }else if(tag == 1) {
                            tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                        }else if(tag == -1) {
                            tasks = new NewtaskController().searchNotFinishTasks(AppApplication.getUser().getuId());
                        }
                        lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
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
                                    if(tag == 0) {
                                        tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());
                                    }else if(tag == 1) {
                                        tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                                    }else if(tag == -1) {
                                        tasks = new NewtaskController().searchNotFinishTasks(AppApplication.getUser().getuId());
                                    }
                                    lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                                }
                            });
                    //如果任务未完成，可以修改为完成
                }else if(task.getNfinish() == 0){
                    dialog.setNegativeButton("完成任务",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    new NewtaskController().changeToFinish(task.getNtId());
                                    //tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                                    if(tag == 0) {
                                        tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());
                                    }else if(tag == 1) {
                                        tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                                    }else if(tag == -1) {
                                        tasks = new NewtaskController().searchNotFinishTasks(AppApplication.getUser().getuId());
                                    }
                                    lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));


                                }
                            });
                }
                //点击完成该条任务
                dialog.setNeutralButton("修改任务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        FragmentTabHost fth = (FragmentTabHost) getActivity().findViewById(android.R.id.tabhost);
                        fth.setup(getActivity(), getActivity().getSupportFragmentManager(), R.id.realtabcontent);
                        AppApplication.setUpdatetask(task);//把任务传递过去
                        fth.setCurrentTab(1);
                    }

                });
                dialog.show();
            }
        });
      /*  allbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tag = 0;
                tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());
                lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
            }
        });
        notfinishbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tag = -1;
                tasks = new NewtaskController().searchNotFinishTasks(AppApplication.getUser().getuId());
                lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
            }
        });
        finishbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tag = 1;
                tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
            }
        });*/
       //对饼图添加事件处理 设置一个选中区域监听
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //设置处理，按类型查询
                if (searchway == 0){
                    switch (e.getXIndex()) {
                        case 0:
                            //已完成
                            searchwaystate = 0;
                            tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        case 1:
                            //未完成
                            searchwaystate = 1;
                            tasks = new NewtaskController().searchNotCompleteTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        case 2:
                            //过期searchOverdueTasks
                            searchwaystate = 2;
                            tasks = new NewtaskController().searchOverdueTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        default:
                            break;
                    }
                }else if(searchway == 1) {
                    switch (e.getXIndex()) {
                        case 0:
                            searchwaystate = 0;
                            tasks = new NewtaskController().searchAlertTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        case 1:
                            searchwaystate = 1;
                            tasks = new NewtaskController().searchNotAlertTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        default:
                            break;
                    }
                }
                //设置描述
               // mChart.setDescription(completes[e.getXIndex()] + " " + (int)e.getVal() + "分");
            }
            @Override
            public void onNothingSelected() {
            }
        });
        return view;
    }

    /**
     * 初始化控件
     * @param view
     */
    private void init(View view) {
        //tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());
        lv = (ListView) view.findViewById(R.id.alltask);
       searchComplete = (Button) view.findViewById(R.id.searchComplete);
        searchAlert = (Button) view.findViewById(R.id.searchAlert);
        searchWay = (Button) view.findViewById(R.id.searchWay);
        searchTime = (Button) view.findViewById(R.id.searchTime);
        //searchType = (Spinner) view.findViewById(R.id.searchType);
        /*searchtype_list = new ArrayList<String>();
        searchtype_list.add("完成情况");
        searchtype_list.add("提醒情况");
        searchtype_list.add("分类查询");
        searchtype_list.add("日期查询");
        taskserachtypeAdapter = new ArrayAdapter(AppApplication.getContext(),R.layout.tasktype_item,searchtype_list);
        taskserachtypeAdapter.setDropDownViewResource(R.layout.tasktype_item);
        searchType.setAdapter(taskserachtypeAdapter);*/
        //设置饼状图
        mChart = (PieChart) view.findViewById(R.id.taskpc);

        //默认显示所有任务的饼图
        showCompelte();
    }

    /**
     * 显示所有任务的饼图，里面有完成，未完成和过期三种类型的任务
     */
    private void showCompelte() {
        switch (searchwaystate) {
            case -1:
                tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());
                break;
            case 0:
                tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                break;
            case 1:
                tasks = new NewtaskController().searchNotCompleteTasks(AppApplication.getUser().getuId());
                break;
            case 2:
                tasks = new NewtaskController().searchOverdueTasks(AppApplication.getUser().getuId());
                break;
            default:
                break;
        }

        //完成的数目
        complete = new NewtaskController().searchCompleted(AppApplication.getUser().getuId());
        //未完成和过期数目
        NotCompleted =  new NewtaskController().searchNotCompleted(AppApplication.getUser().getuId());
        conditiontypes[0] = complete;//完成
        conditiontypes[1] = NotCompleted[0];//未完成
        conditiontypes[2] = NotCompleted[1];//逾期
        //总任务数目
        int sum = conditiontypes[0] + conditiontypes[1] + conditiontypes[2];
        //默认显示完成，未完成，过期这三种类型的任务
        mPieData = getPieData(3,completes,conditiontypes,100);
        //展示图形
        showChart(mChart, mPieData,sum);
        //设置listview的适配器，默认显示所有任务,根据用户选择选择不同类型的任务
        taskadapter = new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks);
        lv.setAdapter(taskadapter);
        //设置listview的高度
        setListViewHeightBasedOnChildren(lv);
    }

    /**
     * 设置提醒和未提醒 饼图
     * @param
     * @param
     * @param
     */
    private void showAlert() {
        switch (searchwaystate) {
            case -1:
                tasks = new NewtaskController().searchNotAlertTasks(AppApplication.getUser().getuId());
                break;
            case 0:
                tasks = new NewtaskController().searchAlertTasks(AppApplication.getUser().getuId());
                break;
            case 1:
                tasks = new NewtaskController().searchNotAlertTasks(AppApplication.getUser().getuId());
                break;
            default:
                break;
        }
        //查询提醒的数目
        alertnumber = new NewtaskController().searchAlertTasksNumber(AppApplication.getUser().getuId());
        //查询未提醒的数目
        notalertnumber = new NewtaskController().searchNotAlertTasksNumber(AppApplication.getUser().getuId());
        //查询提醒任务

        //总任务数目
        int sum = alertnumber + notalertnumber;
        NotCompleted[0] = alertnumber;
        NotCompleted[1] = notalertnumber;
        //默认显示完成，未完成，过期这三种类型的任务
        mPieData = getPieData(2,alerts,NotCompleted,100);
        //展示图形
        showChart(mChart, mPieData,sum);
        //设置listview的适配器，默认显示所有任务,根据用户选择选择不同类型的任务
        taskadapter = new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks);
        lv.setAdapter(taskadapter);
        //设置listview的高度
        setListViewHeightBasedOnChildren(lv);
    }
    private void showChart(PieChart pieChart, PieData pieData,int sum) {
        //pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(60f);  //半径
        pieChart.setTransparentCircleRadius(64f); // 半透明圈
        //pieChart.setHoleRadius(0)  //实心圆
        //pieChart.setDescription("空空任务");
        pieChart.setDescription(" ");
        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字
        pieChart.setDrawHoleEnabled(true);
        pieChart.setRotationAngle(90); // 初始旋转角度
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        // draws the corresponding description value into the slice
        // mChart.setDrawXValues(true);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转
        // display percentage values
        pieChart.setUsePercentValues(true);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);
        // add a selection listener
//      mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);
//      mChart.setOnAnimationListener(this);
        pieChart.setCenterText("所有任务\n" + sum + "件");  //饼状图中间的文字
        //设置数据
        pieChart.setData(pieData);
        //设置饼图右下角的文字大小
       // pieChart.setDescriptionTextSize(20);
        pieChart.setCenterTextSize(16f);
        pieChart.setCenterTextColor(Color.rgb(51, 51, 51));
        // undo all highlights
//      pieChart.highlightValues(null);
//      pieChart.invalidate();
        //pieChart.setSliceSpace(1f);
        Legend mLegend = pieChart.getLegend();  //设置比例图
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);  //最右边显示RIGHT_OF_CHART
//      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setTextColor(Color.rgb(144, 144, 144));
        mLegend.setTextSize(12f);
        mLegend.setXEntrySpace(4f);
        mLegend.setYEntrySpace(3f);
        //pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
    }
    /**
     *
     * @param count 分成几部分
     * @param range
     */
    private PieData getPieData(int count, String[] kinds,int[] rates,float range) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
      /*  for (int i = 0; i < count; i++) {
            xValues.add("Quarterly" + (i + 1));  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
        }*/
        for(int i=0; i < count;i++) {
            xValues.add(kinds[i] + rates[i] + "件");
        }
        /*xValues.add("未完成");
        xValues.add("已完成");
        xValues.add("不提醒");
        xValues.add("提醒");*/
        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据
        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         */
      /*  float quarterly1 = 14;
        float quarterly2 = 14;
        float quarterly3 = 34;
        float quarterly4 = 38;*/
        float[] quarterly = new float[count];
        float sum = 0.0f;
        for(int i = 0 ; i< rates.length; i++ ) {
            sum += rates[i];
        }
        for(int i = 0; i< rates.length; i++) {
            quarterly[i] = rates[i]/sum;
            Log.w("task:quarterly" + i,String.valueOf(quarterly[i]));
            yValues.add(new Entry(quarterly[i], i));
        }

        /*yValues.add(new Entry(quarterly1, 0));
        yValues.add(new Entry(quarterly2, 1));
        yValues.add(new Entry(quarterly3, 2));
        yValues.add(new Entry(quarterly4, 3));*/

        //y轴的集合  /*显示在比例图上*/
       // PieDataSet pieDataSet = new PieDataSet(yValues, "所有的任务");
        PieDataSet pieDataSet = new PieDataSet(yValues, " ");

        //pieDataSet.notifyDataSetChanged();

        pieDataSet.setSliceSpace(3f); //设置个饼状图之间的距离
        // 部分区域被选中时多出的长度
        pieDataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        pieDataSet.setColors(colors);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = 5 * (metrics.densityDpi / 160f);
        pieDataSet.setSelectionShift(px); // 选中态多出的长度

        PieData pieData = new PieData(xValues, pieDataSet);
        //设置百分比
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.rgb(144, 144, 144));
        //pieData.setValueTypeface(tf);
        return pieData;
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