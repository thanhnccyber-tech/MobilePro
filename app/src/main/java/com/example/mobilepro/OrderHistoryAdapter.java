package com.example.mobilepro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends BaseAdapter {

    private final Context context;
    private final List<Order> orders;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public OrderHistoryAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override public int getCount() { return orders.size(); }
    @Override public Object getItem(int position) { return orders.get(position); }
    @Override public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_order_history, parent, false);
        }

        Order order = orders.get(position);

        TextView tvCode = convertView.findViewById(R.id.tvOrderCode);
        TextView tvStatus = convertView.findViewById(R.id.tvOrderStatus);
        TextView tvDate = convertView.findViewById(R.id.tvOrderDate);
        TextView tvItems = convertView.findViewById(R.id.tvOrderItems);
        TextView tvTotal = convertView.findViewById(R.id.tvOrderTotal);

        tvCode.setText(order.orderCode);
        tvStatus.setText(order.status);
        tvDate.setText(order.createdAt != null ? dateFormat.format(order.createdAt) : "");
        tvItems.setText(buildItemSummary(order));
        tvTotal.setText(Product.formatPrice(order.total));

        Order.bindStatusBadge(tvStatus, order.status);

        return convertView;
    }

    private String buildItemSummary(Order order) {
        Product first = order.items.get(0);
        if (order.items.size() == 1) {
            return first.getName() + (first.getQuantity() > 1 ? " x" + first.getQuantity() : "");
        }
        return first.getName() + " + " + (order.items.size() - 1) + " món khác";
    }
}