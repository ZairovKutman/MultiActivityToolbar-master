package kg.soulsb.ayu.activities.zakaz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;


import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.TovarAdapter;
import kg.soulsb.ayu.models.Stock;

/**
 * Created by Sultanbek Baibagyshev on 1/10/17.
 */

public class AddTovarFragment extends Fragment {
    ArrayList<Item> arrayList = new ArrayList<Item>();
    EditText editText;
    OrderAddActivity parentActivity;
    TovarAdapter arrayAdapter;
    PricesRepo pricesRepo;
    StocksRepo stocksRepo;
    OrderAddActivity activityMy;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_tovar_fragment,container,false);

        // Создаем отбор
        Spinner otborSpinner = (Spinner) v.findViewById(R.id.spinner_otbor);
        ArrayList<String> otborArrayList = new ArrayList<String>();
        otborArrayList.add("Показать все");
        otborArrayList.add("Отбор 1");
        otborArrayList.add("Отбор 2");
        ArrayAdapter<String> otborAdapter = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,otborArrayList);
        otborSpinner.setAdapter(otborAdapter);

        // создаем список товаров
        final ListView listView = (ListView) v.findViewById(R.id.list_view_tovary);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
                editText = (EditText) dialogView.findViewById(R.id.edit_text_picker);
                d.setTitle(arrayList.get(position).getName());
                d.setMessage("Выберите количество");
                d.setView(dialogView);
                final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.numberPicker);
                numberPicker.setMaxValue(1000);
                numberPicker.setMinValue(1);
                numberPicker.setWrapSelectorWheel(false);
                numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                        editText.setText(String.valueOf(i1));
                    }
                });
                d.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Если этот товар уже был, то удаляем его
                        parentActivity.checkItem(arrayList.get(position));
                        // Заново добавим товар
                        String quantity = String.valueOf(editText.getText());
                        if (quantity.equals("")) quantity="0";
                        if (Integer.parseInt(quantity) > 0) {
                            parentActivity.addItem(arrayList.get(position), Integer.parseInt(String.valueOf(editText.getText())));
                            arrayList.get(position).setQuantity(Integer.parseInt(quantity));
                            arrayList.get(position).setSum(arrayList.get(position).getQuantity() * arrayList.get(position).getPrice());
                            System.out.println(arrayList.get(position));
                            arrayAdapter.notifyDataSetChanged();
                        }
                        else {
                            arrayList.get(position).setQuantity(Integer.parseInt(quantity));
                            arrayList.get(position).setSum(arrayList.get(position).getQuantity() * arrayList.get(position).getPrice());
                            System.out.println(arrayList.get(position));
                            arrayAdapter.notifyDataSetChanged();
                        }

                    }
                });
                d.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                AlertDialog alertDialog = d.create();
                alertDialog.show();
            }
        });
        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        arrayList = new ItemsRepo().getItemsObject();
        pricesRepo = new PricesRepo();
        stocksRepo = new StocksRepo();
        activityMy = (OrderAddActivity) getActivity();

        if (parentActivity.order != null){
            for (Item item: parentActivity.order.getArraylistTovar() )
            {
                if (arrayList.indexOf(item) != -1) {
                    arrayList.get(arrayList.indexOf(item)).setQuantity(item.getQuantity());
                    parentActivity.orderedItemsArrayList.put(item,item.getQuantity());
                }
            }
        }

        arrayAdapter = new TovarAdapter(this.getActivity(),R.layout.list_tovary_layout,arrayList);
        listView.setAdapter(arrayAdapter);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = ((OrderAddActivity)getActivity());
    }

    public void updatePrices()
    {
        for (Item item: arrayList)
        {
            item.setPrice(pricesRepo.getItemPriceByPriceType(item.getGuid(),activityMy.getPriceType()));
        }
        arrayAdapter.notifyDataSetChanged();
    }

    public void updateStock()
    {
        for (Item item: arrayList)
        {
            item.setStock(stocksRepo.getItemStockByWarehouse(item.getGuid(),activityMy.getWarehouse()));
        }
        arrayAdapter.notifyDataSetChanged();
    }
}