package crypto.wallet.bittrex;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import crypto.wallet.lib_data.Currency;
import crypto.wallet.lib_data.DataAll;

import java.text.DecimalFormat;

/**
 * Created by Fynov on 12/03/17.
 */

class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {
    DataAll all;
    Activity ac;
    boolean ttt = false;


    public CryptoAdapter(DataAll all, Activity ac) {
        this.all = all;
        this.ac = ac;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtFooter;
        public TextView txtBTCValue;
        public ImageView iv;
        public ConstraintLayout ly;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.currency);
            txtFooter = (TextView) v.findViewById(R.id.quantity);
            txtBTCValue = (TextView) v.findViewById(R.id.BTCValue);
            iv = (ImageView)v.findViewById(R.id.icon);
            ly = (ConstraintLayout) v.findViewById(R.id.layout);

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
        final Currency trenutni = all.getCurrency(position);
        final String name = trenutni.getName();
        final String value = Double.toString(trenutni.getQuantity());
        final String LP = Double.toString(trenutni.getValueBTC());

        DecimalFormat df = new DecimalFormat("0.00000000");

        holder.txtHeader.setText(name);
        holder.txtFooter.setText(df.format(trenutni.getQuantity()));
        holder.txtBTCValue.setText(df.format(trenutni.getValueBTC()) + " Ƀ");
        /*if (ttt){
            holder.ly.setBackgroundColor(Color.LTGRAY);
            ttt = false;
        }else {
            holder.ly.setBackgroundColor(Color.WHITE);
            ttt = true;
        }*/


        /*if (trenutni.hasImage()) {
            //"http://i.imgur.com/DvpvklR.png"
            System.out.println("Picasso: "+trenutni.getFileName());
            File f = new File(trenutni.getFileName()); //
            Picasso.with(ac.getApplicationContext())
                    .load(f) //URL
                    .placeholder(R.drawable.ic_cloud_download_black_124dp)
                    .error(R.drawable.ic_error_black_124dp)
                    // To fit image into imageView
                    .fit()
                    // To prevent fade animation
                    .noFade()
                    .into(holder.iv);

            //   Picasso.with(ac).load(trenutni.getFileName()).into(holder.iv);
            // holder.iv.setImageDrawable(this.ac.getDrawable(R.drawable.ic_airline_seat_recline_extra_black_24dp));
        }
        else {
            switch (trenutni.getName()){
                case "BTC":
                    holder.iv.setImageResource(R.drawable.bitcoin);
                default:
                    holder.iv.setImageResource(R.drawable.defaultcoin);
            }
        }
*/

        holder.ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CryptoAdapter.startDView(trenutni.getID(),ac);
            }
        });


    }

    @Override
    public int getItemCount() {
        return all.getCurrencySize();
    }
}


