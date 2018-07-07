package currency.crypto.wallet.ui.widgets;

import android.app.PendingIntent;
import android.arch.lifecycle.MutableLiveData;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
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

import currency.crypto.wallet.R;
import currency.crypto.wallet.data.models.Currency;

import static currency.crypto.wallet.ui.activities.ActivityStart.round;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetNetValue extends AppWidgetProvider {
    public String api = "abc123";
    public String sec = "abc123";
    public String link = "";
    public String hash = "";
    public getNet mTask;
    public MutableLiveData<ArrayList<Currency>> curList = new MutableLiveData<>();


    public WidgetNetValue() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
    }

    public String getHash()
    {
        return hash;
    }

    public String URL_TO_HIT = link;

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId, double netValue) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_net_value);
        DecimalFormat df = new DecimalFormat("0.00000000");
        //views.setTextViewText(R.id.appwidget_text, "Net: \n" + df.format(netValue) + "Ƀ");
        views.setTextViewText(R.id.appwidget_text, "Net: \n" + 1.3592925 + " Ƀ");

        Intent intentUpdate = new Intent(context, WidgetNetValue.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                context, appWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.relativeLayout, pendingUpdate);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        api = SP.getString("key","");
        sec = SP.getString("secret","");

        if (api != "" && sec !="" && api.length()!=0&& sec.length()!=0){
            link = "https://bittrex.com/api/v1.1/account/getbalances?apikey="+api+"&nonce="+System.currentTimeMillis();
            hash = calculateHash(sec, link, "HmacSHA512");

            URL_TO_HIT = link;
            mTask = new getNet();
            mTask.contextRef = new WeakReference<>(context);
            mTask.appWidgetManager = appWidgetManager;
            mTask.appWidgetIds = appWidgetIds;
            mTask.execute(URL_TO_HIT);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public class getNet extends AsyncTask<String,String, Double > {
        public WeakReference<Context> contextRef;
        AppWidgetManager appWidgetManager;
        int[] appWidgetIds;

        @Override
        protected Double doInBackground(String... params) {
            double allBalance = 0.0;
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

                //GET HIGH/LOW
                allBalance = 0.0;
                for(Currency c : currencyList)
                {
                    if(c.getName().equals("BTC"))
                    {
                        c.setLastPrice(1.0);
                        c.setValueBTC(c.getLastPrice()*c.getQuantity());
                        c.setValueBTC(round(c.getLastPrice()*c.getQuantity(), 7));
                        allBalance += (c.getLastPrice()*c.getQuantity());
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
                                Log.d("Json2: ", JSo.toString());
                                JSONArray JSarr = JSo.getJSONArray("result");
                                JSONObject jo = JSarr.getJSONObject(0);
                                c.setLastPrice(jo.getDouble("Last"));;
                                c.setValueBTC(round(c.getLastPrice()*c.getQuantity(), 7));
                                allBalance += (c.getLastPrice()*c.getQuantity());
                            }
                        }
                        catch(Exception e){
                        }
                    }
                }

                return allBalance;

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
        protected void onPostExecute(final Double result) {
            super.onPostExecute(result);

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(contextRef.get(), appWidgetManager, appWidgetId, result);
            }
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

}

