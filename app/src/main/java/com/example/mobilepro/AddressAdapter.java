package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    public interface OnAddressActionListener {
        void onSelect(Address address);
        void onEdit(Address address);
        void onSetDefault(Address address);
        void onDelete(Address address);
    }

    private final Context context;
    private final List<Address> addressList;
    private final OnAddressActionListener listener;

    public AddressAdapter(Context context, List<Address> addressList, OnAddressActionListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address addr = addressList.get(position);

        String label = (addr.getLabel() != null && !addr.getLabel().isEmpty()) ? addr.getLabel() : "Địa chỉ";
        holder.tvLabel.setText(label);
        holder.tvDefaultBadge.setVisibility(addr.isDefault() ? View.VISIBLE : View.GONE);
        holder.tvSetDefault.setVisibility(addr.isDefault() ? View.GONE : View.VISIBLE);

        holder.tvReceiver.setText(addr.getFullName() + " • " + addr.getPhone());
        holder.tvDetail.setText(addr.getAddress());

        holder.itemView.setOnClickListener(v -> listener.onSelect(addr));
        holder.tvEdit.setOnClickListener(v -> listener.onEdit(addr));
        holder.tvSetDefault.setOnClickListener(v -> listener.onSetDefault(addr));
        holder.tvDelete.setOnClickListener(v -> listener.onDelete(addr));
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvDefaultBadge, tvEdit, tvReceiver, tvDetail, tvSetDefault, tvDelete;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvAddressLabel);
            tvDefaultBadge = itemView.findViewById(R.id.tvAddressDefaultBadge);
            tvEdit = itemView.findViewById(R.id.tvEditAddress);
            tvReceiver = itemView.findViewById(R.id.tvAddressReceiver);
            tvDetail = itemView.findViewById(R.id.tvAddressDetail);
            tvSetDefault = itemView.findViewById(R.id.tvSetDefaultAddress);
            tvDelete = itemView.findViewById(R.id.tvDeleteAddress);
        }
    }
}