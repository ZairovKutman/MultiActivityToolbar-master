package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.models.Client;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class ClientAdapter extends ArrayAdapter<Client> implements Filterable {
    Context context;
    int layoutResourceId;
    List<Client> data = null;

    public ClientAdapter(Context context, int layoutResourceId, List<Client> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ClientHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ClientHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.listClient);
            holder.txtAddress = (TextView)row.findViewById(R.id.listAddress);

            row.setTag(holder);
        }
        else
        {
            holder = (ClientHolder)row.getTag();
        }

        Client client = getItem(position);
        holder.txtTitle.setText(client.getName());
        holder.txtAddress.setText(client.getAddress());
        if (client.getDebt()>0)
            holder.txtTitle.setTextColor(Color.RED);
        return row;


    }


    static class ClientHolder
    {
        TextView txtTitle;
        TextView txtAddress;
    }
}