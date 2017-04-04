package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.ClientAdapter;
import kg.soulsb.ayu.adapters.OrderAdapter;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.Contract;
import kg.soulsb.ayu.grpctest.nano.Contracts;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
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
import kg.soulsb.ayu.grpctest.nano.Settings;
import kg.soulsb.ayu.grpctest.nano.Stock;
import kg.soulsb.ayu.grpctest.nano.Stocks;
import kg.soulsb.ayu.grpctest.nano.Warehouse;
import kg.soulsb.ayu.grpctest.nano.Warehouses;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.ReportsRepo;
import kg.soulsb.ayu.helpers.repo.SavedReportsRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Item;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.grpctest.nano.Price;
import kg.soulsb.ayu.models.Order;
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
    Button loadButton,loadOnlyDocButton;
    int globalCounter = 0;
    Baza baza = null;
    String currentBaseString;
    ListView listViewDocuments;
    ArrayAdapter<Order> arrayAdapter;
    ArrayList<Order> arrayList = new ArrayList<>();
    boolean fullObmen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_obmen);

        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        loadingComment = (TextView) findViewById(R.id.loading_comment);
        lastObmenText = (TextView) findViewById(R.id.last_obmen_text);
        loadButton = (Button) findViewById(R.id.button);
        loadOnlyDocButton = (Button) findViewById(R.id.button_only_documents);
        listViewDocuments = (ListView) findViewById(R.id.listView_documents);
        textViewEmpty = (TextView) findViewById(R.id.textViewEmpty);
        arrayList = new OrdersRepo().getOrdersObjectNotDelivered(CurrentBaseClass.getInstance().getCurrentBase());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, arrayList);
        listViewDocuments.setAdapter(arrayAdapter);
        listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(),OrderAddActivity.class);
                intent.putExtra("doctype",arrayList.get(i).getDoctype());
                intent.putExtra("savedobj", arrayList.get(i));
                startActivity(intent);}
        });
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
            textView.setText("Используемая база: "+baza.getName());
        else
            textView.setText("Используемая база: Создайте базу данных");

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baza!= null) {
                loadingComment.setText("Подключение... "+"5%");
                try {
                    fullObmen = true;
                    loadButton.setEnabled(false);
                    loadOnlyDocButton.setEnabled(false);
                    doFullObmen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }}
                else{
                Toast.makeText(getBaseContext(),"Создайте базу данных",Toast.LENGTH_SHORT).show();}
            }
        });

        loadOnlyDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (baza!= null) {
                    loadingComment.setText("Подключение... "+"5%");
                    try {
                        fullObmen = false;
                        loadButton.setEnabled(false);
                        loadOnlyDocButton.setEnabled(false);
                        doFullObmen();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }}
                else{
                    Toast.makeText(getBaseContext(),"Создайте базу данных",Toast.LENGTH_SHORT).show();}
            }
        });

        textView = (TextView) findViewById(R.id.usingBase);
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
        }, 150000 );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_settings_obmen:
                return true;
        }
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
            loadingComment.setText("Идет загрузка... "+1+"%");
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
            publishProgress("Проверка устройства в базе данных... 0%");
            DeviceStatus deviceStatus = blockingStub.checkDeviceStatus(device);
            System.out.println(deviceStatus.comment);
            if (!deviceStatus.active) {
                publishProgress("Ошибка, доступ с этого телефона запрещен");
                return null;
            }

            //
            publishProgress("Выгрузка документов... 2%");

            //Выгружаю документы
            DocPurch docPurchArray[] = new DocPurch[arrayList.size()];
            for (Order order : arrayList) {
                DocPurch docPurch = new DocPurch();
                docPurch.organizationGuid = order.getOrganization();
                docPurch.agent = name;
                docPurch.clientGuid = order.getClient();
                docPurch.comment = order.getComment();
                docPurch.deliveryDate = order.getDateSend();
                docPurch.contractGuid = order.getDogovor();
                docPurch.date = order.getDate();
                docPurch.warehouseGuid = order.getWarehouse();
                docPurch.priceTypeGuid = order.getPriceType();
                docPurch.docType = Integer.parseInt(order.getDoctype());
                docPurch.docId = order.getOrderID();

                ArrayList<Item> itemsList = order.getArraylistTovar();

                PurchDocLine[] purchDocLines = new PurchDocLine[itemsList.size()];
                int counter = 0;
                for (Item item : itemsList) {
                    PurchDocLine line = new PurchDocLine();
                    line.amount = item.getQuantity() * item.getPrice();
                    line.itemGuid = item.getGuid();
                    line.price = item.getPrice();
                    line.quantity = item.getQuantity();

                    purchDocLines[counter] = line;
                    counter++;
                }

                docPurch.lines = purchDocLines;

                docPurchArray[arrayList.indexOf(order)] = docPurch;
            }
            if (arrayList.size() > 0){
                Docs docs = new Docs();
                docs.doc = docPurchArray;
                DocsStatus docsStatus = blockingStub.getDocuments(docs);

                for (int i=0;i<docsStatus.docsStatus.length;i++) {
                    if (docsStatus.docsStatus[i].operationStatus.status == 0)
                    {
                        new OrdersRepo().setDocDelivered(docsStatus.docsStatus[i].docId, true);
                        System.out.println("DOCUMENTS DELIVERED: "+docsStatus.docsStatus[i].operationStatus.status+"$"+docsStatus.docsStatus[i].operationStatus.comment);
                    }
                }
            }
            //Конец выгрузки документов
            if (!fullObmen)
            {
                return new Points();
            }

            new OrdersRepo().deleteDocDelivered();

            publishProgress("Загружаю данные из сервера... 7%");

            ExchangeData exchangeData = blockingStub.getAllData(request);

            publishProgress("Загрузка настроек... 10%");
            Settings settings = exchangeData.settings;

            if (!settings.status) {
                publishProgress("Ошибка: " + settings.errorMessage);
            }
            // Получаю настройки
            SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
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
            editor.putString(UserSettings.status, Boolean.toString(settings.status));
            editor.apply();
            publishProgress("Загрузка клиентов... 20%");
            Points pointIterator = exchangeData.points;
            globalCounter = 10;

            ArrayList<Client> clientsArray = new ArrayList<>();
            ArrayList<Item> itemsArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Warehouse> warehousesArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Contract> contractsArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.PriceType> pricetypesArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Price> pricesArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Stock> stocksArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Report> reportsArray = new ArrayList<>();
            ArrayList<kg.soulsb.ayu.models.Organization> organizationsArray = new ArrayList<>();
            DBHelper dbHelper = new DBHelper(getBaseContext());
            DatabaseManager.initializeInstance(dbHelper);


            // getting clients
            ClientsRepo clientsRepo = new ClientsRepo();
            clientsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Point point: pointIterator.point)
            {
                Client client = new Client(point.guid,point.description,point.address, point.phoneNumber,point.latitude,point.longitude,point.debt);
                client.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                clientsArray.add(client);
                clientsRepo.insert(client);
           }
            System.out.println("Clients: Done");

            publishProgress("Загрузка товаров... 30%");
            // getting tovar
            Items items = exchangeData.items;
            ItemsRepo itemsRepo = new ItemsRepo();
            itemsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (kg.soulsb.ayu.grpctest.nano.Item item: items.item)
            {
                Item item1 = new Item(item.guid,item.description,item.unit, item.price,item.stock);
                item1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                itemsArray.add(item1);
                itemsRepo.insert(item1);
            }
            System.out.println("Items: Done");
            publishProgress("Загрузка складов... 40%");
            // getting warehouses
            Warehouses warehouses = exchangeData.warehouses;
            WarehousesRepo warehousesRepo = new WarehousesRepo();
            warehousesRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Warehouse warehouse: warehouses.warehouse)
            {
                kg.soulsb.ayu.models.Warehouse warehouse1 = new kg.soulsb.ayu.models.Warehouse(warehouse.guid,warehouse.description);
                warehouse1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                warehousesArray.add(warehouse1);
                warehousesRepo.insert(warehouse1);
            }
            System.out.println("Warehouses: Done");
            publishProgress("Загрузка договоров... 50%");
            // getting contracts
            Contracts contracts = exchangeData.contracts;
            ContractsRepo contractsRepo = new ContractsRepo();
            contractsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Contract contract: contracts.contract)
            {
                kg.soulsb.ayu.models.Contract contract1 = new kg.soulsb.ayu.models.Contract(contract.guid,contract.description,contract.pointGuid);
                contract1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                contractsArray.add(contract1);
                contractsRepo.insert(contract1);
            }
            System.out.println("Contracts: Done");

            publishProgress("Загрузка организации... 60%");
            // getting Organization
            Organizations organizations = exchangeData.organizations;
            OrganizationsRepo organizationsRepo = new OrganizationsRepo();
            organizationsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Organization organization: organizations.organization)
            {
                kg.soulsb.ayu.models.Organization organization1 = new kg.soulsb.ayu.models.Organization(organization.guid,organization.description);
                organization1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                organizationsArray.add(organization1);
                organizationsRepo.insert(organization1);
            }
            System.out.println("Organization: Done");
            publishProgress("Загрузка типов цен... 70%");
            // getting price types
            PriceTypes priceTypes = exchangeData.priceTypes;
            PriceTypesRepo priceTypesRepo = new PriceTypesRepo();
            priceTypesRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (PriceType priceType: priceTypes.priceType)
            {
                kg.soulsb.ayu.models.PriceType priceType1 = new kg.soulsb.ayu.models.PriceType(priceType.guid,priceType.description);
                priceType1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                pricetypesArray.add(priceType1);
                priceTypesRepo.insert(priceType1);
            }
            System.out.println("Price types: Done");
            publishProgress("Загрузка цен... 80%");
            // getting prices
            Prices prices = exchangeData.prices;
            PricesRepo pricesRepo = new PricesRepo();
            pricesRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Price price: prices.price)
            {
                kg.soulsb.ayu.models.Price price1 = new kg.soulsb.ayu.models.Price(price.item,price.price,price.priceType);
                price1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                pricesArray.add(price1);
                pricesRepo.insert(price1);
            }
            System.out.println("Price: Done");
            publishProgress("Загрузка остатков... 90%");
            // getting Stocks
            Stocks stocks = exchangeData.stocks;
            StocksRepo stocksRepo = new StocksRepo();
            stocksRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Stock stock: stocks.stock)
            {
                kg.soulsb.ayu.models.Stock stock1 = new kg.soulsb.ayu.models.Stock(stock.item,stock.warehouse,stock.stock);
                stock1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                stocksArray.add(stock1);
                stocksRepo.insert(stock1);
            }
            System.out.println("Stock: Done");
            publishProgress("Загрузка отчетов... 97%");
            //getting reports
            Reports reports = exchangeData.reports;
            ReportsRepo reportsRepo = new ReportsRepo();
            reportsRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Report report: reports.report)
            {
                kg.soulsb.ayu.models.Report report1 = new kg.soulsb.ayu.models.Report(report.guid,report.description);
                report1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                reportsArray.add(report1);
                reportsRepo.insert(report1);
            }
            System.out.println("Reports: Done");

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
                loadingComment.setText("Загружено... " + 100 + "%");
                // Сохранить дату последнего обмена
                SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LAST_OBMEN", DateFormat.getDateTimeInstance().format(new Date()));
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
    }
}