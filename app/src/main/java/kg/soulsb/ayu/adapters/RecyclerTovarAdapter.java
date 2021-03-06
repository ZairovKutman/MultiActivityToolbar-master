package kg.soulsb.ayu.adapters;


import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OthersFragment;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.helpers.repo.UnitsRepo;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Unit;

/**
 * Created by Sultanbek Baibagyshev on 2/15/19.
 */

public class RecyclerTovarAdapter extends RecyclerView.Adapter<RecyclerTovarAdapter.TovarHolder> implements Filterable {
    private ArrayList<Item> allTovarsList = new ArrayList<>();
    private ArrayList<Item> tovarsList;
    private ArrayList<Item> selectedTovarsList;
    private ArrayList<Item> filteredTovarsList;
    private int spinnerSelectedState = 0;  // all tovars = 0 , selected tovars = 1;
    private OthersFragment othersFragment;

    public RecyclerTovarAdapter(ArrayList<Item> tovarsList, ArrayList<Item> selectedTovarsList, OthersFragment othersFragment) {
        this.allTovarsList.addAll(tovarsList);
        this.tovarsList = tovarsList;
        this.filteredTovarsList = new ArrayList<>();
        this.selectedTovarsList = selectedTovarsList;
        this.othersFragment = othersFragment;
    }

    @Override
    public TovarHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_tovary_layout, viewGroup, false);

        return new TovarHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TovarHolder tovarHolder, int position) {

        Item tovar = tovarsList.get(position);

        tovarHolder.txtTitle.setText(tovar.getName());
        if (tovar.getPrice() != 0)
            tovarHolder.txtPrice.setText("????????: "+Double.toString(tovar.getPrice())+" ??????");
        else
            tovarHolder.txtPrice.setText("");

        tovarHolder.txtStock.setText("??????: "+Double.toString(tovar.getStock())+" "+tovar.getMyUnit().getName());

        if (tovar.getQuantity() == 0)
            tovarHolder.txtQuantity.setText("");
        else {
            tovarHolder.txtQuantity.setText("??????-????: " + tovar.getQuantity() + " " + tovar.getMyUnit().getName());
            tovarHolder.txtPrice.setText("????????: "+Double.toString(tovar.getPrice()*tovar.getMyUnit().getCoefficient())+" ?????? ");
        }
        if (tovar.getSum() == 0)
            tovarHolder.txtSum.setText("");
        else
            tovarHolder.txtSum.setText("??????????: "+Double.toString(tovar.getSum()));

        tovarHolder.txtqty1.setText(Double.toString(tovar.getQty1()));
        tovarHolder.txtqty2.setText(Double.toString(tovar.getQty2()));
        tovarHolder.txtqty3.setText(Double.toString(tovar.getQty3()));

        tovarHolder.txtdate1.setText(tovar.getDate1());
        tovarHolder.txtdate2.setText(tovar.getDate2());
        tovarHolder.txtdate3.setText(tovar.getDate3());
    }

    @Override
    public int getItemCount() {
        return tovarsList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                filteredTovarsList.clear();
                FilterResults filterResults = new FilterResults();

                if (charString.isEmpty()) {
                    tovarsList.clear();
                    if (spinnerSelectedState==1) {
                        tovarsList.addAll(selectedTovarsList);
                    }
                    else if (spinnerSelectedState==0)
                    {
                        tovarsList.addAll(allTovarsList);
                    }
                    else {
                        for (Item item: allTovarsList)
                        {
                            if (item.getStock()>0)
                            {
                                tovarsList.add(item);
                            }
                        }
                    }
                filteredTovarsList.addAll(tovarsList);
                } else {

                    for (Item row : tovarsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.toString().toLowerCase().contains(charString.toLowerCase())) {
                            filteredTovarsList.add(row);
                        }
                    }
                }


                filterResults.values = filteredTovarsList;
                filterResults.count = filteredTovarsList.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tovarsList.clear();
                tovarsList.addAll((ArrayList<Item>) filterResults.values);
                // refresh the list with filtered data

                notifyDataSetChanged();
            }
        };
    }

    public int getSpinnerSelectedState() {
        return spinnerSelectedState;
    }

    public void setSpinnerSelectedState(int spinnerSelectedState, String category) {
        this.spinnerSelectedState = spinnerSelectedState;
        if (spinnerSelectedState==1) {
            tovarsList.clear();
            tovarsList.addAll(selectedTovarsList);
        }
        else if (spinnerSelectedState==0)
        {
            tovarsList.clear();
            tovarsList.addAll(allTovarsList);
        }
        else if (spinnerSelectedState == 2) {
            tovarsList.clear();
            for (Item item : allTovarsList) {
                if (item.getStock() > 0) {
                    tovarsList.add(item);
                }
            }
        }
        else {
            tovarsList.clear();
            for (Item item : allTovarsList) {
                if (item.getCategory().equals(category)) {
                    tovarsList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public class TovarHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtPrice, txtStock, txtQuantity, txtSum, txtdate1,txtdate2,txtdate3, txtqty1,txtqty2,txtqty3;

        public TovarHolder(View view) {
            super(view);
            txtTitle = (TextView) view.findViewById(R.id.tovar_name);
            txtPrice = (TextView) view.findViewById(R.id.tovar_price);
            txtStock = (TextView) view.findViewById(R.id.stock_list);
            txtQuantity = (TextView) view.findViewById(R.id.tovar_quantity);
            txtSum = (TextView) view.findViewById(R.id.tovar_sum);

            txtdate1 = (TextView) view.findViewById(R.id.txtDate1);
            txtdate2 = (TextView) view.findViewById(R.id.txtDate2);
            txtdate3 = (TextView) view.findViewById(R.id.txtDate3);

            txtqty1 = (TextView) view.findViewById(R.id.txtQty1);
            txtqty2 = (TextView) view.findViewById(R.id.txtQty2);
            txtqty3 = (TextView) view.findViewById(R.id.txtQty3);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<Unit> unitArrayList = new ArrayList<>();
                    ArrayAdapter<Unit> unitArrayAdapter;
                    EditText quantityEditText;
                    Spinner unitSpinner;
                    Item selectedItem = tovarsList.get(getAdapterPosition());
                    final AlertDialog.Builder d = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = LayoutInflater.from(view.getContext());
                    View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                    quantityEditText = (EditText) dialogView.findViewById(R.id.quantity_picker);
                    unitSpinner = (Spinner) dialogView.findViewById(R.id.unit_spinner);
                    unitArrayList = new UnitsRepo().getUnitsObjectByItemGuid(selectedItem.getGuid());
                    unitArrayAdapter = new ArrayAdapter<Unit>(view.getContext(),R.layout.baza_spinner_item,unitArrayList);
                    unitSpinner.setAdapter(unitArrayAdapter);

                    for (Unit unit: unitArrayList)
                    {
                        if (unit.isDefault()) {
                            unitSpinner.setSelection(unitArrayList.indexOf(unit));
                            break;
                        }

                    }
                    d.setTitle(selectedItem.getName());
                    d.setMessage("???????????????? ????????????????????");
                    d.setView(dialogView);

                    d.setPositiveButton("??????????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String quantity = String.valueOf(quantityEditText.getText());
                            Unit selectedUnit = (Unit) unitSpinner.getSelectedItem();

                            if (quantity.equals("")) quantity="0";
                            if (selectedItem.getStock()<Double.parseDouble(quantity) * selectedUnit.getCoefficient() && selectedItem.getStock()>=0)
                            {
                                Toast.makeText(view.getContext(),"?????????????? ???????????????????? ???????????? ?????? ??????????????, ???????????????? ?????????? ???? ??????????????????????.",Toast.LENGTH_SHORT).show();
                            }

                            // ???????? ?????????? ???????? ?????????? ?????? ???????????? ??????????, ???? ?????????????? ??????
                            selectedTovarsList.remove(selectedItem);

                            // ???????????? ?????????????? ?????????? ?? ???????????? ??????????????
                            if (Double.parseDouble(quantity) > 0) {
                                selectedItem.setQuantity(Integer.parseInt(quantity));
                                selectedItem.setMyUnit(selectedUnit);
                                selectedItem.setSum(selectedItem.getQuantity() * selectedItem.getPrice()  * selectedItem.getMyUnit().getCoefficient());
                                selectedTovarsList.add(selectedItem);

                                for (Item item: allTovarsList)
                                {
                                    if (item.equals(selectedItem)) {
                                        item.setQuantity(selectedItem.getQuantity());
                                        item.setMyUnit(selectedItem.getMyUnit());
                                        item.setSum(selectedItem.getSum());
                                        break;
                                    }
                                }
                            }
                            else {
                                selectedItem.setQuantity(0);
                                selectedItem.setSum(0);
                                for (Item item: allTovarsList)
                                {
                                    if (item.equals(selectedItem)) {
                                        item.setQuantity(0);
                                        item.setSum(0);
                                        break;
                                    }
                                }
                            }
                            notifyDataSetChanged();
                            othersFragment.updateTotalSum();
                        }
                    });
                    d.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog alertDialog = d.create();
                    alertDialog.show();

                }
            });
        }
    }



    //////////// Change values of arraylist

    public void updatePrices(String priceType) {
        PricesRepo pricesRepo = new PricesRepo();
        for (Item item: tovarsList)
        { item.setPrice(pricesRepo.getItemPriceByPriceType(item.getGuid(),priceType)); }
        for (Item item: allTovarsList)
        { item.setPrice(pricesRepo.getItemPriceByPriceType(item.getGuid(),priceType)); }



    }

    public void updateStock(String warehouse )
    {
        StocksRepo stocksRepo = new StocksRepo();

        for (Item item: allTovarsList)
        { item.setStock(stocksRepo.getItemStockByWarehouse(item.getGuid(),warehouse)); }

        for (Item item: tovarsList)
        { item.setStock(stocksRepo.getItemStockByWarehouse(item.getGuid(),warehouse)); }

        for (Item item: selectedTovarsList)
        { item.setStock(stocksRepo.getItemStockByWarehouse(item.getGuid(),warehouse)); }
    }

    public void updateSalesHistory(String clientGuid )
    {

        for (Item item: allTovarsList)
        { item.updateSalesHistory(clientGuid); }

        for (Item item: tovarsList)
        { item.updateSalesHistory(clientGuid);}

        for (Item item: selectedTovarsList)
        { item.updateSalesHistory(clientGuid); }
    }
}
