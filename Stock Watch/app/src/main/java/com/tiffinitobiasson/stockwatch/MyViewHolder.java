package com.tiffinitobiasson.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by tiffi on 3/3/2018.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView Symbol;
    public TextView Name;
    public TextView Price;
    public TextView PercentChange;
    public TextView Change;

    public MyViewHolder(View view){
        super(view);
        Symbol = (TextView) view.findViewById(R.id.symbol);
        Name = (TextView) view.findViewById(R.id.name);
        Price = (TextView) view.findViewById(R.id.price);
        PercentChange = (TextView) view.findViewById(R.id.percent_change);
        Change = (TextView) view.findViewById(R.id.change);
    }

}
