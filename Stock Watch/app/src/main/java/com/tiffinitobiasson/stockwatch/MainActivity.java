package com.tiffinitobiasson.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener{

    private RecyclerView recyclerview;
    private SwipeRefreshLayout swiper;
    private List<Stock> stockList;
    private StockAdapter sAdapter;
    private DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stockList = new ArrayList<>();
        recyclerview = (RecyclerView) findViewById(R.id.recycler);
        sAdapter = new StockAdapter(stockList, this);
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        dbHandler = new DatabaseHandler(this);

        recyclerview.setAdapter(sAdapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        if(checkConnection()==true){
            refreshStocks();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Stocks cannot be loaded without a network connection.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        dbHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        //onClick will open up the website for the selected stock
        String base = "http://www.marketwatch.com/investing/stock/";
        int pos = recyclerview.getChildLayoutPosition(view);
        String sym = stockList.get(pos).getSymbol();
        String url = base + sym;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    @Override
    public boolean onLongClick(View view) {
        //onLongClick will first show a dialog box confirming that user wants to delete stock and then based on answer will delete the stock or not
        final int pos = recyclerview.getChildLayoutPosition(view);
        final Stock s = stockList.get(pos);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHandler.deleteStock(s.getSymbol());
                stockList.remove(pos);
                sAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setMessage("Delete Stock "+s.getSymbol()+"("+s.getName()+")?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Add:
                if(checkConnection()==true){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    final MainActivity ma = this;
                    final EditText et = new EditText(this);
                    et.setInputType(InputType.TYPE_CLASS_TEXT);
                    et.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                    et.setGravity(Gravity.CENTER_HORIZONTAL);

                    builder.setView(et);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new AsyncVerifySymbol(ma).execute(et.getText().toString());
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

                    builder.setMessage("Please enter a stock Symbol:");
                    builder.setTitle("New Stock");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage("Stocks cannot be added without a network connection.");
                    builder.setTitle("No Network Connection");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void doRefresh() {
        if(checkConnection() == true){
            refreshStocks();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Stocks cannot be updated without a network connection.");
            builder.setTitle("No Network Connection");

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        swiper.setRefreshing(false);
    }

    private boolean checkConnection(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else {
            return false;
        }
    }

    public void addNewStock(Stock stock) {
        if(stock != null){
            stockList.add(stock);
            Collections.sort(stockList, new Comparator<Stock>() {
                public int compare(Stock s1, Stock s2) {
                    return s1.getSymbol().compareTo(s2.getSymbol());
                }
            });
            dbHandler.addStock(stock);
            sAdapter.notifyDataSetChanged();
        }
    }

    private void refreshStocks(){
        ArrayList<String[]> temp = dbHandler.loadStocks();
        stockList.clear();
        for(int i=0; i<temp.size();i++){
            new AsyncDownloadData(this).execute(temp.get(i)[0]);
        }
    }

    public void processNewStock(String symbol){
        boolean duplicate = false;
        for(int i=0; i<stockList.size();i++){
            if(stockList.get(i).getSymbol().equals(symbol)){
                duplicate = true;
            }
        }

        if(duplicate){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Stock symbol "+symbol+" is already displayed.");
            builder.setTitle("Duplicate Stock");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            new AsyncDownloadData(this).execute(symbol);
        }
    }
}
