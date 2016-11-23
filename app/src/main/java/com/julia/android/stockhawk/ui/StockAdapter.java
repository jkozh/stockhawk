package com.julia.android.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.julia.android.stockhawk.R;
import com.julia.android.stockhawk.data.Contract;
import com.julia.android.stockhawk.data.PrefUtils;
import com.julia.android.stockhawk.util.Formatter;

import butterknife.BindView;
import butterknife.ButterKnife;

class StockAdapter extends RecyclerView.Adapter<StockAdapter.StockViewHolder> {

    final private Context context;
    private Cursor cursor;
    private StockAdapterOnClickHandler clickHandler;

    StockAdapter(Context context, StockAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    String getSymbolAtPosition(int position) {

        cursor.moveToPosition(position);
        return cursor.getString(Contract.Quote.POSITION_SYMBOL);
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(context).inflate(R.layout.list_item_quote, parent, false);

        return new StockViewHolder(item);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {

        cursor.moveToPosition(position);


        holder.symbol.setText(cursor.getString(Contract.Quote.POSITION_SYMBOL));
        holder.price.setText(
                Formatter.getDollarFormat(cursor.getFloat(Contract.Quote.POSITION_PRICE)));


        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

        if (rawAbsoluteChange > 0) {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_green);
        } else {
            holder.change.setBackgroundResource(R.drawable.percent_change_pill_red);
        }

        String change = Formatter.getDollarFormatWithPlus(rawAbsoluteChange);
        String percentage = Formatter.getPercentageFormat(percentageChange);

        if (PrefUtils.getDisplayMode(context).equals(context.getString(R.string.pref_display_mode_absolute_key))) {
            holder.change.setText(change);
        } else {
            holder.change.setText(percentage);
        }

        // Set striped view for the items background
        if(position % 2 == 1) {
            // Set a background color for RecyclerView regular row
            holder.itemView.setBackgroundColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.dark_blue));
        } else {
            // Set the background color for alternate row
            holder.itemView.setBackgroundColor(ContextCompat.getColor(
                    holder.itemView.getContext(), R.color.light_blue));
        }

    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }


    interface StockAdapterOnClickHandler {
        void onClick(String symbol);
    }

    class StockViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.symbol)
        TextView symbol;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.change)
        TextView change;

        StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            cursor.moveToPosition(adapterPosition);
            int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL);
            clickHandler.onClick(cursor.getString(symbolColumn));
        }

    }
}
