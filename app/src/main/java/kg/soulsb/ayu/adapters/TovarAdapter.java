package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.models.Item;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class TovarAdapter extends ArrayAdapter<Item> {
    Context context;
    int layoutResourceId;
    List<Item> data = null;

    public TovarAdapter(Context context, int layoutResourceId, List<Item> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
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
        holder.txtTitle.setText(Tovar.getName());
        holder.txtPrice.setText("Цена: "+Double.toString(Tovar.getPrice())+" сом");
        holder.txtStock.setText("ост: "+Double.toString(Tovar.getStock())+" "+Tovar.getUnit());
        if (Tovar.getQuantity() == 0)
            holder.txtQuantity.setText("");
        else
            holder.txtQuantity.setText("кол-во: "+Tovar.getQuantity()+" "+Tovar.getUnit());

        if (Tovar.getSum() == 0)
            holder.txtSum.setText("");
        else
            holder.txtSum.setText("Сумма: "+Double.toString(Tovar.getSum()));
        return row;
    }

    static class TovarHolder
    {
        TextView txtTitle;
        TextView txtPrice;
        TextView txtStock;
        TextView txtQuantity;
        TextView txtSum;
    }

}
