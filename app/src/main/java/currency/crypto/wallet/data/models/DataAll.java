package currency.crypto.wallet.data.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fynov on 07/03/17.
 */

public class DataAll {
    public static final String CURRENCY_ID = "currency_idXX";
    private User uporabnik;
    private ArrayList<Currency> currencyList;
    public Double allBalance = 0.0;
    public Double fiatVal = 0.0;
    public String fiatType;


    public Currency getCurrencyByID(String ID) {
        for (Currency l: currencyList) { //TODO this solution is relatively slow! If possible don't use it!
            // if (l.getId() == ID) return l; //NAPAKA primerja reference
            if (l.getID().equals(ID)) return l;
        }
        return null;
    }

    public DataAll(){
        uporabnik = new User("Username", "user.name@mail.com");
        currencyList = new ArrayList<>();
    }

    public Currency addCurrency(String name, double value, String im){
        if (im==null) im = Currency.NODATA;
        else
            if (im.trim().length()==0) im = Currency.NODATA;

        Currency c = new Currency(name, value, uporabnik.getID(), im);
        currencyList.add(c);
        return c;
    }

    public void addCurrency(Currency c) {
        currencyList.add(c);
    }

    public void dumpCurrency() {
        currencyList.clear();
    }

    @Override
    public String toString() {
        return "DataAll{" +
                "\nuserMe=" + uporabnik +
                ", \nCharList=" + currencyList +
                '}';
    }

    public static DataAll scenarijA(){
        DataAll da = new DataAll();
        da.uporabnik = new User("Borko", "bor.praznik@gmail.com");
        return da;
    }

    public Currency getCurrency(int i){
        return  currencyList.get(i);
    }

    public  List<Currency> getCurrencyAll(){
        return currencyList;
    }

    public Currency getNewCurrency(String name, double value){ return addCurrency(name, value, Currency.NODATA); }

    public int getCurrencySize() {
        return currencyList.size();
    }

    public User getUserMe(){
        return uporabnik;
    }
}