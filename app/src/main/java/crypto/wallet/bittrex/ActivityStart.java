package crypto.wallet.bittrex;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import crypto.wallet.lib_data.Currency;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class ActivityStart extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ApplicationMy app;
    private RecyclerView mRecyclerView;
    private CryptoAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ConstraintLayout mConstraintLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public TextView balanceAll;
    public TextView tvBallanceText;
    public TextView tvWalletText;
    public TextView tvBallanceAllFiat;
    public TextView tvBTCvalueFiat;
    public View inclWallet;
    public View inclBalance;
    private JSONTask mTask;
    private AdView mAdView;
    private ProgressBar pbLoader;
    private SearchView searchView;


    static final Integer READ_EXST = 0x4;

    public String api = "abc123";
    public String sec = "abc123";
    String fiat = "";
    double fiatVal = 0;
    public boolean refreshing = false;
    public boolean canceled = false;
    public double btc1val;

    public String link = "";
    public String hash = "";

    public ActivityStart() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
    }

    public String getHash()
    {
        return hash;
    }
    public String getLink()
    {
        return link;
    }

    public String URL_TO_HIT = link;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        app = (ApplicationMy) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        inclBalance = findViewById(R.id.inclBalance);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mAdapter = new CryptoAdapter(app.getAll(), this);

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.layout);

        //REFRESHER
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing)
                    doItMan();
            }
        });


        //SEARCH
        searchView = (SearchView) findViewById(R.id.svSearch);
        searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));
        search(searchView);

        //TEXT
        balanceAll = (TextView)findViewById(R.id.balanceAll);
        tvBallanceText = findViewById(R.id.tvBallanceText);
        tvWalletText = findViewById(R.id.tvWalletText);
        tvBTCvalueFiat = findViewById(R.id.BTCvalueFiat);
        tvBallanceAllFiat = findViewById(R.id.balanceAllFiat);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CryptoAdapter(app.getAll(), this);
        mRecyclerView.setAdapter(mAdapter);

        //ADS
        MobileAds.initialize(this, getResources().getString(R.string.addAppID));
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("A36349524CE26B693AF263F8DE5B8159").build();
        mAdView.loadAd(adRequest);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()){
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: on resume clears variables, therfore it always refreshes
        if (!refreshing) {
            doItMan();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDone();
        try{
            mTask.cancel(true);
        }catch (Exception e){

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_wallet) {

        } else if (id == R.id.nav_market) {
            startActivity(new Intent(this,ActivityMarket.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this,ActivityMySettings.class));
        }/* else if (id == R.id.nav_suggest) {

        }*/ else if (id == R.id.nav_about) {
            startActivity(new Intent(this,ActivityAbout.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void askForPermission(String permission, Integer requestCode) {

        if (ContextCompat.checkSelfPermission(ActivityStart.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivityStart.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ActivityStart.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ActivityStart.this, new String[]{permission}, requestCode);
            }
        }
    }
    public void doItMan()
    {
        refreshing = true;

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        api = SP.getString("key","");
        sec = SP.getString("secret","");

        if (api != "" && sec !=""){
            mSwipeRefreshLayout.setRefreshing(true);
            //mProgressBar.setVisibility(View.VISIBLE);
            link = "https://bittrex.com/api/v1.1/account/getbalances?apikey="+api+"&nonce="+System.currentTimeMillis();
            hash = calculateHash(sec, link, "HmacSHA512");
            app.getAll().allBalance = 0.00;

            URL_TO_HIT = link;
            mTask = new ActivityStart.JSONTask();
            mTask.execute(URL_TO_HIT);
        }else {
            Toast.makeText(ActivityStart.this, "No API keys set!", Toast.LENGTH_SHORT).show();
            loadingDone();
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

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
        protected void onCancelled() {
            super.onCancelled();
            Log.d("Cancle", "cancled");
            app.getAll().allBalance = 0.0;
        }

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

                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);
                    /**
                     * below single line of code from Gson saves you from writing the json parsing yourself
                     * which is commented below
                     */
                    //MovieModel valuta = gson.fromJson(finalObject.toString(), MovieModel.class); // a single line json parsing using Gson
                    Currency valuta = new Currency();
                    valuta.setName(finalObject.getString("Currency"));
                    valuta.setLongName(finalObject.getString("Currency"));
                    valuta.setQuantity(round(finalObject.getDouble("Balance"),7));

                    Random r = new Random();
                    double randomValue = -30 + (189 - (-30)) * r.nextDouble();

                    String neki = finalObject.getString("Currency");
                    String mojaSlikica = "";

                    mojaSlikica = Currency.NODATA;


                    valuta.setFileName(
                            mojaSlikica
                    );
                    // adding the final object in the list
                    if(valuta.getQuantity() != 0)
                        currencyList.add(valuta);
                }


                //GET FIAT VALUE
                String str1="https://api.coinmarketcap.com/v1/ticker/bitcoin/?convert=EUR";
                try {
                    fiatVal =0;
                    URL url4 = new URL(str1);
                    StringBuffer buferer = new StringBuffer();
                    URLConnection urlf = url4.openConnection();
                    BufferedReader bfr5 = new BufferedReader(new InputStreamReader(urlf.getInputStream()));
                    String line8;
                    while ((line8 = bfr5.readLine()) != null) {
                        buferer.append(line8+"\n");
                    }
                    JSONArray jsonArr = new JSONArray(buferer.toString());
                    Log.d("Myapp", jsonArr.getString(0));
                    JSONObject jsonObj = new JSONObject(jsonArr.getString(0));


                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    int pick = Integer.valueOf(SP.getString("fiatCurrency","0"));
                    if (pick == 1)
                        fiat =  jsonObj.getString("price_usd");
                    else
                        fiat =  jsonObj.getString("price_eur");

                    btc1val = Double.parseDouble(fiat);

                    app.getAll().fiatVal = Double.valueOf(fiat);
                }
                catch(Exception e){
                    Log.d("MyApp","Error");
                }

                for(Currency c : currencyList)
                {
                        //JSON CENA FFS...
                        String str="https://min-api.cryptocompare.com/data/histohour?fsym=" + c.getName() + "&tsym=BTC&limit=1&aggregate=3&e=BitTrex";
                        try {
                            URL url3 = new URL(str);
                            URLConnection urlc = url3.openConnection();
                            BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                            String line3;
                            while ((line3 = bfr.readLine()) != null) {
                                JSONObject JSo = new JSONObject(line3);
                                JSONArray JSarr = JSo.getJSONArray("Data");
                                JSONObject jo = JSarr.getJSONObject(0);
                                c.setHigh1(jo.getDouble("high"));
                                c.setLow1(jo.getDouble("low"));
                            }
                        }
                        catch(Exception e){
                        }
                }

                //GET HIGH/LOW

                for(Currency c : currencyList)
                {
                    if(c.getName().equals("BTC"))
                    {
                        c.setLastPrice(1.0);
                        c.setValueBTC(c.getLastPrice()*c.getQuantity());
                        c.setValueBTC(round(c.getLastPrice()*c.getQuantity(), 7));
                        app.getAll().allBalance += (c.getLastPrice()*c.getQuantity());
                    }
                    else
                    {
                        //JSON CENA FFS...
                        String str="https://bittrex.com/api/v1.1/public/getmarketsummary?market=btc-"+c.getName();
                        try {
                            URL url3 = new URL(str);
                            URLConnection urlc = url3.openConnection();
                            BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                            String line3;
                            while ((line3 = bfr.readLine()) != null) {
                                JSONObject JSo = new JSONObject(line3);
                                JSONArray JSarr = JSo.getJSONArray("result");
                                JSONObject jo = JSarr.getJSONObject(0);
                                c.setLastPrice(jo.getDouble("Last"));;
                                c.setValueBTC(round(c.getLastPrice()*c.getQuantity(), 7));
                                app.getAll().allBalance += (c.getLastPrice()*c.getQuantity());
                            }
                        }
                        catch(Exception e){
                        }
                    }
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
            return  null;
        }

        @Override
        protected void onPostExecute(final List<Currency> result) {
            super.onPostExecute(result);

            app.getAll().dumpCurrency();
            fiatVal =0;

            if(result != null) {

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                float minVal = Float.parseFloat(SP.getString("hideNumber", "0.0000001f"));
                boolean minCheck = SP.getBoolean("hideSmall", false);
                for(int i=0; i<result.size(); i++){
                    if (minCheck){
                        if (result.get(i).getValueBTC() > minVal)
                            app.getAll().addCurrency(result.get(i));
                    }
                    else{
                        app.getAll().addCurrency(result.get(i));
                    }
                }


                Log.d("MyApp","I am here");

                fiatVal = app.getAll().allBalance * Double.valueOf(fiat);
                fiatVal = round(fiatVal, 2);
                app.getAll().allBalance = round(app.getAll().allBalance, 7);

                int pick = Integer.valueOf(SP.getString("fiatCurrency","0"));
                String compound;
                DecimalFormat ef = new DecimalFormat("0.00");
                if (pick == 1)
                    compound = ef.format(fiatVal) + "$";
                else
                    compound = ef.format(fiatVal) + "€";


                DecimalFormat df = new DecimalFormat("0.00000000");
                balanceAll.setText(df.format(app.getAll().allBalance) + "Ƀ");
                tvBallanceAllFiat.setText("≈ " + compound);

                if (pick == 1)
                    tvBTCvalueFiat.setText("BTC: "+ef.format(btc1val) + "$");
                else
                    tvBTCvalueFiat.setText("BTC: "+ef.format(btc1val) + "€");

                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ActivityStart.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
            }
            loadingDone();
        }
    }

    public static double round(double value, int places) {
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
        tvBallanceText.setVisibility(View.VISIBLE);
        tvWalletText.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        inclBalance.setVisibility(View.VISIBLE);
        refreshing = false;
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}
