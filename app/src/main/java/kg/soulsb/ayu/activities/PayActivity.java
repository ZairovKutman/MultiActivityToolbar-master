package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;

import kg.soulsb.ayu.R;

public class PayActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_pay:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
