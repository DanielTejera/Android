package eii.ulpgc.es.orders.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import eii.ulpgc.es.orders.Models.Product;
import eii.ulpgc.es.orders.R;

/**
 * Created by Daniel on 15/04/2017.
 */

public class SelectProductAdapter extends ArrayAdapter<Product> implements View.OnClickListener{

    private ArrayList<Product> products;
    private Context context;

    private static class ViewHolder {
        TextView name;
    }

    public SelectProductAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.select_product_row_item, data);
        this.products = data;
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
        Product product = getItem(position);

        SelectProductAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new SelectProductAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.select_product_row_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.row_item_product_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SelectProductAdapter.ViewHolder) convertView.getTag();
        }

        if (product != null) {
            viewHolder.name.setText(product.getName());
        }

        return convertView;
    }
}