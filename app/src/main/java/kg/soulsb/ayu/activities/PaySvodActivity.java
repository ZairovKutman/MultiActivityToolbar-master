package kg.soulsb.ayu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import kg.soulsb.ayu.grpctest.nano.ConsPaymentLine;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.DocPurch;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.PaymentLine;
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
import kg.soulsb.ayu.models.SvodPay;
import kg.soulsb.ayu.models.SvodPayRow;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;
import kg.soulsb.ayu.singletons.UserSettings;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class PaySvodActivity extends BaseActivity {

    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;

    DBHelper dbHelper;

    EditText editTextDate;

    Spinner editText_organization;

    Button createDocButton;
    Button saveDocButton;
    int rowId = 0;
    ArrayList<Organization> arrayListOrganization;
    ArrayList<Contract> arrayListContract = new ArrayList<>();
    ArrayList<SvodPay> svodPayArrayList = new ArrayList<>();
    ArrayList<SvodPayRow> svodPayRowArrayList = new ArrayList<>();
    ArrayAdapter<Contract> arrayAdapterContract;
    ArrayAdapter<Organization> organizationArrayAdapter;
    double itog = 0;
    private Timer mTimer = new Timer();
    Location mLastLocation;

    Location clientLocation = new Location("");
    Order order = null;
    String isDelivered = "false";

    int mYear, mMonth, mDay;
    int docType = 3;
    float distance = 0;
    TextView itogText;
    String clientGUID;
    String clientLat = "0";
    String clientLong = "0";
    Intent myIntent;
    int counter=0;
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
                System.out.println("DISTANCE = " + distance);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                System.out.println("requestcode = "+requestCode);
                String my_data = data.getStringExtra("data");
                clientGUID = data.getStringExtra("guid");
                clientLat = data.getStringExtra("lat");
                clientLong = data.getStringExtra("long");
                for (SvodPayRow svod: svodPayRowArrayList)
                {
                    System.out.println(svod.getRowId());

                    if (svod.getRowId() == requestCode)
                    {
                        svod.setClientGuid(clientGUID);
                        svod.clientEditText.setText(my_data);
                        DatabaseManager.initializeInstance(dbHelper);
                        arrayListContract = new ContractsRepo().getContractsObject(clientGUID);
                        if (arrayListContract.isEmpty()){
                            svod.setDogovorGuid("");
                        }
                        else
                        {
                            svod.setDogovorGuid(arrayListContract.get(0).getGuid());
                        }
                        svod.container.invalidate();
                        svod.sumEditText.requestFocus();
                    }
                }
            }
    }

    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    private void calculateItog() {
        itog = 0;
        for (SvodPayRow svod: svodPayRowArrayList)
        {
            itog = itog + svod.getSum();
        }
        itogText.setText("Итого по документу: " +itog+" сом");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_svod);

        arrayAdapterContract = new ArrayAdapter<>(this, R.layout.baza_spinner_item, arrayListContract);


        if (docId == null) {
            Long tsLong = System.currentTimeMillis() / 1000;
            docId = tsLong.toString();
        }

        if (getIntent().getStringExtra("isDelivered")!=null)
        {
            isDelivered = getIntent().getStringExtra("isDelivered");
        }

        dbHelper = new DBHelper(this);

        editText_organization = (Spinner) findViewById(R.id.pay_spinner_organization);
        arrayListOrganization = new OrganizationsRepo().getOrganizationsObject();
        organizationArrayAdapter = new ArrayAdapter<>(this, R.layout.baza_spinner_item, arrayListOrganization);
        editText_organization.setAdapter(organizationArrayAdapter);
        itogText = (TextView) findViewById(R.id.textView_itog);
        itogText.setText("Итого по документу: " +itog+" сом");
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


        createDocButton = (Button) findViewById(R.id.btn_pay_create_doc);
        saveDocButton = (Button) findViewById(R.id.save_pay_button);

        Button addRowButton = (Button) findViewById(R.id.Button01);

        addRowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateItog();
                for (SvodPayRow svod: svodPayRowArrayList)
                {
                    if (svod.getClientGuid().equals("") || svod.getSum() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Не выбран клиент или не указана сумма.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                rowId = rowId + 1;
                TableLayout tl=(TableLayout)findViewById(R.id.TableLayout01);
                TableRow tr=new TableRow(PaySvodActivity.this);

                final EditText clientEditText = new EditText(PaySvodActivity.this);
                tr.addView(clientEditText);
                clientEditText.setText("");
                clientEditText.setFocusable(false);
                clientEditText.setMinEms(10);
                myIntent = new Intent(getApplicationContext(), ChooseClientTableActivity.class);
                clientEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("clicked");
                        for (SvodPayRow svod: svodPayRowArrayList)
                        {
                            if (svod.clientEditText.equals(v))
                            {
                                startActivityForResult(myIntent, svod.getRowId());
                                break;
                            }
                        }

                    }
                });

                clientEditText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        d = new AlertDialog.Builder(PaySvodActivity.this);
                        final View v1 = v;
                        d.setTitle("Подтвердите удаление");
                        d.setMessage("Вы действительно хотите удалить cтроку?");

                        d.setCancelable(false);
                        alertDialog = d.create();
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                View row = (View) v1;

                                for (SvodPayRow svod: svodPayRowArrayList)
                                {
                                    if (svod.clientEditText.equals(row))
                                    {
                                        svod.container.removeView((View)row.getParent());
                                        svod.container.invalidate();
                                        svodPayRowArrayList.remove(svod);
                                        break;
                                    }
                                }

                                calculateItog();


                            }
                        });

                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("yo no!");
                            }
                        });

                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialog) {
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
                            }
                        });
                        alertDialog.show();
                        return true;
                    }
                });

                final EditText SumEditText = new EditText(PaySvodActivity.this);
                SumEditText.setMinEms(5);
                SumEditText.setText("");
                SumEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
                SumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                //v.clearFocus();
                                // the user is done typing.
                                calculateItog();
                                return false; // consume.
                            }
                        }

                        return false;
                    }
                });


                SumEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            hideKeyboard((Activity) v.getContext());
                        } else {
                            showKeyboard((Activity) v.getContext());
                        }
                    }
                });

                SumEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                tr.addView(SumEditText);


                tl.addView(tr,new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

                svodPayRowArrayList.add(new SvodPayRow(rowId,tl, clientEditText,"",SumEditText));
                clientEditText.performClick();
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
                d = new AlertDialog.Builder(PaySvodActivity.this);
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

        DatabaseManager.initializeInstance(dbHelper);

        for (Organization organization: arrayListOrganization)
        {
            if (organization.getGuid().equals(order1.getOrganization()))
            {
                editText_organization.setSelection(arrayListOrganization.indexOf(organization));
                break;
            }
        }
        isDelivered = String.valueOf(order1.isDelivered());
        System.out.println(isDelivered);



        TableLayout tl=(TableLayout)findViewById(R.id.TableLayout01);

        for (SvodPay svodPay: order1.getArraylistSvodPay()) {
            TableRow tr = new TableRow(PaySvodActivity.this);
            rowId = rowId + 1;
            final EditText clientEditText = new EditText(PaySvodActivity.this);
            tr.addView(clientEditText);
            Client client = new ClientsRepo().getClientObjectByGuid(svodPay.getClient());
            clientEditText.setText(client.getName());
            clientEditText.setFocusable(false);
            clientEditText.setMinEms(10);
            myIntent = new Intent(getApplicationContext(), ChooseClientTableActivity.class);
            clientEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("clicked");
                    for (SvodPayRow svod : svodPayRowArrayList) {
                        if (svod.clientEditText.equals(v)) {
                            startActivityForResult(myIntent, svod.getRowId());
                            break;
                        }
                    }

                }
            });

            clientEditText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    d = new AlertDialog.Builder(PaySvodActivity.this);
                    final View v1 = v;
                    d.setTitle("Подтвердите удаление");
                    d.setMessage("Вы действительно хотите удалить cтроку?");

                    d.setCancelable(false);
                    alertDialog = d.create();
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            View row = (View) v1;

                            for (SvodPayRow svod : svodPayRowArrayList) {
                                if (svod.clientEditText.equals(row)) {
                                    svod.container.removeView((View) row.getParent());
                                    svod.container.invalidate();
                                    svodPayRowArrayList.remove(svod);
                                    break;
                                }
                            }

                            calculateItog();


                        }
                    });

                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("yo no!");
                        }
                    });

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
                        }
                    });
                    alertDialog.show();
                    return true;
                }
            });

            final EditText SumEditText = new EditText(PaySvodActivity.this);
            SumEditText.setMinEms(5);
            SumEditText.setText(String.valueOf(svodPay.getSum()));
            SumEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            SumEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                            actionId == EditorInfo.IME_ACTION_DONE ||
                            event != null &&
                                    event.getAction() == KeyEvent.ACTION_DOWN &&
                                    event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (event == null || !event.isShiftPressed()) {
                            //v.clearFocus();
                            // the user is done typing.
                            calculateItog();
                            return false; // consume.
                        }
                    }

                    return false;
                }
            });
            SumEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            tr.addView(SumEditText);

            if (order1.isDelivered())
            {
                SumEditText.setEnabled(false);
                clientEditText.setEnabled(false);
                tr.setEnabled(false);
                SumEditText.setFocusable(false);
            }

            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            SvodPayRow svodPayRow = new SvodPayRow(rowId,tl, clientEditText,svodPay.getDogovor(),SumEditText);
            svodPayRow.setClientGuid(svodPay.getClient());

            svodPayRowArrayList.add(svodPayRow);
        }








        docId = order1.getOrderID();
        if (order1.isDelivered()) {
            disableButtons();
            isDelivered = "true";
        }
        tl.invalidate();
        calculateItog();
    }


    @Override
    public void onBackPressed()
    {
        createDialog();
    }

    private void createDialog() {
        if (isDelivered.equals("true"))
        {
            PaySvodActivity.super.onBackPressed();
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
                    PaySvodActivity.super.onBackPressed();
                }
            }
        });

        alertDlg.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PaySvodActivity.super.onBackPressed();
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

        if ((editText_organization.getSelectedItem() == null) || editText_organization == null) {
            Toast.makeText(getApplicationContext(), "Не заполнено поле Организация", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (svodPayRowArrayList.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Не заполнена табличная часть", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void disableButtons() {
        Toast.makeText(PaySvodActivity.this,"Документ выгружен, редактрование запрещено.",Toast.LENGTH_SHORT).show();

          Button addRowButton = (Button) findViewById(R.id.Button01);
          addRowButton.setEnabled(false);
          editTextDate.setEnabled(false);
          editText_organization.setEnabled(false);
          createDocButton.setEnabled(false);
          saveDocButton.setEnabled(false);

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

            if (!deviceStatus.active) {
                return null;
            }

            DocPurch docPurch = new DocPurch();
            docPurch.organizationGuid = arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid();
            docPurch.agent = name;
            docPurch.comment = "Аю-Агент сводная оплата";
            docPurch.date = editTextDate.getText().toString();
            docPurch.docType = docType;
            docPurch.docId = docId;
            ConsPaymentLine[] arrayPayments = new ConsPaymentLine[svodPayRowArrayList.size()];
            int i =0;

            for (SvodPayRow svodPay: svodPayRowArrayList) {
                if (svodPay.getClientGuid().equals("")) {continue;}
                ConsPaymentLine consPaymentLine = new ConsPaymentLine();
                consPaymentLine.clientGuid = svodPay.getClientGuid();
                consPaymentLine.contractGuid = svodPay.getDogovorGuid();
                consPaymentLine.amount = svodPay.getSum();
                arrayPayments[i] = consPaymentLine;
                i = i +1;
            }

            docPurch.payments = arrayPayments;

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
        order.setDate(editTextDate.getText().toString());
        order.setDoctype(Integer.toString(docType));
        order.setDelivered(b);
        order.setOrganization(arrayListOrganization.get(editText_organization.getSelectedItemPosition()).getGuid());
        order.setTotalSum(itog);
        svodPayArrayList.clear();

        for (SvodPayRow svod: svodPayRowArrayList)
        {
            if (svod.getClientGuid().equals("")) {continue;}

            svodPayArrayList.add(new SvodPay(svod.getClientGuid(),svod.getDogovorGuid(),svod.getSum()));
        }

        order.setArraylistSvodPay(svodPayArrayList);
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
