package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import java.util.ArrayList;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.TovarAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.models.Item;

public class ItemsTableActivity extends BaseActivity {
    private ListView listViewTovary;
    private TovarAdapter arrayAdapter;
    private ArrayList<Item> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_tovar_fragment);

        listViewTovary = (ListView) findViewById(R.id.list_view_tovary);
        DBHelper dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);

        arrayList = new ItemsRepo().getItemsObject();
        arrayAdapter = new TovarAdapter(this,R.layout.list_tovary_layout, arrayList);
        listViewTovary.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_catalog:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
