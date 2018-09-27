package com.tiffinitobiasson.knowyourgovernment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by tiffi on 4/14/2018.
 */

public class OfficialViewHolder extends RecyclerView.ViewHolder {
    public TextView office;
    public TextView nameParty;

    public OfficialViewHolder(View view){
        super(view);
        office = (TextView) view.findViewById(R.id.OfficialTitle);
        nameParty = (TextView) view.findViewById(R.id.OfficialNameParty);
    }
}
