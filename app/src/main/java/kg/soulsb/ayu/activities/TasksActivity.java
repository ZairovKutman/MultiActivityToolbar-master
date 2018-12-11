package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.ExpandableListAdapter;

public class TasksActivity extends BaseActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.tasks_listview);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_catalog:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Супермаркет Фрунзе");
        listDataHeader.add("Магазин Народный");
        listDataHeader.add("Глобус гипермаркет");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Создать заказ");
        top250.add("Создать оплату");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("Создать фото-отчет");


        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("Создать заказ");
        comingSoon.add("Создать фото-отчет");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }
}
