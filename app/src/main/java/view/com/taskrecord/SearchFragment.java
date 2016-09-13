package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import controller.AppApplication;
import controller.NewtaskController;
import model.Newtask;
import myadapter.TaskAdapter;
public class SearchFragment extends Fragment {
    private ArrayList<Newtask> tasks = new ArrayList<Newtask>();
    private Button allbtn;
    private Button notfinishbtn;
    private Button finishbtn;
    private ListView lv;
    private int tag = 0;//用于标记目前是哪个界面，是所有，未完，还是已完，用于在删除时判断跳转到哪个界面，默认是0
    //表示所有界面，1是完成，-1是未完成
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_search, container,false);


        tasks = new NewtaskController().searchAllTasks(AppApplication.getUser().getuId());

        lv = (ListView) view.findViewById(R.id.alltask);
        allbtn = (Button) view.findViewById(R.id.searchalltask);
        notfinishbtn = (Button) view.findViewById(R.id.searchnotfinish);
        finishbtn = (Button) view.findViewById(R.id.searchfinish);
        //选择所有的任务


        TaskAdapter taskadapter = new TaskAdapter(AppApplication.getContext(), R.layout.task_item, tasks);
        lv.setAdapter(taskadapter);
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
        allbtn.setOnClickListener(new OnClickListener() {

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
        });
        return view;
    }

}