package kg.soulsb.ayu.activities.zakaz;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.helpers.DBHelper;
import kg.soulsb.ayu.helpers.DatabaseManager;
import kg.soulsb.ayu.helpers.repo.ContractsRepo;
import kg.soulsb.ayu.helpers.repo.WarehousesRepo;
import kg.soulsb.ayu.models.Contract;
import kg.soulsb.ayu.models.Warehouse;
import kg.soulsb.ayu.singletons.DataHolderClass;

import static android.app.Activity.RESULT_OK;

/**
 * Created by soulsb on 1/10/17.
 */

public class OthersFragment extends Fragment {

    EditText comments;
    TextView totalSumTextView;
    OrderAddActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.others_fragment, container, false);


        comments = (EditText) v.findViewById(R.id.editText_komment);

        if (parentActivity.order != null){

            if (parentActivity.isDelivered.equals("true"))
            {
                disableButtons();
            }
        }
        totalSumTextView = (TextView) v.findViewById(R.id.otherFragment_total_sum);

        updateTotalSum(parentActivity.totalSum);

        return v;
    }

    private void disableButtons() {
        comments.setEnabled(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = ((OrderAddActivity)getActivity());
    }

    public void updateTotalSum(double sum)
    {
        if (totalSumTextView != null)
            totalSumTextView.setText("Сумма документа: "+sum);
    }
}


