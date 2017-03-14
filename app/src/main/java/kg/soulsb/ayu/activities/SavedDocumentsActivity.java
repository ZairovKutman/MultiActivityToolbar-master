package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.Serializable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_documents);

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
}
