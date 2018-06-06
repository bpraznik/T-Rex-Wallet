package currency.crypto.lib_data;

/**
 * Created by Bor on 09/01/2018.
 */

public class DataPoint {
    private long timestamp;
    private double valueEval;
    private double value;
    private double valueDay;
    private double valueWeek;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getValueEval() {
        return valueEval;
    }

    public void setValueEval(double valueEval) {
        this.valueEval = valueEval;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValueDay() {
        return valueDay;
    }

    public void setValueDay(double valueDay) {
        this.valueDay = valueDay;
    }

    public double getValueWeek() {
        return valueWeek;
    }

    public void setValueWeek(double valueWeek) {
        this.valueWeek = valueWeek;
    }
}
