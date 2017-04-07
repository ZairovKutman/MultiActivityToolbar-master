package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;

import kg.soulsb.ayu.R;

public class MessagesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
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
