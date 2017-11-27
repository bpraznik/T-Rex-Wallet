package crypto.wallet.lib_data;

/**
 * Created by Bor Praznik on 20/11/2017.
 */

public class Order {
    String orderID;
    String orderType;
    double value;

    public String getOrderID() {
        return orderID;
    }

    public double getValue() {
        return value;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
