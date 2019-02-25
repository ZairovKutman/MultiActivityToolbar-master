package kg.soulsb.ayu.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Point;
import kg.soulsb.ayu.grpctest.nano.PointLocation;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.UserSettings;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ClientDetailActivity extends BaseActivity implements LocationListener {

    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;
    TextView clientNameTextView;
    TextView clientPhoneTextView;
    TextView clientAddressTextView;
    TextView clientCommentTextView;
    TextView clientLatitudeTextView;
    TextView clientLongitudeTextView;
    TextView clientDebtTextView;
    TextView clientAccuracy;
    Button clientCallButton;
    Button clientMapButton;
    Button clientSaveLocationButton;

    AlertDialog.Builder d;
    public Baza baza;
    ProgressBar progressBar;
    AlertDialog alertDialog;

    protected LocationManager locationManager;
    protected Location currentLocation = new Location("client");

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        clientNameTextView = (TextView) findViewById(R.id.ClientName);
        clientPhoneTextView = (TextView) findViewById(R.id.ClientPhone);
        clientAddressTextView = (TextView) findViewById(R.id.ClientAddress);
        clientCommentTextView = (TextView) findViewById(R.id.ClientComment);
        clientLatitudeTextView = (TextView) findViewById(R.id.ClientLatitude);
        clientLongitudeTextView = (TextView) findViewById(R.id.ClientLongitude);
        clientCallButton = (Button) findViewById(R.id.ClientCall);
        clientMapButton = (Button) findViewById(R.id.ClientMap);
        clientSaveLocationButton = (Button) findViewById(R.id.Client_saveLocation);
        clientDebtTextView = (TextView) findViewById(R.id.ClientDebt);
        clientAccuracy = (TextView) findViewById(R.id.Client_accuracy);

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

        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
        if (sharedPreferences.getString(UserSettings.can_get_gpc_coordinates_of_clients,"false").equals("false")) {
                clientSaveLocationButton.setEnabled(false);
                clientAccuracy.setVisibility(View.INVISIBLE);
        }
        else {
            clientSaveLocationButton.setEnabled(true);
            clientAccuracy.setVisibility(View.VISIBLE);
        }

        clientSaveLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentLocation.getLatitude()!=0 || currentLocation.getLongitude()!=0)
                {
                    if (currentLocation.getAccuracy()<=20) {
                    baza = CurrentBaseClass.getInstance().getCurrentBaseObject();


                    String mHost = baza.getHost();
                    int mPort = baza.getPort();
                    d = new AlertDialog.Builder(ClientDetailActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.loading_dialog, null);
                    progressBar = (ProgressBar) dialogView.findViewById(R.id.loading_bar);

                    d.setTitle("Отправка координатов");
                    d.setMessage("Подождите...");
                    d.setView(dialogView);
                    alertDialog = d.create();
                    alertDialog.show();

                    ClientDetailActivity.GrpcTask grpcTask = new ClientDetailActivity.GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
                            .usePlaintext(true).build(),CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                    grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);}
                    else
                    {
                        Toast.makeText(getBaseContext(),"Точность должна быть меньше 20 м.",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                    Toast.makeText(getBaseContext(),"Местоположение не определено.",Toast.LENGTH_SHORT).show();

            }
        });

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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getAccuracy()>=20)
        {
            clientAccuracy.setTextColor(Color.RED);
            clientSaveLocationButton.setEnabled(false);
        }
        else
        {
            clientAccuracy.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
            if (!sharedPreferences.getString(UserSettings.can_get_gpc_coordinates_of_clients,"false").equals("false")) {
                clientSaveLocationButton.setEnabled(true);
                clientAccuracy.setText("Точность: "+location.getAccuracy()+" м.");
                currentLocation = location;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class GrpcTask extends AsyncTask<Void, Void, Points> {
        private ManagedChannel mChannel;
        private String name;

        public GrpcTask(ManagedChannel mChannel, String name) {
            this.mChannel = mChannel;
            this.name = name;
        }
        /**
         * Метод срабатывает перед началом работы AsyncTask
         */
        @Override
        protected void onPreExecute() {
        }

        /**
         * Метод отрабатывает код в фоновом режиме.
         *
         * @param nothing
         * @return
         */
        @Override
        protected Points doInBackground(Void... nothing) {
            try {
                return sendLocationGRPC(mChannel);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Points sendLocationGRPC(ManagedChannel mChannel) {
            blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
            Agent request = new Agent();
            request.name = name;

            String android_id = android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Device device = new Device();
            device.agent = name;
            device.deviceId = android_id;
            device.modelDescription = Build.MANUFACTURER + " " + Build.MODEL;
            DeviceStatus deviceStatus = blockingStub.checkDeviceStatus(device);
            System.out.println(deviceStatus.comment);
            if (!deviceStatus.active) {
                return null;
            }

            PointLocation pointLocation = new PointLocation();
            pointLocation.agent = request;
            Point point = new Point();
            point.guid = getIntent().getStringExtra("guid");
            point.address = getIntent().getStringExtra("address");
            point.debt = Double.parseDouble(getIntent().getStringExtra("debt"));
            point.description = getIntent().getStringExtra("name");
            point.phoneNumber = getIntent().getStringExtra("phone");

            pointLocation.point = point;

            pointLocation.latitude = Double.toString(currentLocation.getLatitude());
            pointLocation.longitude = Double.toString(currentLocation.getLongitude());
            OperationStatus bl = blockingStub.setPointLocation(pointLocation);
            System.out.println("location status: "+bl.status+" "+bl.comment);

            return new Points();
        }

        @Override
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            alertDialog.dismiss();
            if (pointIterator == null)
            {
                Toast.makeText(getBaseContext(),"Ошибка, доступ запрещен!",Toast.LENGTH_SHORT).show();
            }
            else {
                new ClientsRepo().setClientLocation(getIntent().getStringExtra("guid"),currentLocation);
                clientLatitudeTextView.setText("Широта: "+Double.toString(currentLocation.getLatitude()));
                clientLongitudeTextView.setText("Долгота: "+Double.toString(currentLocation.getLongitude()));
                Toast.makeText(getBaseContext(),"Успех! геолокация отправлена. =)",Toast.LENGTH_SHORT).show();
            }
        }
    }
}