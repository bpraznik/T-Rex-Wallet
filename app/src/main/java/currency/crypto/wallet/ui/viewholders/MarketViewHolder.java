package currency.crypto.wallet.ui.viewholders;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import currency.crypto.wallet.R;

public class MarketViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView tvSymbol;
    public TextView tvValue;
    public TextView tvPercentChange;
    public ConstraintLayout ly;

    public MarketViewHolder(View v) {
        super(v);
        tvSymbol = (TextView) v.findViewById(R.id.tvSymbol);
        tvValue = (TextView) v.findViewById(R.id.tvValue);
        tvPercentChange = (TextView) v.findViewById(R.id.tvPercentChange);
        ly = (ConstraintLayout) v.findViewById(R.id.layout);
    }
}
