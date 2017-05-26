package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
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
import kg.soulsb.ayu.models.Item;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class TovarAdapter extends ArrayAdapter<Item> implements Filterable {
    Context context;
    int layoutResourceId;
    List<Item> data = null;
    List<Item> originalData = new ArrayList<>();

    public TovarAdapter(Context context, int layoutResourceId, List<Item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        originalData.addAll(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TovarHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TovarHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.tovar_name);
            holder.txtPrice = (TextView)row.findViewById(R.id.tovar_price);
            holder.txtStock = (TextView)row.findViewById(R.id.stock_list);
            holder.txtQuantity = (TextView)row.findViewById(R.id.tovar_quantity);
            holder.txtSum = (TextView)row.findViewById(R.id.tovar_sum);
            row.setTag(holder);
        }
        else
        {
            holder = (TovarHolder)row.getTag();
        }

        Item Tovar = data.get(position);
        if (Tovar == null)
            return null;
        holder.txtTitle.setText(Tovar.getName());

        if (Tovar.getPrice() != 0)
            holder.txtPrice.setText("Цена: "+Double.toString(Tovar.getPrice())+" сом");
        else
            holder.txtPrice.setText("");

        holder.txtStock.setText("ост: "+Double.toString(Tovar.getStock())+" "+Tovar.getUnit());

        if (Tovar.getQuantity() == 0)
            holder.txtQuantity.setText("");
        else {
            holder.txtQuantity.setText("кол-во: " + Tovar.getQuantity() + " " + Tovar.getMyUnit().getName());
            holder.txtPrice.setText("Цена: "+Double.toString(Tovar.getPrice()*Tovar.getMyUnit().getCoefficient())+" сом");
        }
        if (Tovar.getSum() == 0)
            holder.txtSum.setText("");
        else
            holder.txtSum.setText("Сумма: "+Double.toString(Tovar.getSum()));
        return row;
    }

    @Override
    public Filter getFilter() {

        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<Item> tempList=new ArrayList<>();
                //constraint is the result from text you want to filter against.
                //objects is your data set you will filter from
                data.clear();
                data.addAll(originalData);
                if(constraint != null && data!=null) {
                    int length=data.size();
                    int i=0;
                    while(i<length){
                        Item item=data.get(i);
                        //do whatever you wanna do here
                        //adding result set output array
                        if (item == null) break;

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
                try {
                    data.addAll((ArrayList<Item>) results.values);
                }
                catch (Exception E)
                {
                    E.printStackTrace();
                    notifyDataSetInvalidated();
                }

                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return myFilter;
    }

    static class TovarHolder
    {
        TextView txtTitle;
        TextView txtPrice;
        TextView txtStock;
        TextView txtQuantity;
        TextView txtSum;
    }

    @Override
    public void notifyDataSetChanged() {
        originalData.clear();
        originalData.addAll(data);
        super.notifyDataSetChanged();
    }
}
