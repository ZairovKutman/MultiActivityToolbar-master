package kg.soulsb.ayu.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
        Order order = getItem(position);

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);


            holder = new OrderHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.text_client);
            holder.txtDetail = (TextView)row.findViewById(R.id.text_detail);
            holder.delivered = (ImageView)row.findViewById(R.id.delivered);
            holder.image = (ImageView)row.findViewById(R.id.imageView_doc);
            row.setTag(holder);

        }
        else
        {
            holder = (OrderHolder) row.getTag();
        }


        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        ClientsRepo clientsRepo = new ClientsRepo();
        String myClient = clientsRepo.getClientObjectByGuid(order.getClient()).getName();

        holder.txtTitle.setText(myClient);

        if (order.getDoctype().equals("1")){
            holder.txtDetail.setText("Реализация от "+order.getDate()+", сумма = "+order.getTotalSum());
        }
        else if (order.getDoctype().equals("0")) {
            holder.txtDetail.setText("Заказ от " + order.getDate() + ", сумма = " + order.getTotalSum());
        }
        else if (order.getDoctype().equals("2")) {
            holder.txtDetail.setText("Оплата от " + order.getDate() + ", сумма = " + order.getTotalSum());
        }
        else if (order.getDoctype().equals("3")) {
            holder.txtTitle.setText("Сводная оплата");
            holder.txtDetail.setText("оплата от " + order.getDate() + ", сумма = " + order.getTotalSum());
        }

        if (order.isDelivered())
        {
            holder.image.setColorFilter(ContextCompat.getColor(context,android.R.color.holo_green_dark));
            holder.delivered.setVisibility(View.VISIBLE);
        }
        else {
            holder.image.setColorFilter(ContextCompat.getColor(context,android.R.color.darker_gray));
            holder.delivered.setVisibility(View.INVISIBLE);
        }

        return row;
    }

    static class OrderHolder
    {
        TextView txtTitle;
        TextView txtDetail;
        ImageView delivered;
        ImageView image;
    }

}