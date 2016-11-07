package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

import java.util.ArrayList;

import controller.ActivityCollector;
import controller.AppApplication;
import controller.TUserController;
import controller.TasktypeController;


public class MyFragment extends Fragment {
    private ImageView myphoto;
    private Uri imageUri;
    private TextView updatetype;
    private TextView description;
    private TextView about;
    private TextView updatepwd;
    private TextView logouttv;
    private TextView userName;
    private TextView dynamic;
    private int selectedFruitIndex = 0;
    private SharedPreferences pref;//用于设置注销状态
    private SharedPreferences.Editor editor;
    private ArrayList<String> types;
    private static final String TAG = "youmi-demo";
    private Context mContext;
    private RelativeLayout mNativeAdLayout;
    private Button btnShowSpot;
    private View.OnKeyListener backlistener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK || i == KeyEvent.KEYCODE_HOME) {  //表示按返回键 时的操作
                    if (SpotManager.getInstance(mContext).isSpotShowing()) {
                        SpotManager.getInstance(mContext).hideSpot();
                        return  false;
                    }
                    return false;    //已处理
                }
            }
            return false;
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_myfragment, container, false);
        init(view);
        SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);
        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
        /*if(AppApplication.isShow()) {
            // 展示插屏广告
            SpotManager.getInstance(mContext).showSpot(mContext, new SpotListener() {
                @Override
                public void onShowSuccess() {
                }
                @Override
                public void onShowFailed(int errorCode) {
                }
                @Override
                public void onSpotClosed() {
                }
                @Override
                public void onSpotClicked(boolean isWebPage) {
                    //Log.d(TAG, "插屏被点击");
                    // Log.i(TAG, String.format("是否是网页广告？%s", isWebPage ? "是" : "不是"));
                }
            });
            AppApplication.setShow(false);
        }*/
        btnShowSpot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                // 展示插屏广告
                SpotManager.getInstance(mContext).showSpot(mContext, new SpotListener() {
                    @Override
                    public void onShowSuccess() {
                    }
                    @Override
                    public void onShowFailed(int errorCode) {
                    }
                    @Override
                    public void onSpotClosed() {
                    }
                    @Override
                    public void onSpotClicked(boolean isWebPage) {
                        //Log.d(TAG, "插屏被点击");
                        // Log.i(TAG, String.format("是否是网页广告？%s", isWebPage ? "是" : "不是"));
                    }
                });
            }
        });
        logouttv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ActivityCollector.finishAll();
                editor = pref.edit();
                editor.putBoolean("logout", true);
                editor.commit();
                Intent pagelogin = new Intent(getActivity(), MainActivity.class);
                AppApplication.setUser(null);
                AppApplication.setTasks(null);
                startActivity(pagelogin);
                getActivity().finish();
            }
        });
        dynamic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View updateView = layoutInflater.inflate(R.layout.content, null);
                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                titlealert.setText("设置动画");
                contentalert.setText("\n\n\n\n        是否在查询界面显示动画?");
                dialog.setView(updateView);
                //dialog.setTitle("空空清单作品信息");
                dialog.setNegativeButton("是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                editor = pref.edit();
                                editor.putBoolean("dynamic", true);
                                editor.commit();
                            }
                        });
                dialog.setPositiveButton("否",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                editor = pref.edit();
                                editor.putBoolean("dynamic", false);
                                editor.commit();
                            }
                        });
                dialog.show();
            }
        });
        userName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出对话框,显示作品信息
                AlertDialog.Builder newdialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                //不能有相同的类型
                //newdialog.setTitle("修改用户名");
                final EditText et = new EditText(getActivity());
                final String un = userName.getText().toString();
                et.setText(un);
                et.setSelection(un.length());
                et.setEnabled(true);
                et.setBackground(null);
                et.setGravity(Gravity.TOP | Gravity.LEFT);
                et.setMinHeight(250);
                et.setTextColor(Color.rgb(51, 51, 51));
                newdialog.setView(et);
                newdialog.setNegativeButton("修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                //点击修改
                                String newname = et.getText().toString();
                                if (("").equals(newname) || newname == null) {
                                    AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("修改失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        用户名不能为空哟(⊙o⊙)");
                                    adialog.setView(updateView);
                                   /* adialog.setTitle("修改失败");
                                    adialog.setMessage("用户名不能为空！");*/
                                    adialog.setPositiveButton("好吧，那我输入吧(′⌒`)", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    adialog.show();
                                } else if (new TUserController().isExistUsername(newname)) {
                                    //如果用户名已经存在
                                    AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("修改失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        用户名已经存在(ㄒoㄒ)");
                                    adialog.setView(updateView);
                                    /*adialog.setTitle("修改失败");
                                    adialog.setMessage("用户名已经存在！");*/
                                    adialog.setPositiveButton("好吧，那我重新输入吧(*^__^*)", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    adialog.show();
                                } else {

                                    new TUserController().updateUserName(AppApplication.getUser().getuId(), newname);
                                    editor = pref.edit();
                                    editor.putString("uName", newname);
                                    editor.commit();
                                    Toast.makeText(getActivity(), "修改用户名成功", Toast.LENGTH_SHORT).show();
                                    userName.setText(newname);
                                }
                            }
                        });
                newdialog.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                            }
                        });
                newdialog.show();
            }

        });

        about.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出对话框,显示作品信息
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View updateView = layoutInflater.inflate(R.layout.content, null);
                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                titlealert.setText("关于空空");
                contentalert.setText("        空空清单是一款用于记录个人事务的app，操作方便简单，主要功能如下：\n" +
                        "\n1、根据日历方便快捷的查看清单。" +
                        "\n2、有提醒、添加、删除和修改功能。" +
                        "\n3、具有5类强大查询功能：" +
                        "\n        a、按照完成情况查询；" +
                        "\n        b、按照是否提醒查询；" +
                        "\n        c、按照类型查询；" +
                        "\n        d、按照月份查询；" +
                        "\n        e、按照日期查询。\n" +
                        "\n        您有任何问题，请联系作者，邮箱：tzjsmile@qq.com。");
                dialog.setView(updateView);
                //dialog.setTitle("空空清单作品信息");
                dialog.setPositiveButton("感谢查看",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                            }
                        });
                dialog.show();
            }
        });
        description.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出对话框,显示作品信息
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View updateView = layoutInflater.inflate(R.layout.content, null);
                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                titlealert.setText("使用说明");
                contentalert.setText(
                        "\n1、注册功能：\n" +
                                "        点击首页中的（没有账号 点击注册），进入注册界面填写信息，然后点击（注册）按钮，提示注册成功。\n" +
                                "\n2、主页界面：" +
                                "\n        a、您可以点击上面的向左、向右和今这三个圆形按钮来调整月份日期；" +
                                "\n        b、点击日期，然后再点击圆形的浮动按钮，就会跳转到清单界面，添加清单；" +
                                "\n        c、点击一条清单，会弹出清单的详细信息，您可以更改清单的状态，修改清单和删除清单。\n" +
                                "\n3、清单界面：" +
                                "\n        a、您可以点击上面最左边的椭圆形按钮，来修改清单的类型，也可以自定义清单类型，定义的类型不超过2个字符；" +
                                "\n        b、您可以点击上面最右边的椭圆形按钮，来修改清单是否需要提醒的功能；" +
                                "\n        c、您可以点击上面中间的圆形按钮，添加改天清单。\n" +
                                "\n4、查询界面：" +
                                "\n        a、按照完成情况查询，用手可以将饼图转动哟；" +
                                "\n        b、按照是否提醒查询；" +
                                "\n        c、按照类型查询，如果类型比较多，可以通过双击放大柱状图或者拖动柱状图改变形状；" +
                                "\n        d、按照月份查询，根据弹出的日期选择月份，点击完成；" +
                                "\n        e、按照日期查询。\n" +
                                "\n5、我的界面：" +
                                "\n        a、设置清单类型，这里可以修改类型和删除类型，注意（系统自定义的类型不能修改和删除，同时如果该类型有清单，也不能删除）；" +
                                "\n        b、使用说明，点击使用说明，弹出使用说明界面；" +
                                "\n        c、关于空空，点击关于空空，弹出关于空空清单界面；" +
                                "\n        d、修改密码：点击修改密码，输入原密码和新密码；" +
                                "\n        e、点击空空清单下面的名称，就可以修改您的名称。" +
                                "\n        f、点击注销程序，跳转到登录界面。\n" +
                                "\n        您有任何问题，请联系作者，邮箱：tzjsmile@qq.com。");
                dialog.setView(updateView);
                dialog.setPositiveButton("感谢查看",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                            }
                        });
                dialog.show();
            }
        });
        updatepwd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出对话框,显示作品信息
                // 取得自定义View
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View updateView = layoutInflater.inflate(R.layout.updatupwd, null);
                final EditText orginpwd = (EditText) updateView.findViewById(R.id.originPwd);
                final EditText newpwd = (EditText) updateView.findViewById(R.id.newpwd);
                final EditText anewpwd = (EditText) updateView.findViewById(R.id.renewpwd);
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                dialog.setView(updateView);
                dialog.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                            }
                        });
                dialog.setNegativeButton("修改",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                                String sorigin = orginpwd.getText().toString();
                                String snewpwd = newpwd.getText().toString();
                                String sanewpwd = anewpwd.getText().toString();
                                if (sorigin == null || sorigin.equals("") || snewpwd == null || snewpwd.equals("") || sanewpwd == null || sanewpwd.equals("")) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("修改密码失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        密码信息不能为空(ㄒoㄒ)");
                                    dialog.setView(updateView);
                                    /*dialog.setTitle("修改密码失败");
                                    dialog.setMessage("密码信息不能为空");*/
                                    dialog.setPositiveButton("好吧，那我输入嘛", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });
                                    dialog.show();
                                } else if (!snewpwd.equals(sanewpwd)) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("修改密码失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        两次新密码应该相同(ㄒoㄒ)");
                                    dialog.setView(updateView);
                                    dialog.setPositiveButton("那我输入相同的╮(╯_╰)╭", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });
                                    dialog.show();
                                    //判断数据库中是否该密码是否正确，根据username进行判断
                                } else if (new TUserController().judgePwd(AppApplication.getUser().getuName(), sorigin)) {
                                    //修改密码
                                    editor = pref.edit();
                                    editor.putBoolean("logout", true);
                                    editor.commit();
                                    new TUserController().updateUserPwd(AppApplication.getUser().getuId(), snewpwd);
                                    Toast.makeText(getActivity(), "修改密码成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("修改密码失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        修改密码失败(ㄒoㄒ)");
                                    dialog.setView(updateView);
                                   /* dialog.setTitle("修改密码失败");
                                    dialog.setMessage("密码错误");*/
                                    dialog.setPositiveButton("那我想想再重新输入o(╯□╰)o", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {
                                        }
                                    });
                                    dialog.show();
                                }
                            }
                        });

                dialog.show();
            }
        });
        updatetype.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                types = new TasktypeController().selectAllTypesWithoutSignal(AppApplication.getUser().getuId());
                //Log.w("types",types.toString());
                final String[] arraytypes = new String[types.size()];
                for (int i = 0; i < types.size(); i++) {
                    arraytypes[i] = types.get(i);
                }
                //弹出对话框,显示作品信息
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                //dialog.setTitle("请选择您要修改的清单类型");
                dialog.setSingleChoiceItems(arraytypes, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedFruitIndex = which;
                    }
                });
                dialog.setPositiveButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {

                            }
                        });
                dialog.setNeutralButton("删除",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0,
                                                int arg1) {
                                //首先找到任务id
                                final String oldtype = arraytypes[selectedFruitIndex];
                                int olduid = new TasktypeController().getUidByTstyle(oldtype);
                                int oldsid = new TasktypeController().getSidByTstyle(oldtype);
                                //如果该类型的uid为0，表示不能删除
                                if (olduid == 0) {
                                    AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                    View updateView = layoutInflater.inflate(R.layout.content, null);
                                    final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                    final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                    titlealert.setText("删除失败");
                                    contentalert.setText("\n" +
                                            "\n" +
                                            "\n" +
                                            "\n        系统定义的类型不能删除\n" +
                                            "        包含(学习，工作，杂事)");
                                    adialog.setView(updateView);
                                    /*adialog.setTitle("删除失败");
                                    adialog.setMessage("    系统定义的类型不能删除\n    包含(学习，工作，杂事)");*/
                                    adialog.setPositiveButton("我记住了，下次不删除了(⊙o⊙)", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    adialog.show();
                                } else {
                                    //再判断该类型是否有任务，如果有，那么不能删除
                                    int thisTypeNumber = AppApplication.getTasktypeNumber(oldsid, AppApplication.getUser().getuId());
                                    if (thisTypeNumber > 0) {
                                        AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                        View updateView = layoutInflater.inflate(R.layout.content, null);
                                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                        titlealert.setText("删除失败");
                                        contentalert.setText("\n" +
                                                "\n" +
                                                "\n" +
                                                "\n        因为该类型有清单，不能删除");
                                        adialog.setView(updateView);
                                        /*adialog.setTitle("删除失败");
                                        adialog.setMessage("因为该类型有清单，不能删除");*/
                                        adialog.setPositiveButton("好吧，那我清空了再来o(╯□╰)o", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                        adialog.show();
                                    } else {
                                        //去数据库中删除
                                        new TasktypeController().deleteTypeBySid(oldsid);
                                        Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });
                dialog.setNegativeButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getActivity(),arraytypes[selectedFruitIndex],Toast.LENGTH_LONG);
                        //新建一个对话框，将当前选项放在里面
                        // Log.w("tasktype",arraytypes[selectedFruitIndex]);
                        final String oldtypea = arraytypes[selectedFruitIndex];
                        int oldida = new TasktypeController().getUidByTstyle(oldtypea);
                        if (oldida == 0) {
                            AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                            View updateView = layoutInflater.inflate(R.layout.content, null);
                            final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                            final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                            titlealert.setText("修改失败");
                            contentalert.setText("\n" +
                                    "\n" +
                                    "\n" +
                                    "\n        系统定义的类型不能修改\n" +
                                    "        包含(学习，工作，杂事)");
                            adialog.setView(updateView);
                            /*adialog.setTitle("修改失败");
                            adialog.setMessage("    系统定义的类型不能修改\n    包含(学习，工作，杂事)");*/
                            adialog.setPositiveButton("恩恩，俺记住了，下次不删了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                            adialog.show();
                        } else {
                            AlertDialog.Builder newdialog = new AlertDialog.Builder(
                                    getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                            //不能有相同的类型
                            //newdialog.setTitle("请修改");
                            final EditText et = new EditText(getActivity());
                            final String oldtype = arraytypes[selectedFruitIndex];
                            et.setText(arraytypes[selectedFruitIndex]);
                            et.setSelection(arraytypes[selectedFruitIndex].length());
                            et.setEnabled(true);
                            et.setBackground(null);
                            et.setGravity(Gravity.TOP | Gravity.LEFT);
                            et.setMinHeight(250);
                            et.setTextColor(Color.rgb(51, 51, 51));
                            newdialog.setView(et);
                            newdialog.setNegativeButton("确定修改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //去数据库中进行操作
                                    // Log.w("tasktype","确认修改");
                                    String newtype = et.getText().toString();
                                    if (("").equals(newtype) || newtype == null) {
                                        AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                        View updateView = layoutInflater.inflate(R.layout.content, null);
                                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                        titlealert.setText("修改失败");
                                        contentalert.setText("\n" +
                                                "\n" +
                                                "\n" +
                                                "\n        内容不能为空哟( ⊙ o ⊙ )");
                                        adialog.setView(updateView);
                                        adialog.setPositiveButton("恩恩，那我输入信息再修改(*^__^*) ", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                        adialog.show();
                                    } else if (newtype.length() >= 3) {
                                        AlertDialog.Builder bdialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                        View updateView = layoutInflater.inflate(R.layout.content, null);
                                        final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                        final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                        titlealert.setText("修改失败");
                                        contentalert.setText("\n" +
                                                "\n" +
                                                "\n" +
                                                "\n        修改的类型不超过2个字( ⊙ o ⊙ )");
                                        bdialog.setView(updateView);
                                        bdialog.setPositiveButton("恩恩额，下次记住了(ㄒoㄒ)", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                        bdialog.show();
                                    } else {
                                        int id = new TasktypeController().getSidByTstyle(newtype);
                                        if (id == -1) {
                                            //根据id 修改
                                            int oldid = new TasktypeController().getSidByTstyle(oldtype);
                                            boolean rs = new TasktypeController().updateType(newtype, oldid);
                                            //如果为真，那么添加成功，否则提示添加失败
                                            if (rs) {
                                                // 建立Adapter并且绑定数据源
                                                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                            } else {
                                                AlertDialog.Builder cdialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                                View updateView = layoutInflater.inflate(R.layout.content, null);
                                                final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                                final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                                titlealert.setText("修改失败");
                                                contentalert.setText("\n" +
                                                        "\n" +
                                                        "\n" +
                                                        "\n        不好意思，由于未知原因，修改失败O(∩_∩)O");
                                                cdialog.setView(updateView);
                                                cdialog.setPositiveButton("算你狠", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                    }
                                                });
                                                cdialog.show();
                                            }
                                        } else {
                                            //弹出dialog，显示该类型已经存在!
                                            AlertDialog.Builder ddialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                                            View updateView = layoutInflater.inflate(R.layout.content, null);
                                            final EditText titlealert = (EditText) updateView.findViewById(R.id.titlealert);
                                            final EditText contentalert = (EditText) updateView.findViewById(R.id.contentalert);
                                            titlealert.setText("修改失败");
                                            contentalert.setText("\n" +
                                                    "\n" +
                                                    "\n" +
                                                    "\n        不好意思，该类型已经存在O(∩_∩)O");
                                            ddialog.setView(updateView);
                                            ddialog.setPositiveButton("好吧，我重新输入新的╮(╯_╰)╭", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                }
                                            });
                                            ddialog.show();
                                        }
                                    }
                                }
                            });
                            newdialog.setPositiveButton("取消修改", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            newdialog.show();
                        }
                    }
                });

                dialog.show();

            }
        });
        view.setFocusable(true);//这个和下面的这个命令必须要设置了，才能监听back事件。
        view.setFocusableInTouchMode(true);
        view.setOnKeyListener(backlistener);
        return view;
    }

    private void init(View view) {
        mContext = getActivity();
        myphoto = (ImageView) view.findViewById(R.id.photo);
        updatetype = (TextView) view.findViewById(R.id.updatetype);
        description = (TextView) view.findViewById(R.id.description);
        about = (TextView) view.findViewById(R.id.about);
        updatepwd = (TextView) view.findViewById(R.id.updatepwd);
        logouttv = (TextView) view.findViewById(R.id.logouttv);
        userName = (TextView) view.findViewById(R.id.userName);
        dynamic = (TextView) view.findViewById(R.id.dynamic);
        userName.setFocusable(true);
        updatepwd.setFocusable(true);
        logouttv.setFocusable(true);
        dynamic.setFocusable(true);
        about.setFocusable(true);
        description.setFocusable(true);
        updatetype.setFocusable(true);
        pref = getActivity().getSharedPreferences("data", getActivity().MODE_PRIVATE);
        types = null;
        userName.setText(AppApplication.getUser().getuName());
        btnShowSpot = (Button) view.findViewById(R.id.btn_show_spot);


        /*View bannerView = BannerManager.getInstance(getActivity())
                .getBannerView(new BannerViewListener() {
                    @Override
                    public void onRequestSuccess() {

                    }

                    @Override
                    public void onSwitchBanner() {

                    }

                    @Override
                    public void onRequestFailed() {

                    }
                });
    // 获取要嵌入广告条的布局
        LinearLayout bannerLayout = (LinearLayout) view.findViewById(R.id.ll_banner);
    // 将广告条加入到布局中
        if (bannerView!=null) {
            bannerLayout.addView(bannerView);
        }*/

    }


    @Override
    public void onPause() {
        super.onPause();
        // 插播广告
        SpotManager.getInstance(mContext).onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        // 插播广告
        SpotManager.getInstance(mContext).onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 插播广告
        SpotManager.getInstance(mContext).onDestroy();
    }

}


