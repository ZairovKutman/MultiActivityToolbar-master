package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;

/**
 * Created by Sultanbek Baibagyshev on 1/16/17.
 */

public class OrderAdapter extends ArrayAdapter<Order> {
    Context context;
    int layoutResourceId;
    List<Order> data = null;

    public OrderAdapter(Context context, int layoutResourceId, List<Order> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        OrderHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new OrderHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text_client);
            holder.txtDetail = (TextView)row.findViewById(R.id.text_detail);
            holder.delivered = (ImageView)row.findViewById(R.id.delivered);
            row.setTag(holder);
        }
        else
        {
            holder = (OrderHolder) row.getTag();
        }

        Order order = data.get(position);
        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        ClientsRepo clientsRepo = new ClientsRepo();
        String myClient = clientsRepo.getClientObjectByGuid(order.getClient()).getName();
        holder.txtTitle.setText(myClient);
        if (order.getDoctype().equals("1")){
            holder.txtDetail.setText("Реализация от "+order.getDate()+", сумма = "+order.getTotalSum());
        }
        else
            holder.txtDetail.setText("Заказ от "+order.getDate()+", сумма = "+order.getTotalSum());

        if (order.isDelivered())
            holder.delivered.setVisibility(View.VISIBLE);
        else
            holder.delivered.setVisibility(View.INVISIBLE);

        return row;
    }

    static class OrderHolder
    {
        TextView txtTitle;
        TextView txtDetail;
        ImageView delivered;
    }

}