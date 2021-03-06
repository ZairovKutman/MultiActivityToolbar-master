//package kg.soulsb.ayu.activities.zakaz;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.support.v7.widget.SearchView;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import java.util.ArrayList;
//
//import kg.soulsb.ayu.helpers.DBHelper;
//import kg.soulsb.ayu.helpers.DatabaseManager;
//
//import kg.soulsb.ayu.helpers.repo.ItemsRepo;
//import kg.soulsb.ayu.helpers.repo.PricesRepo;
//import kg.soulsb.ayu.helpers.repo.StocksRepo;
//
//import kg.soulsb.ayu.helpers.repo.UnitsRepo;
//import kg.soulsb.ayu.models.Item;
//import kg.soulsb.ayu.R;
//import kg.soulsb.ayu.adapters.TovarAdapter;
//import kg.soulsb.ayu.models.Unit;
//
///**
// * Created by Sultanbek Baibagyshev on 1/10/17.
// */
//
//public class AddTovarFragment extends Fragment {
//    ArrayList<Item> arrayList = new ArrayList<Item>();
//    ArrayList<Item> originalArrayList = new ArrayList<Item>();
//    EditText editText;
//    OrderAddActivity parentActivity;
//    TovarAdapter arrayAdapter;
//    PricesRepo pricesRepo;
//    StocksRepo stocksRepo;
//    OrderAddActivity activityMy;
//    Spinner otborSpinner;
//    ListView listView;
//    SearchView searchView;
//    Spinner unitSpinner;
//    ArrayList<Unit> unitArrayList = new ArrayList<>();
//    ArrayAdapter<Unit> unitArrayAdapter;
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.add_tovar_fragment,container,false);
//
//        // Создаем отбор
//        otborSpinner = (Spinner) v.findViewById(R.id.spinner_otbor);
//        ArrayList<String> otborArrayList = new ArrayList<String>();
//        otborArrayList.add("Показать все");
//        otborArrayList.add("Показать выбранные товары");
//
//        ArrayAdapter<String> otborAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.baza_spinner_item,otborArrayList);
//        otborSpinner.setAdapter(otborAdapter);
//
//        otborSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (otborSpinner.getItemAtPosition(position).equals("Показать выбранные товары"))
//                {
//                    ArrayList<Item> itemArrayList2 = new ArrayList<>();
//                    for (Item item: arrayList)
//                    {
//                        if (item.getQuantity()<=0)
//                        {
//                            itemArrayList2.add(item);
//                        }
//                    }
//                    arrayList.removeAll(itemArrayList2);
//                    arrayAdapter.notifyDataSetChanged();
//                }
//                else
//                {
//                    arrayList.clear();
//                    arrayList.addAll(originalArrayList);
//                    arrayAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        // создаем список товаров
//        listView = (ListView) v.findViewById(R.id.list_view_tovary);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                final AlertDialog.Builder d = new AlertDialog.Builder(getContext());
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
//                editText = (EditText) dialogView.findViewById(R.id.quantity_picker);
//                unitSpinner = (Spinner) dialogView.findViewById(R.id.unit_spinner);
//                unitArrayList = new UnitsRepo().getUnitsObjectByItemGuid(arrayList.get(position).getGuid());
//                unitArrayAdapter = new ArrayAdapter<Unit>(getContext(),R.layout.baza_spinner_item,unitArrayList);
//                unitSpinner.setAdapter(unitArrayAdapter);
//
//                for (Unit unit: unitArrayList)
//                {
//                    if (unit.isDefault()) {
//                        unitSpinner.setSelection(unitArrayList.indexOf(unit));
//                        break;
//                    }
//
//                }
//                d.setTitle(arrayList.get(position).getName());
//                d.setMessage("Выберите количество");
//                d.setView(dialogView);
//
//                d.setPositiveButton("Выбрать", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String quantity = String.valueOf(editText.getText());
//                        Unit selectedUnit = (Unit) unitSpinner.getSelectedItem();
//                        if (quantity.equals("")) quantity="0";
//                        if (arrayList.get(position).getStock()<Integer.parseInt(quantity) * selectedUnit.getCoefficient() && arrayList.get(position).getStock()>=0)
//                        {
//                            Toast.makeText(getContext(),"Количество не может быть больше остатка",Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        // Если этот товар уже был, то удаляем его
//                        parentActivity.checkItem(arrayList.get(position));
//                        // Заново добавим товар
//
//                        if (Integer.parseInt(quantity) > 0) {
//                            arrayList.get(position).setQuantity(Integer.parseInt(quantity));
//                            arrayList.get(position).setMyUnit(selectedUnit);
//                            arrayList.get(position).setSum(arrayList.get(position).getQuantity() * arrayList.get(position).getPrice()  * arrayList.get(position).getMyUnit().getCoefficient());
//                            parentActivity.addItem(arrayList.get(position));
//                            arrayAdapter.notifyDataSetChanged();
//                        }
//                        else {
//                            arrayList.get(position).setQuantity(0);
//                            arrayList.get(position).setSum(0);
//                            arrayAdapter.notifyDataSetChanged();
//
//                        }
//
//                    }
//                });
//                d.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//                AlertDialog alertDialog = d.create();
//                alertDialog.show();
//            }
//        });
//        DBHelper dbHelper = new DBHelper(getContext());
//        DatabaseManager.initializeInstance(dbHelper);
//        if (parentActivity.order == null) {
//            originalArrayList = new ItemsRepo().getItemsObjectByCategory(parentActivity.category);
//        }
//        else
//        {
//            originalArrayList = new ItemsRepo().getItemsObjectByCategory(parentActivity.order.getDogovorObject().getCategory());
//        }
//
//        arrayList.addAll(originalArrayList);
//        pricesRepo = new PricesRepo();
//        stocksRepo = new StocksRepo();
//        activityMy = (OrderAddActivity) getActivity();
//
//        if (parentActivity.order != null){
//            for (Item item: parentActivity.order.getArraylistTovar() )
//            {
//                if (arrayList.indexOf(item) != -1) {
//                    arrayList.get(arrayList.indexOf(item)).setMyUnit(item.getMyUnit());
//                    arrayList.get(arrayList.indexOf(item)).setQuantity(item.getQuantity());
//                    arrayList.get(arrayList.indexOf(item)).setSum(item.getSum());
//                    arrayList.get(arrayList.indexOf(item)).setPrice(item.getPrice());
//                    parentActivity.addItem(item);
//                }
//            }
//            if (parentActivity.isDelivered.equals("true"))
//            {
//                disableButtons();
//            }
//        }
//        listView.setAdapter(arrayAdapter);
//
//        setHasOptionsMenu(true);
//        return v;
//    }
//
//    private void disableButtons() {
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
//
//        otborSpinner.setSelection(1);
//
//        ArrayList<Item> arrayList2 = new ArrayList<>();
//        for (Item item: arrayList)
//        {
//            if (item.getQuantity()<=0)
//            {
//                arrayList2.add(item);
//            }
//        }
//
//        arrayList.removeAll(arrayList2);
//        arrayAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.action_search, menu);
//
//        MenuItem item = menu.findItem(R.id.action_search);
//        searchView = (SearchView) item.getActionView();
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                try {
//                if (newText.equals(""))
//                {
//                    arrayList.clear();
//                    arrayList.addAll(originalArrayList);
//                    arrayAdapter.notifyDataSetChanged();
//                }
//
//                arrayAdapter.getFilter().filter(newText);
//                arrayList.clear();
//                arrayList.addAll(originalArrayList);
//                arrayAdapter.notifyDataSetChanged();}
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//                return true;
//            }
//        });
//
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                try {
//                arrayList.clear();
//                arrayList.addAll(originalArrayList);
//                arrayAdapter.notifyDataSetChanged();}
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//        });
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        parentActivity = ((OrderAddActivity)getActivity());
//        arrayAdapter = new TovarAdapter(this.getActivity(),R.layout.list_tovary_layout,arrayList);
//    }
//
//    public void updatePrices()
//    {
//        for (Item item: originalArrayList)
//        {
//            item.setPrice(pricesRepo.getItemPriceByPriceType(item.getGuid(),activityMy.getPriceType()));
//        }
//        arrayList.clear();
//        arrayList.addAll(originalArrayList);
//        arrayAdapter.notifyDataSetChanged();
//        if (parentActivity.isDelivered.equals("true"))
//        {
//            disableButtons();
//        }
//    }
//
//    public void updateStock()
//    {
//        arrayList.clear();
//        arrayList.addAll(originalArrayList);
//        for (Item item: originalArrayList)
//        {
//            item.setStock(stocksRepo.getItemStockByWarehouse(item.getGuid(),activityMy.getWarehouse()));
//        }
//        try {
//            arrayAdapter.notifyDataSetChanged();
//            if (parentActivity.isDelivered.equals("true"))
//            {
//                disableButtons();
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//    }
//}