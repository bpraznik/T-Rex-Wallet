package crypto.wallet.bittrex;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import crypto.wallet.lib_data.Currency;
import crypto.wallet.lib_data.DataAll;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ActivityCurrency extends AppCompatActivity {

    ApplicationMy app;
    TextView currencyName;
    TextView currencyValue;
    TextView currencyBoughtFor;
    TextView currencyBTCValue;
    TextView currencyTotal;
    String ID;
    Currency c;
    Bundle extras;


    public boolean refreshing = false;
    public boolean autoRefresh = false;
    public  int delay;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdView mAdView;

    Handler mHandler;


    static final Integer READ_EXST = 0x4;
    public String api = "abc123";
    public String sec = "abc123";
    String fiat = "";
    double fiatVal = 0;

    //private final String URL_TO_HIT = "https://bittrex.com/api/v1.1/public/getcurrencies";
    public String link = "";
    public String hash = "";


    public String getHash()
    {
        return hash;
    }
    public String getLink()
    {
        return link;
    }

    public String URL_TO_HIT = link;




    public ActivityCurrency() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
    }
    public void doItMan()
    {
        if (!refreshing){
            refreshing = true;
            mSwipeRefreshLayout.setRefreshing(true);
            link = "https://bittrex.com/api/v1.1/account/getorderhistory?apikey="+api+"&nonce="+System.currentTimeMillis()+"&market=BTC-"+c.getName();
            Log.d("MyApp", link);
            hash = calculateHash(sec, link, "HmacSHA512");
            c.setBoughtFor(0.0);

            URL_TO_HIT = link;
            new JSONTask().execute(URL_TO_HIT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
        extras = getIntent().getExtras();


        app = (ApplicationMy) getApplication();
        currencyName = (TextView) findViewById(R.id.currencyView);
        currencyValue = (TextView) findViewById(R.id.valueView);
        currencyBoughtFor = (TextView) findViewById(R.id.boughtForView);
        currencyBTCValue = (TextView) findViewById(R.id.curentBTCValueView);
        currencyTotal = (TextView) findViewById(R.id.totalTextView);

        ID ="";

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (!SP.getBoolean("hideBeta", true)){
            View namebar = findViewById(R.id.cw2);
            ((ViewGroup) namebar.getParent()).removeView(namebar);
        }

        //REFRESHER
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doItMan();
            }
        });

        try {
            if(extras !=null)
            {
                Log.d("CUrrency ID", extras.getString(DataAll.CURRENCY_ID));
                ID = extras.getString(DataAll.CURRENCY_ID);
                setCurrency(ID);
                Log.d("NAME",c.getName());
                api = SP.getString("key","error");
                sec = SP.getString("secret","error");
                if (SP.getString("key","error") != "error" || SP.getString("secret","error") != "error"){
                    c.setBoughtFor(0.0);
                }else {
                    Log.d("MyApp", api + " " + sec);
                    Toast.makeText(ActivityCurrency.this, "No API key set!", Toast.LENGTH_SHORT);
                }
            } else {
                System.out.println("Nič ni v extras!");
            }
        }catch (Exception ex){
            Log.d("ERROR" , ex.toString());
        }


        mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A36349524CE26B693AF263F8DE5B8159").build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ID = extras.getString(DataAll.CURRENCY_ID);
        if (api != "error" || sec != "error"){
            doItMan();
        }else {
            Log.d("MyApp", api + " " + sec);
            Toast.makeText(ActivityCurrency.this, "No API key set!", Toast.LENGTH_SHORT);
        }

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        this.mHandler = new Handler();

        Log.d("MyAPP", "Resumed");
    }


    void setCurrency(String ID){
        c = app.getCurrencyByID(ID);
        if(c!=null)
            update(c);
    }

    public void update(Currency c) {
        currencyName.setText(c.getName());

        DecimalFormat df = new DecimalFormat("0.00000000");
        currencyValue.setText(df.format(c.getQuantity()));
    }



    public static String calculateHash(String secret, String url, String encryption) {

        Mac shaHmac = null;

        try {

            shaHmac = Mac.getInstance(encryption);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }

        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), encryption);

        try {

            shaHmac.init(secretKey);

        } catch (InvalidKeyException e) {

            e.printStackTrace();
        }

        byte[] hash = shaHmac.doFinal(url.getBytes());
        String check = bytesToHex(hash);

        return check;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for(int j = 0; j < bytes.length; j++) {

            int v = bytes[j] & 0xFF;

            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    public class JSONTask extends AsyncTask<String,String, List<Currency> > {

        @Override
        protected List<Currency> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String mojHash = getHash();

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.addRequestProperty("apisign", mojHash);
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("result");

                List<Currency> currencyList = new ArrayList<>();
                Log.d("JSONarr", parentArray.toString());
                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself
                     * which is commented below
                     */
                    //MovieModel valuta = gson.fromJson(finalObject.toString(), MovieModel.class); // a single line json parsing using Gson
                    Currency valuta = new Currency();
                    valuta.setBoughtFor(finalObject.getDouble("Price"));
                    double celaCena = (finalObject.getDouble("Quantity") * finalObject.getDouble("PricePerUnit")) + finalObject.getDouble("Commission");


                    Log.d("Cena", String.valueOf(celaCena));
                    String type = finalObject.getString("OrderType");
                    Log.d("Cena", type);
                    if (type.contains("BUY")){
                        c.setBoughtFor(c.getBoughtFor()+celaCena);
                        Log.d("Cena", "Buy");
                    }else {
                        break;
                    }
                    Log.d("Bought For Cena", String.valueOf(c.getBoughtFor()));

                }
                /*
                String str1="https://bittrex.com/api/v1.1/public/getmarketsummary?market=btc-"+c.getName();
                try {
                    URL url4 = new URL(str1);
                    StringBuffer buferer = new StringBuffer();
                    URLConnection urlf = url4.openConnection();
                    BufferedReader bfr5 = new BufferedReader(new InputStreamReader(urlf.getInputStream()));
                    String line8;
                    while ((line8 = bfr5.readLine()) != null) {
                        buferer.append(line8+"\n");
                    }

                    String finJson = buferer.toString();


                    JSONArray jsonArr = new JSONArray(finJson);
                    Log.d("Myapp", jsonArr.toString());
                    JSONObject jsonObj = new JSONObject(jsonArr.getString(0));

                }
                catch(Exception e){

                }
                */


                try {
                    String str="https://bittrex.com/api/v1.1/public/getticker?market=BTC-"+c.getName();
                    try {
                        URL url3 = new URL(str);
                        URLConnection urlc = url3.openConnection();
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                        String line3;
                        while ((line3 = bfr.readLine()) != null) {
                            JSONObject jsa = new JSONObject(line3);
                            JSONObject jo = jsa.getJSONObject("result");
                            c.setValueBTC( (c.getQuantity() * jo.getDouble("Last")) * 0.9975);
                            Log.d("VALUE" , String.valueOf(c.getValueBTC()));
                        }
                    }
                    catch(Exception e){
                    }

                }catch (Exception es){
                    Log.d("Error" ,es.toString());
                }
                return currencyList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<Currency> result) {
            super.onPostExecute(result);

            DecimalFormat df = new DecimalFormat("0.00000000");
            DecimalFormat ef = new DecimalFormat("0.00");

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            int pick = Integer.valueOf(SP.getString("fiatCurrency","0"));
            double tmp;

            String compound;

            tmp = c.getBoughtFor();

            if (pick == 1)
                compound = ef.format(tmp*app.getAll().fiatVal) + "$";
            else
                compound = ef.format(tmp*app.getAll().fiatVal) + "€";


            currencyBoughtFor.setText(df.format(c.getBoughtFor()) + "Ƀ ≈ " + compound);


            tmp = c.getValueBTC();
            if (pick == 1)
                compound = ef.format(tmp*app.getAll().fiatVal) + "$";
            else
                compound = ef.format(tmp*app.getAll().fiatVal) + "€";
            currencyBTCValue.setText(df.format(c.getValueBTC()) + "Ƀ ≈ " + compound);


            tmp = c.getValueBTC()-c.getBoughtFor();
            tmp = round(tmp, 8);


            if (pick == 1)
                compound = ef.format(tmp*app.getAll().fiatVal) + "$";
            else
                compound = ef.format(tmp*app.getAll().fiatVal) + "€";

            double percent = (tmp / c.getBoughtFor()) * 100;
            currencyTotal.setText(df.format(tmp) + "Ƀ ≈ " + compound + " (" + ef.format(percent) + "%)");
            Log.d("MyAPP", "Loading Done");
            loadingDone();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MyAPP", "Paused");
        loadingDone();
    }

    public static double round(double value, int places) {
        /*
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
        */

        if (places == 2){
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(value));
        }else {
            DecimalFormat twoDForm = new DecimalFormat("#.########");
            return Double.valueOf(twoDForm.format(value));
        }

    }
    public void loadingDone(){
        mSwipeRefreshLayout.setRefreshing(false);
        refreshing = false;
    }
}
