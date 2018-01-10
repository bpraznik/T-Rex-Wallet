package crypto.wallet.lib_data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Fynov on 04/10/17.
 */

public class Currency {
    private String ID;
    private String userID;

    private String name;
    private String longName;
    private int rank;
    private double quantity;
    private double lastPrice;

    private double low24;
    private double low1;
    private double percentChange24;
    private double high1;

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

    public  Currency(){
        this.ID = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public void setHigh1(double high1) {
        this.high1 = high1;
    }

    public double getHigh1() {
        return high1;
    }

    public void setLow1(double low1) {
        this.low1 = low1;
    }

    public double getLow1() {
        return low1;
    }

    public void setPercentChange24(double percentChange24) {
        this.percentChange24 = percentChange24;
    }

    public double getPercentChange24() {
        return percentChange24;
    }

    public void setLow24(double low24) {
        this.low24 = low24;
    }

    public double getLow24() {
        return low24;
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

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public boolean hasImage() {
        if (fileName==null) return false;
        else if (fileName.equals(NODATA)) return false;
        return true;
    }

    public static final Comparator<Currency> By_Name_Alphabeticaly = new Comparator<Currency>() {
        @Override
        public int compare(Currency currency, Currency t1) {
            return currency.name.compareTo(t1.name);
        }
    };
    public static final Comparator<Currency> By_Value = new Comparator<Currency>() {
        @Override
        public int compare(Currency currency, Currency t1) {
            return Integer.compare(currency.rank,t1.rank);
        }
    };
    public static final Comparator<Currency> By_Change = new Comparator<Currency>() {
        @Override
        public int compare(Currency currency, Currency t1) {
            return Double.compare(currency.percentChange24,t1.percentChange24);
        }
    };

    public static final Comparator<Currency> ByRank = new Comparator<Currency>() {
        @Override
        public int compare(Currency currency, Currency t1) {

            return currency.name.compareTo(t1.name);
        }
    };
}
