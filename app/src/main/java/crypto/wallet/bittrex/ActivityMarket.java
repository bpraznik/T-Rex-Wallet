package crypto.wallet.bittrex;

import android.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import crypto.wallet.lib_data.Currency;

public class ActivityMarket extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MarketAdapter mAdapter;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ConstraintLayout mConstraintLayout;
    ApplicationMy app;
    SearchView searchView;
    private RecyclerView.LayoutManager mLayoutManager;

    public ArrayList<Currency> currencyList = new ArrayList<>();

    public String api = "abc123";
    public String sec = "abc123";
    public boolean refreshing = false;
    private JSONTask mTask;

    public String link = "";
    public String hash = "";

    public ActivityMarket() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
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

        mAdapter = new MarketAdapter(currencyList, this);

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
        mAdapter = new MarketAdapter(currencyList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!refreshing)
                    doItMan();
            }
        });

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
        }


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_wallet) {
            startActivity(new Intent(this,ActivityStart.class));
            finish();
        } else if (id == R.id.nav_market) {

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


    public void doItMan()
    {
        refreshing = true;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        api = SP.getString("key","");
        sec = SP.getString("secret","");

        if (api != "" && sec !=""){
            mSwipeRefreshLayout.setRefreshing(true);
            //mProgressBar.setVisibility(View.VISIBLE);
            link = "https://api.coinmarketcap.com/v1/ticker/?limit=300";
            hash = calculateHash(sec, link, "HmacSHA512");

            URL_TO_HIT = link;
            new JSONTask().execute(URL_TO_HIT);
        }else {
            Toast.makeText(ActivityMarket.this, "No API keys set!", Toast.LENGTH_SHORT).show();
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
                ArrayList<Currency> curList = new ArrayList<>();
                JSONArray jsA = new JSONArray(finalJson);

                for(int i=0; i<jsA.length(); i++) {
                    JSONObject finalObject = jsA.getJSONObject(i);

                    Currency valuta = new Currency();
                    valuta.setName(finalObject.getString("symbol"));
                    valuta.setLongName(finalObject.getString("name"));
                    valuta.setQuantity(-2);
                    valuta.setValueBTC(round(finalObject.getDouble("price_btc"),7));
                    if (finalObject.getString("percent_change_24h") == "null")
                        continue;
                    valuta.setHigh1(finalObject.getDouble("percent_change_24h"));
                    curList.add(valuta);
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
