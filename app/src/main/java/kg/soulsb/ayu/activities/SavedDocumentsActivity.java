package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.OrderAdapter;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

public class SavedDocumentsActivity extends BaseActivity {
    AlertDialog.Builder d;
    ListView listViewDocuments;
    ArrayList<Order> orderArrayList;
    ArrayAdapter<Order> arrayAdapter;
    Spinner otborSpinner;
    AlertDialog alertDialog;
    TextView totalSumDelivered_textView;
    TextView totalSumNotDelivered_textView;
    TextView totalSum_textView;
    double totalSumDelivered = 0;
    double totalSumNotDelivered = 0;
    double totalSum = 0;

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_documents);
        totalSumDelivered_textView = (TextView) findViewById(R.id.totalDelivered_listview);
        totalSumNotDelivered_textView = (TextView) findViewById(R.id.totalNotDelivered_listview);
        totalSum_textView = (TextView) findViewById(R.id.total_listview);

        // Создаем отбор
        otborSpinner = (Spinner) findViewById(R.id.spinner_otbor);
        ArrayList<String> otborArrayList = new ArrayList<String>();
        otborArrayList.add("Все документы");
        otborArrayList.add("Выгруженные документы");
        otborArrayList.add("Невыгруженные документы");
        otborArrayList.add("Заказы");
        otborArrayList.add("Продажи");
        otborArrayList.add("Оплаты");
        final ArrayAdapter<String> otborAdapter = new ArrayAdapter<String>(this,R.layout.baza_spinner_item,otborArrayList);
        otborSpinner.setAdapter(otborAdapter);

        otborSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showContentUpdates(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        orderArrayList = new OrdersRepo().getOrdersObject(CurrentBaseClass.getInstance().getCurrentBase());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, orderArrayList);

        listViewDocuments = (ListView) findViewById(R.id.list_view_documents);
        listViewDocuments.setAdapter(arrayAdapter);
        listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (!orderArrayList.get(i).getDoctype().equals("2")) {
                    Intent intent = new Intent(SavedDocumentsActivity.this, OrderAddActivity.class);
                    intent.putExtra("doctype", orderArrayList.get(i).getDoctype());
                    intent.putExtra("savedobj", orderArrayList.get(i));
                    if (orderArrayList.get(i).isDelivered()) {
                        intent.putExtra("isDelivered", "true");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(SavedDocumentsActivity.this, PayActivity.class);
                    intent.putExtra("doctype", orderArrayList.get(i).getDoctype());
                    intent.putExtra("savedobj", orderArrayList.get(i));
                    if (orderArrayList.get(i).isDelivered()) {
                        intent.putExtra("isDelivered", "true");
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });

        listViewDocuments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                d = new AlertDialog.Builder(SavedDocumentsActivity.this);

                d.setTitle("Подтвердите удаление");
                d.setMessage("Вы действительно хотите удалить данный документ?");

                d.setCancelable(false);
                alertDialog = d.create();
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        OrdersRepo ordersRepo = new OrdersRepo();
                        ordersRepo.delete(orderArrayList.get(position));
                        orderArrayList.remove(position);
                        arrayAdapter.notifyDataSetChanged();
                        showContentUpdates(otborSpinner.getSelectedItemPosition());

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

    }

    private void showContentUpdates(int position) {
        totalSum = 0;
        totalSumDelivered = 0;
        totalSumNotDelivered = 0;
        totalSumDelivered_textView.setVisibility(View.VISIBLE);
        totalSumNotDelivered_textView.setVisibility(View.VISIBLE);
        if (otborSpinner.getItemAtPosition(position).equals("Выгруженные документы"))
        {
            ArrayList<Order> arrayList2 = new ArrayList<>();
            for (Order item: orderArrayList)
            {
                if (item.isDelivered())
                {
                    arrayList2.add(item);
                    totalSum = totalSum + item.getTotalSum();
                }
            }
            arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this,R.layout.list_docs_layout, arrayList2);
            listViewDocuments.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            totalSum_textView.setText("Итого: "+totalSum);
            totalSumDelivered_textView.setVisibility(View.INVISIBLE);
            totalSumNotDelivered_textView.setVisibility(View.INVISIBLE);
        }
        else if (otborSpinner.getItemAtPosition(position).equals("Невыгруженные документы"))
        {
            ArrayList<Order> arrayList2 = new ArrayList<>();
            for (Order item: orderArrayList)
            {

                if (!item.isDelivered())
                {
                    arrayList2.add(item);
                    totalSum = totalSum + item.getTotalSum();
                }
            }
            arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this,R.layout.list_docs_layout, arrayList2);
            listViewDocuments.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            totalSumDelivered_textView.setVisibility(View.INVISIBLE);
            totalSumNotDelivered_textView.setVisibility(View.INVISIBLE);
            totalSum_textView.setText("Итого: "+totalSum);
        }
        else if (otborSpinner.getItemAtPosition(position).equals("Заказы")) {
            ArrayList<Order> arrayList2 = new ArrayList<>();
            for (Order item : orderArrayList) {
                if (item.getDoctype().equals("0")) {
                    arrayList2.add(item);

                    totalSum = totalSum + item.getTotalSum();
                    if (item.isDelivered())
                    {
                        totalSumDelivered = totalSumDelivered + item.getTotalSum();
                    }
                    else
                    {
                        totalSumNotDelivered = totalSumNotDelivered + item.getTotalSum();
                    }
                }
            }
            arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this, R.layout.list_docs_layout, arrayList2);
            listViewDocuments.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            totalSumDelivered_textView.setVisibility(View.VISIBLE);
            totalSumNotDelivered_textView.setVisibility(View.VISIBLE);
            totalSumDelivered_textView.setText("Итого выгруженных: "+totalSumDelivered);
            totalSumNotDelivered_textView.setText("Итого невыгруженных: "+totalSumNotDelivered);
            totalSum_textView.setText("Общий итог: "+totalSum);
        }
        else if (otborSpinner.getItemAtPosition(position).equals("Продажи")) {
            ArrayList<Order> arrayList2 = new ArrayList<>();
            for (Order item : orderArrayList) {
                if (item.getDoctype().equals("1")) {
                    arrayList2.add(item);
                    totalSum = totalSum + item.getTotalSum();
                    if (item.isDelivered())
                    {
                        totalSumDelivered = totalSumDelivered + item.getTotalSum();
                    }
                    else
                    {
                        totalSumNotDelivered = totalSumNotDelivered + item.getTotalSum();
                    }
                }
            }
            arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this, R.layout.list_docs_layout, arrayList2);
            listViewDocuments.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            totalSumDelivered_textView.setVisibility(View.VISIBLE);
            totalSumNotDelivered_textView.setVisibility(View.VISIBLE);
            totalSumDelivered_textView.setText("Итого выгруженных: "+totalSumDelivered);
            totalSumNotDelivered_textView.setText("Итого невыгруженных: "+totalSumNotDelivered);
            totalSum_textView.setText("Общий итог: "+totalSum);
        }
        else if (otborSpinner.getItemAtPosition(position).equals("Оплаты")) {
            ArrayList<Order> arrayList2 = new ArrayList<>();
            for (Order item : orderArrayList) {
                if (item.getDoctype().equals("2")) {
                    arrayList2.add(item);
                    totalSum = totalSum + item.getTotalSum();
                    if (item.isDelivered())
                    {
                        totalSumDelivered = totalSumDelivered + item.getTotalSum();
                    }
                    else
                    {
                        totalSumNotDelivered = totalSumNotDelivered + item.getTotalSum();
                    }
                }
            }
            arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this, R.layout.list_docs_layout, arrayList2);
            listViewDocuments.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            totalSumDelivered_textView.setVisibility(View.VISIBLE);
            totalSumNotDelivered_textView.setVisibility(View.VISIBLE);
            totalSumDelivered_textView.setText("Итого выгруженных: "+totalSumDelivered);
            totalSumNotDelivered_textView.setText("Итого невыгруженных: "+totalSumNotDelivered);
            totalSum_textView.setText("Общий итог: "+totalSum);
        }
        else
        {
            for (Order item: orderArrayList)
            {
                totalSum = totalSum + item.getTotalSum();
                if (item.isDelivered())
                {
                    totalSumDelivered = totalSumDelivered + item.getTotalSum();
                }
                else
                {
                    totalSumNotDelivered = totalSumNotDelivered + item.getTotalSum();
                }
            }

            arrayAdapter = new OrderAdapter(SavedDocumentsActivity.this,R.layout.list_docs_layout, orderArrayList);
            listViewDocuments.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
            totalSumDelivered_textView.setVisibility(View.VISIBLE);
            totalSumNotDelivered_textView.setVisibility(View.VISIBLE);
            totalSumDelivered_textView.setText("Итого выгруженных: "+totalSumDelivered);
            totalSumNotDelivered_textView.setText("Итого невыгруженных: "+totalSumNotDelivered);
            totalSum_textView.setText("Общий итог: "+totalSum);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderArrayList = new OrdersRepo().getOrdersObject(CurrentBaseClass.getInstance().getCurrentBase());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, orderArrayList);
        arrayAdapter.notifyDataSetChanged();
    }
}
