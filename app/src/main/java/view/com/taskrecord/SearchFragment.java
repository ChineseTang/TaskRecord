package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;

import controller.AppApplication;
import controller.NewtaskController;
import controller.TaskAlertController;
import controller.TasktypeController;
import model.Newtask;
import myadapter.TaskAdapter;
public class SearchFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private ArrayList<Newtask> tasks = new ArrayList<Newtask>();
  private Button searchComplete;//按完成情况查询
    private Button searchAlert;//按照提醒情况查询
    private Button searchWay;//按照类型查询
    private Button searchTime;//按照月份查询
    private Button searchTimeDay;//按照日期查询
    public static final String DATEPICKER_TAG = "datepicker";//设置显示日历
    private ListView lv;//显示任务的列表
    PieData mPieData;//显示数据
    private PieChart mChart;//显示完成情况的饼图
    private PieChart alertBarChart;//显示提醒的饼图
    private PieChart timePieChart;//显示月份查询的饼图
    private BarChart typesearchBarChart;//显示按照类型查询的柱状图
    private ArrayList<Newtask> monthtasks = new ArrayList<Newtask>();//存储按月查询的任务
    private TaskAdapter taskadapter;//适配器
    private String[] completes = {"已完成","未完成","过期"};
    private String[] completes2 = {"已完成","未完成"};
    private String[] completes3 = {"已完成","过期"};
    private String[] alerts = {"提醒","不提醒"};
    private String[] kindssearch;//分类查询
    private int complete = 0;
    private int[] NotCompleted = {0,0};
    private int[] timeSetComplet = {0,0};
    //查询类型，0表示完成查询，1表示提醒查询，2表示类型查询，3表示按月份查询，4按日期查询
    private int searchway = 0;
    private int searchwaystate = -1; //0表示选择了完成，1表示选择了未完成，2表示选择了过期
    private int alertnumber = 0;
    private int notalertnumber = 0;
    private int[] conditiontypes ={0,0,0};//完成情况，完成，未完成，逾期
    private int[] conditiontypes2 ={0,0};//完成情况，完成，未完成
    private int[] conditiontypes3 ={0,0};//完成情况，完成，逾期
    private ArrayList<String> xVals;
    private View vline;
    private int tag = 0;//用于标记目前是哪个界面，是所有，未完，还是已完，用于在删除时判断跳转到哪个界面，默认是0
    //表示所有界面，1是完成，-1是未完成
    private int completeflag = 0;
    private SharedPreferences pref;//用于记录个人信息，是否记住密码
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_search, container,false);
        init(view);
        //mChart.setSaveEnabled(false);
        //设置完成的监听
        searchCompleteSetListener();
        //设置提醒的监听
        searchAlertSetListener();
        //设置查询方式的监听
        searchwaySetListener();
        //设置查询月份的监听
        searchTimeSetListener();
        //设置查询日期的监听
        searchTimeDaySetListener();
        //对每一项作一个任务事件处理，点击则可以新建一个AlertBuilder显示一个任务的所有内容。
        lvsetOnItemClickListener();
       //对饼图添加事件处理 设置一个选中区域监听
        mChatsetOnChartValueSelectedListener();
        typesearchBCSelectedListener();
        return view;
    }
    /**
     * 初始化控件
     * @param view
     */
    private void init(View view) {
        lv = (ListView) view.findViewById(R.id.alltask);
       searchComplete = (Button) view.findViewById(R.id.searchComplete);
        searchAlert = (Button) view.findViewById(R.id.searchAlert);
        searchWay = (Button) view.findViewById(R.id.searchWay);
        searchTime = (Button) view.findViewById(R.id.searchTime);
        searchTimeDay = (Button) view.findViewById(R.id.searchTimeDay);
        vline = view.findViewById(R.id.line);
        //设置饼状图
        mChart = (PieChart) view.findViewById(R.id.taskpc);
        alertBarChart = (PieChart) view.findViewById(R.id.alertBarChart);
        timePieChart = (PieChart) view.findViewById(R.id.timePieChart);
        //设置BarChart
        typesearchBarChart = (BarChart) view.findViewById(R.id.typesearchBarChart);
        typesearchBarChart.setDrawBarShadow(false);
        typesearchBarChart.setDrawValueAboveBar(true);
        typesearchBarChart.setDescription("");
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        typesearchBarChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        typesearchBarChart.setPinchZoom(false);
        typesearchBarChart.setDrawGridBackground(false);
        //设置网格
        typesearchBarChart.setDrawBorders(false);
        //设置透明度
        typesearchBarChart.setAlpha(0.8f);
        //设置是否可以触摸，如为false，则不能拖动，缩放等
        //设置是否可以拖拽，缩放
        typesearchBarChart.setDragEnabled(true);
        typesearchBarChart.setScaleEnabled(true);
        //设置是否能扩大扩小
        typesearchBarChart.setPinchZoom(true);
        mChart.setTouchEnabled(true);
        alertBarChart.setTouchEnabled(true);
        timePieChart.setTouchEnabled(true);
        //默认显示所有任务的饼图
        typesearchBarChart.setVisibility(View.GONE);
        mChart.setVisibility(View.VISIBLE);
        alertBarChart.setVisibility(View.GONE);
        timePieChart.setVisibility(View.GONE);
        xVals = new ArrayList<String>();
        pref = getActivity().getSharedPreferences("data", getActivity().MODE_PRIVATE);
        switch (AppApplication.getSearchpage()) {
            case 0:
                setSearchCompleteMethod();
                break;
            case 1:
                searchAlertMethod();
                break;
            case 2:
                searchWayMethod();
                break;
            case 3:
                searchTimeCallMethod();
                break;
            case 4:
                searchTimeDayMethod();
                break;
            default:break;

        }

    }

    /**
     * 显示所有任务的饼图，里面有完成，未完成和过期三种类型的任务
     */
    private void showCompelte() {
        //完成的数目
        //complete = new NewtaskController().searchCompleted(AppApplication.getUser().getuId());
        complete = AppApplication.searchCompleted(AppApplication.getUser().getuId());
        //未完成和过期数目
        //NotCompleted =  new NewtaskController().searchNotCompleted(AppApplication.getUser().getuId());
        NotCompleted = AppApplication.searchNotCompleted(AppApplication.getUser().getuId());
        conditiontypes[0] = complete;//完成
        conditiontypes[1] = NotCompleted[0];//未完成
        conditiontypes[2] = NotCompleted[1];//逾期
        conditiontypes2[0] = complete;
        conditiontypes2[1] = NotCompleted[0];
        conditiontypes3[0] = complete;
        conditiontypes3[1] = NotCompleted[1];
        //总任务数目
        int sum = conditiontypes[0] + conditiontypes[1] + conditiontypes[2];
        //默认显示完成，未完成，过期这三种类型的任务
        //如果过期等于0 ，那么就显示 完成 和 未完成的
        if(NotCompleted[1] == 0) {
            completeflag = 0;
            mPieData = getPieData(2, completes2, conditiontypes2, 100);
        }else if(NotCompleted[0] == 0) {//如果未完成为0，那么显示已完成和过期
            //这里设置一个标志位，用于查询时，如果是只显示已完成和过期，当点击过期时，将过期的任务
            //显示出来
            completeflag = 1;
            mPieData = getPieData(2, completes3, conditiontypes3, 100);
        }else{
            completeflag = 0;
            mPieData = getPieData(3, completes, conditiontypes, 100);
        }
        //展示图形
        showChart(mChart, mPieData,sum);
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
                tasks = null;
                break;
            case 0:
                tasks = AppApplication.searchAlertTasks(AppApplication.getUser().getuId());
                break;
            case 1:
                tasks = AppApplication.searchNotAlertTasks(AppApplication.getUser().getuId());
                break;
            default:
                break;
        }
        //查询提醒的数目
        alertnumber = AppApplication.searchAlertTasksNumber(AppApplication.getUser().getuId());
        //查询未提醒的数目
        notalertnumber = AppApplication.searchNotAlertTasksNumber(AppApplication.getUser().getuId());
        //查询提醒任务

        //总任务数目
        int sum = alertnumber + notalertnumber;
        NotCompleted[0] = alertnumber;
        NotCompleted[1] = notalertnumber;
        //默认显示完成，未完成，过期这三种类型的任务
        mPieData = getPieData(2,alerts,NotCompleted,100);
        //展示图形
        showChart(alertBarChart, mPieData,sum);
        //设置listview的适配器，默认显示所有任务,根据用户选择选择不同类型的任务
        if(tasks != null ) {
            taskadapter = new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks);
            lv.setAdapter(taskadapter);
            //设置listview的高度
            setListViewHeightBasedOnChildren(lv);
        }

    }
    private void showChart(PieChart pieChart, PieData pieData,int sum) {

        pieChart.setNoDataTextDescription("您需要提供数据");
        //pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(48f);  //半径
        pieChart.setTransparentCircleRadius(30f); // 半透明圈
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
        //pieChart.setTransparentCircleRadius(61f);
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
        pieChart.setCenterText("所有清单\n" + sum + "件");  //饼状图中间的文字
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
        mLegend.setTextSize(10f);
        mLegend.setXEntrySpace(6f);
        mLegend.setYEntrySpace(3f);
        boolean isRember = pref.getBoolean("dynamic", true);
        if(isRember) {
            pieChart.animateXY(1000, 1000);  //设置动画
        }

        // mChart.spin(2000, 0, 360);
    }
    /**
     *
     * @param count 分成几部分
     * @param range
     */
    private PieData getPieData(int count, String[] kinds,int[] rates,float range) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容
        for(int i=0; i < count;i++) {
            xValues.add(kinds[i] + rates[i] + "件");
        }
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
            //Log.w("task:quarterly" + i,String.valueOf(quarterly[i]));
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
        pieDataSet.setSelectionShift(4f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        /*for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);*/
        colors.add(Color.rgb(255, 247, 140));
        colors.add(Color.rgb(140, 234, 255));
        colors.add(Color.rgb(255, 228, 224));
       /* for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);*/
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
    /**
     * 设置柱状图的数据
     * @param count
     * @param range
     */
    private void setData(int count, float range,int sum) {
        //1 找到所有的类型，这是x轴的数据集
        typesearchBarChart.setNoDataTextDescription("您需要提供数据");
        xVals = new TasktypeController().selectAllTypesWithoutSignal(AppApplication.getUser().getuId());
        count = xVals.size();
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();//设置y轴值
        String type = null;//类型
        int sid = -1;//类型的主键
        int uid = AppApplication.getUser().getuId();
        int number = 0;//类型的任务数目
        for (int i = 0; i < count; i++) {
            //通过xVals的值，获得类型
            type = xVals.get(i);
            //根据类型获取sid
            sid = new TasktypeController().getSidByTstyle(type);
            //根据sid 获取该用户该类型的任务数
            //number = new NewtaskController().getTasktypeNumber(sid,uid);
            number = AppApplication.getTasktypeNumber(sid,uid);
            float val = number;
            yVals1.add(new BarEntry(val, i));
        }
        ArrayList<Integer> colors = new ArrayList<Integer>();
        //设置颜色
       for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
               /* colors.add(Color.rgb(192, 255, 140));
                colors.add(Color.rgb(255, 247, 140));
                colors.add(Color.rgb(255, 208, 140));
                colors.add(Color.rgb(140, 234, 255));*/
                colors.add(Color.rgb(255, 228, 224));
      /* for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);*/
       /* for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);*/
        //colors.add(ColorTemplate.getHoloBlue());
        //pieDataSet.setColors(colors);
        //设置y轴的数据集
        BarDataSet set1;
        set1 = new BarDataSet(yVals1, "类型查询");
        if (typesearchBarChart.getData() != null &&
                typesearchBarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)typesearchBarChart.getData().getDataSetByIndex(0);
            set1.setYVals(yVals1);
            set1.setBarBorderWidth(0f);
            typesearchBarChart.getData().setXVals(xVals);
            typesearchBarChart.getData().notifyDataChanged();
            typesearchBarChart.notifyDataSetChanged();
            typesearchBarChart.setBorderWidth(0f);
        } else {
           // set1 = new BarDataSet(yVals1, "类型查询:" + sum + "件");
            //Log.w("sumtask",String.valueOf(sum));
            set1.setBarSpacePercent(35f);
            //set1.setColors(ColorTemplate.MATERIAL_COLORS);
            set1.setColors(colors);
            //IBarDataSet 接口很关键，是添加多组数据的关键结构
            // LineChart也是可以采用对应的接口类，也可以添加多组数据
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(10f);
            data.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                    int rs = (int)entry.getVal();
                    return rs + "件";//只用拿到对应Entry的值然后加个“元”即可，传入的这几个参数，v就是Y轴的value, entry为数据入口，i就是X轴方向的位置，viewPortHandler应该就是对应View的操作手，控制视图的移动缩放什么的
                }
            });
            typesearchBarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
            typesearchBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
            typesearchBarChart.getXAxis().setDrawGridLines(false);//不显示网格
            typesearchBarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
            typesearchBarChart.getAxisLeft().setAxisMinValue(0.0f);//设置Y轴显示最小值，不然0下面会有空隙
            typesearchBarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
            XAxis xAxis = typesearchBarChart.getXAxis();
            xAxis.setSpaceBetweenLabels(2);
            YAxis leftAxis = typesearchBarChart.getAxisLeft();
            leftAxis.setLabelCount(8, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            YAxis rightAxis = typesearchBarChart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            Legend l = mChart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
            l.setForm(Legend.LegendForm.SQUARE);
            l.setFormSize(9f);
            l.setTextSize(11f);
            l.setXEntrySpace(4f);
            //Typeface mTf  = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");;
            //data.setValueTypeface(mTf);
            typesearchBarChart.setData(data);
            //设置动画
            boolean isRember = pref.getBoolean("dynamic", true);
            if(isRember) {
                typesearchBarChart.animateXY(1000, 1000);
            }
            typesearchBarChart.invalidate();
        }
    }
    private void searchTimeMethod() {
            //弹出日历选择框
        final Calendar calendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setYearRange(2010, 2035);//设置年份区间
        datePickerDialog.setCloseOnSingleTapDay(false);//选择后是否消失，推荐false
        datePickerDialog.show(getActivity().getSupportFragmentManager(), DATEPICKER_TAG);//展示dialog，传一个tag参数
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        month = month + 1;
        String smonth = null;
        String sday = null;
        String gettime = null;
        if(month >=0 && month<= 9) {
            smonth = '0' + String.valueOf(month);
        }else{
            smonth = String.valueOf(month);
        }
        if(day >= 0 && day <= 9) {
            sday = '0' + String.valueOf(day);
        }else{
            sday = String.valueOf(day);
        }
        //searchway等于3按月查询，等于4按日查询
        if(searchway == 3) {
            typesearchBarChart.setVisibility(View.GONE);
            mChart.setVisibility(View.GONE);
            alertBarChart.setVisibility(View.GONE);
            timePieChart.setVisibility(View.VISIBLE);
            gettime = year +"."+ smonth + "." ;
            //然后去数据库里面根据日期查询
            //tasks = new NewtaskController().searchDrawByTime(AppApplication.getUser().getuId(),gettime);
            monthtasks = AppApplication.searchDrawByTime(AppApplication.getUser().getuId(),gettime);
            //得到了任务
            //设置数目，完成的和未完成的
            MonthTasksDepart();
            //总任务数目
            int sum = timeSetComplet[0] + timeSetComplet[1];
            //默认显示完成，未完成
            mPieData = getPieData(2, completes2, timeSetComplet, 100);
            //展示图形
            showChart(timePieChart, mPieData,sum);
            //默认不显示任何内容
            lv.setAdapter(null);
            timePieChart.invalidate();
        }else if(searchway == 4) {
            gettime = year +"."+ smonth + "." + sday;
            typesearchBarChart.setVisibility(View.GONE);
            mChart.setVisibility(View.GONE);
            alertBarChart.setVisibility(View.GONE);
            timePieChart.setVisibility(View.GONE);
            //tasks = new NewtaskController().searchDrawByTime(AppApplication.getUser().getuId(),gettime);
            tasks = AppApplication.searchByDate(AppApplication.getUser().getuId(),gettime);
            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
            setListViewHeightBasedOnChildren(lv);
            //Toast.makeText(getActivity(),gettime, Toast.LENGTH_LONG).show();
        }

    }
    private void searchCompleteSetListener() {
        searchComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSearchCompleteMethod();
            }
        });
    }
    private void setSearchCompleteMethod() {
        AppApplication.setSearchpage(0);
        searchComplete.setBackgroundResource(R.drawable.circleset8);
        searchAlert.setBackgroundResource(R.drawable.circleset2);
        searchWay.setBackgroundResource(R.drawable.circleset2);
        searchTime.setBackgroundResource(R.drawable.circleset2);
        searchTimeDay.setBackgroundResource(R.drawable.circleset2);
        //设置柱状图不显示，且不占位置
        typesearchBarChart.setVisibility(View.GONE);
        //设置piechart显示
        mChart.setVisibility(View.VISIBLE);
        //设置提醒piechart不显示
        alertBarChart.setVisibility(View.GONE);
        timePieChart.setVisibility(View.GONE);
        //1.设置查询的类型
        searchway = 0;
        //2.设置图形的显示情况
        showCompelte();
        //默认不显示任何内容
        lv.setAdapter(null);
        mChart.postInvalidate();
    }
    private void searchAlertSetListener(){
        searchAlert.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAlertMethod();
            }
        });
    }
    private void searchAlertMethod() {
        AppApplication.setSearchpage(1);
        //设置颜色
        searchComplete.setBackgroundResource(R.drawable.circleset2);
        searchAlert.setBackgroundResource(R.drawable.circleset8);
        searchWay.setBackgroundResource(R.drawable.circleset2);
        searchTime.setBackgroundResource(R.drawable.circleset2);
        searchTimeDay.setBackgroundResource(R.drawable.circleset2);
        typesearchBarChart.setVisibility(View.GONE);
        mChart.setVisibility(View.GONE);
        alertBarChart.setVisibility(View.VISIBLE);
        timePieChart.setVisibility(View.GONE);
        //1.设置查询的类型
        searchway = 1;
        //2.设置图形的显示情况
        showAlert();
        lv.setAdapter(null);
        //mChart.performClick();
        alertBarChart.postInvalidate();
    }
    private void searchwaySetListener(){
        searchWay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWayMethod();
            }
        });
    }
    private void searchWayMethod() {
        AppApplication.setSearchpage(2);
        searchComplete.setBackgroundResource(R.drawable.circleset2);
        searchAlert.setBackgroundResource(R.drawable.circleset2);
        searchWay.setBackgroundResource(R.drawable.circleset8);
        searchTime.setBackgroundResource(R.drawable.circleset2);
        searchTimeDay.setBackgroundResource(R.drawable.circleset2);
        //1.设置查询的类型
        searchway = 2;
        typesearchBarChart.setVisibility(View.VISIBLE);
        mChart.setVisibility(View.GONE);
        alertBarChart.setVisibility(View.GONE);
        timePieChart.setVisibility(View.GONE);
        //2.设置图形的显示情况
        //int sum = new NewtaskController().searchAllTasksNumber(AppApplication.getUser().getuId());
        int sum = AppApplication.searchAllTasksNumber(AppApplication.getUser().getuId());
        int min = 20;
        if(sum > 20 ) {
            int re = sum%20;
            switch (re) {
                case 1:
                    min = 20;break;
                case 2:
                    min = 30;break;
                case 3:
                    min = 40;break;
                case 4:
                    min = 50;break;
                case 5:
                    min = 60;break;
                default: min = 100;break;
            }
        }
        lv.setAdapter(null);
        //lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
        //setListViewHeightBasedOnChildren(lv);

        setData(0,min,sum);
        typesearchBarChart.postInvalidate();
    }
    private void searchTimeSetListener(){
        searchTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTimeCallMethod();
            }
        });
    }
    private void searchTimeCallMethod() {
        AppApplication.setSearchpage(3);
        vline.setVisibility(View.GONE);
        searchComplete.setBackgroundResource(R.drawable.circleset2);
        searchAlert.setBackgroundResource(R.drawable.circleset2);
        searchWay.setBackgroundResource(R.drawable.circleset2);
        searchTime.setBackgroundResource(R.drawable.circleset8);
        searchTimeDay.setBackgroundResource(R.drawable.circleset2);
        //1.设置查询的类型
        searchway = 3;
        //2.设置图形的显示情况
        searchTimeMethod();
    }
    private void searchTimeDaySetListener(){
        searchTimeDay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchTimeDayMethod();
            }
        });
    }
    private void searchTimeDayMethod() {
        AppApplication.setSearchpage(4);
        vline.setVisibility(View.GONE);
        searchComplete.setBackgroundResource(R.drawable.circleset2);
        searchAlert.setBackgroundResource(R.drawable.circleset2);
        searchWay.setBackgroundResource(R.drawable.circleset2);
        searchTime.setBackgroundResource(R.drawable.circleset2);
        searchTimeDay.setBackgroundResource(R.drawable.circleset8);

        //1.设置查询的类型
        searchway = 4;
        //2.设置图形的显示情况
        searchTimeMethod();
    }
    private void lvsetOnItemClickListener(){
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final Newtask task = tasks.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_LIGHT);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View updateView = layoutInflater.inflate(R.layout.content, null);
                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                titlealert.setText("清单内容");
                contentalert.setText("    " + task.getNcontent());
                dialog.setView(updateView);
                dialog.setPositiveButton("删除清单",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //删除任务
                        String gettime = task.getaTime();
                        gettime  = gettime.substring(0, gettime.length()-2);//用于删除月份时用，重新按照时间查询
                        new NewtaskController().deleteTaskById(task.getNtId());
                        //将该任务的所有通知全部修改成已经提醒的状态
                        new TaskAlertController().ChangeToFinishByNtid(task.getNtId());
                        tasks.remove(position);
                        lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                        switch (searchway) {
                            //更新界面
                            case 0:
                                showCompelte();
                                mChart.postInvalidate();
                                break;
                            case 1:
                                showAlert();
                                alertBarChart.postInvalidate();
                                break;
                            case 2:
                                int sum = AppApplication.searchAllTasksNumber(AppApplication.getUser().getuId());
                                int min = 20;
                                if(sum > 20 ) {
                                    int re = sum%20;
                                    switch (re) {
                                        case 1:
                                            min = 20;break;
                                        case 2:
                                            min = 30;break;
                                        case 3:
                                            min = 40;break;
                                        case 4:
                                            min = 50;break;
                                        case 5:
                                            min = 60;break;
                                        default: min = 100;break;
                                    }
                                }
                                setData(0,min,sum);
                                typesearchBarChart.postInvalidate();
                                break;
                            case 3:
                                //Log.w("tasktime",gettime);
                                monthtasks = AppApplication.searchDrawByTime(AppApplication.getUser().getuId(),gettime);
                                //得到了任务
                                //设置数目，完成的和未完成的
                                MonthTasksDepart();
                                //总任务数目
                                int asum = timeSetComplet[0] + timeSetComplet[1];
                                //默认显示完成，未完成
                                mPieData = getPieData(2, completes2, timeSetComplet, 100);
                                //展示图形
                                showChart(timePieChart, mPieData,asum);
                                timePieChart.invalidate();
                                break;
                            default:
                                break;
                        }

                    }
                });
                //如果任务完成，那么可以修改为未完成
                if(task.getNfinish() == 1 ) {
                    dialog.setNegativeButton("未完成清单",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    String gettime = task.getaTime();
                                    gettime  = gettime.substring(0, gettime.length()-2);
                                    //改变状态
                                    new NewtaskController().changeToNotFinish(task.getNtId());
                                    lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item,tasks));
                                    switch (searchway) {
                                        //更新界面
                                        case 0:
                                            tasks.remove(position);
                                            showCompelte();
                                            mChart.postInvalidate();
                                            break;
                                        case 1:
                                            break;
                                        case 2:
                                            break;
                                        case 3:
                                            tasks.remove(position);
                                            monthtasks = AppApplication.searchDrawByTime(AppApplication.getUser().getuId(),gettime);
                                            //得到了任务
                                            //设置数目，完成的和未完成的
                                            MonthTasksDepart();
                                            //总任务数目
                                            int asum = timeSetComplet[0] + timeSetComplet[1];
                                            //默认显示完成，未完成
                                            mPieData = getPieData(2, completes2, timeSetComplet, 100);
                                            //展示图形
                                            showChart(timePieChart, mPieData,asum);
                                            timePieChart.invalidate();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                    //如果任务未完成，可以修改为完成
                }else if(task.getNfinish() == 0){
                    dialog.setNegativeButton("完成清单",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    String gettime = task.getaTime();
                                    gettime  = gettime.substring(0, gettime.length()-2);
                                    new NewtaskController().changeToFinish(task.getNtId());
                                    lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                                    switch (searchway) {
                                        //更新界面 完成情况
                                        case 0:
                                            tasks.remove(position);
                                            showCompelte();
                                            mChart.postInvalidate();
                                            break;
                                        case 1:
                                            //提醒情况
                                            break;
                                        case 2:
                                            break;
                                        case 3:
                                            tasks.remove(position);
                                            monthtasks = AppApplication.searchDrawByTime(AppApplication.getUser().getuId(),gettime);
                                            //得到了任务
                                            //设置数目，完成的和未完成的
                                            MonthTasksDepart();
                                            //总任务数目
                                            int asum = timeSetComplet[0] + timeSetComplet[1];
                                            //默认显示完成，未完成
                                            mPieData = getPieData(2, completes2, timeSetComplet, 100);
                                            //展示图形
                                            showChart(timePieChart, mPieData,asum);
                                            timePieChart.invalidate();
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            });
                }
                //点击完成该条任务
                dialog.setNeutralButton("修改清单", new DialogInterface.OnClickListener() {
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
    }
    private void mChatsetOnChartValueSelectedListener() {
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //设置处理，按类型查询
                if (searchway == 0){
                    switch (e.getXIndex()) {
                        case 0:
                            //已完成
                            searchwaystate = 0;
                            //tasks = new NewtaskController().searchFinishTasks(AppApplication.getUser().getuId());
                            tasks = AppApplication.searchFinishTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        case 1:
                            //未完成
                            if(completeflag == 0) {
                                searchwaystate = 1;
                                tasks = AppApplication.searchNotCompleteTasks(AppApplication.getUser().getuId());
                                lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                                setListViewHeightBasedOnChildren(lv);
                            }else if(completeflag == 1) {
                                searchwaystate = 2;
                                tasks = AppApplication.searchOverdueTasks(AppApplication.getUser().getuId());
                                lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                                setListViewHeightBasedOnChildren(lv);
                            }
                            break;
                        case 2:
                            //过期searchOverdueTasks
                            searchwaystate = 2;
                            tasks = AppApplication.searchOverdueTasks(AppApplication.getUser().getuId());
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

        alertBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //设置处理，按类型查询
                if (searchway == 1) {
                    switch (e.getXIndex()) {
                        case 0:
                            searchwaystate = 0;
                            tasks = AppApplication.searchAlertTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        case 1:
                            searchwaystate = 1;
                            tasks = AppApplication.searchNotAlertTasks(AppApplication.getUser().getuId());
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        default:
                            break;
                    }
                }
            }
            @Override
            public void onNothingSelected() {
            }
        });

        timePieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                //设置处理，按类型查询
                if (searchway == 3) {
                    tasks = new ArrayList<Newtask>();
                    switch (e.getXIndex()) {
                        case 0:
                            //选择完成的任务
                            for(Newtask nt :monthtasks){
                                if(nt.getNfinish() == 1) {
                                    tasks.add(nt);
                                }
                            }
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        case 1:
                            //选择未完成的任务
                            //选择完成的任务
                            for(Newtask nt :monthtasks){
                                if(nt.getNfinish() == 0) {
                                    tasks.add(nt);
                                }
                            }
                            lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                            setListViewHeightBasedOnChildren(lv);
                            break;
                        default:
                            break;
                    }
                }
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }
    private void typesearchBCSelectedListener() {
        typesearchBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                String type;
                if(e == null)
                    return;
                //首先获取横坐标的值
                type = xVals.get(e.getXIndex());
                //然后根据横坐标的值去取得sid
                int getsid = new TasktypeController().getSidByTstyle(type);
                //根据sid查找该用户的该类型的任务 更新adapter
                tasks = AppApplication.getTasksBytypeNumber(getsid,AppApplication.getUser().getuId());
                lv.setAdapter(new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks));
                setListViewHeightBasedOnChildren(lv);
            }
            @Override
            public void onNothingSelected() {
            }
        });
    }

    /**
     * 从已经选择好的任务中 查找 未完成的数目，已经完成的数目
     * @param
     */
    private void MonthTasksDepart() {
        int completeNumber = 0;
        int notCompleteNumber = 0;
        for (Newtask nt: monthtasks) {
            if(nt.getNfinish() == 1) {
                completeNumber = completeNumber + 1;
            }else{
                notCompleteNumber = notCompleteNumber + 1;
            }
        }
        timeSetComplet[0] = completeNumber;
        timeSetComplet[1] = notCompleteNumber;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}