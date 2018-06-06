package currency.crypto.wallet;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.DecimalFormat;

import currency.crypto.lib_data.Currency;
import currency.crypto.lib_data.DataAll;
import currency.crypto.wallet.util.ApplicationMy;

public class ActivityMarketCurrency extends AppCompatActivity {

    WebView tradeView;
    //TextView tvRecomend;
    TextView tvName;
    String ID;
    Bundle extras;
    ApplicationMy app;
    ActivityMarketCurrency ac;
    String currencyName;
    String nameShort;
    //DataPoint recomend = new DataPoint();
    Button btnFullScreen;
    //Button btnRecomend;
    //ProgressBar pbRecomend;
    TextView tvLast;
    TextView tvHigh;
    TextView tvLow;
    View devider1;
    ConstraintLayout includeLast;
    ProgressBar pbLast;
    Currency c;

    public ActivityMarketCurrency() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, UnsupportedEncodingException {
    }

    public String link = "";
    public String URL_TO_HIT = link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_currency);

        app = (ApplicationMy) getApplication();
        tradeView = findViewById(R.id.chartView);
        extras = getIntent().getExtras();
        //tvRecomend = findViewById(R.id.tvRecomend);
        tvName = findViewById(R.id.tvName);
        btnFullScreen = findViewById(R.id.btnFull);
        //btnRecomend = findViewById(R.id.btnRecomend);
        //pbRecomend = findViewById(R.id.pbRecomend);
        //pbRecomend.setVisibility(View.INVISIBLE);


        tvLast = (TextView) findViewById(R.id.tvLast);
        tvHigh = (TextView) findViewById(R.id.tvHigh);
        tvLow = (TextView) findViewById(R.id.tvLow);
        includeLast = findViewById(R.id.includeLast);
        pbLast = findViewById(R.id.pbLast);

        ac = this;

        c = new Currency();
/*
        btnRecomend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbRecomend.setVisibility(View.VISIBLE);
                new JSONTask().execute(URL_TO_HIT);
            }
        });*/

        btnFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ac, ActivityChart.class);
                i.putExtra("Name",  currencyName);
                ac.startActivity(i);
            }
        });

        ID ="";


        try {
            if(extras !=null)
            {
                Log.d("Currency ID", extras.getString(DataAll.CURRENCY_ID));
                currencyName = extras.getString(DataAll.CURRENCY_ID);
                nameShort = extras.getString("Name");
                c.setName(nameShort);
                tvName.setText(nameShort);
                new GetLast().execute("dd");
                loadchart();
            } else {
                System.out.println("Nič ni v extras!");
            }
        }catch (Exception ex){
            Log.d("ERROR" , ex.toString());
        }
    }

/*
    public class JSONTask extends AsyncTask<String,String, ArrayList<DataPoint>> {

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected ArrayList<DataPoint> doInBackground(String... params) {

            ArrayList<ArrayList<Long>> timestamps = new ArrayList<>();
            Date now = new Date();

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, -90);
            Date past = cal.getTime();

            for (int i = 0; i <50; i++){
                ArrayList<Long> tmp = new ArrayList<Long>();
                long timestampEval = ThreadLocalRandom.current().nextLong(past.getTime()/1000, now.getTime()/1000);
                tmp.add(timestampEval);

                long timestamp = timestampEval - (604800000L/1000);
                tmp.add(timestamp);

                long timestampDayAgo = timestamp - (86400000/1000);
                tmp.add(timestampDayAgo);

                long timestampWeekAgo = timestamp - (604800000L/1000);
                tmp.add(timestampWeekAgo);

                timestamps.add(tmp);
            }

            ArrayList<DataPoint> historyData = new ArrayList<>();
            int progress = 0;
            for (ArrayList<Long> points : timestamps){
                int i = 0;
                DataPoint tmp = new DataPoint();
                for (long timestamp : points) {
                    progress += 1;
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL("https://min-api.cryptocompare.com/data/pricehistorical?fsym="+nameShort+"&tsyms=USD&ts="+timestamp);
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

                        JSONObject js1 = new JSONObject(finalJson);
                        JSONObject jso = js1.getJSONObject(nameShort);

                        Date tmpDate = new Date(timestamp);
                        long tmpln = tmpDate.getTime();
                        double value = jso.getDouble("USD");
                        switch (i){
                            case 0:
                                tmp.setValueEval(value);
                                break;
                            case 1:
                                tmp.setValue(value);
                                break;
                            case 2:
                                tmp.setValueDay(value);
                                break;
                            case 3:
                                tmp.setValueWeek(value);
                                break;
                            default:
                                break;
                        }
                        i++;
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
                    //pbRecomend.setProgress(progress/2);
                }
                if(tmp.getValue() != 0 && tmp.getValueDay() != 0 && tmp.getValueWeek() != 0)
                    historyData.add(tmp);
            }


            ArrayList<Long> obj = new ArrayList<>();
            long timestamp = now.getTime()/1000;
            Log.d("Time", String.valueOf(timestamp));
            obj.add(timestamp);
            long timestampDayAgo = timestamp - (86400000/1000);
            obj.add(timestampDayAgo);
            long timestampWeekAgo = timestamp - (604800000L/1000);
            obj.add(timestampWeekAgo);
            int i = 0;
            for(Long time: obj){
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("https://min-api.cryptocompare.com/data/pricehistorical?fsym="+nameShort+"&tsyms=USD&ts="+time);
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

                    JSONObject js1 = new JSONObject(finalJson);
                    JSONObject jso = js1.getJSONObject(nameShort);

                    Date tmpDate = new Date(timestamp);
                    long tmpln = tmpDate.getTime();
                    double value = jso.getDouble("USD");
                    switch (i){
                        case 0:
                            recomend.setValue(value);
                            break;
                        case 1:
                            recomend.setValueDay(value);
                            break;
                        case 2:
                            recomend.setValueWeek(value);
                            break;
                        default:
                            break;
                    }
                    i++;
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
            }
            return historyData;
        }

        @Override
        protected void onPostExecute(final ArrayList<DataPoint> result) {
            super.onPostExecute(result);
            String ARFF = "";

            ARFF = "@relation crypto\n" +
                    "\n" +
                    "@attribute value numeric\n" +
                    "@attribute dayChange numeric\n" +
                    "@attribute weekChange numeric\n" +
                    "@attribute class {buy, nothing, sell}\n" +
                    "\n" +
                    "@data\n";
            DecimalFormat df = new DecimalFormat("0.00000000");
            if(result != null) {
                for (DataPoint data: result) {
                    Log.d("DataPoint: ", df.format(data.getValue()) + ", " +df.format(data.getValueDay()) + ", " +df.format(data.getValueWeek()) + " : " + df.format(data.getValueEval()));

                    ARFF += df.format(data.getValue()) +", " + df.format(data.getValue() - data.getValueDay()) + ", " + df.format(data.getValue() - data.getValueWeek());
                    double top = data.getValue() * 1.05;
                    double bottom = data.getValue() * 0.95;

                    if (data.getValueEval() > bottom && data.getValueEval() < top)
                        ARFF += ", nothing";
                    else if (data.getValue() < data.getValueEval())
                        ARFF += ", buy";
                    else
                        ARFF += ", sell";
                    ARFF += "\n";
                }


                BufferedReader reader;
                try {
                    InputStream is = new ByteArrayInputStream(ARFF.getBytes());
                    reader = new BufferedReader(new InputStreamReader(is));

                    int percentSplit = 66;

                    Instances data = new Instances(reader);
                    reader.close();
                    // setting class attribute
                    data.setClassIndex(data.numAttributes() - 1);

                    Classifier cl = new J48();
                    Filter myRand = new Randomize();
                    myRand.setInputFormat(data);

                    data = Filter.useFilter(data, myRand);

                    int trainSize = data.numInstances() * percentSplit / 100;
                    int testSize = data.numInstances() - trainSize;
                    Instances train = new Instances(data, 0, trainSize);

                    cl.buildClassifier(train);
                    int numCorrect = 0;
                    for (int i = trainSize; i < data.numInstances(); i ++){
                        Instance current = data.instance(i);
                        double predicted = cl.classifyInstance(current);
                        if(predicted == data.instance(i).classValue())
                            numCorrect++;
                    }
                    DecimalFormat df1 = new DecimalFormat("0.00");
                    double percent = (double)((double)numCorrect/(double)testSize *100.00);
                    String out = "Correctly classified: " + numCorrect + " (" + percent + " %)" +
                            "\nIncorrectly classified: " + (testSize - numCorrect) + " (" + (100-percent) + " %)";
                    Log.d("Weka: ", out);


                    Attribute Attribute1 = new Attribute("value");
                    Attribute Attribute2 = new Attribute("dayChange");
                    Attribute Attribute3 = new Attribute("weekChange");

                    FastVector Classes = new FastVector(3);
                    Classes.addElement("buy");
                    Classes.addElement("nothing");
                    Classes.addElement("sell");
                    Attribute ClassAttribute = new Attribute("Class", Classes);

                    FastVector fvWekaAttributes = new FastVector(4);
                    fvWekaAttributes.addElement(Attribute1);
                    fvWekaAttributes.addElement(Attribute2);
                    fvWekaAttributes.addElement(Attribute3);
                    fvWekaAttributes.addElement(ClassAttribute);

                    Instances evalSet = new Instances("Rel", fvWekaAttributes, 10);
                    evalSet.setClassIndex(3);
                    Instance testInstance = new DenseInstance(4);
                    testInstance.setValue((Attribute)fvWekaAttributes.elementAt(0), recomend.getValue());
                    testInstance.setValue((Attribute)fvWekaAttributes.elementAt(1), recomend.getValueDay());
                    testInstance.setValue((Attribute)fvWekaAttributes.elementAt(2), recomend.getValueWeek());
                    evalSet.add(testInstance);

                    System.out.println("The evaluation data: " + evalSet.instance(0));

                    //Classify new Instance
                    double ClassLabel = 100;
                    ClassLabel = cl.classifyInstance(evalSet.instance(0));
                    System.out.println("Classified: " + ClassLabel);

                    String output ="";
                    if (ClassLabel == 0.0)
                        output = "Recommending BUY\nWith: " + df1.format(percent) + "% certainty";
                    else if (ClassLabel == 1.0)
                        output = "Recommending HOLD\nWith: " + df1.format(percent) + "% certainty";
                    else if (ClassLabel == 2.0)
                        output = "Recommending SELL\nWith: " + df1.format(percent) + "% certainty";
                    tvRecomend.setText("");

                    if (percent > 99)
                        output = "Could not calculate";
                    AlertDialog.Builder builder = new AlertDialog.Builder(ac);
                    builder.setMessage(output)
                            .setTitle("Recommendation")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {}
                    });

                    pbRecomend.setVisibility(View.INVISIBLE);
                    pbRecomend.setProgress(0);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {}
        }
    }
    */
    public class GetLast extends AsyncTask<String,String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                String str="https://bittrex.com/api/v1.1/public/getmarketsummary?market=BTC-"+c.getName();
                URL url = new URL(str);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line3;
                while ((line3 = reader.readLine()) != null) {
                    JSONObject jsa = new JSONObject(line3);
                    JSONArray jsa2 = jsa.getJSONArray("result");
                    JSONObject jo = jsa2.getJSONObject(0);
                    c.setLast(jo.getDouble("Last"));
                    c.setLow(jo.getDouble("Low"));
                    c.setHigh(jo.getDouble("High"));
                }
                return "DONE";

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
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            if (o != null){
                DecimalFormat df = new DecimalFormat("0.00000000");
                includeLast.setVisibility(View.VISIBLE);
                pbLast.setProgress((int) Math.floor((100-0)/(c.getHigh()-c.getLow())*(c.getLast()-c.getHigh())+100));
                tvLast.setText("Last:\n" + df.format(c.getLast()));
                tvHigh.setText("High:\n" + df.format(c.getHigh()));
                tvLow.setText("Low:\n" + df.format(c.getLow()));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    public void loadchart(){
        String tradeString = "<!-- TradingView Widget BEGIN -->\n" +
                "<script type=\"text/javascript\" src=\"https://s3.tradingview.com/tv.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "new TradingView.widget({\n" +
                "  \"autosize\": true,\n" +
                "  \"symbol\": \"" + currencyName+"\",\n" +
                "  \"interval\": \"30\",\n" +
                "  \"timezone\": \"Etc/UTC\",\n" +
                "  \"theme\": \"Light\",\n" +
                "  \"style\": \"1\",\n" +
                "  \"locale\": \"en\",\n" +
                "  \"toolbar_bg\": \"#f1f3f6\",\n" +
                "  \"enable_publishing\": false,\n" +
                "  \"hide_top_toolbar\": true,\n" +
                "  \"save_image\": false,\n" +
                "  \"hideideas\": true\n" +
                "});\n" +
                "</script>\n" +
                "<!-- TradingView Widget END --> ";
        tradeView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        WebSettings webSettings = tradeView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        tradeView.loadData(tradeString, "text/html", "utf-8");
    }
}
