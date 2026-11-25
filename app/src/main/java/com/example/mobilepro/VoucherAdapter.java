package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    public interface OnVoucherSelectListener {
        void onSelect(Voucher voucher);
    }

    private final Context context;
    private final List<Voucher> voucherList;
    private final int currentSubtotal;
    private final OnVoucherSelectListener listener;

    public VoucherAdapter(Context context, List<Voucher> voucherList, int currentSubtotal, OnVoucherSelectListener listener) {
        this.context = context;
        this.voucherList = voucherList;
        this.currentSubtotal = currentSubtotal;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher v = voucherList.get(position);
        holder.tvCode.setText(v.getCode());
        holder.tvTitle.setText(v.getTitle());

        boolean isEligible = currentSubtotal >= v.getMinOrderValue();
        if (isEligible) {
            holder.tvCondition.setText("Áp dụng cho đơn từ " + Product.formatPrice(v.getMinOrderValue()));
            holder.btnSelect.setEnabled(true);
            holder.btnSelect.setAlpha(1f);
            holder.btnSelect.setOnClickListener(view -> listener.onSelect(v));
        } else {
            int missing = v.getMinOrderValue() - currentSubtotal;
            holder.tvCondition.setText("Mua thêm " + Product.formatPrice(missing) + " để sử dụng");
            holder.btnSelect.setEnabled(false);
            holder.btnSelect.setAlpha(0.45f);
        }
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvTitle, tvCondition;
        MaterialButton btnSelect;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvVoucherCode);
            tvTitle = itemView.findViewById(R.id.tvVoucherTitle);
            tvCondition = itemView.findViewById(R.id.tvVoucherCondition);
            btnSelect = itemView.findViewById(R.id.btnSelectVoucher);
        }
    }
}