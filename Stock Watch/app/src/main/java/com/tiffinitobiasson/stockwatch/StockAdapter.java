package com.tiffinitobiasson.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by tiffi on 3/3/2018.
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private List<Stock> stockList;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> sList, MainActivity ma){
        this.stockList = sList;
        mainAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_list_row, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.Name.setText(stock.getName());
        holder.Symbol.setText(stock.getSymbol());
        holder.Price.setText(Double.toString(stock.getLatestPrice()));
        Double pc = stock.getChangePercent();
        holder.PercentChange.setText("("+String.format( "%.2f", pc )+"%)");

        if(stock.getChange()<0){
            holder.Name.setTextColor(Color.RED);
            holder.Symbol.setTextColor(Color.RED);
            holder.Price.setTextColor(Color.RED);
            holder.PercentChange.setTextColor(Color.RED);
            holder.Change.setTextColor(Color.RED);

            String changeWithSymbol = "\u25BC "+Double.toString(stock.getChange());
            holder.Change.setText(changeWithSymbol);
        }
        else{
            holder.Name.setTextColor(Color.GREEN);
            holder.Symbol.setTextColor(Color.GREEN);
            holder.Price.setTextColor(Color.GREEN);
            holder.PercentChange.setTextColor(Color.GREEN);
            holder.Change.setTextColor(Color.GREEN);

            String changeWithSymbol = "\u25B2 "+Double.toString(stock.getChange());
            holder.Change.setText(changeWithSymbol);
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}
