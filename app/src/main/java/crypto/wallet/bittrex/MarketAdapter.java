package crypto.wallet.bittrex;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import crypto.wallet.lib_data.Currency;
import crypto.wallet.lib_data.DataAll;

/**
 * Created by Fynov on 12/03/17.
 */

class MarketAdapter extends RecyclerView.Adapter<MarketAdapter.ViewHolder> implements Filterable {

    public ArrayList<Currency> mArrayList;
    public ArrayList<Currency> mFilteredList;
    Activity ac;


    public MarketAdapter(ArrayList<Currency> all, Activity acc) {
        mArrayList = all;
        mFilteredList = all;
        ac = acc;
        Log.d("Adapter", "here");
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvSymbol;
        public TextView tvValue;
        public TextView tvPercentChange;


        public ViewHolder(View v) {
            super(v);
            Log.d("Adpater", "Im ");
            tvSymbol = (TextView) v.findViewById(R.id.tvSymbol);
            tvValue = (TextView) v.findViewById(R.id.tvValue);
            tvPercentChange = (TextView) v.findViewById(R.id.tvPercentChange);

        }
    }

    private static void startDView(String currencyID, Activity ac) {
        //  System.out.println(name+":"+position);
        Intent i = new Intent(ac.getBaseContext(), ActivityCurrency.class);
        i.putExtra(DataAll.CURRENCY_ID,  currencyID);
        ac.startActivity(i);

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Adpater", "Im heeeere");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.marketrow, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //final Currency trenutni = all.getCurrency(position);
        final Currency trenutni = mFilteredList.get(position);
        double percent = trenutni.getHigh1();

        holder.tvSymbol.setText(trenutni.getName());

        DecimalFormat df = new DecimalFormat("0.00000000");
        holder.tvValue.setText(df.format(trenutni.getValueBTC()));

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


