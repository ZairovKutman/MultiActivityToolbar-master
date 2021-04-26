package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.ChooseClientTableActivity;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.TasksAdapter;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.ConsPaymentLine;
import kg.soulsb.ayu.grpctest.nano.DailyTasks;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
import kg.soulsb.ayu.grpctest.nano.Docs;
import kg.soulsb.ayu.grpctest.nano.DocsStatus;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.grpctest.nano.PurchDocLine;
import kg.soulsb.ayu.grpctest.nano.TaskPhoto;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.DailyTasksRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.PhotosRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.DailyPhoto;
import kg.soulsb.ayu.models.DailyTask;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.SvodPay;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import kg.soulsb.ayu.singletons.UserSettings;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class TasksActivity extends BaseActivity {

    ListView expListView;
    TasksAdapter listAdapter;
    ArrayList<DailyTask> orderTasksArraylist = new ArrayList<>();
    Button sendTasks;
    ArrayList<Order> arrayList = new ArrayList<>();
    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;
    String errorMessage="";
    String mHost;
    int mPort;
    Baza baza1 = null;
    String currentBaseString;
    AlertDialog.Builder d;
    ProgressBar progressBar;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // get the listview
        expListView = (ListView) findViewById(R.id.tasks_listview);
        sendTasks = findViewById(R.id.button_send_tasks);
        sendTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTasks.setEnabled(false);

                ArrayList<Baza> arrayList1 = new BazasRepo().getBazasObject();
                currentBaseString = sharedPreferences.getString("default_name", null);
                for (Baza baza: arrayList1)
                {
                    if (baza.getName().equals(currentBaseString))
                    {
                        baza1 = baza;
                    }
                }

                mHost = baza1.getHost();
                mPort = baza1.getPort();

                d = new AlertDialog.Builder(TasksActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.loading_dialog, null);
                progressBar = (ProgressBar) dialogView.findViewById(R.id.loading_bar);

                d.setTitle("Выгрузка документа");
                d.setMessage("Подождите...");
                d.setView(dialogView);
                d.setCancelable(false);
                alertDialog = d.create();
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Готово", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

                final TasksActivity.GrpcTask grpcTask = new TasksActivity.GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
                        .usePlaintext(true).build(),CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable()
                {
                    @Override
                    public void run() {
                        if ( grpcTask.getStatus() == AsyncTask.Status.RUNNING )
                        {
                            grpcTask.cancel(true);
                            Toast.makeText(TasksActivity.this, "Таймаут, сервер не отвечает, попробуйте еще раз", Toast.LENGTH_LONG).show();
                            arrayList.clear();
                            arrayList.addAll(new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase()));
                            sendTasks.setEnabled(true);
                        }
                    }
                }, 150000 );

            }
        });






        orderTasksArraylist = new DailyTasksRepo().getDailyTasksObject();
        listAdapter = new TasksAdapter(TasksActivity.this, R.layout.tasks_list, orderTasksArraylist);
        expListView.setAdapter(listAdapter);

        expListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                {
                    if (orderTasksArraylist.get(position-1).getStatus().equals("0")) {
                        int counter = 1;
                        int priorityNow = orderTasksArraylist.get(position).getPriority();
                        int previousPriority = priorityNow;
                        boolean foundPreviousPriority = false;
                        while (position-counter>0) {

                            if (orderTasksArraylist.get(position - counter).getPriority() < priorityNow && !foundPreviousPriority) {
                                foundPreviousPriority = true;
                                previousPriority = orderTasksArraylist.get(position - counter).getPriority();

                            }
                            else {
                                if (!foundPreviousPriority) {
                                    if (orderTasksArraylist.get(position - counter).getPriority() == priorityNow && !orderTasksArraylist.get(position - counter).getStatus().equals("0"))
                                        break;

                                    if (orderTasksArraylist.get(position - counter).getPriority() == priorityNow && orderTasksArraylist.get(position - counter).getStatus().equals("0")) {
                                        counter = counter + 1;
                                        continue;
                                    }
                                }

                            }

                            if (foundPreviousPriority)
                            {
                                if (orderTasksArraylist.get(position - counter).getPriority() == previousPriority && !orderTasksArraylist.get(position - counter).getStatus().equals("0"))
                                {
                                    break;
                                }

                                if (orderTasksArraylist.get(position - counter).getPriority() == previousPriority && orderTasksArraylist.get(position - counter).getStatus().equals("0"))
                                {
                                    if (position - counter - 1 == 0 && orderTasksArraylist.get(position - counter - 1).getStatus().equals("0"))
                                    {
                                        Toast.makeText(TasksActivity.this, "Нужно закончить предыдущее задание!", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    counter = counter + 1;
                                    continue;
                                }

                                if (orderTasksArraylist.get(position - counter).getPriority()<previousPriority)
                                {
                                    Toast.makeText(TasksActivity.this, "Нужно закончить предыдущее задание!", Toast.LENGTH_LONG).show();
                                    return;
                                }


                            }
                        }
                    }
                }
                Location clientLocation = orderTasksArraylist.get(position).getClientLocation();
                Location myLocation = CurrentLocationClass.getInstance().getCurrentLocation();

                float distance = myLocation.distanceTo(clientLocation);

                if (distance> UserSettings.DISTANCE_TO_CLIENT) {
                    AlertDialog.Builder alertDlg = new AlertDialog.Builder(TasksActivity.this);
                    alertDlg.setMessage("Клиент находится на расстоянии " + distance + "м. Подойдите ближе!");
                    alertDlg.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            return;
                        }
                    });
                    alertDlg.setCancelable(false);
                    alertDlg.show();
                    return;
                }


                    intent = new Intent(TasksActivity.this, TasksDetailActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("clientGuid", orderTasksArraylist.get(position).getClientGuid());
                    intent.putExtra("clientName", orderTasksArraylist.get(position).getClientName());
                    intent.putExtra("clientLat", orderTasksArraylist.get(position).getLatitude());
                    intent.putExtra("clientLon", orderTasksArraylist.get(position).getLongitude());
                    intent.putExtra("priority", orderTasksArraylist.get(position).getPriority());
                    intent.putExtra("status", orderTasksArraylist.get(position).getStatus());
                    intent.putExtra("docGuid", orderTasksArraylist.get(position).getDocGuid());
                    intent.putExtra("rate", orderTasksArraylist.get(position).getRate());
                    intent.putExtra("rateDate", orderTasksArraylist.get(position).getRateDate());
                    intent.putExtra("rateDetails", orderTasksArraylist.get(position).getRateComment());
                    startActivity(intent);


//                if (orderTasksArraylist.get(position).getDocId().equals("")) {
//                    intent = new Intent(TasksActivity.this, OrderAddActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("doctype", "0");
//                    intent.putExtra("isTask", "true");
//                    intent.putExtra("clientGuid", orderTasksArraylist.get(position).getClientGuid());
//                    intent.putExtra("clientName", orderTasksArraylist.get(position).getClientName());
//                    intent.putExtra("clientLat", orderTasksArraylist.get(position).getLatitude());
//                    intent.putExtra("clientLon", orderTasksArraylist.get(position).getLongitude());
//                    intent.putExtra("priority", orderTasksArraylist.get(position).getPriority());
//
//                    startActivity(intent);
//                }
//                else {
//                    ArrayList<Order> orders = new OrdersRepo().getOrdersObject(CurrentBaseClass.getInstance().getCurrentBase());
//                    Order order = new Order();
//                    for (Order order1: orders)
//                    {
//                        if (order1.getOrderID().equals(orderTasksArraylist.get(position).getDocId()))
//                        {
//                            order = order1;
//                            break;
//                        }
//                    }
//
//                    Intent intent = new Intent(getBaseContext(), OrderAddActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.putExtra("doctype", order.getDoctype());
//                    intent.putExtra("savedobj", order);
//
//                    intent.putExtra("isTask", "true");
//                    intent.putExtra("clientGuid", orderTasksArraylist.get(position).getClientGuid());
//                    intent.putExtra("clientName",orderTasksArraylist.get(position).getClientName());
//                    intent.putExtra("clientLat",orderTasksArraylist.get(position).getLatitude());
//                    intent.putExtra("clientLon",orderTasksArraylist.get(position).getLongitude());
//                    intent.putExtra("priority",orderTasksArraylist.get(position).getPriority());
//
//                    startActivity(intent);
//                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderTasksArraylist.clear();
        orderTasksArraylist.addAll(new DailyTasksRepo().getDailyTasksObject());
        listAdapter.notifyDataSetChanged();
    }




    private class GrpcTask extends AsyncTask<String, String, Points> {
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
            Toast.makeText(TasksActivity.this,"Выгружаю...", Toast.LENGTH_LONG).show();
        }

        private Points sendTasksDocuments(ManagedChannel mChannel) {
            blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
            Agent request = new Agent();
            request.name = name;
            errorMessage = "Успех! ";
            arrayList = new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase());

            //Выгружаю документы
            DocPurch docPurchArray[] = new DocPurch[arrayList.size()];
            for (Order order : arrayList) {
                DocPurch docPurch = new DocPurch();
                docPurch.organizationGuid = order.getOrganization();
                docPurch.agent = name;
                docPurch.clientGuid = order.getClient();
                docPurch.comment = order.getComment();
                docPurch.bonusTT = order.getCheckedBonusTT();
                docPurch.deliveryDate = order.getDateSend();
                docPurch.contractGuid = order.getDogovor();
                docPurch.date = order.getDate();
                docPurch.warehouseGuid = order.getWarehouse();
                docPurch.priceTypeGuid = order.getPriceType();
                docPurch.docType = Integer.parseInt(order.getDoctype());
                docPurch.docId = order.getOrderID();
                docPurch.amount = order.getTotalSum();
                ArrayList<Item> itemsList = order.getArraylistTovar();

                ConsPaymentLine[] arrayPayments = new ConsPaymentLine[order.getArraylistSvodPay().size()];
                int i =0;

                for (SvodPay svodPay: order.getArraylistSvodPay()) {

                    ConsPaymentLine consPaymentLine = new ConsPaymentLine();
                    consPaymentLine.clientGuid = svodPay.getClient();
                    consPaymentLine.contractGuid = svodPay.getDogovor();
                    consPaymentLine.amount = svodPay.getSum();
                    arrayPayments[i] = consPaymentLine;
                    i = i +1;
                }

                docPurch.payments = arrayPayments;

                PurchDocLine[] purchDocLines = new PurchDocLine[itemsList.size()];
                int counter = 0;
                for (Item item : itemsList) {
                    PurchDocLine line = new PurchDocLine();
                    line.amount = item.getQuantity() * item.getPrice() * item.getMyUnit().getCoefficient();
                    line.itemGuid = item.getGuid();
                    line.price = item.getPrice() * item.getMyUnit().getCoefficient();
                    line.quantity = item.getQuantity();
                    line.unit = item.getMyUnit().getUnitGuid();

                    purchDocLines[counter] = line;
                    counter++;
                }

                docPurch.lines = purchDocLines;

                docPurchArray[arrayList.indexOf(order)] = docPurch;
            }
            if (arrayList.size() > 0) {
                Docs docs = new Docs();
                docs.doc = docPurchArray;
                DocsStatus docsStatus = blockingStub.getDocuments(docs);
                errorMessage = "";
                for (int i = 0; i < docsStatus.docsStatus.length; i++) {
                    if (docsStatus.docsStatus[i].operationStatus.status == 0) {
                        new OrdersRepo().setDocDelivered(docsStatus.docsStatus[i].docId, true);
                    }
                    else
                    {
                        errorMessage=errorMessage+"\n"+ docsStatus.docsStatus[i].operationStatus.comment;
                    }
                }
            } //Конец выгрузки документов

            ArrayList<DailyTask> dailyTaskArrayList = new DailyTasksRepo().getDailyTasksObject();
            kg.soulsb.ayu.grpctest.nano.DailyTask[] dailyTasks = new kg.soulsb.ayu.grpctest.nano.DailyTask[dailyTaskArrayList.size()];
            int i=0;
            String android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            String docGuid = "";
            for (DailyTask dt: dailyTaskArrayList)
            {
                docGuid = dt.getDocGuid();
                kg.soulsb.ayu.grpctest.nano.DailyTask dt2 = new kg.soulsb.ayu.grpctest.nano.DailyTask();
                dt2.agentName = name;
                dt2.deviceId = android_id;
                dt2.clientGuid = dt.getClientGuid();
                dt2.dateClosed = dt.getDateClosed();
                dt2.docDate = dt.getDocDate();
                dt2.docGuid = dt.getDocGuid();
                dt2.docId =dt.getDocId();
                dt2.latitude = Double.parseDouble(dt.getLatitude());
                dt2.longitude = Double.parseDouble(dt.getLongitude());
                dt2.priority = dt.getPriority();
                dt2.status = Integer.parseInt(dt.getStatus());

                dailyTasks[i] = dt2;
                i=i+1;
            }
            DailyTasks dailyTasks1 = new DailyTasks();
            dailyTasks1.task = dailyTasks;

            DocsStatus ds = blockingStub.updateDailyTasks(dailyTasks1);
            System.out.println(Arrays.toString(ds.docsStatus));

//            ArrayList<DailyPhoto> dailyPhotoArrayList = new PhotosRepo().getPhotosByDocGuid(docGuid);
//            int j = 0;
//            for (DailyPhoto dailyPhoto: dailyPhotoArrayList)
//            {
//                j= j+1;
//                publishProgress("Выгрузка фотографий: " + j + " из "+dailyPhotoArrayList.size());
//                TaskPhoto tp = new TaskPhoto();
//                tp.photo = dailyPhoto.getPhotoBytes();
//                Agent agent = new Agent();
//                agent.name = dailyPhoto.getAgent();
//                tp.agent = agent;
//                tp.clientGuid = dailyPhoto.getClientGuid();
//                tp.dateClosed = dailyPhoto.getDateClosed();
//                tp.deviceId = dailyPhoto.getDevice_id();
//                tp.docGuid = dailyPhoto.getDocGuid();
//                tp.latitude = dailyPhoto.getLatitude();
//                tp.longitude = dailyPhoto.getLongitude();
//
//                OperationStatus photoStatus = blockingStub.getTaskPhoto(tp);
//                System.out.println("photo send status: "+photoStatus.status+", "+photoStatus.comment);
//                if (photoStatus.status==0)
//                {
//                    new PhotosRepo().deleteDailyPhoto(tp.photo);
//                }
//                else {
//                    errorMessage=errorMessage+"\n"+ photoStatus.comment;
//                }
//            }

            return new Points();

        }

        /**
         * Метод отрабатывает код в фоновом режиме.
         *
         * @param nothing
         * @return
         */
        @Override
        protected Points doInBackground(String... nothing) {
            try {
                return sendTasksDocuments(mChannel);
            } catch (Exception e) {
                e.printStackTrace();

                errorMessage = e.getMessage();
                // Возвращает пустой итератор
                return null;
            }
        }

        @Override
        protected void onPostExecute(Points pointIterator) {

            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            alertDialog.setTitle("Выгрузка заданий");
            if (errorMessage.equals("")){
            alertDialog.setMessage("Выгрузка завершена");
            }
            else
            {
                alertDialog.setMessage(errorMessage);
            }


            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            sendTasks.setEnabled(true);

        }
    }
}
