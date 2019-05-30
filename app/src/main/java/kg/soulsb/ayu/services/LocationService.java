package kg.soulsb.ayu.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
import kg.soulsb.ayu.models.KalmanLatLong;
import kg.soulsb.ayu.models.MyLocation;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;

import static android.content.ContentValues.TAG;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class LocationService extends Service implements LocationListener, GpsStatus.Listener {
    public static final String LOG_TAG = LocationService.class.getSimpleName();

    private final LocationServiceBinder binder = new LocationServiceBinder();
    boolean isLocationManagerUpdatingLocation;

    String currentBaseString="";
    Baza baza;
    String android_id = "";
    boolean isSending = false;
    Location mLastLocation = new Location("null");
    Location lastSentLocation = null;
    ArrayList<MyLocation> arrayList = new ArrayList<>();
    public static final String ACTION_LOCATION_BROADCAST = "LocationBroadcast";


    ArrayList<Location> locationList;

    ArrayList<Location> oldLocationList;
    ArrayList<Location> noAccuracyLocationList;
    ArrayList<Location> kalmanNGLocationList;


    float currentSpeed = 0.0f; // meters/second

    KalmanLatLong kalmanFilter;
    long runStartTimeInMillis;

    int gpsCount;


    public LocationService() {

    }

    @Override
    public void onCreate() {
        isLocationManagerUpdatingLocation = false;
        locationList = new ArrayList<>();
        noAccuracyLocationList = new ArrayList<>();
        oldLocationList = new ArrayList<>();
        kalmanNGLocationList = new ArrayList<>();
        kalmanFilter = new KalmanLatLong(3);
        startUpdatingLocation();

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
    public int onStartCommand(Intent i, int flags, int startId) {
        super.onStartCommand(i, flags, startId);
        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onRebind(Intent intent) {
        Log.d(LOG_TAG, "onRebind ");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOG_TAG, "onUnbind ");

        return true;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy ");
    }


    //This is where we detect the app is being killed, thus stop service.
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(LOG_TAG, "onTaskRemoved ");
//        this.stopUpdatingLocation();
//        getApplicationContext().unregisterReceiver(gpsReceiver);
//        stopSelf();
    }

    /**
     * Binder class
     *
     * @author Takamitsu Mizutori
     *
     */
    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }



    /* LocationListener implemenation */
    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("onSTATUS DISABLED "+provider);
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            notifyLocationProviderStatusUpdated(false);
        }

    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("onSTATUS ENABLED "+provider);
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            notifyLocationProviderStatusUpdated(true);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("onSTATUS CHANGED "+provider);
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            if (status == LocationProvider.OUT_OF_SERVICE) {
                notifyLocationProviderStatusUpdated(false);
            } else {
                notifyLocationProviderStatusUpdated(true);
            }
        }
    }

    /* GpsStatus.Listener implementation */
    public void onGpsStatusChanged(int event) {


    }

        private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                sendMessageToUI("checkgps");
            }
        }
    };

    private void notifyLocationProviderStatusUpdated(boolean isLocationProviderAvailable) {
        sendMessageToUI("checkgps");
    }

    public void startUpdatingLocation() {

            getApplicationContext().registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
            locationList.clear();

            kalmanNGLocationList.clear();

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            System.out.println("Я Сервис, и начал работу =) ");
            //Exception thrown when GPS or Network provider were not available on the user's device.
            try {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE); //setAccuracyは内部では、https://stackoverflow.com/a/17874592/1709287の用にHorizontalAccuracyの設定に変換されている。
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                //API level 9 and up
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                //criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
                //criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

                Integer gpsFreqInMillis = 20000;
                Integer gpsFreqInDistance = 2;  // in meters

                locationManager.addGpsStatusListener(this);

                locationManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, this, null);

            } catch (IllegalArgumentException e) {
                System.out.println("oops, im here");
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (SecurityException e) {
                System.out.println("oops, im here2");
                Log.e(LOG_TAG, e.getLocalizedMessage());
            } catch (RuntimeException e) {
                System.out.println("oops, im here3");
                Log.e(LOG_TAG, e.getLocalizedMessage());
            }
    }


    public void stopUpdatingLocation(){
        if(this.isLocationManagerUpdatingLocation){
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.removeUpdates(this);
            isLocationManagerUpdatingLocation = false;
        }
    }

    @Override
    public void onLocationChanged(final Location newLocation) {

        gpsCount++;
        filterAndAddLocation(newLocation);
    }

    @SuppressLint("NewApi")
    private long getLocationAge(Location newLocation){
        long locationAge;
        if(android.os.Build.VERSION.SDK_INT >= 17) {
            long currentTimeInMilli = (long)(SystemClock.elapsedRealtimeNanos() / 1000000);
            long locationTimeInMilli = (long)(newLocation.getElapsedRealtimeNanos() / 1000000);
            locationAge = currentTimeInMilli - locationTimeInMilli;
        }else{
            locationAge = System.currentTimeMillis() - newLocation.getTime();
        }
        return locationAge;
    }


    private boolean filterAndAddLocation(Location location){

        long age = getLocationAge(location);

        if(age > 40 * 1000){ //more than 5 seconds
            Log.d(TAG, "Location is old");
            oldLocationList.add(location);
            sendMessageToUI("yes_yellow");
            return false;
        }

        if(location.getAccuracy() <= 0){
            Log.d(TAG, "Latitidue and longitude values are invalid.");
            noAccuracyLocationList.add(location);
            sendMessageToUI("no");
            return false;
        }

        //setAccuracy(newLocation.getAccuracy());
        float horizontalAccuracy = location.getAccuracy();
        if(horizontalAccuracy > 80){ //10meter filter
            Log.d(TAG, "Accuracy is too low.");
            sendMessageToUI("yes_yellow");
            return false;
        }


        /* Kalman Filter */
        float Qvalue;

        long locationTimeInMillis = (long)(location.getElapsedRealtimeNanos() / 1000000);
        long elapsedTimeInMillis = locationTimeInMillis - runStartTimeInMillis;

        if(currentSpeed == 0.0f){
            Qvalue = 3.0f; //3 meters per second
        }else{
            Qvalue = currentSpeed; // meters per second
        }

        kalmanFilter.Process(location.getLatitude(), location.getLongitude(), location.getAccuracy(), elapsedTimeInMillis, Qvalue);
        double predictedLat = kalmanFilter.get_lat();
        double predictedLng = kalmanFilter.get_lng();

        Location predictedLocation = new Location("");//provider name is unecessary
        predictedLocation.setLatitude(predictedLat);//your coords of course
        predictedLocation.setLongitude(predictedLng);
        float predictedDeltaInMeters =  predictedLocation.distanceTo(location);

        if(predictedDeltaInMeters > 88){
            Log.d(TAG, "Kalman Filter detects mal GPS, we should probably remove this from track");
            kalmanFilter.consecutiveRejectCount += 1;
            sendMessageToUI("yes_yellow");
            if(kalmanFilter.consecutiveRejectCount > 3){
                kalmanFilter = new KalmanLatLong(3); //reset Kalman Filter if it rejects more than 3 times in raw.
            }

            kalmanNGLocationList.add(location);
            return false;
        }
        else{
            kalmanFilter.consecutiveRejectCount = 0;
        }

        Log.d(TAG, "Location quality is good enough.");
        currentSpeed = location.getSpeed();
        //locationList.add(location);
        location.setLatitude(predictedLocation.getLatitude());
        location.setLongitude(predictedLocation.getLongitude());
        mLastLocation.set(location);

        if (lastSentLocation == null) {
            lastSentLocation = location;
            //prepareSending();
            sendMessageToUI("yes_green");
            CurrentLocationClass.getInstance().setCurrentLocation(location);
            return true;
        }

        lastSentLocation.set(mLastLocation);
        prepareSending();

        sendMessageToUI("yes_green");
        CurrentLocationClass.getInstance().setCurrentLocation(location);
        Log.d(TAG, "(" + location.getLatitude() + "," + location.getLongitude() + ")");
        return true;
    }

    private void sendMessageToUI(String active) {

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("active", active);
        intent.putExtra("lat",mLastLocation.getLatitude());
        intent.putExtra("lon",mLastLocation.getLongitude());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void saveLocationInDatabase(Location loc, String name) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(loc.getTime());

        MyLocationsRepo myLocationsRepo = new MyLocationsRepo();
        MyLocation myLocation = new MyLocation();
        myLocation.setAgent(name);
        myLocation.setLatitude(loc.getLatitude());
        myLocation.setLongitude(loc.getLongitude());
        myLocation.setFormattedDate(formattedDate);
        myLocation.setSpeed(loc.getSpeed());
        myLocation.setDeviceID(android_id);
        myLocation.setAccuracy(loc.getAccuracy());
        myLocationsRepo.insert(myLocation);
        System.out.println("____LOCATION SAVED IN DATABASE____");

    }

    private void prepareSending() {
        try {
            currentBaseString = CurrentBaseClass.getInstance().getCurrentBaseObject().getName();
        }
        catch (Exception e) {
            System.out.println("Application is dead");
            return;
        }

        baza = CurrentBaseClass.getInstance().getCurrentBaseObject();
        if (CurrentBaseClass.getInstance().getCurrentBaseObject().getName().equals("")) {
            System.out.println("базы нет, отмена!");
            return;
        }
        String mHost = baza.getHost();
        int mPort = baza.getPort();
        try {
            LocationService.GrpcTask grpcTask = new LocationService.GrpcTask(ManagedChannelBuilder.forAddress(mHost, mPort)
                    .usePlaintext(true).build(), CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());

            grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            DatabaseManager.initializeInstance(dbHelper);

            MyLocationsRepo myLocationsRepo = new MyLocationsRepo();

            OperationStatus bl = blockingStub.sendLocation(request);

            if (bl.status == 1) {
                System.out.println("insertion occured");
                saveLocationInDatabase(mLastLocation, name);
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
                    System.out.println(bl.status+" <- Saved location uploading status");
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

                saveLocationInDatabase(mLastLocation, name);
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
