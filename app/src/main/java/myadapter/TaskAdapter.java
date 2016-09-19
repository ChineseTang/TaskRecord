package myadapter;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import controller.TasktypeController;
import model.Newtask;
import view.com.taskrecord.R;

/**
 * Created by tangzhijing on 2016/9/9.
 */
public class TaskAdapter extends ArrayAdapter<Newtask> {
    private int resourceId;

    public TaskAdapter(Context context, int resource, List<Newtask> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Newtask anewtask = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId,
                    null);
            viewHolder = new ViewHolder();
            // match
            viewHolder.tasknumber = (TextView) view.findViewById(R.id.tasknumber);
            viewHolder.taskcontent = (TextView) view.findViewById(R.id.taskcontent);
            viewHolder.tasktype = (TextView) view.findViewById(R.id.tasktype);
            viewHolder.taskfinish = (TextView) view.findViewById(R.id.taskfinish);
            viewHolder.tasknotetime = (TextView)view.findViewById(R.id.notetime);
            viewHolder.tasktime = (TextView) view.findViewById(R.id.tasktime);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tasknumber.setText(String.valueOf(position + 1) + "、");
        viewHolder.taskcontent.setText(anewtask.getNcontent());
        String type = new TasktypeController().getTstyleBySid(anewtask.getSid());
        viewHolder.tasktype.setText(type);
        int values = position%6;
        switch(values){
            case 1:
                viewHolder.tasktype.setBackgroundResource(R.drawable.circleset2);
                break;
            case 2:
                viewHolder.tasktype.setBackgroundResource(R.drawable.circleset3);
                break;
            case 3:
                viewHolder.tasktype.setBackgroundResource(R.drawable.circleset4);
                break;
            case 4:
                viewHolder.tasktype.setBackgroundResource(R.drawable.circleset5);
                break;
            case 5:
                viewHolder.tasktype.setBackgroundResource(R.drawable.circleset6);
                break;
            default:
                viewHolder.tasktype.setBackgroundResource(R.drawable.circleset);
                break;
        }


        if (anewtask.getNfinish() == 1) {
            viewHolder.taskfinish.setText("已完成");
            viewHolder.taskfinish.setTextColor(Color.rgb(255, 67, 67));
        } else {
            viewHolder.taskfinish.setText("未完成");
            viewHolder.taskfinish.setTextColor(Color.rgb(144, 144, 144));
        }
        viewHolder.tasknotetime.setText(anewtask.getNotetime());
        viewHolder.tasktime.setText(anewtask.getaTime());
       /* if(position%2 == 0 ) {
            view.setBackgroundResource(R.drawable.listitembgcolor1);
        }else if(position%2 == 1 ) {
            view.setBackgroundResource(R.drawable.listitembgcolor2);
        }*/
        return view;
    }

    class ViewHolder {
        TextView tasknumber;
        TextView taskcontent;
        TextView tasktype;
        TextView taskfinish;
        TextView tasknotetime;
        TextView tasktime;
    }
}
