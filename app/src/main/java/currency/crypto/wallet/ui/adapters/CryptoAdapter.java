package currency.crypto.wallet.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import currency.crypto.wallet.data.models.Currency;
import currency.crypto.wallet.data.models.DataAll;
import currency.crypto.wallet.ui.activities.ActivityCurrency;
import currency.crypto.wallet.R;
import currency.crypto.wallet.ui.viewholders.CryptoViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Fynov on 12/03/17.
 */

public class CryptoAdapter extends RecyclerView.Adapter<CryptoViewHolder> implements Filterable {
    DataAll all;
    Activity ac;
    boolean ttt = false;

    public ArrayList<Currency> mArrayList;
    public ArrayList<Currency> mFilteredList;


    public CryptoAdapter(DataAll all, Activity ac) {
        this.all = all;
        this.ac = ac;
        mArrayList = (ArrayList<Currency>) all.getCurrencyAll();
        mFilteredList = (ArrayList<Currency>) all.getCurrencyAll();
    }

    private static void startDView(String currencyID, Activity ac) {
        //  System.out.println(name+":"+position);
        Intent i = new Intent(ac.getBaseContext(), ActivityCurrency.class);
        i.putExtra(DataAll.CURRENCY_ID,  currencyID);
        ac.startActivity(i);
    }

    @Override
    public CryptoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listrow, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CryptoViewHolder vh = new CryptoViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(CryptoViewHolder holder, final int position) {
        //final Currency trenutni = all.getCurrency(position);
        final Currency trenutni = mFilteredList.get(position);
        final String name = trenutni.getName();
        final String value = Double.toString(trenutni.getQuantity());
        final String LP = Double.toString(trenutni.getValueBTC());

        DecimalFormat df = new DecimalFormat("0.00000000");

        holder.txtHeader.setText(name);
        holder.txtFooter.setText(df.format(trenutni.getQuantity()));
        holder.txtBTCValue.setText(df.format(trenutni.getValueBTC()) + " Ƀ");
        double avg = trenutni.getHigh1() + trenutni.getLow1();
        avg = avg/2;
        double percent = trenutni.getLastPrice() - avg;
        percent = percent / trenutni.getLastPrice();
        percent *= 100;

        if (percent>0)
            holder.tvLast.setTextColor(ContextCompat.getColor(ac, R.color.materialGreen));
        else if (percent < avg)
            holder.tvLast.setTextColor(ContextCompat.getColor(ac, R.color.materialRed));
        DecimalFormat df2 = new DecimalFormat("0.00");
        holder.tvLast.setText(df.format(trenutni.getLastPrice()) + " (" + df2.format(percent) + "%)");


        holder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CryptoAdapter.startDView(trenutni.getID(),ac);
            }
        });


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


