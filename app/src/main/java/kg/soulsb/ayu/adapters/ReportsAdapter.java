package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.models.Report;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class ReportsAdapter extends ArrayAdapter<Report> {
    Context context;
    int layoutResourceId;
    List<Report> data = null;

    public ReportsAdapter(Context context, int layoutResourceId, List<Report> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReportHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ReportHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.report_title);
            row.setTag(holder);
        }
        else
        {
            holder = (ReportHolder) row.getTag();
        }
        Report report = getItem(position);
        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        holder.txtTitle.setText(report.getName());
        holder.txtTitle.setGravity(Gravity.CENTER_VERTICAL);


        return row;
    }

    static class ReportHolder
    {
        TextView txtTitle;

    }

}