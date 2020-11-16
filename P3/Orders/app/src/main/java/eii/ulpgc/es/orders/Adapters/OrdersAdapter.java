package eii.ulpgc.es.orders.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import eii.ulpgc.es.orders.Models.Order;
import eii.ulpgc.es.orders.R;

/**
 * Created by Daniel on 15/04/2017.
 */

public class OrdersAdapter extends ArrayAdapter<Order> implements View.OnClickListener{

    private ArrayList<Order> orders;
    private Context context;

    private static class ViewHolder {
        TextView code;
        TextView customerName;
        TextView productName;
    }

    public OrdersAdapter(ArrayList<Order> data, Context context) {
        super(context, R.layout.order_row_item, data);
        this.orders = data;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Order order = getItem(position);

        OrdersAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new OrdersAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_row_item, parent, false);
            viewHolder.customerName = (TextView) convertView.findViewById(R.id.order_customer_name);
            viewHolder.productName = (TextView) convertView.findViewById(R.id.order_product_name);
            viewHolder.code = (TextView) convertView.findViewById(R.id.order_code);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OrdersAdapter.ViewHolder) convertView.getTag();
        }

        if (order != null) {
            viewHolder.customerName.setText(order.getCustomerName());
            viewHolder.productName.setText(order.getProductName());
            viewHolder.code.setText(order.getCode());
        }

        return convertView;
    }
}
