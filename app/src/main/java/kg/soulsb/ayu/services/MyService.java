package kg.soulsb.ayu.services;

/**
 * Created by Sultanbek Baibagyshev on 2/15/17.
 */

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.LocationRequest;

import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.functions.Consumer;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.MyLocationsRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.MyLocation;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class MyService extends Service {
    private static final String TAG = "MyService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 2000;
    private static final float LOCATION_DISTANCE = 1;
    Location mLastLocation = new Location("null");
    String currentBaseString = "";
    Baza baza;
    private Timer mTimer = null;
    boolean isSending = false;
    ArrayList<MyLocation> arrayList = new ArrayList<>();

    private class LocationListener implements android.location.LocationListener {
        private Baza baza;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            if (location.getAccuracy() < 100) {
                mLastLocation.set(location);
                CurrentLocationClass.getInstance().setCurrentLocation(mLastLocation);
            }
        }


        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    private void prepareSending() {
        try {
            currentBaseString = CurrentBaseClass.getInstance().getCurrentBaseObject().getName();
        } catch (Exception e) {
            System.out.println("Application is dead");
            return;
        }

        if (mLastLocation.getLatitude() == 0 && mLastLocation.getLongitude() == 0) return;
        baza = CurrentBaseClass.getInstance().getCurrentBaseObject();
        String mHost = baza.getHost();
        int mPort = baza.getPort();
        try {
            MyService.GrpcTask grpcTask = new MyService.GrpcTask(ManagedChannelBuilder.forAddress(mHost, mPort)
                    .usePlaintext(true).build(), CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());

            grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        System.out.println("started onCreate");
        Log.e(TAG, "onCreate");
        LocationRequest request = LocationRequest.create() //standard GMS LocationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setInterval(100);

        ReactiveLocationProvider locationProvider = new ReactiveLocationProvider(getApplicationContext());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Subscription subscription = locationProvider.getUpdatedLocation(request).subscribe(new Subscription<Location>() {

 f
        });
    }
    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            prepareSending();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }


    private class GrpcTask extends AsyncTask<Void, Void, Points> {
        private ManagedChannel mChannel;
        private String name;

        private GrpcTask(ManagedChannel mChannel, String name) {
            this.mChannel = mChannel;
            this.name = name;
        }
        /**
         * Метод срабатывает перед началом работы AsyncTask
         */
        @Override
        protected void onPreExecute() {
        }

        private Points sendLocation(ManagedChannel mChannel) {
            arrayList = new ArrayList<>();
            AyuServiceGrpc.AyuServiceBlockingStub blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
            kg.soulsb.ayu.grpctest.nano.Location request = new kg.soulsb.ayu.grpctest.nano.Location();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            Agent agent = new Agent();
            agent.name = name;
            request.agent = agent;
            request.date = formattedDate;
            request.latitude = Double.toString(mLastLocation.getLatitude());
            request.longitude = Double.toString(mLastLocation.getLongitude());
            request.speed = Double.toString(mLastLocation.getSpeed());

            DBHelper dbHelper = new DBHelper(getBaseContext());
            DatabaseManager.initializeInstance(dbHelper);

            MyLocationsRepo myLocationsRepo = new MyLocationsRepo();
            OperationStatus bl = blockingStub.sendLocation(request);
            if (bl.status == 1) {
                myLocationsRepo.insert(new MyLocation(Double.toString(mLastLocation.getLatitude()), Double.toString(mLastLocation.getLongitude()), name, formattedDate, Double.toString(mLastLocation.getSpeed())));
            }
            if (!isSending) {
                isSending = true;
                arrayList = myLocationsRepo.getMyLocationsObject();
                for (MyLocation list : arrayList) {
                    agent = new Agent();
                    agent.name = list.getAgent();
                    request.agent = agent;
                    request.date = list.getFormattedDate();
                    request.latitude = list.getLatitude();
                    request.longitude = list.getLongitude();
                    request.speed = Double.toString(Double.valueOf(list.getSpeed())*3600/1000) ;

                    myLocationsRepo = new MyLocationsRepo();
                    bl = blockingStub.sendLocation(request);
                    if (bl.status == 0)
                        myLocationsRepo.delete(list);
                }
                isSending = false;
            }
            return null;
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
                System.out.println("DoInBackround started");
                return sendLocation(mChannel);
            } catch (Exception e) {
                e.printStackTrace();

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDate = df.format(c.getTime());

                MyLocationsRepo myLocationsRepo = new MyLocationsRepo();
                MyLocation myLocation = new MyLocation();
                myLocation.setAgent(name);
                myLocation.setLatitude(Double.toString(mLastLocation.getLatitude()));
                myLocation.setLongitude(Double.toString(mLastLocation.getLongitude()));
                myLocation.setFormattedDate(formattedDate);
                myLocation.setSpeed(Double.toString(mLastLocation.getSpeed()));

                myLocationsRepo.insert(myLocation);
                return
                        // Возвращает пустой итератор
                        null;
            }
        }

        @Override
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}