package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.OrderAddActivity;
import kg.soulsb.ayu.adapters.OrderAdapter;

import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.DailyTasksRepo;
import kg.soulsb.ayu.helpers.repo.OrdersRepo;

import kg.soulsb.ayu.helpers.repo.PhotosRepo;
import kg.soulsb.ayu.models.Client;
import kg.soulsb.ayu.models.DailyPhoto;
import kg.soulsb.ayu.models.Order;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.CurrentLocationClass;

public class TasksDetailActivity extends BaseActivity {

    TextView clientTextView, taskDetailTextView;
    Button newOrderButton, newPhotoButton, newClosedPhotoButton;
    ListView listViewDocuments;
    ArrayList<Order> orderArrayList;
    ArrayList<String> photosList = new ArrayList<>();
    ArrayAdapter<Order> arrayAdapter;
    AlertDialog.Builder d;
    AlertDialog alertDialog;
    String status;
    String priority;
    Client client;
    ArrayList<DailyPhoto> dailyPhotosArraylist = new ArrayList<>();
    LinearLayout linearLayout;
    ArrayList<DailyPhoto> dailyPhotosArrayList = new ArrayList<>();
    Bitmap myBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_detail);

        linearLayout = findViewById(R.id.gv);
        clientTextView = findViewById(R.id.textView_task_client);
        taskDetailTextView = findViewById(R.id.textView_task_details);
        newOrderButton = findViewById(R.id.new_order_button);
        newPhotoButton = findViewById(R.id.new_photo_button);
        newClosedPhotoButton = findViewById(R.id.new_closed_photo_button);

        client = new ClientsRepo().getClientObjectByGuid(getIntent().getStringExtra("clientGuid"));
        clientTextView.setText(client.getName());


        priority = Integer.toString(getIntent().getIntExtra("priority",-1));
        status = getIntent().getStringExtra("status");

        taskDetailTextView.setText("Задание №"+priority+", Статус = "+status);

        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(TasksDetailActivity.this, OrderAddActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("doctype", "0");
                    intent.putExtra("isTask", "true");
                    intent.putExtra("clientGuid", client.getGuid());
                    intent.putExtra("clientName", client.getName());
                    intent.putExtra("clientLat", client.getLatitude());
                    intent.putExtra("clientLon", client.getLongitude());
                    intent.putExtra("priority", priority);

                    startActivity(intent);
            }
        });

        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyPhotosArrayList = new PhotosRepo().getPhotosByClientGuid(client.getGuid());
                if (dailyPhotosArrayList.size()>5)
                {
                    Toast.makeText(TasksDetailActivity.this,"Нельзя добавить больше 5 фотографий",Toast.LENGTH_LONG).show();
                    return;
                }
                //new ImagePicker.Builder(TasksDetailActivity.this)
                //        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                //        .compressLevel(ImagePicker.ComperesLevel.HARD)
                //        .directory(ImagePicker.Directory.DEFAULT)
                //        .extension(ImagePicker.Extension.JPG)
                //        .allowMultipleImages(false)
                //        .enableDebuggingMode(true)
                //       .build();

                ImagePicker.create(TasksDetailActivity.this)
                        .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                        .toolbarImageTitle("Выберите фото") // image selection title
                        .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                        .single() // single mode
                        .start();

            }
        });


        orderArrayList = new OrdersRepo().getOrdersObjectByClientGuid(CurrentBaseClass.getInstance().getCurrentBase(),client.getGuid());
        arrayAdapter = new OrderAdapter(this,R.layout.list_docs_layout, orderArrayList);

        listViewDocuments = (ListView) findViewById(R.id.list_view_docs);
        listViewDocuments.setAdapter(arrayAdapter);
        TextView empty_1 = findViewById(R.id.empty_1);
        listViewDocuments.setEmptyView(empty_1);

        listViewDocuments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    Intent intent = new Intent(TasksDetailActivity.this, OrderAddActivity.class);
                    intent.putExtra("doctype", orderArrayList.get(i).getDoctype());
                    intent.putExtra("savedobj", orderArrayList.get(i));
                    if (orderArrayList.get(i).isDelivered()) {
                        intent.putExtra("isDelivered", "true");
                    }

                    intent.putExtra("isTask", "true");
                    intent.putExtra("clientGuid", client.getGuid());
                    intent.putExtra("clientName",client.getName());
                    intent.putExtra("clientLat",client.getLatitude());
                    intent.putExtra("clientLon",client.getLongitude());
                    intent.putExtra("priority",priority);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }

        });

        listViewDocuments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                d = new AlertDialog.Builder(TasksDetailActivity.this);

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

                    }
                });

                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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

        populateImages();
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);

            myBitmap = BitmapFactory.decodeFile(image.getPath());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] imageInByte = baos.toByteArray();
            DailyPhoto dailyPhoto = new DailyPhoto(client.getGuid(), imageInByte);
            new PhotosRepo().insert(dailyPhoto);

            if (orderArrayList.isEmpty())
            {
                Calendar myCalendar = Calendar.getInstance();
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                myCalendar.setTimeInMillis(myCalendar.getTimeInMillis());
                new DailyTasksRepo().updateStatusByPhoto(client.getGuid(), CurrentLocationClass.getInstance().getCurrentLocation().getLatitude(), CurrentLocationClass.getInstance().getCurrentLocation().getLongitude(), sdf.format(myCalendar.getTime()), "3");
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        orderArrayList = new OrdersRepo().getOrdersObjectByClientGuid(CurrentBaseClass.getInstance().getCurrentBase(),client.getGuid());
        arrayAdapter.notifyDataSetChanged();
        populateImages();
        if (status.equals("1")) {
            taskDetailTextView.setText("Задание №"+priority+", Статус = Заказ принят");
        }
        else if (status.equals("2")) {
            taskDetailTextView.setText("Задание №"+priority+", Статус = Магазин закрыт (фото)");
        }
        else if (status.equals("3")) {
            taskDetailTextView.setText("Задание №"+priority+", Статус = Пропустил");
        }
        else if (status.equals("0")) {
            taskDetailTextView.setText("Задание №"+priority+", Статус = Не выполнен");
        }

    }

    private void populateImages() {
        linearLayout.removeAllViews();
        dailyPhotosArrayList = new PhotosRepo().getPhotosByClientGuid(client.getGuid());
        int i = 0;
        for (DailyPhoto dailyPhoto1: dailyPhotosArrayList) {

            ImageView iv = new ImageView(TasksDetailActivity.this);

            Bitmap bmp = BitmapFactory.decodeByteArray(dailyPhoto1.getPhotoBytes(), 0, dailyPhoto1.getPhotoBytes().length);

            iv.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.getWidth(),
                    bmp.getHeight(), false));

            iv.setMaxWidth(100);
            iv.setMaxHeight(100);
            ViewGroup.MarginLayoutParams imageViewParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT,
                    ViewGroup.MarginLayoutParams.WRAP_CONTENT);
            imageViewParams.height = 100;
            imageViewParams.width = 100;
            imageViewParams.rightMargin = 100;
            iv.setLayoutParams(imageViewParams);

            linearLayout.addView(iv);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setId(i);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d = new AlertDialog.Builder(TasksDetailActivity.this);

                    d.setTitle("Подтвердите удаление");
                    d.setMessage("Вы действительно хотите удалить данное фото?");

                    d.setCancelable(false);
                    alertDialog = d.create();
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new PhotosRepo().deleteDailyPhoto(dailyPhotosArrayList.get(iv.getId()).getPhotoBytes());
                            linearLayout.removeView(v);

                        }
                    });

                    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.RED);
                        }
                    });
                    alertDialog.show();


                }
            });
            i=i+1;
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




}
