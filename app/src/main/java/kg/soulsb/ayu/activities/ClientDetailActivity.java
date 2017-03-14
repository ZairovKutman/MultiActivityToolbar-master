package kg.soulsb.ayu.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.singletons.MyServiceActivatorClass;

public class ClientDetailActivity extends BaseActivity {

    TextView clientNameTextView;
    TextView clientPhoneTextView;
    TextView clientAddressTextView;
    TextView clientCommentTextView;
    TextView clientLatitudeTextView;
    TextView clientLongitudeTextView;
    TextView clientDebtTextView;
    Button clientCallButton;
    Button clientMapButton;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Дайте разрешение на звонки в настройках!", Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_details);

        clientNameTextView = (TextView) findViewById(R.id.ClientName);
        clientPhoneTextView = (TextView) findViewById(R.id.ClientPhone);
        clientAddressTextView = (TextView) findViewById(R.id.ClientAddress);
        clientCommentTextView = (TextView) findViewById(R.id.ClientComment);
        clientLatitudeTextView = (TextView) findViewById(R.id.ClientLatitude);
        clientLongitudeTextView = (TextView) findViewById(R.id.ClientLongitude);
        clientCallButton = (Button) findViewById(R.id.ClientCall);
        clientMapButton = (Button) findViewById(R.id.ClientMap);
        clientDebtTextView = (TextView) findViewById(R.id.ClientDebt);

        clientNameTextView.setText(getIntent().getStringExtra("name"));
        clientAddressTextView.setText(getIntent().getStringExtra("address"));
        clientCommentTextView.setText(getIntent().getStringExtra("comment"));
        clientPhoneTextView.setText(getIntent().getStringExtra("phone"));
        clientLatitudeTextView.setText("Широта: "+getIntent().getStringExtra("latitude"));
        clientLongitudeTextView.setText("Долгота: "+getIntent().getStringExtra("longitude"));
        clientDebtTextView.setText(getIntent().getStringExtra("debt"));
        //
        if (getIntent().getStringExtra("phone").equals(""))
        {
            clientCallButton.setEnabled(false);
        }
        else
        {
            clientCallButton.setEnabled(true);
        }

        clientCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + clientPhoneTextView.getText()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getParent(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                    return;
                }

                startActivity(intent);
            }
        });

        clientMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lat = getIntent().getStringExtra("latitude");
                String lon = getIntent().getStringExtra("longitude");
                String uri = "google.navigation:"+"q="+lat+", "+lon;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}
