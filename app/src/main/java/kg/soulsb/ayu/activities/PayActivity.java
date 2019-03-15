package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.ChooseClientTableActivity;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.Organization;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import kg.soulsb.ayu.singletons.UserSettings;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class PayActivity extends BaseActivity {

    public static final int REQUEST_CODE = 100;
    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;

    DBHelper dbHelper;
    EditText editText_client;
    EditText editTextDate;
    EditText editTextSum;
    Spinner editText_organization;
    Spinner spinner_contract;
    Button createDocButton;
    Button saveDocButton;

    ArrayList<Organization> arrayListOrganization;
    ArrayList<Contract> arrayListContract;

    ArrayAdapter<Organization> organizationArrayAdapter;
    ArrayAdapter<Contract> arrayAdapterContract;
    ArrayAdapter<String> arrayAdapterContractNull;

    private Timer mTimer = new Timer();
    Location mLastLocation;

    Location clientLocation = new Location("");
    Order order = null;
    String isDelivered = "false";

    int mYear, mMonth, mDay;
    int docType = 2;
    float distance = 0;

    String clientGUID;
    String clientLat = "0";
    String clientLong = "0";
    Intent myIntent;

    AlertDialog.Builder d;
    ProgressBar progressBar;
    AlertDialog alertDialog;
    String docId;

    public Location getLocation() {
        System.out.println(" I GOT A LOCATION: lat=" + CurrentLocationClass.getInstance().getCurrentLocation().getLatitude()
                + " long=" + CurrentLocationClass.getInstance().getCurrentLocation().getLongitude());
        return CurrentLocationClass.getInstance().getCurrentLocation();
    }

    public void locationUpdate() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // run on another thread
                mLastLocation = getLocation();

                clientLocation.setLatitude(Double.parseDouble(clientLat));
                clientLocation.setLongitude(Double.parseDouble(clientLong));
                distance = mLastLocation.distanceTo(clientLocation);
                if (distance < 51) {
                    createDocButton.setEnabled(true);
                } else {
                    createDocButton.setEnabled(false);
                }
            }
        }, 0);
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            locationUpdate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        if (docId == null) {
            Long tsLong = System.currentTimeMillis() / 1000;
            docId = tsLong.toString();
        }

        dbHelper = new DBHelper(this);

        createDocButton = (Button) findViewById(R.id.btn_pay_create_doc);
        saveDocButton = (Button) findViewById(R.id.save_pay_button);

        editText_organization = (Spinner) findViewById(R.id.pay_spinner_organization);
        spinner_contract = (Spinner) findViewById(R.id.pay_spinner_dogovor);
        editTextSum = (EditText) findViewById(R.id.pay_sum);
        arrayListOrganization = new OrganizationsRepo().getOrganizationsObject();
        organizationArrayAdapter = new ArrayAdapter<>(this, R.layout.baza_spinner_item, arrayListOrganization);
        editText_organization.setAdapter(organizationArrayAdapter);


        // CLIENT CONTRACT
        if (clientGUID != null) {
            arrayListContract = new ContractsRepo().getContractsObject(clientGUID);
            arrayAdapterContract = new ArrayAdapter<>(this, R.layout.baza_spinner_item, arrayListContract);
            spinner_contract.setAdapter(arrayAdapterContract);
        } else {
            ArrayList<String> newArray = new ArrayList<>();
            newArray.add(" ");
            arrayAdapterContractNull = new ArrayAdapter<>(this, R.layout.baza_spinner_item, newArray);
            spinner_contract.setAdapter(arrayAdapterContractNull);
        }


        //DATE
        editTextDate = (EditText) findViewById(R.id.pay_date);
        // Set default Date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        editTextDate.setText(formattedDate);

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(getParent(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy HH:mm:ss";

                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        editTextDate.setText(sdf.format(myCalendar.getTime()));

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Выберите дату");
                mDatePicker.show();
            }
        });

        editText_client = (EditText) findViewById(R.id.pay_editText_client);
        myIntent = new Intent(this, ChooseClientTableActivity.class);
        editText_client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(myIntent, REQUEST_CODE);
            }
        });


        // Сохранить кнопка
        saveDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (documentIsReady()) {
                    saveDocument(false);
                    Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

        // Выгрузить кнопка
        createDocButton.setOnClickListener(new View.OnClickListener() {
            Baza baza;

            @Override
            public void onClick(View v) {
                if (!documentIsReady()) return;

                baza = CurrentBaseClass.getInstance().getCurrentBaseObject();

                String mHost = baza.getHost();
                int mPort = baza.getPort();
                d = new AlertDialog.Builder(PayActivity.this);
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
                        finish();
                    }
                });

                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                GrpcTask grpcTask = new GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
                        .usePlaintext(true).build(),CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
            }
        });
        createDocButton.setEnabled(true);

        if (getIntent().getStringExtra("doctype")!=null)
        {
            fillFields();
        }


    }

    private void fillFields() {
        Order order1 = (Order) getIntent().getSerializableExtra("savedobj");
        editTextDate.setText(order1.getDate());
        editTextSum.setText(Double.toString(order1.getTotalSum()));

        Client client = new ClientsRepo().getClientObjectByGuid(order1.getClient());
        clientGUID = client.getGuid();
        editText_client.setText(client.getName());
        clientLat = client.getLatitude();
        clientLong = client.getLongitude();
        DatabaseManager.initializeInstance(dbHelper);
        arrayListContract = new ContractsRepo().getContractsObject(clientGUID);

        arrayAdapterContract = new ArrayAdapter<>(this, R.layout.baza_spinner_item, arrayListContract);
        spinner_contract.setAdapter(arrayAdapterContract);

        for (Contract contract: arrayListContract)
        {
            if (contract.getGuid().equals(order1.getDogovor()))
            {
                spinner_contract.setSelection(arrayListContract.indexOf(contract));
                break;
            }
        }

        for (Organization organization: arrayListOrganization)
        {
            if (organization.getGuid().equals(order1.getOrganization()))
            {
                editText_organization.setSelection(arrayListOrganization.indexOf(organization));
                break;
            }
        }

        docId = order1.getOrderID();
        if (order1.isDelivered())
            disableButtons();

    }


    @Override
    public void onBackPressed()
    {
        createDialog();
    }

    private void createDialog() {
        if (isDelivered.equals("true"))
        {
            PayActivity.super.onBackPressed();
            return;
        }

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Сохранить документ перед выходом?");
        alertDlg.setCancelable(false); // We avoid that the dialog can be cancelled, forcing the user to choose one of the options
        alertDlg.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (documentIsReady()) {
                    saveDocument(false);
                    Toast.makeText(getBaseContext(),"Сохранено",Toast.LENGTH_SHORT).show();
                    PayActivity.super.onBackPressed();
                }
            }
        });

        alertDlg.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PayActivity.super.onBackPressed();
            }
        });
        alertDlg.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDlg.create().show();
    }

    public boolean documentIsReady() {

        // Дата
        if (TextUtils.isEmpty(editTextDate.getText())) {
            Toast.makeText(getApplicationContext(), "Не заполнена дата", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editTextSum.getText())) {
            Toast.makeText(getApplicationContext(), "Не заполнена сумма", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(editText_client.getText())) {
            Toast.makeText(getApplicationContext(), "Не заполнено поле Клиент", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((editText_organization.getSelectedItem() == null) || editText_organization == null) {
            Toast.makeText(getApplicationContext(), "Не заполнено поле Организация", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((spinner_contract.getSelectedItem() == null) || spinner_contract == null) {
            Toast.makeText(getApplicationContext(), "Не заполнено поле Договор", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void disableButtons() {
        Toast.makeText(PayActivity.this,"Документ выгружен, редактрование запрещено.",Toast.LENGTH_SHORT).show();

        editTextSum.setFocusable(false);
        editTextDate.setEnabled(false);
        editText_client.setEnabled(false);
        editText_organization.setEnabled(false);
        spinner_contract.setEnabled(false);
        editTextSum.setEnabled(false);
        createDocButton.setEnabled(false);
        saveDocButton.setEnabled(false);
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
                arrayAdapterContract = new ArrayAdapter<>(this, R.layout.baza_spinner_item, arrayListContract);
                spinner_contract.setAdapter(arrayAdapterContract);
            }
        }
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

            String android_id = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
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
            docPurch.comment = "comment";
            docPurch.contractGuid = arrayListContract.get(spinner_contract.getSelectedItemPosition()).getGuid();
            docPurch.date = editTextDate.getText().toString();
            docPurch.amount = Double.parseDouble(editTextSum.getText().toString());
            docPurch.docType = docType;
            docPurch.docId = docId;

            OperationStatus bl = blockingStub.createDoc(docPurch);

            if (bl.status != 0) {

                return null;
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
        protected void onPostExecute(Points pointIterator) {
            try {
                mChannel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (pointIterator == null) {
                saveDocument(false);
                alertDialog.setTitle("Ошибка");
                alertDialog.setMessage("Произошла ошибка, попробуйте еще раз.");
            } else {
                saveDocument(true);
                alertDialog.setTitle("Успех");
                alertDialog.setMessage("Документ выгружен =)");
                Toast.makeText(getApplicationContext(), "Успех! Документ выгружен. =)", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.INVISIBLE);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    private void saveDocument(boolean b) {
        Order order = new Order();

        // ID is timestamp
        order.setOrderID(docId);
        order.setBaza(CurrentBaseClass.getInstance().getCurrentBaseObject());
        order.setClient(clientGUID);
        order.setDate(editTextDate.getText().toString());
        order.setDoctype(Integer.toString(docType));
        order.setDogovor(arrayListContract.get(spinner_contract.getSelectedItemPosition()).getGuid());
        order.setDelivered(b);
        order.setOrganization(arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid());
        order.setTotalSum(Double.parseDouble(editTextSum.getText().toString()));
        OrdersRepo ordersRepo = new OrdersRepo();
        ordersRepo.delete(order);
        ordersRepo.insert(order);
        Toast.makeText(getApplicationContext(), "Сохранено", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //
        SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(), MODE_PRIVATE);
        String flag;

        flag = sharedPreferences.getString(UserSettings.create_sales_at_clients_coordinates, "false");

        if (flag.equals("true")) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimeDisplay(), 2000, 2000);
        }
    }
}
