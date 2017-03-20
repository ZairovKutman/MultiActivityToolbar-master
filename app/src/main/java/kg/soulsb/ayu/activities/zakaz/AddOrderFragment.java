package kg.soulsb.ayu.activities.zakaz;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Point;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.grpctest.nano.PurchDocLine;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Organization;
import kg.soulsb.ayu.models.PriceType;
import kg.soulsb.ayu.models.Warehouse;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.DataHolderClass;
import kg.soulsb.ayu.singletons.MyServiceActivatorClass;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
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
    String currentBaseString;
    String clientGUID;
    String clientLat="0";
    String clientLong="0";
    ArrayList<Contract> arrayListContract;
    EditText editText;
    ArrayAdapter<Contract> arrayAdapterContract;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.add_order_fragment, container, false);
        dbHelper = new DBHelper(getContext());
        final Intent dateintent = new Intent(getContext(), ChooseClientTableActivity.class);

        editText_client = (EditText) v.findViewById(R.id.order_editText_client);

        editText_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(dateintent, REQUEST_CODE);
            }
        });

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
        ArrayAdapter<Warehouse> arrayAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, arrayListWarehouse);
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
        ArrayAdapter<Organization> organizationArrayAdapter = new ArrayAdapter<Organization>(this.getActivity(), android.R.layout.simple_spinner_item, arrayListOrganization);
        editText_organization.setAdapter(organizationArrayAdapter);

        // CLIENT CONTRACT
        spinner_contract = (Spinner) v.findViewById(R.id.order_spinner_dogovor);
        if (clientGUID != null){
            arrayListContract = new ContractsRepo().getContractsObject(clientGUID);
            arrayAdapterContract = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, arrayListContract);
            spinner_contract.setAdapter(arrayAdapterContract);
        }
        // PRICE TYPE
        spinner_pricetype = (Spinner) v.findViewById(R.id.order_spinner_tipcen);
        arrayListPriceType = new PriceTypesRepo().getPricetypesObject();
        ArrayAdapter<PriceType> arrayAdapterPriceType = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, arrayListPriceType);
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
                }

            }
        });

        // Выгрузить кнопка
        createDocButton = (Button) v.findViewById(R.id.btn_create_doc);
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
                alertDialog = d.create();
                alertDialog.show();

                AddOrderFragment.GrpcTask grpcTask = new AddOrderFragment.GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
                        .usePlaintext(true).build(),CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);

                saveDocument(true);
            }
        });
        createDocButton.setEnabled(true);
        if (parentActivity.order != null) {
            fillFields();
        }
        return v;
    }

    private boolean documentIsReady() {
        // TODO: Проверить на заполненность всех полей

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
        arrayAdapterContract = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, arrayListContract);
        spinner_contract.setAdapter(arrayAdapterContract);
        docId = parentActivity.order.getOrderID();
        editText.setText(parentActivity.order.getDate());
        DataHolderClass.getInstance().setAddOrderDateOtgruzki(parentActivity.order.getDateSend());
        DataHolderClass.getInstance().setAddOrderComments(parentActivity.order.getComment());

        for (Contract contract: arrayListContract)
        {
            if (contract.getGuid().equals(parentActivity.order.getDogovor()))
            {
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
        parentActivity.updatePrices();
        parentActivity.updateStock();

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
                arrayAdapterContract = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, arrayListContract);
                spinner_contract.setAdapter(arrayAdapterContract);
            }
        }
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
            System.out.println(deviceStatus.comment);
            if (!deviceStatus.active) {
                return null;
            }

            DocPurch docPurch = new DocPurch();
            docPurch.organizationGuid = arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid();
            docPurch.agent = name;
            docPurch.clientGuid = clientGUID;
            docPurch.comment = DataHolderClass.getInstance().getAddOrderComments();
            docPurch.deliveryDate = DataHolderClass.getInstance().getAddOrderDateOtgruzki();
            docPurch.contractGuid = arrayListContract.get(spinner_contract.getSelectedItemPosition()).getGuid();
            docPurch.date = editText.getText().toString();
            docPurch.warehouseGuid = arrayListWarehouse.get(spinner_warehouse.getSelectedItemPosition()).getGuid();
            docPurch.priceTypeGuid = arrayListPriceType.get(spinner_pricetype.getSelectedItemPosition()).getGuid();
            docPurch.docType = Integer.parseInt(parentActivity.doctype);
            docPurch.docId = docId;

            Map<Item,Integer> orderedItemsArrayList;
            orderedItemsArrayList = parentActivity.getSelectedItems();

            PurchDocLine[] purchDocLines = new PurchDocLine[orderedItemsArrayList.size()];
            int counter = 0;
            for (Item item: orderedItemsArrayList.keySet())
            {
                PurchDocLine line = new PurchDocLine();
                line.amount = orderedItemsArrayList.get(item) * item.getPrice();
                line.itemGuid  = item.getGuid();
                line.price = item.getPrice();
                line.quantity = orderedItemsArrayList.get(item);

                purchDocLines[counter]=line;
                counter++;
            }

            docPurch.lines = purchDocLines;
            OperationStatus bl = blockingStub.createDoc(docPurch);
            System.out.println(bl.status+" "+bl.comment);

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
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            alertDialog.dismiss();
            if (pointIterator == null)
            {
                Toast.makeText(getContext(),"Ошибка, доступ запрещен!",Toast.LENGTH_SHORT).show();
            }
            else {

                saveDocument(true);
                Toast.makeText(getContext(),"Успех! Документ выгружен. =)",Toast.LENGTH_SHORT).show();
            }
            getActivity().finish();
        }
    }
    private void saveDocument(boolean b) {
        Order order = new Order();

        // ID is timestamp
        order.setOrderID(docId);
        order.setBaza(CurrentBaseClass.getInstance().getCurrentBaseObject());
        order.setClient(clientGUID);
        order.setComment(DataHolderClass.getInstance().getAddOrderComments());
        order.setDate(editText.getText().toString());
        order.setDateSend(DataHolderClass.getInstance().getAddOrderDateOtgruzki());
        order.setDoctype(parentActivity.doctype);
        order.setDogovor(arrayListContract.get(spinner_contract.getSelectedItemPosition()).getGuid());
        order.setDelivered(b);
        order.setPriceType(arrayListPriceType.get(spinner_pricetype.getSelectedItemPosition()).getGuid());
        order.setWarehouse(arrayListWarehouse.get(spinner_warehouse.getSelectedItemPosition()).getGuid());
        order.setOrganization(arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid());
        ArrayList<Item> itemArrayList = new ArrayList<Item>();
        Map<Item,Integer> orderedItemsArrayList;
        orderedItemsArrayList = parentActivity.getSelectedItems();
        double total = 0;
        for (Item item: orderedItemsArrayList.keySet())
        {
            itemArrayList.add(item);
            total = total + orderedItemsArrayList.get(item) * item.getPrice();
        }
        order.setTotalSum(total);
        order.setArraylistTovar(itemArrayList);

        OrdersRepo ordersRepo = new OrdersRepo();
        ordersRepo.delete(order);
        ordersRepo.insert(order);
        Toast.makeText(getContext(),"Сохранено",Toast.LENGTH_SHORT).show();
    }

    public String getClientGUID()
    {
        return clientGUID;
    }
}