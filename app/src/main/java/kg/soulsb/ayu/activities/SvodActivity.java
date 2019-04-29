package kg.soulsb.ayu.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.adapters.SvodAdapter;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.helpers.repo.UnitsRepo;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.models.SvodRow;
import kg.soulsb.ayu.models.Unit;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

public class SvodActivity extends BaseActivity {
    EditText editTextDate;
    TextView itogText, dateText;
    int mYear, mMonth, mDay;
    private List<SvodRow> svodRowList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SvodAdapter mAdapter;
    String formattedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svod);
        editTextDate = (EditText) findViewById(R.id.svod_date);
        itogText = (TextView) findViewById(R.id.textView_itog);
        dateText = (TextView) findViewById(R.id.textView_date);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,1);

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        formattedDate = df.format(c.getTime());

        editTextDate.setText(formattedDate);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                mYear = mcurrentDate.get(Calendar.YEAR);
                mMonth = mcurrentDate.get(Calendar.MONTH);
                mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(SvodActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        Calendar myCalendar = Calendar.getInstance();
                        myCalendar.set(Calendar.YEAR, selectedyear);
                        myCalendar.set(Calendar.MONTH, selectedmonth);
                        myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                        String myFormat = "dd/MM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                        formattedDate = sdf.format(myCalendar.getTime());
                        editTextDate.setText(formattedDate);

                        mDay = selectedday;
                        mMonth = selectedmonth;
                        mYear = selectedyear;
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Выберите дату");
                mDatePicker.show();
            }
        });

        Button buttonSvod = (Button) findViewById(R.id.button_svod);

        buttonSvod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTable();
            }
        });
    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.nav)
//            return true;

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
    public void createTable()
    {
        double upakovka = 0;
        double shtuk = 0;
        double summa = 0;
        svodRowList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ArrayList<Order> orderArrayList = new OrdersRepo().getOrdersObject(CurrentBaseClass.getInstance().getCurrentBase());
        for (Order order: orderArrayList)
        {
            String dateSend = order.getDateSend();
            String dateSvod = editTextDate.getText().toString();

            if (dateSend.equals(dateSvod)) {
                for (Item item: order.getArraylistTovar())
                {
                    ArrayList<Unit> myUnits = new UnitsRepo().getUnitsObjectByItemGuid(item.getGuid());
                    Unit myUnit = item.getMyUnit();
                    for (Unit unit: myUnits)
                    {
                        if (unit.getCoefficient() > myUnit.getCoefficient())
                        {
                            myUnit = unit;
                        }

                    }

                    SvodRow rowToAdd = new SvodRow(item.getGuid(), item.getName(),myUnit,item.getQuantity()*item.getMyUnit().getCoefficient(),item.getPrice());

                    boolean flag = false;

                    for (SvodRow svod: svodRowList)
                    {
                        if (svod.equals(rowToAdd))
                        {
                            svod.setQuantity(svod.getQuantity() + rowToAdd.getQuantity());

                            summa = summa + item.getQuantity()*item.getMyUnit().getCoefficient() * item.getPrice();
                            upakovka = upakovka + item.getQuantity()*(int)item.getMyUnit().getCoefficient()/(int)myUnit.getCoefficient();
                            shtuk = shtuk + item.getQuantity()*(int)item.getMyUnit().getCoefficient() % (int)myUnit.getCoefficient();
                            flag = true;
                            break;
                        }
                    }

                    if (!flag) {
                        svodRowList.add(rowToAdd);

                        summa = summa + item.getQuantity() * item.getMyUnit().getCoefficient() * item.getPrice();
                        upakovka = upakovka + item.getQuantity() * (int) item.getMyUnit().getCoefficient() / (int) myUnit.getCoefficient();
                        shtuk = shtuk + item.getQuantity() * (int) item.getMyUnit().getCoefficient() % (int) myUnit.getCoefficient();
                    }
                }
            }
        }

        mAdapter = new SvodAdapter(svodRowList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        dateText.setText("Свод заявок на дату: "+formattedDate+"г.");
        itogText.setText("Итого: "+upakovka+"уп. + "+shtuk+"шт. на сумму: "+summa+"сом.");

    }
}
