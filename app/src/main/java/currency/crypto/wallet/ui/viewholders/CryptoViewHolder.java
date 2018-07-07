package currency.crypto.wallet.ui.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import currency.crypto.wallet.R;

public class CryptoViewHolder extends RecyclerView.ViewHolder{
    public TextView txtHeader;
    public TextView txtFooter;
    public TextView txtBTCValue;
    public ImageView iv;
    public ConstraintLayout ly;
    public TextView tvLast;

    public CryptoViewHolder(View v) {
        super(v);
        txtHeader = (TextView) v.findViewById(R.id.currency);
        txtFooter = (TextView) v.findViewById(R.id.quantity);
        txtBTCValue = (TextView) v.findViewById(R.id.BTCValue);
        iv = (ImageView)v.findViewById(R.id.icon);
        ly = (ConstraintLayout) v.findViewById(R.id.layout);
        tvLast = v.findViewById(R.id.tvLast);
    }
}
