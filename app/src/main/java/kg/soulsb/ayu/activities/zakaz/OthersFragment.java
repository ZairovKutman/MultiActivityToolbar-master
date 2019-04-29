package kg.soulsb.ayu.activities.zakaz;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import kg.soulsb.ayu.R;
import kg.soulsb.ayu.singletons.UserSettings;

/**
 * Created by soulsb on 1/10/17.
 */

public class OthersFragment extends Fragment {

    EditText comments;
    CheckBox bonusTT;
    TextView totalSumTextView;
    OrderAddActivity parentActivity;
    LinearLayout taskLayout;
    Spinner statusSpinner;
    Button pickImageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.others_fragment, container, false);


        comments = (EditText) v.findViewById(R.id.editText_komment);
        bonusTT = (CheckBox) v.findViewById(R.id.checkBoxTT);
        statusSpinner = v.findViewById(R.id.status_spinner);
        pickImageButton = v.findViewById(R.id.pick_image);

        if (parentActivity.order != null){

            if (parentActivity.isDelivered.equals("true"))
            {
                disableButtons();
            }
            comments.setText(parentActivity.order.getComment());
            bonusTT.setChecked(parentActivity.order.getCheckedBonusTT());
        }
        totalSumTextView = (TextView) v.findViewById(R.id.otherFragment_total_sum);

        updateTotalSum();


        if (!parentActivity.sharedPreferences.getString(UserSettings.workWithTasks,"false").equals("true"))
        {
            taskLayout = v.findViewById(R.id.tasksLayout);
            taskLayout.setVisibility(View.INVISIBLE);
        }

        ArrayList<String> statusArrayList = new ArrayList<String>();
        statusArrayList.add("Заказ принял");
        statusArrayList.add("Магазин закрыт");
        statusArrayList.add("Пропустил (нет заказа)");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(parentActivity,R.layout.baza_spinner_item,statusArrayList);
        statusSpinner.setAdapter(statusAdapter);

        return v;
    }

    private void disableButtons() {

        comments.setEnabled(false);
        bonusTT.setEnabled(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = ((OrderAddActivity)getActivity());
    }

    public void updateTotalSum()
    {
        if (totalSumTextView != null)
            totalSumTextView.setText("Сумма документа: "+parentActivity.calculateTotalSum());
    }
}


