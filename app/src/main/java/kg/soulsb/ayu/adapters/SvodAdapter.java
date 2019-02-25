package kg.soulsb.ayu.adapters;

import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.models.SvodRow;

/**
 * Created by soulsb on 10/30/17.
 */

public class SvodAdapter extends RecyclerView.Adapter<SvodAdapter.MyViewHolder> {

    private List<SvodRow> svodRowList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, unit, summa;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            unit = (TextView) view.findViewById(R.id.unit);
            summa = (TextView) view.findViewById(R.id.summa);
        }
    }


    public SvodAdapter(List<SvodRow> svodRowList) {
        this.svodRowList = svodRowList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.svod_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SvodRow svodRow = svodRowList.get(position);
        holder.name.setText(svodRow.getName());
        holder.unit.setText(svodRow.getQuantityString());
        holder.summa.setText(Double.toString(svodRow.getSumma()));
    }

    @Override
    public int getItemCount() {
        return svodRowList.size();
    }

}
