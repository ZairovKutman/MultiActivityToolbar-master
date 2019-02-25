package kg.soulsb.ayu.activities.zakaz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import kg.soulsb.ayu.R;

/**
 * Created by soulsb on 1/10/17.
 */

public class OthersFragment extends Fragment {

    EditText comments;
    CheckBox bonusTT;
    TextView totalSumTextView;
    OrderAddActivity parentActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.others_fragment, container, false);


        comments = (EditText) v.findViewById(R.id.editText_komment);
        bonusTT = (CheckBox) v.findViewById(R.id.checkBoxTT);

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


