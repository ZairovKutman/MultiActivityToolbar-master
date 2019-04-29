package kg.soulsb.ayu.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import kg.soulsb.ayu.R;


import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.util.List;

public class ScanDocumentActivity extends BaseActivity {

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

    // use a compound button so either checkbox or switch widgets work.
    private TextView statusMessage;
    private TextView barcodeValue;

    private Button scanButton;
    private Button scanDocButton;
    private ImageView imageViewScanDoc;
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_document);

        statusMessage = (TextView)findViewById(R.id.textView_qrcode_found);
        barcodeValue = (TextView)findViewById(R.id.textView_qrcode_guid);

        scanButton = (Button) findViewById(R.id.scan_button);
        imageViewScanDoc = (ImageView) findViewById(R.id.imageView_scan_doc);
        scanDocButton = (Button) findViewById(R.id.button_scan_doc);

        statusMessage.setVisibility(View.INVISIBLE);
        barcodeValue.setVisibility(View.INVISIBLE);
        imageViewScanDoc.setVisibility(View.INVISIBLE);
        scanDocButton.setVisibility(View.INVISIBLE);



        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch barcode activity.
                Intent intent = new Intent(ScanDocumentActivity.this, BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }
        });
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
