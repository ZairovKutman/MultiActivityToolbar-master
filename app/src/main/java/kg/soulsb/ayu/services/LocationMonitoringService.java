package kg.soulsb.ayu.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
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
import kg.soulsb.ayu.singletons.UserSettings;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;


public class LocationMonitoringService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = LocationMonitoringService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    Location mLastLocation = new Location("null");
    ArrayList<MyLocation> arrayList = new ArrayList<>();
    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    boolean isSending = false;
    Timer mTimer = null;
    String currentBaseString="";
    Baza baza;
    String android_id = "";

    @Override
    public void onCreate() {
        super.onCreate();
        android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            builder = new Notification.Builder(this, "Аю-Агент GPS");
        } else {
            builder = new Notification.Builder(this);
        }
        Notification notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("АЮ-Агент")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("АЮ-Агент")
                .setContentText("GPS сервис работает")
                .build();
        startForeground(785826, notification);
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String id = "Аю-Агент GPS";
            CharSequence name = "АЮ-Агент";
            String description = "GPS сервис работает";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(false);
            mChannel.enableVibration(false);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendMessageToUI("destroyed");
        stopForeground(true);
        unregisterReceiver(gpsReceiver);
    }

    private void prepareSending() {
        try {
            currentBaseString = CurrentBaseClass.getInstance().getCurrentBaseObject().getName();
        }
        catch (Exception e) {
            System.out.println("Application is dead");
            return;
        }

        if (mLastLocation.getLatitude() == 0 && mLastLocation.getLongitude() == 0) return;
        baza = CurrentBaseClass.getInstance().getCurrentBaseObject();
        String mHost = baza.getHost();
        int mPort = baza.getPort();
        try {
            LocationMonitoringService.GrpcTask grpcTask = new LocationMonitoringService.GrpcTask(ManagedChannelBuilder.forAddress(mHost, mPort)
                    .usePlaintext(true).build(), CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());

            grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                sendMessageToUI("checkgps");
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(UserSettings.LOCATION_INTERVAL);
        mLocationRequest.setFastestInterval(UserSettings.FASTEST_LOCATION_INTERVAL);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        mLocationClient.connect();

        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        //        if (mTimer != null) // Cancel if already existed
//            mTimer.cancel();
//        else
//            mTimer = new Timer();
//
//        mTimer.scheduleAtFixedRate(new LocationMonitoringService.TimeDisplay(),10000,20000);
//        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.
            sendMessageToUI("no");
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
        sendMessageToUI("no");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null)
            {
            Calendar c = Calendar.getInstance();
            mLastLocation.set(location);
            prepareSending();
            sendMessageToUI("yes_green");
            CurrentLocationClass.getInstance().setCurrentLocation(location);
            }
        else
            {
                sendMessageToUI("yes_yellow");
            }
            System.out.println(String.valueOf(location.getLatitude())+" ______-______ "+String.valueOf(location.getLongitude()));
        }


    private void sendMessageToUI(String active) {

        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("active", active);
        intent.putExtra("test", mLastLocation.getLatitude()+" - "+mLastLocation.getLongitude()+", "+mLastLocation.getTime());
        intent.putExtra("lat",mLastLocation.getLatitude());
        intent.putExtra("lon",mLastLocation.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");
        sendMessageToUI("no");

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
            String formattedDate = df.format(mLastLocation.getTime());


            Agent agent = new Agent();
            agent.name = name;
            request.agent = agent;
            request.date = formattedDate;
            request.latitude = mLastLocation.getLatitude();
            request.longitude = mLastLocation.getLongitude();
            request.speed = mLastLocation.getSpeed();
            request.deviceId = android_id;
            request.accuracy = mLastLocation.getAccuracy();
            DBHelper dbHelper = new DBHelper(getBaseContext());
            DatabaseManager.initializeInstance(dbHelper);

            MyLocationsRepo myLocationsRepo = new MyLocationsRepo();

            OperationStatus bl = blockingStub.sendLocation(request);

            if (bl.status == 1) {
                System.out.println("insertion occured");
                myLocationsRepo.insert(new MyLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), name, formattedDate, mLastLocation.getSpeed(), mLastLocation.getAccuracy(), android_id));
            }
            System.out.println(bl.status+" <- ANSWER");
            if (!isSending) {
                isSending = true;
                arrayList = myLocationsRepo.getMyLocationsObject();
                System.out.println(arrayList);
                for (MyLocation list : arrayList) {
                    agent = new Agent();
                    agent.name = list.getAgent();
                    request.agent = agent;
                    request.date = list.getFormattedDate();
                    request.latitude = list.getLatitude();
                    request.longitude = list.getLongitude();
                    request.speed = list.getSpeed()*3600/1000;
                    request.deviceId = list.getDeviceID();
                    request.accuracy = list.getAccuracy();
                    myLocationsRepo = new MyLocationsRepo();
                    bl = blockingStub.sendLocation(request);
                    System.out.println(bl.status+" <- ANSWER INSIDE");
                    if (bl.status == 0) {
                        myLocationsRepo.delete(list);
                        System.out.println("Location DONE");
                    }
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

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDate = df.format(mLastLocation.getTime());

                MyLocationsRepo myLocationsRepo = new MyLocationsRepo();
                MyLocation myLocation = new MyLocation();
                myLocation.setAgent(name);
                myLocation.setLatitude(mLastLocation.getLatitude());
                myLocation.setLongitude(mLastLocation.getLongitude());
                myLocation.setFormattedDate(formattedDate);
                myLocation.setSpeed(mLastLocation.getSpeed());
                myLocation.setDeviceID(android_id);
                myLocation.setAccuracy(mLastLocation.getAccuracy());
                myLocationsRepo.insert(myLocation);
                System.out.println("____LOCATION SAVED IN DATABASE____");
                return
                        // Возвращает пустой итератор
                        null;
            }
        }

        @Override
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}