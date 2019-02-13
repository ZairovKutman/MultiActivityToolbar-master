package kg.soulsb.ayu.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.repo.BazasRepo;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.models.Baza;
import kg.soulsb.ayu.singletons.CurrentBaseClass;

/**
 * Created by Sultanbek Baibagyshev on 1/19/17.
 */

public class SettingsBasesAddEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_bases_add_edit);

        Button saveButton = (Button) findViewById(R.id.save_button);
        Button cancelButton = (Button) findViewById(R.id.cancel_button);

        final EditText baseName = (EditText) findViewById(R.id.editText_base_name);
        final EditText baseIP = (EditText) findViewById(R.id.editText_ip);
        final EditText basePort = (EditText) findViewById(R.id.editText_port);
        final EditText baseAgent = (EditText) findViewById(R.id.editText_agent);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(baseName.getText()) || TextUtils.isEmpty(baseIP.getText()) || TextUtils.isEmpty(basePort.getText()))
                {
                    if (TextUtils.isEmpty(baseName.getText()))
                    {
                        Toast.makeText(v.getContext(), "Введите имя базы", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(baseIP.getText()))
                    {
                        Toast.makeText(v.getContext(), "Введите IP адрес", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(basePort.getText()))
                    {
                        Toast.makeText(v.getContext(), "Введите Порт", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(baseAgent.getText()))
                    {
                        Toast.makeText(v.getContext(), "Введите код агента", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                Intent intent = new Intent();

                BazasRepo bazasRepo = new BazasRepo();
                Baza baza = new Baza();
                baza.setPort(basePort.getText().toString());
                baza.setHost(baseIP.getText().toString());
                baza.setName(baseName.getText().toString());
                baza.setAgent(baseAgent.getText().toString());
                baza.setBazaId(Double.toString(System.currentTimeMillis()));

                bazasRepo.insert(baza);
                setResult(RESULT_OK,intent);
                CurrentBaseClass.getInstance().setCurrentBase(baza.getName());
                CurrentBaseClass.getInstance().setCurrentBaseObject(baza);
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
                editor1.putString("default_name",baza.getName());
                editor1.putString("default_host",baza.getHost());
                editor1.putString("default_port",Integer.toString(baza.getPort()));
                editor1.putString("default_agent",baza.getAgent());
                editor1.putString("default_bazaId",baza.getBazaId());
                editor1.apply();
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
