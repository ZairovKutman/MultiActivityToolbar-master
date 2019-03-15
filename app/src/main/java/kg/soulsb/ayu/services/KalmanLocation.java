//package kg.soulsb.ayu.services;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.AsyncTask;
//import android.support.v4.content.LocalBroadcastManager;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
//
//import io.grpc.ManagedChannel;
//import io.grpc.ManagedChannelBuilder;
//import kg.soulsb.ayu.grpctest.nano.Agent;
//import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
//import kg.soulsb.ayu.grpctest.nano.OperationStatus;
//import kg.soulsb.ayu.grpctest.nano.Points;
//import kg.soulsb.ayu.helpers.DBHelper;
//import kg.soulsb.ayu.helpers.DatabaseManager;
//import kg.soulsb.ayu.helpers.repo.MyLocationsRepo;
//import kg.soulsb.ayu.models.Baza;
//import kg.soulsb.ayu.models.MyLocation;
//import kg.soulsb.ayu.singletons.CurrentBaseClass;
//import kg.soulsb.ayu.singletons.CurrentLocationClass;
//import mad.location.manager.lib.Commons.Utils;
//import mad.location.manager.lib.Interfaces.LocationServiceInterface;
//import mad.location.manager.lib.Services.KalmanLocationService;
//import mad.location.manager.lib.Services.ServicesHelper;
//
//import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
//
//public class KalmanLocation implements LocationServiceInterface {
//
//    String currentBaseString="";
//    Baza baza;
//    String android_id = "";
//    boolean isSending = false;
//    Location mLastLocation = new Location("null");
//    Location lastSentLocation = null;
//    ArrayList<MyLocation> arrayList = new ArrayList<>();
//    private Context m_context;
//    private Context m_appp_context;
//    private KalmanLocationService.Settings settings;
//    public static final String ACTION_LOCATION_BROADCAST = "LocationBroadcast";
//
//
//    public KalmanLocation (Context app_context, Context context) {
//        m_context = context;
//        m_appp_context = app_context;
//        android_id = android.provider.Settings.Secure.getString(app_context.getContentResolver(),
//                android.provider.Settings.Secure.ANDROID_ID);
//
//        settings = new KalmanLocationService.Settings(Utils.ACCELEROMETER_DEFAULT_DEVIATION,
//                5,
//                15000,
//                6,
//                2,
//                Utils.SENSOR_DEFAULT_FREQ_HZ,
//                null, false, Utils.DEFAULT_VEL_FACTOR, Utils.DEFAULT_POS_FACTOR);
//        ServicesHelper.addLocationServiceInterface(this);
//    }
//    @Override
//    public void locationChanged(Location location) {
//        if (lastSentLocation == null) {
//            lastSentLocation = location;
//            mLastLocation.set(location);
//            prepareSending();
//            sendMessageToUI("yes_green");
//            CurrentLocationClass.getInstance().setCurrentLocation(location);
//            return;
//        }
//
//        System.out.println("lat: "+location.getLatitude()+", lon="+location.getLongitude()+", accuracy="+location.getAccuracy());
//        mLastLocation.set(location);
//        if (mLastLocation.getTime() - lastSentLocation.getTime() > 1200000) {
//            prepareSending();
//        }
//        else {
//            saveLocationInDatabase(mLastLocation, CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
//        }
//        sendMessageToUI("yes_green");
//        CurrentLocationClass.getInstance().setCurrentLocation(location);
//
//    }
//
//    private void sendMessageToUI(String active) {
//
//        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
//        intent.putExtra("active", active);
//        intent.putExtra("lat",mLastLocation.getLatitude());
//        intent.putExtra("lon",mLastLocation.getLongitude());
//        LocalBroadcastManager.getInstance(m_appp_context).sendBroadcast(intent);
//    }
//
//    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
//                sendMessageToUI("checkgps");
//            }
//        }
//    };
//
//    public void start_k() {
//        m_context.registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
//        ServicesHelper.getLocationService(m_context, value -> {
//            if (value.IsRunning()) {
//                return;
//            }
//            value.stop();
//            value.reset(settings); //warning!! here you can adjust your filter behavior
//            value.start();
//        });
//
//    }
//
//    public void stop_k() {
//        ServicesHelper.getLocationService(m_context, value -> {
//            value.stop();
//        });
//        m_context.unregisterReceiver(gpsReceiver);
//    }
//
//
//    private void saveLocationInDatabase(Location loc, String name) {
//        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        String formattedDate = df.format(loc.getTime());
//
//        MyLocationsRepo myLocationsRepo = new MyLocationsRepo();
//        MyLocation myLocation = new MyLocation();
//        myLocation.setAgent(name);
//        myLocation.setLatitude(loc.getLatitude());
//        myLocation.setLongitude(loc.getLongitude());
//        myLocation.setFormattedDate(formattedDate);
//        myLocation.setSpeed(loc.getSpeed());
//        myLocation.setDeviceID(android_id);
//        myLocation.setAccuracy(loc.getAccuracy());
//        myLocationsRepo.insert(myLocation);
//        System.out.println("____LOCATION SAVED IN DATABASE____");
//
//    }
//
//    private void prepareSending() {
//        try {
//            currentBaseString = CurrentBaseClass.getInstance().getCurrentBaseObject().getName();
//        }
//        catch (Exception e) {
//            System.out.println("Application is dead");
//            return;
//        }
//
//        if (mLastLocation.getLatitude() == 0 && mLastLocation.getLongitude() == 0) return;
//        baza = CurrentBaseClass.getInstance().getCurrentBaseObject();
//        String mHost = baza.getHost();
//        int mPort = baza.getPort();
//        try {
//            KalmanLocation.GrpcTask grpcTask = new KalmanLocation.GrpcTask(ManagedChannelBuilder.forAddress(mHost, mPort)
//                    .usePlaintext(true).build(), CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
//
//            grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private class GrpcTask extends AsyncTask<Void, Void, Points> {
//        private ManagedChannel mChannel;
//        private String name;
//
//        private GrpcTask(ManagedChannel mChannel, String name) {
//            this.mChannel = mChannel;
//            this.name = name;
//        }
//        /**
//         * Метод срабатывает перед началом работы AsyncTask
//         */
//        @Override
//        protected void onPreExecute() {
//        }
//
//        private Points sendLocation(ManagedChannel mChannel) {
//            arrayList = new ArrayList<>();
//            AyuServiceGrpc.AyuServiceBlockingStub blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
//            kg.soulsb.ayu.grpctest.nano.Location request = new kg.soulsb.ayu.grpctest.nano.Location();
//
//            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//            String formattedDate = df.format(mLastLocation.getTime());
//
//
//            Agent agent = new Agent();
//            agent.name = name;
//            request.agent = agent;
//            request.date = formattedDate;
//            request.latitude = mLastLocation.getLatitude();
//            request.longitude = mLastLocation.getLongitude();
//            request.speed = mLastLocation.getSpeed();
//            request.deviceId = android_id;
//            request.accuracy = mLastLocation.getAccuracy();
//            DBHelper dbHelper = new DBHelper(m_context);
//            DatabaseManager.initializeInstance(dbHelper);
//
//            MyLocationsRepo myLocationsRepo = new MyLocationsRepo();
//
//            OperationStatus bl = blockingStub.sendLocation(request);
//
//            if (bl.status == 1) {
//                System.out.println("insertion occured");
//                myLocationsRepo.insert(new MyLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), name, formattedDate, mLastLocation.getSpeed(), mLastLocation.getAccuracy(), android_id));
//            }
//            System.out.println(bl.status+" <- ANSWER");
//            if (!isSending) {
//                isSending = true;
//                arrayList = myLocationsRepo.getMyLocationsObject();
//                System.out.println(arrayList);
//                for (MyLocation list : arrayList) {
//                    agent = new Agent();
//                    agent.name = list.getAgent();
//                    request.agent = agent;
//                    request.date = list.getFormattedDate();
//                    request.latitude = list.getLatitude();
//                    request.longitude = list.getLongitude();
//                    request.speed = list.getSpeed()*3600/1000;
//                    request.deviceId = list.getDeviceID();
//                    request.accuracy = list.getAccuracy();
//                    myLocationsRepo = new MyLocationsRepo();
//                    bl = blockingStub.sendLocation(request);
//                    System.out.println(bl.status+" <- ANSWER INSIDE");
//                    if (bl.status == 0) {
//                        myLocationsRepo.delete(list);
//                        System.out.println("Location DONE");
//                    }
//                }
//                isSending = false;
//            }
//
//
//            return null;
//        }
//
//        /**
//         * Метод отрабатывает код в фоновом режиме.
//         *
//         * @param nothing
//         * @return
//         */
//        @Override
//        protected Points doInBackground(Void... nothing) {
//            try {
//                System.out.println("DoInBackround started");
//                return sendLocation(mChannel);
//            } catch (Exception e) {
//                e.printStackTrace();
//
//                saveLocationInDatabase(mLastLocation, name);
//                return
//                        // Возвращает пустой итератор
//                        null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Points pointIterator) {
//            try {
//                mChannel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//
//}
