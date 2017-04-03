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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;


import java.util.ArrayList;

import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;

import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;

import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.TovarAdapter;


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
    Spinner otborSpinner;
    ListView listView;
    SearchView searchView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_tovar_fragment,container,false);

        // Создаем отбор
        otborSpinner = (Spinner) v.findViewById(R.id.spinner_otbor);
        ArrayList<String> otborArrayList = new ArrayList<String>();
        otborArrayList.add("Показать все");
        otborArrayList.add("Показать выбранные товары");

        ArrayAdapter<String> otborAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.baza_spinner_item,otborArrayList);
        otborSpinner.setAdapter(otborAdapter);

        otborSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (otborSpinner.getItemAtPosition(position).equals("Показать выбранные товары"))
                {
                    ArrayList<Item> arrayList2 = new ArrayList<Item>();
                    for (Item item: arrayList)
                    {
                        if (item.getQuantity()>0)
                        {
                            arrayList2.add(item);
                        }
                    }
                    arrayAdapter = new TovarAdapter(getActivity(),R.layout.list_tovary_layout,arrayList2);
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    arrayAdapter = new TovarAdapter(getActivity(),R.layout.list_tovary_layout,arrayList);
                    listView.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // создаем список товаров
        listView = (ListView) v.findViewById(R.id.list_view_tovary);

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

        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                System.out.println(newText);
                return true;
            }
        });

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