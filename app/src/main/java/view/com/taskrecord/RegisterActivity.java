package view.com.taskrecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import controller.TUserController;
import model.TUser;

public class RegisterActivity extends BaseActivity {


    private ImageButton iback;
    private ImageView iphoto;
    private EditText eusername;
    private EditText email;
    private EditText epwd;
    private EditText erepwd;
    /*private RadioGroup rg;
    private RadioButton rmale;
    private RadioButton rfemale;*/
    private Button regbtn;

    private String susername;
    private String semail;
    private String spwd;
    private String srpwd;
    private String sgender = "男";//性别

    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
        setContentView(R.layout.activity_register);
        //get values from view
        iback = (ImageButton) findViewById(R.id.array);
        iphoto = (ImageView) findViewById(R.id.photo);
        eusername = (EditText) findViewById(R.id.userName);
        email = (EditText) findViewById(R.id.emailaddr);
        epwd = (EditText) findViewById(R.id.pwd);
        erepwd = (EditText) findViewById(R.id.repwd);
		/*rg = (RadioGroup) findViewById(R.id.gender);
		rmale = (RadioButton) findViewById(R.id.male);
		rfemale = (RadioButton) findViewById(R.id.female);*/
        regbtn = (Button) findViewById(R.id.registerbtn);
        //获得性别
		/*rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkId) {
				if(checkId == rmale.getId()) {
					sgender = rmale.getText().toString();
				}else if(checkId == rfemale.getId()) {
					sgender = rfemale.getText().toString();
				}
			}
		});*/
        //为iback按钮注册监听事件
        iback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        //为iphoto 按钮注册监听事件，启动照相机，然后照一张相片
        iphoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //创建File对象，用于存储拍照后的照片
                File outputImage = new File(Environment.getExternalStorageDirectory(),"output_image.jpg");
                try {
                    if(outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHOTO);//启动相机程序

            }
        });
        //注册按钮监听处理事件
        regbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //1 获得注册信息中的值
                susername = eusername.getText().toString();
                semail = email.getText().toString();
                spwd = epwd.getText().toString();
                srpwd = erepwd.getText().toString();
                //2 验证注册信息的值是否合理
                if(susername == null || susername.equals("")|| semail == null || semail.equals("") || spwd == null || spwd.equals("")|| srpwd == null || srpwd.equals("")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                    dialog.setTitle("注册失败");
                    dialog.setMessage("用户信息不能为空");
                    dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    dialog.show();
                }else if(!spwd.equals(srpwd)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                    dialog.setTitle("注册失败");
                    dialog.setMessage("两次密码应该相同");
                    dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    dialog.show();
                }else{
                    //将注册信息封装到TUser对象中
                    DateFormat format= new SimpleDateFormat("yyyy.MM.dd");
                    TUser user = new TUser();
                    user.setuName(susername);
                    user.setuEmail(semail);
                    user.setuPwd(spwd);
                    user.setuImage(Environment.getExternalStorageDirectory() + "output_image.jpg");//存储是相对于SD根目录下的位置
                    user.setuGender(sgender);
                    user.setuTime(format.format(new Date()));
                    //信息没有问题了，那么就调用Controller层插入数据吧
                    boolean result = new TUserController().registerUser(user);

                    if(result) {
                        //如果插入成功，跳转到登录界面
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("注册成功");
                        dialog.setMessage("点击进入登录页面");
                        dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.show();


                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this,AlertDialog.THEME_HOLO_LIGHT);
                        dialog.setTitle("注册失败");
                        dialog.setMessage("注册信息有错误");
                        dialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });
                        dialog.show();
                    }
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                    startActivityForResult(intent, CROP_PHOTO);//启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if(resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        bitmap = makeRoundCorner(bitmap);
                        iphoto.setImageBitmap(bitmap);//将裁剪后的照片显示出来
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
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


}