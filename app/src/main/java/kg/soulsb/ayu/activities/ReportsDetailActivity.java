package kg.soulsb.ayu.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import kg.soulsb.ayu.R;

public class ReportsDetailActivity extends BaseActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        webView = (WebView) findViewById(R.id.webView);
        webView.loadData(getIntent().getStringExtra("decoded"),"text/html; charset=utf-8","UTF-8");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);

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
