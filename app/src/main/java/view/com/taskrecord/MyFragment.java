package view.com.taskrecord;

/**
 * Created by tangzhijing on 2016/8/31.
 */
import java.io.File;

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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import controller.ActivityCollector;


public class MyFragment extends Fragment{
    private ImageView myphoto;
    private Uri imageUri;
    private Button logoutbtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_myfragment, container,false);

        myphoto = (ImageView) view.findViewById(R.id.photo);
        logoutbtn = (Button) view.findViewById(R.id.logout);
        String filepath = "/sdcard/output_image.jpg";
        //获得头像
        //首先获得头像的路径，如果有头像，则改变，如果没有，那么就不显示
        //System.out.println(Environment.getExternalStorageDirectory() + "/" + "output_image.jpg");

        //注销功能，跳转到登录界面
        logoutbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ActivityCollector.finishAll();
                Intent pagelogin = new Intent(getActivity(),MainActivity.class);
                startActivity(pagelogin);
                //System.exit(0);
            }
        });
        File myfile = new File(filepath);
        if (myfile.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(filepath);
            bm = makeRoundCorner(bm);
            //将图片显示到ImageView中
            myphoto.setImageBitmap(bm);
        }

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
}

