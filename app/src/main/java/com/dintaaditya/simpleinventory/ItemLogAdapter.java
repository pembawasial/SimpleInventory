package com.dintaaditya.simpleinventory;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dintaaditya.simpleinventory.Model.ItemLog;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ItemLogAdapter extends FirestoreRecyclerAdapter<ItemLog, ItemLogAdapter.ItemLogHolder> {

    public ItemLogAdapter(@NonNull FirestoreRecyclerOptions<ItemLog> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemLogHolder holder, int position, @NonNull ItemLog model) {
        holder.tvStatus.setText(model.getStatus());
        holder.tvPreviousStock.setText(String.valueOf(model.getPrevious_stock()));
        holder.tvStockMovement.setText(String.valueOf(model.getStock_movement()));
        holder.tvLastStock.setText(String.valueOf(model.getLast_stock()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = model.getTimestamp().toDate();
        holder.tvDate.setText("Date : " + format.format(date));
    }

    @NonNull
    @Override
    public ItemLogHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_log, viewGroup, false);
        return new ItemLogHolder(view);
    }

    class ItemLogHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvPreviousStock, tvStockMovement, tvLastStock, tvDate;

        public ItemLogHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPreviousStock = itemView.findViewById(R.id.tv_previous_stock);
            tvStockMovement = itemView.findViewById(R.id.tv_stock_movement);
            tvLastStock = itemView.findViewById(R.id.tv_last_stock);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
