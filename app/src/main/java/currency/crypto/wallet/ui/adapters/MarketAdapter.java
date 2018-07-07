package currency.crypto.wallet.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.text.DecimalFormat;
import java.util.ArrayList;

import currency.crypto.wallet.data.models.Currency;
import currency.crypto.wallet.data.models.DataAll;
import currency.crypto.wallet.ui.activities.ActivityMarketCurrency;
import currency.crypto.wallet.R;
import currency.crypto.wallet.ui.viewholders.MarketViewHolder;

/**
 * Created by Fynov on 12/03/17.
 */

public class MarketAdapter extends RecyclerView.Adapter<MarketViewHolder> implements Filterable {

    public ArrayList<Currency> mArrayList;
    public ArrayList<Currency> mFilteredList;
    Context context;
    Activity ac;


    public MarketAdapter(ArrayList<Currency> all, Activity acc, Context con) {
        mArrayList = all;
        mFilteredList = all;
        context = con;
        ac = acc;
    }

    private static void startDView(String currencyID, Activity ac) {
        //  System.out.println(name+":"+position);
        String tmp = currencyID;
        if (tmp.contains("MIOTA")) {
            tmp = "IOTA";
            currencyID = "IOTA";
        }
        currencyID += "BTC";
        if (currencyID.equalsIgnoreCase("BTCBTC")){
            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(ac);
            int pick = Integer.valueOf(SP.getString("fiatCurrency","0"));
            if (pick == 1)
                currencyID = "BTCUSD";
            else
                currencyID = "BTCEUR";
        }
        Intent i = new Intent(ac.getBaseContext(), ActivityMarketCurrency.class);
        i.putExtra(DataAll.CURRENCY_ID,  currencyID);
        i.putExtra("Name",  tmp);
        ac.startActivity(i);

    }



    @Override
    public MarketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketrow, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MarketViewHolder vh = new MarketViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MarketViewHolder holder, final int position) {
        //final Currency trenutni = all.getCurrency(position);
        final Currency trenutni = mFilteredList.get(position);
        double percent = trenutni.getPercentChange24();

        holder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarketAdapter.startDView(trenutni.getName(),ac);
            }
        });

        holder.tvSymbol.setText(trenutni.getName());

        DecimalFormat df = new DecimalFormat("0.00");
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        int pick = Integer.valueOf(SP.getString("fiatCurrency","0"));

        if (pick == 1)
            holder.tvValue.setText(df.format(trenutni.getValueBTC())+"$");
        else
            holder.tvValue.setText(df.format(trenutni.getValueBTC())+"€");

        if (percent>0)
            holder.tvPercentChange.setTextColor(ContextCompat.getColor(ac, R.color.materialGreen));
        else if (percent < 0)
            holder.tvPercentChange.setTextColor(ContextCompat.getColor(ac, R.color.materialRed));
        DecimalFormat df2 = new DecimalFormat("0.00");
        holder.tvPercentChange.setText(df2.format(percent) + "%");

    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }


    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = mArrayList;
                } else {

                    ArrayList<Currency> filteredList = new ArrayList<>();

                    for (Currency c : mArrayList) {

                        if (c.getName().toLowerCase().contains(charString)) {

                            filteredList.add(c);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Currency>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}


