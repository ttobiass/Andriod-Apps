package com.tiffinitobiasson.multi_note;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView updated;
    public TextView preview;

    public MyViewHolder(View view){
        super(view);
        title = (TextView) view.findViewById(R.id.Title);
        updated = (TextView) view.findViewById(R.id.Updated);
        preview = (TextView) view.findViewById(R.id.Preview);
    }
}
