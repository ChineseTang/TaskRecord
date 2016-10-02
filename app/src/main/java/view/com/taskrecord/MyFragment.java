package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import controller.ActivityCollector;
import controller.AppApplication;
import controller.TasktypeController;


public class MyFragment extends Fragment{
    private ImageView myphoto;
    private Uri imageUri;
    private TextView updatetype;
    private TextView description;
    private TextView about;
    private TextView logouttv;
    private TextView emailaddr;
    private TextView userName;
    private int selectedFruitIndex = 0;
    private SharedPreferences pref;//用于设置注销状态
    private SharedPreferences.Editor editor;
    private ArrayList<String> types;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_myfragment, container,false);
        init(view);

        String filepath = "/sdcard/output_image.jpg";
        //获得头像
        //首先获得头像的路径，如果有头像，则改变，如果没有，那么就不显示
        //System.out.println(Environment.getExternalStorageDirectory() + "/" + "output_image.jpg");
        logouttv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ActivityCollector.finishAll();
                //AppApplication.setLoginout(true);
                editor = pref.edit();
                editor.putBoolean("logout",true);
                editor.commit();
                Intent pagelogin = new Intent(getActivity(),MainActivity.class);
                AppApplication.setUser(null);
                AppApplication.setTasks(null);
                //AppApplication.setArraytypes(null);
                startActivity(pagelogin);
                getActivity().finish();
                //System.exit(0);
            }
        });
        about.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //弹出对话框,显示作品信息
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                dialog.setTitle("空空清单作品信息");
                dialog.setMessage("        空空清单是一款用于记录个人事务的app，操作方便简单，主要功能如下：\n" +
                        "\n1、根据日历方便快捷的查看任务。" +
                        "\n2、有提醒、添加、删除和修改功能。" +
                        "\n3、具有5类强大查询功能：" +
                        "\n        a、按照完成情况查询；" +
                        "\n        b、按照是否提醒查询；" +
                        "\n        c、按照类型查询；" +
                        "\n        d、按照月份查询；" +
                        "\n        e、按照日期查询。\n" +
                        "\n        您有任何问题，请联系作者，邮箱：tzjsmile@qq.com。");
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
                dialog.setTitle("空空清单使用说明");
                dialog.setMessage("        空空清单是一款用于记录个人事务的app，操作方便简单，主要功能如下：\n" +
                        "\n1、根据日历方便快捷的查看任务。" +
                        "\n2、有提醒、添加、删除和修改功能。" +
                        "\n3、具有5类强大查询功能：" +
                        "\n        a、按照完成情况查询；" +
                        "\n        b、按照是否提醒查询；" +
                        "\n        c、按照类型查询；" +
                        "\n        d、按照月份查询；" +
                        "\n        e、按照日期查询。\n" +
                        "\n        您有任何问题，请联系作者，邮箱：tzjsmile@qq.com。");
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

        updatetype.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                types = new TasktypeController().selectAllTypesWithoutSignal(AppApplication.getUser().getuId());
                //Log.w("types",types.toString());
                final String[] arraytypes = new String[types.size()];
                for(int i = 0 ; i < types.size();i++){
                    arraytypes[i] = types.get(i);
                }
                //弹出对话框,显示作品信息
                AlertDialog.Builder dialog = new AlertDialog.Builder(
                        getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                dialog.setTitle("请选择您要修改的任务类型");
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
                                if(olduid == 0){
                                    AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                    adialog.setTitle("删除失败");
                                    adialog.setMessage("    系统定义的类型不能删除\n包含(学习，工作，杂事)");
                                    adialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0, int arg1) {

                                        }
                                    });
                                    adialog.show();
                                }else{
                                    //再判断该类型是否有任务，如果有，那么不能删除
                                    int thisTypeNumber = AppApplication.getTasktypeNumber(oldsid,AppApplication.getUser().getuId());
                                    if(thisTypeNumber > 0) {
                                        AlertDialog.Builder adialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        adialog.setTitle("删除失败");
                                        adialog.setMessage("因为该类型有任务，不能删除");
                                        adialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                        adialog.show();
                                    }else{
                                        //去数据库中删除
                                        new TasktypeController().deleteTypeBySid(oldsid);
                                        Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
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
                            adialog.setTitle("修改失败");
                            adialog.setMessage("    系统定义的类型不能修改\n   包含(学习，工作，杂事)");
                            adialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                }
                            });
                            adialog.show();
                        } else {
                            AlertDialog.Builder newdialog = new AlertDialog.Builder(
                                    getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                            //不能有相同的类型
                            newdialog.setTitle("请修改");
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
                                        adialog.setTitle("修改失败");
                                        adialog.setMessage("内容不能为空！");
                                        adialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                        adialog.show();
                                    } else if (newtype.length() > 3) {
                                        AlertDialog.Builder bdialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                        bdialog.setTitle("修改失败");
                                        bdialog.setMessage("修改的类型不超过3个字！");
                                        bdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                                                //newtype = newtype + " ▼";
                                                // Log.w("tasktype","修改成功");
                                                //Log.w("tasktype",newtype);
                                                Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                                                ;
                                            } else {
                                                AlertDialog.Builder cdialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                                cdialog.setTitle("修改失败");
                                                cdialog.setMessage("不好意思，由于未知原因，修改失败，哈哈");
                                                cdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {

                                                    }
                                                });
                                                cdialog.show();
                                            }
                                        } else {
                                            //弹出dialog，显示该类型已经存在!
                                            AlertDialog.Builder ddialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT);
                                            ddialog.setTitle("修改失败");
                                            ddialog.setMessage("该类型已经存在！");
                                            ddialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
                                    //去数据库中进行操作
                                    //Log.w("tasktype","取消修改");
                                }
                            });
                            newdialog.show();
                        }
                    }
                });

                dialog.show();

            }
        });
       /* File myfile = new File(filepath);
        if (myfile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(filepath);
            bm = makeRoundCorner(bm);
            //将图片显示到ImageView中
            myphoto.setImageBitmap(bm);
        }*/

        return view;
    }
    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }
        // ZLog.i(TAG, "ps:"+ left +", "+ top +", "+ right +", "+ bottom);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    private void init(View view) {
        myphoto = (ImageView) view.findViewById(R.id.photo);
        updatetype = (TextView) view.findViewById(R.id.updatetype);
        description = (TextView) view.findViewById(R.id.description);
        about = (TextView) view.findViewById(R.id.about);
        logouttv = (TextView) view.findViewById(R.id.logouttv);
        userName = (TextView) view.findViewById(R.id.userName);
        emailaddr = (TextView) view.findViewById(R.id.emailaddr);
        logouttv.setFocusable(true);
        about.setFocusable(true);
        description.setFocusable(true);
        updatetype.setFocusable(true);
        pref = getActivity().getSharedPreferences("data", getActivity().MODE_PRIVATE);
        types = null;
        userName.setText(AppApplication.getUser().getuName());
        emailaddr.setText(AppApplication.getUser().getuEmail());
        //Log.w("taskemail",AppApplication.getUser().getuEmail());
    }
}

