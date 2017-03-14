package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;

import kg.soulsb.ayu.R;

public class OtherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_documents);
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
