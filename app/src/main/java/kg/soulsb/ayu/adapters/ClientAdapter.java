package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
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
    List<Client> originalData = new ArrayList<>();


    public ClientAdapter(Context context, int layoutResourceId, List<Client> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        originalData.addAll(data);
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
            holder.txtDebt = (TextView)row.findViewById(R.id.client_debt);
            holder.txtLoc = (TextView) row.findViewById(R.id.client_loc);
            row.setTag(holder);
        }
        else
        {
            holder = (ClientHolder)row.getTag();
        }

        Client client = getItem(position);
        holder.txtLoc.setText(client.getLocOfAgentText());
        holder.txtTitle.setText(client.getName());
        holder.txtAddress.setText(client.getAddress());
        if (client.getDebt()>0) {
            holder.txtTitle.setTextColor(Color.RED);
            holder.txtDebt.setText("Долг: "+Double.toString(client.getDebt())+" сом");
        }
        else
        {
            holder.txtTitle.setTextColor(Color.DKGRAY);
            holder.txtDebt.setText("");
        }


        return row;


    }

    @Override
    public Filter getFilter() {

        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Client> tempList=new ArrayList<Client>();
                //constraint is the result from text you want to filter against.
                //objects is your data set you will filter from
                data.clear();
                data.addAll(originalData);
                if(constraint != null && data!=null) {
                    int length=data.size();
                    int i=0;
                    while(i<length){
                        Client item=data.get(i);
                        //do whatever you wanna do here
                        //adding result set output array
                        if (item.toString().toLowerCase().contains(constraint.toString().toLowerCase()))
                            tempList.add(item);
                        i++;
                    }
                    //following two lines is very important
                    //as publish result can only take FilterResults objects
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                data.clear();
                data.addAll((ArrayList<Client>) results.values);
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;
    }

    static class ClientHolder
    {
        TextView txtTitle;
        TextView txtAddress;
        TextView txtDebt;
        TextView txtLoc;
    }
}