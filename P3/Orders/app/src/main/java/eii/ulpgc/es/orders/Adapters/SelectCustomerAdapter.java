package eii.ulpgc.es.orders.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import eii.ulpgc.es.orders.Models.Customer;
import eii.ulpgc.es.orders.R;

/**
 * Created by Daniel on 14/04/2017.
 */

public class SelectCustomerAdapter extends ArrayAdapter<Customer> implements View.OnClickListener{

    private ArrayList<Customer> customers;
    private Context context;

    private static class ViewHolder {
        TextView name;
    }

    public SelectCustomerAdapter(ArrayList<Customer> data, Context context) {
        super(context, R.layout.select_customer_row_item, data);
        this.customers = data;
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
        Customer customer = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.select_customer_row_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.row_item_customer_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (customer != null) {
            viewHolder.name.setText(customer.getName());
        }

        return convertView;
    }
}
