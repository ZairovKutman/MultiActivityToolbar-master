package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.PriceType;

public class ItemDetailActivity extends BaseActivity {

    TextView itemName;
    TextView itemEd;
    ListView itemPriceList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<PriceType> priceTypesArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Item item = (Item) getIntent().getSerializableExtra("item");

        itemName = (TextView) findViewById(R.id.item_name);
        itemName.setText(item.getName());

        itemEd = (TextView) findViewById(R.id.item_ed);
        itemEd.setText("ед. измерения: "+item.getUnit());

        itemPriceList = (ListView) findViewById(R.id.item_price_list);

        priceTypesArrayList = new PriceTypesRepo().getPricetypesObject();


        for (PriceType priceType: priceTypesArrayList)
        {
            arrayList.add(""+priceType.getName()+": "+Double.toString(new PricesRepo().getItemPriceByPriceType(item.getGuid(),priceType.getGuid()))+" сом");
        }

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        itemPriceList.setAdapter(arrayAdapter);
        itemPriceList.setEnabled(false);
    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.nav)
//            return true;
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}