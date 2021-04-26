package kg.soulsb.ayu.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kg.soulsb.ayu.R;
import kg.soulsb.ayu.grpctest.nano.Agent;
import kg.soulsb.ayu.grpctest.nano.AyuServiceGrpc;
import kg.soulsb.ayu.grpctest.nano.Device;
import kg.soulsb.ayu.grpctest.nano.DeviceStatus;
import kg.soulsb.ayu.grpctest.nano.ItemImageInput;
import kg.soulsb.ayu.grpctest.nano.ItemImageOutput;
import kg.soulsb.ayu.grpctest.nano.OperationStatus;
import kg.soulsb.ayu.grpctest.nano.Point;
import kg.soulsb.ayu.grpctest.nano.PointLocation;
import kg.soulsb.ayu.grpctest.nano.Points;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.models.Item;
import kg.soulsb.ayu.models.PriceType;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class ItemDetailActivity extends BaseActivity {
    private AyuServiceGrpc.AyuServiceBlockingStub blockingStub;
    TextView itemName;
    TextView itemEd;
    ListView itemPriceList;
    ImageView itemImageView;
    Button buttonPhoto;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<PriceType> priceTypesArrayList;
    public Baza baza;
    Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        item = (Item) getIntent().getSerializableExtra("item");
        buttonPhoto = findViewById(R.id.button_show_photo);
        itemImageView = findViewById(R.id.imageView_item_photo);
        itemName = (TextView) findViewById(R.id.item_name);
        itemName.setText(item.getName());

        itemEd = (TextView) findViewById(R.id.item_ed);
        itemEd.setText("ед. измерения: "+item.getUnit());

        itemPriceList = (ListView) findViewById(R.id.item_price_list);

        priceTypesArrayList = new PriceTypesRepo().getPricetypesObject();


        for (PriceType priceType: priceTypesArrayList)
        {
            arrayList.add(""+priceType.getName()+": "+Double.toString(new PricesRepo().getItemPriceByPriceType(item.getGuid(),priceType.getGuid()))+" сом");
        }

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arrayList);
        itemPriceList.setAdapter(arrayAdapter);
        itemPriceList.setEnabled(false);

        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baza = CurrentBaseClass.getInstance().getCurrentBaseObject();


                String mHost = baza.getHost();
                int mPort = baza.getPort();

                ItemDetailActivity.GrpcTask grpcTask = new ItemDetailActivity.GrpcTask(ManagedChannelBuilder.forAddress(mHost,mPort)
                        .usePlaintext(true).build(), CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                grpcTask.executeOnExecutor(THREAD_POOL_EXECUTOR);
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




    private class GrpcTask extends AsyncTask<Void, Void, Bitmap> {
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

        /**
         * Метод отрабатывает код в фоновом режиме.
         *
         * @param nothing
         * @return
         */
        @Override
        protected Bitmap doInBackground(Void... nothing) {
            try {
                return getPhotoGRPC(mChannel);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private Bitmap getPhotoGRPC(ManagedChannel mChannel) {
            blockingStub = AyuServiceGrpc.newBlockingStub(mChannel);
            Agent request = new Agent();
            request.name = name;

            ItemImageInput itemImageInput = new ItemImageInput();
            itemImageInput.agentName = name;
            itemImageInput.itemGuid = item.getGuid();

            ItemImageOutput bl = blockingStub.getItemImage(itemImageInput);

            Bitmap bmp = BitmapFactory.decodeByteArray(bl.itemImage, 0, bl.itemImage.length);



            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            try {
                mChannel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (bmp == null)
            {
                Toast.makeText(getBaseContext(),"Фото нет в базе или нет доступа к интернету!",Toast.LENGTH_SHORT).show();
            }
            else {
                itemImageView.setImageBitmap(Bitmap.createBitmap(bmp));
                Toast.makeText(getBaseContext(),"Успешно!",Toast.LENGTH_SHORT).show();
            }
        }
    }
}