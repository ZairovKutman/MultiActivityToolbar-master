package kg.soulsb.ayu.activities.zakaz;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.DailyTasks;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
import kg.soulsb.ayu.grpctest.nano.DocsStatus;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.grpctest.nano.PurchDocLine;
import kg.soulsb.ayu.grpctest.nano.Stock;
import kg.soulsb.ayu.grpctest.nano.Stocks;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.DailyTasksRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.StocksRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.DailyTask;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Organization;
import kg.soulsb.ayu.models.PriceType;
import kg.soulsb.ayu.models.Warehouse;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * Created by soulsb on 1/10/17.
 */

public class AddOrderFragment extends Fragment {
    View v;
    int mYear, mMonth, mDay;
    public static final int REQUEST_CODE = 100;
    public EditText editText_client;
    Spinner editText_organization;
    Spinner spinner_warehouse;
    Spinner spinner_contract;
    Button createDocButton;
    Button saveDocButton;
    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;
    String clientGUID;
    String clientLat="0";
    String clientLong="0";
    ArrayList<Contract> arrayListContract;
    EditText editText;
    EditText editTextDostavka;
    ArrayAdapter<Contract> arrayAdapterContract;
    ArrayAdapter<String> arrayAdapterContractNull;
    ArrayList<Warehouse> arrayListWarehouse;
    ArrayList<PriceType> arrayListPriceType;
    ArrayList<Organization> arrayListOrganization;
    private Spinner spinner_pricetype;
    OrderAddActivity parentActivity;
    DBHelper dbHelper;
    AlertDialog.Builder d;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    String docId;
    Contract previous=new Contract();
    Contract current=null;
    TextView distanceClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.add_order_fragment, container, false);
        dbHelper = new DBHelper(getContext());
        final Intent dateintent = new Intent(getContext(), ChooseClientTableActivity.class);

        createDocButton = (Button) v.findViewById(R.id.btn_create_doc);
        distanceClient = (TextView) v.findViewById(R.id.order_client_distance_text);
        editText_client = (EditText) v.findViewById(R.id.order_editText_client);

        editText_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateintent.putExtra("latitude",parentActivity.mLastLocation.getLatitude());
                dateintent.putExtra("longitude",parentActivity.mLastLocation.getLongitude());
                dateintent.putExtra("doctype",parentActivity.doctype);
                startActivityForResult(dateintent, REQUEST_CODE);
            }
        });


        editTextDostavka = (EditText) v.findViewById(R.id.delivery_date);
        editTextDostavka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        editTextDostavka.setText(sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Выберите дату");
                mDatePicker.show();
            }
        });
        Calendar myCalendar = Calendar.getInstance();
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        myCalendar.setTimeInMillis(myCalendar.getTimeInMillis()+86400000);
        editTextDostavka.setText(sdf.format(myCalendar.getTime()));

        editText = (EditText) v.findViewById(R.id.order_date);
        // Set default Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        editText.setText(formattedDate);

        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy HH:mm:ss";

                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        editText.setText(sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Выберите дату");
                mDatePicker.show();
            }
        });

        // WAREHOUSE
        spinner_warehouse = (Spinner) v.findViewById(R.id.order_spinner_sklad);
        DBHelper dbHelper = new DBHelper(getContext());
        DatabaseManager.initializeInstance(dbHelper);
        arrayListWarehouse = new WarehousesRepo().getWarehousesObject();
        ArrayAdapter<Warehouse> arrayAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.baza_spinner_item, arrayListWarehouse);
        spinner_warehouse.setAdapter(arrayAdapter);
        spinner_warehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrderAddActivity activityMy = (OrderAddActivity) getActivity();
                activityMy.setWarehouseGuid(arrayListWarehouse.get(position).getGuid());
                activityMy.updateStock();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Organization
        editText_organization = (Spinner) v.findViewById(R.id.order_spinner_organization);

        arrayListOrganization = new OrganizationsRepo().getOrganizationsObject();
        ArrayAdapter<Organization> organizationArrayAdapter = new ArrayAdapter<Organization>(this.getActivity(), R.layout.baza_spinner_item, arrayListOrganization);
        editText_organization.setAdapter(organizationArrayAdapter);

        if (parentActivity.isTask.equals("true"))
        {
            clientGUID = parentActivity.taskClient;
            prepareTask();
        }

        // CLIENT CONTRACT
        spinner_contract = (Spinner) v.findViewById(R.id.order_spinner_dogovor);
        if (clientGUID != null){
            arrayListContract = new ContractsRepo().getContractsObject(clientGUID);
            arrayAdapterContract = new ArrayAdapter<>(this.getActivity(), R.layout.baza_spinner_item, arrayListContract);
            spinner_contract.setAdapter(arrayAdapterContract);
            parentActivity.category = arrayListContract.get(0).getCategory();
            current = arrayListContract.get(0);
        }
        else
        {
            ArrayList<String> newArray = new ArrayList<>();
            newArray.add(" ");
            arrayAdapterContractNull = new ArrayAdapter<>(this.getActivity(), R.layout.baza_spinner_item, newArray);
            spinner_contract.setAdapter(arrayAdapterContractNull);
        }

        spinner_contract.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (arrayListContract != null && current != null)
                {
                    current = (Contract) spinner_contract.getSelectedItem();
                    updateTovarListByContract();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // PRICE TYPE
        spinner_pricetype = (Spinner) v.findViewById(R.id.order_spinner_tipcen);
        arrayListPriceType = new PriceTypesRepo().getPricetypesObject();
        ArrayAdapter<PriceType> arrayAdapterPriceType = new ArrayAdapter<>(this.getActivity(), R.layout.baza_spinner_item, arrayListPriceType);
        spinner_pricetype.setAdapter(arrayAdapterPriceType);

        spinner_pricetype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrderAddActivity activityMy = (OrderAddActivity) getActivity();
                activityMy.setPriceTypeGUID(arrayListPriceType.get(position).getGuid());
                activityMy.updatePrices();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Сохранить кнопка
        saveDocButton = (Button) v.findViewById(R.id.save_order_button);
        saveDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (documentIsReady()) {
                    saveDocument(false);
                    Toast.makeText(getContext(),"Сохранено",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }

            }
        });

        // Выгрузить кнопка

        createDocButton.setOnClickListener(new View.OnClickListener() {
            public Baza baza;

            @Override
            public void onClick(View v) {
                if (!documentIsReady()) return;

                baza = CurrentBaseClass.getInstance().getCurrentBaseObject();

                String mHost = baza.getHost();
                int mPort = baza.getPort();
                d = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getActivity().getLayoutInflater();
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
                        getActivity().finish();
                    }
                });

                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                AddOrderFragment.GrpcTask grpcTask = new AddOrderFragment.GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
                        .usePlaintext(true).build(),CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
            }
        });
        createDocButton.setEnabled(true);
        if (parentActivity.order != null) {

            fillFields();
            if (parentActivity.isDelivered.equals("true"))
            {
                disableButtons();
            }
        }
        return v;
    }

    private void prepareTask() {
        editText_client.setEnabled(false);
        editText_client.setText(parentActivity.clientName);
        createDocButton.setEnabled(false);
    }

    private void updateTovarListByContract() {

        if (previous.equals(current)) {return;}


        previous = current;

        parentActivity.category = current.getCategory();
//        parentActivity.addTovarFragment.originalArrayList.clear();
//
//        parentActivity.addTovarFragment.originalArrayList.addAll(new ItemsRepo().getItemsObjectByCategory(current.getCategory()));
//        parentActivity.addTovarFragment.arrayList.clear();
//        parentActivity.addTovarFragment.arrayList.addAll(new ItemsRepo().getItemsObjectByCategory(current.getCategory()));
//
//        parentActivity.orderedItemsArrayList.clear();
//
//        parentActivity.addTovarFragment.updatePrices();
//        parentActivity.addTovarFragment.updateStock();
//        parentActivity.addTovarFragment.arrayAdapter.notifyDataSetChanged();

        parentActivity.tovarsFragment.arrayListAllTovars.clear();
        parentActivity.tovarsFragment.arrayListAllTovars.addAll(new ItemsRepo().getItemsObjectByCategory(current.getCategory()));

        parentActivity.orderedItemsArrayList.clear();

        parentActivity.tovarsFragment.updatePrices();
        parentActivity.tovarsFragment.updateStock();
}

    private void disableButtons() {
        editText.setEnabled(false);
        editTextDostavka.setEnabled(false);
        editText_client.setEnabled(false);
        editText_organization.setEnabled(false);
        spinner_contract.setEnabled(false);
        spinner_pricetype.setEnabled(false);
        spinner_warehouse.setEnabled(false);
        createDocButton.setEnabled(false);
        saveDocButton.setEnabled(false);
    }

    public boolean documentIsReady() {

        // Дата
        if (TextUtils.isEmpty(editText.getText())) {
            Toast.makeText(getContext(), "Не заполнена дата", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editText_client.getText())) {
            Toast.makeText(getContext(), "Не заполнено поле Клиент", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((editText_organization.getSelectedItem() == null) || editText_organization == null) {
            Toast.makeText(getContext(), "Не заполнено поле Организация", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((spinner_contract.getSelectedItem() == null) || spinner_contract == null) {
            Toast.makeText(getContext(), "Не заполнено поле Договор", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((spinner_pricetype.getSelectedItem() == null) || spinner_pricetype == null) {
            Toast.makeText(getContext(), "Не заполнено поле Тип цен", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((spinner_warehouse.getSelectedItem() == null) || spinner_warehouse == null) {
            Toast.makeText(getContext(), "Не заполнено поле Склад", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (parentActivity.orderedItemsArrayList.isEmpty()) {
            Toast.makeText(getContext(), "Не выбраны товары", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void fillFields() {
        Client client = new ClientsRepo().getClientObjectByGuid(parentActivity.order.getClient());
        clientGUID = client.getGuid();
        editText_client.setText(client.getName());
        clientLat = client.getLatitude();
        clientLong = client.getLongitude();
        DatabaseManager.initializeInstance(dbHelper);
        arrayListContract = new ContractsRepo().getContractsObject(clientGUID);
        arrayAdapterContract = new ArrayAdapter<>(this.getActivity(), R.layout.baza_spinner_item, arrayListContract);
        spinner_contract.setAdapter(arrayAdapterContract);
        docId = parentActivity.order.getOrderID();
        editText.setText(parentActivity.order.getDate());
        editTextDostavka.setText(parentActivity.order.getDateSend());
        //parentActivity.othersFragment.comments.setText(parentActivity.order.getComment());

        for (Contract contract: arrayListContract)
        {
            if (contract.getGuid().equals(parentActivity.order.getDogovor()))
            {
                current = contract;
                previous = contract;
                spinner_contract.setSelection(arrayListContract.indexOf(contract));
                break;
            }
        }

        for (PriceType priceType: arrayListPriceType)
        {
            if (priceType.getGuid().equals(parentActivity.order.getPriceType()))
            {
                spinner_pricetype.setSelection(arrayListPriceType.indexOf(priceType));
                break;
            }
        }

        for (Warehouse warehouse: arrayListWarehouse)
        {
            if (warehouse.getGuid().equals(parentActivity.order.getWarehouse()))
            {
                spinner_warehouse.setSelection(arrayListWarehouse.indexOf(warehouse));
                break;
            }
        }

        for (Organization organization: arrayListOrganization)
        {
            if (organization.getGuid().equals(parentActivity.order.getOrganization()))
            {
                editText_organization.setSelection(arrayListOrganization.indexOf(organization));
                break;
            }
        }

        parentActivity.setWarehouseGuid(parentActivity.getWarehouse());
        parentActivity.setPriceTypeGUID(parentActivity.getPriceType());
//        parentActivity.updatePrices();
//        parentActivity.updateStock();

        if (parentActivity.order.isDelivered())
        {
            editText_client.setEnabled(false);
            editText.setEnabled(false);
            editText_organization.setEnabled(false);
            spinner_pricetype.setEnabled(false);
            spinner_contract.setEnabled(false);
            spinner_warehouse.setEnabled(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = ((OrderAddActivity)getActivity());
        if (parentActivity.order == null) {
            Long tsLong = System.currentTimeMillis() / 1000;
            docId = tsLong.toString();
        }
        else
            {
                docId = parentActivity.order.getOrderID();
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String my_data = data.getStringExtra("data");
                clientGUID = data.getStringExtra("guid");
                clientLat = data.getStringExtra("lat");
                clientLong = data.getStringExtra("long");

                editText_client.setText(my_data);
                DatabaseManager.initializeInstance(dbHelper);
                arrayListContract = new ContractsRepo().getContractsObject(clientGUID);
                arrayAdapterContract = new ArrayAdapter<>(this.getActivity(), R.layout.baza_spinner_item, arrayListContract);
                spinner_contract.setAdapter(arrayAdapterContract);
                current = arrayListContract.get(0);
                updateTovarListByContract();
            }
        }
    }



    private class GrpcTask extends AsyncTask<Void, String, Points> {
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
            saveDocument(false);
        }

        private Points createDoc(ManagedChannel mChannel) {

            blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
            Agent request = new Agent();
            request.name = name;

            String android_id = android.provider.Settings.Secure.getString(getContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            Device device = new Device();
            device.agent = name;
            device.deviceId = android_id;
            DeviceStatus deviceStatus = blockingStub.checkDeviceStatus(device);

            if (!deviceStatus.active) {
                publishProgress("Доступ с телефона запрещен");
                return null;
            }

            DocPurch docPurch = new DocPurch();
            docPurch.organizationGuid = arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid();
            docPurch.agent = name;
            docPurch.clientGuid = clientGUID;
            docPurch.comment = parentActivity.othersFragment.comments.getText().toString();
            docPurch.bonusTT = parentActivity.othersFragment.bonusTT.isChecked();

            docPurch.deliveryDate = editTextDostavka.getText().toString();
            docPurch.contractGuid = arrayListContract.get(spinner_contract.getSelectedItemPosition()).getGuid();
            docPurch.date = editText.getText().toString();
            docPurch.warehouseGuid = arrayListWarehouse.get(spinner_warehouse.getSelectedItemPosition()).getGuid();
            docPurch.priceTypeGuid = arrayListPriceType.get(spinner_pricetype.getSelectedItemPosition()).getGuid();
            docPurch.docType = Integer.parseInt(parentActivity.doctype);
            docPurch.docId = docId;
            docPurch.latitude = Double.toString(parentActivity.mLastLocation.getLatitude());
            docPurch.longitude = Double.toString(parentActivity.mLastLocation.getLongitude());

            ArrayList<Item> orderedItemsArrayList;
            orderedItemsArrayList = parentActivity.getSelectedItems();

            PurchDocLine[] purchDocLines = new PurchDocLine[orderedItemsArrayList.size()];
            int counter = 0;
            for (Item item: orderedItemsArrayList)
            {
                PurchDocLine line = new PurchDocLine();
                line.amount = item.getQuantity() * item.getPrice() * item.getMyUnit().getCoefficient();
                line.itemGuid  = item.getGuid();
                line.price = item.getPrice() * item.getMyUnit().getCoefficient();
                line.quantity = item.getQuantity();
                line.unit = item.getMyUnit().getUnitGuid();
                purchDocLines[counter]=line;
                counter++;
            }

            docPurch.lines = purchDocLines;
            OperationStatus bl = blockingStub.createDoc(docPurch);



            if (bl.status != 0)
            {
                publishProgress("Ошибка:"+bl.comment);
                return null;
            }

            if (parentActivity.isTask.equals("true")) {
                ArrayList<DailyTask> dailyTaskArrayList = new DailyTasksRepo().getDailyTasksObject();
                kg.soulsb.ayu.grpctest.nano.DailyTask[] dailyTasks = new kg.soulsb.ayu.grpctest.nano.DailyTask[dailyTaskArrayList.size()];
                int i=0;
                for (DailyTask dt: dailyTaskArrayList)
                {
                    kg.soulsb.ayu.grpctest.nano.DailyTask dt2 = new kg.soulsb.ayu.grpctest.nano.DailyTask();
                    dt2.agentName = name;
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

                System.out.println(ds.docsStatus);
            }
            ArrayList<kg.soulsb.ayu.models.Stock> stocksArray = new ArrayList<>();
            // getting Stocks
            Stocks stocks = blockingStub.getStock(request);
            StocksRepo stocksRepo = new StocksRepo();
            stocksRepo.deleteByBase(CurrentBaseClass.getInstance().getCurrentBase());
            for (Stock stock : stocks.stock) {
                kg.soulsb.ayu.models.Stock stock1 = new kg.soulsb.ayu.models.Stock(stock.item, stock.warehouse, stock.stock);
                stock1.setBase(CurrentBaseClass.getInstance().getCurrentBase());
                stocksArray.add(stock1);
                stocksRepo.insert(stock1);
            }

            return new Points();
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
                return createDoc(mChannel);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

            alertDialog.setMessage(values[0]);
        }

        @Override
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (pointIterator == null)
            {
                alertDialog.setTitle("Ошибка");
            }
            else {
                saveDocument(true);
                alertDialog.setTitle("Успех");
                alertDialog.setMessage("Документ выгружен =)");
                Toast.makeText(getContext(), "Успех! Документ выгружен. =)", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        }
    }
    public void saveDocument(boolean b) {
        Order order = new Order();

        // ID is timestamp
        order.setOrderID(docId);
        order.setBaza(CurrentBaseClass.getInstance().getCurrentBaseObject());
        order.setClient(clientGUID);
        order.setComment(parentActivity.othersFragment.comments.getText().toString());
        if (parentActivity.othersFragment.bonusTT.isChecked()) {
            order.setCheckedBonusTT("true");
        }
        else{
            order.setCheckedBonusTT("false");
        }
        order.setDate(editText.getText().toString());
        order.setDateSend(editTextDostavka.getText().toString());
        order.setDoctype(parentActivity.doctype);
        order.setDogovor(arrayListContract.get(spinner_contract.getSelectedItemPosition()).getGuid());
        order.setDelivered(b);
        order.setPriceType(arrayListPriceType.get(spinner_pricetype.getSelectedItemPosition()).getGuid());
        order.setWarehouse(arrayListWarehouse.get(spinner_warehouse.getSelectedItemPosition()).getGuid());
        order.setOrganization(arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid());

        ArrayList<Item> itemArrayList;

        itemArrayList = parentActivity.getSelectedItems();
        double total = 0;
        for (Item item: itemArrayList)
        {
            total = total + item.getQuantity() * item.getPrice()*item.getMyUnit().getCoefficient();
        }

        order.setTotalSum(total);
        order.setArraylistTovar(itemArrayList);

        OrdersRepo ordersRepo = new OrdersRepo();
        ordersRepo.delete(order);
        ordersRepo.insert(order);

        if (parentActivity.isTask.equals("true"))
        {
            Calendar myCalendar = Calendar.getInstance();
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
            myCalendar.setTimeInMillis(myCalendar.getTimeInMillis());

            new DailyTasksRepo().updateStatus(clientGUID, docId, editText.getText().toString(), CurrentLocationClass.getInstance().getCurrentLocation().getLatitude(), CurrentLocationClass.getInstance().getCurrentLocation().getLongitude(), sdf.format(myCalendar.getTime()), "1");
        }

        Toast.makeText(getContext(),"Сохранено",Toast.LENGTH_SHORT).show();
    }
}