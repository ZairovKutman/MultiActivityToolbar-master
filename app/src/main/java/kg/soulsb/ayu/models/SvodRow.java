package kg.soulsb.ayu.models;

/**
 * Created by soulsb on 10/19/17.
 */

public class SvodRow {
    private String name;
    private Unit myUnit;
    private double quantity;
    private double price;
    private String guid;

    public SvodRow(String guid, String name, Unit myUnit, double quantity, double price) {

        this.name = name;
        this.myUnit = myUnit;
        this.quantity = quantity;
        this.price = price;
        this.guid = guid;
    }


    public void setQuantity(double quantity1) {
        this.quantity = quantity1;
    }
    public String getName() {
        return name;
    }

    public Unit getMyUnit() {
        return myUnit;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getQuantityString() {

        int up = (int) getQuantity()/(int)getMyUnit().getCoefficient();
        int sht = (int) getQuantity() % (int)getMyUnit().getCoefficient();

        String myString = "";

            myString = myString+Integer.toString(up)+"уп.";
            myString = myString + " + "+ Integer.toString(sht)+"шт.";

        return myString;

    }

    public double getPrice() {
        return price;
    }
    public double getSumma()
    {
        return price * getQuantity();
    }

    public boolean equals(Object o) {
        boolean retVal = false;

        if (o instanceof SvodRow){
            SvodRow ptr = (SvodRow) o;
            retVal = ptr.getGuid().equals(this.getGuid());
        }

        return retVal;
    }

    public String getGuid() {
        return guid;
    }
}
