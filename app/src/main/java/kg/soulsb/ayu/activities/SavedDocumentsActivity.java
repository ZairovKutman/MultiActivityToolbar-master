package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.OrderAdapter;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

public class SavedDocumentsActivity extends BaseActivity {

    ListView listViewDocuments;
    ArrayList<Order> orderArrayList;
    ArrayAdapter<Order> arrayAdapter;
    Spinner otborSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_documents);


        // Создаем отбор
        otborSpinner = (Spinner) findViewById(R.id.spinner_otbor);
        ArrayList<String> otborArrayList = new ArrayList<String>();
        otborArrayList.add("Все документы");
        otborArrayList.add("Выгруженные документы");
        otborArrayList.add("Невыгруженные документы");
        ArrayAdapter<String> otborAdapter = new ArrayAdapter<String>(this,R.layout.baza_spinner_item,otborArrayList);
        otborSpinner.setAdapter(otborAdapter);

        otborSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (otborSpinner.getItemAtPosition(position).equals("Выгруженные документы"))
                {
                    ArrayList<Order> arrayList2 = new ArrayList<>();
                    for (Order item: orderArrayList)
                    {
                        if (item.isDelivered())
                        {
                            arrayList2.add(item);
                        }
                    }
                    arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this,R.layout.list_docs_layout, arrayList2);
                    listViewDocuments.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
                else if (otborSpinner.getItemAtPosition(position).equals("Невыгруженные документы"))
                {
                    ArrayList<Order> arrayList2 = new ArrayList<>();
                    for (Order item: orderArrayList)
                    {
                        if (!item.isDelivered())
                        {
                            arrayList2.add(item);
                        }
                    }
                    arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this,R.layout.list_docs_layout, arrayList2);
                    listViewDocuments.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this,R.layout.list_docs_layout, orderArrayList);
                    listViewDocuments.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        orderArrayList = new OrdersRepo().getOrdersObject(CurrentBaseClass.getInstance().getCurrentBase());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, orderArrayList);

        listViewDocuments = (ListView) findViewById(R.id.list_view_documents);
        listViewDocuments.setAdapter(arrayAdapter);
        listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(),OrderAddActivity.class);
                intent.putExtra("doctype",orderArrayList.get(i).getDoctype());
                intent.putExtra("savedobj", orderArrayList.get(i));
                if (orderArrayList.get(i).isDelivered())
                {
                    intent.putExtra("isDelivered","true");
                }
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_journal:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderArrayList = new OrdersRepo().getOrdersObject(CurrentBaseClass.getInstance().getCurrentBase());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, orderArrayList);
        arrayAdapter.notifyDataSetChanged();
    }
}
