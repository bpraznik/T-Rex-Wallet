package crypto.wallet.bittrex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import crypto.wallet.lib_data.Currency;
import crypto.wallet.lib_data.DataAll;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Fynov on 12/03/17.
 */

class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> implements Filterable {
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



    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public TextView txtBTCValue;
        public ImageView iv;
        public ConstraintLayout ly;
        public TextView tvLast;



        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.currency);
            txtFooter = (TextView) v.findViewById(R.id.quantity);
            txtBTCValue = (TextView) v.findViewById(R.id.BTCValue);
            iv = (ImageView)v.findViewById(R.id.icon);
            ly = (ConstraintLayout) v.findViewById(R.id.layout);
            tvLast = v.findViewById(R.id.tvLast);

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listrow, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
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


