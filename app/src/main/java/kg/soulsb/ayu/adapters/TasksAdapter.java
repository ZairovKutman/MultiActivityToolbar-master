package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.DailyTask;
import kg.soulsb.ayu.models.Report;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class TasksAdapter extends ArrayAdapter<DailyTask> {
    Context context;
    int layoutResourceId;
    List<DailyTask> data = null;

    public TasksAdapter(Context context, int layoutResourceId, List<DailyTask> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DailyTaskHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DailyTaskHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.task_title);
            holder.txtStatus = (TextView)row.findViewById(R.id.task_status);
            holder.imageStatus = row.findViewById(R.id.task_image);
            row.setTag(holder);
        }
        else
        {
            holder = (DailyTaskHolder) row.getTag();
        }
        DailyTask dailyTask = getItem(position);
        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        holder.txtTitle.setText(dailyTask.toString());
        if (dailyTask.getStatus().equals("0"))
            holder.imageStatus.setImageResource(android.R.drawable.checkbox_off_background);
        else {
            holder.imageStatus.setImageResource(android.R.drawable.checkbox_on_background);
            holder.txtTitle.setTextColor(Color.parseColor("#ff669900"));
            holder.txtStatus.setTextColor(Color.parseColor("#ff669900"));
        }

        if (dailyTask.getStatus().equals("1")) {
            holder.txtStatus.setText("Статус: Заказ принят");
        }
        else if (dailyTask.getStatus().equals("2")) {
            holder.txtStatus.setText("Статус: Магазин закрыт (фото)");
        }
        else if (dailyTask.getStatus().equals("3")) {
            holder.txtStatus.setText("Статус: Пропустил");
        }
        else if (dailyTask.getStatus().equals("0")) {
            holder.txtStatus.setText("Статус: Не выполнен");
        }
        else {
            holder.txtStatus.setText("Статус: Неизвестно");
        }

        return row;
    }

    static class DailyTaskHolder
    {
        TextView txtTitle;
        TextView txtStatus;
        ImageView imageStatus;

    }

}