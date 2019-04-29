package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tovar_table_activity);

        listViewTovary = (ListView) findViewById(R.id.list_view_tovary);
        DBHelper dbHelper = new DBHelper(getBaseContext());
        DatabaseManager.initializeInstance(dbHelper);

        arrayList = new ItemsRepo().getItemsObject();
        arrayAdapter = new TovarAdapter(this,R.layout.list_tovary_layout, arrayList);
        listViewTovary.setAdapter(arrayAdapter);

        listViewTovary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(),ItemDetailActivity.class);
                intent.putExtra("item",arrayList.get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_search, menu);
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
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }
}
