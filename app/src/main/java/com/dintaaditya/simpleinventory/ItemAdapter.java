package com.dintaaditya.simpleinventory;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dintaaditya.simpleinventory.Model.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ItemAdapter extends FirestoreRecyclerAdapter<Item, ItemAdapter.ItemHolder> {


    public ItemAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item model) {
        final DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String SKU = "SKU : " + documentSnapshot.getId();
        String Stock = "Stock : " + model.getStock() + " item(s)";

        holder.tvSKU.setText(SKU);
        holder.tvName.setText(model.getName());
        holder.tvStock.setText(Stock);
        Glide.with(holder.imgItem.getContext()).load(model.getImage()).apply(RequestOptions.circleCropTransform()).into(holder.imgItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), DetailActivity.class).putExtra("SKU", documentSnapshot.getId()));
            }
        });
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_items, viewGroup, false);
        return new ItemHolder(view);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSKU, tvStock;
        ImageView imgItem;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvSKU = itemView.findViewById(R.id.tv_SKU);
            tvStock = itemView.findViewById(R.id.tv_stock);
            imgItem = itemView.findViewById(R.id.img_item);
        }
    }
}
