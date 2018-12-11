package kg.soulsb.ayu.models;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by soulsb on 4/3/18.
 */

public class SvodPayRow {

    public int getRowId() {
        return rowId;
    }

    int rowId;
    public ViewGroup container;
    public EditText clientEditText;

    public EditText sumEditText;

    private String clientGuid = "";
    private String dogovorGuid;

    double sum;

    public String getClientGuid() {
        return clientGuid;
    }

    public void setClientGuid(String clientGuid) {
        this.clientGuid = clientGuid;
    }

    public String getDogovorGuid() {
        return dogovorGuid;
    }

    public void setDogovorGuid(String dogovorGuid) {
        this.dogovorGuid = dogovorGuid;
    }

    public double getSum() {
        double x = 0;
        try {
            x=  Double.parseDouble(sumEditText.getText().toString());
        }
        catch (Exception e)
        {
            x = 0;
        }

        return x;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public SvodPayRow(int rowId,ViewGroup container, EditText clientEditText,String dogovorGuid, EditText sumEditText)
    {
        this.rowId = rowId;
        this.container = container;
        this.clientEditText = clientEditText;
        this.dogovorGuid = dogovorGuid;
        this.sumEditText = sumEditText;
    }



}
