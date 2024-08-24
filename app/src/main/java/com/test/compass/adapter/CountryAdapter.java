package com.test.compass.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.test.compass.R;
import com.test.compass.language.Model.CountryItem;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends ArrayAdapter<CountryItem> {
    private final List<CountryItem> countryListFull;

    public CountryAdapter(@NonNull Context context, @NonNull List<CountryItem> countryList) {
        super(context, 0, countryList);
        countryListFull = new ArrayList<>(countryList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country, parent, false);
        }

        TextView tvCountry = convertView.findViewById(R.id.tv_country);

        CountryItem countryItem = getItem(position);

        if (countryItem != null) {
            tvCountry.setText(countryItem.getCountryName());
        }

        return convertView;
    }

    private final Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<CountryItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(countryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (CountryItem item : countryListFull) {
                    if (item.getCountryName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((CountryItem) resultValue).getCountryName();
        }
    };
}