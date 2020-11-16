package eii.ulpgc.es.serverorders.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import eii.ulpgc.es.serverorders.Models.Product;
import eii.ulpgc.es.serverorders.R;

/**
 * Created by Daniel on 15/04/2017.
 */

public class ProductsAdapter extends ArrayAdapter<Product> implements View.OnClickListener {
    private ArrayList<Product> products;
    private Context context;

    private static class ViewHolder {
        TextView name;
        TextView description;
    }

    public ProductsAdapter(ArrayList<Product> data, Context context) {
        super(context, R.layout.product_row_item, data);
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

        ProductsAdapter.ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ProductsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.product_row_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.product_name);
            viewHolder.description = (TextView) convertView.findViewById(R.id.product_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ProductsAdapter.ViewHolder) convertView.getTag();
        }

        if (product != null) {
            viewHolder.name.setText(product.getName());
            viewHolder.description.setText(product.getDescription());
        }

        return convertView;
    }
}