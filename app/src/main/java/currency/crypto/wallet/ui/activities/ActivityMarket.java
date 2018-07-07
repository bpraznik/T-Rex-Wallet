package currency.crypto.wallet.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import currency.crypto.wallet.data.models.Currency;
import currency.crypto.wallet.R;
import currency.crypto.wallet.ui.adapters.MarketAdapter;
import currency.crypto.wallet.data.ApplicationMy;

public class ActivityMarket extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MarketAdapter mAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ConstraintLayout mConstraintLayout;
    ApplicationMy app;
    SearchView searchView;
    AdView mAdView;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Currency> currencyList = new ArrayList<>();

    public String api = "abc123";
    public String sec = "abc123";
    public boolean refreshing = false;
    private JSONTask mTask;
    String fiat = "";
    double fiatVal = 0;

    public String link = "";
    public String hash = "";

    public ActivityMarket() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
    }


    public String URL_TO_HIT = link;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        app = (ApplicationMy) getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAdapter = new MarketAdapter(currencyList, this, getBaseContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.myRecyclerView);
        mRecyclerView.setHasFixedSize(true);

        mConstraintLayout = (ConstraintLayout) findViewById(R.id.layout);

        //REFRESHER
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);


        //SEARCH
        searchView = (SearchView) findViewById(R.id.svSearch1);
        searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));
        search(searchView);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MarketAdapter(currencyList, this,getBaseContext());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing)
                    doItMan();
            }
        });


        //ADS
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        doItMan();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()){

            searchView.setIconified(true);
        }else {
            super.onBackPressed();
            finish();
        }


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_wallet) {
            finish();
        } else if (id == R.id.nav_market) {

        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this,ActivityMySettings.class));
        }/* else if (id == R.id.nav_suggest) {

        }*/ else if (id == R.id.nav_about) {
            startActivity(new Intent(this,ActivityAbout.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void doItMan()
    {
        refreshing = true;

        mSwipeRefreshLayout.setRefreshing(true);
        link = "https://api.coinmarketcap.com/v1/ticker/?limit=300";

        URL_TO_HIT = link;
        new JSONTask().execute(URL_TO_HIT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.alphaAsc:
                Collections.sort(currencyList, Currency.By_Name_Alphabeticaly);
                break;
            case R.id.alphaDesc:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(currencyList, Currency.By_Name_Alphabeticaly.reversed());
                }
                break;
            case R.id.valAsc:
                Collections.sort(currencyList, Currency.By_Value);
                break;
            case R.id.valDesc:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(currencyList, Currency.By_Value.reversed());
                }
                break;
            case R.id.changeAsc:
                Collections.sort(currencyList, Currency.By_Change);
                break;
            case R.id.changeDesc:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Collections.sort(currencyList, Currency.By_Change.reversed());
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        mAdapter.notifyDataSetChanged();
        return true;
    }

    public class JSONTask extends AsyncTask<String,String, List<Currency> > {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected List<Currency> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;


            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();
                ArrayList<Currency> curList = new ArrayList<>();
                JSONArray jsA = new JSONArray(finalJson);

                for(int i=0; i<jsA.length(); i++) {
                    JSONObject finalObject = jsA.getJSONObject(i);

                    Currency valuta = new Currency();
                    valuta.setName(finalObject.getString("symbol"));
                    valuta.setLongName(finalObject.getString("name"));
                    valuta.setRank(finalObject.getInt("rank"));
                    valuta.setQuantity(-2);
                    valuta.setValueBTC(round(finalObject.getDouble("price_btc"),7)*app.getAll().fiatVal);
                    if (finalObject.getString("percent_change_24h") == "null")
                        continue;
                    valuta.setPercentChange24(finalObject.getDouble("percent_change_24h"));
                    curList.add(valuta);
                }

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
                    if (pick == 1){
                        fiat =  jsonObj.getString("price_usd");
                    }
                    else{
                        fiat =  jsonObj.getString("price_eur");
                    }

                    app.getAll().fiatVal = Double.valueOf(fiat);
                }
                catch(Exception e){
                    Log.d("MyApp","Error");
                }

                return curList;

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
            currencyList.clear();
            if(result != null) {
                for(int i=0; i<result.size(); i++){

                    currencyList.add(result.get(i));

                }

                mAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ActivityMarket.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
            }
            loadingDone();
        }
    }
/*
    public static double round(double value, int places) {
        if (places == 2){
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(value));
        }else {
            DecimalFormat twoDForm = new DecimalFormat("#.########");
            return Double.valueOf(twoDForm.format(value));
        }
    }*/
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void loadingDone(){

        mSwipeRefreshLayout.setRefreshing(false);
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
