package crypto.wallet.lib_data;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Fynov on 04/10/17.
 */

public class Currency {
    private String ID;
    private String userID;

    private String name;
    private String longName;
    private double quantity;
    private double lastPrice;
    private double valueBTC;
    private double boughtFor;
    private ArrayList<Order> orderList = new ArrayList<>();

    String fileName;
    public static final String NODATA="_NA";

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }

    public  Currency(String name, double quantity, String uid, String fileName){
        this.ID = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = name;
        this.quantity = quantity;
        this.userID = uid;
        this.fileName = fileName;
    }

    public  Currency(String name, double quantity){
        this.ID = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = name;
        this.quantity = quantity;
    }

    public  Currency(){
        this.ID = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public double getBoughtFor() {
        return boughtFor;
    }

    public void setBoughtFor(double boughtFor) {
        this.boughtFor = boughtFor;
    }

    public Double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getValueBTC() {
        return valueBTC;
    }

    public void setValueBTC(double valueBTC) {
        this.valueBTC = valueBTC;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return name + "\n   Value: " + quantity;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean hasImage() {
        if (fileName==null) return false;
        else if (fileName.equals(NODATA)) return false;
        return true;
    }
}
