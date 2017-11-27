package crypto.wallet.bittrex;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import crypto.wallet.lib_data.Currency;
import crypto.wallet.lib_data.DataAll;

import java.io.File;
import java.util.List;

/**
 * Created by Fynov on 07/03/17.
 */

public class ApplicationMy extends Application {
    int x;
    DataAll all;

    private static final String DATA_MAP = "cryptodatamap";
    private static final String FILE_NAME = "BitTrex.json";

    public static SharedPreferences preferences;


    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        x= 5;
        if (!load())
            all = DataAll.scenarijA();
    }


    public DataAll getAll() {
        return  all;
    }
    public void setAll(DataAll a) {
        all = a;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Currency getTestCurrency(){return all.getCurrency(0);}

    public Currency getCurrencyByID(String id){
        return all.getCurrencyByID(id);
    }

    public List<Currency> getCurrencyAll(){return all.getCurrencyAll();}


    public Currency getNewCharacter(String name, double value) {
        return all.getNewCurrency(name, value);
    }


    public boolean save() {
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""
                + FILE_NAME);

        return ApplicationJson.save(all,file);
    }
    public boolean load(){
        File file = new File(this.getExternalFilesDir(DATA_MAP), ""
                + FILE_NAME);
        DataAll tmp = ApplicationJson.load(file);
        if (tmp!=null) all = tmp;
        else return false;
        return true;
    }


    public void removeCharByPosition(int adapterPosition) {
        all.getCurrencyAll().remove(adapterPosition);
    }
}