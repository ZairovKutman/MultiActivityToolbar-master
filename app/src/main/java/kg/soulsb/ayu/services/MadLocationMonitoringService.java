//package kg.soulsb.ayu.services;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import kg.soulsb.ayu.R;
//
//
//public class MadLocationMonitoringService extends Service {
//    private KalmanLocation kalmanLocation;
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        kalmanLocation.stop_k();
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//
//        Notification.Builder builder;
//        if (Build.VERSION.SDK_INT >= 26) {
//            createNotificationChannel();
//            builder = new Notification.Builder(this, "Аю-Агент GPS");
//        } else {
//            builder = new Notification.Builder(this);
//        }
//
//        Notification notification = builder
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setTicker("АЮ-Агент")
//                .setWhen(System.currentTimeMillis())
//                .setContentTitle("АЮ-Агент")
//                .setContentText("GPS сервис работает")
//                .build();
//        startForeground(785826, notification);
//        kalmanLocation = new KalmanLocation( getApplicationContext(), this );
//        kalmanLocation.start_k();
//    }
//
//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= 26) {
//            NotificationManager mNotificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            String id = "Аю-Агент GPS";
//            CharSequence name = "АЮ-Агент";
//            String description = "GPS сервис работает";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//            mChannel.setDescription(description);
//            mChannel.enableLights(false);
//            mChannel.enableVibration(false);
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//    }
//}