package kg.soulsb.ayu.activities.zakaz;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.RecyclerTovarAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.models.Item;

public class TovarsFragment extends Fragment {

    Spinner otborSpinner;

    OrderAddActivity parentActivity;
    SearchView searchView;

    ArrayAdapter<String> otborAdapter;
    RecyclerView recyclerView;
    PricesRepo pricesRepo;
    StocksRepo stocksRepo;
    ArrayList<Item> arrayListAllTovars = new ArrayList<>();
    List<String> categories = new ArrayList<>();
    RecyclerTovarAdapter recyclerTovarAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = ((OrderAddActivity)getActivity());

        DBHelper dbHelper = new DBHelper(parentActivity.getApplicationContext());
        DatabaseManager.initializeInstance(dbHelper);
        categories = new ItemsRepo().getItemCategories();
//        if (parentActivity.order == null) {
            arrayListAllTovars = new ItemsRepo().getItemsObjectByCategory(parentActivity.category);
//        }
//        else
//        {
//            arrayListAllTovars = new ItemsRepo().getItemsObjectByCategory(parentActivity.order.getDogovorObject().getCategory());
//        }


    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tovars_fragment,container,false);


        // ?????????????? ???????????? ??????????????
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_tovars);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(parentActivity.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //

        pricesRepo = new PricesRepo();
        stocksRepo = new StocksRepo();

        // ?????????????? ??????????
        otborSpinner = (Spinner) v.findViewById(R.id.spinner_otbor);

        List<String> staticOtborList = new ArrayList<>();
        staticOtborList.add("???????????????? ??????");
        staticOtborList.add("???????????????? ?????????????????? ????????????");
        staticOtborList.add("???????????????? ???????????? ?? ??????????????");

        // ?????????????? ???????????? ???? ????????????????????
        List<String> otborArrayList = Stream.concat(staticOtborList.stream(), categories.stream())
                .collect(Collectors.toList());

        if (parentActivity.order != null){
            for (Item item: parentActivity.order.getArraylistTovar() )
            {
                if (arrayListAllTovars.indexOf(item) != -1) {
                    arrayListAllTovars.get(arrayListAllTovars.indexOf(item)).setMyUnit(item.getMyUnit());
                    arrayListAllTovars.get(arrayListAllTovars.indexOf(item)).setQuantity(item.getQuantity());
                    arrayListAllTovars.get(arrayListAllTovars.indexOf(item)).setSum(item.getSum());
                    arrayListAllTovars.get(arrayListAllTovars.indexOf(item)).setPrice(item.getPrice());
                    parentActivity.addItem(item);
                }
            }
            checkDeliveredDoc();
        }
        recyclerTovarAdapter = new RecyclerTovarAdapter(arrayListAllTovars,parentActivity.orderedItemsArrayList,parentActivity.othersFragment);

        otborAdapter = new ArrayAdapter<String>(this.getActivity(),R.layout.baza_spinner_item,otborArrayList);
        otborSpinner.setAdapter(otborAdapter);
        otborSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (otborSpinner.getItemAtPosition(position).equals("???????????????? ?????????????????? ????????????")) {
                    recyclerTovarAdapter.setSpinnerSelectedState(1, null);
                } else if (otborSpinner.getItemAtPosition(position).equals("???????????????? ???????????? ?? ??????????????")) {
                    recyclerTovarAdapter.setSpinnerSelectedState(2, null);
                } else if (otborSpinner.getItemAtPosition(position).equals("???????????????? ??????")) {
                    recyclerTovarAdapter.setSpinnerSelectedState(0, null);
                } else {
                    String category = categories.get(position - 3);
                    recyclerTovarAdapter.setSpinnerSelectedState(-1, category);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


//        if (otborSpinner.getSelectedItem().equals("???????????????? ?????????????????? ????????????")) {
//            recyclerTovarAdapter.setSpinnerSelectedState(1);
//        } else {
//            recyclerTovarAdapter.setSpinnerSelectedState(0);
//        }


        recyclerView.setAdapter(recyclerTovarAdapter);
        recyclerTovarAdapter.notifyDataSetChanged();
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
                recyclerTovarAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerTovarAdapter.getFilter().filter(newText);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            recyclerTovarAdapter.getFilter().filter("");
            return false;
        });
    }


    private void checkDeliveredDoc() {
        if (parentActivity.isDelivered.equals("true"))
        {
            recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                    return false;
                }
                @Override
                public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) { }
                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean b) { }
            });

            otborSpinner.setSelection(1);

        }
    }

    public void updatePrices()
    {
        recyclerTovarAdapter.updatePrices(parentActivity.getPriceType());
        recyclerTovarAdapter.notifyDataSetChanged();
        checkDeliveredDoc();
    }

    public void updateStock()
    {
        recyclerTovarAdapter.updateStock(parentActivity.getWarehouse());
        recyclerTovarAdapter.notifyDataSetChanged();
        checkDeliveredDoc();

    }

    public void updateSalesHistory()
    {
        recyclerTovarAdapter.updateSalesHistory(parentActivity.addOrderFragment.clientGUID);
        recyclerTovarAdapter.notifyDataSetChanged();
        checkDeliveredDoc();
    }
}
