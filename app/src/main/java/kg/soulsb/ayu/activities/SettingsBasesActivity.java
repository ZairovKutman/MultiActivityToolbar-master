package kg.soulsb.ayu.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.activities.zakaz.ChooseClientTableActivity;
import kg.soulsb.ayu.adapters.BazaAdapter;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.singletons.CurrentBaseClass;
import kg.soulsb.ayu.singletons.UserSettings;

import static kg.soulsb.ayu.activities.zakaz.AddOrderFragment.REQUEST_CODE;

/**
 * Created by Sultanbek Baibagyshev on 1/19/17.
 */

public class SettingsBasesActivity extends BaseActivity {
    ArrayList<Baza> arrayList = new ArrayList<>();
    Intent dateintent;
    ListView listView;
    BazaAdapter arrayAdapter;
    Button addButton;
    String currentBaseString;
    AlertDialog.Builder d;
    AlertDialog alertDialog;
    EditText editTextPassword;
    SharedPreferences sharedPreferences;

    @Override
    protected boolean useDrawerToggle() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_bases);
        dateintent = new Intent(this, SettingsBasesAddEditActivity.class);
        listView = (ListView) findViewById(R.id.listView_bases);


        sharedPreferences = getSharedPreferences(CurrentBaseClass.getInstance().getCurrentBase(),MODE_PRIVATE);
        if (sharedPreferences.contains("default_name")) {
            currentBaseString = sharedPreferences.getString("default_name", null);
            System.out.println(sharedPreferences.getString(UserSettings.password_for_app_settings,"0000"));
            if (!sharedPreferences.getString(UserSettings.password_for_app_settings,"0000").equals("0000"));
            {
                d = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.password_menu, null);
                editTextPassword = (EditText) dialogView.findViewById(R.id.editText_password);
                d.setTitle("Страница защищена");
                d.setMessage("Введите пароль:");
                d.setView(dialogView);
                d.setCancelable(false);
                d.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!sharedPreferences.getString(UserSettings.password_for_app_settings,"0000").equals(editTextPassword.getText().toString()))
                        {
                            finish();
                        }
                    }
                });

                d.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     finish();
                    }
                });

                alertDialog = d.create();
                alertDialog.show();
            }

        }

        updateListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Baza str = arrayList.get(position);
                Intent intent = new Intent(getBaseContext(), BaseDetailActivity.class);

                intent.putExtra("name", str.getName());
                intent.putExtra("host", str.getHost());
                intent.putExtra("agent", str.getAgent());
                intent.putExtra("port", Integer.toString(str.getPort()));
                intent.putExtra("bazaId", str.getBazaId());
                startActivity(intent);
            }
        });

        addButton = (Button) findViewById(R.id.button_add_base);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(dateintent, REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
            onBackPressed();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
            }
            if (resultCode == RESULT_OK)
            {
                // TODO: проверить поведение Навигации и название базы
                updateListView();
                setUpNavView();
            }
        }
    }

    private void updateListView() {
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        DatabaseManager.initializeInstance(dbHelper);
        arrayList = new BazasRepo().getBazasObject();
        arrayAdapter = new BazaAdapter(this,R.layout.baza_layout_adapter,arrayList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateListView();
    }

    @Override
    public void onBackPressed()
    {
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
