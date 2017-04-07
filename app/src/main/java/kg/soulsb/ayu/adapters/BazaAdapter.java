package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
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
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class BazaAdapter extends ArrayAdapter<Baza> {
    Context context;
    int layoutResourceId;
    List<Baza> data = null;

    public BazaAdapter(Context context, int layoutResourceId, List<Baza> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BazaHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new BazaHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.baza_title);
            holder.imageView = (ImageView) row.findViewById(R.id.baza_selected);
            row.setTag(holder);
        }
        else
        {
            holder = (BazaHolder) row.getTag();
        }
        Baza baza = getItem(position);
        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        holder.txtTitle.setText(baza.getName());
        if (CurrentBaseClass.getInstance().getCurrentBase().equals(baza.getName()))
        {
            holder.imageView.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imageView.setVisibility(View.INVISIBLE);
        }


        return row;
    }

    static class BazaHolder
    {
        TextView txtTitle;
        ImageView imageView;

    }

}