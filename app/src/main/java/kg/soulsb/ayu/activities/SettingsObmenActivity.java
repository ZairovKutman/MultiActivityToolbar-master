package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Tasks;

import kg.soulsb.ayu.BuildConfig;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.ClientAdapter;
import kg.soulsb.ayu.adapters.OrderAdapter;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.ConsPaymentLine;
import kg.soulsb.ayu.grpctest.nano.Contract;
import kg.soulsb.ayu.grpctest.nano.Contracts;
import kg.soulsb.ayu.grpctest.nano.DailyTask;
import kg.soulsb.ayu.grpctest.nano.DailyTasks;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
import kg.soulsb.ayu.grpctest.nano.DocStatus;
import kg.soulsb.ayu.grpctest.nano.Docs;
import kg.soulsb.ayu.grpctest.nano.DocsStatus;
import kg.soulsb.ayu.grpctest.nano.ExchangeData;
import kg.soulsb.ayu.grpctest.nano.Items;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Organization;
import kg.soulsb.ayu.grpctest.nano.Organizations;
import kg.soulsb.ayu.grpctest.nano.Point;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.grpctest.nano.PriceType;
import kg.soulsb.ayu.grpctest.nano.PriceTypes;
import kg.soulsb.ayu.grpctest.nano.Prices;
import kg.soulsb.ayu.grpctest.nano.PurchDocLine;
import kg.soulsb.ayu.grpctest.nano.Report;
import kg.soulsb.ayu.grpctest.nano.Reports;
import kg.soulsb.ayu.grpctest.nano.SalesHistories;
import kg.soulsb.ayu.grpctest.nano.SalesHistory;
import kg.soulsb.ayu.grpctest.nano.Settings;
import kg.soulsb.ayu.grpctest.nano.Stock;
import kg.soulsb.ayu.grpctest.nano.Stocks;
import kg.soulsb.ayu.grpctest.nano.TaskPhoto;
import kg.soulsb.ayu.grpctest.nano.Units;
import kg.soulsb.ayu.grpctest.nano.Warehouse;
import kg.soulsb.ayu.grpctest.nano.Warehouses;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.DailyTasksRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.MyLocationsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PhotosRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.ReportsRepo;
import kg.soulsb.ayu.helpers.repo.SalesHistoryRepo;
import kg.soulsb.ayu.helpers.repo.SavedReportsRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.helpers.repo.UnitsRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.DailyPhoto;
import kg.soulsb.ayu.models.Item;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.grpctest.nano.Price;
import kg.soulsb.ayu.models.MyLocation;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.SvodPay;
import kg.soulsb.ayu.models.SvodPayRow;
import kg.soulsb.ayu.models.Unit;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.DataHolderClass;
import kg.soulsb.ayu.singletons.UserSettings;

import org.json.JSONException;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class SettingsObmenActivity extends BaseActivity {
    TextView textView;
    String mHost;
    int mPort;
    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;
    ProgressBar progressBar;
    TextView loadingComment;
    TextView lastObmenText;
    TextView textViewEmpty;
    Button loadButton,loadOnlyDocButton,loadStockButton;
    int globalCounter = 0;
    Baza baza = null;
    String currentBaseString;
    ListView listViewDocuments;
    ArrayAdapter<Order> arrayAdapter;
    ArrayList<Order> arrayList = new ArrayList<>();
    boolean fullObmen = false;
    boolean onlyOstatki = false;
    boolean onlyDocs = false;
    boolean sendPhotos = false;
    String errorMessage="";


    public void fillDocumentsTable() {
        arrayList = new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, arrayList);
        listViewDocuments.setAdapter(arrayAdapter);
        listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (arrayList.get(i).getDoctype().equals("3")) {
                    intent = new Intent(getApplicationContext(), PaySvodActivity.class);
                }
                else if (arrayList.get(i).getDoctype().equals("2")) {
                    intent = new Intent(getApplicationContext(), PayActivity.class);
                }
                else
                {
                    intent = new Intent(getApplicationContext(), OrderAddActivity.class);
                }

                intent.putExtra("doctype",arrayList.get(i).getDoctype());
                intent.putExtra("savedobj", arrayList.get(i));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);}
        });
        arrayAdapter.notifyDataSetChanged();

    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_obmen);
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        DatabaseManager.initializeInstance(dbHelper);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        loadingComment = (TextView) findViewById(R.id.loading_comment);
        lastObmenText = (TextView) findViewById(R.id.last_obmen_text);
        loadButton = (Button) findViewById(R.id.button);
        loadStockButton = (Button) findViewById(R.id.button_update_stocks);
        loadOnlyDocButton = (Button) findViewById(R.id.button_only_documents);
        listViewDocuments = (ListView) findViewById(R.id.listView_documents);
        textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
        lastObmenText.setText("Добавьте базу данных");

        if (arrayList.isEmpty())
        {
            textViewEmpty.setVisibility(View.VISIBLE);
        }
        else{
            textViewEmpty.setVisibility(View.INVISIBLE);
        }
        if (sharedPreferences.contains("default_name")) {
            currentBaseString = sharedPreferences.getString("default_name", null);
            lastObmenText.setText("Последний обмен: " + sharedPreferences.getString("LAST_OBMEN","никогда"));
        }

        ArrayList<Baza> arrayList = new BazasRepo().getBazasObject();

        for (Baza baza: arrayList)
        {
            if (baza.getName().equals(currentBaseString))
            {
                this.baza = baza;
            }
        }

        textView = (TextView) findViewById(R.id.usingBase);
        if (baza!=null)
            textView.setText("Используемая база "+ BuildConfig.VERSION_NAME.toString()+": "+baza.getName());
        else
            textView.setText("Используемая база "+ BuildConfig.VERSION_NAME.toString()+": "+" Создайте базу данных");

        // BUTTON STOCKS
        loadStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baza!= null) {
                    loadingComment.setText("Подключение... "+"5%");
                    try {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        fullObmen = false;
                        onlyOstatki = true;
                        onlyDocs = false;
                        loadButton.setEnabled(false);
                        loadOnlyDocButton.setEnabled(false);
                        loadStockButton.setEnabled(false);
                        doFullObmen();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }}
                else{
                    Toast.makeText(getBaseContext(),"Создайте базу данных",Toast.LENGTH_SHORT).show();}
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder d1 = new AlertDialog.Builder(SettingsObmenActivity.this);
                LayoutInflater inflater1 = getLayoutInflater();
                final View dialogView1 = inflater1.inflate(R.layout.are_you_sure_dialog, null);
                d1.setTitle("Подтвердите");
                d1.setMessage("Вы уверены что хотите сделать Полный Обмен? Все заказы будут отправлены на сервер и удалены с телефона.");
                d1.setView(dialogView1);

                d1.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (baza!= null) {
                            loadingComment.setText("Подключение... "+"5%");
                            try {
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                fullObmen = true;
                                sendPhotos = true;
                                onlyOstatki = false;
                                onlyDocs = false;
                                loadButton.setEnabled(false);
                                loadOnlyDocButton.setEnabled(false);
                                loadStockButton.setEnabled(false);
                                doFullObmen();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }}
                        else{
                            Toast.makeText(getBaseContext(),"Создайте базу данных",Toast.LENGTH_SHORT).show();}


                    }
                });

                d1.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog1 = d1.create();
                alertDialog1.show();
            }
        });

        loadOnlyDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baza!= null) {
                    loadingComment.setText("Подключение... "+"5%");
                    try {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        fullObmen = false;
                        onlyOstatki = true;
                        onlyDocs = true;
                        sendPhotos = false;
                        loadButton.setEnabled(false);
                        loadOnlyDocButton.setEnabled(false);
                        loadStockButton.setEnabled(false);
                        doFullObmen();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }}
                else{
                    Toast.makeText(getBaseContext(),"Создайте базу данных",Toast.LENGTH_SHORT).show();}
            }
        });

        textView = (TextView) findViewById(R.id.usingBase);

        fillDocumentsTable();
    }
    /**
     * Метод делает полный обмен с сервером и загружает в базу
     */
    public void doFullObmen() throws JSONException {
        progressBar.setVisibility(View.VISIBLE);

        mHost = baza.getHost();
        mPort = baza.getPort();

        final SettingsObmenActivity.GrpcTask grpcTask = new SettingsObmenActivity.GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
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
                    loadingComment.setText("Таймаут, сервер не отвечает, попробуйте еще раз");
                    progressBar.setVisibility(View.INVISIBLE);
                    arrayList.clear();
                    arrayList.addAll(new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase()));
                    arrayAdapter.notifyDataSetChanged();
                    loadButton.setEnabled(true);
                    loadOnlyDocButton.setEnabled(true);
                }
            }
        }, 400000 );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
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
            loadingComment.setText("Идет загрузка...");
        }

        private Points getClients(ManagedChannel mChannel) {
            blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
            Agent request = new Agent();
            request.name = name;
            String android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Device device = new Device();
            device.agent = name;
            device.deviceId = android_id;
            device.modelDescription = Build.MANUFACTURER + " " + Build.MODEL;
            publishProgress("Соединение с сервером и запрос доступа...");
            DeviceStatus deviceStatus = blockingStub.checkDeviceStatus(device);

            if (!deviceStatus.active) {
                publishProgress("Ошибка, доступ с этого телефона запрещен.");
                return null;
            }

            if (onlyDocs || fullObmen) {
                //
                publishProgress("Выгрузка документов...");

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
                    int i = 0;

                    for (SvodPay svodPay : order.getArraylistSvodPay()) {

                        ConsPaymentLine consPaymentLine = new ConsPaymentLine();
                        consPaymentLine.clientGuid = svodPay.getClient();
                        consPaymentLine.contractGuid = svodPay.getDogovor();
                        consPaymentLine.amount = svodPay.getSum();
                        arrayPayments[i] = consPaymentLine;
                        i = i + 1;
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

                    for (int i = 0; i < docsStatus.docsStatus.length; i++) {
                        if (docsStatus.docsStatus[i].operationStatus.status == 0) {
                            new OrdersRepo().setDocDelivered(docsStatus.docsStatus[i].docId, true);
                        } else {
                            errorMessage = errorMessage + "\n" + docsStatus.docsStatus[i].operationStatus.comment;
                        }
                    }
                }
                //Конец выгрузки документов

                if (sendPhotos) {
                    ArrayList<DailyPhoto> dailyPhotoArrayList = new PhotosRepo().getPhotos();
                    int j = 0;
                    for (DailyPhoto dailyPhoto : dailyPhotoArrayList) {
                        j = j + 1;
                        publishProgress("Выгрузка фотографий: " + j + " из " + dailyPhotoArrayList.size());
                        TaskPhoto tp = new TaskPhoto();
                        tp.photo = dailyPhoto.getPhotoBytes();
                        Agent agent = new Agent();
                        agent.name = dailyPhoto.getAgent();
                        tp.agent = agent;
                        tp.clientGuid = dailyPhoto.getClientGuid();
                        tp.dateClosed = dailyPhoto.getDateClosed();
                        tp.deviceId = dailyPhoto.getDevice_id();
                        tp.docGuid = dailyPhoto.getDocGuid();
                        tp.latitude = dailyPhoto.getLatitude();
                        tp.longitude = dailyPhoto.getLongitude();

                        OperationStatus photoStatus = blockingStub.getTaskPhoto(tp);
                        System.out.println("photo send status: " + photoStatus.status + ", " + photoStatus.comment);
                        if (photoStatus.status == 0) {
                            new PhotosRepo().deleteDailyPhoto(tp.photo);
                        } else {
                            errorMessage = errorMessage + "\n" + photoStatus.comment;
                        }
                    }
                }

                publishProgress("Выгрузка заданий...");
                ArrayList<kg.soulsb.ayu.models.DailyTask> dailyTaskArrayList = new DailyTasksRepo().getDailyTasksObject();
                kg.soulsb.ayu.grpctest.nano.DailyTask[] dailyTasks = new kg.soulsb.ayu.grpctest.nano.DailyTask[dailyTaskArrayList.size()];
                int i = 0;
                String docGuid = "";
                for (kg.soulsb.ayu.models.DailyTask dt : dailyTaskArrayList) {
                    docGuid = dt.getDocGuid();
                    kg.soulsb.ayu.grpctest.nano.DailyTask dt2 = new kg.soulsb.ayu.grpctest.nano.DailyTask();
                    dt2.agentName = name;
                    dt2.deviceId = android_id;
                    dt2.clientGuid = dt.getClientGuid();
                    dt2.dateClosed = dt.getDateClosed();
                    dt2.docDate = dt.getDocDate();
                    dt2.docGuid = dt.getDocGuid();
                    dt2.docId = dt.getDocId();
                    dt2.latitude = Double.parseDouble(dt.getLatitude());
                    dt2.longitude = Double.parseDouble(dt.getLongitude());
                    dt2.priority = dt.getPriority();
                    dt2.status = Integer.parseInt(dt.getStatus());

                    dailyTasks[i] = dt2;
                    i = i + 1;
                }
                DailyTasks dailyTasks1 = new DailyTasks();
                dailyTasks1.task = dailyTasks;
                DocsStatus ds = blockingStub.updateDailyTasks(dailyTasks1);
                System.out.println(Arrays.toString(ds.docsStatus));

                if (!errorMessage.equals("")) {
                    return null;
                }
            }


            if (onlyOstatki) {
                ArrayList<kg.soulsb.ayu.models.Stock> stocksArray = new ArrayList<>();
                publishProgress("Загрузка остатков...");
                // getting Stocks
                Stocks stocks = blockingStub.getStock(request);
                StocksRepo stocksRepo = new StocksRepo();
                stocksRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
                for (Stock stock : stocks.stock) {
                    System.out.println(stock.stock);
                    kg.soulsb.ayu.models.Stock stock1 = new kg.soulsb.ayu.models.Stock(stock.item, stock.warehouse, stock.stock);
                    stock1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                    stocksArray.add(stock1);
                    stocksRepo.insert(stock1);
                }
                System.out.println("Stock: Done");
                return new Points();
            }

            if (!fullObmen) {
                return new Points();
            }

            new OrdersRepo().deleteDocDelivered();


            publishProgress("Выгрузка GPS координат");
            kg.soulsb.ayu.grpctest.nano.Location requestLoc = new kg.soulsb.ayu.grpctest.nano.Location();

            MyLocationsRepo myLocationsRepo = new MyLocationsRepo();

            ArrayList<MyLocation> arrayListLoc = myLocationsRepo.getMyLocationsObject();

            for (MyLocation list : arrayListLoc) {
                requestLoc.agent = request;
                requestLoc.date = list.getFormattedDate();
                requestLoc.latitude = list.getLatitude();
                requestLoc.longitude = list.getLongitude();
                requestLoc.speed = list.getSpeed() * 3600 / 1000;
                requestLoc.deviceId = list.getDeviceID();
                requestLoc.accuracy = list.getAccuracy();
                myLocationsRepo = new MyLocationsRepo();
                OperationStatus bl = blockingStub.sendLocation(requestLoc);

                System.out.println(bl.status + " <- ANSWER INSIDE");
                if (bl.status == 0) {
                    myLocationsRepo.delete(list);
                    System.out.println("Location DONE");
                }
            }

            publishProgress("Загружаю данные из сервера...");

            ExchangeData exchangeData = blockingStub.getAllData(request);

            publishProgress("Загрузка настроек...");
            Settings settings = exchangeData.settings;

            if (!settings.status) {
                publishProgress("Ошибка: " + settings.errorMessage);
            }
            // Получаю настройки
            SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(UserSettings.can_create_orders, Boolean.toString(settings.canCreateOrders));
            editor.putString(UserSettings.can_create_sales, Boolean.toString(settings.canCreateSales));
            editor.putString(UserSettings.send_all_documents_with_exchange, Boolean.toString(settings.sendAllDocumentsWithExchange));
            editor.putString(UserSettings.force_daily_exchange, Boolean.toString(settings.forceDailyExchange));
            editor.putString(UserSettings.force_gps_turn_on, Boolean.toString(settings.forceGpsTurnOn));
            editor.putString(UserSettings.forbit_select_client_with_outstanding_debt, Boolean.toString(settings.forbitSelectClientWithOutstandingDebt));
            editor.putString(UserSettings.can_get_gpc_coordinates_of_clients, Boolean.toString(settings.canGetGpcCoordinatesOfClients));
            editor.putString(UserSettings.create_order_at_clients_coordinates, Boolean.toString(settings.createOrderAtClientsCoordinates));
            editor.putString(UserSettings.create_sales_at_clients_coordinates, Boolean.toString(settings.createSalesAtClientsCoordinates));
            editor.putString(UserSettings.password_for_app_settings, settings.passwordForAppSettings);
            editor.putString(UserSettings.can_create_payment, Boolean.toString(settings.canCreatePayment));
            editor.putString(UserSettings.status, Boolean.toString(settings.status));
            editor.putString(UserSettings.workWithTasks, Boolean.toString(settings.workWithTasks));
            editor.putString(UserSettings.can_rate_point,Boolean.toString(settings.canRatePoint) );
            editor.apply();

            publishProgress("Загрузка клиентов...");
            Points pointIterator = exchangeData.points;
            globalCounter = 10;

            ArrayList<Client> clientsArray = new ArrayList<>();
            ArrayList<Item> itemsArray = new ArrayList<>();
            ArrayList<Unit> unitsArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Warehouse> warehousesArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Contract> contractsArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.PriceType> pricetypesArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Price> pricesArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Stock> stocksArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Report> reportsArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Organization> organizationsArray = new ArrayList<>();
            DBHelper dbHelper = new DBHelper(getApplicationContext());
            DatabaseManager.initializeInstance(dbHelper);

            // getting clients
            ClientsRepo clientsRepo = new ClientsRepo();
            clientsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Point point : pointIterator.point) {
                Client client = new Client(point.guid, point.description, point.address, point.phoneNumber, point.latitude, point.longitude, point.debt);
                client.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                clientsArray.add(client);
                clientsRepo.insert(client);
            }
            System.out.println("Clients: Done");

            publishProgress("Загрузка товаров...");
            // getting tovar

            Items items = exchangeData.items;
            ItemsRepo itemsRepo = new ItemsRepo();
            itemsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (kg.soulsb.ayu.grpctest.nano.Item item : items.item) {
                Item item1 = new Item(item.guid, item.description, item.unit, item.price, item.stock, item.category);
                item1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                itemsArray.add(item1);
                itemsRepo.insert(item1);
            }
            System.out.println("Items: Done");


            publishProgress("Загрузка единиц измерения...");
            // getting units

            Units units = exchangeData.units;
            UnitsRepo unitsRepo = new UnitsRepo();
            unitsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());

            for (kg.soulsb.ayu.grpctest.nano.Unit unit : units.unit) {
                Unit unit1 = new Unit(unit.guid, unit.item, unit.coefficient, unit.description, unit.default_);
                unit1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                unitsArray.add(unit1);
                unitsRepo.insert(unit1);
            }
            System.out.println("Units: Done");

            publishProgress("Загрузка типов цен...");
            // getting price types
            PriceTypes priceTypes = exchangeData.priceTypes;
            PriceTypesRepo priceTypesRepo = new PriceTypesRepo();
            priceTypesRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (PriceType priceType : priceTypes.priceType) {
                kg.soulsb.ayu.models.PriceType priceType1 = new kg.soulsb.ayu.models.PriceType(priceType.guid, priceType.description);
                priceType1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                pricetypesArray.add(priceType1);
                priceTypesRepo.insert(priceType1);
            }
            System.out.println("Price types: Done");
            publishProgress("Загрузка цен...");
            // getting prices
            Prices prices = exchangeData.prices;
            PricesRepo pricesRepo = new PricesRepo();
            pricesRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Price price : prices.price) {
                kg.soulsb.ayu.models.Price price1 = new kg.soulsb.ayu.models.Price(price.item, price.price, price.priceType);
                price1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                pricesArray.add(price1);
                pricesRepo.insert(price1);
            }
            System.out.println("Price: Done");

            publishProgress("Загрузка складов...");
            // getting warehouses
            Warehouses warehouses = exchangeData.warehouses;
            WarehousesRepo warehousesRepo = new WarehousesRepo();
            warehousesRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Warehouse warehouse : warehouses.warehouse) {
                kg.soulsb.ayu.models.Warehouse warehouse1 = new kg.soulsb.ayu.models.Warehouse(warehouse.guid, warehouse.description);
                warehouse1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                warehousesArray.add(warehouse1);
                warehousesRepo.insert(warehouse1);
            }
            System.out.println("Warehouses: Done");
            publishProgress("Загрузка договоров...");
            // getting contracts
            Contracts contracts = exchangeData.contracts;
            ContractsRepo contractsRepo = new ContractsRepo();
            contractsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Contract contract : contracts.contract) {
                kg.soulsb.ayu.models.Contract contract1 = new kg.soulsb.ayu.models.Contract(contract.guid, contract.description, contract.pointGuid, contract.itemCategory);
                contract1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                contractsArray.add(contract1);
                contractsRepo.insert(contract1);
            }
            System.out.println("Contracts: Done");

            publishProgress("Загрузка организации...");
            // getting Organization
            Organizations organizations = exchangeData.organizations;
            OrganizationsRepo organizationsRepo = new OrganizationsRepo();
            organizationsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Organization organization : organizations.organization) {
                kg.soulsb.ayu.models.Organization organization1 = new kg.soulsb.ayu.models.Organization(organization.guid, organization.description);
                organization1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                organizationsArray.add(organization1);
                organizationsRepo.insert(organization1);
            }
            System.out.println("Organization: Done");

            publishProgress("Загрузка остатков...");
            // getting Stocks
            Stocks stocks = exchangeData.stocks;
            StocksRepo stocksRepo = new StocksRepo();
            stocksRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Stock stock : stocks.stock) {
                kg.soulsb.ayu.models.Stock stock1 = new kg.soulsb.ayu.models.Stock(stock.item, stock.warehouse, stock.stock);
                stock1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                stocksArray.add(stock1);
                stocksRepo.insert(stock1);
            }
            System.out.println("Stock: Done");
            publishProgress("Загрузка отчетов...");
            //getting reports
            Reports reports = exchangeData.reports;
            ReportsRepo reportsRepo = new ReportsRepo();
            reportsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Report report : reports.report) {
                kg.soulsb.ayu.models.Report report1 = new kg.soulsb.ayu.models.Report(report.guid, report.description);
                report1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                reportsArray.add(report1);
                reportsRepo.insert(report1);
            }
            System.out.println("Reports: Done");


            // gettings tasks
            publishProgress("Загрузка заданий...");
            DailyTasks tasks = exchangeData.dailyTasks;
            new DailyTasksRepo().deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            System.out.println("DAILYTASKS: " + tasks.task);
            for (DailyTask task : tasks.task) {
                System.out.println("ONETASK: " + task.docGuid);
                kg.soulsb.ayu.models.DailyTask dailyTask1 = new kg.soulsb.ayu.models.DailyTask(task.docGuid, task.clientGuid, task.priority, Integer.toString(task.status), task.docId, task.docDate, task.dateClosed, Double.toString(task.latitude), Double.toString(task.longitude), task.agentName, CurrentBaseClass.getInstance().getCurrentBase());
                dailyTask1.setRateDate(task.rateDate);
                dailyTask1.setRate(Integer.toString(task.rate));
                dailyTask1.setRateComment(task.rateComment);
                new DailyTasksRepo().insert(dailyTask1);
            }
            System.out.println("Tasks: Done");

            // gettings Sales Histories
            publishProgress("Загрузка истории продаж...");
            System.out.println(request.name);
            SalesHistories salesHistories = blockingStub.getSalesHistory(request);

            if (settings.getSalesHistory) {

                new SalesHistoryRepo().deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());

                for (SalesHistory salesHistory : salesHistories.salesHistory) {

                    kg.soulsb.ayu.models.SalesHistory salesHistory1 = new kg.soulsb.ayu.models.SalesHistory(salesHistory.clientGuid, salesHistory.itemGuid, salesHistory.date1, salesHistory.qty1, salesHistory.date2, salesHistory.qty2, salesHistory.date3, salesHistory.qty3, CurrentBaseClass.getInstance().getCurrentBase());
                    new SalesHistoryRepo().insert(salesHistory1);
                }


                System.out.println("Sales History: Done");
            }

            System.out.println("Finally all done! yay!");
            return pointIterator;
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
                return getClients(mChannel);
            } catch (Exception e) {
                e.printStackTrace();

                publishProgress(e.getMessage());
                // Возвращает пустой итератор
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            loadingComment.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Points pointIterator) {

            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (pointIterator != null){
                loadingComment.setText("Загружено!");
                // Сохранить дату последнего обмена
                Calendar myCalendar = Calendar.getInstance();
                String myFormat = "dd/MM/yyyy HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                String formattedDate = sdf.format(myCalendar.getTime());

                SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LAST_OBMEN", formattedDate);
                editor.putLong("LAST_OBMEN_MILLI", System.currentTimeMillis());
                editor.apply();
            }

            progressBar.setVisibility(View.INVISIBLE);


            SharedPreferences sharedPreferences1 = getSharedPreferences("DefaultBase",MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPreferences1.edit();
            editor1.putString("default_name",baza.getName());
            editor1.putString("default_host",baza.getHost());
            editor1.putString("default_port",Integer.toString(baza.getPort()));
            editor1.putString("default_agent",baza.getAgent());
            editor1.apply();

            setUpNavView();

            arrayList.clear();
            arrayList.addAll(new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase()));
            arrayAdapter.notifyDataSetChanged();
            loadButton.setEnabled(true);
            loadOnlyDocButton.setEnabled(true);
            loadStockButton.setEnabled(true);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (arrayList.isEmpty())
            {
                textViewEmpty.setVisibility(View.VISIBLE);
            }
            else{
                textViewEmpty.setVisibility(View.INVISIBLE);
            }
            if (!errorMessage.equals(""))
            {
                loadingComment.setText(errorMessage);
            }
            errorMessage="";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (arrayList.isEmpty())
        {
            textViewEmpty.setVisibility(View.VISIBLE);
        }
        else{
            textViewEmpty.setVisibility(View.INVISIBLE);
        }
        fillDocumentsTable();
    }

    @Override
    public void onBackPressed()
    {
        if (loadButton.isEnabled()) {
            super.onBackPressed();
    }
    }
}