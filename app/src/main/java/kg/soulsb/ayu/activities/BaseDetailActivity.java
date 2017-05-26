package kg.soulsb.ayu.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ClientsRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.ItemsRepo;
import kg.soulsb.ayu.helpers.repo.OrganizationsRepo;
import kg.soulsb.ayu.helpers.repo.PriceTypesRepo;
import kg.soulsb.ayu.helpers.repo.PricesRepo;
import kg.soulsb.ayu.helpers.repo.ReportsRepo;
import kg.soulsb.ayu.helpers.repo.SavedReportsRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.UserSettings;

public class BaseDetailActivity extends BaseActivity {

    TextView nameText;
    EditText ipText;
    EditText portText;
    EditText agentText;
    EditText bazaIdText;
    Button buttonDelete;
    Button buttonEdit;
    Button buttonSetDefault;
    boolean edited = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_details);

        nameText = (TextView) findViewById(R.id.bazaName);
        ipText = (EditText) findViewById(R.id.bazaHost);
        portText = (EditText) findViewById(R.id.bazaPort);
        agentText = (EditText) findViewById(R.id.bazaAgent);
        bazaIdText = (EditText) findViewById(R.id.bazaId);
        buttonDelete = (Button) findViewById(R.id.button_delete_baza);
        buttonSetDefault = (Button) findViewById(R.id.buttton_set_default);
        buttonEdit = (Button) findViewById(R.id.buttton_edit);

        ipText.setEnabled(false);
        agentText.setEnabled(false);
        portText.setEnabled(false);
        bazaIdText.setEnabled(false);

        nameText.setText(getIntent().getStringExtra("name"));
        ipText.setText(getIntent().getStringExtra("host"));
        portText.setText(getIntent().getStringExtra("port"));
        agentText.setText(getIntent().getStringExtra("agent"));
        bazaIdText.setText(getIntent().getStringExtra("bazaId"));

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ipText.isEnabled()) {
                    buttonEdit.setText("Изменить");
                    ipText.setEnabled(false);
                    agentText.setEnabled(false);
                    portText.setEnabled(false);
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    DatabaseManager.initializeInstance(dbHelper);
                    new BazasRepo().updateIpAndPortAndAgent(nameText.getText().toString(),ipText.getText().toString(), portText.getText().toString(),agentText.getText().toString(),getIntent().getStringExtra("bazaId"));
                    finish();
                }
                else {
                    buttonEdit.setText("Сохранить");

                    ipText.setEnabled(true);
                    portText.setEnabled(true);
                    agentText.setEnabled(true);

                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BazasRepo bazaRepo = new BazasRepo();
                bazaRepo.delete(new Baza(ipText.getText().toString(),Integer.parseInt(portText.getText().toString()),nameText.getText().toString(),agentText.getText().toString(), getIntent().getStringExtra("bazaId")));
                SharedPreferences sharedPreferences = getSharedPreferences(getIntent().getStringExtra("name"),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("using");
                editor.remove(UserSettings.can_create_orders);
                editor.remove(UserSettings.can_create_sales);
                editor.remove(UserSettings.password_for_app_settings);
                editor.remove(UserSettings.can_get_gpc_coordinates_of_clients);
                editor.remove(UserSettings.create_order_at_clients_coordinates);
                editor.remove(UserSettings.create_sales_at_clients_coordinates);
                editor.remove(UserSettings.forbit_select_client_with_outstanding_debt);
                editor.remove(UserSettings.force_daily_exchange);
                editor.remove(UserSettings.force_gps_turn_on);
                editor.remove(UserSettings.send_all_documents_with_exchange);
                editor.remove(UserSettings.status);
                editor.apply();

                ClientsRepo clientsRepo = new ClientsRepo();
                clientsRepo.deleteByBase(getIntent().getStringExtra("name"));

                new ContractsRepo().deleteByBase(getIntent().getStringExtra("name"));
                new ItemsRepo().deleteByBase(getIntent().getStringExtra("name"));
                new OrganizationsRepo().deleteByBase(getIntent().getStringExtra("name"));
                new PricesRepo().deleteByBase(getIntent().getStringExtra("name"));
                new PriceTypesRepo().deleteByBase(getIntent().getStringExtra("name"));
                new ReportsRepo().deleteByBase(getIntent().getStringExtra("name"));
                new SavedReportsRepo().deleteByBase(getIntent().getStringExtra("name"));

                finish();
            }
        });

        buttonSetDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrentBaseClass.getInstance().setCurrentBase(getIntent().getStringExtra("name"));
                CurrentBaseClass.getInstance().setCurrentBaseObject(new Baza(getIntent().getStringExtra("host"),Integer.parseInt(getIntent().getStringExtra("port")),getIntent().getStringExtra("name"),getIntent().getStringExtra("agent"), getIntent().getStringExtra("bazaId")));

                SharedPreferences sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("default_name", CurrentBaseClass.getInstance().getCurrentBaseObject().getName());
                editor.putString("default_host", CurrentBaseClass.getInstance().getCurrentBaseObject().getHost());
                editor.putString("default_port", Integer.toString(CurrentBaseClass.getInstance().getCurrentBaseObject().getPort()));
                editor.putString("default_agent", CurrentBaseClass.getInstance().getCurrentBaseObject().getAgent());
                editor.putString("default_bazaId", CurrentBaseClass.getInstance().getCurrentBaseObject().getBazaId());
                editor.apply();

                SharedPreferences sharedPreferences1 = getSharedPreferences("DefaultBase",MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences1.edit();
                editor1.putString("default_name",getIntent().getStringExtra("name"));
                editor1.putString("default_host",getIntent().getStringExtra("host"));
                editor1.putString("default_port",getIntent().getStringExtra("port"));
                editor1.putString("default_agent",getIntent().getStringExtra("agent"));
                editor1.putString("default_bazaId",getIntent().getStringExtra("bazaId"));
                editor1.apply();
                finish();
            }
        });

    }

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}
