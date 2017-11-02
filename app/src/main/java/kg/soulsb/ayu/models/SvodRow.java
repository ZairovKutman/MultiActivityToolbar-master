package kg.soulsb.ayu.models;

/**
 * Created by soulsb on 10/19/17.
 */

public class SvodRow {
    private String name;
    private Unit myUnit;
    private double quantity;
    private double price;


    public SvodRow(String name, Unit myUnit, double quantity, double price) {
        this.name = name;
        this.myUnit = myUnit;
        this.quantity = quantity;
        this.price = price;
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
}
